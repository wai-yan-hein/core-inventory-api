package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.OrderStatus;
import cv.api.entity.OrderStatusKey;
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
public class OrderStatusService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<OrderStatus> save(OrderStatus dto) {
        String code = dto.getKey().getCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(code)) {
            return seqService.getNextCode("OrderStatus", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setCode(seqNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    public Mono<OrderStatus> insert(OrderStatus dto) {
        String sql = """
                INSERT INTO order_status (code, description, created_by, created_date,
                updated_by, updated_date, mac_id, comp_code, user_code, dept_id,
                intg_upd_status, deleted, active, order_by)
                VALUES (:code, :description, :createdBy, :createdDate, :updatedBy,
                :updatedDate, :macId, :compCode, :userCode, :deptId, :intgUpdStatus,
                :deleted, :active, :orderBy)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<OrderStatus> update(OrderStatus dto) {
        String sql = """
                UPDATE order_status
                SET description = :description, created_by = :createdBy, created_date = :createdDate,
                updated_by = :updatedBy, updated_date = :updatedDate, mac_id = :macId, user_code = :userCode,
                dept_id = :deptId, intg_upd_status = :intgUpdStatus, deleted = :deleted, active = :active, order_by = :orderBy
                WHERE code = :code AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<OrderStatus> executeUpdate(String sql, OrderStatus dto) {
        return client.sql(sql)
                .bind("code", dto.getKey().getCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("description", dto.getDescription())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("macId", dto.getMacId())
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("deptId", dto.getDeptId())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("active", Util1.getBoolean(dto.getActive()))
                .bind("orderBy", Util1.getInteger(dto.getOrderBy()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    private OrderStatus mapRow(Row row) {
        return OrderStatus.builder()
                .key(OrderStatusKey.builder()
                        .code(row.get("code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .description(row.get("description", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .macId(row.get("mac_id", Integer.class))
                .userCode(row.get("user_code", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .active(row.get("active", Boolean.class))
                .orderBy(row.get("order_by", Integer.class))
                .build();
    }

    public Flux<OrderStatus> findAll(String compCode) {
        String sql = """
                SELECT *
                FROM order_status
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(String code, String compCode) {
        String sql = """
                UPDATE order_status
                SET deleted = true, updated_date = :updatedDate
                WHERE comp_code = :compCode
                AND code = :code
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("code", code)
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<OrderStatus> findById(OrderStatusKey key) {
        String code = key.getCode();
        if (Util1.isNullOrEmpty(code)) {
            return Mono.empty();
        }
        String sql = """
                SELECT *
                FROM order_status
                WHERE comp_code = :compCode
                AND code = :code
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("code", code)
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<OrderStatus> getOrderStatus(LocalDateTime updatedDate) {
        String sql = """
                select *
                from order_status
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }
}
