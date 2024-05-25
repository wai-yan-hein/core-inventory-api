/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.OrderDetailKey;
import cv.api.entity.OrderHis;
import cv.api.entity.OrderHisDetail;
import cv.api.entity.OrderHisKey;
import cv.api.model.VDescription;
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
public class OrderHisService {

    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<OrderHis> saveOrder(OrderHis dto) {
        return operator.transactional(Mono.defer(() -> saveOrUpdate(dto)
                .flatMap(ri -> deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode())
                        .flatMap(delete -> {
                            List<OrderHisDetail> list = dto.getListSH();
                            if (list != null && !list.isEmpty()) {
                                return Flux.fromIterable(list)
                                        .filter(detail -> Util1.getDouble(detail.getQty()) != 0)
                                        .concatMap(detail -> {
                                            if (detail.getKey() == null) {
                                                detail.setKey(OrderDetailKey.builder().build());
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
                        }))));
    }

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from order_his_detail where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    private Mono<OrderHis> saveOrUpdate(OrderHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "ORDER", compCode, macId)
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

    @Transactional
    public Mono<OrderHis> insert(OrderHis dto) {
        String sql = """
                INSERT INTO order_his
                (vou_no, comp_code, dept_id, trader_code, saleman_code, vou_date, credit_term, cur_code,
                 remark, vou_total, created_date, created_by, deleted, vou_balance, updated_by, updated_date,
                 loc_code, mac_id, intg_upd_status, reference, vou_lock, project_no, order_status, post, ref_no, inv_update)
                VALUES
                (:vouNo, :compCode, :deptId, :traderCode, :salemanCode, :vouDate, :creditTerm, :curCode,
                 :remark, :vouTotal, :createdDate, :createdBy, :deleted, :vouBalance, :updatedBy, :updatedDate,
                 :locCode, :macId, :intgUpdStatus, :reference, :vouLock, :projectNo, :orderStatus, :post, :refNo, :invUpdate)
                """;
        return executeUpdate(sql, dto);
    }

    @Transactional
    public Mono<OrderHisDetail> insert(OrderHisDetail dto) {
        String sql = """
                INSERT INTO order_his_detail
                (vou_no, comp_code, unique_id, dept_id, stock_code, order_qty, qty, unit, price, amt,
                 loc_code, weight, weight_unit, design, size, heat_press_qty)
                VALUES
                (:vouNo, :compCode, :uniqueId, :deptId, :stockCode, :orderQty, :qty, :unit, :price, :amt,
                 :locCode, :weight, :weightUnit, :design, :size, :heatPressQty)
                """;

        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("deptId", dto.getDeptId())
                .bind("stockCode", dto.getStockCode())
                .bind("orderQty", Parameters.in(R2dbcType.DOUBLE, dto.getOrderQty()))
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnitCode())
                .bind("price", Parameters.in(R2dbcType.DOUBLE, dto.getPrice()))
                .bind("amt", Parameters.in(R2dbcType.DOUBLE, dto.getAmount()))
                .bind("locCode", dto.getLocCode())
                .bind("weight", Parameters.in(R2dbcType.DOUBLE, dto.getWeight()))
                .bind("weightUnit", Parameters.in(R2dbcType.VARCHAR, dto.getWeightUnit()))
                .bind("design", Parameters.in(R2dbcType.VARCHAR, dto.getDesign()))
                .bind("size", Parameters.in(R2dbcType.VARCHAR, dto.getSize()))
                .bind("heatPressQty", Parameters.in(R2dbcType.DOUBLE, dto.getHeatPressQty()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    @Transactional
    private Mono<OrderHis> update(OrderHis dto) {
        String sql = """
                UPDATE order_his
                SET comp_code = :compCode,
                    dept_id = :deptId,
                    trader_code = :traderCode,
                    saleman_code = :salemanCode,
                    vou_date = :vouDate,
                    credit_term = :creditTerm,
                    cur_code = :curCode,
                    remark = :remark,
                    vou_total = :vouTotal,
                    created_date = :createdDate,
                    created_by = :createdBy,
                    deleted = :deleted,
                    vou_balance = :vouBalance,
                    updated_by = :updatedBy,
                    updated_date = :updatedDate,
                    loc_code = :locCode,
                    mac_id = :macId,
                    intg_upd_status = :intgUpdStatus,
                    reference = :reference,
                    vou_lock = :vouLock,
                    project_no = :projectNo,
                    order_status = :orderStatus,
                    post = :post,
                    ref_no = :refNo,
                    inv_update = :invUpdate
                WHERE vou_no = :vouNo
                AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<OrderHis> executeUpdate(String sql, OrderHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("traderCode", dto.getTraderCode())
                .bind("salemanCode", Parameters.in(R2dbcType.VARCHAR, dto.getSaleManCode()))
                .bind("vouDate", dto.getVouDate())
                .bind("creditTerm", Parameters.in(R2dbcType.DATE, dto.getCreditTerm()))
                .bind("curCode", Parameters.in(R2dbcType.VARCHAR, dto.getCurCode()))
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("vouTotal", Util1.getDouble(dto.getVouTotal()))
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("vouBalance", Parameters.in(R2dbcType.DOUBLE, dto.getVouBalance()))
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("locCode", dto.getLocCode())
                .bind("macId", dto.getMacId())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("reference", Parameters.in(R2dbcType.VARCHAR, dto.getReference()))
                .bind("vouLock", Util1.getBoolean(dto.getVouLock()))
                .bind("projectNo", Parameters.in(R2dbcType.VARCHAR, dto.getProjectNo()))
                .bind("orderStatus", Parameters.in(R2dbcType.VARCHAR, dto.getOrderStatus()))
                .bind("post", Util1.getBoolean(dto.getPost()))
                .bind("refNo", Parameters.in(R2dbcType.VARCHAR, dto.getRefNo()))
                .bind("invUpdate", Util1.getBoolean(dto.getInvUpdate()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public OrderHis mapRow(Row row) {
        return OrderHis.builder()
                .key(OrderHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .traderCode(row.get("trader_code", String.class))
                .saleManCode(row.get("saleman_code", String.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .creditTerm(row.get("credit_term", LocalDateTime.class))
                .curCode(row.get("cur_code", String.class))
                .remark(row.get("remark", String.class))
                .vouTotal(row.get("vou_total", Double.class))
                .createdDate(row.get("created_date", java.time.LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .vouBalance(row.get("vou_balance", Double.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .locCode(row.get("loc_code", String.class))
                .macId(row.get("mac_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .reference(row.get("reference", String.class))
                .vouLock(row.get("vou_lock", Boolean.class))
                .projectNo(row.get("project_no", String.class))
                .orderStatus(row.get("order_status", String.class))
                .post(row.get("post", Boolean.class))
                .refNo(row.get("ref_no", String.class))
                .invUpdate(row.get("inv_update", Boolean.class))
                .build();
    }


    public Mono<OrderHis> findById(OrderHisKey key) {
        String sql = """
                select *
                from order_his
                where comp_code =:compCode
                and vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }


    public Mono<Boolean> delete(OrderHisKey key) {
        return updateDeleteStatus(key, true);
    }

    @Transactional
    private Mono<Boolean> updateDeleteStatus(OrderHisKey key, boolean status) {
        String sql = """
                update order_his
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


    public Mono<Boolean> restore(OrderHisKey key) {
        return updateDeleteStatus(key, false);
    }

    @Transactional
    public Mono<Boolean> updateOrder(OrderHisKey key, boolean post) {
        String sql = """
                update order_his
                set post = :post, inv_update = :invUpdate
                where vou_no = :vouNo
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .bind("post", post)
                .bind("invUpdate", !post)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<OrderHis> getOrderHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String reference = Util1.isNull(filter.getReference(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        String orderStatus = Util1.isNull(filter.getOrderStatus(), "-");
        String sql = """
                select a.*,t.trader_name,t.user_code,os.description order_status_name
                from (
                select  vou_no,vou_date,remark,reference,created_by,vou_total,deleted,trader_code,loc_code,
                comp_code,dept_id,order_status,post,inv_update
                from v_order s
                where comp_code = :compCode
                and (dept_id = :deptId or 0 =:deptId)
                and deleted = :deleted
                and date(vou_date) between :fromDate and :toDate
                and cur_code =:curCode
                and (vou_no =:vouNo or '-' =:vouNo)
                and (remark REGEXP :remark or '-' =:remark)
                and (reference REGEXP :reference or '-' = :reference)
                and (trader_code =:traderCode or '-' =:traderCode)
                and (created_by =:createdBy or '-' =:createdBy)
                and (stock_code =:stockCode or '-' =:stockCode)
                and (loc_code =:locCode or '-' =:locCode)
                and (project_no =:projectNo or '-' =:projectNo)
                and (order_status =:orderStatus or '-' =:orderStatus)
                group by vou_no
                )a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                left join order_status os on a.order_status = os.code
                and a.comp_code = os.comp_code
                order by vou_date desc""";
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("reference", reference)
                .bind("traderCode", traderCode)
                .bind("createdBy", userCode)
                .bind("stockCode", stockCode)
                .bind("locCode", locCode)
                .bind("projectNo", projectNo)
                .bind("orderStatus", orderStatus)
                .map((row, metadata) -> OrderHis.builder()
                        .key(OrderHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .remark(row.get("remark", String.class))
                        .reference(row.get("reference", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .traderCode(row.get("trader_code", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .orderStatus(row.get("order_status", String.class))
                        .post(row.get("post", Boolean.class))
                        .invUpdate(row.get("inv_update", Boolean.class))
                        .traderName(row.get("trader_name", String.class))
                        .userCode(row.get("user_code", String.class))
                        .orderStatusName(row.get("order_status_name", String.class))
                        .build())
                .all();
    }

    public Flux<OrderHisDetail> searchDetail(String vouNo, String compCode) {
        String sql = """
                select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name,t.trader_name
                from order_his_detail op
                join location l on op.loc_code = l.loc_code
                and op.comp_code = l.comp_code
                left join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and op.comp_code = rel.comp_code
                left join stock_type st  on s.stock_type_code = st.stock_type_code
                and op.comp_code = st.comp_code
                left join category cat on s.category_code = cat.cat_code
                and op.comp_code = cat.comp_code
                left join stock_brand sb on s.brand_code = sb.brand_code
                and op.comp_code = sb.comp_code
                left join grn g on op.comp_code = g.comp_code
                and g.deleted = false
                left join trader t on g.trader_code = t.code
                and g.comp_code = t.comp_code
                where op.vou_no = :vouNo
                and op.comp_code = :compCode
                order by unique_id""";

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, metadata) -> OrderHisDetail.builder()
                        .key(OrderDetailKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .vouNo(row.get("vou_no", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .qty(row.get("qty", Double.class))
                        .orderQty(row.get("order_qty", Double.class))
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
                        .traderName(row.get("trader_name", String.class))
                        .design(row.get("design", String.class))
                        .size(row.get("size", String.class))
                        .heatPressQty(row.get("heat_press_qty", Double.class))
                        .build())
                .all();
    }

    public Flux<OrderHisDetail> getOrderVoucher(String vouNo, String compCode) {
        String sql = """
                select t.trader_name,t.rfid,t.phone,t.address,v.remark,v.vou_no,v.vou_date,v.stock_name,
                v.order_qty,v.qty,v.weight,v.weight_unit,v.price,v.unit,v.amt,v.design,v.size,t.user_code t_user_code,t.phone,t.address,
                l.loc_name,v.created_by,v.comp_code,os.description,sm.saleman_name
                from v_order v join trader t
                on v.trader_code = t.code
                and v.comp_code = t.comp_code
                join location l on v.loc_code = l.loc_code
                and  v.comp_code = l.comp_code
                join order_status os on v.order_status = os.code
                and v.comp_code = os.comp_code
                left join sale_man sm on v.saleman_code = sm.saleman_code
                and v.comp_code = sm.comp_code
                where v.vou_no = :vouNo
                and v.comp_code = :compCode""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> {
                    OrderHisDetail order = OrderHisDetail.builder().build();
                    order.setKey(OrderDetailKey.builder()
                            .vouNo(row.get("vou_no", String.class))
                            .compCode(row.get("comp_code", String.class))
                            .build());
                    order.setVouNo(row.get("vou_no", String.class));
                    String remark = row.get("remark", String.class);
                    order.setTraderCode(row.get("t_user_code", String.class));
                    order.setTraderName(row.get("trader_name", String.class));
                    order.setRemark(remark);
                    order.setPhoneNo(row.get("phone", String.class));
                    order.setAddress(row.get("address", String.class));
                    order.setRfId(row.get("rfid", String.class));
                    order.setVouDateStr(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    order.setStockName(row.get("stock_name", String.class));
                    order.setOrderQty(row.get("order_qty", Double.class));
                    order.setQty(row.get("qty", Double.class));
                    order.setPrice(row.get("price", Double.class));
                    order.setAmount(row.get("amt", Double.class));
                    order.setUnitCode(row.get("unit", String.class));
                    order.setLocationName(row.get("loc_name", String.class));
                    order.setCreatedBy(row.get("created_by", String.class));
                    double weight = Util1.getDouble(row.get("weight", Double.class));
                    if (weight > 0) {
                        order.setWeight(weight);
                        order.setWeightUnit(row.get("weight_unit", String.class));
                    }
                    order.setOrderStatusName(row.get("description", String.class));
                    order.setSaleManName(row.get("saleman_name", String.class));
                    return order;
                }).all();
    }

    public Flux<OrderHis> getOrderSummaryByDepartment(String fromDate, String toDate, String compCode) {
        String sql = """
                select sum(vou_total) vou_total,cur_code,dept_id,count(*) vou_count
                from order_his
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and comp_code = :compCode
                group by dept_id,cur_code""";

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .map(row -> {
                    OrderHis s = OrderHis.builder().build();
                    s.setVouTotal(row.get("vou_total", Double.class));
                    s.setDeptId(row.get("dept_id", Integer.class));
                    s.setVouCount(row.get("vou_count", Integer.class));
                    return s;
                })
                .all();
    }

    public Flux<VDescription> getDesign(String str, String compCode) {
        String sql = """
                select distinct design
                from order_his_detail
                where comp_code =:compCode
                and design regexp :str
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("str", str)
                .map((row) -> VDescription.builder()
                        .description(row.get("design", String.class))
                        .build()).all();
    }

    public Flux<VDescription> getSize(String str, String compCode) {
        String sql = """
                select distinct size
                from order_his_detail
                where comp_code =:compCode
                and size regexp :str
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("str", str)
                .map((row) -> VDescription.builder()
                        .description(row.get("design", String.class))
                        .build()).all();
    }

    public Flux<OrderHis> searchByRefNo(String refNo, String compCode) {
        String sql = """
                select o.vou_no,o.vou_date,o.ref_no,o.trader_code,t.trader_name
                from order_his o join trader t on o.trader_code = t.code
                and o.comp_code = t.comp_code
                where o.comp_code =:compCode
                and o.ref_no regexp :refNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("refNo", refNo)
                .map((row) -> OrderHis.builder()
                        .key(OrderHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .build())
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .refNo(row.get("ref_no", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .build()).all();
    }
}
