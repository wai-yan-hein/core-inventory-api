/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.RetOutHis;
import cv.api.entity.RetOutHisDetail;
import cv.api.entity.RetOutHisKey;
import cv.api.entity.RetOutKey;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Wai Yan
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RetOutService {

    private final DatabaseClient client;
    private final VouNoService vouNoService;

    public Mono<RetOutHis> save(RetOutHis dto) {
        return saveOrUpdate(dto).flatMap(ri -> deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode()).flatMap(delete -> {
            List<RetOutHisDetail> list = dto.getListRD();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> Util1.getDouble(detail.getAmount()) != 0)
                        .concatMap(detail -> {
                            if (detail.getKey() == null) {
                                detail.setKey(RetOutKey.builder().build());
                            }
                            int uniqueId = list.indexOf(detail) + 1;
                            detail.getKey().setUniqueId(uniqueId);
                            detail.getKey().setVouNo(ri.getKey().getVouNo());
                            detail.getKey().setCompCode(ri.getKey().getCompCode());
                            detail.setDeptId(ri.getDeptId());
                            return insert(detail);
                        })
                        .then(Mono.just(ri));
            } else {
                return Mono.just(ri);
            }
        }));
    }

    private Mono<RetOutHis> saveOrUpdate(RetOutHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "RETURN_OUT", compCode, macId)
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

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from ret_out_his_detail
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


    @Transactional
    public Mono<RetOutHis> insert(RetOutHis retInHis) {
        String sql = """
                INSERT INTO ret_out_his (
                    vou_no, comp_code, dept_id, balance, created_by, created_date, deleted, discount,
                    paid, vou_date, ref_no, remark, session_id, updated_by, updated_date, vou_total,
                    cur_code, trader_code, loc_code, disc_p, intg_upd_status, mac_id, vou_lock,
                    project_no, print_count, tax_amt, tax_p, grand_total, dept_code, src_acc, cash_acc, payable_acc
                ) VALUES (
                    :vouNo, :compCode, :deptId, :balance, :createdBy, :createdDate, :deleted, :discount,
                    :paid, :vouDate, :refNo, :remark, :sessionId, :updatedBy, :updatedDate, :vouTotal,
                    :curCode, :traderCode, :locCode, :discP, :intgUpdStatus, :macId, :vouLock,
                    :projectNo, :printCount, :taxAmt, :taxP, :grandTotal, :deptCode, :srcAcc, :cashAcc, :payableAcc
                )
                """;
        return executeUpdate(sql, retInHis);
    }

    public Mono<RetOutHis> update(RetOutHis retInHis) {
        String sql = """
                UPDATE ret_out_his
                SET dept_id = :deptId, balance = :balance, created_by = :createdBy, created_date = :createdDate,
                    deleted = :deleted, discount = :discount, paid = :paid, vou_date = :vouDate, ref_no = :refNo,
                    remark = :remark, session_id = :sessionId, updated_by = :updatedBy, updated_date = :updatedDate,
                    vou_total = :vouTotal, cur_code = :curCode, trader_code = :traderCode, loc_code = :locCode,
                    disc_p = :discP, intg_upd_status = :intgUpdStatus, mac_id = :macId, vou_lock = :vouLock,
                    project_no = :projectNo, print_count = :printCount, tax_amt = :taxAmt, tax_p = :taxP,
                    grand_total=:grandTotal, dept_code=:deptCode, src_acc=:srcAcc, cash_acc=:cashAcc, payable_acc=:payableAcc
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, retInHis);
    }

    private Mono<RetOutHis> executeUpdate(String sql, RetOutHis ri) {
        return client.sql(sql)
                .bind("vouNo", ri.getKey().getVouNo())
                .bind("compCode", ri.getKey().getCompCode())
                .bind("deptId", ri.getDeptId())
                .bind("balance", ri.getBalance())
                .bind("createdBy", ri.getCreatedBy())
                .bind("createdDate", ri.getCreatedDate())
                .bind("deleted", ri.getDeleted())
                .bind("discount", ri.getDiscount())
                .bind("paid", ri.getPaid())
                .bind("vouDate", ri.getVouDate())
                .bind("refNo", Parameters.in(R2dbcType.VARCHAR, ri.getRefNo()))
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, ri.getRemark()))
                .bind("sessionId", Parameters.in(R2dbcType.INTEGER, ri.getSessionId()))
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, ri.getUpdatedBy()))
                .bind("updatedDate", ri.getUpdatedDate())
                .bind("vouTotal", ri.getVouTotal())
                .bind("curCode", ri.getCurCode())
                .bind("traderCode", ri.getTraderCode())
                .bind("locCode", ri.getLocCode())
                .bind("discP", Parameters.in(R2dbcType.VARCHAR, ri.getDiscP()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, ri.getIntgUpdStatus()))
                .bind("macId", ri.getMacId())
                .bind("vouLock", Parameters.in(R2dbcType.VARCHAR, ri.getVouLock()))
                .bind("projectNo", Parameters.in(R2dbcType.VARCHAR, ri.getProjectNo()))
                .bind("printCount", Parameters.in(R2dbcType.VARCHAR, ri.getPrintCount()))
                .bind("taxAmt", Parameters.in(R2dbcType.VARCHAR, ri.getTaxAmt()))
                .bind("taxP", Parameters.in(R2dbcType.VARCHAR, ri.getTaxP()))
                .bind("grandTotal", Parameters.in(R2dbcType.DOUBLE, ri.getGrandTotal()))
                .bind("deptCode", Parameters.in(R2dbcType.VARCHAR, ri.getDeptCode()))
                .bind("srcAcc", Parameters.in(R2dbcType.VARCHAR, ri.getSrcAcc()))
                .bind("cashAcc", Parameters.in(R2dbcType.VARCHAR, ri.getCashAcc()))
                .bind("payableAcc", Parameters.in(R2dbcType.VARCHAR, ri.getPayableAcc()))
                .fetch()
                .rowsUpdated()
                .thenReturn(ri);
    }

    @Transactional
    public Mono<RetOutHisDetail> insert(RetOutHisDetail dto) {
        String sql = """
                INSERT INTO ret_out_his_detail (
                    vou_no, stock_code, qty, unit, price, amt, loc_code, unique_id, comp_code, dept_id,
                    weight, weight_unit, total_weight, wet, rice, bag
                ) VALUES (
                    :vouNo, :stockCode, :qty, :unit, :price, :amt, :locCode, :uniqueId, :compCode, :deptId,
                    :weight, :weightUnit, :totalWeight, :wet, :rice, :bag
                )
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<RetOutHisDetail> executeUpdate(String sql, RetOutHisDetail dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("stockCode", dto.getStockCode())
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnitCode())
                .bind("price", dto.getPrice())
                .bind("amt", dto.getAmount())
                .bind("locCode", dto.getLocCode())
                .bind("deptId", dto.getDeptId())
                .bind("weight", Parameters.in(R2dbcType.DOUBLE, dto.getWeight()))
                .bind("weightUnit", Parameters.in(R2dbcType.VARCHAR, dto.getWeightUnit()))
                .bind("totalWeight", Parameters.in(R2dbcType.DOUBLE, dto.getTotalWeight()))
                .bind("wet", Parameters.in(R2dbcType.VARCHAR, dto.getWet()))
                .bind("rice", Parameters.in(R2dbcType.VARCHAR, dto.getRice()))
                .bind("bag", Parameters.in(R2dbcType.VARCHAR, dto.getBag()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }


    public Mono<RetOutHis> findById(RetOutHisKey key) {
        String sql = """
                select *
                from ret_out_his
                where vou_no=:vouNo
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapToRow(row)).one();
    }


    public Mono<Boolean> delete(RetOutHisKey key) {
        return updateDeleteStatus(key, true);
    }

    private Mono<Boolean> updateDeleteStatus(RetOutHisKey key, boolean status) {
        String sql = """
                update ret_out_his
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


    public Mono<Boolean> restore(RetOutHisKey key) {
        return updateDeleteStatus(key, false);
    }


    public Flux<RetOutHis> unUploadVoucher(LocalDateTime syncDate) {
        String sql = """
                select *
                from ret_out_his
                where intg_upd_status is null
                and vou_date >= :syncDate
                """;
        return client.sql(sql)
                .bind("syncDate", syncDate)
                .map((row, rowMetadata) -> mapToRow(row)).all();
    }

    public RetOutHis mapToRow(Row row) {
        return RetOutHis.builder()
                .key(RetOutHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .balance(row.get("balance", Double.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .deleted(row.get("deleted", Boolean.class))
                .discount(row.get("discount", Double.class))
                .paid(row.get("paid", Double.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .refNo(row.get("ref_no", String.class))
                .remark(row.get("remark", String.class))
                .sessionId(row.get("session_id", Integer.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .vouTotal(row.get("vou_total", Double.class))
                .grandTotal(row.get("grand_total", Double.class))
                .curCode(row.get("cur_code", String.class))
                .traderCode(row.get("trader_code", String.class))
                .locCode(row.get("loc_code", String.class))
                .discP(row.get("disc_p", Double.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .macId(row.get("mac_id", Integer.class))
                .vouLock(row.get("vou_lock", Boolean.class))
                .projectNo(row.get("project_no", String.class))
                .printCount(row.get("print_count", Integer.class))
                .taxAmt(row.get("tax_amt", Double.class))
                .taxP(row.get("tax_p", Double.class))
                .build();
    }


    public Flux<RetOutHis> getHistory(ReportFilter filter) {
        String sql = """
                SELECT a.*, t.trader_name
                FROM (
                    SELECT vou_date, vou_no,comp_code, remark, created_by, paid, vou_total, deleted, trader_code, dept_id
                    FROM v_return_out
                    WHERE comp_code = :compCode
                    AND deleted = :deleted
                    AND cur_code = :curCode
                    AND (:deptId = 0 OR dept_id = :deptId)
                    AND DATE(vou_date) BETWEEN :fromDate AND :toDate
                    AND (vou_no = :vouNo OR '-' = :vouNo)
                    AND (remark LIKE CONCAT(:remark, '%') OR '-%' = CONCAT(:remark, '%'))
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
                .bind("compCode", filter.getCompCode())
                .bind("deleted", filter.isDeleted())
                .bind("curCode", Util1.isAll(filter.getCurCode()))
                .bind("deptId", filter.getDeptId())
                .bind("fromDate", Util1.isNull(filter.getFromDate(), "-"))
                .bind("toDate", Util1.isNull(filter.getToDate(), "-"))
                .bind("vouNo", Util1.isNull(filter.getVouNo(), "-"))
                .bind("remark", Util1.isNull(filter.getRemark(), "-"))
                .bind("traderCode", Util1.isNull(filter.getTraderCode(), "-"))
                .bind("userCode", Util1.isNull(filter.getUserCode(), "-"))
                .bind("stockCode", Util1.isNull(filter.getStockCode(), "-"))
                .bind("locCode", Util1.isNull(filter.getLocCode(), "-"))
                .bind("projectNo", Util1.isAll(filter.getProjectNo()))
                .map(row -> RetOutHis.builder()
                        .key(RetOutHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .traderName(row.get("trader_name", String.class))
                        .remark(row.get("remark", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .paid(row.get("paid", Double.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .build())
                .all();
    }


    public Flux<RetOutHisDetail> getRetOutDetail(String vouNo, String compCode) {
        String sql = """
                select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name
                from ret_out_his_detail op
                join location l on op.loc_code = l.loc_code
                and op.comp_code =l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code =s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and op.comp_code =rel.comp_code
                left join stock_type st  on s.stock_type_code = st.stock_type_code
                and op.comp_code =st.comp_code
                left join category cat on s.category_code = cat.cat_code
                and op.comp_code =cat.comp_code
                left join stock_brand sb on s.brand_code = sb.brand_code
                and op.comp_code =sb.comp_code
                where op.vou_no =:vouNo
                and op.comp_code =:compCode
                order by unique_id""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> RetOutHisDetail.builder()
                        .deptId(row.get("dept_id", Integer.class))
                        .key(RetOutKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .stockCode(row.get("stock_code", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amt", Double.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .unitCode(row.get("unit", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .catName(row.get("cat_name", String.class))
                        .groupName(row.get("stock_type_name", String.class))
                        .brandName(row.get("brand_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .build())
                .all();
    }

    public Mono<RetOutHis> generateForAcc(String vouNo, String compCode) {
        String sql = """
                select sh.vou_no,sh.comp_code,sh.vou_date,sh.trader_code,
                sh.cur_code,sh.remark,sh.deleted,sh.project_no,sh.reference,
                sh.vou_total,sh.grand_total,sh.disc_p,sh.discount,
                sh.tax_p,sh.tax_amt,sh.paid,sh.balance,sh.dept_id,
                ifnull(sh.dept_code,ifnull(l.dept_code,a.dep_code)) dept_code,
                ifnull(sh.src_acc,a.source_acc) src_acc,
                ifnull(sh.cash_acc,ifnull(l.cash_acc,a.pay_acc)) cash_acc,
                ifnull(sh.payable_acc,ifnull(t.account,a.bal_acc)) bal_acc,
                a.dis_acc,a.tax_acc,a.comm_acc
                from ret_out_his sh
                join location l
                on sh.loc_code = l.loc_code
                and sh.comp_code = l.comp_code
                join trader t on sh.trader_code= t.code
                and sh.comp_code = t.comp_code
                and sh.comp_code =l.comp_code
                join acc_setting a
                on sh.comp_code = a.comp_code
                and a.type ='RETURN_IN'
                where sh.comp_code =:compCode
                and sh.vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row) -> RetOutHis.builder()
                        .key(RetOutHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .traderCode(row.get("trader_code", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .refNo(row.get("reference", String.class))
                        .remark(row.get("remark", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .projectNo(row.get("project_no", String.class))
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
                        .srcAcc(row.get("src_acc", String.class))
                        .cashAcc(row.get("cash_acc", String.class))
                        .payableAcc(row.get("bal_acc", String.class))
                        .disAcc(row.get("dis_acc", String.class))
                        .taxAcc(row.get("tax_acc", String.class))
                        .build()).one();
    }
    public Flux<RetOutHisDetail> getReturnOutVoucher(String vouNo, String compCode) {
        String sql="""
                        SELECT stock_name, unit, qty, price, amt, t.trader_name, r.remark, DATE(vou_date) vou_date,
                               r.vou_total, r.paid, r.balance, r.vou_no
                        FROM v_return_out r
                        JOIN trader t ON r.trader_code = t.code AND r.comp_code = t.comp_code
                        WHERE r.comp_code = :compCode AND vou_no = :vouNo
                        ORDER BY unique_id
                        """;
        return client
                .sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row, metadata) -> RetOutHisDetail.builder()
                        .stockName(row.get("stock_name", String.class))
                        .unit(row.get("unit", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amt", Double.class))
                        .remark(row.get("remark", String.class))
                        .vouDate(row.get("vou_date", String.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .paid(row.get("paid", Double.class))
                        .vouBalance(row.get("balance", Double.class))
                        .vouNo(row.get("vou_no", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .build())
                .all();
    }
}
