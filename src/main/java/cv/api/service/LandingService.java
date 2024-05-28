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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class LandingService {
    private final LandingPriceService priceService;
    private final LandingQtyService qtyService;
    private final LandingGradeService gradeService;
    private final VouNoService vouNoService;
    private final DatabaseClient client;
    private final TransactionalOperator operator;

    public Mono<LandingHis> findByCode(LandingHisKey key) {
        if (Util1.isNullOrEmpty(key.getVouNo())) {
            return Mono.empty();
        }
        String sql = """
                select *
                from landing_his
                where comp_code = :compCode
                and vou_no = :vouNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("vouNo", key.getVouNo())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Mono<LandingHis> save(LandingHis dto) {
        Integer deptId = dto.getDeptId();
        if (deptId == null) {
            log.error("deptId is null from mac id : {}", dto.getMacId());
            return Mono.empty();
        }
        return operator.transactional(Mono.defer(() -> saveLanding(dto)
                .flatMap((mh) -> savePrice(mh)
                        .then(saveQty(mh))
                        .then(saveGrade(mh))
                        .thenReturn(mh))));
    }

    private Mono<LandingHis> saveLanding(LandingHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            return vouNoService.getVouNo(deptId, "Landing", compCode, macId)
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

    private Mono<Boolean> savePrice(LandingHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<LandingHisPrice> list = sh.getListPrice();
        if (list != null) {
            return priceService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> !Util1.isNullOrEmpty(e.getCriteriaCode()))
                    .flatMap(e -> {
                        if (e.getKey() == null) {
                            e.setKey(LandingHisPriceKey.builder().build());
                        }
                        int uniqueId = list.indexOf(e) + 1;
                        e.getKey().setUniqueId(uniqueId);
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        return priceService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }

    private Mono<Boolean> saveQty(LandingHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<LandingHisQty> list = sh.getListQty();
        if (list != null) {
            return qtyService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> !Util1.isNullOrEmpty(e.getCriteriaCode()))
                    .flatMap(e -> {
                        if (e.getKey() == null) {
                            e.setKey(LandingHisQtyKey.builder().build());
                        }
                        int uniqueId = list.indexOf(e) + 1;
                        e.getKey().setUniqueId(uniqueId);
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        return qtyService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }

    private Mono<Boolean> saveGrade(LandingHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        List<LandingHisGrade> list = sh.getListGrade();
        if (list != null) {
            return gradeService.deleteDetail(vouNo, compCode).flatMap(aBoolean -> Flux.fromIterable(list)
                    .filter(e -> !Util1.isNullOrEmpty(e.getStockCode()))
                    .flatMap(e -> {
                        if (e.getKey() == null) {
                            e.setKey(LandingHisGradeKey.builder().build());
                        }
                        int uniqueId = list.indexOf(e) + 1;
                        e.getKey().setUniqueId(uniqueId);
                        e.getKey().setVouNo(vouNo);
                        e.getKey().setCompCode(compCode);
                        return gradeService.insert(e).thenReturn(true);
                    })
                    .next()
                    .defaultIfEmpty(false));
        } else {
            return Mono.just(false);
        }
    }

    public Mono<LandingHis> insert(LandingHis dto) {
        String sql = """
                    INSERT INTO landing_his (
                        vou_no, comp_code, dept_id, mac_id, vou_date, trader_code, loc_code, remark,
                        created_by, created_date, updated_by, updated_date, deleted, stock_code,
                        gross_qty, price, amount, cargo, criteria_amt, pur_amt, pur_price, cur_code, print_count
                    ) VALUES (
                        :vouNo, :compCode, :deptId, :macId, :vouDate, :traderCode, :locCode, :remark,
                        :createdBy, :createdDate, :updatedBy, :updatedDate, :deleted, :stockCode,
                        :grossQty, :price, :amount, :cargo, :criteriaAmt, :purAmt, :purPrice, :curCode, :printCount
                    )
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<LandingHis> update(LandingHis dto) {
        String sql = """
                    UPDATE landing_his SET
                        dept_id = :deptId,
                        mac_id = :macId,
                        vou_date = :vouDate,
                        trader_code = :traderCode,
                        loc_code = :locCode,
                        remark = :remark,
                        created_by = :createdBy,
                        created_date = :createdDate,
                        updated_by = :updatedBy,
                        updated_date = :updatedDate,
                        deleted = :deleted,
                        stock_code = :stockCode,
                        gross_qty = :grossQty,
                        price = :price,
                        amount = :amount,
                        cargo = :cargo,
                        criteria_amt = :criteriaAmt,
                        pur_amt = :purAmt,
                        pur_price = :purPrice,
                        cur_code = :curCode,
                        print_count = :printCount
                    WHERE vou_no = :vouNo
                    AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<LandingHis> executeUpdate(String sql, LandingHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("macId", dto.getMacId())
                .bind("vouDate", dto.getVouDate())
                .bind("traderCode", dto.getTraderCode())
                .bind("locCode", dto.getLocCode())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("stockCode", dto.getStockCode())
                .bind("grossQty", dto.getGrossQty())
                .bind("price", dto.getPrice())
                .bind("amount", dto.getAmount())
                .bind("cargo", dto.getCargo())
                .bind("criteriaAmt", dto.getCriteriaAmt())
                .bind("purAmt", dto.getPurAmt())
                .bind("purPrice", dto.getPurPrice())
                .bind("curCode", dto.getCurCode())
                .bind("printCount", dto.getPrintCount())
                .fetch().rowsUpdated().thenReturn(dto);
    }

    public LandingHis mapRow(Row row) {
        return LandingHis.builder()
                .key(LandingHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .deptId(row.get("dept_id", Integer.class))
                .macId(row.get("mac_id", Integer.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .vouDateTime(Objects.requireNonNull(row.get("vou_date", LocalDateTime.class)).atZone(ZoneId.systemDefault()))
                .traderCode(row.get("trader_code", String.class))
                .locCode(row.get("loc_code", String.class))
                .remark(row.get("remark", String.class))
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .deleted(row.get("deleted", Boolean.class))
                .stockCode(row.get("stock_code", String.class))
                .grossQty(row.get("gross_qty", Double.class))
                .price(row.get("price", Double.class))
                .amount(row.get("amount", Double.class))
                .cargo(row.get("cargo", String.class))
                .criteriaAmt(row.get("criteria_amt", Double.class))
                .purAmt(row.get("pur_amt", Double.class))
                .purPrice(row.get("pur_price", Double.class))
                .curCode(row.get("cur_code", String.class))
                .printCount(row.get("print_count", Integer.class))
                .build();
    }


    public Mono<Boolean> delete(LandingHisKey key) {
        return updateDeleteStatus(key, true);
    }

    public Mono<Boolean> restore(LandingHisKey key) {
        return updateDeleteStatus(key, false);
    }

    private Mono<Boolean> updateDeleteStatus(LandingHisKey key, boolean status) {
        String sql = """
                update landing_his
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

    public Flux<LandingHisPrice> getLandingPrice(String vouNo, String compCode) {
        return priceService.getLandingPrice(vouNo, compCode);
    }

    public Flux<LandingHisQty> getLandingQty(String vouNo, String compCode) {
        return qtyService.getLandingQty(vouNo, compCode);
    }

    public Mono<LandingHisGrade> getLandingChooseGrade(String vouNo, String compCode) {
        return gradeService.getLandingGrade(vouNo, compCode)
                .filter(t -> Util1.getBoolean(t.getChoose()))
                .next(); // Return the first element that matches the filter
    }

    public Flux<LandingHisGrade> getLandingGrade(String vouNo, String compCode) {
        return gradeService.getLandingGrade(vouNo, compCode);
    }

    public Flux<LandingHis> getLandingHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        String sql = """
                select a.vou_no,a.comp_code,a.dept_id,a.vou_date,a.created_by,a.deleted,a.remark,
                a.cargo,a.pur_price,a.pur_amt,a.post,a.loc_code,a.stock_code,a.trader_code,
                t.trader_name,l.loc_name,s.stock_name
                from (
                select *
                from landing_his
                where comp_code = :compCode
                and deleted = :deleted
                and date(vou_date) between :fromDate and :toDate
                and (trader_code = :traderCode or '-' = :traderCode)
                and (vou_no = :vouNo or '-' = :vouNo)
                and (remark like :remark or '-%' = :remark)
                and (created_by = :userCode or '-' = :userCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (loc_code = :locCode or '-' = :locCode)
                and (dept_id = :deptId or 0 = :deptId)
                )a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                order by vou_date desc
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("traderCode", traderCode)
                .bind("vouNo", vouNo)
                .bind("remark", remark + "%")
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("locCode", locCode)
                .bind("deptId", deptId)
                .map((row, rowMetadata) -> {
                    LandingHisKey key = LandingHisKey.builder()
                            .compCode(row.get("comp_code", String.class))
                            .vouNo(row.get("vou_no", String.class))
                            .build();

                    return LandingHis.builder()
                            .key(key)
                            .deptId(row.get("dept_id", Integer.class))
                            .vouDate(row.get("vou_date", LocalDateTime.class))
                            .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                            .createdBy(row.get("created_by", String.class))
                            .deleted(row.get("deleted", Boolean.class))
                            .remark(row.get("remark", String.class))
                            .cargo(row.get("cargo", String.class))
                            .traderCode(row.get("trader_code", String.class))
                            .traderName(row.get("trader_name", String.class))
                            .locCode(row.get("loc_code", String.class))
                            .locName(row.get("loc_name", String.class))
                            .stockCode(row.get("stock_code", String.class))
                            .stockName(row.get("stock_name", String.class))
                            .purPrice(row.get("pur_price", Double.class))
                            .purAmt(row.get("pur_amt", Double.class))
                            .post(row.get("post", Boolean.class))
                            .build();
                })
                .all();
    }

    public Mono<Boolean> updatePost(LandingHisKey key, boolean post) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        if (!Util1.isNullOrEmpty(vouNo)) {
            String sql = """
                    update landing_his
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
}
