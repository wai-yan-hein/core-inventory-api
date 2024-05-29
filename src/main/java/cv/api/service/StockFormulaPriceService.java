package cv.api.service;

import cv.api.entity.StockFormulaPrice;
import cv.api.entity.StockFormulaPriceKey;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockFormulaPriceService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<StockFormulaPrice> insert(StockFormulaPrice dto) {
        String sql = """
                INSERT INTO stock_formula_price (formula_code, comp_code, unique_id, criteria_code, percent, price,
                percent_allow, updated_date)
                VALUES (:formulaCode, :compCode, :uniqueId, :criteriaCode, :percent, :price, :percentAllow, :updatedDate)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<StockFormulaPrice> update(StockFormulaPrice dto) {
        String sql = """
                UPDATE stock_formula_price
                SET criteria_code = :criteriaCode, percent = :percent, price = :price, percent_allow = :percentAllow, 
                updated_date = :updatedDate
                WHERE formula_code = :formulaCode AND comp_code = :compCode AND unique_id = :uniqueId
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<StockFormulaPrice> executeUpdate(String sql, StockFormulaPrice dto) {
        return client.sql(sql)
                .bind("formulaCode", dto.getKey().getFormulaCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("criteriaCode", dto.getCriteriaCode())
                .bind("percent", dto.getPercent())
                .bind("price", dto.getPrice())
                .bind("percentAllow", dto.getPercentAllow())
                .bind("updatedDate", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<StockFormulaPrice> save(StockFormulaPrice dto) {
        Integer uniqueId = dto.getKey().getUniqueId();
        if (uniqueId == null) {
            return insert(dto);
        }
        return update(dto);
    }

    private StockFormulaPrice mapRow(Row row) {
        return StockFormulaPrice.builder()
                .key(StockFormulaPriceKey.builder()
                        .formulaCode(row.get("formula_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .uniqueId(row.get("unique_id", Integer.class))
                        .build())
                .criteriaCode(row.get("criteria_code", String.class))
                .percent(row.get("percent", Double.class))
                .price(row.get("price", Double.class))
                .percentAllow(row.get("percent_allow", Double.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .build();
    }

    public Flux<StockFormulaPrice> findAll(String compCode) {
        String sql = """
                select *
                from stock_formula_price
                where comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(StockFormulaPriceKey key) {
        String sql = """
                delete from stock_formula_price
                where comp_code =:compCode
                and formula_code =:formulaCode
                and unique_id =:uniqueId
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("formulaCode", key.getFormulaCode())
                .bind("uniqueId", key.getUniqueId())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<StockFormulaPrice> findById(StockFormulaPriceKey key) {
        String sql = """
                select *
                from stock_formula_price
                where comp_code =:compCode
                and formula_code =:formulaCode
                and unique_id =:uniqueId
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("formulaCode", key.getFormulaCode())
                .bind("uniqueId", key.getUniqueId())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<StockFormulaPrice> getStockFormulaPrice(LocalDateTime updatedDate) {
        String sql = """
                select *
                from stock_formula_price
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<StockFormulaPrice> getStockFormulaPrice(String code, String compCode) {
        String sql = """
                select s.*,sc.criteria_name,sc.user_code
                from stock_formula_price s
                join stock_criteria sc on s.criteria_code = sc.criteria_code
                and s.comp_code = s.comp_code
                where s.comp_code = :compCode
                and s.formula_code = :formulaCode
                order by s.unique_id
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("formulaCode", code)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM stock_formula_price
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count", Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
