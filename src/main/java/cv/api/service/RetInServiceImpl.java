/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.RetInHis;
import cv.api.entity.RetInHisDetail;
import cv.api.entity.RetInHisKey;
import cv.api.entity.RetInKey;
import cv.api.model.VReturnIn;
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

/**
 * @author Wai Yan
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class RetInServiceImpl implements RetInService {

    private final DatabaseClient client;
    private final VouNoService vouNoService;

    @Override
    public Mono<RetInHis> save(RetInHis dto) {
        return saveOrUpdate(dto).flatMap(ri -> deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode()).flatMap(delete -> {
            List<RetInHisDetail> list = dto.getListRD();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> Util1.getDouble(detail.getAmount()) != 0)
                        .concatMap(detail -> {
                            if (detail.getKey() == null) {
                                detail.setKey(RetInKey.builder().build());
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

    private Mono<RetInHis> saveOrUpdate(RetInHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "RETURN_IN", compCode, macId)
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
                delete from ret_in_his_detail
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
    public Mono<RetInHis> insert(RetInHis retInHis) {
        String sql = """
                INSERT INTO ret_in_his (
                    vou_no, comp_code, dept_id, balance, created_by, created_date, deleted, discount,
                    paid, vou_date, ref_no, remark, session_id, updated_by, updated_date, vou_total,
                    cur_code, trader_code, loc_code, disc_p, intg_upd_status, mac_id, vou_lock,
                    project_no, print_count, tax_amt, tax_p
                ) VALUES (
                    :vouNo, :compCode, :deptId, :balance, :createdBy, :createdDate, :deleted, :discount,
                    :paid, :vouDate, :refNo, :remark, :sessionId, :updatedBy, :updatedDate, :vouTotal,
                    :curCode, :traderCode, :locCode, :discP, :intgUpdStatus, :macId, :vouLock,
                    :projectNo, :printCount, :taxAmt, :taxP
                )
                """;
        return executeUpdate(sql, retInHis);
    }

    public Mono<RetInHis> update(RetInHis retInHis) {
        String sql = """
                UPDATE ret_in_his
                SET dept_id = :deptId, balance = :balance, created_by = :createdBy, created_date = :createdDate,
                    deleted = :deleted, discount = :discount, paid = :paid, vou_date = :vouDate, ref_no = :refNo,
                    remark = :remark, session_id = :sessionId, updated_by = :updatedBy, updated_date = :updatedDate,
                    vou_total = :vouTotal, cur_code = :curCode, trader_code = :traderCode, loc_code = :locCode,
                    disc_p = :discP, intg_upd_status = :intgUpdStatus, mac_id = :macId, vou_lock = :vouLock,
                    project_no = :projectNo, print_count = :printCount, tax_amt = :taxAmt, tax_p = :taxP
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, retInHis);
    }

    private Mono<RetInHis> executeUpdate(String sql, RetInHis ri) {
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
                .fetch()
                .rowsUpdated()
                .thenReturn(ri);
    }

    @Transactional
    public Mono<RetInHisDetail> insert(RetInHisDetail retInHisDetail) {
        String sql = """
                INSERT INTO ret_in_his_detail (
                    vou_no, stock_code, qty, unit, price, amt, loc_code, unique_id, comp_code, dept_id,
                    weight, weight_unit, total_weight, wet, rice, bag
                ) VALUES (
                    :vouNo, :stockCode, :qty, :unit, :price, :amt, :locCode, :uniqueId, :compCode, :deptId,
                    :weight, :weightUnit, :totalWeight, :wet, :rice, :bag
                )
                """;
        return executeUpdate(sql, retInHisDetail);
    }

    private Mono<RetInHisDetail> executeUpdate(String sql, RetInHisDetail dto) {
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

    @Override
    public Mono<RetInHis> findById(RetInHisKey key) {
        String sql = """
                select *
                from ret_in_his
                where vou_no=:vouNo
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapToRow(row)).one();
    }

    @Override
    public Mono<Boolean> delete(RetInHisKey key) {
        return updateDeleteStatus(true);
    }

    private Mono<Boolean> updateDeleteStatus(boolean status) {
        String sql = """
                update ret_in_his
                set deleted =:status,updated_date=:updatedDate
                where vou_no=:vouNo
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("status", status)
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    @Override
    public Mono<Boolean> restore(RetInHisKey key) {
        return updateDeleteStatus(false);
    }

    @Override
    public Flux<RetInHis> unUploadVoucher(LocalDateTime syncDate) {
        String sql = """
                select *
                from ret_in_his
                where intg_upd_status is null
                and vou_date >= :syncDate
                """;
        return client.sql(sql)
                .bind("syncDate", syncDate)
                .map((row, rowMetadata) -> mapToRow(row)).all();
    }

    public RetInHis mapToRow(Row row) {
        return RetInHis.builder()
                .key(RetInHisKey.builder()
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

    @Override
    public Flux<VReturnIn> getHistory(ReportFilter filter) {
        String sql = """
                SELECT a.*, t.trader_name
                FROM (
                    SELECT vou_date, vou_no, remark, created_by, paid, vou_total, deleted, trader_code, comp_code, dept_id
                    FROM v_return_in
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
                .map(row -> VReturnIn.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouNo(row.get("vou_no", String.class))
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


    @Override
    public Flux<RetInHisDetail> search(String vouNo, String compCode) {
        String sql = """
                select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name
                from ret_in_his_detail op
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
                .map(row -> RetInHisDetail.builder()
                        .deptId(row.get("dept_id", Integer.class))
                        .key(RetInKey.builder()
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

}
