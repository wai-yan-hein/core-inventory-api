/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.*;
import cv.api.exception.ResponseUtil;
import cv.api.model.VSale;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SaleHisService {

    private final SaleExpenseService saleExpenseService;
    private final VouDiscountService discountService;
    private final SaleOrderJoinService saleOrderJoinService;
    private final OrderHisService orderHisService;
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final SaleDetailService saleDetailService;
    private final TransactionalOperator operator;


    public Mono<SaleHis> save(SaleHis dto) {
        return isValid(dto).flatMap(sh -> operator.transactional(Mono.defer(() -> saveSale(sh)
                .flatMap((saleHis) -> saveSaleExpense(saleHis)
                        .then(saveVouDiscount(saleHis))
                        .then(saveSaleOrderJoin(saleHis))
                        .thenReturn(saleHis)))));
    }

    private Mono<SaleHis> isValid(SaleHis sh) {
        List<SaleHisDetail> list = Util1.nullToEmpty(sh.getListSH());
        list.removeIf(t -> Util1.isNullOrEmpty(t.getStockCode()));
        if (list.isEmpty()) {
            return ResponseUtil.createBadRequest("Detail is null/empty");
        } else if (Util1.isNullOrEmpty(sh.getDeptId())) {
            return ResponseUtil.createBadRequest("deptId is null from mac id : " + sh.getMacId());
        } else if (Util1.isNullOrEmpty(sh.getCurCode())) {
            return ResponseUtil.createBadRequest("Currency is null");
        } else if (Util1.isNullOrEmpty(sh.getLocCode())) {
            return ResponseUtil.createBadRequest("Location is null");
        } else if (Util1.isNullOrEmpty(sh.getTraderCode())) {
            return ResponseUtil.createBadRequest("Trader is null");
        } else if (Util1.isNullOrEmpty(sh.getVouDate())) {
            return ResponseUtil.createBadRequest("Voucher Date is null");
        }
        return Mono.just(sh);
    }

    private Mono<Boolean> saveSaleExpense(SaleHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<SaleExpense> list = sh.getListExpense();
        if (list != null) {
            return saleExpenseService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> Util1.getDouble(e.getAmount()) > 0 && e.getKey().getExpenseCode() != null)
                    .flatMap(e -> {
                        if (e.getKey().getUniqueId() == 0) {
                            int uniqueId = list.indexOf(e) + 1;
                            e.getKey().setUniqueId(uniqueId);
                        }
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        return saleExpenseService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }


    private Mono<SaleHis> saveSale(SaleHis dto) {
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        return saveOrUpdate(dto).flatMap(ri -> saleDetailService.delete(ri.getKey().getVouNo(), ri.getKey().getCompCode()).flatMap(delete -> {
            List<SaleHisDetail> list = dto.getListSH();
            return Flux.fromIterable(list)
                    .filter(detail -> Util1.getDouble(detail.getQty()) != 0)
                    .concatMap(detail -> {
                        if (detail.getKey() == null) {
                            detail.setKey(SaleDetailKey.builder().build());
                        }
                        int uniqueId = list.indexOf(detail) + 1;
                        detail.getKey().setUniqueId(uniqueId);
                        detail.getKey().setVouNo(ri.getKey().getVouNo());
                        detail.getKey().setCompCode(ri.getKey().getCompCode());
                        detail.setDeptId(ri.getDeptId());
                        return saleDetailService.save(detail);
                    })
                    .then(Mono.just(ri));
        }));
    }

    private Mono<Boolean> saveVouDiscount(SaleHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<VouDiscount> list = sh.getListVouDiscount();
        if (list != null) {
            return Flux.fromIterable(list)
                    .filter(e -> Util1.getDouble(e.getAmount()) > 0)
                    .flatMap(e -> {
                        if (e.getKey().getUniqueId() == 0) {
                            int uniqueId = list.indexOf(e) + 1;
                            e.getKey().setUniqueId(uniqueId);
                        }
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        return discountService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false);
        } else {
            return Mono.just(false);
        }
    }


    private Mono<Boolean> saveSaleOrderJoin(SaleHis sh) {
        List<String> list = sh.getListOrder();
        if (list != null) {
            String saleVouNo = sh.getKey().getVouNo();
            String compCode = sh.getKey().getCompCode();
            return saleOrderJoinService.delete(saleVouNo, compCode)
                    .thenMany(Flux.fromIterable(list).flatMap(orderNo -> {
                        SaleOrderJoin obj = SaleOrderJoin.builder().build();
                        SaleOrderJoinKey key = SaleOrderJoinKey.builder().build();
                        key.setSaleVouNo(saleVouNo);
                        key.setOrderVouNo(orderNo);
                        key.setCompCode(compCode);
                        obj.setKey(key);
                        return saleOrderJoinService.insert(obj).flatMap(saleOrderJoin -> {
                            OrderHisKey orderKey = OrderHisKey.builder().build();
                            orderKey.setVouNo(orderNo);
                            orderKey.setCompCode(compCode);
                            return orderHisService.updateOrder(orderKey, true);
                        });
                    })).then(Mono.just(true));
        } else {
            return Mono.just(false);
        }
    }


    public Mono<SaleHis> findById(SaleHisKey key) {
        String sql = """
                select *
                from sale_his
                where vou_no =:vouNo
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<Boolean> delete(SaleHisKey key) {
        return updateDeleteStatus(key, true)
                .thenMany(saleOrderJoinService.getSaleOrder(key.getVouNo(), key.getCompCode())
                        .flatMap(order -> {
                            OrderHisKey orderKey = OrderHisKey.builder().build();
                            orderKey.setVouNo(order.getKey().getOrderVouNo());
                            orderKey.setCompCode(order.getKey().getCompCode());
                            return orderHisService.updateOrder(orderKey, false);
                        })).then(Mono.just(true));
    }

    public Mono<Boolean> restore(SaleHisKey key) {
        return updateDeleteStatus(key, false);
    }

    public Flux<SaleHis> unUploadVoucher(LocalDateTime syncDate) {
        String sql = """
                select vou_no,comp_code,deleted
                from sale_his
                where intg_upd_status is null
                and vou_date >= :syncDate
                """;
        return client.sql(sql)
                .bind("syncDate", syncDate)
                .map((row, rowMetadata) -> SaleHis.builder()
                        .key(SaleHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deleted(row.get("deleted", Boolean.class))
                        .build()).all();
    }

    public Mono<General> getVoucherInfo(String vouDate, String compCode) {
        String sql = """
                select count(*) vou_count, sum(paid) paid
                from sale_his
                where deleted = false
                and date(vou_date) = :vouDate
                and comp_code = :compCode
                """;

        return client.sql(sql)
                .bind("vouDate", vouDate)
                .bind("compCode", compCode)
                .map((row, rowMetadata) ->
                        General.builder()
                                .qty(row.get("vou_count", Double.class))
                                .amount(row.get("paid", Double.class))
                                .build())
                .one();
    }


    public Flux<VouDiscount> getVoucherDiscount(String vouNo, String compCode) {
        return discountService.getVoucherDiscount(vouNo, compCode);
    }


    public Flux<VouDiscount> searchDiscountDescription(String str, String compCode) {
        return discountService.getDescription(str, compCode);
    }

    public Flux<VSale> getHistory(ReportFilter f) {
        String fromDate = Util1.isNull(f.getFromDate(), "-");
        String toDate = Util1.isNull(f.getToDate(), "-");
        String vouNo = Util1.isNull(f.getVouNo(), "-");
        String userCode = Util1.isNull(f.getUserCode(), "-");
        String traderCode = Util1.isNull(f.getTraderCode(), "-");
        String remark = Util1.isNull(f.getRemark(), "-");
        String stockCode = Util1.isNull(f.getStockCode(), "-");
        String saleManCode = Util1.isNull(f.getSaleManCode(), "-");
        String reference = Util1.isNull(f.getReference(), "-");
        String compCode = f.getCompCode();
        String locCode = Util1.isNull(f.getLocCode(), "-");
        Integer deptId = f.getDeptId();
        boolean deleted = f.isDeleted();
        String batchNo = Util1.isNull(f.getBatchNo(), "-");
        String projectNo = Util1.isAll(f.getProjectNo());
        String curCode = Util1.isAll(f.getCurCode());
        int paymentType = f.getPaymentType();
        String sql = """
                select a.*,t.trader_name,t.user_code t_user_code
                from (
                select vou_no,vou_date,remark,reference,created_by,discount,paid,vou_total,vou_balance,
                deleted,trader_code,loc_code,comp_code,dept_id,post,sum(qty) qty,sum(bag) bag,
                opening,outstanding,total_payment,total_balance,s_pay
                from v_sale s
                where comp_code = :compCode
                and (dept_id = :deptId or 0 = :deptId)
                and deleted = :deleted
                and date(vou_date) between :fromDate and :toDate
                and (vou_no = :vouNo or '-' = :vouNo)
                and (remark REGEXP :remark or '-' = :remark)
                and (reference REGEXP :reference or '-' = :reference)
                and (trader_code = :traderCode or '-' = :traderCode)
                and (created_by = :userCode or '-' = :userCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (saleman_code = :saleManCode or '-' = :saleManCode)
                and (loc_code = :locCode or '-' = :locCode)
                and (batch_no = :batchNo or '-' = :batchNo)
                and (project_no = :projectNo or '-' = :projectNo)
                and (cur_code = :curCode or '-' = :curCode)
                and (payment_type = :paymentType or 0 = :paymentType)
                group by vou_no)a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by vou_date desc""";

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("reference", reference)
                .bind("traderCode", traderCode)
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("saleManCode", saleManCode)
                .bind("locCode", locCode)
                .bind("batchNo", batchNo)
                .bind("projectNo", projectNo)
                .bind("curCode", curCode)
                .bind("paymentType", paymentType)
                .map((row) -> VSale.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDateTime.class), "dd/MM/yyyy"))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouNo(row.get("vou_no", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .traderUserCode(row.get("t_user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .remark(row.get("remark", String.class))
                        .reference(row.get("reference", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .discount(row.get("discount", Double.class))
                        .paid(row.get("paid", Double.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .vouBalance(row.get("vou_balance", Double.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .post(row.get("post", Boolean.class))
                        .qty(row.get("qty", Double.class))
                        .bag(row.get("bag", Double.class))
                        .opening(row.get("opening", Double.class))
                        .outstanding(row.get("outstanding", Double.class))
                        .totalBalance(row.get("total_balance", Double.class))
                        .totalPayment(row.get("total_payment", Double.class))
                        .sPay((row.get("s_pay", Boolean.class)))
                        .build()
                ).all();
    }

    public Mono<Boolean> updatePost(String vouNo, String compCode, boolean post) {
        String sql = """
                update sale_his
                set post = :post
                where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("post", post)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated().thenReturn(true);
    }

    public Mono<Boolean> updateSPay(String vouNo, String compCode, boolean sPay) {
        String sql = """
                update sale_his
                set s_pay = :sPay
                where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("sPay", sPay)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated().thenReturn(true);
    }

    public Mono<SaleHis> insert(SaleHis sh) {
        String sql = """
                    INSERT INTO sale_his
                    (vou_no, trader_code, saleman_code, vou_date, credit_term, cur_code, remark, vou_total, grand_total, discount,
                    disc_p, tax_amt, tax_p, created_date, created_by, deleted, paid, vou_balance, updated_by, updated_date,
                    comp_code, address, order_code, reg_code, loc_code, mac_id, session_id, intg_upd_status, reference,
                    dept_id, vou_lock, order_no, project_no, car_no, grn_vou_no, expense, account, labour_group_code, print_count,
                    dept_code, cash_acc, debtor_acc, weight_vou_no, post, s_pay, tran_source, total_payment, opening,
                    total_balance, outstanding)
                    VALUES
                    (:vouNo, :traderCode, :salemanCode, :vouDate, :creditTerm, :curCode, :remark, :vouTotal, :grandTotal,
                    :discount, :discP, :taxAmt, :taxP, :createdDate, :createdBy, :deleted, :paid, :vouBalance, :updatedBy,
                    :updatedDate, :compCode, :address, :orderCode, :regCode, :locCode, :macId, :sessionId, :intgUpdStatus,
                    :reference, :deptId, :vouLock, :orderNo, :projectNo, :carNo, :grnVouNo, :expense, :account,
                    :labourGroupCode, :printCount, :deptCode, :cashAcc, :debtorAcc, :weightVouNo, :post, :sPay,
                    :tranSource, :totalPayment, :opening, :totalBalance, :outstanding)
                """;

        return executeUpdate(sql, sh);
    }

    private Mono<SaleHis> executeUpdate(String sql, SaleHis sh) {
        return client.sql(sql)
                .bind("vouNo", sh.getKey().getVouNo())
                .bind("compCode", sh.getKey().getCompCode())
                .bind("traderCode", sh.getTraderCode())
                .bind("salemanCode", Parameters.in(R2dbcType.VARCHAR, sh.getSaleManCode()))
                .bind("vouDate", sh.getVouDate())
                .bind("creditTerm", Parameters.in(R2dbcType.VARCHAR, sh.getCreditTerm()))
                .bind("curCode", sh.getCurCode())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, sh.getRemark()))
                .bind("vouTotal", sh.getVouTotal())
                .bind("grandTotal", sh.getGrandTotal())
                .bind("discount", sh.getDiscount())
                .bind("discP", sh.getDiscP())
                .bind("taxAmt", sh.getTaxAmt())
                .bind("taxP", sh.getTaxPercent())
                .bind("createdDate", sh.getCreatedDate())
                .bind("createdBy", sh.getCreatedBy())
                .bind("deleted", Util1.getBoolean(sh.getDeleted()))
                .bind("paid", sh.getPaid())
                .bind("vouBalance", sh.getBalance())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, sh.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("address", Parameters.in(R2dbcType.VARCHAR, sh.getAddress()))
                .bind("orderCode", Parameters.in(R2dbcType.VARCHAR, sh.getOrderCode()))
                .bind("regCode", Parameters.in(R2dbcType.VARCHAR, sh.getRegionCode()))
                .bind("locCode", Parameters.in(R2dbcType.VARCHAR, sh.getLocCode()))
                .bind("macId", Parameters.in(R2dbcType.INTEGER, sh.getMacId()))
                .bind("sessionId", Parameters.in(R2dbcType.INTEGER, sh.getSession()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, sh.getIntgUpdStatus()))
                .bind("reference", Parameters.in(R2dbcType.VARCHAR, sh.getReference()))
                .bind("deptId", sh.getDeptId())
                .bind("vouLock", Util1.getBoolean(sh.getVouLock()))
                .bind("orderNo", Parameters.in(R2dbcType.VARCHAR, sh.getOrderNo()))
                .bind("projectNo", Parameters.in(R2dbcType.VARCHAR, sh.getProjectNo()))
                .bind("carNo", Parameters.in(R2dbcType.VARCHAR, sh.getCarNo()))
                .bind("grnVouNo", Parameters.in(R2dbcType.VARCHAR, sh.getGrnVouNo()))
                .bind("expense", Parameters.in(R2dbcType.DOUBLE, sh.getExpense()))
                .bind("account", Parameters.in(R2dbcType.VARCHAR, sh.getSaleAcc()))
                .bind("labourGroupCode", Parameters.in(R2dbcType.VARCHAR, sh.getLabourGroupCode()))
                .bind("printCount", Parameters.in(R2dbcType.INTEGER, sh.getPrintCount()))
                .bind("deptCode", Parameters.in(R2dbcType.VARCHAR, sh.getDeptCode()))
                .bind("cashAcc", Parameters.in(R2dbcType.VARCHAR, sh.getCashAcc()))
                .bind("debtorAcc", Parameters.in(R2dbcType.VARCHAR, sh.getDebtorAcc()))
                .bind("weightVouNo", Parameters.in(R2dbcType.VARCHAR, sh.getWeightVouNo()))
                .bind("post", Util1.getBoolean(sh.getPost()))
                .bind("sPay", Util1.getBoolean(sh.getSPay()))
                .bind("tranSource", Util1.getInteger(sh.getTranSource(), 1))
                .bind("totalPayment", Parameters.in(R2dbcType.DOUBLE, sh.getTotalPayment()))
                .bind("opening", Parameters.in(R2dbcType.DOUBLE, sh.getOpening()))
                .bind("totalBalance", Parameters.in(R2dbcType.DOUBLE, sh.getTotalBalance()))
                .bind("outstanding", Parameters.in(R2dbcType.DOUBLE, sh.getOutstanding()))
                .fetch()
                .rowsUpdated().thenReturn(sh);
    }

    private Mono<SaleHis> update(SaleHis sh) {
        String sql = """
                    UPDATE sale_his
                    SET dept_id = :deptId,
                        trader_code = :traderCode,
                        saleman_code = :salemanCode,
                        vou_date = :vouDate,
                        credit_term = :creditTerm,
                        cur_code = :curCode,
                        remark = :remark,
                        vou_total = :vouTotal,
                        grand_total = :grandTotal,
                        discount = :discount,
                        disc_p = :discP,
                        tax_amt = :taxAmt,
                        tax_p = :taxP,
                        created_date = :createdDate,
                        created_by = :createdBy,
                        deleted = :deleted,
                        paid = :paid,
                        vou_balance = :vouBalance,
                        updated_by = :updatedBy,
                        updated_date = :updatedDate,
                        address = :address,
                        order_code = :orderCode,
                        reg_code = :regCode,
                        loc_code = :locCode,
                        mac_id = :macId,
                        session_id = :sessionId,
                        intg_upd_status = :intgUpdStatus,
                        reference = :reference,
                        vou_lock = :vouLock,
                        order_no = :orderNo,
                        project_no = :projectNo,
                        car_no = :carNo,
                        grn_vou_no = :grnVouNo,
                        expense = :expense,
                        account = :account,
                        labour_group_code = :labourGroupCode,
                        print_count = :printCount,
                        dept_code = :deptCode,
                        cash_acc = :cashAcc,
                        debtor_acc = :debtorAcc,
                        weight_vou_no = :weightVouNo,
                        post = :post,
                        s_pay = :sPay,
                        tran_source = :tranSource,
                        total_payment = :totalPayment,
                        opening = :opening,
                        total_balance = :totalBalance,
                        outstanding = :outstanding
                    WHERE vou_no = :vouNo
                    AND comp_code = :compCode
                """;

        return executeUpdate(sql, sh);
    }


    private Mono<SaleHis> saveOrUpdate(SaleHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            return vouNoService.getVouNo(deptId, "SALE", compCode, macId)
                    .flatMap(seqNo -> {
                        dto.getKey().setVouNo(seqNo);
                        dto.setCreatedDate(LocalDateTime.now());
                        dto.setUpdatedDate(LocalDateTime.now());
                        return insert(dto);
                    });
        } else {
            return update(dto);
        }
    }

    public SaleHis mapRow(Row row) {
        return SaleHis.builder()
                .key(SaleHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .traderCode(row.get("trader_code", String.class))
                .saleManCode(row.get("saleman_code", String.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .creditTerm(row.get("credit_term", LocalDate.class))
                .curCode(row.get("cur_code", String.class))
                .remark(row.get("remark", String.class))
                .vouTotal(row.get("vou_total", Double.class))
                .grandTotal(row.get("grand_total", Double.class))
                .discount(row.get("discount", Double.class))
                .discP(row.get("disc_p", Double.class))
                .taxAmt(row.get("tax_amt", Double.class))
                .taxPercent(row.get("tax_p", Double.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .paid(row.get("paid", Double.class))
                .balance(row.get("vou_balance", Double.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .address(row.get("address", String.class))
                .orderCode(row.get("order_code", String.class))
                .regionCode(row.get("reg_code", String.class))
                .locCode(row.get("loc_code", String.class))
                .macId(row.get("mac_id", Integer.class))
                .session(row.get("session_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .reference(row.get("reference", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .vouLock(row.get("vou_lock", Boolean.class))
                .orderNo(row.get("order_no", String.class))
                .projectNo(row.get("project_no", String.class))
                .carNo(row.get("car_no", String.class))
                .grnVouNo(row.get("grn_vou_no", String.class))
                .expense(row.get("expense", Double.class))
                .saleAcc(row.get("account", String.class))
                .labourGroupCode(row.get("labour_group_code", String.class))
                .printCount(row.get("print_count", Integer.class))
                .deptCode(row.get("dept_code", String.class))
                .cashAcc(row.get("cash_acc", String.class))
                .debtorAcc(row.get("debtor_acc", String.class))
                .weightVouNo(row.get("weight_vou_no", String.class))
                .post(row.get("post", Boolean.class))
                .sPay(row.get("s_pay", Boolean.class))
                .tranSource(row.get("tran_source", Integer.class))
                .totalPayment(row.get("total_payment", Double.class))
                .opening(row.get("opening", Double.class))
                .totalBalance(row.get("total_balance", Double.class))
                .outstanding(row.get("outstanding", Double.class))
                .build();
    }

    private Mono<Boolean> updateDeleteStatus(SaleHisKey key, boolean status) {
        String sql = """
                update sale_his
                set deleted =:status,updated_date=:updatedDate
                where vou_no=:vouNo
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("status", status)
                .bind("updatedDate", LocalDateTime.now())
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<SaleHis> generateForAcc(String vouNo, String compCode) {
        String sql = """
                select sh.vou_no,sh.comp_code,sh.vou_date,sh.trader_code,
                sh.cur_code,sh.reference,sh.remark,sh.deleted,sh.project_no,g.batch_no,
                sh.vou_total,sh.grand_total,sh.disc_p,sh.discount,
                sh.tax_p,sh.tax_amt,sh.paid,sh.vou_balance,sh.total_payment,sh.dept_id,
                ifnull(sh.dept_code,ifnull(l.dept_code,a.dep_code)) dept_code,
                ifnull(sh.account,a.source_acc) src_acc,
                ifnull(sh.cash_acc,ifnull(l.cash_acc,a.pay_acc)) cash_acc,
                ifnull(sh.debtor_acc,ifnull(t.account,a.bal_acc)) bal_acc,
                a.dis_acc,a.tax_acc
                from sale_his sh left join grn g
                on sh.grn_vou_no = g.vou_no
                and sh.comp_code = g.comp_code
                join location l
                on sh.loc_code = l.loc_code
                and sh.comp_code = l.comp_code
                join trader t on sh.trader_code= t.code
                and sh.comp_code = t.comp_code
                and sh.comp_code =l.comp_code
                join acc_setting a
                on sh.comp_code = a.comp_code
                and a.type ='SALE'
                where sh.comp_code =:compCode
                and sh.vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row) -> SaleHis.builder()
                        .key(SaleHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .traderCode(row.get("trader_code", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .reference(row.get("reference", String.class))
                        .remark(row.get("remark", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .projectNo(row.get("project_no", String.class))
                        .grnVouNo(row.get("batch_no", String.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .grandTotal(row.get("grand_total", Double.class))
                        .discP(row.get("disc_p", Double.class))
                        .discount(row.get("discount", Double.class))
                        .taxPercent(row.get("tax_p", Double.class))
                        .taxAmt(row.get("tax_amt", Double.class))
                        .paid(row.get("paid", Double.class))
                        .balance(row.get("vou_balance", Double.class))
                        .totalPayment(row.get("total_payment", Double.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .deptCode(row.get("dept_code", String.class))
                        .saleAcc(row.get("src_acc", String.class))
                        .cashAcc(row.get("cash_acc", String.class))
                        .debtorAcc(row.get("bal_acc", String.class))
                        .disAcc(row.get("dis_acc", String.class))
                        .taxAcc(row.get("tax_acc", String.class))
                        .build()).one();
    }

    public Mono<Boolean> updateACK(String ack, String vouNo, String compCode) {
        String sql = """
                update sale_his set intg_upd_status = :ACK where vou_no =:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("ACK", Parameters.in(R2dbcType.VARCHAR, ack))
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<VSale> getSaleSummaryByDepartment(String fromDate, String toDate, String compCode, Integer deptId) {
        String sql = """
                select *
                from (
                select 1 sort_id,'Sale' tran_source,sum(vou_total) vou_total,sum(vou_balance) vou_balance,
                sum(paid) paid,sum(discount)*-1 discount,cur_code,dept_id,count(*) vou_count
                from sale_his
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and comp_code =  :compCode
                and (dept_id = :deptId or 0 = :deptId)
                group by dept_id,cur_code
                	union
                select 2 sort_id,'Return In' tran_source,sum(vou_total)*-1 vou_total,sum(balance) vou_balance,
                sum(paid)*-1 paid,sum(discount)*-1 discount,cur_code,dept_id,count(*) vou_count
                from ret_in_his
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and comp_code =  :compCode
                and (dept_id = :deptId or 0 = :deptId)
                group by dept_id,cur_code
                	union
                select 3 sort_id,'Customer Received' tran_source,0,0,sum(amount) paid,0,cur_code,dept_id,count(*) vou_count
                from payment_his
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and comp_code =  :compCode
                and (dept_id = :deptId or 0 = :deptId)
                and tran_option ='C'
                group by dept_id,cur_code
                    union
                select 4 sort_id,'Purchase' tran_source,sum(vou_total)*-1 vou_total,sum(balance)*-1 vou_balance,
                sum(paid)*-1 paid,sum(discount) discount,cur_code,dept_id,count(*) vou_count
                from pur_his
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and comp_code =  :compCode
                and (dept_id = :deptId or 0 = :deptId)
                group by dept_id,cur_code
                )a
                order by dept_id,sort_id
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId",deptId)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    s.setTranSource(row.get("tran_source", String.class));
                    s.setVouTotal(row.get("vou_total", Double.class));
                    s.setVouBalance(row.get("vou_balance", Double.class));
                    s.setPaid(row.get("paid", Double.class));
                    s.setDiscount(row.get("discount", Double.class));
                    s.setDeptId(row.get("dept_id", Integer.class));
                    s.setVouCount(row.get("vou_count", Integer.class));
                    s.setUniqueId(row.get("sort_id", Integer.class));
                    return s;
                })
                .all();
    }

}
