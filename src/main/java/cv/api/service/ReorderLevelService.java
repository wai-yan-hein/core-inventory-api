package cv.api.service;

import cv.api.entity.ReorderKey;
import cv.api.entity.ReorderLevel;
import io.r2dbc.spi.Row;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReorderLevelService {
    private final DatabaseClient client;

    public Mono<ReorderLevel> saveOrUpdate(ReorderLevel dto) {
        return findById(dto.getKey())
                .flatMap(reorderLevel -> update(dto))
                .switchIfEmpty(Mono.defer(() -> insert(dto)));
    }

    private Mono<ReorderLevel> findById(ReorderKey key) {
        String sql = """
                select *
                from reorder_level
                where comp_code = :compCode
                and stock_code = :stockCode
                and loc_code = :locCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("stockCode", key.getStockCode())
                .bind("locCode", key.getLocCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public ReorderLevel mapRow(Row row) {
        return ReorderLevel.builder()
                .key(ReorderKey.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .minQty(row.get("min_qty", Double.class))
                .minUnitCode(row.get("min_unit", String.class))
                .maxQty(row.get("max_qty", Double.class))
                .maxUnitCode(row.get("max_unit", String.class))
                .balSmallQty(row.get("bal_qty", Double.class))
                .balUnit(row.get("bal_unit", String.class))
                .build();
    }

    public Mono<ReorderLevel> insert(ReorderLevel dto) {
        String sql = """
                INSERT INTO reorder_level (stock_code, min_qty, min_unit, max_qty, max_unit, 
                                           bal_qty, bal_unit, comp_code, dept_id, loc_code)
                VALUES (:stockCode, :minQty, :minUnit, :maxQty, :maxUnit, 
                        :balQty, :balUnit, :compCode, :deptId, :locCode)
                """;
        return client.sql(sql)
                .bind("stockCode", dto.getKey().getStockCode())
                .bind("minQty", dto.getMinQty())
                .bind("minUnit", dto.getMinUnitCode())
                .bind("maxQty", dto.getMaxQty())
                .bind("maxUnit", dto.getMaxUnitCode())
                .bind("balQty", dto.getBalSmallQty())
                .bind("balUnit", dto.getBalUnit())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("locCode", dto.getKey().getLocCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<ReorderLevel> update(ReorderLevel dto) {
        String sql = """
                UPDATE reorder_level
                SET min_qty = :minQty, min_unit = :minUnit, max_qty = :maxQty, max_unit = :maxUnit,
                bal_qty = :balQty, bal_unit = :balUnit
                WHERE stock_code = :stockCode AND comp_code = :compCode AND dept_id = :deptId AND loc_code = :locCode
                """;
        return client.sql(sql)
                .bind("stockCode", dto.getKey().getStockCode())
                .bind("minQty", dto.getMinQty())
                .bind("minUnit", dto.getMinUnitCode())
                .bind("maxQty", dto.getMaxQty())
                .bind("maxUnit", dto.getMaxUnitCode())
                .bind("balQty", dto.getBalSmallQty())
                .bind("balUnit", dto.getBalUnit())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("locCode", dto.getKey().getLocCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }
}
