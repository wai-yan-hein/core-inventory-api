/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.RelationKey;
import cv.api.entity.UnitRelation;
import cv.api.entity.UnitRelationDetail;
import cv.api.entity.UnitRelationDetailKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UnitRelationService {
    private final DecimalFormat formatter = new DecimalFormat(Util1.DECIMAL_FORMAT);
    private final SeqService seqService;
    private final DatabaseClient client;

    private Mono<UnitRelation> saveOrUpdate(UnitRelation dto) {
        String relCode = dto.getKey().getRelCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(relCode)) {
            return seqService.getNextCode("Unit", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setRelCode(seqNo);
                        return insert(dto);
                    });
        }
        return findByCode(dto.getKey())
                .flatMap(t -> update(dto))
                .switchIfEmpty(Mono.defer(() -> insert(dto)));
    }

    public Mono<UnitRelation> save(UnitRelation dto) {
        List<UnitRelationDetail> detail = generateDetail(dto);
        dto.setRelName(getRelStr(detail));
        return saveOrUpdate(dto).thenMany(Flux.fromIterable(detail).flatMap(this::insert)).then(Mono.just(dto));
    }

    private List<UnitRelationDetail> generateDetail(UnitRelation dto) {
        List<UnitRelationDetail> detail = dto.getDetailList();
        detail.removeIf(rd -> rd.getUnit() == null);
        int size = detail.size();
        //cal smallest
        if (size > 0) {
            while (size != 0) {
                int lastIndex = size - 1;
                // last index
                if (lastIndex == detail.size() - 1) {
                    UnitRelationDetail ud = detail.get(lastIndex);
                    UnitRelationDetailKey key = new UnitRelationDetailKey(lastIndex + 1, dto.getKey().getRelCode(), dto.getKey().getCompCode());
                    ud.setKey(key);
                    ud.setDeptId(dto.getDeptId());
                    ud.setSmallestQty(1.0);
                } else {
                    UnitRelationDetail rd = detail.get(size - 1);
                    UnitRelationDetailKey key = new UnitRelationDetailKey(lastIndex + 1, dto.getKey().getRelCode(), dto.getKey().getCompCode());
                    rd.setKey(key);
                    rd.setDeptId(dto.getDeptId());
                    double qty = detail.get(size).getQty();
                    double small = detail.get(size).getSmallestQty();
                    rd.setSmallestQty(qty * small);
                }
                size--;
            }
        }
        return detail;
    }



    private String getRelStr(List<UnitRelationDetail> listRD) {
        StringBuilder relStr = new StringBuilder();
        for (UnitRelationDetail ud : listRD) {
            relStr.append(String.format("%s%s%s", formatter.format(ud.getQty()), ud.getUnit(), "*"));
        }
        String str = relStr.toString();
        return str.substring(0, str.length() - 1);
    }

    public Flux<UnitRelation> getRelation(String compCode) {
        String sql = """
                select *
                from unit_relation
                where comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }


    public Flux<UnitRelationDetail> getRelationDetail(String relCode, String compCode) {
        String sql = """
                select *
                from unit_relation_detail
                where comp_code =:compCode
                and rel_code =:relCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("relCode", relCode)
                .map((row, metadata) -> UnitRelationDetail.builder()
                        .key(UnitRelationDetailKey.builder()
                                .relCode(row.get("rel_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .unit(row.get("unit", String.class))
                        .qty(row.get("qty", Double.class))
                        .smallestQty(row.get("smallest_qty", Double.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .build())
                .all();
    }

    public Mono<UnitRelation> findByCode(RelationKey key) {
        String relCode = key.getRelCode();
        if (Util1.isNullOrEmpty(relCode)) {
            return Mono.empty();
        }
        String sql = """
                select *
                from unit_relation
                where rel_code =:relCode
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("relCode", key.getRelCode())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }


    public Flux<UnitRelation> getUnitRelation(LocalDateTime updatedDate) {
        String sql = """
                SELECT *
                FROM unit_relation
                WHERE updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all()
                .flatMap(t -> {
                    String relCode = t.getKey().getRelCode();
                    String compCode = t.getKey().getCompCode();
                    return getRelationDetail(relCode, compCode).collectList()
                            .map(details -> {
                                t.setDetailList(details);
                                return t;
                            });
                });
    }
    public Flux<UnitRelation> getUnitRelationAndDetail(String compCode) {
        String sql = """
                SELECT *
                FROM unit_relation
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all()
                .flatMap(t -> {
                    String relCode = t.getKey().getRelCode();
                    return getRelationDetail(relCode, compCode).collectList()
                            .map(details -> {
                                t.setDetailList(details);
                                return t;
                            });
                });
    }


    public Mono<UnitRelation> insert(UnitRelation dto) {
        String sql = """
                INSERT INTO unit_relation (rel_code, comp_code, dept_id, rel_name, intg_upd_status, updated_date)
                VALUES (:relCode, :compCode, :deptId, :relName, :intgUpdStatus, :updatedDate)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<UnitRelation> update(UnitRelation dto) {
        String sql = """
                UPDATE unit_relation
                SET dept_id = :deptId, rel_name = :relName, intg_upd_status = :intgUpdStatus, updated_date = :updatedDate
                WHERE rel_code = :relCode AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<UnitRelation> executeUpdate(String sql, UnitRelation dto) {
        return client.sql(sql)
                .bind("relCode", dto.getKey().getRelCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("relName", dto.getRelName())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(dto);
    }

    private UnitRelation mapRow(Row row) {
        return UnitRelation.builder()
                .key(RelationKey.builder()
                        .relCode(row.get("rel_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .relName(row.get("rel_name", String.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .build();
    }

    public Mono<UnitRelationDetail> insert(UnitRelationDetail dto) {
        String sql = """
                INSERT INTO unit_relation_detail (rel_code, unique_id, comp_code, unit, qty, smallest_qty, dept_id)
                VALUES (:relCode, :uniqueId, :compCode, :unit, :qty, :smallestQty, :deptId)
                """;
        return client
                .sql(sql)
                .bind("relCode", dto.getKey().getRelCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("unit", dto.getUnit())
                .bind("qty", dto.getQty())
                .bind("smallestQty", dto.getSmallestQty())
                .bind("deptId", dto.getDeptId())
                .fetch().rowsUpdated().thenReturn(dto);
    }
}
