package cv.api.service;

import cv.api.common.FilterObject;
import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.dto.*;
import cv.api.r2dbc.LabourPayment;
import cv.api.r2dbc.LabourPaymentDetail;
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

import java.time.LocalDate;
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

    private Mono<OrderNote> saveOrUpdate(OrderNote orderNote) {
        String vouNo = orderNote.getVouNo();
        String compCode = orderNote.getCompCode();
        int deptId = orderNote.getDeptId();
        int macId = orderNote.getMacId();
        orderNote.setVouDate(Util1.toDateTime(orderNote.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "OrderNote", compCode, macId)
                    .flatMap(seqNo -> {
                        orderNote.setVouNo(seqNo);
                        orderNote.setCreatedDate(LocalDateTime.now());
                        orderNote.setUpdatedDate(LocalDateTime.now());
                        return insert(orderNote);
                    });
        } else {
            return update(orderNote);
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
                vou_no = :vouNo,
                comp_code = :compCode,
                dept_id = :deptId,
                mac_id = :macId,
                trader_code = :traderCode,
                stock_code = :stockCode,
                order_code = :orderCode,
                order_name = :orderName,
                vou_date = :vouDate,
                created_date = :createdDate,
                created_by = :createdBy
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
                .bind("orderCode", p.getOrderCode())
                .bind("orderName", p.getOrderName())
                .bind("vouDate", p.getVouDate())
                .bind("createdDate", p.getCreatedDate())
                .bind("createdBy", p.getCreatedBy())
                .bind("updatedDate", p.getUpdatedDate())
                .bind("updatedBy", p.getUpdatedBy())
                .bind("deleted", p.getDeleted())
                .fetch()
                .rowsUpdated()
                .thenReturn(p);
    }


    public Flux<LabourPaymentDetail> calculatePayment(ReportFilter filter) {
        String startDate = filter.getFromDate();
        String enDate = filter.getToDate();
        String labourGroupCode = filter.getLabourGroupCode();
        String compCode = filter.getCompCode();
        String sql = """
                select a.*,l.labour_name,ifnull(l.price,0) price,ifnull(l.qty,0)*ifnull(l.price,0)*a.bag amount
                from (
                select 'Purchase' tran_option,labour_group_code,sum(bag) bag,comp_code
                from v_purchase
                where date(vou_date) between :startDate and :endDate
                and deleted = false
                and comp_code =:compCode
                and labour_group_code =:labourGroupCode
                    union all
                select v.description tran_option,s.labour_group_code,sum(s.bag) bag,s.comp_code
                from v_stock_io s join vou_status v
                on s.vou_status = v.code
                and s.comp_code = v.comp_code
                where date(s.vou_date) between :startDate and :endDate
                and s.deleted = false
                and s.comp_code =:compCode
                and s.labour_group_code =:labourGroupCode
                group by s.vou_status
                )a
                join labour_group l on a.labour_group_code = l.code
                and a.comp_code = l.comp_code
                """;
        return databaseClient.sql(sql)
                .bind("startDate", startDate)
                .bind("endDate", enDate)
                .bind("compCode", compCode)
                .bind("labourGroupCode", labourGroupCode)
                .map((row) -> LabourPaymentDetail.builder()
                        .tranOption(row.get("tran_option", String.class))
                        .description(row.get("tran_option", String.class))
                        .qty(row.get("bag", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amount", Double.class))
                        .build())
                .all();
    }

    public Flux<LabourPaymentDto> history(FilterObject filter) {
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String curCode = filter.getCurCode();
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String sql = """
                select a.*,l.labour_name
                from (
                select *
                from labour_payment
                where deleted = :deleted
                and comp_code =:compCode
                and date(vou_date) between :fromDate and :toDate
                and cur_code =:curCode
                and (created_by =:userCode or '-' =:userCode)
                )a
                join labour_group l on a.labour_group_code = l.code
                and a.comp_code = l.comp_code
                order by a.vou_date desc
                """;
        return databaseClient.sql(sql)
                .bind("deleted", deleted)
                .bind("compCode", compCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("userCode", userCode)
                //vou_no, comp_code, dept_id, vou_date, labour_group_code, cur_code,
                // remark, created_date, created_by, updated_date,
                // updated_by, deleted, mac_id, account, member_count,
                // pay_total, source_acc, expense_acc, labour_name
                .map((row) -> LabourPaymentDto.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .labourGroupCode(row.get("labour_group_code", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .remark(row.get("remark", String.class))
                        .createdDate(row.get("created_date", LocalDateTime.class))
                        .createdBy(row.get("created_by", String.class))
                        .updatedDate(row.get("updated_date", LocalDateTime.class))
                        .updatedBy(row.get("updated_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .memberCount(row.get("member_count", Integer.class))
                        .payTotal(row.get("pay_total", Double.class))
                        .sourceAcc(row.get("source_acc", String.class))
                        .expenseAcc(row.get("expense_acc", String.class))
                        .labourName(row.get("labour_name", String.class))
                        .fromDate(row.get("from_date", LocalDate.class))
                        .toDate(row.get("to_date", LocalDate.class))
                        .deptCode(row.get("dept_code", String.class))
                        .post(row.get("post", Boolean.class))
                        .build()).all();
    }

    public Flux<LabourPaymentDetail> getDetail(String vouNo, String compCode) {
        //vou_no, comp_code, unique_id, description, qty, price, amount, account
        String sql = """
                select *
                from labour_payment_detail
                where vou_no=:vouNo
                and comp_code=:compCode
                """;
        return databaseClient.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row) -> LabourPaymentDetail.builder()
                        .vouNo(row.get("vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .uniqueId(row.get("unique_id", Integer.class))
                        .description(row.get("description", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amount", Double.class))
                        .account(row.get("account", String.class))
                        .deptCode(row.get("dept_code", String.class))
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
                update labour_payment
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
                update labour_payment
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
}

