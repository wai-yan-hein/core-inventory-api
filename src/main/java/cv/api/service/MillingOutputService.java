package cv.api.service;

import cv.api.entity.MillingOutDetail;
import cv.api.entity.MillingOutDetailKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class MillingOutputService {
    private final DatabaseClient client;


    public Mono<MillingOutDetail> insert(MillingOutDetail dto) {
        String sql = """
                INSERT INTO milling_output (vou_no, comp_code, unique_id, stock_code, qty, unit, price, amt, loc_code, dept_id, weight, weight_unit, percent, tot_weight, percent_qty, sort_id)
                VALUES (:vouNo, :compCode, :uniqueId, :stockCode, :qty, :unit, :price, :amt, :locCode, :deptId, :weight, :weightUnit, :percent, :totWeight, :percentQty, :sortId)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<MillingOutDetail> update(MillingOutDetail dto) {
        String sql = """
                UPDATE milling_output
                SET stock_code = :stockCode, qty = :qty, unit = :unit, price = :price, amt = :amt, loc_code = :locCode, dept_id = :deptId, weight = :weight, weight_unit = :weightUnit, percent = :percent, tot_weight = :totWeight, percent_qty = :percentQty, sort_id = :sortId
                WHERE vou_no = :vouNo AND comp_code = :compCode AND unique_id = :uniqueId
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<MillingOutDetail> executeUpdate(String sql, MillingOutDetail dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("stockCode", dto.getStockCode())
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnitCode())
                .bind("price", dto.getPrice())
                .bind("amt", dto.getAmount())
                .bind("locCode", dto.getLocCode())
                .bind("deptId", dto.getDeptId())
                .bind("weight", dto.getWeight())
                .bind("weightUnit", dto.getWeightUnit())
                .bind("percent", dto.getPercent())
                .bind("totWeight", dto.getTotalWeight())
                .bind("percentQty", dto.getPercentQty())
                .bind("sortId", dto.getSortId())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from milling_output where vou_no = :vouNo and compCode = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<MillingOutDetail> search(String vouNo, String compCode) {
        String sql = """
                SELECT op.*, s.user_code, s.stock_name, l.loc_name, u1.unit_name, u2.unit_name weight_unit_name
                FROM milling_output op
                JOIN location l ON op.loc_code = l.loc_code
                AND op.comp_code = l.comp_code
                JOIN stock s ON op.stock_code = s.stock_code
                AND op.comp_code = s.comp_code
                LEFT JOIN stock_unit u1 ON op.unit = u1.unit_code
                AND op.comp_code = u1.comp_code
                LEFT JOIN stock_unit u2 ON op.weight_unit = u2.unit_code
                AND op.comp_code = u2.comp_code
                WHERE op.vou_no = :vouNo
                AND op.comp_code = :compCode
                ORDER BY sort_id, unique_id
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> MillingOutDetail.builder()
                        .key(MillingOutDetailKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .vouNo(row.get("vou_no", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amt", Double.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .unitCode(row.get("unit", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .totalWeight(row.get("tot_weight", Double.class))
                        .percent(row.get("percent", Double.class))
                        .percentQty(row.get("percent_qty", Double.class))
                        .unitName(row.get("unit_name", String.class))
                        .weightUnitName(row.get("weight_unit_name", String.class))
                        .build())
                .all();
    }


}
