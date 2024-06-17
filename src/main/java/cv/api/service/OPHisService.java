package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.OPHis;
import cv.api.entity.OPHisDetail;
import cv.api.entity.OPHisDetailKey;
import cv.api.entity.OPHisKey;
import cv.api.exception.ResponseUtil;
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

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OPHisService {
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<OPHis> save(OPHis dto) {
        return isValid(dto).flatMap(his -> operator.transactional(Mono.defer(() -> saveOrUpdate(his)
                .flatMap(ri -> deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode())
                        .flatMap(delete -> {
                            List<OPHisDetail> list = his.getDetailList();
                            return Flux.fromIterable(list)
                                    .filter(detail -> !Util1.isNullOrEmpty(detail.getStockCode()))
                                    .concatMap(detail -> {
                                        if (detail.getKey() == null) {
                                            detail.setKey(OPHisDetailKey.builder().build());
                                        }
                                        int uniqueId = list.indexOf(detail) + 1;
                                        detail.getKey().setUniqueId(uniqueId);
                                        detail.getKey().setVouNo(ri.getKey().getVouNo());
                                        detail.getKey().setCompCode(ri.getKey().getCompCode());
                                        detail.setDeptId(ri.getDeptId());
                                        detail.setLocCode(ri.getLocCode());
                                        return insert(detail);
                                    })
                                    .then(Mono.just(ri));
                        })))));
    }

    private Mono<OPHis> isValid(OPHis sh) {
        List<OPHisDetail> list = Util1.nullToEmpty(sh.getDetailList());
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


    private Mono<OPHis> saveOrUpdate(OPHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        if (Util1.isNullOrEmpty(vouNo)) {
            return vouNoService.getVouNo(deptId, "OPENING", compCode, macId)
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
                delete from op_his_detail
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


    public Mono<OPHis> findByCode(OPHisKey key) {
        String sql = """
                select *
                from op_his
                where comp_code =:compCode
                and vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    private OPHis mapRow(Row row) {
        return OPHis.builder()
                .key(OPHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .vouDate(row.get("op_date", LocalDate.class))
                .remark(row.get("remark", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .macId(row.get("mac_id", Integer.class))
                .deleted(row.get("deleted", Boolean.class))
                .locCode(row.get("loc_code", String.class))
                .opAmt(row.get("op_amt", Double.class))
                .curCode(row.get("cur_code", String.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .traderCode(row.get("trader_code", String.class))
                .tranSource(row.get("tran_source", Integer.class))
                .build();
    }


    public Mono<Boolean> delete(OPHisKey key) {
        return updateDeleteStatus(key, true);
    }

    public Mono<Boolean> restore(OPHisKey key) {
        return updateDeleteStatus(key, false);
    }

    private Mono<Boolean> updateDeleteStatus(OPHisKey key, boolean status) {
        String sql = """
                update op_his
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

    public Mono<String> getOpeningDateByLocation(String compCode, String locCode) {
        String sql = """
                select ifnull(max(op_date),'1998-10-07') op_date
                from op_his
                where deleted = false
                and comp_code =:compCode
                and (loc_code =:locCode or '-'=:locCode)
                and (tran_source=1 or tran_source=3)
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("locCode", locCode)
                .map((row) -> row.get("op_date", String.class))
                .one();
    }

    public Mono<String> getOpeningDate(String compCode, int tranSource) {
        String sql = """
                select
                case
                when exists (
                            select 1 from op_his
                            where deleted = false
                            and comp_code =:compCode
                            and tran_source =:tranSource
                        ) then op_date
                        else max(op_date)
                    end as op_date
                from op_his
                where deleted = false
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("tranSource", tranSource)
                .map((row) -> row.get("op_date", String.class))
                .one()
                .switchIfEmpty(Mono.defer(() -> Mono.just("1998-10-07")));
    }

    private Mono<OPHis> insert(OPHis dto) {
        String sql = """
                INSERT INTO op_his (vou_no, comp_code, dept_id, op_date, remark, created_by, created_date,
                updated_date, updated_by, mac_id, deleted, loc_code, op_amt, cur_code, intg_upd_status,
                trader_code, tran_source)
                VALUES (:vouNo, :compCode, :deptId, :opDate, :remark, :createdBy, :createdDate,
                :updatedDate, :updatedBy, :macId, :deleted, :locCode, :opAmt, :curCode, :intgUpdStatus,
                :traderCode, :tranSource)
                """;
        return executeUpdate(dto, sql);
    }

    private Mono<OPHis> update(OPHis dto) {
        String sql = """
                UPDATE op_his
                SET dept_id = :deptId, op_date = :opDate, remark = :remark, created_by = :createdBy,
                created_date = :createdDate, updated_date = :updatedDate, updated_by = :updatedBy,
                mac_id = :macId, deleted = :deleted, loc_code = :locCode, op_amt = :opAmt,
                cur_code = :curCode, intg_upd_status = :intgUpdStatus, trader_code = :traderCode,
                tran_source = :tranSource
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(dto, sql);
    }


    public Mono<OPHis> executeUpdate(OPHis dto, String sql) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("opDate", dto.getVouDate())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedDate", dto.getUpdatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("macId", dto.getMacId())
                .bind("deleted", dto.getDeleted())
                .bind("locCode", dto.getLocCode())
                .bind("opAmt", dto.getOpAmt())
                .bind("curCode", dto.getCurCode())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("traderCode", Parameters.in(R2dbcType.VARCHAR, dto.getTraderCode()))
                .bind("tranSource", Parameters.in(R2dbcType.VARCHAR, dto.getTranSource()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<OPHisDetail> insert(OPHisDetail dto) {
        String sql = """
                INSERT INTO op_his_detail (vou_no, unique_id, comp_code, stock_code, qty, price, amount, loc_code, unit, dept_id, weight, weight_unit, total_weight, wet, rice, bag)
                VALUES (:vouNo, :uniqueId, :compCode, :stockCode, :qty, :price, :amount, :locCode, :unit, :deptId, :weight, :weightUnit, :totalWeight, :wet, :rice, :bag)
                """;
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("stockCode", dto.getStockCode())
                .bind("qty", dto.getQty())
                .bind("price", Parameters.in(R2dbcType.VARCHAR, dto.getPrice()))
                .bind("amount", Parameters.in(R2dbcType.VARCHAR, dto.getAmount()))
                .bind("locCode", dto.getLocCode())
                .bind("unit", Parameters.in(R2dbcType.VARCHAR, dto.getUnitCode()))
                .bind("deptId", dto.getDeptId())
                .bind("weight", Parameters.in(R2dbcType.DOUBLE, dto.getWeight()))
                .bind("weightUnit", Parameters.in(R2dbcType.DOUBLE, dto.getWeightUnit()))
                .bind("totalWeight", Parameters.in(R2dbcType.DOUBLE, dto.getTotalWeight()))
                .bind("wet", Parameters.in(R2dbcType.DOUBLE, dto.getWet()))
                .bind("rice", Parameters.in(R2dbcType.DOUBLE, dto.getRice()))
                .bind("bag", Parameters.in(R2dbcType.DOUBLE, dto.getBag()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Flux<OPHisDetail> getOpeningDetail(String vouNo, String compCode) {
        String sql = """
                select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name
                from op_his_detail op
                join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and op.comp_code = rel.comp_code
                left join stock_type st on s.stock_type_code = st.stock_type_code
                and op.comp_code = st.comp_code
                left join category cat on s.category_code = cat.cat_code
                and op.comp_code = cat.comp_code
                left join stock_brand sb on s.brand_code = sb.brand_code
                and op.comp_code = sb.comp_code
                where op.vou_no = :vouNo
                and op.comp_code = :compCode
                order by unique_id
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row) -> OPHisDetail.builder()
                        .key(OPHisDetailKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amount", Double.class))
                        .locCode(row.get("loc_code", String.class))
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
                        .wet(row.get("wet", Double.class))
                        .rice(row.get("rice", Double.class))
                        .bag(row.get("bag", Double.class))
                        .build())
                .all();
    }

    public Flux<OPHis> getOpeningHistory(ReportFilter f) {
        String fromDate = Util1.isNull(f.getFromDate(), "-");
        String toDate = Util1.isNull(f.getToDate(), "-");
        String vouNo = Util1.isNull(f.getVouNo(), "-");
        String userCode = Util1.isNull(f.getUserCode(), "-");
        String compCode = Util1.isNull(f.getCompCode(), "-");
        String stockCode = Util1.isNull(f.getStockCode(), "-");
        String remark = Util1.isNull(f.getRemark(), "-");
        String locCode = Util1.isNull(f.getLocCode(), "-");
        Integer deptId = f.getDeptId();
        String curCode = Util1.isAll(f.getCurCode());
        boolean deleted = f.isDeleted();
        int type = Integer.parseInt(f.getTranSource());
        String traderCode = String.valueOf(f.getTraderCode());
        String sql = """
                select sum(v.qty) qty,sum(v.bag) bag,sum(v.amount) amount,v.op_date,v.vou_no,v.remark,v.created_by,v.deleted,l.loc_name,v.comp_code,v.dept_id\s
                from v_opening v join location l
                on v.loc_code = l.loc_code
                and v.comp_code = l.comp_code
                where v.comp_code = :compCode
                and v.cur_code = :curCode
                and v.deleted = :deleted
                and (v.dept_id = :deptId or 0 = :deptId)
                and date(v.op_date) between :fromDate and :toDate
                and (v.vou_no = :vouNo or '-' = :vouNo)
                and (v.remark REGEXP :remark or '-' = :remark)
                and (v.created_by = :userCode or '-'=:userCode)
                and (v.stock_code = :stockCode or '-' = :stockCode)
                and (v.loc_code = :locCode or '-' = :locCode)
                and (v.trader_code = :traderCode or '-' = :traderCode)
                and v.tran_source = :type
                group by v.vou_no
                order by v.op_date desc,v.vou_no desc
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("deleted", deleted)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("locCode", locCode)
                .bind("type", type)
                .bind("traderCode", traderCode)
                .map((row) -> OPHis.builder()
                        .key(OPHisKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .vouNo(row.get("vou_no", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .qty(row.get("qty", Double.class))
                        .bag(row.get("bag", Double.class))
                        .opAmt(row.get("amount", Double.class))
                        .vouDateStr(Util1.toDateStr(row.get("op_date", LocalDate.class), "dd/MM/yyyy"))
                        .remark(row.get("remark", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .locName(row.get("loc_name", String.class))
                        .build())
                .all();
    }


}
