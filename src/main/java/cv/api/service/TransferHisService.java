package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.THDetailKey;
import cv.api.entity.TransferHis;
import cv.api.entity.TransferHisDetail;
import cv.api.entity.TransferHisKey;
import cv.api.model.VTransfer;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
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
@Transactional
@RequiredArgsConstructor
public class TransferHisService {
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<TransferHis> saveTransfer(TransferHis dto) {
        return operator.transactional(Mono.defer(() -> saveOrUpdate(dto)
                .flatMap(ri -> deleteDetail(ri.getKey().getVouNo(), ri.getKey().getCompCode())
                        .flatMap(delete -> {
                            List<TransferHisDetail> list = dto.getListTD();
                            if (list != null && !list.isEmpty()) {
                                return Flux.fromIterable(list)
                                        .filter(detail -> !Util1.isNullOrEmpty(detail.getStockCode()))
                                        .concatMap(detail -> {
                                            if (detail.getKey() == null) {
                                                detail.setKey(THDetailKey.builder().build());
                                            }
                                            int uniqueId = list.indexOf(detail) + 1;
                                            detail.getKey().setUniqueId(uniqueId);
                                            detail.getKey().setVouNo(ri.getKey().getVouNo());
                                            detail.getKey().setCompCode(ri.getKey().getCompCode());
                                            detail.setDeptId(ri.getDeptId());
                                            return insert(detail);
                                        })
                                        .then(Mono.just(ri));
                            } else {
                                return Mono.just(ri);
                            }
                        }))));
    }

    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from transfer_his_detail where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    private Mono<TransferHis> saveOrUpdate(TransferHis dto) {
        String vouNo = dto.getKey().getVouNo();
        String compCode = dto.getKey().getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            return vouNoService.getVouNo(deptId, "TRANSFER", compCode, macId)
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


    private TransferHis mapRow(Row row) {
        return TransferHis.builder()
                .key(TransferHisKey.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .createdBy(row.get("created_by", String.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .deleted(row.get("deleted", Boolean.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .refNo(row.get("ref_no", String.class))
                .remark(row.get("remark", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .locCodeFrom(row.get("loc_code_from", String.class))
                .locCodeTo(row.get("loc_code_to", String.class))
                .macId(row.get("mac_id", Integer.class))
                .deptId(row.get("dept_id", Integer.class))
                .intgUpdStatus(row.get("intg_upd_status", String.class))
                .labourGroupCode(row.get("labour_group_code", String.class))
                .jobCode(row.get("job_code", String.class))
                .vouLock(row.get("vou_lock", Boolean.class))
                .traderCode(row.get("trader_code", String.class))
                .printCount(row.get("print_count", Integer.class))
                .skipInv(row.get("skip_inv", Boolean.class))
                .build();
    }

    public Mono<TransferHis> findById(TransferHisKey key) {
        String sql = """
                select *
                from transfer_his
                where vou_no =:vouNo
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", key.getVouNo())
                .bind("compCode", key.getCompCode())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }


    public Mono<Boolean> delete(TransferHisKey key) {
        return updateDeleteStatus(key, true);
    }

    public Mono<Boolean> restore(TransferHisKey key) {
        return updateDeleteStatus(key, false);
    }

    private Mono<Boolean> updateDeleteStatus(TransferHisKey key, boolean status) {
        String sql = """
                update transfer_his
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


    public Mono<TransferHisDetail> insert(TransferHisDetail dto) {
        String query = """
                INSERT INTO transfer_his_detail (vou_no, stock_code, qty, unit, unique_id, comp_code, dept_id, weight,
                weight_unit, total_weight, wet, rice, bag, price, amount)
                VALUES (:vouNo, :stockCode, :qty, :unit, :uniqueId, :compCode, :deptId, :weight, :weightUnit,
                :totalWeight, :wet, :rice, :bag, :price, :amount)
                """;
        return client
                .sql(query)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("stockCode", dto.getStockCode())
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnitCode())
                .bind("deptId", dto.getDeptId())
                .bind("weight", Parameters.in(R2dbcType.VARCHAR, dto.getWeight()))
                .bind("weightUnit", Parameters.in(R2dbcType.VARCHAR, dto.getWeightUnit()))
                .bind("totalWeight", Parameters.in(R2dbcType.DOUBLE, dto.getTotalWeight()))
                .bind("wet", Parameters.in(R2dbcType.DOUBLE, dto.getWet()))
                .bind("rice", Parameters.in(R2dbcType.DOUBLE, dto.getRice()))
                .bind("bag", Parameters.in(R2dbcType.DOUBLE, dto.getBag()))
                .bind("price", Parameters.in(R2dbcType.DOUBLE, dto.getPrice()))
                .bind("amount", Parameters.in(R2dbcType.DOUBLE, dto.getAmount()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<TransferHis> insert(TransferHis dto) {
        String sql = """
                INSERT INTO transfer_his (vou_no, created_by, created_date, deleted, vou_date, ref_no, remark,
                updated_by, updated_date, loc_code_from, loc_code_to, mac_id, comp_code, dept_id, intg_upd_status,
                labour_group_code, job_code, vou_lock, trader_code, print_count, skip_inv) VALUES
                (:vouNo, :createdBy, :createdDate, :deleted, :vouDate, :refNo, :remark, :updatedBy, :updatedDate,
                :locCodeFrom, :locCodeTo, :macId, :compCode, :deptId, :intgUpdStatus, :labourGroupCode, :jobCode,
                :vouLock, :traderCode, :printCount, :skipInv)
                """;
        return executeUpdate(sql, dto);

    }

    public Mono<TransferHis> update(TransferHis dto) {
        String sql = """
                UPDATE transfer_his
                SET created_by = :createdBy,
                    created_date = :createdDate,
                    deleted = :deleted,
                    vou_date = :vouDate,
                    ref_no = :refNo,
                    remark = :remark,
                    updated_by = :updatedBy,
                    updated_date = :updatedDate,
                    loc_code_from = :locCodeFrom,
                    loc_code_to = :locCodeTo,
                    mac_id = :macId,
                    comp_code = :compCode,
                    dept_id = :deptId,
                    intg_upd_status = :intgUpdStatus,
                    labour_group_code = :labourGroupCode,
                    job_code = :jobCode,
                    vou_lock = :vouLock,
                    trader_code = :traderCode,
                    print_count = :printCount
                WHERE vou_no = :vouNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);

    }

    private Mono<TransferHis> executeUpdate(String sql, TransferHis dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("createdBy", dto.getCreatedBy())
                .bind("createdDate", dto.getCreatedDate())
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("vouDate", dto.getVouDate())
                .bind("refNo", Parameters.in(R2dbcType.VARCHAR, dto.getRefNo()))
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("updatedDate", LocalDateTime.now())
                .bind("locCodeFrom", dto.getLocCodeFrom())
                .bind("locCodeTo", dto.getLocCodeTo())
                .bind("macId", dto.getMacId())
                .bind("deptId", dto.getDeptId())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("labourGroupCode", Parameters.in(R2dbcType.VARCHAR, dto.getLabourGroupCode()))
                .bind("jobCode", Parameters.in(R2dbcType.VARCHAR, dto.getJobCode()))
                .bind("vouLock", Util1.getBoolean(dto.getVouLock()))
                .bind("traderCode", Parameters.in(R2dbcType.VARCHAR, dto.getTraderCode()))
                .bind("printCount", Parameters.in(R2dbcType.INTEGER, dto.getPrintCount()))
                .bind("skipInv", Parameters.in(R2dbcType.INTEGER, dto.getSkipInv()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Flux<VTransfer> getTransferVoucher(String vouNo, String compCode) {
        String sql = """
                SELECT stock_name, unit, t.qty,sale_price_n,t.qty*sale_price_n sale_amt,ft.loc_name AS fLocName, tt.loc_name AS tLocName,
                t.vou_no, t.vou_date, t.user_code, t.remark, t.ref_no, t.weight, t.weight_unit,
                u1.unit_name, u2.unit_name AS weight_unit_name, g.labour_name
                FROM v_transfer t
                JOIN location ft ON t.loc_code_from = ft.loc_code AND t.comp_code = ft.comp_code
                JOIN location tt ON t.loc_code_to = tt.loc_code AND t.comp_code = tt.comp_code
                LEFT JOIN stock_unit u1 ON t.unit = u1.unit_code AND t.comp_code = u1.comp_code
                LEFT JOIN stock_unit u2 ON t.weight_unit = u2.unit_code AND t.comp_code = u2.comp_code
                LEFT JOIN labour_group g ON t.labour_group_code = g.code AND t.comp_code = g.comp_code
                WHERE t.comp_code = :compCode AND t.vou_no = :vouNo
                ORDER BY unique_id
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row) -> VTransfer.builder()
                        .stockName(row.get("stock_name", String.class))
                        .unit(row.get("unit", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("sale_price_n", Double.class))
                        .saleAmt(row.get("sale_amt", Double.class))
                        .vouNo(row.get("vou_no", String.class))
                        .vouDate(row.get("vou_date", String.class))
                        .fromLocationName(row.get("fLocName", String.class))
                        .toLocationName(row.get("tLocName", String.class))
                        .stockCode(row.get("user_code", String.class))
                        .remark(row.get("remark", String.class))
                        .refNo(row.get("ref_no", String.class))
                        .unitName(row.get("unit_name", String.class))
                        .labourGroupName(row.get("labour_name", String.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .weightUnitName(row.get("weight_unit_name", String.class))
                        .build())
                .all();
    }

    public Flux<VTransfer> getTransferHistory(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String refNo = Util1.isNull(filter.getRefNo(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        boolean deleted = filter.isDeleted();
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");

        String sql = """
                    select a.*,l.loc_name from_loc_name,ll.loc_name to_loc_name,t.trader_name
                    from (
                    select vou_date,vou_no,comp_code,remark,ref_no,loc_code_from,loc_code_to,
                    created_by,deleted,dept_id, labour_group_code,trader_code
                    from v_transfer v
                    where comp_code = :compCode
                    and deleted = :deleted
                    and (dept_id = :deptId or 0 = :deptId)
                    and date(vou_date) between :fromDate and :toDate
                    and (vou_no = :vouNo or '-' = :vouNo)
                    and (ref_no REGEXP :refNo or '-' = :refNo)
                    and (remark REGEXP :remark or '-' = :remark)
                    and (created_by = :userCode or '-' = :userCode)
                    and (stock_code = :stockCode or '-' = :stockCode)
                    and (trader_code = :traderCode or '-' = :traderCode)
                    and (loc_code_from = :locCode or '-'=:locCode or loc_code_to = :locCode)
                    group by vou_no
                    )a
                    join location l
                    on a.loc_code_from = l.loc_code
                    and a.comp_code = l.comp_code
                    join location ll on a.loc_code_to = ll.loc_code
                    and a.comp_code = ll.comp_code
                    left join trader t on a.trader_code = t.code
                    and a.comp_code = t.comp_code
                    order by vou_date desc
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("refNo", refNo)
                .bind("remark", remark)
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .bind("locCode", locCode)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> VTransfer.builder()
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .remark(row.get("remark", String.class))
                        .refNo(row.get("ref_no", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .fromLocationName(row.get("from_loc_name", String.class))
                        .toLocationName(row.get("to_loc_name", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .traderName(row.get("trader_name", String.class))
                        .build())
                .all();
    }

    public Flux<TransferHisDetail> search(String vouNo, String compCode) {
        String sql = """
                select td.*,s.user_code,s.stock_name,st.stock_type_name,rel.rel_name
                from transfer_his_detail td
                join stock s on td.stock_code = s.stock_code
                and td.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and td.comp_code = rel.comp_code
                where td.vou_no =:vouNo
                and td.comp_code =:compCode
                order by td.unique_id""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> TransferHisDetail.builder()
                        .key(THDetailKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .userCode(row.get("user_code", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .groupName(row.get("stock_type_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .unitCode(row.get("unit", String.class))
                        .relName(row.get("rel_name", String.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .wet(row.get("wet", Double.class))
                        .rice(row.get("rice", Double.class))
                        .bag(row.get("bag", Double.class))
                        .build()).all();

    }

}
