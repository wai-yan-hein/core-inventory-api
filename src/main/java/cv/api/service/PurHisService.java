/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.*;
import cv.api.model.VDescription;
import cv.api.model.VPurchase;
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
public class PurHisService {

    private final PurHisDetailService pdService;
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final WeightService weightService;
    private final LandingService landingService;
    private final PurExpenseService purExpenseService;
    private final TransactionalOperator operator;

    public Mono<PurHis> save(PurHis dto) {
        Integer deptId = dto.getDeptId();
        String compCode = dto.getKey().getCompCode();
        if (deptId == null) {
            log.error("deptId is null from mac id : {}", dto.getMacId());
            return Mono.empty();
        }
        return operator.transactional(Mono.defer(() -> savePurchase(dto).flatMap((pur) -> savePurExpense(pur)
                .then(landingService.updatePost(LandingHisKey.builder()
                                .vouNo(dto.getLandVouNo())
                                .compCode(compCode)
                                .build(), true)
                        .then(weightService.updatePost(WeightHisKey.builder()
                                .vouNo(dto.getWeightVouNo())
                                .compCode(compCode)
                                .build(), true))).thenReturn(pur))));
    }

    private Mono<Boolean> savePurExpense(PurHis ph) {
        String vouNo = ph.getKey().getVouNo();
        String compCode = ph.getKey().getCompCode();
        List<PurExpense> list = ph.getListExpense();
        if (list != null) {
            return purExpenseService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> Util1.getDouble(e.getAmount()) > 0 && e.getKey().getExpenseCode() != null)
                    .flatMap(e -> {
                        if (e.getKey().getUniqueId() == 0) {
                            int uniqueId = list.indexOf(e) + 1;
                            e.getKey().setUniqueId(uniqueId);
                        }
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        return purExpenseService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }

    public Mono<PurHis> findById(PurHisKey key) {
        String sql = """
                select *
                from pur_his
                where vou_no =:vouNo
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<Boolean> delete(PurHisKey key) {
        String compCode = key.getCompCode();
        return findById(key).flatMap(ph -> weightService.updatePost(WeightHisKey.builder()
                        .vouNo(ph.getWeightVouNo())
                        .compCode(compCode).build(), false)
                .then(landingService.updatePost(LandingHisKey.builder()
                        .vouNo(ph.getLandVouNo())
                        .compCode(compCode)
                        .build(), false))).then(updateDeleteStatus(key, true));
    }


    public Mono<Boolean> restore(PurHisKey key) {
        return updateDeleteStatus(key, false);
    }

    public PurHis mapRow(Row row) {
        return PurHis.builder()
                .key(PurHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .balance(row.get("balance", Double.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .deleted(row.get("deleted", Boolean.class))
                .discount(row.get("discount", Double.class))
                .dueDate(row.get("due_date", LocalDate.class))
                .expense(row.get("pur_exp_total", Double.class))
                .paid(row.get("paid", Double.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .refNo(row.get("ref_no", String.class))
                .remark(row.get("remark", String.class))
                .session(row.get("session_id", Integer.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .curCode(row.get("cur_code", String.class))
                .traderCode(row.get("trader_code", String.class))
                .locCode(row.get("loc_code", String.class))
                .discP(row.get("disc_p", Double.class))
                .taxP(row.get("tax_p", Double.class))
                .taxAmt(row.get("tax_amt", Double.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .macId(row.get("mac_id", Integer.class))
                .vouTotal(row.get("vou_total", Double.class))
                .reference(row.get("reference", String.class))
                .vouLock(row.get("vou_lock", Boolean.class))
                .commP(row.get("comm_p", Double.class))
                .commAmt(row.get("comm_amt", Double.class))
                .batchNo(row.get("batch_no", String.class))
                .expense(row.get("expense", Double.class))
                .projectNo(row.get("project_no", String.class))
                .carNo(row.get("car_no", String.class))
                .labourGroupCode(row.get("labour_group_code", String.class))
                .landVouNo(row.get("land_vou_no", String.class))
                .printCount(row.get("print_count", Integer.class))
                .weightVouNo(row.get("weight_vou_no", String.class))
                .cashAcc(row.get("cash_acc", String.class))
                .purchaseAcc(row.get("purchase_acc", String.class))
                .deptCode(row.get("dept_code", String.class))
                .payableAcc(row.get("payable_acc", String.class))
                .grandTotal(row.get("grand_total", Double.class))
                .sRec(row.get("s_rec", Boolean.class))
                .tranSource(row.get("tran_source", Integer.class))
                .outstanding(row.get("outstanding", Double.class))
                .grnVouNo(row.get("grn_vou_no", String.class))
                .build();
    }

    private Mono<Boolean> updateDeleteStatus(PurHisKey key, boolean status) {
        String sql = """
                update pur_his
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


    public Flux<PurHis> unUploadVoucher(LocalDateTime syncDate) {
        String sql = """
                select vou_no,comp_code,deleted
                from pur_his
                where intg_upd_status is null
                and vou_date >= :syncDate
                """;
        return client.sql(sql)
                .bind("syncDate", syncDate)
                .map((row, rowMetadata) -> PurHis.builder()
                        .key(PurHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deleted(row.get("deleted", Boolean.class))
                        .build()).all();
    }


    public Flux<VDescription> getDescription(String str, String compCode, String tranType) {
        String table = tranType.equals("Sale") ? "sale_his" : "pur_his";
        String sql = """
                SELECT DISTINCT car_no
                FROM %s
                WHERE comp_code = :compCode
                AND (car_no LIKE :str)
                LIMIT 20
                """;
        sql = String.format(sql, table);
        sql = String.format(sql, table);
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("str", str)
                .map((row) -> VDescription.builder()
                        .description(row.get("car_no", String.class))
                        .build()).all();
    }

    private Mono<PurHis> savePurchase(PurHis dto) {
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        return saveOrUpdate(dto).flatMap(ri -> pdService.delete(ri.getKey().getVouNo(), ri.getKey().getCompCode()).flatMap(delete -> {
            List<PurHisDetail> list = dto.getListPD();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> Util1.getDouble(detail.getAmount()) != 0)
                        .concatMap(detail -> {
                            if (detail.getKey() == null) {
                                detail.setKey(PurDetailKey.builder().build());
                            }
                            int uniqueId = list.indexOf(detail) + 1;
                            detail.getKey().setUniqueId(uniqueId);
                            detail.getKey().setVouNo(ri.getKey().getVouNo());
                            detail.getKey().setCompCode(ri.getKey().getCompCode());
                            detail.setDeptId(ri.getDeptId());
                            return pdService.insert(detail);
                        })
                        .then(Mono.just(ri));
            } else {
                return Mono.just(ri);
            }
        }));
    }

    private Mono<PurHis> saveOrUpdate(PurHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "PURCHASE", compCode, macId)
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

    public Mono<PurHis> insert(PurHis dto) {
        String sql = """
                INSERT INTO pur_his (
                    vou_no, comp_code, dept_id, balance, created_by, created_date,
                    deleted, discount, due_date, pur_exp_total, paid, vou_date,
                    ref_no, remark, session_id, updated_by, updated_date,
                    cur_code, trader_code, loc_code, disc_p, tax_p, tax_amt,
                    intg_upd_status, mac_id, vou_total, reference, vou_lock,
                    comm_p, comm_amt, batch_no, expense, project_no, car_no,
                    labour_group_code, land_vou_no, print_count, weight_vou_no,
                    cash_acc, purchase_acc, dept_code, payable_acc, grand_total,
                    s_rec, tran_source, outstanding, grn_vou_no
                ) VALUES (
                    :vouNo, :compCode, :deptId, :balance, :createdBy, :createdDate,
                    :deleted, :discount, :dueDate, :purExpTotal, :paid, :vouDate,
                    :refNo, :remark, :sessionId, :updatedBy, :updatedDate,
                    :curCode, :traderCode, :locCode, :discP, :taxP, :taxAmt,
                    :intgUpdStatus, :macId, :vouTotal, :reference, :vouLock,
                    :commP, :commAmt, :batchNo, :expense, :projectNo, :carNo,
                    :labourGroupCode, :landVouNo, :printCount, :weightVouNo,
                    :cashAcc, :purchaseAcc, :deptCode, :payableAcc, :grandTotal,
                    :sRec, :tranSource, :outstanding, :grnVouNo
                )
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<PurHis> update(PurHis dto) {
        String sql = """
                UPDATE pur_his
                SET dept_id = :deptId, balance = :balance,
                    created_by = :createdBy, created_date = :createdDate, deleted = :deleted,
                    discount = :discount, due_date = :dueDate, pur_exp_total = :purExpTotal,
                    paid = :paid, vou_date = :vouDate, ref_no = :refNo, remark = :remark,
                    session_id = :sessionId, updated_by = :updatedBy, updated_date = :updatedDate,
                    cur_code = :curCode, trader_code = :traderCode, loc_code = :locCode,
                    disc_p = :discP, tax_p = :taxP, tax_amt = :taxAmt, intg_upd_status = :intgUpdStatus,
                    mac_id = :macId, vou_total = :vouTotal, reference = :reference, vou_lock = :vouLock,
                    comm_p = :commP, comm_amt = :commAmt, batch_no = :batchNo, expense = :expense,
                    project_no = :projectNo, car_no = :carNo, labour_group_code = :labourGroupCode,
                    land_vou_no = :landVouNo, print_count = :printCount, weight_vou_no = :weightVouNo,
                    cash_acc = :cashAcc, purchase_acc = :purchaseAcc, dept_code = :deptCode,
                    payable_acc = :payableAcc, grand_total = :grandTotal, s_rec = :sRec,
                    tran_source = :tranSource, outstanding = :outstanding, grn_vou_no = :grnVouNo
                WHERE vou_no = :vouNo and comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<PurHis> executeUpdate(String sql, PurHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("balance", dto.getBalance())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("deleted", dto.getDeleted())
                .bind("discount", Parameters.in(R2dbcType.DOUBLE, dto.getDiscount()))
                .bind("dueDate", Parameters.in(R2dbcType.DATE, dto.getDueDate()))
                .bind("purExpTotal", Parameters.in(R2dbcType.DOUBLE, dto.getExpense()))
                .bind("paid", dto.getPaid())
                .bind("vouDate", dto.getVouDate())
                .bind("refNo", Parameters.in(R2dbcType.DOUBLE, dto.getRefNo()))
                .bind("remark", Parameters.in(R2dbcType.DOUBLE, dto.getRemark()))
                .bind("sessionId", Parameters.in(R2dbcType.INTEGER, dto.getSession()))
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("curCode", dto.getCurCode())
                .bind("traderCode", dto.getTraderCode())
                .bind("locCode", dto.getLocCode())
                .bind("discP", Parameters.in(R2dbcType.DOUBLE, dto.getDiscP()))
                .bind("taxP", Parameters.in(R2dbcType.DOUBLE, dto.getTaxP()))
                .bind("taxAmt", Parameters.in(R2dbcType.DOUBLE, dto.getTaxAmt()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.DOUBLE, dto.getIntgUpdStatus()))
                .bind("macId", dto.getMacId())
                .bind("vouTotal", dto.getVouTotal())
                .bind("reference", Parameters.in(R2dbcType.DOUBLE, dto.getReference()))
                .bind("vouLock", dto.getVouLock())
                .bind("commP", Parameters.in(R2dbcType.DOUBLE, dto.getCommP()))
                .bind("commAmt", Parameters.in(R2dbcType.DOUBLE, dto.getCommAmt()))
                .bind("batchNo", Parameters.in(R2dbcType.VARCHAR, dto.getBatchNo()))
                .bind("expense", Parameters.in(R2dbcType.DOUBLE, dto.getExpense()))
                .bind("projectNo", Parameters.in(R2dbcType.VARCHAR, dto.getProjectNo()))
                .bind("carNo", Parameters.in(R2dbcType.VARCHAR, dto.getCarNo()))
                .bind("labourGroupCode", Parameters.in(R2dbcType.VARCHAR, dto.getLabourGroupCode()))
                .bind("landVouNo", Parameters.in(R2dbcType.VARCHAR, dto.getLandVouNo()))
                .bind("printCount", Parameters.in(R2dbcType.INTEGER, dto.getPrintCount()))
                .bind("weightVouNo", Parameters.in(R2dbcType.VARCHAR, dto.getWeightVouNo()))
                .bind("cashAcc", Parameters.in(R2dbcType.VARCHAR, dto.getCashAcc()))
                .bind("purchaseAcc", Parameters.in(R2dbcType.VARCHAR, dto.getPurchaseAcc()))
                .bind("deptCode", Parameters.in(R2dbcType.VARCHAR, dto.getDeptCode()))
                .bind("payableAcc", Parameters.in(R2dbcType.VARCHAR, dto.getPayableAcc()))
                .bind("grandTotal", Parameters.in(R2dbcType.VARCHAR, dto.getGrandTotal()))
                .bind("sRec", Util1.getBoolean(dto.getSRec()))
                .bind("tranSource", Util1.getInteger(dto.getTranSource(), 1))
                .bind("outstanding", Parameters.in(R2dbcType.DOUBLE, dto.getOutstanding()))
                .bind("grnVouNo", Parameters.in(R2dbcType.VARCHAR, dto.getGrnVouNo()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<PurHis> generateForAcc(String vouNo, String compCode) {
        String sql = """
                select sh.vou_no,sh.comp_code,sh.vou_date,sh.trader_code,
                sh.cur_code,sh.reference,sh.remark,sh.deleted,sh.project_no,g.batch_no,
                sh.vou_total,sh.grand_total,sh.disc_p,sh.discount,
                sh.tax_p,sh.tax_amt,sh.paid,sh.balance,sh.dept_id,
                ifnull(sh.dept_code,ifnull(l.dept_code,a.dep_code)) dept_code,
                ifnull(sh.purchase_acc,a.source_acc) src_acc,
                ifnull(sh.cash_acc,ifnull(l.cash_acc,a.pay_acc)) cash_acc,
                ifnull(sh.payable_acc,ifnull(t.account,a.bal_acc)) bal_acc,
                a.dis_acc,a.tax_acc,a.comm_acc
                from pur_his sh left join grn g
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
                and a.type ='PURCHASE'
                where sh.comp_code =:compCode
                and sh.vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row) -> PurHis.builder()
                        .key(PurHisKey.builder()
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
                        .taxP(row.get("tax_p", Double.class))
                        .taxAmt(row.get("tax_amt", Double.class))
                        .paid(row.get("paid", Double.class))
                        .balance(row.get("balance", Double.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .deptCode(row.get("dept_code", String.class))
                        .purchaseAcc(row.get("src_acc", String.class))
                        .cashAcc(row.get("cash_acc", String.class))
                        .payableAcc(row.get("bal_acc", String.class))
                        .disAcc(row.get("dis_acc", String.class))
                        .taxAcc(row.get("tax_acc", String.class))
                        .commAcc(row.get("comm_acc", String.class))
                        .build()).one();
    }

    public Flux<VPurchase> getPurchaseHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String reference = Util1.isNull(filter.getReference(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        boolean deleted =filter.isDeleted();
        Integer deptId = filter.getDeptId();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        remark = remark.concat("%");
        reference = reference.concat("%");
        String sql = """
                SELECT a.*, t.trader_name
                FROM (
                    SELECT vou_date, vou_no, remark, reference, created_by, paid, vou_total, deleted, trader_code, comp_code, dept_id
                    FROM v_purchase
                    WHERE comp_code = :compCode
                    AND (dept_id = :deptId OR 0 = :deptId)
                    AND deleted = :deleted
                    AND DATE(vou_date) BETWEEN :fromDate AND :toDate
                    AND cur_code = :curCode
                    AND (vou_no = :vouNo OR '-' = :vouNo)
                    AND (remark LIKE :remark OR '-%' = :remark)
                    AND (reference LIKE :reference OR '-%' = :reference)
                    AND (trader_code = :traderCode OR '-' = :traderCode)
                    AND (created_by = :userCode OR '-' = :userCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    AND (loc_code = :locCode OR '-' = :locCode)
                    AND (project_no = :projectNo OR '-' = :projectNo)
                    GROUP BY vou_no
                ) a
                JOIN trader t ON a.trader_code = t.code
                AND a.comp_code = t.comp_code
                ORDER BY vou_date DESC
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("deleted", Util1.getBoolean(deleted))
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("reference", reference)
                .bind("traderCode", traderCode)
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("locCode", locCode)
                .bind("projectNo", projectNo)
                .map(row -> VPurchase.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouNo(row.get("vou_no", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .remark(row.get("remark", String.class))
                        .reference(row.get("reference", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .paid(row.get("paid", Double.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .build())
                .all();
    }
    public Mono<Boolean> updateACK(String ack, String vouNo, String compCode) {
        String sql = """
                update pur_his set intg_upd_status = :ACK where vou_no =:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("ACK", Parameters.in(R2dbcType.VARCHAR, ack))
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }
}
