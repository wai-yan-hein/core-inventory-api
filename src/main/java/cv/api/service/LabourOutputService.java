package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.LabourOutput;
import cv.api.entity.LabourOutputDetail;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LabourOutputService {
    private final DatabaseClient client;
    private final VouNoService vouNoService;

    public Mono<LabourOutput> saveLabourOutput(LabourOutput dto) {
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        return saveOrUpdate(dto).flatMap(ri -> deleteDetail(ri.getVouNo(), ri.getCompCode()).flatMap(delete -> {
            List<LabourOutputDetail> list = dto.getListDetail();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> Util1.getDouble(detail.getOutputQty()) != 0)
                        .concatMap(detail -> {
                            int uniqueId = list.indexOf(detail) + 1;
                            detail.setUniqueId(uniqueId);
                            detail.setVouNo(ri.getVouNo());
                            detail.setCompCode(ri.getCompCode());
                            return insert(detail);
                        })
                        .then(Mono.just(ri));
            } else {
                return Mono.just(ri);
            }
        }));
    }

    private Mono<LabourOutput> saveOrUpdate(LabourOutput dto) {
        String vouNo = dto.getVouNo();
        String compCode = dto.getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "LabourOutput", compCode, macId)
                    .flatMap(seqNo -> {
                        dto.setVouNo(seqNo);
                        dto.setCreatedDate(LocalDateTime.now());
                        dto.setUpdatedDate(LocalDateTime.now());
                        return insert(dto);
                    });
        } else {
            return update(dto);
        }
    }

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from labour_output_detail where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<LabourOutput> insert(LabourOutput dto) {
        String sql = """
                INSERT INTO labour_output (vou_no, comp_code, dept_id, vou_date, remark, created_date,
                created_by, updated_date, updated_by, mac_id, deleted)
                VALUES (:vouNo, :compCode, :deptId, :vouDate, :remark, :createdDate,
                :createdBy, :updatedDate, :updatedBy, :macId, :deleted)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<LabourOutput> update(LabourOutput dto) {
        String sql = """
                UPDATE labour_output
                SET dept_id = :deptId, vou_date = :vouDate, remark = :remark,
                created_date = :createdDate, created_by = :createdBy,
                updated_date = :updatedDate, updated_by = :updatedBy, mac_id = :macId,
                deleted= :deleted
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private LabourOutput mapRow(Row row) {
        return LabourOutput.builder()
                .vouNo(row.get("vou_no", String.class))
                .compCode(row.get("comp_code", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .remark(row.get("remark", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .macId(row.get("mac_id", Integer.class))
                .deleted(row.get("deleted", Boolean.class))
                .build();
    }

    private Mono<LabourOutput> executeUpdate(String sql, LabourOutput dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getVouNo())
                .bind("compCode", dto.getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("vouDate", dto.getVouDate())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedDate", LocalDateTime.now())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("macId", dto.getMacId())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<LabourOutputDetail> insert(LabourOutputDetail dto) {
        String sql = """
                INSERT INTO labour_output_detail (vou_no, comp_code, unique_id, job_no, labour_code,
                description, output_qty, reject_qty, order_vou_no, ref_no, vou_status_code, remark, price, amount)
                VALUES (:vouNo, :compCode, :uniqueId, :jobNo, :labourCode, :description,
                :outputQty, :rejectQty, :orderVouNo, :refNo, :vouStatusCode, :remark, :price, :amount)
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<LabourOutputDetail> executeUpdate(String sql, LabourOutputDetail dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getVouNo())
                .bind("compCode", dto.getCompCode())
                .bind("uniqueId", dto.getUniqueId())
                .bind("jobNo", dto.getJobNo())
                .bind("labourCode", dto.getLabourCode())
                .bind("description", Parameters.in(R2dbcType.VARCHAR, dto.getDescription()))
                .bind("outputQty", dto.getOutputQty())
                .bind("rejectQty", Parameters.in(R2dbcType.DOUBLE, dto.getRejectQty()))
                .bind("orderVouNo", Parameters.in(R2dbcType.VARCHAR, dto.getOrderVouNo()))
                .bind("refNo", Parameters.in(R2dbcType.VARCHAR, dto.getRefNo()))
                .bind("vouStatusCode", Parameters.in(R2dbcType.VARCHAR, dto.getVouStatusCode()))
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("price", Parameters.in(R2dbcType.DOUBLE, dto.getPrice()))
                .bind("amount", Parameters.in(R2dbcType.DOUBLE, dto.getAmount()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<LabourOutput> findById(String vouNo, String compCode) {
        String sql = """
                select *
                from labour_output
                where comp_code = :compCode
                and vou_no = :vouNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<Boolean> delete(String vouNo, String compCode) {
        return updateDeleteStatus(vouNo, compCode, true);
    }

    public Mono<Boolean> restore(String vouNo, String compCode) {
        return updateDeleteStatus(vouNo, compCode, false);
    }

    @Transactional
    private Mono<Boolean> updateDeleteStatus(String vouNo, String compCode, boolean status) {
        String sql = """
                update labour_payment
                set deleted =:status,updated_date=:updatedDate
                where vou_no=:vouNo
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("status", status)
                .bind("updatedDate", LocalDateTime.now())
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }


}
