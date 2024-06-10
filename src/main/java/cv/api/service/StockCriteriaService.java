package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.StockCriteria;
import cv.api.entity.StockCriteriaKey;
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
public class StockCriteriaService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<StockCriteria> insert(StockCriteria dto) {
        String sql = """
                INSERT INTO stock_criteria (criteria_code, comp_code, user_code, criteria_name, created_by, created_date,
                updated_by, updated_date, active, deleted)
                VALUES (:criteriaCode, :compCode, :userCode, :criteriaName, :createdBy, :createdDate, :updatedBy,
                :updatedDate, :active, :deleted)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<StockCriteria> update(StockCriteria dto) {
        String sql = """
                UPDATE stock_criteria
                SET user_code = :userCode, criteria_name = :criteriaName, created_by = :createdBy, created_date = :createdDate,
                updated_by = :updatedBy, updated_date = :updatedDate, active = :active, deleted = :deleted
                WHERE criteria_code = :criteriaCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<StockCriteria> executeUpdate(String sql, StockCriteria dto) {
        return client.sql(sql)
                .bind("criteriaCode", dto.getKey().getCriteriaCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("userCode", dto.getUserCode())
                .bind("criteriaName", dto.getCriteriaName())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("active", Util1.getBoolean(dto.getActive()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<StockCriteria> save(StockCriteria dto) {
        String criteriaCode = dto.getKey().getCriteriaCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(criteriaCode)) {
            return seqService.getNextCode("StockCriteria", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setCriteriaCode(seqNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    private StockCriteria mapRow(Row row) {
        return StockCriteria.builder()
                .key(StockCriteriaKey.builder()
                        .criteriaCode(row.get("criteria_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .userCode(row.get("user_code", String.class))
                .criteriaName(row.get("criteria_name", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .active(row.get("active", Boolean.class))
                .deleted(row.get("deleted", Boolean.class))
                .build();
    }

    public Flux<StockCriteria> findAll(String compCode, Boolean active) {
        String sql = """
                select *
                from stock_criteria
                where comp_code = :compCode
                and active = :active
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("active", active)
                .map((row, rowMetadata) -> mapRow(row))
                .all();
    }

    public Mono<StockCriteria> search(String compCode, String text) {
        return Mono.empty();
    }

    public Mono<Boolean> delete(String code) {
        String sql = """
                update stock_criteria
                set deleted = true, updated_date = :updatedDate
                where criteria_code = :criteriaCode
                """;
        return client.sql(sql)
                .bind("criteriaCode", code)
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<Boolean> delete(StockCriteriaKey key) {
        String sql = """
                update stock_criteria
                set deleted = true, updated_date = :updatedDate
                where comp_code = :compCode
                and criteria_code = :criteriaCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("criteriaCode", key.getCriteriaCode())
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<StockCriteria> findById(StockCriteriaKey key) {
        String sql = """
                select *
                from stock_criteria
                where comp_code = :compCode
                and criteria_code = :criteriaCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("criteriaCode", key.getCriteriaCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<StockCriteria> getStockCriteria(LocalDateTime updatedDate) {
        String sql = """
                select *
                from stock_criteria
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM stock_criteria
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count", Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
