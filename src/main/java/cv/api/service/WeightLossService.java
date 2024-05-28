package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.*;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeightLossService {
    private final DatabaseClient client;
    private final WeightLossDetailService detailService;
    private final VouNoService vouNoService;

    public Mono<WeightLossHis> save(WeightLossHis dto) {
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        return saveOrUpdate(dto).flatMap(ri -> detailService.deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode()).flatMap(delete -> {
            List<WeightLossHisDetail> list = dto.getListDetail();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> !Util1.isNullOrEmpty(detail.getStockCode()))
                        .concatMap(detail -> {
                            if (detail.getKey() == null) {
                                detail.setKey(WeightLossHisDetailKey.builder().build());
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

    private Mono<WeightLossHis> saveOrUpdate(WeightLossHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "WEIGHT_LOSS", compCode, macId)
                    .flatMap(seqNo -> {
                        dto.getKey().setVouNo(seqNo);
                        dto.setUpdatedDate(LocalDateTime.now());
                        return insert(dto);
                    });
        } else {
            return update(dto);
        }
    }

    public Mono<WeightLossHis> insert(WeightLossHis dto) {
        String sql = """
                    INSERT INTO weight_loss_his (
                        vou_no, comp_code, dept_id, vou_date, ref_no, remark, created_by, updated_by, updated_date, mac_id, deleted
                    ) VALUES (
                        :vouNo, :compCode, :deptId, :vouDate, :refNo, :remark, :createdBy, :updatedBy, :updatedDate, :macId, :deleted
                    )
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<WeightLossHis> update(WeightLossHis dto) {
        String sql = """
                    UPDATE weight_loss_his SET
                        dept_id = :deptId,
                        vou_date = :vouDate,
                        ref_no = :refNo,
                        remark = :remark,
                        created_by = :createdBy,
                        updated_by = :updatedBy,
                        updated_date = :updatedDate,
                        mac_id = :macId,
                        deleted = :deleted
                    WHERE vou_no = :vouNo
                    AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<WeightLossHis> executeUpdate(String sql, WeightLossHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("vouDate", dto.getVouDate())
                .bind("refNo", dto.getRefNo())
                .bind("remark", dto.getRemark())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedBy", dto.getUpdatedBy())
                .bind("updatedDate", dto.getUpdatedDate())
                .bind("macId", dto.getMacId())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .fetch().rowsUpdated().thenReturn(dto);
    }

    public WeightLossHis mapRow(Row row) {
        return WeightLossHis.builder()
                .key(WeightLossHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .refNo(row.get("ref_no", String.class))
                .remark(row.get("remark", String.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .macId(row.get("mac_id", Integer.class))
                .deleted(row.get("deleted", Boolean.class))
                .build();
    }

    public Mono<WeightLossHis> findById(WeightLossHisKey key) {
        if (Util1.isNullOrEmpty(key.getVouNo())) {
            return Mono.empty();
        }
        String sql = """
                select *
                from weight_loss_his
                where comp_code = :compCode
                and vou_no = :vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<Boolean> delete(WeightLossHisKey key) {
        return updateDeleteStatus(key, true);
    }

    public Mono<Boolean> restore(WeightLossHisKey key) {
        return updateDeleteStatus(key, false);
    }


    private Mono<Boolean> updateDeleteStatus(WeightLossHisKey key, boolean status) {
        String sql = """
                update weight_loss_his
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

    public Flux<WeightLossHis> getWeightLossHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        String sql = """
                SELECT vou_no, vou_date, remark, ref_no, created_by, deleted
                FROM v_weight_loss
                WHERE deleted = :deleted
                AND vou_date BETWEEN :fromDate AND :toDate
                AND comp_code = :compCode
                AND (dept_id = :deptId OR :deptId = 0)
                AND (vou_no = :vouNo OR '-' = :vouNo)
                AND (loc_code = :locCode OR '-' = :locCode)
                AND (stock_code = :stockCode OR '-' = :stockCode)
                AND (remark LIKE :remarkPrefix)
                AND (ref_no LIKE :refNoPrefix)
                GROUP BY vou_no
                ORDER BY vou_date
                """;

        return client.sql(sql)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("vouNo", vouNo)
                .bind("locCode", locCode)
                .bind("stockCode", stockCode)
                .bind("remarkPrefix", remark + "%")
                .bind("refNoPrefix", refNo + "%")
                .map((row, rowMetadata) -> WeightLossHis.builder()
                        .key(WeightLossHisKey.builder()
                                .compCode(compCode)
                                .vouNo(row.get("vou_no", String.class))
                                .build())
                        .deptId(deptId)
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .remark(row.get("remark", String.class))
                        .refNo(row.get("ref_no", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .build())
                .all();
    }

    public Flux<WeightLossHisDetail> search(String vouNo, String compCode) {
        String sql = """
                SELECT w.*, s.user_code, s.stock_name, rel.rel_name, l.loc_name
                FROM weight_loss_his_detail w
                JOIN stock s
                ON w.stock_code = s.stock_code
                AND w.comp_code = s.comp_code
                LEFT JOIN unit_relation rel
                ON s.rel_code = rel.rel_code
                AND s.comp_code = s.comp_code
                JOIN location l
                ON w.loc_code = l.loc_code
                AND w.comp_code = l.comp_code
                WHERE w.comp_code = :compCode
                AND w.vou_no = :vouNo
                ORDER BY unique_id
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row, rowMetadata) -> WeightLossHisDetail.builder()
                        .key(WeightLossHisDetailKey.builder()
                                .vouNo(vouNo)
                                .compCode(compCode)
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .stockUserCode(row.get("user_code", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .unit(row.get("unit", String.class))
                        .price(row.get("price", Double.class))
                        .lossQty(row.get("loss_qty", Double.class))
                        .lossUnit(row.get("loss_unit", String.class))
                        .lossPrice(row.get("loss_price", Double.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .build())
                .all();
    }
}
