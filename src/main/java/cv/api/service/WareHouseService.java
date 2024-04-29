package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.WareHouse;
import cv.api.entity.WareHouseKey;
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
public class WareHouseService {

    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<WareHouse> insert(WareHouse dto) {
        String sql = """
                INSERT INTO warehouse (code, description, created_by, created_date, updated_by, updated_date, comp_code, user_code, active, deleted)
                VALUES (:code, :description, :createdBy, :createdDate, :updatedBy, :updatedDate, :compCode, :userCode, :active, :deleted)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<WareHouse> update(WareHouse dto) {
        String sql = """
                UPDATE warehouse
                SET description = :description,created_by = :createdBy, created_date = :createdDate, updated_by = :updatedBy, updated_date = :updatedDate, user_code = :userCode, active = :active, deleted = :deleted
                WHERE code = :code AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }


    private Mono<WareHouse> executeUpdate(String sql, WareHouse dto) {
        return client.sql(sql)
                .bind("code", dto.getKey().getCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("description", dto.getDescription())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("active", Util1.getBoolean(dto.getActive()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<WareHouse> save(WareHouse dto) {
        String locCode = dto.getKey().getCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(locCode)) {
            return seqService.getNextCode("WareHouse", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setCode(seqNo);
                        dto.setCreatedDate(LocalDateTime.now());
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    public Flux<WareHouse> findAll(String compCode) {
        String sql = """
                select *
                from warehouse
                where comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public WareHouse mapRow(Row row) {
        return WareHouse.builder()
                .key(WareHouseKey.builder()
                        .code(row.get("code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .description(row.get("description", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .userCode(row.get("user_code", String.class))
                .active(row.get("active", Boolean.class))
                .deleted(row.get("deleted", Boolean.class))
                .build();
    }


    public Mono<Boolean> delete(WareHouseKey key) {
        String sql = """
                update warehouse
                set deleted = true
                where comp_code =:compCode
                and code =:code
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("code", key.getCode())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<WareHouse> findById(WareHouseKey key) {
        String sql = """
                select *
                from warehouse
                where comp_code =:compCode
                and code =:code
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("code", key.getCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<WareHouse> getWarehouse(LocalDateTime updatedDate) {
        String sql = """
                select *
                from warehouse
                where updated_date>:updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

}
