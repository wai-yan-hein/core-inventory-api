/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.Location;
import cv.api.entity.LocationKey;
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
import java.util.List;

/**
 * @author wai yan
 */
@Service
@RequiredArgsConstructor
public class LocationService {

    private final SeqService seqService;
    private final DatabaseClient client;
    @Transactional
    public Mono<Location> insert(Location dto) {
        String sql = """
            INSERT INTO location (loc_code, comp_code, dept_id, loc_name, parent, calc_stock, updated_date, location_type, created_date, created_by, updated_by, user_code, intg_upd_status, map_dept_id, dept_code, cash_acc, deleted, active, warehouse_code)
            VALUES (:locCode, :compCode, :deptId, :locName, :parent, :calcStock, :updatedDate, :locationType, :createdDate, :createdBy, :updatedBy, :userCode, :intgUpdStatus, :mapDeptId, :deptCode, :cashAcc, :deleted, :active, :warehouseCode)
            """;
        return executeUpdate(dto, sql);
    }

    @Transactional
    public Mono<Location> update(Location dto) {
        String sql = """
            UPDATE location
            SET dept_id = :deptId, loc_name = :locName, parent = :parent, calc_stock = :calcStock,
                updated_date = :updatedDate, location_type = :locationType, created_date = :createdDate,
                created_by = :createdBy, updated_by = :updatedBy, user_code = :userCode,
                intg_upd_status = :intgUpdStatus, map_dept_id = :mapDeptId,
                dept_code = :deptCode, cash_acc = :cashAcc, deleted = :deleted,
                active = :active, warehouse_code = :warehouseCode
            WHERE loc_code = :locCode AND comp_code = :compCode
            """;
        return executeUpdate(dto, sql);
    }

    private Mono<Location> executeUpdate(Location dto, String sql) {
        return client.sql(sql)
                .bind("locCode", dto.getKey().getLocCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("locName", dto.getLocName())
                .bind("parent", Parameters.in(R2dbcType.VARCHAR, dto.getParentCode()))
                .bind("calcStock", dto.getCalcStock())
                .bind("updatedDate", LocalDateTime.now())
                .bind("locationType", Parameters.in(R2dbcType.VARCHAR, dto.getLocationType()))
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("mapDeptId", Parameters.in(R2dbcType.INTEGER, dto.getMapDeptId()))
                .bind("deptCode", Parameters.in(R2dbcType.VARCHAR, dto.getDeptCode()))
                .bind("cashAcc", Parameters.in(R2dbcType.VARCHAR, dto.getCashAcc()))
                .bind("deleted", dto.getDeleted())
                .bind("active", dto.getActive())
                .bind("warehouseCode", Parameters.in(R2dbcType.VARCHAR, dto.getWareHouseCode()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }


    public Mono<Location> save(Location dto) {
        String locCode = dto.getKey().getLocCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(locCode)) {
            return seqService.getNextCode("Location", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setLocCode(seqNo);
                        dto.setCreatedDate(LocalDateTime.now());
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    public Flux<Location> findAll(String compCode, String whCode) {
        String sql = """
                SELECT l.*, w.description
                FROM location l LEFT JOIN warehouse w
                ON l.warehouse_code = w.code
                AND l.comp_code = w.comp_code
                WHERE l.deleted = false
                AND l.active = true
                AND l.comp_code = :compCode
                AND (l.warehouse_code = :whCode OR '-' = :whCode)
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("whCode", whCode)
                .map((row, metadata) -> Location.builder()
                        .key(LocationKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .locCode(row.get("loc_code", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .macId(row.get("mac_id", Integer.class))
                        .locName(row.get("loc_name", String.class))
                        .calcStock(row.get("calc_stock", Boolean.class))
                        .createdDate(row.get("created_date",LocalDateTime.class))
                        .createdBy(row.get("created_by", String.class))
                        .updatedBy(row.get("updated_by", String.class))
                        .userCode(row.get("user_code", String.class))
                        .deptCode(row.get("dept_code", String.class))
                        .cashAcc(row.get("cash_acc", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .active(row.get("active", Boolean.class))
                        .wareHouseCode(row.get("warehouse_code", String.class))
                        .wareHouseName(row.get("description", String.class))
                        .build())
                .all();
    }

    public Mono<Boolean> delete(LocationKey key) {
        return updateDeleteStatus(key);
    }

    private Mono<Boolean> updateDeleteStatus(LocationKey key) {
        String sql = """
                update location
                set deleted =:status,updated_date=:updatedDate
                where stock_code=:stockCode
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("status", true)
                .bind("updatedDate", LocalDateTime.now())
                .bind("stockCode", key.getLocCode())
                .bind("compCode", key.getCompCode())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<Location> getLocation(LocalDateTime updatedDate) {
        String sql = """
                select *
                from location
                where updated_date>:updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> insertTmp(List<String> listLocation, String compCode, Integer macId, String warehouse) {
        if (listLocation == null || listLocation.isEmpty() || !warehouse.equals("-")) {
            String sql = """
                    insert into f_location(f_code,mac_id)
                    select loc_code,:macId
                    from location
                    where comp_code =:compCode
                    and (warehouse_code =:whCode or '-' =:whCode)
                    """;
            return deleteTmp(macId).then(client.sql(sql)
                    .bind("compCode", compCode)
                    .bind("whCode", warehouse)
                    .bind("macId", macId)
                    .fetch().rowsUpdated().thenReturn(true));
        } else {
            return deleteTmp(macId)
                    .flatMap(aBoolean -> Flux.fromIterable(listLocation)
                            .flatMap(locCode -> {
                                String sql = """
                                        insert into f_location (f_code,mac_id)
                                        values (:locCode,:macId);
                                        """;
                                return client.sql(sql)
                                        .bind("locCode", locCode)
                                        .bind("macId", macId)
                                        .fetch()
                                        .rowsUpdated()
                                        .thenReturn(true);
                            }).then(Mono.just(true)));

        }
    }

    private Mono<Boolean> deleteTmp(int macId) {
        String sql = """
                delete from f_location where mac_id =:macId
                """;
        return client.sql(sql)
                .bind("macId", macId)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Location mapRow(Row row) {
        return Location.builder()
                .key(LocationKey.builder()
                        .locCode(row.get("loc_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .locName(row.get("loc_name", String.class))
                .parentCode(row.get("parent", String.class))
                .calcStock(row.get("calc_stock", Boolean.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .locationType(row.get("location_type", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .userCode(row.get("user_code", String.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .mapDeptId(row.get("map_dept_id", Integer.class))
                .deptCode(row.get("dept_code", String.class))
                .cashAcc(row.get("cash_acc", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .active(row.get("active", Boolean.class))
                .wareHouseCode(row.get("warehouse_code", String.class))
                .build();
    }

    public Mono<Location> findByCode(LocationKey key) {
        if (Util1.isNullOrEmpty(key.getLocCode())) {
            return Mono.empty();
        }
        String sql = """
                select *
                from location
                where comp_code =:compCode
                and loc_code =:locCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("locCode", key.getLocCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }
    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT COUNT(*)
                FROM location
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated()
                .map(count -> count > 0);
    }
}
