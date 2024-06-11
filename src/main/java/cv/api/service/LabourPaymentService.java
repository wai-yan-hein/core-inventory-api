package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.dto.LabourPaymentDto;
import cv.api.entity.LabourPaymentDetail;
import cv.api.exception.ResponseUtil;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LabourPaymentService {
    private final DatabaseClient client;
    private final VouNoService vouNoService;
    private final TransactionalOperator operator;

    public Mono<LabourPaymentDto> save(LabourPaymentDto dto) {
       return isValid(dto).flatMap(his -> operator.transactional(Mono.defer(() -> saveOrUpdate(his)
               .flatMap(payment -> deleteDetail(payment.getVouNo(), payment.getCompCode())
                       .flatMap(delete -> {
                           List<LabourPaymentDetail> list = his.getListDetail();
                           if (list != null && !list.isEmpty()) {
                               return Flux.fromIterable(list)
                                       .filter(detail -> Util1.getDouble(detail.getAmount()) != 0)
                                       .concatMap(detail -> {
                                           int uniqueId = list.indexOf(detail) + 1;
                                           detail.setUniqueId(uniqueId);
                                           detail.setVouNo(payment.getVouNo());
                                           detail.setCompCode(payment.getCompCode());
                                           detail.setDeptId(payment.getDeptId());
                                           return insert(detail);
                                       })
                                       .then(Mono.just(payment));
                           } else {
                               return Mono.just(payment);
                           }
                       })))));
    }

    private Mono<LabourPaymentDto> isValid(LabourPaymentDto sh) {
        List<LabourPaymentDetail> list = Util1.nullToEmpty(sh.getListDetail());
        list.removeIf(t -> Util1.getDouble(t.getAmount()) == 0);
        if (list.isEmpty()) {
            return ResponseUtil.createBadRequest("Detail is null/empty");
        } else if (Util1.isNullOrEmpty(sh.getDeptId())) {
            return ResponseUtil.createBadRequest("deptId is null from mac id : " + sh.getMacId());
        } else if (Util1.isNullOrEmpty(sh.getCurCode())) {
            return ResponseUtil.createBadRequest("Currency is null");
        } else if (Util1.isNullOrEmpty(sh.getVouDate())) {
            return ResponseUtil.createBadRequest("Voucher Date is null");
        }
        return Mono.just(sh);
    }


    private Mono<LabourPaymentDto> saveOrUpdate(LabourPaymentDto dto) {
        String vouNo = dto.getVouNo();
        String compCode = dto.getCompCode();
        int deptId = dto.getDeptId();
        int macId = dto.getMacId();
        dto.setVouDate(Util1.toDateTime(dto.getVouDate()));
        if (Util1.isNullOrEmpty(vouNo)) {
            return vouNoService.getVouNo(deptId, "LabourPayment", compCode, macId)
                    .flatMap(seqNo -> {
                        dto.setVouNo(seqNo);
                        dto.setCreatedDate(LocalDateTime.now());
                        dto.setUpdatedDate(LocalDateTime.now());
                        return insert(dto);
                    });
        } else {
            return update(dto);
        }
    }

    public Mono<LabourPaymentDto> insert(LabourPaymentDto dto) {
        String sql = """
                INSERT INTO labour_payment (
                   vou_no, comp_code, dept_id, vou_date, labour_group_code, cur_code, remark,pay_total,
                   created_date, created_by, updated_date, updated_by, deleted, mac_id,
                   member_count, source_acc, expense_acc, from_date, to_date, dept_code, intg_upd_status, post
                ) VALUES (
                    :vouNo, :compCode,:deptId, :vouDate, :labourGroupCode, :curCode, :remark, :payTotal,
                    :createdDate, :createdBy, :updatedDate, :updatedBy, :deleted, :macId,
                    :memberCount, :sourceAcc, :expenseAcc,:fromDate,:toDate, :deptCode,:intgUpdStatus, :post
                )
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<LabourPaymentDetail> insert(LabourPaymentDetail dto) {
        String sql = """
                INSERT INTO labour_payment_detail (
                    vou_no, comp_code, unique_id, description, qty, price, amount, account,dept_code
                ) VALUES (
                    :vouNo, :compCode, :uniqueId, :description, :qty, :price, :amount, :account,:deptCode
                )
                """;
        return client.sql(sql)
                .bind("vouNo", dto.getVouNo())
                .bind("compCode", dto.getCompCode())
                .bind("uniqueId", dto.getUniqueId())
                .bind("description", Parameters.in(R2dbcType.VARCHAR, dto.getDescription()))
                .bind("qty", dto.getQty())
                .bind("price", dto.getPrice())
                .bind("amount", dto.getAmount())
                .bind("account", dto.getAccount())
                .bind("deptCode", Parameters.in(R2dbcType.VARCHAR, dto.getDeptCode()))
                .fetch().rowsUpdated().map(rowsUpdated -> dto);
    }

    private Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from labour_payment_detail
                where vou_no=:vouNo and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated()
                .thenReturn(true)
                .defaultIfEmpty(false);
    }

    public Mono<LabourPaymentDto> update(LabourPaymentDto dto) {
        String sql = """
                UPDATE labour_payment
                SET
                  vou_no = :vouNo,
                  comp_code = :compCode,
                  dept_id = :deptId,
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
                  from_date =:fromDate,
                  to_date =:toDate,
                  intg_upd_status =:intgUpdStatus,
                  dept_code=:deptCode,
                  post=:post
                WHERE vou_no = :vouNo AND comp_code = :compCode""";
        return executeUpdate(sql, dto);
    }

    private Mono<LabourPaymentDto> executeUpdate(String sql, LabourPaymentDto dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getVouNo())
                .bind("compCode", dto.getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("vouDate", dto.getVouDate())
                .bind("labourGroupCode", dto.getLabourGroupCode())
                .bind("curCode", dto.getCurCode())
                .bind("remark", Parameters.in(R2dbcType.VARCHAR, dto.getRemark()))
                .bind("payTotal", dto.getPayTotal())
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedDate", LocalDateTime.now())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("macId", dto.getMacId())
                .bind("memberCount", Parameters.in(R2dbcType.INTEGER, dto.getMemberCount()))
                .bind("sourceAcc", Parameters.in(R2dbcType.VARCHAR, dto.getSourceAcc()))
                .bind("expenseAcc", Parameters.in(R2dbcType.VARCHAR, dto.getExpenseAcc()))
                .bind("fromDate", dto.getFromDate())
                .bind("toDate", dto.getToDate())
                .bind("intgUpdStatus", Parameters.in(R2dbcType.VARCHAR, dto.getIntgUpdStatus()))
                .bind("deptCode", Parameters.in(R2dbcType.VARCHAR, dto.getDeptCode()))
                .bind("post", Util1.getBoolean(dto.getPost()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
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
                select v.description tran_option,s.labour_group_code,sum(iszero(s.in_bag,s.out_bag)) bag,s.comp_code
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
        return client.sql(sql)
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

    public Flux<LabourPaymentDto> history(ReportFilter filter) {
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
        return client.sql(sql)
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
        return client.sql(sql)
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

    public Flux<LabourPaymentDto> unUploadVoucher(LocalDateTime syncDate) {
        String sql = """
                select *
                from labour_payment
                where intg_upd_status is null
                and vou_date >= :syncDate
                """;
        return client.sql(sql)
                .bind("syncDate", syncDate)
                .map((row, rowMetadata) -> mapToRow(row)).all();
    }

    public LabourPaymentDto mapToRow(Row row) {
        return LabourPaymentDto.builder()
                .vouNo(row.get("vou_no", String.class))
                .compCode(row.get("comp_code", String.class))
                .deptId(row.get("dept_id", Integer.class))
                .vouDate(row.get("vou_date", LocalDateTime.class))
                .labourGroupCode(row.get("labour_group_code", String.class))
                .curCode(row.get("cur_code", String.class))
                .remark(row.get("remark", String.class))
                .payTotal(row.get("pay_total", Double.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .updatedBy(row.get("updated_by", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .macId(row.get("mac_id", Integer.class))
                .memberCount(row.get("member_count", Integer.class))
                .sourceAcc(row.get("source_acc", String.class))
                .expenseAcc(row.get("expense_acc", String.class))
                .deptCode(row.get("dept_code", String.class))
                .post(row.get("post", Boolean.class))
                .build();
    }

    public Mono<Boolean> updateACK(String ack, String vouNo, String compCode) {
        String sql = """
                update labour_payment
                set intg_upd_status = :ACK
                where vou_no =:vouNo
                and comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .bind("ACK", Parameters.in(R2dbcType.VARCHAR, ack))
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
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .fetch()
                .rowsUpdated()
                .thenReturn(true);
    }
}

