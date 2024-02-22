package cv.api.service;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisDetail;
import cv.api.model.VSale;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentHisServiceImpl implements PaymentHisService {
    private final SaleHisService saleHisService;
    private final DatabaseClient client;
    private final VouNoService vouNoService;

    @Override
    public Mono<PaymentHis> save(PaymentHis dto) {
        return saveOrUpdate(dto).flatMap(payment -> deleteDetail(payment.getVouNo(), payment.getCompCode()).flatMap(delete -> {
            List<PaymentHisDetail> list = dto.getListDetail();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> Util1.getDouble(detail.getPayAmt()) != 0)
                        .concatMap(detail -> {
                            int uniqueId = list.indexOf(detail) + 1;
                            detail.setUniqueId(uniqueId);
                            detail.setVouNo(payment.getVouNo());
                            detail.setCompCode(payment.getCompCode());
                            detail.setDeptId(payment.getDeptId());
                            return updateSale(detail, true).then(insertDetail(detail));
                        }).then(Mono.just(payment));
            }
            return Mono.empty();
        }));
    }

    public Mono<PaymentHisDetail> insertDetail(PaymentHisDetail his) {
        String sql = """
                INSERT INTO payment_his_detail
                (vou_no, comp_code, unique_id, dept_id, sale_vou_date, sale_vou_no, pay_amt,
                 dis_percent, dis_amt, cur_code, remark, reference, full_paid, vou_total, vou_balance)
                VALUES
                (:vouNo, :compCode, :uniqueId, :deptId, :saleVouDate, :saleVouNo, :payAmt,
                 :disPercent, :disAmt, :curCode, :remark, :reference, :fullPaid, :vouTotal, :vouBalance)
                """;
        return executeUpdate(sql, his);
    }

    private Mono<PaymentHisDetail> executeUpdate(String sql, PaymentHisDetail his) {
        return client.sql(sql)
                .bind("vouNo", his.getVouNo())
                .bind("compCode", his.getCompCode())
                .bind("uniqueId", his.getUniqueId())
                .bind("deptId", his.getDeptId())
                .bind("saleVouDate", his.getSaleDate())
                .bind("saleVouNo", his.getSaleVouNo())
                .bind("payAmt", his.getPayAmt())
                .bind("disPercent", Parameters.in(R2dbcType.DOUBLE, his.getDisPercent()))
                .bind("disAmt", Parameters.in(R2dbcType.DOUBLE, his.getDisAmt()))
                .bind("curCode", Parameters.in(R2dbcType.VARCHAR, his.getCurCode()))
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, his.getRemark()))
                .bind("reference", Parameters.in(R2dbcType.VARCHAR, his.getReference()))
                .bind("fullPaid", his.getFullPaid())
                .bind("vouTotal", his.getVouTotal())
                .bind("vouBalance", his.getVouBalance())
                .fetch().rowsUpdated().thenReturn(his);
    }

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from payment_his_detail
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


    private Mono<PaymentHis> saveOrUpdate(PaymentHis dto) {
        String vouNo = dto.getVouNo();
        String compCode = dto.getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        String tranOption = dto.getTranOption();
        String option = tranOption.equals("C") ? "RECEIVE" : "PAYMENT";
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, option, compCode, macId)
                    .flatMap(seqNo -> {
                        seqNo = tranOption + "-" + seqNo;
                        dto.setVouNo(seqNo);
                        dto.setCreatedDate(LocalDateTime.now());
                        dto.setUpdatedDate(LocalDateTime.now());
                        return insertPayment(dto);
                    });
        } else {
            return updatePayment(dto);
        }
    }

    public Mono<PaymentHis> insertPayment(PaymentHis his) {
        String sql = """
                INSERT INTO payment_his
                (vou_no, comp_code, dept_id, vou_date, trader_code, cur_code, remark, amount,
                 deleted, created_date, created_by, updated_date, updated_by, mac_id, account,
                 project_no, intg_upd_status, tran_option)
                VALUES
                (:vouNo, :compCode, :deptId, :vouDate, :traderCode, :curCode, :remark, :amount,
                 :deleted, :createdDate, :createdBy, :updatedDate, :updatedBy, :macId, :account,
                 :projectNo, :intgUpdStatus, :tranOption)
                """;
        return executeUpdate(sql, his);
    }

    public Mono<PaymentHis> updatePayment(PaymentHis his) {
        String sql = """
                UPDATE payment_his
                SET dept_id = :deptId,
                    vou_date = :vouDate,
                    trader_code = :traderCode,
                    cur_code = :curCode,
                    remark = :remark,
                    amount = :amount,
                    deleted = :deleted,
                    created_date = :createdDate,
                    created_by = :createdBy,
                    updated_date = :updatedDate,
                    updated_by = :updatedBy,
                    mac_id = :macId,
                    account = :account,
                    project_no = :projectNo,
                    intg_upd_status = :intgUpdStatus,
                    tran_option = :tranOption
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, his);
    }

    private Mono<PaymentHis> executeUpdate(String sql, PaymentHis his) {
        return client.sql(sql)
                .bind("vouNo", his.getVouNo())
                .bind("compCode", his.getCompCode())
                .bind("deptId", his.getDeptId())
                .bind("vouDate", his.getVouDate())
                .bind("traderCode", his.getTraderCode())
                .bind("curCode", his.getCurCode())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, his.getRemark()))
                .bind("amount", his.getAmount())
                .bind("deleted", his.getDeleted())
                .bind("createdDate", his.getCreatedDate())
                .bind("createdBy", his.getCreatedBy())
                .bind("updatedDate", LocalDateTime.now())
                .bind("updatedBy", Parameters.in(R2dbcType.TIMESTAMP, his.getUpdatedBy()))
                .bind("macId", his.getMacId())
                .bind("account", Parameters.in(R2dbcType.VARCHAR, his.getAccount()))
                .bind("projectNo", Parameters.in(R2dbcType.VARCHAR, his.getProjectNo()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, his.getIntgUpdStatus()))
                .bind("tranOption", his.getTranOption())
                .fetch().rowsUpdated().thenReturn(his);
    }

    private PaymentHis mapToPayment(Row row) {
        return PaymentHis.builder()
                .vouNo(row.get("vou_no", String.class))
                .compCode(row.get("comp_code", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .traderCode(row.get("trader_code", String.class))
                .curCode(row.get("cur_code", String.class))
                .remark(row.get("remark", String.class))
                .amount(row.get("amount", Double.class))
                .deleted(row.get("deleted", Boolean.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .macId(row.get("mac_id", Integer.class))
                .account(row.get("account", String.class))
                .projectNo(row.get("project_no", String.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .tranOption(row.get("tran_option", String.class))
                .build();
    }


    private Mono<Boolean> updateSale(PaymentHisDetail ph, boolean post) {
        String saleVouNo = ph.getSaleVouNo();
        String compCode = ph.getCompCode();
        if (!Util1.isNullOrEmpty(saleVouNo)) {
            return saleHisService.updatePost(saleVouNo, compCode, post);
        }
        return Mono.just(false);
    }


    @Override
    public Mono<PaymentHis> find(String vouNo, String compCode) {
        String sql = """
                select *
                from payment_his
                where comp_code =:compCode
                and vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row, rowMetadata) -> mapToPayment(row)).one();

    }

    @Override
    public Mono<Boolean> delete(String vouNo, String compCode) {
        String sql = """
                update payment_his set deleted = true where vou_no = :vouNo and comp_code = :compCode
                """;
        Mono<Boolean> deleteMono = client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated()
                .thenReturn(true);

        Mono<Boolean> updateMono = getPaymentDetail(vouNo, compCode)
                .flatMap(pd -> updateSale(pd, false))
                .then(Mono.just(true));

        return deleteMono.then(updateMono);
    }

    @Override
    public Mono<Boolean> restore(String vouNo, String compCode) {
        String sql = """
                update payment_his set deleted = true where vou_no = :vouNo and comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    @Override
    public Flux<PaymentHis> search(FilterObject filter) {
        String tranOption = Util1.isNull(filter.getTranOption(), "-");
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String saleVouNo = Util1.isNull(filter.getSaleVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String account = Util1.isAll(filter.getAccount());
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        if (tranOption.equals("C") || tranOption.equals("S")) {
            String sql = """
                    select a.*,t.trader_name
                    from (
                    select ph.*
                    from payment_his ph,payment_his_detail phd
                    where ph.vou_no = phd.vou_no
                    and ph.comp_code = phd.comp_code
                    and ph.deleted =:deleted
                    and ph.comp_code =:compCode
                    and ph.cur_code = :curCode
                    and ph.tran_option =:tranOption
                    and (ph.trader_code = :traderCode or '-' =:traderCode)
                    and (ph.project_no =:projectNo or '-' =:projectNo)
                    and (ph.vou_no =:vouNo or '-' =:vouNo)
                    and (phd.sale_vou_no = :saleVouNo or '-'=:saleVouNo)
                    and (ph.created_by = :createdBy or '-' =:createdBy)
                    and (ph.account = :account or '-' =:account)
                    and (ph.remark REGEXP :remark or '-' = :remark)
                    and date(ph.vou_date) between :fromDate and :toDate)a
                    join trader t on a.trader_code = t.code
                    and a.comp_code = t.comp_code
                    group by a.vou_no
                    order by a.vou_date desc""";
            return client.sql(sql)
                    .bind("deleted", deleted)
                    .bind("compCode", compCode)
                    .bind("curCode", curCode)
                    .bind("tranOption", tranOption)
                    .bind("traderCode", traderCode)
                    .bind("projectNo", projectNo)
                    .bind("vouNo", vouNo)
                    .bind("saleVouNo", saleVouNo)
                    .bind("fromDate", fromDate)
                    .bind("toDate", toDate)
                    .bind("createdBy", userCode)
                    .bind("account", account)
                    .bind("remark", remark)
                    .map((row) -> PaymentHis.builder()
                            .vouNo(row.get("vou_no", String.class))
                            .compCode(row.get("comp_code", String.class))
                            .deptId(row.get("dept_id", Integer.class))
                            .vouDate(row.get("vou_date", LocalDateTime.class))
                            .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                            .amount(row.get("amount", Double.class))
                            .remark(row.get("remark", String.class))
                            .deleted(row.get("deleted", Boolean.class))
                            .createdBy(row.get("created_by", String.class))
                            .projectNo(row.get("project_no", String.class))
                            .traderCode(row.get("trader_code", String.class))
                            .traderName(row.get("trader_name", String.class))
                            .account(row.get("account", String.class))
                            .curCode(row.get("cur_code", String.class))
                            .createdDate(row.get("created_date", LocalDateTime.class))
                            .createdBy(row.get("created_by", String.class))
                            .build())
                    .all();

        }
        return Flux.empty();
    }

    @Override
    public Flux<PaymentHis> unUploadVoucher(LocalDateTime syncDate) {
        String sql = """
                select *
                from payment_his
                where intg_upd_status is null
                and vou_date >= :syncDate
                """;
        return client.sql(sql)
                .bind("syncDate", syncDate)
                .map((row, rowMetadata) -> mapToPayment(row)).all();
    }

    @Override
    public Flux<VSale> getPaymentVoucher(String vouNo, String compCode) {
        String sql = """
                select a.*,t.user_code,t.trader_name,t.address
                from (
                select ph.vou_date,ph.vou_no,ph.trader_code,ph.cur_code,ph.amount,phd.sale_vou_no,
                phd.sale_vou_date,phd.vou_total,phd.pay_amt,phd.vou_balance,phd.unique_id,ph.comp_code,ph.tran_option
                from payment_his ph, payment_his_detail phd
                where ph.vou_no = phd.vou_no
                and ph.comp_code = phd.comp_code
                and ph.vou_no =:vouNo
                and ph.comp_code =:compCode
                )a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by unique_id""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> VSale.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .paid(row.get("pay_amt", Double.class))
                        .vouBalance(row.get("vou_balance", Double.class))
                        .userCode(row.get("user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .address(row.get("address", String.class))
                        .tranOption(row.get("tran_option", String.class))
                        .saleVouNo(row.get("sale_vou_no", String.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .payDate(Util1.toDateStr(row.get("vou_date", LocalDateTime.class), "dd/MM/yyyy"))
                        .vouDate(Util1.toDateStr(row.get("sale_vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .build()).all();

    }


    @Override
    public Flux<PaymentHisDetail> getTraderBalance(String traderCode, String tranOption, String compCode) {
        if (tranOption.equals("C") || tranOption.equals("S")) {
            String sql;
            if (tranOption.equals("C")) {
                sql = """
                        select sh.vou_date,sh.reference,sh.remark,sh.vou_total,b.vou_no,b.cur_code,b.outstanding
                        from (
                        select vou_no,cur_code,sum(vou_balance) outstanding,comp_code
                        from (
                        select vou_no,cur_code,round(vou_balance,0) vou_balance,comp_code
                        from sale_his
                        where trader_code=:traderCode
                        and comp_code =:compCode
                        and deleted = false
                        and vou_balance>0
                          union all
                        select phd.sale_vou_no,phd.cur_code,round(phd.pay_amt,0)*-1,pd.comp_code
                        from payment_his pd join payment_his_detail phd
                        on pd.vou_no = phd.vou_no
                        and pd.comp_code = phd.comp_code
                        where pd.trader_code=:traderCode
                        and pd.comp_code =:compCode
                        and pd.tran_option =:tranOption
                        and pd.deleted = false
                        )a
                        group by vou_no,cur_code
                        )b
                        join sale_his sh
                        on b.vou_no = sh.vou_no
                        and b.comp_code = sh.comp_code
                        where outstanding<>0
                        order by vou_date""";
            } else {
                sql = """
                        select sh.vou_date,sh.reference,sh.remark,sh.vou_total,b.vou_no,b.cur_code,b.outstanding
                        from (
                        select vou_no,cur_code,sum(balance) outstanding,comp_code
                        from (
                        select vou_no,cur_code,round(balance,0) balance,comp_code
                        from pur_his
                        where trader_code=:traderCode
                        and comp_code =:compCode
                        and deleted = false
                        and balance>0
                            union all
                        select phd.sale_vou_no,phd.cur_code,round(phd.pay_amt,0)*-1,pd.comp_code
                        from payment_his pd join payment_his_detail phd
                        on pd.vou_no = phd.vou_no
                        and pd.comp_code = phd.comp_code
                        where pd.trader_code=:traderCode
                        and pd.comp_code =:compCode
                        and pd.tran_option =:tranOption
                        and pd.deleted = false
                        )a
                        group by vou_no,cur_code
                        )b
                        join pur_his sh
                        on b.vou_no = sh.vou_no
                        and b.comp_code = sh.comp_code
                        where b.outstanding<>0
                        order by vou_date;""";

            }
            return client.sql(sql)
                    .bind("traderCode", traderCode)
                    .bind("compCode", compCode)
                    .bind("tranOption", tranOption)
                    .map((row, rowMetadata) -> PaymentHisDetail.builder()
                            .saleDate(row.get("vou_date", LocalDate.class))
                            .saleVouNo(row.get("vou_no", String.class))
                            .remark(row.get("remark", String.class))
                            .vouTotal(row.get("vou_total", Double.class))
                            .vouBalance(row.get("outstanding", Double.class))
                            .curCode(row.get("cur_code", String.class))
                            .reference(row.get("reference", String.class))
                            .build())
                    .all();

        }
        return Flux.empty();
    }

    @Override
    public Mono<PaymentHis> getTraderBalanceSummary(String traderCode, String tranOption, String compCode) {
        String sql;
        if (tranOption.equals("C")) {
            sql = """
                    select trader_code,cur_code,sum(vou_balance) outstanding,comp_code
                    from (
                    select trader_code,cur_code,round(sum(vou_balance),0) vou_balance,comp_code
                    from sale_his
                    where trader_code=:traderCode
                    and comp_code =:compCode
                    and deleted = false
                    and vou_balance>0
                    group by trader_code,cur_code,comp_code
                      union all
                    select pd.trader_code,phd.cur_code,round(sum(phd.pay_amt),0)*-1,pd.comp_code
                    from payment_his pd join payment_his_detail phd
                    on pd.vou_no = phd.vou_no
                    and pd.comp_code = phd.comp_code
                    where pd.trader_code=:traderCode
                    and pd.comp_code =:compCode
                    and pd.tran_option ='C'
                    and pd.deleted = false
                    group by pd.trader_code,pd.cur_code,pd.comp_code
                    )a
                    group by trader_code,cur_code
                    """;
        } else {
            sql = """
                    select trader_code,cur_code,sum(vou_balance) outstanding,comp_code
                    from (
                    select trader_code,cur_code,round(sum(balance),0) vou_balance,comp_code
                    from pur_his
                    where trader_code=:traderCode
                    and comp_code =:compCode
                    and deleted = false
                    and balance>0
                    group by trader_code,cur_code,comp_code
                      union all
                    select pd.trader_code,phd.cur_code,round(sum(phd.pay_amt),0)*-1,pd.comp_code
                    from payment_his pd join payment_his_detail phd
                    on pd.vou_no = phd.vou_no
                    and pd.comp_code = phd.comp_code
                    where pd.trader_code=:traderCode
                    and pd.comp_code =:compCode
                    and pd.tran_option ='S'
                    and pd.deleted = false
                    group by pd.trader_code,pd.cur_code,pd.comp_code
                    )a
                    group by trader_code,cur_code
                    """;
        }
        return client.sql(sql)
                .bind("traderCode", traderCode)
                .bind("compCode", compCode)
                .map((row) -> PaymentHis.builder()
                        .traderCode(row.get("trader_code", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .amount(row.get("outstanding", Double.class))
                        .build()).one();
    }

    @Override
    public Flux<PaymentHisDetail> getPaymentDetail(String vouNo, String compCode) {
        String sql = """
                select *
                from payment_his_detail
                where comp_code = :compCode
                and vou_no=:vouNo
                order by unique_id;
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row) -> PaymentHisDetail.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .deptId(row.get("unique_id", Integer.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .saleDate(row.get("sale_vou_date", LocalDate.class))
                        .saleVouNo(row.get("sale_vou_no", String.class))
                        .payAmt(row.get("pay_amt", Double.class))
                        .disPercent(row.get("dis_percent", Double.class))
                        .disAmt(row.get("dis_amt", Double.class))
                        .curCode(row.get("cur_code", String.class))
                        .remark(row.get("remark", String.class))
                        .reference(row.get("reference", String.class))
                        .fullPaid(row.get("full_paid", Boolean.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .vouBalance(row.get("vou_balance", Double.class))
                        .build())
                .all();

    }
}
