package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.LabourGroup;
import cv.api.entity.LabourGroupKey;
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
public class LabourGroupService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<LabourGroup> save(LabourGroup dto) {
        String code = dto.getKey().getCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(code)) {
            return seqService.getNextCode("LabourGroup", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setCode(seqNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    public Mono<LabourGroup> insert(LabourGroup dto) {
        String sql = """
                INSERT INTO labour_group (code, labour_name, created_by, created_date, updated_by, updated_date, comp_code, user_code, active, member_count, deleted, qty, price)
                VALUES (:code, :labourName, :createdBy, :createdDate, :updatedBy, :updatedDate, :compCode, :userCode, :active, :memberCount, :deleted, :qty, :price)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<LabourGroup> update(LabourGroup dto) {
        String sql = """
                UPDATE labour_group
                SET labour_name = :labourName, created_by = :createdBy, created_date = :createdDate, updated_by = :updatedBy, updated_date = :updatedDate, comp_code = :compCode, user_code = :userCode, active = :active, member_count = :memberCount, deleted = :deleted, qty = :qty, price = :price
                WHERE code = :code AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<LabourGroup> executeUpdate(String sql, LabourGroup dto) {
        return client.sql(sql)
                .bind("code", dto.getKey().getCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("labourName", dto.getLabourName())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("userCode", dto.getUserCode())
                .bind("active", Util1.getBoolean(dto.getActive()))
                .bind("memberCount", Util1.getInteger(dto.getMemberCount()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("qty", Parameters.in(R2dbcType.DOUBLE, dto.getQty()))
                .bind("price", Parameters.in(R2dbcType.DOUBLE, dto.getPrice()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public LabourGroup mapRow(Row row) {
        return LabourGroup.builder()
                .key(LabourGroupKey.builder()
                        .code(row.get("code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .labourName(row.get("labour_name", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .userCode(row.get("user_code", String.class))
                .active(row.get("active", Boolean.class))
                .memberCount(row.get("member_count", Integer.class))
                .deleted(row.get("deleted", Boolean.class))
                .qty(row.get("qty", Double.class))
                .price(row.get("price", Double.class))
                .build();
    }

    public Flux<LabourGroup> getLabourGroup(LocalDateTime updatedDate) {
        String sql = """
                select *
                from labour_group
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<LabourGroup> findAll(String compCode) {
        String sql = """
                select *
                from labour_group
                where comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<LabourGroup> findById(LabourGroupKey key) {
        String sql = """
                select *
                from labour_group
                where comp_code = :compCode
                and code = :code
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("code", key.getCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }
}