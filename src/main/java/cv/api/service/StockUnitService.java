/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.StockUnit;
import cv.api.entity.StockUnitKey;
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
@RequiredArgsConstructor
public class StockUnitService {

    private final DatabaseClient client;

    public Flux<StockUnit> findAll(String compCode) {
        String sql = """
                select *
                from stock_unit
                where comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }


    public Flux<StockUnit> getUnit(LocalDateTime updatedDate) {
        String sql = """
                select *
                from stock_unit
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<StockUnit> findByCode(StockUnitKey key) {
        String unitCode = key.getUnitCode();
        if (Util1.isNullOrEmpty(unitCode)) {
            return Mono.empty();
        }
        String sql = """
                select *
                from stock_unit
                where comp_code =:compCode
                and unit_code =:unitCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("unitCode", key.getUnitCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    @Transactional
    public Mono<StockUnit> insert(StockUnit dto) {
        String sql = """
                INSERT INTO stock_unit (unit_code, unit_name, updated_date, comp_code, created_date, mac_id,
                                        created_by, updated_by, user_code, dept_id, intg_upd_status)
                VALUES (:unitCode, :unitName, :updatedDate, :compCode, :createdDate, :macId,
                        :createdBy, :updatedBy, :userCode, :deptId, :intgUpdStatus)
                """;
        return executeUpdate(sql, dto);
    }

    @Transactional
    public Mono<StockUnit> update(StockUnit dto) {
        String sql = """
                UPDATE stock_unit
                SET dept_id = :deptId,unit_name = :unitName, updated_date = :updatedDate, comp_code = :compCode,
                    created_date = :createdDate, mac_id = :macId, created_by = :createdBy,
                    updated_by = :updatedBy, user_code = :userCode, dept_id = :deptId,
                    intg_upd_status = :intgUpdStatus
                WHERE unit_code = :unitCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<StockUnit> executeUpdate(String sql, StockUnit dto) {
        return client
                .sql(sql)
                .bind("unitCode", dto.getKey().getUnitCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("unitName", Parameters.in(R2dbcType.VARCHAR, dto.getUnitName()))
                .bind("updatedDate", dto.getUpdatedDate())
                .bind("createdDate", dto.getCreatedDate())
                .bind("macId", dto.getMacId())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("deptId", dto.getDeptId())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .fetch().rowsUpdated().thenReturn(dto);
    }

    private StockUnit mapRow(Row row) {
        return StockUnit.builder()
                .key(StockUnitKey.builder()
                        .unitCode(row.get("unit_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .unitName(row.get("unit_name", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .macId(row.get("mac_id", Integer.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .userCode(row.get("user_code", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .build();
    }
    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM stock_unit
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count",Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
