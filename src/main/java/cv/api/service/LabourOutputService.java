package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.ReturnObject;
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
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LabourOutputService {
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<LabourOutput> saveLabourOutput(LabourOutput dto) {
        return operator.transactional(Mono.defer(() -> {
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
        }));

    }

    private Mono<LabourOutput> saveOrUpdate(LabourOutput dto) {
        String vouNo = dto.getVouNo();
        String compCode = dto.getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
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

    @Transactional
    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from labour_output_detail where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    @Transactional
    public Mono<LabourOutput> insert(LabourOutput dto) {
        String sql = """
                INSERT INTO labour_output (vou_no, comp_code, dept_id, vou_date, remark, created_date,
                created_by, updated_date, updated_by, mac_id, deleted, output_qty, reject_qty, amount)
                VALUES (:vouNo, :compCode, :deptId, :vouDate, :remark, :createdDate,
                :createdBy, :updatedDate, :updatedBy, :macId, :deleted, :outputQty, :rejectQty, :amount)
                """;
        return executeUpdate(sql, dto);
    }

    @Transactional
    public Mono<LabourOutput> update(LabourOutput dto) {
        String sql = """
                UPDATE labour_output
                SET dept_id = :deptId, vou_date = :vouDate, remark = :remark,
                created_date = :createdDate, created_by = :createdBy,
                updated_date = :updatedDate, updated_by = :updatedBy, mac_id = :macId,
                deleted= :deleted, output_qty = :outputQty, reject_qty= :rejectQty, amount = :amount
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
                .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                .remark(row.get("remark", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .macId(row.get("mac_id", Integer.class))
                .deleted(row.get("deleted", Boolean.class))
                .outputQty(row.get("output_qty", Double.class))
                .rejectQty(row.get("reject_qty", Double.class))
                .amount(row.get("amount", Double.class))
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
                .bind("outputQty", Parameters.in(R2dbcType.DOUBLE, dto.getOutputQty()))
                .bind("rejectQty", Parameters.in(R2dbcType.DOUBLE, dto.getRejectQty()))
                .bind("amount", Parameters.in(R2dbcType.DOUBLE, dto.getAmount()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    @Transactional
    public Mono<LabourOutputDetail> insert(LabourOutputDetail dto) {
        String sql = """
                INSERT INTO labour_output_detail (vou_no, comp_code, unique_id, job_no, labour_code, trader_code,
                description, print_qty, output_qty, reject_qty, order_vou_no, ref_no, vou_status_code, remark, price, amount)
                VALUES (:vouNo, :compCode, :uniqueId, :jobNo, :labourCode, :traderCode, :description, :printQty,
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
                .bind("traderCode", dto.getTraderCode())
                .bind("description", Parameters.in(R2dbcType.VARCHAR, dto.getDescription()))
                .bind("printQty", Parameters.in(R2dbcType.DOUBLE, dto.getPrintQty()))
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
                update labour_output
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


    public Flux<LabourOutput> getHistory(ReportFilter filter) {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        String vouNo = Util1.isAll(filter.getVouNo());
        Integer deptId = filter.getDeptId();
        String sql = """
                select *
                from labour_output
                where comp_code = :compCode
                and deleted = :deleted
                and date(vou_date) between :fromDate and :toDate
                and (vou_no = :vouNo or '-' = :vouNo)
                and (dept_id = :deptId or '-' = :deptId)
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouNo", vouNo)
                .bind("deptId", deptId)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<LabourOutputDetail> getLabourOutputDetail(String vouNo, String compCode) {
        String sql = """
                select l.*,j.job_name,t.trader_name labour_name,tt.trader_name,v.description vou_status_name
                from labour_output_detail l join job j on l.job_no = j.job_no
                and l.comp_code = j.comp_code
                join trader t on l.labour_code = t.code
                and l.comp_code = t.comp_code
                left join vou_status v on l.vou_status_code = v.code
                and l.comp_code = v.comp_code
                join trader tt on l.trader_code = tt.code
                and l.comp_code = tt.comp_code
                where l.vou_no =:vouNo
                and l.comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> LabourOutputDetail.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .uniqueId(row.get("unique_id", Integer.class))
                        .jobNo(row.get("job_no", String.class))
                        .labourCode(row.get("labour_code", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .description(row.get("description", String.class))
                        .printQty(row.get("print_qty", Double.class))
                        .outputQty(row.get("output_qty", Double.class))
                        .rejectQty(row.get("reject_qty", Double.class))
                        .orderVouNo(row.get("order_vou_no", String.class))
                        .refNo(row.get("ref_no", String.class))
                        .vouStatusCode(row.get("vou_status_code", String.class))
                        .remark(row.get("remark", String.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amount", Double.class))
                        .jobName(row.get("job_name", String.class))
                        .labourName(row.get("labour_name", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .vouStatusName(row.get("vou_status_name", String.class))
                        .build()).all();

    }

    public Mono<ReturnObject> getLabourPaymentDetailResult(ReportFilter filter) {
        return getLabourPaymentDetail(filter)
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private Flux<LabourOutputDetail> getLabourPaymentDetail(ReportFilter filter) {
        String compCode = filter.getCompCode();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String labourCode = Util1.isAll(filter.getLabourCode());
        String sql = """
                select a.vou_date,a.description,a.print_qty,a.output_qty,a.reject_qty,a.ref_no,a.price,a.amount,
                j.job_name,t.trader_name labour_name,v.description vou_status_name,tt.trader_name
                from (
                select date(l.vou_date) vou_date,l.comp_code,ld.job_no,ld.trader_code,ld.labour_code,ld.description,ld.print_qty,
                ld.output_qty,ld.reject_qty,ld.ref_no,ld.vou_status_code,ld.price,ld.amount
                from labour_output l join labour_output_detail ld on l.vou_no =ld.vou_no
                and l.comp_code = ld.comp_code
                where l.deleted = false
                and l.comp_code =:compCode
                and date(vou_date) between :fromDate and :toDate
                and (labour_code =:labourCode or '-' =:labourCode)
                )a
                join job j on a.job_no = j.job_no
                and a.comp_code = j.comp_code
                join trader t on a.labour_code = t.code
                and a.comp_code = t.comp_code
                join trader tt on a.trader_code = tt.code
                and a.comp_code = tt.comp_code
                join vou_status v on a.vou_status_code = v.code
                and a.comp_code = v.comp_code
                order by labour_name,vou_date
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("labourCode", labourCode)
                .map((row, rowMetadata) -> LabourOutputDetail.builder()
                        .vouDateStr(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .description(row.get("description", String.class))
                        .printQty(row.get("print_qty", Double.class))
                        .outputQty(row.get("output_qty", Double.class))
                        .rejectQty(row.get("reject_qty", Double.class))
                        .refNo(row.get("ref_no", String.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amount", Double.class))
                        .jobName(row.get("job_name", String.class))
                        .labourName(row.get("labour_name", String.class))
                        .vouStatusName(row.get("vou_status_name", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .build()).all();
    }
}
