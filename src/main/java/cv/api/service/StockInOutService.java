/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.dto.StockInOutDetailDto;
import cv.api.dto.StockInOutKeyDto;
import cv.api.entity.StockIOKey;
import cv.api.entity.StockInOut;
import cv.api.entity.StockInOutDetail;
import cv.api.entity.StockInOutKey;
import cv.api.exception.ResponseUtil;
import cv.api.model.VStockIO;
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

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class StockInOutService {
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<StockInOut> saveStockIO(StockInOut dto) {
        return isValid(dto).flatMap(his -> operator.transactional(Mono.defer(() -> saveOrUpdate(his)
                .flatMap(ri -> deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode())
                        .flatMap(delete -> {
                            List<StockInOutDetail> list = his.getListSH();
                            return Flux.fromIterable(list)
                                    .filter(detail -> !Util1.isNullOrEmpty(detail.getStockCode()))
                                    .concatMap(detail -> {
                                        if (detail.getKey() == null) {
                                            detail.setKey(StockInOutKey.builder().build());
                                        }
                                        int uniqueId = list.indexOf(detail) + 1;
                                        detail.getKey().setUniqueId(uniqueId);
                                        detail.getKey().setVouNo(ri.getKey().getVouNo());
                                        detail.getKey().setCompCode(ri.getKey().getCompCode());
                                        detail.setDeptId(ri.getDeptId());
                                        return insert(detail);
                                    })
                                    .then(Mono.just(ri));
                        })))));
    }

    private Mono<StockInOut> isValid(StockInOut sh) {
        List<StockInOutDetail> list = Util1.nullToEmpty(sh.getListSH());
        list.removeIf(t -> Util1.isNullOrEmpty(t.getStockCode()));
        if (list.isEmpty()) {
            return ResponseUtil.createBadRequest("Detail is null/empty");
        } else if (Util1.isNullOrEmpty(sh.getDeptId())) {
            return ResponseUtil.createBadRequest("deptId is null from mac id : " + sh.getMacId());
        } else if (Util1.isNullOrEmpty(sh.getTraderCode())) {
            return ResponseUtil.createBadRequest("Trader is null");
        } else if (Util1.isNullOrEmpty(sh.getVouDate())) {
            return ResponseUtil.createBadRequest("Voucher Date is null");
        }
        return Mono.just(sh);
    }


    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from stock_in_out_detail where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    private Mono<StockInOut> saveOrUpdate(StockInOut dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            return vouNoService.getVouNo(deptId, "STOCKIO", compCode, macId)
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


    public Mono<StockInOut> findById(StockIOKey key) {
        String sql = """
                select *
                from stock_in_out
                where vou_no =:vouNo
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public StockInOut mapRow(Row row) {
        return StockInOut.builder()
                .key(StockIOKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .remark(row.get("remark", String.class))
                .description(row.get("description", String.class))
                .macId(row.get("mac_id", Integer.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .vouStatusCode(row.get("vou_status", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .labourGroupCode(row.get("labour_group_code", String.class))
                .jobCode(row.get("job_code", String.class))
                .receivedName(row.get("received_name", String.class))
                .receivedPhoneNo(row.get("received_phone", String.class))
                .carNo(row.get("car_no", String.class))
                .traderCode(row.get("trader_code", String.class))
                .printCount(row.get("print_count", Integer.class))
                .build();
    }

    public Mono<Boolean> delete(StockIOKey key) {
        return updateDeleteStatus(key, true);
    }

    public Mono<Boolean> restore(StockIOKey key) {
        return updateDeleteStatus(key, false);
    }

    private Mono<Boolean> updateDeleteStatus(StockIOKey key, boolean status) {
        String sql = """
                update stock_in_out
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


    public Flux<VStockIO> getStockIOHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouStatus = Util1.isNull(filter.getVouStatus(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String desp = Util1.isNull(filter.getDescription(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String jobNo = Util1.isNull(filter.getJobNo(), "-");
        String sql = """
                select a.*,v.description vou_status_name
                from (
                select vou_date,vou_no,description,remark,vou_status,created_by,
                deleted,comp_code,dept_id,sum(ifnull(in_qty,0)) in_qty,sum(ifnull(out_qty,0)) out_qty,
                sum(ifnull(in_bag,0)) in_bag,sum(ifnull(out_bag,0)) out_bag
                from v_stock_io
                where comp_code = :compCode
                and deleted = :deleted
                and (dept_id = :deptId or 0 = :deptId)
                and date(vou_date) between :fromDate and :toDate
                and (vou_no = :vouNo or '-' = :vouNo)
                and (remark REGEXP :remark or '-' = :remark)
                and (description REGEXP :desp or '-' = :desp)
                and (vou_status = :vouStatus or '-' = :vouStatus)
                and (created_by = :userCode or '-' = :userCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (loc_code = :locCode or '-' = :locCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                and (job_code = :jobNo or '-' = :jobNo)
                group by vou_no)a
                join vou_status v on a.vou_status = v.code
                and a.comp_code = v.comp_code
                order by vou_date desc""";

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("desp", desp)
                .bind("vouStatus", vouStatus)
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("locCode", locCode)
                .bind("traderCode", traderCode)
                .bind("jobNo", jobNo)
                .map((row) -> VStockIO.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDateTime.class), "dd/MM/yyyy"))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouNo(row.get("vou_no", String.class))
                        .description(row.get("description", String.class))
                        .remark(row.get("remark", String.class))
                        .vouTypeName(row.get("vou_status_name", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .inQty(row.get("in_qty", Double.class))
                        .outQty(row.get("out_qty", Double.class))
                        .inBag(row.get("in_bag", Double.class))
                        .outBag(row.get("out_bag", Double.class))
                        .build()
                ).all();
    }

    public Mono<StockInOutDetail> insert(StockInOutDetail dto) {
        String sql = """
                INSERT INTO stock_in_out_detail (vou_no, unique_id, comp_code, dept_id, stock_code, loc_code, in_qty,
                in_unit, out_qty, out_unit, cost_price, weight, weight_unit, total_weight, wet, rice, in_bag,
                out_bag, amount)
                VALUES
                (:vouNo, :uniqueId, :compCode, :deptId, :stockCode, :locCode, :inQty, :inUnit, :outQty, :outUnit,
                :costPrice, :weight, :weightUnit, :totalWeight, :wet, :rice, :inBag, :outBag, :amount)
                """;
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("stockCode", dto.getStockCode())
                .bind("locCode", dto.getLocCode())
                .bind("inQty", Parameters.in(R2dbcType.DOUBLE, dto.getInQty()))
                .bind("inUnit", Parameters.in(R2dbcType.VARCHAR, dto.getInUnitCode()))
                .bind("outQty", Parameters.in(R2dbcType.DOUBLE, dto.getOutQty()))
                .bind("outUnit", Parameters.in(R2dbcType.VARCHAR, dto.getOutUnitCode()))
                .bind("costPrice", Util1.getDouble(dto.getCostPrice()))
                .bind("weight", Parameters.in(R2dbcType.DOUBLE, dto.getWeight()))
                .bind("weightUnit", Parameters.in(R2dbcType.VARCHAR, dto.getWeightUnit()))
                .bind("totalWeight", Parameters.in(R2dbcType.DOUBLE, dto.getTotalWeight()))
                .bind("wet", Parameters.in(R2dbcType.DOUBLE, dto.getWet()))
                .bind("rice", Parameters.in(R2dbcType.DOUBLE, dto.getRice()))
                .bind("inBag", Parameters.in(R2dbcType.DOUBLE, dto.getInBag()))
                .bind("outBag", Parameters.in(R2dbcType.DOUBLE, dto.getOutBag()))
                .bind("amount", Parameters.in(R2dbcType.DOUBLE, dto.getAmount()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<StockInOut> insert(StockInOut dto) {
        String sql = """
                INSERT INTO stock_in_out (vou_no, vou_date, remark, description, comp_code, mac_id, created_date,
                created_by, updated_date, updated_by, deleted, vou_status, dept_id, intg_upd_status, labour_group_code,
                job_code, received_name, received_phone, car_no, trader_code, print_count)
                VALUES
                (:vouNo, :vouDate, :remark, :description, :compCode, :macId, :createdDate, :createdBy,
                :updatedDate, :updatedBy, :deleted, :vouStatus, :deptId, :intgUpdStatus, :labourGroupCode,
                :jobCode, :receivedName, :receivedPhone, :carNo, :traderCode, :printCount)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<StockInOut> update(StockInOut dto) {
        String sql = """
                UPDATE stock_in_out
                SET vou_date = :vouDate,
                    remark = :remark,
                    description = :description,
                    comp_code = :compCode,
                    mac_id = :macId,
                    created_date = :createdDate,
                    created_by = :createdBy,
                    updated_date = :updatedDate,
                    updated_by = :updatedBy,
                    deleted = :deleted,
                    vou_status = :vouStatus,
                    dept_id = :deptId,
                    intg_upd_status = :intgUpdStatus,
                    labour_group_code = :labourGroupCode,
                    job_code = :jobCode,
                    received_name = :receivedName,
                    received_phone = :receivedPhone,
                    car_no = :carNo,
                    trader_code = :traderCode,
                    print_count = :printCount
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<StockInOut> executeUpdate(String sql, StockInOut dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("vouDate", dto.getVouDate())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("description", Parameters.in(R2dbcType.VARCHAR, dto.getDescription()))
                .bind("macId", dto.getMacId())
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedDate", LocalDateTime.now())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("vouStatus", dto.getVouStatusCode())
                .bind("deptId", dto.getDeptId())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("labourGroupCode", Parameters.in(R2dbcType.VARCHAR, dto.getLabourGroupCode()))
                .bind("jobCode", Parameters.in(R2dbcType.VARCHAR, dto.getJobCode()))
                .bind("receivedName", Parameters.in(R2dbcType.VARCHAR, dto.getReceivedName()))
                .bind("receivedPhone", Parameters.in(R2dbcType.VARCHAR, dto.getReceivedPhoneNo()))
                .bind("carNo", Parameters.in(R2dbcType.VARCHAR, dto.getCarNo()))
                .bind("traderCode", Parameters.in(R2dbcType.VARCHAR, dto.getTraderCode()))
                .bind("printCount", Parameters.in(R2dbcType.INTEGER, dto.getPrintCount()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Flux<StockInOutDetailDto> search(String vouNo, String compCode) {
        String sql = """
                select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,
                rel.rel_name,l.loc_name
                from stock_in_out_detail op
                join location l on op.loc_code = l.loc_code
                and op.comp_code = l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and op.comp_code = rel.comp_code
                left join stock_type st  on s.stock_type_code = st.stock_type_code
                and op.comp_code = st.comp_code
                left join category cat on s.category_code = cat.cat_code
                and op.comp_code = cat.comp_code
                left join stock_brand sb on s.brand_code = sb.brand_code
                and op.comp_code = sb.comp_code
                where op.vou_no =:vouNo
                and op.comp_code =:compCode
                order by unique_id""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row) -> StockInOutDetailDto.builder()
                        .key(StockInOutKeyDto.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .inQty(row.get("in_qty", Double.class))
                        .inUnitCode(row.get("in_unit", String.class))
                        .outQty(row.get("out_qty", Double.class))
                        .outUnitCode(row.get("out_unit", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .catName(row.get("cat_name", String.class))
                        .groupName(row.get("stock_type_name", String.class))
                        .brandName(row.get("brand_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .costPrice(row.get("cost_price", Double.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .wet(row.get("wet", Double.class))
                        .rice(row.get("rice", Double.class))
                        .inBag(row.get("in_bag", Double.class))
                        .outBag(row.get("out_bag", Double.class))
                        .amount(row.get("amount", Double.class))
                        .build()).all();
    }

    public Flux<StockInOutDetailDto> searchByJob(String jobCode, String compCode) {
        String sql = """
                select a.*,round( a.tot_weight / (in_tot_qty + out_tot_qty),3) avg_weight,
                round( a.ttl_amt / (in_tot_qty + out_tot_qty),3) price
                from (
                select sum(op.total_weight) as tot_weight, sum(iszero(op.in_qty,op.in_bag)) as in_tot_qty,
                sum(iszero(op.out_qty,op.out_bag)) as out_tot_qty,
                sum(amount) ttl_amt,
                op.*,s.user_code,s.stock_name,st.finished_group
                from stock_in_out_detail op
                join stock_in_out l on op.vou_no = l.vou_no
                and op.comp_code = l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                where l.job_code =:jobCode
                and l.comp_code =:compCode
                and l.deleted = false
                group by op.stock_code,weight_unit,in_unit,out_unit)a
                order by a.finished_group desc,a.vou_no,a.unique_id
                """;
        return client.sql(sql)
                .bind("jobCode", jobCode)
                .bind("compCode", compCode)
                .map((row) -> StockInOutDetailDto.builder()
                        .key(StockInOutKeyDto.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .inQty(row.get("in_tot_qty", Double.class))
                        .inUnitCode(row.get("in_unit", String.class))
                        .outQty(row.get("out_tot_qty", Double.class))
                        .outUnitCode(row.get("out_unit", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .costPrice(row.get("price", Double.class))
                        .weight(row.get("avg_weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .totalWeight(row.get("tot_weight", Double.class))
                        .build()).all();

    }
}
