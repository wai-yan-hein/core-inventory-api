/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.Category;
import cv.api.entity.CategoryKey;
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
public class CategoryService {

    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<Category> saveOrUpdate(Category dto) {
        String typeCode = dto.getKey().getCatCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(typeCode)) {
            return seqService.getNextCode("Category", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setCatCode(seqNo);
                        return insert(dto);
                    });
        }
        return findByCode(dto.getKey())
                .flatMap(t -> update(dto))
                .switchIfEmpty(Mono.defer(() -> insert(dto)));
    }

    public Flux<Category> findAll(String compCode) {
        String sql = """
                select *
                from category
                where comp_code =:compCode
                and deleted =false
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<Category> findAllActive(String compCode) {
        String sql = """
                select *
                from category
                where comp_code =:compCode
                and deleted =false
                and active = true
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(CategoryKey key) {
        String sql = """
                update category
                set deleted =true,updated_date=:updatedDate
                where comp_code =:compCode
                and stock_type_code =:stockTypeCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("stockTypeCode", key.getCatCode())
                .fetch().rowsUpdated().thenReturn(true);
    }


    public Flux<Category> getCategory(LocalDateTime updatedDate) {
        String sql = """
                select *
                from category
                where updated_date> :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Category> findByCode(CategoryKey key) {
        String catCode = key.getCatCode();
        if (Util1.isNullOrEmpty(catCode)) {
            return Mono.empty();
        }
        String sql = """
                select *
                from category
                where comp_code =:compCode
                and cat_code =:catCode
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("catCode", key.getCatCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<Category> insert(Category dto) {
        String sql = """
                INSERT INTO category (cat_code, comp_code, dept_id, mac_id, cat_name, mig_id, updated_date, created_date, created_by, updated_by, user_code, intg_upd_status, deleted, active)
                VALUES (:catCode, :compCode, :deptId, :macId, :catName, :migId, :updatedDate, :createdDate, :createdBy, :updatedBy, :userCode, :intgUpdStatus, :deleted, :active)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<Category> update(Category dto) {
        String sql = """
                UPDATE category
                SET dept_id = :deptId, mac_id = :macId, cat_name = :catName, mig_id = :migId, updated_date = :updatedDate,
                created_date = :createdDate, created_by = :createdBy, updated_by = :updatedBy, user_code = :userCode,
                intg_upd_status = :intgUpdStatus, deleted = :deleted, active = :active
                WHERE cat_code = :catCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<Category> executeUpdate(String sql, Category dto) {
        return client.sql(sql)
                .bind("catCode", dto.getKey().getCatCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("macId", dto.getMacId())
                .bind("catName", dto.getCatName())
                .bind("migId", Parameters.in(R2dbcType.VARCHAR, dto.getMigId()))
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

    private Category mapRow(Row row) {
        return Category.builder()
                .key(CategoryKey.builder()
                        .catCode(row.get("cat_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .macId(row.get("mac_id", Integer.class))
                .catName(row.get("cat_name", String.class))
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
                FROM category
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count", Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
