package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.StockFormula;
import cv.api.entity.StockFormulaKey;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockFormulaService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<StockFormula> insert(StockFormula dto) {
        String sql = """
                INSERT INTO stock_formula (formula_code, comp_code, user_code, formula_name, created_by, created_date,
                updated_by, updated_date, active, deleted, qty)
                VALUES (:formulaCode, :compCode, :userCode, :formulaName, :createdBy, :createdDate, :updatedBy, :updatedDate,
                :active, :deleted, :qty)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<StockFormula> update(StockFormula dto) {
        String sql = """
                UPDATE stock_formula
                SET user_code = :userCode, formula_name = :formulaName, created_by = :createdBy, created_date = :createdDate,
                updated_by = :updatedBy, updated_date = :updatedDate, active = :active, deleted = :deleted, qty = :qty
                WHERE formula_code = :formulaCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<StockFormula> executeUpdate(String sql, StockFormula dto) {
        return client.sql(sql)
                .bind("formulaCode", dto.getKey().getFormulaCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("userCode", dto.getUserCode())
                .bind("formulaName", dto.getFormulaName())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", dto.getUpdatedBy())
                .bind("updatedDate", LocalDateTime.now())
                .bind("active", Util1.getBoolean(dto.isActive()))
                .bind("deleted", dto.isDeleted())
                .bind("qty", dto.getQty())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<StockFormula> save(StockFormula dto) {
        String formulaCode = dto.getKey() == null ? null : dto.getKey().getFormulaCode();
        if (Util1.isNullOrEmpty(formulaCode)) {
            String compCode = dto.getKey().getCompCode();
            return seqService.getNextCode("StockFormula", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setFormulaCode(seqNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    private StockFormula mapRow(Row row) {
        return StockFormula.builder()
                .key(StockFormulaKey.builder()
                        .formulaCode(row.get("formula_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .userCode(row.get("user_code", String.class))
                .formulaName(row.get("formula_name", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .active(row.get("active", Boolean.class))
                .deleted(row.get("deleted", Boolean.class))
                .qty(row.get("qty", Double.class))
                .build();
    }

    public Flux<StockFormula> findAll(String compCode) {
        String sql = """
                select *
                from stock_formula
                where comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(StockFormulaKey key) {
        String sql = """
                update stock_formula
                set deleted = true, updated_date = :updatedDate
                where comp_code =:compCode
                and formula_code =:formulaCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("formulaCode", key.getFormulaCode())
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<StockFormula> findById(StockFormulaKey key) {
        String sql = """
                select *
                from stock_formula
                where comp_code =:compCode
                and formula_code =:formulaCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("formulaCode", key.getFormulaCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<StockFormula> getStockFormula(LocalDateTime updatedDate) {
        String sql = """
                select *
                from stock_formula
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM stock_formula
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count", Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
