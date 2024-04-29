/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.StockBrand;
import cv.api.entity.StockBrandKey;
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
public class StockBrandService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<StockBrand> saveOrUpdate(StockBrand dto) {
        String typeCode = dto.getKey().getBrandCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(typeCode)) {
            return seqService.getNextCode("StockBrand", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setBrandCode(seqNo);
                        return insert(dto);
                    });
        }
        return findByCode(dto.getKey())
                .flatMap(t -> update(dto))
                .switchIfEmpty(Mono.defer(() -> insert(dto)));
    }

    public Flux<StockBrand> findAll(String compCode) {
        String sql = """
                select *
                from stock_brand
                where comp_code =:compCode
                and deleted =false
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<StockBrand> findAllActive(String compCode) {
        String sql = """
                select *
                from stock_brand
                where comp_code =:compCode
                and deleted =false
                and active = true
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(StockBrandKey key) {
        String sql = """
                update stock_brand
                set deleted =true,updated_date=:updatedDate
                where comp_code =:compCode
                and brand_code =:stockTypeCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("stockTypeCode", key.getBrandCode())
                .fetch().rowsUpdated().thenReturn(true);
    }


    public Flux<StockBrand> getStockBrand(LocalDateTime updatedDate) {
        String sql = """
                select *
                from stock_brand
                where updated_date> :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<StockBrand> findByCode(StockBrandKey key) {
        String brandCode = key.getBrandCode();
        if (Util1.isNullOrEmpty(brandCode)) {
            return Mono.empty();
        }
        String sql = """
                select *
                from stock_brand
                where brand_code =:typeCode
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("typeCode", key.getBrandCode())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<StockBrand> insert(StockBrand dto) {
        String sql = """
                INSERT INTO stock_brand (brand_code, comp_code, dept_id, mac_id, brand_name, mig_id, updated_date, created_date, created_by, updated_by, user_code, intg_upd_status, deleted, active)
                VALUES (:brandCode, :compCode, :deptId, :macId, :brandName, :migId, :updatedDate, :createdDate, :createdBy, :updatedBy, :userCode, :intgUpdStatus, :deleted, :active)
                """;
        return executeUpdate(sql, dto);
    }
    public Mono<StockBrand> update(StockBrand dto) {
        String sql = """
            UPDATE stock_brand
            SET dept_id = :deptId, mac_id = :macId, brand_name = :brandName, mig_id = :migId, updated_date = :updatedDate,
            created_date = :createdDate, created_by = :createdBy, updated_by = :updatedBy, user_code = :userCode,
            intg_upd_status = :intgUpdStatus, deleted = :deleted, active = :active
            WHERE brand_code = :brandCode AND comp_code = :compCode
            """;
        return executeUpdate(sql, dto);
    }

    private Mono<StockBrand> executeUpdate(String sql, StockBrand dto) {
        return client.sql(sql)
                .bind("brandCode", dto.getKey().getBrandCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("macId", dto.getMacId())
                .bind("brandName", dto.getBrandName())
                .bind("migId", Parameters.in(R2dbcType.INTEGER, dto.getMigId()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("userCode", Parameters.in(R2dbcType.VARCHAR, dto.getUserCode()))
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("active", Util1.getBoolean(dto.getActive()))
                .fetch().rowsUpdated().thenReturn(dto);
    }
    private StockBrand mapRow(Row row){
        return StockBrand.builder()
                .key(StockBrandKey.builder()
                        .brandCode(row.get("brand_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .macId(row.get("mac_id", Integer.class))
                .brandName(row.get("brand_name", String.class))
                .migId(row.get("mig_id", Integer.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .userCode(row.get("user_code", String.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .active(row.get("active", Boolean.class))
                .build();
    }
    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM stock_brand
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count",Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
