package cv.api.service;

import cv.api.entity.PriceOption;
import cv.api.entity.PriceOptionKey;
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
public class PriceOptionService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<PriceOption> insert(PriceOption dto) {
        String sql = """
                INSERT INTO price_option (type, desp, comp_code, unique_id, dept_id, tran_option, updated_date)
                VALUES (:type, :desp, :compCode, :uniqueId, :deptId, :tranOption, :updatedDate)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<PriceOption> update(PriceOption dto) {
        String sql = """
                UPDATE price_option
                SET desp = :desp, unique_id = :uniqueId, tran_option = :tranOption, updated_date = :updatedDate
                WHERE type = :type AND comp_code = :compCode AND dept_id = :deptId
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<PriceOption> executeUpdate(String sql, PriceOption dto) {
        return client.sql(sql)
                .bind("type", dto.getKey().getPriceType())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getKey().getDeptId())
                .bind("desp", dto.getDescription())
                .bind("uniqueId", dto.getUniqueId())
                .bind("tranOption", Parameters.in(R2dbcType.VARCHAR, dto.getTranOption()))
                .bind("updatedDate", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<PriceOption> save(PriceOption dto) {
        return findById(dto.getKey())
                .flatMap(existingDto -> update(dto))
                .switchIfEmpty(insert(dto));
    }

    private PriceOption mapRow(Row row) {
        return PriceOption.builder()
                .key(PriceOptionKey.builder()
                        .priceType(row.get("type", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .build())
                .description(row.get("desp", String.class))
                .uniqueId(row.get("unique_id", Integer.class))
                .tranOption(row.get("tran_option", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .build();
    }

    public Flux<PriceOption> findAll(String compCode) {
        String sql = """
                SELECT *
                FROM price_option
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row))
                .all();
    }

    public Mono<Boolean> delete(PriceOptionKey key) {
        String sql = """
                DELETE FROM price_option
                WHERE type = :type AND comp_code = :compCode AND dept_id = :deptId
                """;
        return client.sql(sql)
                .bind("type", key.getPriceType())
                .bind("compCode", key.getCompCode())
                .bind("deptId", key.getDeptId())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<PriceOption> findById(PriceOptionKey key) {
        String sql = """
                SELECT *
                FROM price_option
                WHERE type = :type AND comp_code = :compCode AND dept_id = :deptId
                """;
        return client.sql(sql)
                .bind("type", key.getPriceType())
                .bind("compCode", key.getCompCode())
                .bind("deptId", key.getDeptId())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<PriceOption> getPriceOptions(String option, String compCode, Integer deptId) {
        String sql = """
                SELECT *
                FROM price_option
                WHERE tran_option = :option
                and compCode = :compCode
                and deptId = :deptId
                """;
        return client.sql(sql)
                .bind("option", option)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<PriceOption> getPriceOption(LocalDateTime updatedDate) {
        String sql = """
                SELECT *
                FROM price_option
                WHERE updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) AS count
                FROM price_option
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count", Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
