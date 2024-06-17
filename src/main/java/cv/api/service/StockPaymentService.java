package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.dto.StockPayment;
import cv.api.dto.StockPaymentDetail;
import cv.api.exception.ResponseUtil;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockPaymentService {
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<StockPayment> save(StockPayment dto) {
        return isValid(dto)
                .flatMap(his -> operator.transactional(Mono.defer(() -> saveOrUpdate(his).flatMap(payment -> deleteDetail(payment.getVouNo(), payment.getCompCode()).flatMap(delete -> {
                    List<StockPaymentDetail> list = his.getListDetail();
                    return Flux.fromIterable(list)
                            .filter(detail -> Util1.getDouble(detail.getPayQty()) > 0 || Util1.getDouble(detail.getPayBag()) > 0)
                            .concatMap(detail -> {
                                int uniqueId = list.indexOf(detail) + 1;
                                detail.setUniqueId(uniqueId);
                                detail.setVouNo(payment.getVouNo());
                                detail.setCompCode(payment.getCompCode());
                                return insertDetail(detail);
                            })
                            .then(Mono.just(payment));
                })))));

    }

    private Mono<StockPayment> isValid(StockPayment sh) {
        List<StockPaymentDetail> list = Util1.nullToEmpty(sh.getListDetail());
        list.removeIf(t -> Util1.getDouble(t.getPayQty()) <= 0 || Util1.getDouble(t.getPayBag()) <= 0);
        if (list.isEmpty()) {
            return ResponseUtil.createBadRequest("Detail is null/empty");
        } else if (Util1.isNullOrEmpty(sh.getDeptId())) {
            return ResponseUtil.createBadRequest("deptId is null from mac id : " + sh.getMacId());
        } else if (Util1.isNullOrEmpty(sh.getVouDate())) {
            return ResponseUtil.createBadRequest("Voucher Date is null");
        }
        return Mono.just(sh);
    }

    private Mono<StockPayment> saveOrUpdate(StockPayment payment) {
        String vouNo = payment.getVouNo();
        String compCode = payment.getCompCode();
        int deptId = payment.getDeptId();
        int macId = payment.getMacId();
        payment.setVouDate(Util1.toDateTime(payment.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            return vouNoService.getVouNo(deptId, "StockPayment", compCode, macId)
                    .flatMap(seqNo -> {
                        payment.setVouNo(seqNo);
                        payment.setCreatedDate(LocalDateTime.now());
                        payment.setUpdatedDate(LocalDateTime.now());
                        return insert(payment);
                    });
        } else {
            return update(payment);
        }
    }

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from stock_payment_detail
                where vou_no=:vouNo and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated()
                .thenReturn(true)
                .defaultIfEmpty(false);
    }

    public Mono<StockPayment> update(StockPayment data) {
        String sql = """
                     UPDATE stock_payment
                     SET
                     vou_no = :vouNo,
                     comp_code = :compCode,
                     dept_id = :deptId,
                     vou_date = :vouDate,
                     trader_code = :traderCode,
                     loc_code =:locCode,
                     remark = :remark,
                     reference = :reference,
                     deleted = :deleted,
                     created_date = :createdDate,
                     updated_date = :updatedDate,
                     created_by = :createdBy,
                     updated_by = :updatedBy,
                     mac_id = :macId,
                     tran_option = :tranOption,
                     calculate = :calculate
                     WHERE vou_no = :vouNo AND comp_code = :compCode;
                """;

        return client.sql(sql)
                .bind("vouNo", data.getVouNo())
                .bind("compCode", data.getCompCode())
                .bind("deptId", data.getDeptId())
                .bind("vouDate", data.getVouDate())
                .bind("traderCode", data.getTraderCode())
                .bind("locCode", data.getLocCode())
                .bind("remark", data.getRemark())
                .bind("reference", data.getReference())
                .bind("deleted", data.getDeleted())
                .bind("createdDate", data.getCreatedDate())
                .bind("updatedDate", LocalDateTime.now())
                .bind("createdBy", data.getCreatedBy())
                .bind("updatedBy", data.getUpdatedBy())
                .bind("macId", data.getMacId())
                .bind("tranOption", data.getTranOption())
                .bind("calculate", data.getCalculate())
                .fetch()
                .rowsUpdated()
                .thenReturn(data);
    }

    private Mono<StockPayment> insert(StockPayment p) {
        String sql = """
                INSERT INTO stock_payment
                (vou_no,
                comp_code,
                dept_id,
                vou_date,
                trader_code,
                loc_code,
                remark,
                reference,
                deleted,
                calculate,
                created_date,
                created_by,
                updated_date,
                updated_by,
                mac_id,
                tran_option)
                VALUES
                (:vouNo,
                :compCode,
                :deptId,
                :vouDate,
                :traderCode,
                :locCode,
                :remark,
                :reference,
                :deleted,
                :calculate,
                :createdDate,
                :createdBy,
                :updatedDate,
                :updatedBy,
                :macId,
                :tranOption);
                """;

        return client.sql(sql)
                .bind("vouNo", p.getVouNo())
                .bind("compCode", p.getCompCode())
                .bind("deptId", p.getDeptId())
                .bind("vouDate", p.getVouDate())
                .bind("traderCode", p.getTraderCode())
                .bind("locCode", p.getLocCode())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, p.getRemark()))
                .bind("reference", Parameters.in(R2dbcType.VARCHAR, p.getReference()))
                .bind("deleted", p.getDeleted())
                .bind("calculate", p.getCalculate())
                .bind("createdDate", p.getCreatedDate())
                .bind("createdBy", p.getCreatedBy())
                .bind("updatedDate", p.getUpdatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, p.getUpdatedBy()))
                .bind("macId", p.getMacId())
                .bind("tranOption", p.getTranOption())
                .fetch()
                .rowsUpdated()
                .thenReturn(p);
    }

    private Mono<StockPaymentDetail> insertDetail(StockPaymentDetail detail) {
        String sql = """
                INSERT INTO stock_payment_detail
                (vou_no,
                comp_code,
                unique_id,
                ref_date,
                stock_code,
                ref_no,
                qty,
                pay_qty,
                bal_qty,
                bag,
                pay_bag,
                bal_bag,
                remark,
                reference,
                full_paid)
                VALUES
                (:vouNo,
                :compCode,
                :uniqueId,
                :refDate,
                :stockCode,
                :refNo,
                :qty,
                :payQty,
                :balQty,
                :bag,
                :payBag,
                :balBag,
                :remark,
                :reference,
                :fullPaid);
                """;
        return client.sql(sql)
                .bind("vouNo", detail.getVouNo())
                .bind("compCode", detail.getCompCode())
                .bind("uniqueId", detail.getUniqueId())
                .bind("refDate", detail.getRefDate())
                .bind("stockCode", detail.getStockCode())
                .bind("refNo", detail.getRefNo())
                .bind("qty", detail.getQty())
                .bind("payQty", detail.getPayQty())
                .bind("balQty", detail.getBalQty())
                .bind("bag", detail.getBag())
                .bind("payBag", detail.getPayBag())
                .bind("balBag", detail.getBalBag())
                .bind("remark", detail.getRemark())
                .bind("reference", detail.getReference())
                .bind("fullPaid", detail.getFullPaid())
                .fetch()
                .one()
                .map(row -> detail);
    }


    public Flux<StockPaymentDetail> calculatePaymentQty(String traderCode, String compCode, String tranOption) {
        // group type 1 is for paddy
        String sql;
        if (tranOption.equals("C")) {
            sql = """
                    select sh.project_no,sh.vou_date,sh.reference,sh.remark,b.vou_no,b.stock_code,
                    sh.s_user_code,sh.stock_name,
                    sh.qty,b.bal_qty
                    from (
                    select vou_no,stock_code,sum(qty) bal_qty,comp_code
                    from (
                    select vou_no,stock_code,qty,comp_code
                    from v_sale
                    where trader_code=:traderCode
                    and comp_code =:compCode
                    and deleted = false
                    and group_type =1
                    and s_pay = true
                    and qty>0
                      union all
                    select phd.ref_no,stock_code,phd.pay_qty*-1,pd.comp_code
                    from stock_payment pd join stock_payment_detail phd
                    on pd.vou_no = phd.vou_no
                    and pd.comp_code = phd.comp_code
                    where pd.trader_code=:traderCode
                    and pd.comp_code =:compCode
                    and pd.tran_option =:tranOption
                    and pd.deleted = false
                    and phd.pay_qty>0
                    )a
                    group by vou_no,stock_code
                    )b
                    join v_sale sh
                    on b.vou_no = sh.vou_no
                    and b.stock_code = sh.stock_code
                    and b.comp_code = sh.comp_code
                    having bal_qty <>0
                    order by vou_date
                    """;
        } else {
            sql = """
                    select sh.project_no,sh.vou_date,sh.reference,sh.remark,b.vou_no,b.stock_code,
                    sh.s_user_code,sh.stock_name,
                    sh.qty,b.bal_qty
                    from (
                    select vou_no,stock_code,sum(qty) bal_qty,comp_code
                    from (
                    select vou_no,stock_code,qty,comp_code
                    from v_purchase
                    where trader_code=:traderCode
                    and comp_code =:compCode
                    and deleted = false
                    and group_type =1
                    and s_rec = true
                    and qty>0
                      union all
                    select phd.ref_no,stock_code,phd.pay_qty*-1,pd.comp_code
                    from stock_payment pd join stock_payment_detail phd
                    on pd.vou_no = phd.vou_no
                    and pd.comp_code = phd.comp_code
                    where pd.trader_code=:traderCode
                    and pd.comp_code =:compCode
                    and pd.tran_option =:tranOption
                    and pd.deleted = false
                    and phd.pay_qty>0
                    )a
                    group by vou_no,stock_code
                    )b
                    join v_purchase sh
                    on b.vou_no = sh.vou_no
                    and b.stock_code = sh.stock_code
                    and b.comp_code = sh.comp_code
                    having bal_qty <>0
                    order by vou_date
                    """;
        }
        //vou_date, reference, remark, vou_no, qty, bag, bal_qty, bal_bag
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("traderCode", traderCode)
                .bind("tranOption", tranOption)
                .map((row) -> StockPaymentDetail.builder()
                        .projectNo(row.get("project_no", String.class))
                        .refDate(row.get("vou_date", LocalDate.class))
                        .reference(row.get("reference", String.class))
                        .remark(row.get("remark", String.class))
                        .refNo(row.get("vou_no", String.class))
                        .qty(row.get("qty", Double.class))
                        .balQty(row.get("bal_qty", Double.class))
                        .stockCode(row.get("stock_code", String.class))
                        .stockUserCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .build())
                .all();
    }

    public Flux<StockPaymentDetail> calculatePaymentBag(String traderCode, String compCode, String tranOption) {
        // group type 2 is for rice
        String sql;
        if (tranOption.equals("C")) {
            sql = """
                    select sh.project_no,sh.vou_date,sh.reference,sh.remark,b.vou_no,b.stock_code,
                    sh.s_user_code,sh.stock_name,
                    sh.bag,b.bal_bag
                    from (
                    select vou_no,stock_code,sum(bag) bal_bag,comp_code
                    from (
                    select vou_no,stock_code,bag,comp_code
                    from v_sale
                    where trader_code=:traderCode
                    and comp_code =:compCode
                    and deleted = false
                    and s_pay = true
                    and group_type =2
                    and bag>0
                      union all
                    select phd.ref_no,stock_code,phd.pay_bag*-1,pd.comp_code
                    from stock_payment pd join stock_payment_detail phd
                    on pd.vou_no = phd.vou_no
                    and pd.comp_code = phd.comp_code
                    where pd.trader_code=:traderCode
                    and pd.comp_code =:compCode
                    and pd.tran_option =:tranOption
                    and pd.deleted = false
                    and phd.pay_bag > 0
                    )a
                    group by vou_no,stock_code
                    )b
                    join v_sale sh
                    on b.vou_no = sh.vou_no
                    and b.stock_code = sh.stock_code
                    and b.comp_code = sh.comp_code
                    having bal_bag <>0
                    order by vou_date
                    """;
        } else {
            sql = """
                    select sh.project_no,sh.vou_date,sh.reference,sh.remark,b.vou_no,b.stock_code,
                    sh.s_user_code,sh.stock_name,
                    sh.bag,b.bal_bag
                    from (
                    select vou_no,stock_code,sum(bag) bal_bag,comp_code
                    from (
                    select vou_no,stock_code,bag,comp_code
                    from v_purchase
                    where trader_code=:traderCode
                    and comp_code =:compCode
                    and deleted = false
                    and s_rec = true
                    and group_type =2
                    and bag>0
                      union all
                    select phd.ref_no,stock_code,phd.pay_bag*-1,pd.comp_code
                    from stock_payment pd join stock_payment_detail phd
                    on pd.vou_no = phd.vou_no
                    and pd.comp_code = phd.comp_code
                    where pd.trader_code=:traderCode
                    and pd.comp_code =:compCode
                    and pd.tran_option =:tranOption
                    and pd.deleted = false
                    and phd.pay_bag > 0
                    )a
                    group by vou_no,stock_code
                    )b
                    join v_purchase sh
                    on b.vou_no = sh.vou_no
                    and b.stock_code = sh.stock_code
                    and b.comp_code = sh.comp_code
                    having bal_bag <>0
                    order by vou_date
                    """;
        }
        //vou_date, reference, remark, vou_no, qty, bag, bal_qty, bal_bag
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("traderCode", traderCode)
                .bind("tranOption", tranOption)
                .map((row) -> StockPaymentDetail.builder()
                        .projectNo(row.get("project_no", String.class))
                        .refDate(row.get("vou_date", LocalDate.class))
                        .reference(row.get("reference", String.class))
                        .remark(row.get("remark", String.class))
                        .refNo(row.get("vou_no", String.class))
                        .bag(row.get("bag", Double.class))
                        .balBag(row.get("bal_bag", Double.class))
                        .stockCode(row.get("stock_code", String.class))
                        .stockUserCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .build())
                .all();
    }

    public Flux<StockPayment> history(ReportFilter filter) {
        String tranOption = Util1.isNull(filter.getTranOption(), "-");
        if (tranOption.equals("C") || tranOption.equals("S")) {
            String fromDate = Util1.isNull(filter.getFromDate(), "-");
            String toDate = Util1.isNull(filter.getToDate(), "-");
            String vouNo = Util1.isNull(filter.getVouNo(), "-");
            String traderCode = Util1.isNull(filter.getTraderCode(), "-");
            String compCode = filter.getCompCode();
            boolean deleted = filter.isDeleted();
            int deptId = filter.getDeptId();
            String stockCode = filter.getStockCode();
            String projectNo = Util1.isAll(filter.getProjectNo());
            String sql = """
                    select a.*,t.trader_name
                    from(
                    select sp.*,spd.project_no,sum(pay_qty) pay_qty,sum(pay_bag)pay_bag
                    from stock_payment sp join stock_payment_detail spd
                    on sp.vou_no = spd.vou_no
                    and sp.comp_code =spd.comp_code
                    where date(sp.vou_date) between :fromDate and :toDate
                    and sp.deleted = :deleted
                    and sp.comp_code =:compCode
                    and sp.tran_option=:tranOption
                    and (sp.dept_id = :deptId or 0 = :deptId)
                    and (sp.trader_code =:traderCode or '-'=:traderCode)
                    and (sp.vou_no =:vouNo or '-'=:vouNo)
                    and (spd.project_no =:projectNo or '-'=:projectNo)
                    and (spd.stock_code =:stockCode or '-'=:stockCode)
                    group by vou_no
                    )a
                    join trader t on a.trader_code = t.code
                    and a.comp_code = t.comp_code
                    order by vou_date desc
                    """;
            return client.sql(sql)
                    .bind("fromDate", fromDate)
                    .bind("toDate", toDate)
                    .bind("deleted", deleted)
                    .bind("compCode", compCode)
                    .bind("tranOption", tranOption)
                    .bind("deptId", deptId)
                    .bind("traderCode", traderCode)
                    .bind("vouNo", vouNo)
                    .bind("projectNo", projectNo)
                    .bind("stockCode", stockCode)
                    .map((row) -> StockPayment.builder()
                            .vouNo(row.get("vou_no", String.class))
                            .compCode(row.get("comp_code", String.class))
                            .deptId(row.get("dept_id", Integer.class))
                            .vouDate(row.get("vou_date", LocalDateTime.class))
                            .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                            .traderCode(row.get("trader_code", String.class))
                            .traderName(row.get("trader_name", String.class))
                            .remark(row.get("remark", String.class))
                            .reference(row.get("reference", String.class))
                            .deleted(row.get("deleted", Boolean.class))
                            .projectNo(row.get("project_no", String.class))
                            .calculate(row.get("calculate", Boolean.class))
                            .payQty(row.get("pay_qty", Double.class))
                            .payBag(row.get("pay_bag", Double.class))
                            .createdDate(row.get("created_date", LocalDateTime.class))
                            .createdBy(row.get("created_by", String.class))
                            .locCode(row.get("loc_code", String.class))
                            .build()).all();
        }
        //vou_no, comp_code, dept_id, vou_date, trader_code, remark, deleted,
        // tran_option, project_no
        return Flux.empty();
    }

    public Flux<StockPaymentDetail> getDetail(String vouNo, String compCode) {
        //vou_no, comp_code, unique_id, ref_date, stock_code, ref_no, qty, bag, pay_qty,
        // pay_bag, bal_qty, bal_bag, remark, reference, full_paid
        String sql = """
                select spd.*,s.user_code,s.stock_name
                from stock_payment_detail spd join stock s
                on spd.stock_code = s.stock_code
                and spd.comp_code = s.comp_code
                where spd.vou_no=:vouNo
                and spd.comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row) -> StockPaymentDetail.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .uniqueId(row.get("unique_id", Integer.class))
                        .refDate(row.get("ref_date", LocalDate.class))
                        .stockCode(row.get("stock_code", String.class))
                        .stockUserCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .refNo(row.get("ref_no", String.class))
                        .qty(row.get("qty", Double.class))
                        .balQty(row.get("bal_qty", Double.class))
                        .payQty(row.get("pay_qty", Double.class))
                        .bag(row.get("bag", Double.class))
                        .balBag(row.get("bal_bag", Double.class))
                        .payBag(row.get("pay_bag", Double.class))
                        .remark(row.get("remark", String.class))
                        .reference(row.get("reference", String.class))
                        .fullPaid(row.get("full_paid", Boolean.class))
                        .build())
                .all();
    }


    public Mono<Boolean> update(String vouNo, String compCode, boolean deleted) {
        String sql = """
                update stock_payment
                set deleted = :deleted
                where vou_no =:vouNo
                and comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .fetch()
                .rowsUpdated()
                .thenReturn(true);
    }
}

