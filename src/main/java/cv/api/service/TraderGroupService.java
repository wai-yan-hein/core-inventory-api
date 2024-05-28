package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.TraderGroup;
import cv.api.entity.TraderGroupKey;
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
public class TraderGroupService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<TraderGroup> save(TraderGroup dto) {
        String code = dto.getKey().getGroupCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(code)) {
            return seqService.getNextCode("TraderGroup", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setGroupCode(seqNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    public Mono<TraderGroup> insert(TraderGroup dto) {
        String sql = """
                INSERT INTO trader_group (group_code, comp_code, user_code, group_name,
                dept_id, intg_upd_status, account)
                VALUES (:groupCode, :compCode, :userCode, :groupName, :deptId, :intgUpdStatus, :account)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<TraderGroup> update(TraderGroup dto) {
        String sql = """
                UPDATE trader_group
                SET user_code = :userCode, group_name = :groupName, dept_id = :deptId,
                intg_upd_status = :intgUpdStatus, account = :account
                WHERE group_code = :groupCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<TraderGroup> executeUpdate(String sql, TraderGroup dto) {
        return client.sql(sql)
                .bind("groupCode", dto.getKey().getGroupCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("groupName", dto.getGroupName())
                .bind("deptId", dto.getDeptId())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("account", Parameters.in(R2dbcType.VARCHAR, dto.getAccount()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    private TraderGroup mapRow(Row row) {
        return TraderGroup.builder()
                .key(TraderGroupKey.builder()
                        .groupCode(row.get("group_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .userCode(row.get("user_code", String.class))
                .groupName(row.get("group_name", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .account(row.get("account", String.class))
                .build();
    }

    public Flux<TraderGroup> findAll(String compCode) {
        String sql = """
                SELECT *
                FROM trader_group
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(String groupCode, String compCode) {
        String sql = """
                DELETE FROM trader_group
                WHERE group_code = :groupCode
                AND comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("groupCode", groupCode)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<TraderGroup> findById(TraderGroupKey key) {
        String groupCode = key.getGroupCode();
        if (Util1.isNullOrEmpty(groupCode)) {
            return Mono.empty();
        }
        String sql = """
                SELECT *
                FROM trader_group
                WHERE group_code = :groupCode
                AND comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("groupCode", groupCode)
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<TraderGroup> getTraderGroup(LocalDateTime updatedDate) {
        String sql = """
                select *
                from trader_group
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }
}
