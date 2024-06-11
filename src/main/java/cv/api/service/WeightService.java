package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisDetail;
import cv.api.entity.WeightHisDetailKey;
import cv.api.entity.WeightHisKey;
import cv.api.exception.ResponseUtil;
import cv.api.model.WeightColumn;
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

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeightService {
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<WeightHis> save(WeightHis dto) {
        return isValid(dto).flatMap(his -> operator.transactional(Mono.defer(() -> {
            his.setVouDate(Util1.toDateTime(his.getVouDate()));
            return saveOrUpdate(his).flatMap(ri -> deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode()).flatMap(delete -> {
                List<WeightHisDetail> list = his.getListDetail();
                return Flux.fromIterable(list)
                        .filter(detail -> Util1.getDouble(detail.getWeight()) != 0)
                        .concatMap(detail -> {
                            if (detail.getKey() == null) {
                                detail.setKey(WeightHisDetailKey.builder().build());
                            }
                            int uniqueId = list.indexOf(detail) + 1;
                            detail.getKey().setUniqueId(uniqueId);
                            detail.getKey().setVouNo(ri.getKey().getVouNo());
                            detail.getKey().setCompCode(ri.getKey().getCompCode());
                            return insert(detail);
                        })
                        .then(Mono.just(ri));
            }));
        })));
    }

    private Mono<WeightHis> isValid(WeightHis sh) {
        List<WeightHisDetail> list = Util1.nullToEmpty(sh.getListDetail());
        list.removeIf(t -> Util1.getDouble(t.getWeight()) == 0);
        if (list.isEmpty()) {
            return ResponseUtil.createBadRequest("Detail is null/empty");
        } else if (Util1.isNullOrEmpty(sh.getDeptId())) {
            return ResponseUtil.createBadRequest("deptId is null from mac id : " + sh.getMacId());
        } else if (Util1.isNullOrEmpty(sh.getVouDate())) {
            return ResponseUtil.createBadRequest("Voucher Date is null");
        }
        return Mono.just(sh);
    }

    private Mono<WeightHis> saveOrUpdate(WeightHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "WEIGHT", compCode, macId)
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


    public Mono<WeightHis> findById(WeightHisKey key) {
        String sql = """
                select *
                from weight_his
                where comp_code =:compCode
                and vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }


    public Mono<Boolean> delete(WeightHisKey key) {
        return updateDeleteStatus(key, true);
    }

    public Mono<Boolean> restore(WeightHisKey key) {
        return updateDeleteStatus(key, false);
    }


    public Flux<WeightHis> getWeightHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String tranSource = Util1.isNull(filter.getTranSource(), "-");
        boolean draft = filter.isDraft();
        String sql = """
                SELECT a.*, s.user_code AS s_user_code, s.stock_name, t.user_code AS t_user_code, t.trader_name
                FROM (
                    SELECT *
                    FROM weight_his
                    WHERE comp_code = :compCode
                    AND deleted = :deleted
                    AND draft = :draft
                    AND date(vou_date) BETWEEN :fromDate AND :toDate
                    AND (trader_code = :traderCode OR '-' = :traderCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    AND (vou_no = :vouNo OR '-' = :vouNo)
                    AND (remark REGEXP :remark or '-' = :remark)
                    AND (tran_source = :tranSource OR '-' = :tranSource)
                ) a
                JOIN trader t ON a.trader_code = t.code AND a.comp_code = t.comp_code
                JOIN stock s ON a.stock_code = s.stock_code AND a.comp_code = s.comp_code
                ORDER BY a.vou_date DESC
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .bind("draft", draft)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("traderCode", traderCode)
                .bind("stockCode", stockCode)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("tranSource", tranSource)
                .map(row -> WeightHis.builder()
                        .key(WeightHisKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .traderCode(row.get("trader_code", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .weight(row.get("weight", Double.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .totalQty(row.get("total_qty", Double.class))
                        .totalBag(row.get("total_bag", Double.class))
                        .createdBy(row.get("created_by", String.class))
                        .createdDate(row.get("created_date", LocalDateTime.class))
                        .updatedBy(row.get("updated_by", String.class))
                        .updatedDate(row.get("updated_date", LocalDateTime.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .macId(row.get("mac_id", Integer.class))
                        .tranSource(row.get("tran_source", String.class))
                        .remark(row.get("remark", String.class))
                        .description(row.get("description", String.class))
                        .draft(row.get("draft", Boolean.class))
                        .post(row.get("post", Boolean.class))
                        .stockUserCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .traderUserCode(row.get("t_user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .build())
                .all();
    }

    public Flux<WeightHisDetail> getWeightDetail(String vouNo, String compCode) {
        String sql = """
                SELECT *
                FROM weight_his_detail
                WHERE vou_no = :vouNo
                AND comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> WeightHisDetail.builder()
                        .key(WeightHisDetailKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .vouNo(row.get("vou_no", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .weight(row.get("weight", Double.class))
                        .build())
                .all();
    }


    public Flux<WeightColumn> getWeightColumn(String vouNo, String compCode) {
        Flux<WeightHisDetail> flux = getWeightDetail(vouNo, compCode);

        return flux.collectList()
                .flatMapMany(list -> {
                    List<WeightColumn> listColumn = new ArrayList<>();
                    int totalElements = list.size();
                    int fullRowCount = totalElements / 15;
                    int remainingElements = totalElements % 15;

                    for (int i = 0; i < fullRowCount + (remainingElements > 0 ? 1 : 0); i++) {
                        Double[] rowData = new Double[15];

                        for (int j = 0; j < 15; j++) {
                            int dataIndex = i * 15 + j;

                            if (dataIndex < totalElements) {
                                WeightHisDetail weightDetailHis = list.get(dataIndex);
                                double weight = weightDetailHis.getWeight();
                                rowData[j] = weight;
                            }
                        }

                        WeightColumn c = new WeightColumn();

                        // Use reflection to dynamically set the fields w1, w2, ..., w15
                        for (int k = 0; k < rowData.length; k++) {
                            try {
                                // Make the field accessible
                                Field field = WeightColumn.class.getDeclaredField("w" + (k + 1));
                                field.setAccessible(true);
                                field.set(c, Util1.getDouble(rowData[k]));
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                log.error(e.getMessage());
                            }
                        }
                        double sum = Arrays.stream(rowData)
                                .filter(Objects::nonNull)
                                .mapToDouble(Double::doubleValue)
                                .sum();
                        c.setTotal(sum);
                        listColumn.add(c);
                    }
                    return Flux.fromIterable(listColumn);
                });
    }

    public Mono<Boolean> updatePost(WeightHisKey key, boolean post) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        if (!Util1.isNullOrEmpty(vouNo)) {
            String sql = """
                    update weight_his
                    set post=:post
                    where comp_code=:compCode
                    and vou_no=:vouNo
                    """;
            return client.sql(sql)
                    .bind("post", post)
                    .bind("compCode", compCode)
                    .bind("vouNo", vouNo)
                    .fetch().rowsUpdated().thenReturn(true);
        }
        return Mono.just(false);
    }

    @Transactional
    public Mono<WeightHis> insert(WeightHis dto) {
        String sql = """
                INSERT INTO weight_his (vou_no, comp_code, dept_id, vou_date, trader_code, stock_code, weight,
                                        total_weight, total_qty, total_bag, created_by, created_date, updated_by,
                                        updated_date, deleted, mac_id, tran_source, remark, description, draft, post)
                VALUES (:vouNo, :compCode, :deptId, :vouDate, :traderCode, :stockCode, :weight, :totalWeight,
                        :totalQty, :totalBag, :createdBy, :createdDate, :updatedBy, :updatedDate, :deleted,
                        :macId, :tranSource, :remark, :description, :draft, :post)
                """;
        return executeUpdate(sql, dto);
    }

    @Transactional
    public Mono<WeightHis> update(WeightHis dto) {
        String sql = """
                UPDATE weight_his
                SET dept_id = :deptId, vou_date = :vouDate, trader_code = :traderCode, stock_code = :stockCode,
                    weight = :weight, total_weight = :totalWeight, total_qty = :totalQty, total_bag = :totalBag,
                    created_by = :createdBy, created_date = :createdDate, updated_by = :updatedBy,
                    updated_date = :updatedDate, deleted = :deleted, mac_id = :macId, tran_source = :tranSource,
                    remark = :remark, description = :description, draft = :draft, post = :post
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<WeightHis> executeUpdate(String sql, WeightHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("vouDate", dto.getVouDate())
                .bind("traderCode", dto.getTraderCode())
                .bind("stockCode", dto.getStockCode())
                .bind("weight", dto.getWeight())
                .bind("totalWeight", dto.getTotalWeight())
                .bind("totalQty", dto.getTotalQty())
                .bind("totalBag", dto.getTotalBag())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", dto.getUpdatedDate())
                .bind("deleted", dto.getDeleted())
                .bind("macId", dto.getMacId())
                .bind("tranSource", dto.getTranSource())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("description", Parameters.in(R2dbcType.VARCHAR, dto.getDescription()))
                .bind("draft", dto.getDraft())
                .bind("post", dto.getPost())
                .fetch().rowsUpdated().thenReturn(dto);
    }

    public WeightHis mapRow(Row row) {
        return WeightHis.builder()
                .key(WeightHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .traderCode(row.get("trader_code", String.class))
                .stockCode(row.get("stock_code", String.class))
                .weight(row.get("weight", Double.class))
                .totalWeight(row.get("total_weight", Double.class))
                .totalQty(row.get("total_qty", Double.class))
                .totalBag(row.get("total_bag", Double.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .deleted(row.get("deleted", Boolean.class))
                .macId(row.get("mac_id", Integer.class))
                .tranSource(row.get("tran_source", String.class))
                .remark(row.get("remark", String.class))
                .description(row.get("description", String.class))
                .draft(row.get("draft", Boolean.class))
                .post(row.get("post", Boolean.class))
                .build();
    }

    @Transactional
    private Mono<Boolean> updateDeleteStatus(WeightHisKey key, boolean status) {
        String sql = """
                update weight_his
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

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from weight_his_detail where comp_code=:compCode and vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .fetch().rowsUpdated().thenReturn(true);
    }

    @Transactional
    public Mono<WeightHisDetail> insert(WeightHisDetail dto) {
        String sql = """
                INSERT INTO weight_his_detail (vou_no, comp_code, unique_id, weight)
                VALUES (:vouNo, :compCode, :uniqueId, :weight)
                """;

        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("weight", dto.getWeight())
                .fetch().rowsUpdated().thenReturn(dto);
    }
}
