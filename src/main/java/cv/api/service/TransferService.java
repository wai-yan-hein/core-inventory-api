package cv.api.service;


import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.model.VTransfer;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransferService {
    private final DatabaseClient client;

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
        String deleted = String.valueOf(filter.isDeleted());
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
                .bind("deleted", Boolean.parseBoolean(deleted))
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
}
