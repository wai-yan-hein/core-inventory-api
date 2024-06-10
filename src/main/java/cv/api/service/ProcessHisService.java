package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.ProcessHis;
import cv.api.entity.ProcessHisDetail;
import cv.api.entity.ProcessHisDetailKey;
import cv.api.entity.ProcessHisKey;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessHisService {
    private final DatabaseClient client;
    private final ProcessHisDetailService detailService;
    private final VouNoService vouNoService;


    public Mono<ProcessHis> save(ProcessHis dto) {
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        return saveOrUpdate(dto).flatMap(ri -> detailService.deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode()).flatMap(delete -> {
            List<ProcessHisDetail> list = dto.getListDetail();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> !Util1.isNullOrEmpty(detail.getStockCode()))
                        .concatMap(detail -> {
                            if (detail.getKey() == null) {
                                detail.setKey(ProcessHisDetailKey.builder().build());
                            }
                            int uniqueId = list.indexOf(detail) + 1;
                            detail.getKey().setUniqueId(uniqueId);
                            detail.getKey().setVouNo(ri.getKey().getVouNo());
                            detail.getKey().setCompCode(ri.getKey().getCompCode());
                            return detailService.insert(detail);
                        })
                        .then(Mono.just(ri));
            } else {
                return Mono.just(ri);
            }
        }));
    }

    private Mono<ProcessHis> saveOrUpdate(ProcessHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "PROCESS", compCode, macId)
                    .flatMap(seqNo -> {
                        dto.getKey().setVouNo(seqNo);
                        return insert(dto);
                    });
        } else {
            return update(dto);
        }
    }

    public Mono<ProcessHis> insert(ProcessHis dto) {
        String sql = """
                    INSERT INTO process_his (
                        vou_no, stock_code, comp_code, dept_id, loc_code, vou_date, end_date, unit, qty, avg_qty, price, avg_price, remark, process_no, pt_code, finished, deleted, created_by, updated_by, mac_id
                    ) VALUES (
                        :vouNo, :stockCode, :compCode, :deptId, :locCode, :vouDate, :endDate, :unit, :qty, :avgQty, :price, :avgPrice, :remark, :processNo, :ptCode, :finished, :deleted, :createdBy, :updatedBy, :macId
                    )
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<ProcessHis> update(ProcessHis dto) {
        String sql = """
                    UPDATE process_his SET
                        dept_id = :deptId,
                        loc_code = :locCode,
                        vou_date = :vouDate,
                        end_date = :endDate,
                        unit = :unit,
                        qty = :qty,
                        avg_qty = :avgQty,
                        price = :price,
                        avg_price = :avgPrice,
                        remark = :remark,
                        process_no = :processNo,
                        pt_code = :ptCode,
                        finished = :finished,
                        deleted = :deleted,
                        created_by = :createdBy,
                        updated_by = :updatedBy,
                        mac_id = :macId
                    WHERE vou_no = :vouNo
                    AND stock_code = :stockCode
                    AND comp_code = :compCode
                    AND dept_id = :deptId
                    AND loc_code = :locCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<ProcessHis> executeUpdate(String sql, ProcessHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("stockCode", dto.getStockCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("locCode", dto.getLocCode())
                .bind("vouDate", dto.getVouDate())
                .bind("endDate", dto.getEndDate())
                .bind("unit", dto.getUnit())
                .bind("qty", dto.getQty())
                .bind("avgQty", dto.getAvgQty())
                .bind("price", dto.getPrice())
                .bind("avgPrice", dto.getAvgPrice())
                .bind("remark", dto.getRemark())
                .bind("processNo", dto.getProcessNo())
                .bind("ptCode", dto.getPtCode())
                .bind("finished", dto.getFinished())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedBy", dto.getUpdatedBy())
                .bind("macId", dto.getMacId())
                .fetch().rowsUpdated().thenReturn(dto);
    }

    public ProcessHis mapRow(Row row) {
        return ProcessHis.builder()
                .key(ProcessHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .locCode(row.get("loc_code", String.class))
                .stockCode(row.get("stock_code", String.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .endDate(row.get("end_date", LocalDateTime.class))
                .unit(row.get("unit", String.class))
                .qty(row.get("qty", Double.class))
                .avgQty(row.get("avg_qty", Double.class))
                .price(row.get("price", Double.class))
                .avgPrice(row.get("avg_price", Double.class))
                .remark(row.get("remark", String.class))
                .processNo(row.get("process_no", String.class))
                .ptCode(row.get("pt_code", String.class))
                .finished(row.get("finished", Boolean.class))
                .deleted(row.get("deleted", Boolean.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .macId(row.get("mac_id", Integer.class))
                .build();
    }

    public Mono<ProcessHis> findById(ProcessHisKey key) {
        if (Util1.isNullOrEmpty(key.getVouNo())) {
            return Mono.empty();
        }
        String sql = """
                SELECT *
                FROM process_his
                WHERE comp_code = :compCode
                AND vou_no = :vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<Boolean> delete(ProcessHisKey key) {
        return updateDeleteStatus(key, true);
    }

    public Mono<Boolean> restore(ProcessHisKey key) {
        return updateDeleteStatus(key, false);
    }

    private Mono<Boolean> updateDeleteStatus(ProcessHisKey key, boolean status) {
        String sql = """
                UPDATE process_his
                SET deleted = :status, updated_date = :updatedDate
                WHERE vou_no = :vouNo
                AND comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("status", status)
                .bind("updatedDate", LocalDateTime.now())
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<ProcessHisDetail> search(String vouNo, String compCode) {
        return detailService.search(vouNo, compCode);
    }


    public Mono<ProcessHisDetail> update(ProcessHisDetail p) {
        return detailService.update(p);
    }
}
