/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.entity.SaleDetailKey;
import cv.api.entity.SaleHisDetail;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author wai yan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SaleDetailService {
    private final DatabaseClient client;


    public Mono<SaleHisDetail> save(SaleHisDetail sh) {
        String sql = """
                    INSERT INTO sale_his_detail
                    (vou_no,comp_code,unique_id, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, dept_id, batch_no, weight, weight_unit, design, size, std_weight, total_weight, org_price, weight_loss, wet, rice, bag)
                    VALUES
                    (:vouNo,:compCode,:uniqueId, :stockCode, :expireDate, :qty, :saleUnit, :salePrice, :saleAmt, :locCode,   :deptId, :batchNo, :weight, :weightUnit, :design, :size, :stdWeight, :totalWeight, :orgPrice, :weightLoss, :wet, :rice, :bag)
                """;

        return client.sql(sql)
                .bind("vouNo", sh.getKey().getVouNo())
                .bind("compCode", sh.getKey().getCompCode())
                .bind("uniqueId", sh.getKey().getUniqueId())
                .bind("stockCode", sh.getStockCode())
                .bind("expireDate", Parameters.in(R2dbcType.DATE, sh.getExpDate()))
                .bind("qty", sh.getQty())
                .bind("saleUnit", sh.getUnitCode())
                .bind("salePrice", sh.getPrice())
                .bind("saleAmt", sh.getAmount())
                .bind("locCode", sh.getLocCode())
                .bind("deptId", sh.getDeptId())
                .bind("batchNo", Parameters.in(R2dbcType.VARCHAR, sh.getBatchNo()))
                .bind("weight", Parameters.in(R2dbcType.DOUBLE, sh.getWeight()))
                .bind("weightUnit", Parameters.in(R2dbcType.VARCHAR, sh.getWeightUnit()))
                .bind("design", Parameters.in(R2dbcType.VARCHAR, sh.getDesign()))
                .bind("size", Parameters.in(R2dbcType.VARCHAR, sh.getSize()))
                .bind("stdWeight", Parameters.in(R2dbcType.DOUBLE, sh.getStdWeight()))
                .bind("totalWeight", Parameters.in(R2dbcType.DOUBLE, sh.getTotalWeight()))
                .bind("orgPrice", Parameters.in(R2dbcType.DOUBLE, sh.getOrgPrice()))
                .bind("weightLoss", Parameters.in(R2dbcType.DOUBLE, sh.getWeightLoss()))
                .bind("wet", Parameters.in(R2dbcType.DOUBLE, sh.getWet()))
                .bind("rice", Parameters.in(R2dbcType.DOUBLE, sh.getRice()))
                .bind("bag", Parameters.in(R2dbcType.DOUBLE, sh.getBag()))
                .fetch()
                .rowsUpdated()
                .thenReturn(sh);

    }


    public Flux<SaleHisDetail> search(String vouNo, String compCode) {
        String sql = """
                    SELECT op.*, s.user_code, s.stock_name,s.calculate, cat.cat_name, st.stock_type_name, sb.brand_name, rel.rel_name, l.loc_name, t.trader_name
                    FROM sale_his_detail op
                    JOIN location l ON op.loc_code = l.loc_code AND op.comp_code = l.comp_code
                    left JOIN stock s ON op.stock_code = s.stock_code AND op.comp_code = s.comp_code
                    LEFT JOIN unit_relation rel ON s.rel_code = rel.rel_code AND op.comp_code = rel.comp_code
                    LEFT JOIN stock_type st ON s.stock_type_code = st.stock_type_code AND op.comp_code = st.comp_code
                    LEFT JOIN category cat ON s.category_code = cat.cat_code AND op.comp_code = cat.comp_code
                    LEFT JOIN stock_brand sb ON s.brand_code = sb.brand_code AND op.comp_code = sb.comp_code
                    LEFT JOIN grn g ON op.batch_no = g.batch_no AND op.comp_code = g.comp_code AND g.deleted = 0
                    LEFT JOIN trader t ON g.trader_code = t.code AND g.comp_code = t.comp_code
                    WHERE op.vou_no = :vouNo AND op.comp_code = :compCode
                    ORDER BY unique_id
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, metadata) -> SaleHisDetail.builder()
                        .key(SaleDetailKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .vouNo(row.get("vou_no", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .stdWeight(row.get("std_weight", Double.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("sale_price", Double.class))
                        .amount(row.get("sale_amt", Double.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .unitCode(row.get("sale_unit", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .calculate(row.get("calculate", Boolean.class))
                        .catName(row.get("cat_name", String.class))
                        .groupName(row.get("stock_type_name", String.class))
                        .brandName(row.get("brand_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .batchNo(row.get("batch_no", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .orgPrice(row.get("org_price", Double.class))
                        .weightLoss(row.get("weight_loss", Double.class))
                        .wet(row.get("wet", Double.class))
                        .rice(row.get("rice", Double.class))
                        .bag(row.get("bag", Double.class))
                        .design(row.get("design", String.class))
                        .size(row.get("size", String.class))
                        .build())
                .all();
    }


    public Flux<SaleHisDetail> getSaleByBatch(String batchNo, String compCode) {
        String sql = """
                    SELECT a.*, a.qty * a.sale_price AS sale_amt, s.user_code AS s_user_code, s.stock_name, rel.rel_name, l.loc_name
                    FROM (
                        SELECT stock_code, SUM(qty) AS qty, sale_unit, sale_price, loc_code, comp_code, dept_id
                        FROM v_sale
                        WHERE batch_no = :batchNo
                        AND comp_code = :compCode
                        AND deleted = 0
                        GROUP BY stock_code, sale_unit, sale_price, loc_code
                    ) a
                    JOIN stock s ON a.stock_code = s.stock_code AND a.comp_code = s.comp_code AND a.dept_id = s.dept_id
                    LEFT JOIN unit_relation rel ON s.rel_code = rel.rel_code AND s.comp_code = rel.comp_code AND s.dept_id = rel.dept_id
                    JOIN location l ON a.loc_code = l.loc_code AND a.comp_code = l.comp_code AND a.dept_id = l.dept_id
                    ORDER BY s_user_code
                """;

        return client.sql(sql)
                .bind("batchNo", batchNo)
                .bind("compCode", compCode)
                .map((row, metadata) -> SaleHisDetail.builder()
                        .key(SaleDetailKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .stockCode(row.get("stock_code", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("sale_price", Double.class))
                        .amount(row.get("sale_amt", Double.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .unitCode(row.get("sale_unit", String.class))
                        .userCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .build())
                .all();
    }


    public Flux<SaleHisDetail> getSaleByBatchDetail(String batchNo, String compCode) {
        String sql = """
                    SELECT a.*, a.qty * a.sale_price AS sale_amt, s.user_code AS s_user_code, s.stock_name, rel.rel_name, l.loc_name
                    FROM (
                        SELECT stock_code, qty, sale_unit, sale_price, loc_code, comp_code, dept_id
                        FROM v_sale
                        WHERE batch_no = :batchNo
                        AND comp_code = :compCode
                        AND deleted = 0
                    ) a
                    JOIN stock s ON a.stock_code = s.stock_code AND a.comp_code = s.comp_code AND a.dept_id = s.dept_id
                    LEFT JOIN unit_relation rel ON s.rel_code = rel.rel_code AND s.comp_code = rel.comp_code
                    JOIN location l ON a.loc_code = l.loc_code AND a.comp_code = l.comp_code
                    ORDER BY s_user_code
                """;
        return client.sql(sql)
                .bind("batchNo", batchNo)
                .bind("compCode", compCode)
                .map((row, metadata) -> SaleHisDetail.builder()
                        .key(SaleDetailKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .stockCode(row.get("stock_code", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("sale_price", Double.class))
                        .amount(row.get("sale_amt", Double.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .unitCode(row.get("sale_unit", String.class))
                        .userCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .build())
                .all();
    }


    public Mono<Boolean> delete(String vouNo, String compCode) {
        String sql = """
                delete from sale_his_detail where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }
}
