package cv.api.service;

import cv.api.common.FilterObject;
import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.dto.LabourPaymentDto;
import cv.api.dto.StockPayment;
import cv.api.dto.StockPaymentDetail;
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
public class StockPaymentService {
    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;
    private final VouNoService vouNoService;

    public Mono<StockPayment> save(StockPayment dto) {
        return saveOrUpdate(dto).flatMap(payment -> deleteDetail(payment.getVouNo(), payment.getCompCode()).flatMap(delete -> {
            List<StockPaymentDetail> list = dto.getListDetail();
            if (list != null && !list.isEmpty()) {
                return Flux.fromIterable(list)
                        .filter(detail -> Util1.getDouble(detail.getQty()) != 0)
                        .concatMap(detail -> {
                            int uniqueId = list.indexOf(detail) + 1;
                            detail.setUniqueId(uniqueId);
                            detail.setVouNo(payment.getVouNo());
                            detail.setCompCode(payment.getCompCode());
                            return template.insert(detail);
                        })
                        .then(Mono.just(payment));
            } else {
                return Mono.just(payment);
            }
        }));

    }

    private Mono<StockPayment> saveOrUpdate(StockPayment payment) {
        String vouNo = payment.getVouNo();
        String compCode = payment.getCompCode();
        int deptId = payment.getDeptId();
        int macId = payment.getMacId();
        payment.setVouDate(Util1.toDateTime(payment.getVouDate()));
        if (vouNo == null) {
            return vouNoService.getVouNo(deptId, "StockPayment", compCode, macId)
                    .flatMap(seqNo -> {
                        payment.setVouNo(seqNo);
                        payment.setCreatedDate(LocalDateTime.now());
                        payment.setUpdatedDate(LocalDateTime.now());
                        return insert(payment);
                    });
        } else {
            return update(payment);
        }
    }

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from labour_payment_detail
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

    public Mono<StockPayment> update(StockPayment data) {
        String sql = """
                UPDATE labour_payment
                SET
                  vou_no = :vouNo,
                  comp_code = :compCode,
                  vou_date = :vouDate,
                  labour_group_code = :labourGroupCode,
                  cur_code = :curCode,
                  remark = :remark,
                  pay_total=:payTotal,
                  created_date = :createdDate,
                  created_by = :createdBy,
                  updated_date = :updatedDate,
                  updated_by = :updatedBy,
                  deleted = :deleted,
                  mac_id = :macId,
                  member_count =:memberCount,
                  source_acc = :sourceAcc,
                  expense_acc =:expenseAcc,
                  dept_code=:deptCode,
                  post=:post
                WHERE vou_no = :vouNo AND comp_code = :compCode""";

        return databaseClient.sql(sql)
                .bind("vouNo", data.getVouNo())
                .bind("compCode", data.getCompCode())
                .bind("vouDate", data.getVouDate())
                .bind("remark", data.getRemark())
                .bind("createdDate", data.getCreatedDate())
                .bind("createdBy", data.getCreatedBy())
                .bind("updatedDate", LocalDateTime.now())
                .bind("updatedBy", data.getUpdatedBy())
                .bind("macId", data.getMacId())
                .bind("vouNo", data.getVouNo())
                .bind("compCode", data.getCompCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(data);
    }
    private Mono<StockPayment> insert(StockPayment p) {
        String sql = """
            INSERT INTO stock_payment
            (vou_no,
            comp_code,
            dept_id,
            vou_date,
            trader_code,
            remark,
            deleted,
            created_date,
            created_by,
            updated_date,
            updated_by,
            mac_id,
            tran_option)
            VALUES
            (:vouNo,
            :compCode,
            :deptId,
            :vouDate,
            :traderCode,
            :remark,
            :deleted,
            :createdDate,
            :createdBy,
            :updatedDate,
            :updatedBy,
            :macId,
            :tranOption);
            """;

        return databaseClient.sql(sql)
                .bind("vouNo", p.getVouNo())
                .bind("compCode", p.getCompCode())
                .bind("deptId", p.getDeptId())
                .bind("vouDate", p.getVouDate())
                .bind("traderCode", p.getTraderCode())
                .bind("remark", p.getRemark())
                .bind("deleted", p.getDeleted())
                .bind("createdDate", p.getCreatedDate())
                .bind("createdBy", p.getCreatedBy())
                .bind("updatedDate", p.getUpdatedDate())
                .bind("updatedBy", p.getUpdatedBy())
                .bind("macId", p.getMacId())
                .bind("tranOption", p.getTranOption())
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

