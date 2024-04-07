/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.Stock;
import cv.api.entity.StockKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@Service
@RequiredArgsConstructor
public class StockService {

    private final ReportService reportService;
    private final DatabaseClient client;
    private final SeqService seqService;


    public Mono<Stock> save(Stock dto) {
        String stockCode = dto.getKey().getStockCode();
        String compCode = dto.getKey().getCompCode();
        Integer deptId = dto.getDeptId();
        if (Util1.isNullOrEmpty(stockCode)) {
            return seqService.getNextCode("Stock", compCode, 5)
                    .flatMap(seqNo -> {
                        String code = String.format("%0" + 2 + "d", deptId) + "-" + seqNo;
                        dto.getKey().setStockCode(code);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    public Stock mapRow(Row row) {
        return Stock.builder()
                .key(StockKey.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .active(row.get("active", Boolean.class))
                .brandCode(row.get("brand_code", String.class))
                .stockName(row.get("stock_name", String.class))
                .catCode(row.get("category_code", String.class))
                .typeCode(row.get("stock_type_code", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .barcode(row.get("barcode", String.class))
                .shortName(row.get("short_name", String.class))
                .purPrice(row.get("pur_price", Double.class))
                .purUnitCode(row.get("pur_unit", String.class))
                .expireDate(row.get("licence_exp_date", LocalDate.class))
                .saleUnitCode(row.get("sale_unit", String.class))
                .remark(row.get("remark", String.class))
                .salePriceN(row.get("sale_price_n", Double.class))
                .salePriceA(row.get("sale_price_a", Double.class))
                .salePriceB(row.get("sale_price_b", Double.class))
                .salePriceC(row.get("sale_price_c", Double.class))
                .salePriceD(row.get("sale_price_d", Double.class))
                .salePriceE(row.get("sale_price_e", Double.class))
                .saleWt(row.get("sale_wt", Double.class))
                .purWt(row.get("pur_wt", Double.class))
                .migCode(row.get("mig_code", String.class))
                .userCode(row.get("user_code", String.class))
                .macId(row.get("mac_id", Integer.class))
                .relCode(row.get("rel_code", String.class))
                .calculate(row.get("calculate", Boolean.class))
                .deptId(row.get("dept_id", Integer.class))
                .explode(row.get("explode", Boolean.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .weightUnit(row.get("weight_unit", String.class))
                .weight(row.get("weight", Double.class))
                .favorite(row.get("favorite", Boolean.class))
                .saleClosed(row.get("sale_closed", Boolean.class))
                .deleted(row.get("deleted", Boolean.class))
                .saleQty(row.get("sale_qty", Double.class))
                .formulaCode(row.get("formula_code", String.class))
                .saleAmt(row.get("sale_amt", Double.class))
                .purAmt(row.get("pur_amt", Double.class))
                .purQty(row.get("pur_qty", Double.class))
                .build();
    }


    public Mono<Stock> findById(StockKey key) {
        String stockCode = key.getStockCode();
        if (Util1.isNullOrEmpty(stockCode)) {
            return Mono.empty();
        }
        String sql = """
                select *
                from stock
                where comp_code = :compCode
                and stock_code =:stockCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("stockCode", stockCode)
                .map((row, rowMetadata) -> mapRow(row)).one();
    }
    public Mono<Stock> findByBarcode(StockKey key) {
        String barcode = key.getStockCode();
        if (Util1.isNullOrEmpty(barcode)) {
            return Mono.empty();
        }
        String sql = """
                select *
                from stock
                where comp_code = :compCode
                and barcode =:barCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("barCode", barcode)
                .map((row, rowMetadata) -> mapRow(row)).one();
    }


    public Flux<Stock> findAll(String compCode, Integer deptId) {
        String sql = """
                select *
                from stock
                where comp_code=:compCode
                and (dept_id =:deptId or 0 =:deptId)
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }


    public Flux<Stock> findActiveStock(String compCode) {
        String sql = """
                select *
                from stock
                where comp_code=:compCode
                and active = true
                and deleted =false
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }


    public Flux<General> delete(StockKey key) {
        String stockCode = key.getStockCode();
        String compCode = key.getCompCode();
        return reportService.isStockExist(stockCode, compCode)
                .collectList()
                .flatMapMany(generalList -> {
                    if (generalList.isEmpty()) {
                        return updateDeleteStatus(key, true)
                                .thenMany(Flux.fromIterable(generalList));
                    } else {
                        return Flux.fromIterable(generalList);
                    }
                });
    }

    @Transactional
    private Mono<Boolean> updateDeleteStatus(StockKey key, boolean status) {
        String sql = """
                update stock
                set deleted =:status,updated_date=:updatedDate
                where stock_code=:stockCode
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("status", status)
                .bind("updatedDate", LocalDateTime.now())
                .bind("stockCode", key.getStockCode())
                .bind("compCode", key.getCompCode())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<Boolean> restore(StockKey key) {
        return updateDeleteStatus(key, false);
    }


    public Flux<Stock> search(ReportFilter filter) {
        String stockCode = Util1.isAll(filter.getStockCode());
        String typCode = Util1.isAll(filter.getStockTypeCode());
        String catCode = Util1.isAll(filter.getCatCode());
        String brandCode = Util1.isAll(filter.getBrandCode());
        Integer deptId = filter.getDeptId();
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        boolean active = filter.isActive();
        String sql = """
                select s.*,st.stock_type_name,cat.cat_name
                from stock s
                left join stock_type st
                on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join category cat
                on s.category_code = cat.cat_code
                and s.comp_code = cat.comp_code
                where s.comp_code = :compCode
                and s.deleted = :deleted
                and s.active = :active
                and (stock_code = :stockCode or '-' = :stockCode)
                and (s.stock_type_code = :stockType or '-' = :stockType)
                and (s.category_code = :cat or '-' = :cat)
                and (s.brand_code = :brand or '-' = :brand)
                and (s.dept_id = :deptId or 0 = :deptId)
                order by st.user_code,cat.user_code,s.user_code,s.stock_name
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .bind("active", active)
                .bind("stockCode", stockCode)
                .bind("stockType", typCode)
                .bind("cat", catCode)
                .bind("brand", brandCode)
                .bind("deptId", deptId)
                .map(row -> Stock.builder()
                        .key(StockKey.builder()
                                .stockCode(row.get("stock_code", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .active(row.get("active", Boolean.class))
                        .brandCode(row.get("brand_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .catCode(row.get("category_code", String.class))
                        .typeCode(row.get("stock_type_code", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .createdDate(row.get("created_date", LocalDateTime.class))
                        .updatedBy(row.get("updated_by", String.class))
                        .updatedDate(row.get("updated_date", LocalDateTime.class))
                        .barcode(row.get("barcode", String.class))
                        .shortName(row.get("short_name", String.class))
                        .purPrice(row.get("pur_price", Double.class))
                        .purUnitCode(row.get("pur_unit", String.class))
                        .saleUnitCode(row.get("sale_unit", String.class))
                        .remark(row.get("remark", String.class))
                        .salePriceN(row.get("sale_price_n", Double.class))
                        .salePriceA(row.get("sale_price_a", Double.class))
                        .salePriceB(row.get("sale_price_b", Double.class))
                        .salePriceC(row.get("sale_price_c", Double.class))
                        .salePriceD(row.get("sale_price_d", Double.class))
                        .salePriceE(row.get("sale_price_e", Double.class))
                        .userCode(row.get("user_code", String.class))
                        .macId(row.get("mac_id", Integer.class))
                        .relCode(row.get("rel_code", String.class))
                        .calculate(row.get("calculate", Boolean.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .explode(row.get("explode", Boolean.class))
                        .intgUpdStatus(row.get("intg_upd_status", String.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .weight(row.get("weight", Double.class))
                        .favorite(row.get("favorite", Boolean.class))
                        .saleClosed(row.get("sale_closed", Boolean.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .saleQty(row.get("sale_qty", Double.class))
                        .formulaCode(row.get("formula_code", String.class))
                        .saleAmt(row.get("sale_amt", Double.class))
                        .purAmt(row.get("pur_amt", Double.class))
                        .purQty(row.get("pur_qty", Double.class))
                        .groupName(row.get("stock_type_name", String.class))
                        .catName(row.get("cat_name", String.class))
                        .build())
                .all();
    }

    @Transactional
    public Mono<Stock> insert(Stock dto) {
        String sql = """
                INSERT INTO stock (
                    stock_code, comp_code, active, brand_code, stock_name, category_code, stock_type_code, created_by, created_date,
                    updated_by, updated_date, barcode, short_name, pur_price, pur_unit, licence_exp_date, sale_unit, remark,
                    sale_price_n, sale_price_a, sale_price_b, sale_price_c, sale_price_d, sale_price_e, sale_wt, pur_wt, mig_code,
                    user_code, mac_id, rel_code, calculate, dept_id, explode, intg_upd_status, weight_unit, weight, favorite,
                    sale_closed, deleted, sale_qty, formula_code, sale_amt, pur_amt, pur_qty
                ) VALUES (
                    :stockCode, :compCode, :active, :brandCode, :stockName, :categoryCode, :stockTypeCode, :createdBy, :createdDate,
                    :updatedBy, :updatedDate, :barcode, :shortName, :purPrice, :purUnit, :licenceExpDate, :saleUnit, :remark,
                    :salePriceN, :salePriceA, :salePriceB, :salePriceC, :salePriceD, :salePriceE, :saleWt, :purWt, :migCode,
                    :userCode, :macId, :relCode, :calculate, :deptId, :explode, :intgUpdStatus, :weightUnit, :weight, :favorite,
                    :saleClosed, :deleted, :saleQty, :formulaCode, :saleAmt, :purAmt, :purQty
                )
                """;
        return executeUpdate(sql, dto);
    }
    @Transactional
    public Mono<Stock> update(Stock dto) {
        String sql = """
                UPDATE stock
                SET
                    active = :active,
                    brand_code = :brandCode,
                    stock_name = :stockName,
                    category_code = :categoryCode,
                    stock_type_code = :stockTypeCode,
                    created_by = :createdBy,
                    created_date = :createdDate,
                    updated_by = :updatedBy,
                    updated_date = :updatedDate,
                    barcode = :barcode,
                    short_name = :shortName,
                    pur_price = :purPrice,
                    pur_unit = :purUnit,
                    licence_exp_date = :licenceExpDate,
                    sale_unit = :saleUnit,
                    remark = :remark,
                    sale_price_n = :salePriceN,
                    sale_price_a = :salePriceA,
                    sale_price_b = :salePriceB,
                    sale_price_c = :salePriceC,
                    sale_price_d = :salePriceD,
                    sale_price_e = :salePriceE,
                    sale_wt = :saleWt,
                    pur_wt = :purWt,
                    mig_code = :migCode,
                    user_code = :userCode,
                    mac_id = :macId,
                    rel_code = :relCode,
                    calculate = :calculate,
                    dept_id = :deptId,
                    explode = :explode,
                    intg_upd_status = :intgUpdStatus,
                    weight_unit = :weightUnit,
                    weight = :weight,
                    favorite = :favorite,
                    sale_closed = :saleClosed,
                    deleted = :deleted,
                    sale_qty = :saleQty,
                    formula_code = :formulaCode,
                    sale_amt = :saleAmt,
                    pur_amt = :purAmt,
                    pur_qty = :purQty
                WHERE
                    stock_code = :stockCode
                    AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<Stock> executeUpdate(String sql, Stock dto) {
        return client.sql(sql)
                .bind("stockCode", dto.getKey().getStockCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("active", dto.getActive())
                .bind("brandCode", Parameters.in(R2dbcType.VARCHAR, dto.getBrandCode()))
                .bind("stockName", dto.getStockName())
                .bind("categoryCode", Parameters.in(R2dbcType.VARCHAR, dto.getCatCode()))
                .bind("stockTypeCode", dto.getTypeCode())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("barcode", Parameters.in(R2dbcType.VARCHAR, dto.getBarcode()))
                .bind("shortName", Parameters.in(R2dbcType.VARCHAR, dto.getShortName()))
                .bind("purPrice", Parameters.in(R2dbcType.DOUBLE, dto.getPurPrice()))
                .bind("purUnit", Parameters.in(R2dbcType.VARCHAR, dto.getPurUnitCode()))
                .bind("licenceExpDate", Parameters.in(R2dbcType.DATE, dto.getExpireDate()))
                .bind("saleUnit", Parameters.in(R2dbcType.VARCHAR, dto.getSaleUnitCode()))
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("salePriceN", Parameters.in(R2dbcType.DOUBLE, dto.getSalePriceN()))
                .bind("salePriceA", Parameters.in(R2dbcType.DOUBLE, dto.getSalePriceA()))
                .bind("salePriceB", Parameters.in(R2dbcType.DOUBLE, dto.getSalePriceB()))
                .bind("salePriceC", Parameters.in(R2dbcType.DOUBLE, dto.getSalePriceC()))
                .bind("salePriceD", Parameters.in(R2dbcType.DOUBLE, dto.getSalePriceD()))
                .bind("salePriceE", Parameters.in(R2dbcType.DOUBLE, dto.getSalePriceE()))
                .bind("saleWt", Parameters.in(R2dbcType.DOUBLE, dto.getSaleWt()))
                .bind("purWt", Parameters.in(R2dbcType.DOUBLE, dto.getPurWt()))
                .bind("migCode", Parameters.in(R2dbcType.VARCHAR, dto.getMigCode()))
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("macId", dto.getMacId())
                .bind("relCode", Parameters.in(R2dbcType.VARCHAR, dto.getRelCode()))
                .bind("calculate", Parameters.in(R2dbcType.BOOLEAN, dto.getCalculate()))
                .bind("deptId", dto.getDeptId())
                .bind("explode", Parameters.in(R2dbcType.BOOLEAN, dto.getExplode()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("weightUnit", Parameters.in(R2dbcType.VARCHAR, dto.getWeightUnit()))
                .bind("weight", Parameters.in(R2dbcType.DOUBLE, dto.getWeight()))
                .bind("favorite", Parameters.in(R2dbcType.BOOLEAN, dto.getFavorite()))
                .bind("saleClosed", Parameters.in(R2dbcType.BOOLEAN, dto.getSaleClosed()))
                .bind("deleted", Parameters.in(R2dbcType.BOOLEAN, dto.getDeleted()))
                .bind("saleQty", Parameters.in(R2dbcType.DOUBLE, dto.getSaleQty()))
                .bind("formulaCode", Parameters.in(R2dbcType.VARCHAR, dto.getFormulaCode()))
                .bind("saleAmt", Parameters.in(R2dbcType.DOUBLE, dto.getSaleAmt()))
                .bind("purAmt", Parameters.in(R2dbcType.DOUBLE, dto.getPurAmt()))
                .bind("purQty", Parameters.in(R2dbcType.DOUBLE, dto.getPurQty()))
                .fetch()
                .rowsUpdated().thenReturn(dto);
    }


    public Flux<Stock> getStock(String str, String compCode, Integer deptId) {
        str = str.toLowerCase().replaceAll("\\s+", "");
        String sql = """
                SELECT s.*, rel.rel_name, st.stock_type_name, cat.cat_name, b.brand_name
                FROM stock s
                LEFT JOIN unit_relation rel ON s.rel_code = rel.rel_code
                AND s.comp_code = rel.comp_code
                LEFT JOIN stock_type st ON s.stock_type_code = st.stock_type_code
                AND s.comp_code = st.comp_code
                LEFT JOIN category cat ON s.category_code = cat.cat_code
                AND s.comp_code = cat.comp_code
                LEFT JOIN stock_brand b ON s.brand_code = b.brand_code
                AND s.comp_code = b.comp_code
                WHERE s.deleted = false
                AND s.comp_code = :compCode
                AND s.active = true
                AND (s.dept_id = :deptId OR 0 = :deptId)
                AND (LOWER(REPLACE(s.user_code, ' ', '')) LIKE :str OR LOWER(REPLACE(s.stock_name, ' ', '')) LIKE :str)
                ORDER BY s.user_code, s.stock_name
                LIMIT 100
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("str", "%" + str + "%")
                .map(row -> Stock.builder()
                        .key(StockKey.builder()
                                .stockCode(row.get("stock_code", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .brandCode(row.get("brand_code", String.class))
                        .catCode(row.get("category_code", String.class))
                        .typeCode(row.get("stock_type_code", String.class))
                        .purPrice(row.get("pur_price", Double.class))
                        .purUnitCode(row.get("pur_unit", String.class))
                        .saleUnitCode(row.get("sale_unit", String.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .weight(row.get("weight", Double.class))
                        .salePriceN(row.get("sale_price_n", Double.class))
                        .salePriceA(row.get("sale_price_a", Double.class))
                        .salePriceB(row.get("sale_price_b", Double.class))
                        .salePriceC(row.get("sale_price_c", Double.class))
                        .salePriceD(row.get("sale_price_d", Double.class))
                        .salePriceE(row.get("sale_price_e", Double.class))
                        .stockName(row.get("stock_name", String.class))
                        .userCode(row.get("user_code", String.class))
                        .formulaCode(row.get("formula_code", String.class))
                        .relName(row.get("rel_name", String.class))
                        .groupName(row.get("stock_type_name", String.class))
                        .catName(row.get("cat_name", String.class))
                        .brandName(row.get("brand_name", String.class))
                        .explode(row.get("explode", Boolean.class))
                        .purQty(row.get("pur_qty", Double.class))
                        .purAmt(row.get("pur_amt", Double.class))
                        .saleAmt(row.get("sale_amt", Double.class))
                        .calculate(row.get("calculate", Boolean.class))
                        .build())
                .all();
    }


    public Flux<Stock> getService(String compCode, Integer deptId) {
        String sql = """
                select *
                from stock
                where calculate = false
                and deleted = false
                and comp_code = :compCode
                and (dept_id = :deptId or 0 = :deptId)
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .map(row -> Stock.builder()
                        .key(StockKey.builder()
                                .stockCode(row.get("stock_code", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .saleUnitCode(row.get("sale_unit", String.class))
                        .salePriceN(row.get("sale_price_n", Double.class))
                        .salePriceA(row.get("sale_price_a", Double.class))
                        .salePriceB(row.get("sale_price_b", Double.class))
                        .salePriceC(row.get("sale_price_c", Double.class))
                        .salePriceD(row.get("sale_price_d", Double.class))
                        .salePriceE(row.get("sale_price_e", Double.class))
                        .stockName(row.get("stock_name", String.class))
                        .userCode(row.get("user_code", String.class))
                        .build())
                .all();
    }


    public Flux<Stock> getStock(LocalDateTime updatedDate) {
        String sql = """
                select *
                from stock
                where updated_date >:updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }
    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT COUNT(*)
                FROM stock
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated()
                .map(count -> count > 0);
    }
}
