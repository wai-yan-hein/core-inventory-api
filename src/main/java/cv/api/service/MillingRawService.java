package cv.api.service;

import cv.api.entity.MillingRawDetail;
import cv.api.entity.MillingRawDetailKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class MillingRawService {
    private final DatabaseClient client;

    public Mono<MillingRawDetail> insert(MillingRawDetail dto) {
        String sql = """
                INSERT INTO milling_raw (vou_no, unique_id, comp_code, stock_code, qty, unit, price, amt, loc_code, dept_id, weight, weight_unit, tot_weight)
                VALUES (:vouNo, :uniqueId, :compCode, :stockCode, :qty, :unit, :price, :amt, :locCode, :deptId, :weight, :weightUnit, :totWeight)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<MillingRawDetail> update(MillingRawDetail dto) {
        String sql = """
                UPDATE milling_raw
                SET stock_code = :stockCode, qty = :qty, unit = :unit, price = :price, amt = :amt, loc_code = :locCode, dept_id = :deptId, weight = :weight, weight_unit = :weightUnit, tot_weight = :totWeight
                WHERE vou_no = :vouNo AND unique_id = :uniqueId AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<MillingRawDetail> executeUpdate(String sql, MillingRawDetail dto) {
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
                .bind("weight", dto.getWeight())
                .bind("weightUnit", dto.getWeightUnit())
                .bind("totWeight", dto.getTotalWeight())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from milling_raw where vou_no = :vouNo and compCode = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<MillingRawDetail> search(String vouNo, String compCode) {
        String sql = """
                SELECT op.*, s.user_code, s.stock_name, l.loc_name, u1.unit_name, u2.unit_name weight_unit_name
                FROM milling_raw op
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
                ORDER BY unique_id
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> MillingRawDetail.builder()
                        .key(MillingRawDetailKey.builder()
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
                        .unitName(row.get("unit_name", String.class))
                        .weightUnitName(row.get("weight_unit_name", String.class))
                        .build())
                .all();
    }

}
