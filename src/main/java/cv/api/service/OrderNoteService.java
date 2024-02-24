package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.dto.OrderFileJoin;
import cv.api.dto.OrderNote;
import cv.api.r2dbc.LabourPayment;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderNoteService {
    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;
    private final VouNoService vouNoService;

    public Mono<OrderNote> save(OrderNote dto) {
        return saveOrUpdate(dto).flatMap(orderNote -> deleteDetail(orderNote.getVouNo(), orderNote.getCompCode()).flatMap(delete -> {
            List<OrderFileJoin> list = dto.getDetailList();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> !detail.getFileId().isEmpty())
                        .concatMap(detail -> {
                            detail.setVouNo(orderNote.getVouNo());
                            detail.setCompCode(orderNote.getCompCode());
                            return template.insert(detail);
                        })
                        .then(Mono.just(orderNote));
            } else {
                return Mono.just(orderNote);
            }
        }));

    }

    private Mono<OrderNote> saveOrUpdate(OrderNote note) {
        String vouNo = note.getVouNo();
        String compCode = note.getCompCode();
        int deptId = note.getDeptId();
        int macId = note.getMacId();
        note.setVouDate(Util1.toDateTime(note.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            note.setCreatedDate(LocalDateTime.now());
            return vouNoService.getVouNo(deptId, "OrderNote", compCode, macId)
                    .flatMap(seqNo -> {
                        note.setVouNo(seqNo);
                        return insert(note);
                    });
        } else {
            return update(note);
        }
    }

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from order_file_join
                where vou_no=:vouNo and comp_code=:compCode
                """;
        return databaseClient.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated()
                .thenReturn(true)
                .defaultIfEmpty(false);
    }

    public Mono<OrderNote> update(OrderNote p) {
        String sql = """
                UPDATE order_note
                SET
                dept_id=:deptId,
                mac_id = :macId,
                trader_code = :traderCode,
                stock_code = :stockCode,
                order_code = :orderCode,
                order_name = :orderName,
                vou_date = :vouDate,
                created_date = :createdDate,
                created_by = :createdBy,
                updated_date = :updatedDate,
                updated_by = :updatedBy,
                deleted = :deleted
                WHERE vou_no = :vouNo AND comp_code = :compCode;
                """;
        return execute(sql, p);
    }

    private Mono<OrderNote> insert(OrderNote p) {
        String sql = """
                INSERT INTO order_note
                (
                vou_no,
                comp_code,
                dept_id,
                mac_id,
                trader_code,
                stock_code,
                order_code,
                order_name,
                vou_date,
                created_date,
                created_by,
                updated_date,
                updated_by,
                deleted)
                VALUES
                (
                :vouNo,
                :compCode,
                :deptId,
                :macId,
                :traderCode,
                :stockCode,
                :orderCode,
                :orderName,
                :vouDate,
                :createdDate,
                :createdBy,
                :updatedDate,
                :updatedBy,
                :deleted);
                """;
        return execute(sql, p);
    }

    private Mono<OrderNote> execute(String sql, OrderNote p) {
        return databaseClient.sql(sql)
                .bind("vouNo", p.getVouNo())
                .bind("compCode", p.getCompCode())
                .bind("deptId", p.getDeptId())
                .bind("macId", p.getMacId())
                .bind("traderCode", p.getTraderCode())
                .bind("stockCode", p.getStockCode())
                .bind("orderCode", Parameters.in(R2dbcType.VARCHAR, p.getOrderCode()))
                .bind("orderName", Parameters.in(R2dbcType.VARCHAR, p.getOrderName()))
                .bind("vouDate", p.getVouDate())
                .bind("createdDate", p.getCreatedDate())
                .bind("createdBy", p.getCreatedBy())
                .bind("updatedDate", Parameters.in(R2dbcType.TIMESTAMP, p.getUpdatedDate()))
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, p.getUpdatedBy()))
                .bind("deleted", p.getDeleted())
                .fetch()
                .rowsUpdated()
                .thenReturn(p);
    }

    public Flux<OrderNote> history(ReportFilter filter) {
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String traderCode = filter.getTraderCode();
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String orderNo = filter.getOrderNo();
        String orderName = filter.getOrderName();
        String sql = """
                SELECT o.*,t.trader_name, s.stock_name
                FROM order_note o
                join trader t
                on t.code = o.trader_code
                and t.comp_code = o.comp_code
                join stock s
                on s.stock_code = o.stock_code
                and s.comp_code = o.comp_code
                where date(vou_date) between :fromDate and :toDate
                and ('-' = :traderCode or o.trader_code = :traderCode)
                and ('-' = :stockCode or o.stock_code = :stockCode)
                and ('-' = :orderNo or o.order_code regexp :orderNo)
                and ('' = :orderName or o.order_name regexp :orderName)
                and o.deleted = :deleted
                and o.comp_code = :compCode
                order by o.vou_date desc;
                """;
        return databaseClient.sql(sql)
                .bind("deleted", deleted)
                .bind("compCode", compCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("traderCode", traderCode)
                .bind("stockCode", stockCode)
                .bind("orderNo", orderNo)
                .bind("orderName", orderName)
                .map((row) -> OrderNote.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .traderName(row.get("trader_name", String.class))
                        .createdDate(row.get("created_date", LocalDateTime.class))
                        .createdBy(row.get("created_by", String.class))
                        .updatedDate(row.get("updated_date", LocalDateTime.class))
                        .updatedBy(row.get("updated_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .stockName(row.get("stock_name", String.class))
                        .orderCode(row.get("order_Code", String.class))
                        .orderName(row.get("order_name", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .build()).all();
    }

    public Flux<OrderFileJoin> getDetail(String vouNo, String compCode) {
        //vou_no, comp_code, unique_id, description, qty, price, amount, account
        String sql = """
                select *
                from order_file_join
                where vou_no=:vouNo
                and comp_code=:compCode
                """;
        return databaseClient.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row) -> OrderFileJoin.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .fileId(row.get("file_id", String.class))
                        .build())
                .all();
    }

    public Flux<LabourPayment> unUploadVoucher(LocalDateTime syncDate) {
        return template.select(LabourPayment.class)
                .matching(Query.query(Criteria.where("intg_upd_status")
                        .isNull()
                        .and("vou_date").greaterThanOrEquals(syncDate))).all();

    }

    public Mono<Boolean> update(String vouNo, String compCode, String status) {
        String sql = """
                update order_note
                set intg_upd_status = :status
                where vou_no =:vouNo
                and comp_code = :compCode
                """;
        return databaseClient.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .bind("status", Parameters.in(R2dbcType.VARCHAR, status))
                .fetch()
                .rowsUpdated()
                .thenReturn(true);
    }

    public Mono<Boolean> update(String vouNo, String compCode, boolean deleted) {
        String sql = """
                update order_note
                set deleted = :deleted
                where vou_no =:vouNo
                and comp_code = :compCode
                """;
        return databaseClient.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .fetch()
                .rowsUpdated()
                .thenReturn(true);
    }

    public Mono<OrderNote> findOrderNote(String vouNo, String compCode) {
        String sql = """
                SELECT * from
                order_note
                where vou_no = :vouNo and comp_code = :compCode;
                """;
        return databaseClient.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row) -> OrderNote.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .createdDate(row.get("created_date", LocalDateTime.class))
                        .createdBy(row.get("created_by", String.class))
                        .updatedDate(row.get("updated_date", LocalDateTime.class))
                        .updatedBy(row.get("updated_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .orderCode(row.get("order_Code", String.class))
                        .orderName(row.get("order_name", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .stockName(row.get("stock_code", String.class))
                        .build()).one();
    }
}

