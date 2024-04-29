/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.StockType;
import cv.api.entity.StockTypeKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class StockTypeService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<StockType> saveOrUpdate(StockType dto) {
        String typeCode = dto.getKey().getStockTypeCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(typeCode)) {
            return seqService.getNextCode("StockType", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setStockTypeCode(seqNo);
                        return insert(dto);
                    });
        }
        return findByCode(dto.getKey())
                .flatMap(t -> update(dto))
                .switchIfEmpty(Mono.defer(() -> insert(dto)));
    }

    public Flux<StockType> findAll(String compCode) {
        String sql = """
                select *
                from stock_type
                where comp_code =:compCode
                and deleted =false
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<StockType> findAllActive(String compCode) {
        String sql = """
                select *
                from stock_type
                where comp_code =:compCode
                and deleted =false
                and active = true
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(StockTypeKey key) {
        String sql = """
                update stock_type
                set deleted =true,updated_date=:updatedDate
                where comp_code =:compCode
                and stock_type_code =:stockTypeCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("stockTypeCode", key.getStockTypeCode())
                .fetch().rowsUpdated().thenReturn(true);
    }


    public Flux<StockType> getStockType(LocalDateTime updatedDate) {
        String sql = """
                select *
                from stock_type
                where updated_date> :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<StockType> findByCode(StockTypeKey key) {
        String typeCode = key.getStockTypeCode();
        if (Util1.isNullOrEmpty(typeCode)) {
            return Mono.empty();
        }
        String sql = """
                select *
                from stock_type
                where stock_type_code =:typeCode
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("typeCode", key.getStockTypeCode())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }


    public Mono<StockType> insert(StockType dto) {
        String sql = """
                INSERT INTO stock_type (stock_type_code, comp_code, dept_id, stock_type_name, account_id, updated_date, user_code, mac_id, created_date, created_by, updated_by, intg_upd_status, deleted, active, finished_group, group_type)
                VALUES (:stockTypeCode, :compCode, :deptId, :stockTypeName, :accountId, :updatedDate, :userCode, :macId, :createdDate, :createdBy, :updatedBy, :intgUpdStatus, :deleted, :active, :finishedGroup, :groupType)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<StockType> update(StockType dto) {
        String sql = """
                UPDATE stock_type
                SET dept_id = :deptId, stock_type_name = :stockTypeName, account_id = :accountId, updated_date = :updatedDate,
                user_code = :userCode, mac_id = :macId, created_date = :createdDate, created_by = :createdBy, updated_by = :updatedBy,
                intg_upd_status = :intgUpdStatus, deleted = :deleted, active = :active, finished_group = :finishedGroup, group_type = :groupType
                WHERE stock_type_code = :stockTypeCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<StockType> executeUpdate(String sql, StockType dto) {
        return client.sql(sql)
                .bind("stockTypeCode", dto.getKey().getStockTypeCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("stockTypeName", dto.getStockTypeName())
                .bind("accountId", Parameters.in(R2dbcType.VARCHAR, dto.getAccount()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("macId", dto.getMacId())
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("active", Util1.getBoolean(dto.getActive()))
                .bind("finishedGroup", dto.getFinishedGroup())
                .bind("groupType", Parameters.in(R2dbcType.VARCHAR, dto.getGroupType()))
                .fetch().rowsUpdated().thenReturn(dto);
    }

    private StockType mapRow(Row row) {
        return StockType.builder()
                .key(StockTypeKey.builder()
                        .stockTypeCode(row.get("stock_type_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .stockTypeName(row.get("stock_type_name", String.class))
                .account(row.get("account_id", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .userCode(row.get("user_code", String.class))
                .macId(row.get("mac_id", Integer.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .active(row.get("active", Boolean.class))
                .finishedGroup(row.get("finished_group", Boolean.class))
                .groupType(row.get("group_type", Integer.class))
                .build();
    }
    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM stock_type
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count",Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
