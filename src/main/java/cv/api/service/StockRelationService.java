package cv.api.service;

import cv.api.common.ReturnObject;
import cv.api.entity.UnitRelationDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockRelationService {
    private final HashMap<String, List<UnitRelationDetail>> hmRelation = new HashMap<>();
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private final DatabaseClient client;
    private final UnitRelationService unitRelationService;

    private Mono<Boolean> calculateOpening(String opDate, String fromDate, String typeCode,
                                           String catCode, String brandCode, String stockCode,
                                           String vouStatus, boolean calSale,
                                           boolean calPur, boolean calRI, boolean calRO,
                                           String compCode, Integer deptId, Integer macId) {
        String opSql = """
                INSERT INTO tmp_stock_opening(tran_date, stock_code, ttl_qty, loc_code, unit, comp_code, dept_id, mac_id)
                SELECT :fromDate op_date, stock_code, SUM(qty) ttl_qty, loc_code, unit, :compCode, :deptId, :macId
                FROM (
                    SELECT stock_code, SUM(qty) qty, loc_code, unit
                    FROM v_opening
                    WHERE DATE(op_date) = :opDate
                    AND comp_code = :compCode
                    AND tran_source = 1
                    AND deleted = true
                    AND calculate = true
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, pur_unit
                    FROM v_purchase
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = true
                    AND (calculate = true AND :calPur = 0)
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, pur_unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, unit
                    FROM v_return_in
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = true
                    AND (calculate = true AND :calRI = 0)
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, in_unit
                    FROM v_stock_io
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = true
                    AND calculate = true
                    AND in_qty IS NOT NULL AND in_unit IS NOT NULL
                    AND comp_code = :compCode
                    AND (vou_status = :vouStatus OR '-' = :vouStatus)
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, in_unit
                    UNION ALL
                    SELECT stock_code, SUM(out_qty) qty, loc_code, out_unit
                    FROM v_stock_io
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = true
                    AND calculate = true
                    AND out_qty IS NOT NULL AND out_unit IS NOT NULL
                    AND comp_code = :compCode
                    AND (vou_status = :vouStatus OR '-' = :vouStatus)
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, out_unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, unit
                    FROM v_return_out
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND (calculate = true AND :calRO = 0)
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, sale_unit
                    FROM v_sale
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = true
                    AND (calculate = true AND :calSale = 0)
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (cat_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, sale_unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code_from, unit
                    FROM v_transfer
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = true
                    AND calculate = true
                    AND comp_code = :compCode
                    AND loc_code_from IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code_to, unit
                    FROM v_transfer
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = true
                    AND calculate = true
                    AND comp_code = :compCode
                    AND loc_code_to IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, unit
                    FROM v_process_his_detail
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND calculate = true
                    AND comp_code = :compCode
                    AND (pt_code = :vouStatus OR '-' = :vouStatus)
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                    UNION ALL
                    SELECT stock_code, SUM(qty) qty, loc_code, unit
                    FROM v_process_his
                    WHERE DATE(vou_date) >= :opDate AND DATE(vou_date) < :fromDate
                    AND deleted = false
                    AND (pt_code = :vouStatus OR '-' = :vouStatus)
                    AND calculate = true
                    AND comp_code = :compCode
                    AND loc_code IN (SELECT f_code FROM f_location WHERE mac_id = :macId)
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (category_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, unit
                ) x
                GROUP BY stock_code, unit""";
        return deleteTmpOpening(macId)
                .then(client.sql(opSql)
                        .bind("opDate", opDate)
                        .bind("fromDate", fromDate)
                        .bind("typeCode", typeCode)
                        .bind("catCode", catCode)
                        .bind("brandCode", brandCode)
                        .bind("stockCode", stockCode)
                        .bind("vouStatus", vouStatus)
                        .bind("calSale", calSale)
                        .bind("calPur", calPur)
                        .bind("calRI", calRI)
                        .bind("calRO", calRO)
                        .bind("compCode", compCode)
                        .bind("deptId", deptId)
                        .bind("macId", macId)
                        .fetch()
                        .rowsUpdated().thenReturn(true));

    }

    private Mono<Boolean> calculateClosing(String fromDate, String toDate, String typeCode, String catCode,
                                           String brandCode, String stockCode, String vouStatus,
                                           boolean calSale, boolean calPur, boolean calRI,
                                           boolean calRO, String compCode, Integer deptId,
                                           Integer macId) {
        return null;

    }

    public Mono<ReturnObject> getStockInOutSummary(String opDate, String fromDate, String toDate,
                                                   String typeCode, String catCode, String brandCode,
                                                   String stockCode, String vouStatus,
                                                   boolean calSale, boolean calPur, boolean calRI,
                                                   boolean calRO, String compCode, Integer deptId, Integer macId) {
        return null;
    }

    private Mono<Boolean> initRelation(String compCode) {
        return unitRelationService.getUnitRelationAndDetail(compCode)
                .map(t -> {
                    String relCode = t.getKey().getRelCode();
                    hmRelation.put(relCode, t.getDetailList());
                    return true;
                }).then(Mono.just(true));
    }

    private String getRelStr(String relCode, double smallestQty) {
        //generate unit relation.
        StringBuilder relStr = new StringBuilder();
        if (smallestQty != 0 && !Objects.isNull(relCode)) {
            List<UnitRelationDetail> detailList = hmRelation.get(relCode);
            if (detailList != null) {
                for (UnitRelationDetail unitRelationDetail : detailList) {
                    double smallQty = unitRelationDetail.getSmallestQty();
                    double divider = smallestQty / smallQty;
                    smallestQty = smallestQty % smallQty;
                    String str;
                    if (smallQty == 1) {
                        if (divider != 0) {
                            str = formatter.format(divider);
                            relStr.append(String.format("%s %s%s", str, unitRelationDetail.getUnit(), "*"));
                        }
                    } else {
                        int first = (int) divider;
                        if (first != 0) {
                            str = formatter.format(first);
                            relStr.append(String.format("%s %s%s", str, unitRelationDetail.getUnit(), "*"));
                        }
                    }
                }
            } else {
                log.info(String.format("non relation: %s", relCode));
            }
        }
        String str = relStr.toString();
        if (str.contains("-")) {
            str = str.replaceAll("-", "");
            str = String.format("%s%s", "-", str);
        }
        if (str.isEmpty()) {
            str = "*";
        }
        str = str.substring(0, str.length() - 1);
        if (str.contains("-")) {
            str = str.replaceAll("-", "");
            str = String.format("(%s)", str);
        }
        return str;

    }

    @Transactional
    private Mono<Boolean> deleteTmpOpening(int macId) {
        String sql = """
                delete from tmp_stock_opening where mac_id = :macId
                """;
        return client.sql(sql)
                .bind("macId", macId)
                .fetch().rowsUpdated().thenReturn(true);
    }

    @Transactional
    private Mono<Boolean> deleteTmpColumn(int macId) {
        String sql = """
                delete from tmp_stock_io_column where mac_id = :macId
                """;
        return client.sql(sql)
                .bind("macId", macId)
                .fetch().rowsUpdated().thenReturn(true);
    }
}
