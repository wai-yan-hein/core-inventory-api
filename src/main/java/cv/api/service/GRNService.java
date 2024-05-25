package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.GRN;
import cv.api.entity.GRNDetail;
import cv.api.entity.GRNDetailKey;
import cv.api.entity.GRNKey;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GRNService {
    private final DatabaseClient client;
    private GRNDetailService detailService;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<GRN> save(GRN dto) {
        return operator.transactional(Mono.defer(() -> saveOrUpdate(dto)
                .flatMap(grn -> detailService.deleteDetail(grn.getKey().getVouNo(), grn.getKey().getCompCode())
                        .flatMap(delete -> {
                            List<GRNDetail> list = dto.getListDetail();
                            if (list != null && !list.isEmpty()) {
                                return Flux.fromIterable(list)
                                        .filter(detail -> !Util1.isNullOrEmpty(detail.getStockCode()))
                                        .concatMap(detail -> {
                                            if (detail.getKey() == null) {
                                                detail.setKey(GRNDetailKey.builder().build());
                                            }
                                            int uniqueId = list.indexOf(detail) + 1;
                                            detail.getKey().setUniqueId(uniqueId);
                                            detail.getKey().setVouNo(grn.getKey().getVouNo());
                                            detail.getKey().setCompCode(grn.getKey().getCompCode());
                                            detail.setDeptId(grn.getDeptId());
                                            return detailService.insert(detail);
                                        })
                                        .then(Mono.just(grn));
                            } else {
                                return Mono.just(grn);
                            }
                        }))));
    }

    private Mono<GRN> saveOrUpdate(GRN dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "GRN", compCode, macId)
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

    public Mono<Boolean> open(GRNKey key) {
        String sql = """
                UPDATE grn
                SET closed = false, updated_date = :updatedDate
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("updatedDate", LocalDateTime.now())
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(true);
    }


    public Mono<GRN> insert(GRN dto) {
        String sql = """
                INSERT INTO grn (vou_no, comp_code, dept_id, vou_date, trader_code, closed, created_date, created_by, updated_date, updated_by, deleted, batch_no, remark, mac_id, loc_code)
                VALUES (:vouNo, :compCode, :deptId, :vouDate, :traderCode, :closed, :createdDate, :createdBy, :updatedDate, :updatedBy, :deleted, :batchNo, :remark, :macId, :locCode)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<GRN> update(GRN dto) {
        String sql = """
                UPDATE grn
                SET dept_id = :deptId, vou_date = :vouDate, trader_code = :traderCode, closed = :closed, created_date = :createdDate, created_by = :createdBy, updated_date = :updatedDate, updated_by = :updatedBy, deleted = :deleted, batch_no = :batchNo, remark = :remark, mac_id = :macId, loc_code = :locCode
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<GRN> executeUpdate(String sql, GRN dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("vouDate", dto.getVouDate())
                .bind("traderCode", dto.getTraderCode())
                .bind("closed", Util1.getBoolean(dto.getClosed()))
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedDate", dto.getUpdatedDate())
                .bind("updatedBy", dto.getUpdatedBy())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("batchNo", dto.getBatchNo())
                .bind("remark", dto.getRemark())
                .bind("macId", dto.getMacId())
                .bind("locCode", dto.getLocCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<Boolean> delete(GRNKey key) {
        return updateDeleteStatus(key, true);
    }

    private Mono<Boolean> updateDeleteStatus(GRNKey key, boolean status) {
        String sql = """
                UPDATE grn
                SET deleted = :status, updated_date = :updatedDate
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("status", status)
                .bind("updatedDate", LocalDateTime.now())
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(true);
    }

    public Mono<Boolean> restore(GRNKey key) {
        return updateDeleteStatus(key, false);
    }

    public Mono<GRN> findById(GRNKey key) {
        String sql = """
                SELECT *
                FROM grn
                WHERE comp_code = :compCode AND vou_no = :vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row))
                .one();
    }

    private GRN mapRow(Row row) {
        return GRN.builder()
                .key(GRNKey.builder().vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class)).build())
                .deptId(row.get("dept_id", Integer.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .traderCode(row.get("trader_code", String.class))
                .closed(row.get("closed", Boolean.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .batchNo(row.get("batch_no", String.class))
                .remark(row.get("remark", String.class))
                .macId(row.get("mac_id", Integer.class))
                .locCode(row.get("loc_code", String.class))
                .build();
    }

    public Flux<GRN> getGRNHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String batchNo = Util1.isNull(filter.getBatchNo(), "-");
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        String close = String.valueOf(filter.isClose());
        String sql = """
                select a.*,t.user_code,t.trader_name
                from (
                select vou_date,g.vou_no,g.comp_code,g.dept_id,g.loc_code,g.created_by,g.batch_no,remark,g.trader_code,g.deleted,g.closed
                from grn g join grn_detail gd
                on g.vou_no = gd.vou_no
                and g.comp_code = gd.comp_code
                where date(vou_date) between :fromDate and :toDate
                and g.comp_code =:compCode
                and g.deleted =:deleted
                and g.closed =:close
                and (g.dept_id =:deptId or 0 =:deptId)
                and (g.batch_no =:batchNo or '-' =:batchNo)
                and (g.vou_no =:vouNo or '-' =:vouNo)
                and (g.trader_code =:traderCode or '-' =:traderCode)
                and (g.batch_no =:batchNo or '-' =:batchNo)
                and (g.remark REGEXP :remark or '-' =:remark)
                and (g.created_by =:userCode or '-' =:userCode)
                and (gd.stock_code =:stockCode or '-' =:stockCode)
                and (gd.loc_code =:locCode or '-' =:locCode)
                group by g.vou_no
                )a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by g.vou_date desc
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("deleted", deleted)
                .bind("close", close)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("batchNo", batchNo)
                .bind("vouNo", vouNo)
                .bind("traderCode", traderCode)
                .bind("remark", remark)
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("locCode", locCode)
                .map(row -> GRN.builder()
                        .key(GRNKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .batchNo(row.get("batch_no", String.class))
                        .remark(row.get("remark", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .traderUserCode(row.get("user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .closed(row.get("closed", Boolean.class))
                        .createdBy(row.get("created_by", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .build())
                .all();
    }

    public Flux<GRN> search(String batchNo, String compCode) {
        String sql = """
                select a.batch_no,t.trader_name,a.vou_no
                from (
                select batch_no,trader_code,comp_code,dept_id,vou_no
                from grn
                where comp_code=:compCode
                and (dept_id =:deptId or 0=:deptId)
                and deleted = false
                and closed =0
                and batch_no like :batchNo
                order by batch_no
                limit 20
                )a
                join trader t on
                a.trader_code = t.code
                and a.comp_code = t.comp_code
                and a.dept_id = t.dept_id""";

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("batchNo", batchNo + "%")
                .map(row -> GRN.builder()
                        .key(GRNKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .batchNo(row.get("batch_no", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .build())
                .all();
    }

    public Flux<GRNDetail> getGRNDetail(String vouNo, String compCode) {
        return detailService.getDetail(vouNo,compCode);
    }
}
