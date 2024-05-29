/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.VouStatus;
import cv.api.entity.VouStatusKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * @author wai yan
 */
@Service
@RequiredArgsConstructor
public class VouStatusService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<VouStatus> insert(VouStatus dto) {
        String sql = """
                INSERT INTO vou_status (code, comp_code, description, created_by, created_date,
                updated_by, updated_date, mac_id, user_code, dept_id, intg_upd_status, deleted,
                active, report_name, mill_report_name)
                VALUES (:code, :compCode, :description, :createdBy, :createdDate, :updatedBy,
                :updatedDate, :macId, :userCode, :deptId, :intgUpdStatus, :deleted, :active,
                :reportName, :millReportName)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<VouStatus> update(VouStatus dto) {
        String sql = """
                UPDATE vou_status
                SET description = :description, created_by = :createdBy, created_date = :createdDate,
                updated_by = :updatedBy, updated_date = :updatedDate,
                mac_id = :macId, user_code = :userCode, dept_id = :deptId, intg_upd_status = :intgUpdStatus,
                deleted = :deleted, active = :active, report_name = :reportName, mill_report_name = :millReportName
                WHERE code = :code AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<VouStatus> executeUpdate(String sql, VouStatus dto) {
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
                .bind("reportName", Parameters.in(R2dbcType.VARCHAR, dto.getReportName()))
                .bind("millReportName", Parameters.in(R2dbcType.VARCHAR, dto.getMillReportName()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<VouStatus> save(VouStatus dto) {
        String code = dto.getKey().getCode();
        String compCode = dto.getKey().getCompCode();
        if (Util1.isNullOrEmpty(code)) {
            return seqService.getNextCode("VouStatus", compCode, 5)
                    .flatMap(seqNo -> {
                        dto.getKey().setCode(seqNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    private VouStatus mapRow(Row row) {
        return VouStatus.builder()
                .key(VouStatusKey.builder()
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
                .reportName(row.get("report_name", String.class))
                .millReportName(row.get("mill_report_name", String.class))
                .build();
    }

    public Flux<VouStatus> findAll(String compCode) {
        String sql = """
                select *
                from vou_status
                where comp_code= :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row))
                .all();
    }

    public Mono<Boolean> delete(VouStatusKey key) {
        String sql = """
                update vou_status
                set deleted = true,updated_date = :updatedDate
                where comp_code =:compCode
                and code =:code
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("code", key.getCode())
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<VouStatus> findById(VouStatusKey key) {
        String sql = """
                select *
                from vou_status
                where comp_code =:compCode
                and code =:code
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("code", key.getCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }


    public Flux<VouStatus> getVouStatus(LocalDateTime updatedDate) {
        String sql = """
                select *
                from vou_status
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM vou_status
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count",Integer.class))
                .one()
                .map(count -> count > 0);
    }


}
