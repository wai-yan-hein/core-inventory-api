package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.ConsignHis;
import cv.api.entity.ConsignHisDetail;
import cv.api.entity.ConsignHisDetailKey;
import cv.api.entity.ConsignHisKey;
import cv.api.model.VConsign;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ConsignService {
    public final ConsignDetailService detailService;
    private final DatabaseClient client;
    private final VouNoService vouNoService;

    public Mono<ConsignHis> save(ConsignHis dto) {
        return saveOrUpdate(dto).flatMap(ri -> detailService.deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode()).flatMap(delete -> {
            List<ConsignHisDetail> list = dto.getListDetail();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> Util1.getDouble(detail.getBag()) != 0)
                        .concatMap(detail -> {
                            if (detail.getKey() == null) {
                                detail.setKey(ConsignHisDetailKey.builder().build());
                            }
                            int uniqueId = list.indexOf(detail) + 1;
                            detail.getKey().setUniqueId(uniqueId);
                            detail.getKey().setVouNo(ri.getKey().getVouNo());
                            detail.getKey().setCompCode(ri.getKey().getCompCode());
                            detail.setDeptId(ri.getDeptId());
                            detail.setLocCode(ri.getLocCode());
                            return detailService.insert(detail);
                        })
                        .then(Mono.just(ri));
            } else {
                return Mono.just(ri);
            }
        }));
    }

    private Mono<ConsignHis> saveOrUpdate(ConsignHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        int tranSource = dto.getTranSource();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            String option = tranSource == 1 ? "I" : "R";
            return vouNoService.getVouNo(deptId, "CONSIGN-" + option, compCode, macId)
                    .flatMap(seqNo -> {
                        dto.getKey().setVouNo(option + "-" + seqNo);
                        dto.setCreatedDate(LocalDateTime.now());
                        dto.setUpdatedDate(LocalDateTime.now());
                        return insert(dto);
                    });
        } else {
            return update(dto);
        }
    }

    public ConsignHis mapRow(Row row) {
        return ConsignHis.builder()
                .key(ConsignHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .locCode(row.get("loc_code", String.class))
                .description(row.get("description", String.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .remark(row.get("remark", String.class))
                .macId(row.get("mac_id", Integer.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .labourGroupCode(row.get("labour_group_code", String.class))
                .receivedName(row.get("received_name", String.class))
                .receivedPhoneNo(row.get("received_phone", String.class))
                .carNo(row.get("car_no", String.class))
                .traderCode(row.get("trader_code", String.class))
                .printCount(row.get("print_count", Integer.class))
                .tranSource(row.get("tran_source", Integer.class))
                .build();
    }

    public Mono<ConsignHis> findById(ConsignHisKey key) {
        String sql = """
                select *
                from consign_his
                where comp_code =:compCode
                and vou_no =:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }


    public Mono<Boolean> delete(ConsignHisKey key) {
        return updateDeleteStatus(key, true);
    }

    private Mono<Boolean> updateDeleteStatus(ConsignHisKey key, boolean status) {
        String sql = """
                update consign_his
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

    public Mono<Boolean> restore(ConsignHisKey key) {
        return updateDeleteStatus(key, false);
    }

    public Flux<ConsignHisDetail> getConsignDetail(String vouNo, String compCode) {
        return detailService.getConsignDetail(vouNo, compCode);
    }


    public Mono<ConsignHis> insert(ConsignHis dto) {
        String sql = """
                INSERT INTO consign_his (vou_no, comp_code, dept_id, loc_code, description, vou_date, remark, mac_id, created_date, created_by, updated_date, updated_by, deleted, intg_upd_status, labour_group_code, received_name, received_phone, car_no, trader_code, print_count, tran_source)
                VALUES (:vouNo, :compCode, :deptId, :locCode, :description, :vouDate, :remark, :macId, :createdDate, :createdBy, :updatedDate, :updatedBy, :deleted, :intgUpdStatus, :labourGroupCode, :receivedName, :receivedPhone, :carNo, :traderCode, :printCount, :tranSource)
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<ConsignHis> update(ConsignHis dto) {
        String sql = """
                UPDATE consign_his
                SET dept_id = :deptId, loc_code = :locCode, description = :description, vou_date = :vouDate,
                remark = :remark, mac_id = :macId, created_date = :createdDate, created_by = :createdBy, updated_date = :updatedDate,
                updated_by = :updatedBy, deleted = :deleted, intg_upd_status = :intgUpdStatus, labour_group_code = :labourGroupCode,
                received_name = :receivedName, received_phone = :receivedPhone, car_no = :carNo, trader_code = :traderCode,
                print_count = :printCount, tran_source = :tranSource
                WHERE vou_no = :vouNo and comp_code =:compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<ConsignHis> executeUpdate(String sql, ConsignHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("locCode", dto.getLocCode())
                .bind("description", Parameters.in(R2dbcType.VARCHAR, dto.getDescription()))
                .bind("vouDate", dto.getVouDate())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("macId", dto.getMacId())
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedDate", LocalDateTime.now())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("deleted", dto.getDeleted())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("labourGroupCode", Parameters.in(R2dbcType.VARCHAR, dto.getLabourGroupCode()))
                .bind("receivedName", Parameters.in(R2dbcType.VARCHAR, dto.getReceivedName()))
                .bind("receivedPhone", Parameters.in(R2dbcType.VARCHAR, dto.getReceivedPhoneNo()))
                .bind("carNo", Parameters.in(R2dbcType.VARCHAR, dto.getCarNo()))
                .bind("traderCode", dto.getTraderCode())
                .bind("printCount", Parameters.in(R2dbcType.INTEGER, dto.getPrintCount()))
                .bind("tranSource", dto.getTranSource())
                .fetch().rowsUpdated().thenReturn(dto);
    }

    public Flux<VConsign> getConsignHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        int tranSource = Integer.parseInt(Util1.isNull(filter.getTranSource(), "-"));
        String sql = """
                SELECT v.vou_date, v.vou_no, v.stock_code, s.stock_name, v.remark, v.created_by,
                v.deleted, v.dept_id, l.loc_name AS loc_name, t.trader_name, v.labour_group_code, SUM(bag) AS bag
                FROM v_consign v
                JOIN location l ON v.loc_code = l.loc_code AND v.comp_code = l.comp_code
                JOIN stock s ON v.stock_code = s.stock_code AND v.comp_code = s.comp_code
                LEFT JOIN trader t ON v.trader_code = t.code AND v.comp_code = t.comp_code
                WHERE v.comp_code = :compCode
                AND v.deleted = :deleted
                AND (v.dept_id = :deptId OR 0 = :deptId)
                AND (v.tran_source = :transSource OR 0 = :transSource)
                AND DATE(v.vou_date) BETWEEN :fromDate AND :toDate
                AND (v.vou_no = :vouNo OR '-' = :vouNo)
                AND (v.remark REGEXP :remark or '-' = :remark)
                AND (v.stock_code = :stockCode OR '-' = :stockCode)
                AND (v.trader_code = :traderCode OR '-' = :traderCode)
                AND (v.loc_code = :locCode OR '-' = :locCode)
                GROUP BY v.vou_no
                ORDER BY v.vou_date DESC
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .bind("deptId", deptId)
                .bind("transSource", tranSource)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .bind("locCode", locCode)
                .map((row, metadata) -> VConsign.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouNo(row.get("vou_no", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .remark(row.get("remark", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .location(row.get("loc_name", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .traderName(row.get("trader_name", String.class))
                        .bag(row.get("bag", Double.class))
                        .build())
                .all();
    }
}
