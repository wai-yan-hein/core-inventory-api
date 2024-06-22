package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.ReorderKey;
import cv.api.entity.ReorderLevel;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReorderLevelService {
    private final DatabaseClient client;
    private final StockRelationService stockRelationService;

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
                                           comp_code, loc_code)
                VALUES (:stockCode, :minQty, :minUnit, :maxQty, :maxUnit,
                         :compCode, :locCode)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<ReorderLevel> update(ReorderLevel dto) {
        String sql = """
                UPDATE reorder_level
                SET min_qty = :minQty, min_unit = :minUnit, max_qty = :maxQty, max_unit = :maxUnit
                WHERE stock_code = :stockCode AND comp_code = :compCode AND loc_code = :locCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<ReorderLevel> executeUpdate(String sql, ReorderLevel dto) {
        return client.sql(sql)
                .bind("stockCode", dto.getKey().getStockCode())
                .bind("minQty", Util1.getDouble(dto.getMinQty()))
                .bind("minUnit", Parameters.in(R2dbcType.VARCHAR,dto.getMinUnitCode()))
                .bind("maxQty", Util1.getDouble(dto.getMaxQty()))
                .bind("maxUnit", Parameters.in(R2dbcType.VARCHAR,dto.getMaxUnitCode()))
                .bind("compCode", dto.getKey().getCompCode())
                .bind("locCode", dto.getKey().getLocCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Flux<ReorderLevel> getReorderLevel(Integer macId,boolean summary) {
        if(summary){
            String sql= """
                    select a.*,rel.rel_name,if(bal_qty<min_small_qty,1,if(bal_qty>min_small_qty,2,if(bal_qty<max_small_qty,3,if(bal_qty> max_small_qty,4,5)))) position
                    from (
                    select tmp.stock_code,s.user_code,s.stock_name,s.rel_code,'-' loc_code,sum(tmp.ttl_qty) bal_qty,sum(ifnull(r.min_qty,0)*ifnull(min.smallest_qty,1)) min_small_qty,
                    sum(ifnull(r.max_qty,0)*ifnull(max.smallest_qty,1)) max_small_qty,tmp.comp_code
                    from tmp_stock_opening tmp
                    join stock s on tmp.stock_code = s.stock_code
                    and tmp.comp_code = s.comp_code
                    left join reorder_level r on tmp.stock_code = r.stock_code
                    and tmp.comp_code = r.comp_code
                    and tmp.loc_code = r.loc_code
                    left join unit_relation_detail min on s.rel_code = min.rel_code
                    and tmp.comp_code = min.comp_code
                    and r.min_unit = min.unit
                    left join unit_relation_detail max on s.rel_code = max.rel_code
                    and tmp.comp_code = max.comp_code
                    and r.max_unit = min.unit
                    where tmp.mac_id = :macId
                    group by tmp.stock_code
                    )a
                    join unit_relation rel on a.rel_code = rel.rel_code
                    and a.comp_code = rel.comp_code
                    order by position
                    """;
            return client.sql(sql)
                    .bind("macId", macId)
                    .map((row) -> ReorderLevel.builder()
                            .key(ReorderKey.builder()
                                    .stockCode(row.get("stock_code", String.class))
                                    .compCode(row.get("comp_code", String.class))
                                    .locCode(row.get("loc_code", String.class))
                                    .build())
                            .minSmallQty(row.get("min_small_qty", Double.class))
                            .maxSmallQty(row.get("max_small_qty", Double.class))
                            .balSmallQty(row.get("bal_qty", Double.class))
                            .userCode(row.get("user_code", String.class))
                            .stockName(row.get("stock_name", String.class))
                            .relCode(row.get("rel_code", String.class))
                            .relName(row.get("rel_name", String.class))
                            .locName("All")
                            .minUnitCode(stockRelationService.getRelStr(row.get("rel_code", String.class), row.get("min_small_qty", Double.class)))
                            .maxUnitCode(stockRelationService.getRelStr(row.get("rel_code", String.class), row.get("max_small_qty", Double.class)))
                            .balUnit(stockRelationService.getRelStr(row.get("rel_code", String.class), row.get("bal_qty", Double.class)))
                            .position(row.get("position",Integer.class))
                            .build())
                    .all();
        }else{
            String sql = """
                select c.*,if(bal_qty<min_small_qty,1,if(bal_qty>min_small_qty,2,if(bal_qty<max_small_qty,3,if(bal_qty> max_small_qty,4,5)))) position
                from (
                select b.*,(ifnull(b.min_qty,0)*ifnull(min.smallest_qty,1)) min_small_qty,
                (ifnull(b.max_qty,0)*ifnull(max.smallest_qty,1)) max_small_qty
                from (
                select a.*,s.user_code,s.stock_name,s.rel_code,rel.rel_name,l.loc_name
                from (
                select tmp.stock_code,tmp.loc_code,tmp.ttl_qty bal_qty,r.min_qty, r.min_unit, r.max_qty, r.max_unit,tmp.comp_code
                from tmp_stock_opening tmp
                left join reorder_level r on tmp.stock_code = r.stock_code
                and tmp.comp_code = r.comp_code
                and tmp.loc_code = r.loc_code
                where tmp.mac_id = :macId
                )a
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and s.comp_code = rel.comp_code
                ) b
                left join unit_relation_detail min on b.rel_code = min.rel_code
                and b.comp_code = min.comp_code
                and b.min_unit = min.unit
                left join unit_relation_detail max on b.rel_code = max.rel_code
                and b.comp_code = max.comp_code
                and b.max_unit = min.unit
                )c
                order by position,bal_qty
                """;
            return client.sql(sql)
                    .bind("macId", macId)
                    .map((row) -> ReorderLevel.builder()
                            .key(ReorderKey.builder()
                                    .stockCode(row.get("stock_code", String.class))
                                    .compCode(row.get("comp_code", String.class))
                                    .locCode(row.get("loc_code", String.class))
                                    .build())
                            .minQty(row.get("min_qty", Double.class))
                            .minSmallQty(row.get("min_small_qty", Double.class))
                            .minUnitCode(row.get("min_unit", String.class))
                            .maxQty(row.get("max_qty", Double.class))
                            .maxSmallQty(row.get("max_small_qty", Double.class))
                            .maxUnitCode(row.get("max_unit", String.class))
                            .balSmallQty(row.get("bal_qty", Double.class))
                            .userCode(row.get("user_code", String.class))
                            .stockName(row.get("stock_name", String.class))
                            .relCode(row.get("rel_code", String.class))
                            .relName(row.get("rel_name", String.class))
                            .locName(row.get("loc_name", String.class))
                            .balUnit(stockRelationService.getRelStr(row.get("rel_code", String.class), row.get("bal_qty", Double.class)))
                            .position(row.get("position",Integer.class))
                            .build())
                    .all();
        }

    }
}
