package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.*;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MillingService {
    private final DatabaseClient client;
    private final MillingExpenseService expenseService;
    private final MillingUsageService usageService;
    private final MillingOutputService outputService;
    private final MillingRawService rawService;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<MillingHis> save(MillingHis sh) {
        Integer deptId = sh.getDeptId();
        if (deptId == null) {
            log.error("deptId is null from mac id : {}", sh.getMacId());
            return Mono.empty();
        }
        return operator.transactional(Mono.defer(() -> saveMilling(sh)
                .flatMap((mh) -> saveRaw(mh)
                        .then(saveExpense(mh))
                        .then(saveOutput(mh))
                        .then(saveUsage(mh))
                        .thenReturn(mh))));
    }

    private Mono<MillingHis> saveMilling(MillingHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            return vouNoService.getVouNo(deptId, "Milling", compCode, macId)
                    .flatMap(seqNo -> {
                        dto.getKey().setVouNo(seqNo);
                        dto.setCreatedDate(LocalDateTime.now());
                        dto.setUpdatedDate(LocalDateTime.now());
                        return insert(dto);
                    });
        } else {
            return update(dto);
        }
    }


    private Mono<Boolean> saveOutput(MillingHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<MillingOutDetail> list = sh.getListOutput();
        if (list != null) {
            return outputService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> !Util1.isNullOrEmpty(e.getStockCode()))
                    .flatMap(e -> {
                        if (e.getKey() == null) {
                            e.setKey(MillingOutDetailKey.builder().build());
                        }
                        int uniqueId = list.indexOf(e) + 1;
                        e.getKey().setUniqueId(uniqueId);
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        e.setDeptId(sh.getDeptId());
                        e.setLocCode(sh.getLocCode());
                        return outputService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }

    private Mono<Boolean> saveRaw(MillingHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<MillingRawDetail> list = sh.getListRaw();
        if (list != null) {
            return rawService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> !Util1.isNullOrEmpty(e.getStockCode()))
                    .flatMap(e -> {
                        if (e.getKey() == null) {
                            e.setKey(MillingRawDetailKey.builder().build());
                        }
                        int uniqueId = list.indexOf(e) + 1;
                        e.getKey().setUniqueId(uniqueId);
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        e.setDeptId(sh.getDeptId());
                        e.setLocCode(sh.getLocCode());
                        return rawService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }

    private Mono<Boolean> saveUsage(MillingHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<MillingUsage> list = sh.getListUsage();
        if (list != null) {
            return usageService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> !Util1.isNullOrEmpty(e.getStockCode()))
                    .flatMap(e -> {
                        if (e.getKey() == null) {
                            e.setKey(MillingUsageKey.builder().build());
                        }
                        int uniqueId = list.indexOf(e) + 1;
                        e.getKey().setUniqueId(uniqueId);
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        return usageService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }

    private Mono<Boolean> saveExpense(MillingHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<MillingExpense> list = sh.getListExpense();
        if (list != null) {
            return expenseService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> Util1.getDouble(e.getAmount()) > 0 && e.getKey().getExpenseCode() != null)
                    .flatMap(e -> {
                        if (e.getKey() == null) {
                            e.setKey(MillingExpenseKey.builder().build());
                        }
                        int uniqueId = list.indexOf(e) + 1;
                        e.getKey().setUniqueId(uniqueId);
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        return expenseService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }

    public Mono<MillingHis> insert(MillingHis dto) {
        String sql = """
                INSERT INTO milling_his (vou_no, comp_code, trader_code, vou_date, cur_code, remark, created_date, created_by, deleted, updated_by, mac_id, intg_upd_status, reference, dept_id, vou_lock, project_no, car_no, vou_status_id, load_qty, load_weight, load_amount, load_expense, load_cost, output_qty, output_weight, output_amount, diff_weight, loc_code, diff_qty, percent_weight, percent_qty, job_no, print_count)
                VALUES (:vouNo, :compCode, :traderCode, :vouDate, :curCode, :remark, :createdDate, :createdBy, :deleted, :updatedBy, :macId, :intgUpdStatus, :reference, :deptId, :vouLock, :projectNo, :carNo, :vouStatusId, :loadQty, :loadWeight, :loadAmount, :loadExpense, :loadCost, :outputQty, :outputWeight, :outputAmount, :diffWeight, :locCode, :diffQty, :percentWeight, :percentQty, :jobNo, :printCount)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<MillingHis> update(MillingHis dto) {
        String sql = """
                UPDATE milling_his
                SET trader_code = :traderCode, vou_date = :vouDate, cur_code = :curCode, remark = :remark, created_date = :createdDate, created_by = :createdBy, deleted = :deleted, updated_by = :updatedBy, mac_id = :macId, intg_upd_status = :intgUpdStatus, reference = :reference, dept_id = :deptId, vou_lock = :vouLock, project_no = :projectNo, car_no = :carNo, vou_status_id = :vouStatusId, load_qty = :loadQty, load_weight = :loadWeight, load_amount = :loadAmount, load_expense = :loadExpense, load_cost = :loadCost, output_qty = :outputQty, output_weight = :outputWeight, output_amount = :outputAmount, diff_weight = :diffWeight, loc_code = :locCode, diff_qty = :diffQty, percent_weight = :percentWeight, percent_qty = :percentQty, job_no = :jobNo, print_count = :printCount
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<MillingHis> executeUpdate(String sql, MillingHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("traderCode", dto.getTraderCode())
                .bind("vouDate", dto.getVouDate())
                .bind("curCode", dto.getCurCode())
                .bind("remark", dto.getRemark())
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("macId", dto.getMacId())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("reference", dto.getReference())
                .bind("deptId", dto.getDeptId())
                .bind("vouLock", Util1.getBoolean(dto.getVouLock()))
                .bind("projectNo", Parameters.in(R2dbcType.VARCHAR, dto.getProjectNo()))
                .bind("carNo", Parameters.in(R2dbcType.VARCHAR, dto.getCarNo()))
                .bind("vouStatusId", Parameters.in(R2dbcType.VARCHAR, dto.getVouStatusId()))
                .bind("loadQty", dto.getLoadQty())
                .bind("loadWeight", dto.getLoadWeight())
                .bind("loadAmount", dto.getLoadAmount())
                .bind("loadExpense", dto.getLoadExpense())
                .bind("loadCost", dto.getLoadCost())
                .bind("outputQty", dto.getOutputQty())
                .bind("outputWeight", dto.getOutputWeight())
                .bind("outputAmount", dto.getOutputAmount())
                .bind("diffWeight", dto.getDiffWeight())
                .bind("locCode", dto.getLocCode())
                .bind("diffQty", dto.getDiffQty())
                .bind("percentWeight", dto.getPercentWeight())
                .bind("percentQty", dto.getPercentQty())
                .bind("jobNo", Parameters.in(R2dbcType.VARCHAR, dto.getJobNo()))
                .bind("printCount", Util1.getInteger(dto.getPrintCount()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<Boolean> delete(MillingHisKey key) {
        return updateDeleteStatus(key, true);
    }

    private Mono<Boolean> updateDeleteStatus(MillingHisKey key, boolean status) {
        String sql = """
                update milling_his
                set deleted =:status,updated_date=:updatedDate
                where vou_no=:vouNo
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("status", status)
                .bind("updatedDate", LocalDateTime.now())
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .fetch().rowsUpdated().thenReturn(true);
    }


    public Mono<Boolean> restore(MillingHisKey key) {
        return updateDeleteStatus(key, false);
    }

    public Mono<MillingHis> findById(MillingHisKey key) {
        String sql = """
                select *
                from milling_his
                where comp_code = :compCode
                and vou_no = :vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row)).one();

    }

    private MillingHis mapRow(Row row) {
        return MillingHis.builder()
                .key(MillingHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .traderCode(row.get("trader_code", String.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .curCode(row.get("cur_code", String.class))
                .remark(row.get("remark", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .macId(row.get("mac_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .reference(row.get("reference", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .vouLock(row.get("vou_lock", Boolean.class))
                .projectNo(row.get("project_no", String.class))
                .carNo(row.get("car_no", String.class))
                .vouStatusId(row.get("vou_status_id", String.class))
                .loadQty(row.get("load_qty", Double.class))
                .loadWeight(row.get("load_weight", Double.class))
                .loadAmount(row.get("load_amount", Double.class))
                .loadExpense(row.get("load_expense", Double.class))
                .loadCost(row.get("load_cost", Double.class))
                .outputQty(row.get("output_qty", Double.class))
                .outputWeight(row.get("output_weight", Double.class))
                .outputAmount(row.get("output_amount", Double.class))
                .diffWeight(row.get("diff_weight", Double.class))
                .locCode(row.get("loc_code", String.class))
                .diffQty(row.get("diff_qty", Double.class))
                .percentWeight(row.get("percent_weight", Double.class))
                .percentQty(row.get("percent_qty", Double.class))
                .jobNo(row.get("job_no", String.class))
                .printCount(row.get("print_count", Integer.class))
                .build();
    }

    public Flux<MillingHis> getMillingHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String reference = Util1.isNull(filter.getReference(), "-");
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        Integer deptId = filter.getDeptId();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        String jobNo = Util1.isNull(filter.getJobNo(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String sql = """
                select a.*,t.trader_name, v.description
                 from (
                 select vou_date vou_date,vou_no,remark,created_by,reference,vou_status_id, trader_code,comp_code,dept_id
                 from milling_his p
                 where comp_code = :compCode
                 and (dept_id = :deptId or 0 = :deptId)
                 and deleted = :deleted
                 and date(vou_date) between :fromDate and :toDate
                 and cur_code = :curCode
                 and (vou_no = :vouNo or '-' = :vouNo)
                 and (remark LIKE CONCAT(:remark, '%') or '-'= :remark)
                 and (reference LIKE CONCAT(:reference, '%') or '-'= :reference)
                 and (trader_code = :traderCode or '-'= :traderCode)
                 and (loc_code = :locCode or '-'= :locCode)
                 and (created_by = :userCode or '-'= :userCode)
                 and (project_no = :projectNo or '-' = :projectNo)
                 and (job_no = :jobNo or '-' = :jobNo)
                 group by vou_no)a
                 join trader t on a.trader_code = t.code
                 and a.comp_code = t.comp_code
                 join vou_status v on a.vou_status_id = v.code
                 and a.comp_code = v.comp_code
                 order by vou_date desc""";

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("reference", reference)
                .bind("traderCode", traderCode)
                .bind("userCode", userCode)
                .bind("projectNo", projectNo)
                .bind("jobNo", jobNo)
                .bind("locCode", locCode)
                .map((row, rowMetadata) -> MillingHis.builder()
                        .key(MillingHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .vouDateStr(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .traderName(row.get("trader_name", String.class))
                        .processType(row.get("description", String.class))
                        .remark(row.get("remark", String.class))
                        .reference(row.get("reference", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .build())
                .all();
    }

    public Flux<MillingRawDetail> getRawDetail(String vouNo, String compCode) {
        return rawService.search(vouNo, compCode);
    }

    public Flux<MillingExpense> getMillingExpense(String vouNo, String compCode) {
        return expenseService.search(vouNo, compCode);
    }

    public Flux<MillingOutDetail> getOutputDetail(String vouNo, String compCode) {
        return outputService.search(vouNo, compCode);
    }

    public Flux<MillingUsage> getUsageDetail(String vouNo, String compCode) {
        return usageService.getMillingUsage(vouNo, compCode);
    }
}

