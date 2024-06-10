package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.Pattern;
import cv.api.entity.PatternKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PatternService {
    private final DatabaseClient client;

    public Mono<Pattern> findByCode(PatternKey key) {
        String sql = """
                select *
                from pattern
                where comp_code= :compCode
                and stock_code = :stockCode and unique_id = :uniqueId
                and f_stock_code = :mapStockCode""";
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("stockCode", key.getStockCode())
                .bind("uniqueId", key.getUniqueId())
                .bind("mapStockCode", key.getMapStockCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<Pattern> save(Pattern dto) {
        return findByCode(dto.getKey())
                .flatMap(pattern -> update(dto)).switchIfEmpty(Mono.defer(() -> insert(dto)));
    }


    public Flux<Pattern> search(String stockCode, String compCode) {
        String sql = """
                SELECT p.*, s.user_code, s.stock_name, l.loc_name, po.desp, p.price_type
                FROM pattern p
                JOIN stock s ON p.stock_code = s.stock_code AND p.comp_code = s.comp_code
                JOIN location l ON p.loc_code = l.loc_code AND p.comp_code = l.comp_code
                LEFT JOIN price_option po ON p.price_type = po.type AND p.comp_code = po.comp_code
                WHERE p.f_stock_code = :stockCode
                AND p.comp_code = :compCode
                ORDER BY p.unique_id
                """;
        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("compCode", compCode)
                .map(row -> Pattern.builder()
                        .key(PatternKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .stockCode(row.get("stock_code", String.class))
                                .mapStockCode(row.get("f_stock_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amount", Double.class))
                        .unitCode(row.get("unit", String.class))
                        .priceTypeCode(row.get("price_type", String.class))
                        .priceTypeName(row.get("desp", String.class))
                        .build())
                .all();
    }

    public Mono<Boolean> delete(PatternKey key) {
        String sql = """
                delete from pattern
                where comp_code= :compCode
                and stock_code = :stockCode and unique_id = :uniqueId
                and f_stock_code = :mapStockCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("stockCode", key.getStockCode())
                .bind("uniqueId", key.getUniqueId())
                .bind("mapStockCode", key.getMapStockCode())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<Pattern> getPattern(LocalDateTime updatedDate) {
        String sql = """
                select *
                from pattern
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Pattern> insert(Pattern dto) {
        String sql = """
                INSERT INTO pattern (stock_code, loc_code, qty, unit, price,amount, explode, f_stock_code, unique_id, comp_code, dept_id, intg_upd_status, price_type, updated_date)
                VALUES (:stockCode, :locCode, :qty, :unit, :price,:amount, :explode, :fStockCode, :uniqueId, :compCode, :deptId, :intgUpdStatus, :priceType, :updatedDate)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<Pattern> update(Pattern dto) {
        String sql = """
                UPDATE pattern
                SET loc_code = :locCode,
                    qty = :qty,
                    unit = :unit,
                    price = :price,
                    amount = :amount,
                    explode = :explode,
                    dept_id = :deptId,
                    intg_upd_status = :intgUpdStatus,
                    price_type = :priceType,
                    updated_date = :updatedDate
                WHERE stock_code = :stockCode
                AND unique_id = :uniqueId
                AND comp_code = :compCode
                AND f_stock_code = :fStockCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<Pattern> executeUpdate(String sql, Pattern dto) {
        return client.sql(sql)
                .bind("stockCode", dto.getKey().getStockCode())
                .bind("fStockCode", dto.getKey().getMapStockCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("locCode", Parameters.in(R2dbcType.VARCHAR, dto.getLocCode()))
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnitCode())
                .bind("price", dto.getPrice())
                .bind("amount", dto.getAmount())
                .bind("explode", Util1.getBoolean(dto.getExplode()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("priceType", Parameters.in(R2dbcType.VARCHAR, dto.getPriceTypeCode()))
                .bind("updatedDate", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    private Pattern mapRow(Row row) {
        return Pattern.builder()
                .key(PatternKey.builder()
                        .compCode(row.get("comp_code", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .mapStockCode(row.get("f_stock_code", String.class))
                        .uniqueId(row.get("unique_id", Integer.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .locCode(row.get("loc_code", String.class))
                .qty(row.get("qty", Double.class))
                .price(row.get("price", Double.class))
                .unitCode(row.get("unit", String.class))
                .amount(row.get("amount", Double.class))
                .priceTypeCode(row.get("price_type", String.class))
                .build();
    }

}
