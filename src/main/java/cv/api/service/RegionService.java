package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.Region;
import cv.api.entity.RegionKey;
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
public class RegionService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<Region> save(Region dto) {
        String code = dto.getKey().getRegCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(code)) {
            return seqService.getNextCode("Region", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setRegCode(seqNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    public Mono<Region> insert(Region dto) {
        String sql = """
                INSERT INTO region (reg_code, mac_id, reg_name, created_by, created_date,
                updated_by, updated_date, comp_code, user_code, dept_id, deleted, active)
                VALUES (:regCode, :macId, :regName, :createdBy, :createdDate, :updatedBy,
                :updatedDate, :compCode, :userCode, :deptId, :deleted, :active)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<Region> update(Region dto) {
        String sql = """
                UPDATE region
                SET reg_name = :regName, created_by = :createdBy, created_date = :createdDate,
                updated_by = :updatedBy, updated_date = :updatedDate,
                comp_code = :compCode, user_code = :userCode, dept_id = :deptId,
                deleted = :deleted, active = :active, mac_id = :macId
                WHERE reg_code = :regCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<Region> executeUpdate(String sql, Region dto) {
        return client.sql(sql)
                .bind("regCode", dto.getKey().getRegCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("macId", dto.getMacId())
                .bind("regName", dto.getRegionName())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("deptId", dto.getDeptId())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("active", Util1.getBoolean(dto.getActive()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    private Region mapRow(Row row) {
        return Region.builder()
                .key(RegionKey.builder()
                        .regCode(row.get("reg_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .macId(row.get("mac_id", Integer.class))
                .regionName(row.get("reg_name", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .userCode(row.get("user_code", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .deleted(row.get("deleted", Boolean.class))
                .active(row.get("active", Boolean.class))
                .build();
    }

    public Flux<Region> findAll(String compCode) {
        String sql = """
                SELECT *
                FROM region
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(RegionKey key) {
        String sql = """
                UPDATE region
                SET deleted = true, updated_date = :updatedDate
                WHERE comp_code = :compCode
                AND reg_code = :regCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("regCode", key.getRegCode())
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<Region> findById(RegionKey key) {
        if (Util1.isNullOrEmpty(key.getRegCode())) {
            return Mono.empty();
        }
        String sql = """
                SELECT *
                FROM region
                WHERE comp_code = :compCode
                AND reg_code = :regCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("regCode", key.getRegCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<Region> getRegion(LocalDateTime updatedDate) {
        String sql = """
                select *
                from region
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }
}
