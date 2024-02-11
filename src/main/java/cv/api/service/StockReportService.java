package cv.api.service;

import cv.api.common.ClosingBalance;
import cv.api.common.ReportFilter;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.VStockBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockReportService {
    private final DatabaseClient client;

    private Mono<Long> calculateOpeningByPaddy(String opDate, String fromDate, String typeCode,
                                               String catCode, String brandCode, String stockCode,
                                               String compCode, Integer macId) {
        //opening
        String sql = """
                 insert into tmp_stock_opening(tran_date,stock_code,ttl_qty,ttl_wet,ttl_rice,ttl_bag,ttl_weight,ttl_amt,loc_code,unit,comp_code,dept_id,mac_id)
                 select :opDate op_date ,stock_code,sum(qty) ttl_qty, sum(wet) wet, sum(rice) rice, sum(bag) bag, sum(weight) ttl_weight,sum(ttl_amt)ttl_amt,loc_code,ifnull(weight_unit,'-') weight_unit,:compCode,1,:macId
                 from (
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(ttl_wet) wet, sum(ttl_rice) rice, sum(bag) bag,loc_code, weight_unit,sum(amount) ttl_amt
                 from v_opening
                 where date(op_date) = :opDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and tran_source = 3
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code
                 union all
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(ttl_wet) wet, sum(ttl_rice) rice, sum(bag) bag, loc_code, weight_unit,sum(pur_amt) ttl_amt
                 from v_purchase
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and s_rec = false
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code
                    union all
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(ttl_wet)*-1 wet, sum(ttl_rice)*-1 rice, sum(bag) bag, loc_code, weight_unit,sum(sale_amt) ttl_amt
                 from v_sale
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code ='01'
                 and deleted = false
                 and calculate = true
                 and s_pay = false
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (cat_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code
                    union all
                 select stock_code,0,sum(pay_qty)*-1 qty, 0, 0, sum(pay_bag)*-1, loc_code, '-',0
                 from v_stock_payment
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code
                 union all
                 select stock_code,sum(total_weight)*-1 weight,sum(qty)*-1 qty, sum(ttl_wet)*-1 wet, sum(ttl_rice)*-1 rice, sum(bag) bag, loc_code_from, weight_unit, sum(amount) ttl_amt
                 from v_transfer
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and loc_code_from in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code
                 union all
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(ttl_wet) wet, sum(ttl_rice) rice, sum(bag) bag, loc_code_to, weight_unit, sum(amount) ttl_amt
                 from v_transfer
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and loc_code_to in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code
                 union all
                 select stock_code,sum(total_weight) weight,sum(in_qty) qty, sum(ttl_wet) wet, sum(ttl_rice) rice, sum(in_bag) bag, loc_code, weight_unit, sum(amount) ttl_amt
                 from v_stock_io
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 and (in_qty>0 or in_bag>0)
                 group by stock_code
                 union all
                 select stock_code,sum(total_weight)*-1 weight,sum(out_qty)*-1 qty, sum(ttl_wet)*-1 wet, sum(ttl_rice)*-1 rice, sum(out_bag)*-1 bag, loc_code, weight_unit, sum(amount) ttl_amt
                 from v_stock_io
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 and (out_qty>0 or out_bag>0)
                 group by stock_code)a
                 group by stock_code;
                """;
        return deleteTmpOpening(macId).then(client.sql(sql)
                .bind("opDate", opDate)
                .bind("fromDate", fromDate)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .bind("compCode", compCode)
                .bind("macId", macId)
                .fetch()
                .rowsUpdated());
    }

    private Mono<Long> calculateClosingByPaddy(String fromDate, String toDate, String typeCode,
                                               String catCode, String brandCode, String stockCode,
                                               String compCode, Integer macId) {
        String opSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,op_qty,op_wet,op_rice,op_bag,op_weight,op_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'A-Opening',tran_date,'-','Opening',stock_code,sum(ttl_qty) ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(ttl_bag) ttl_bag, ifnull(sum(ttl_weight),0) ttl_weight,sum(ttl_amt),loc_code,mac_id,comp_code,dept_id
                from tmp_stock_opening tmp
                where mac_id =:macId
                group by tran_date,stock_code,mac_id,loc_code""";
        String purSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,pur_qty,pur_wet,pur_rice,pur_bag,pur_weight,pur_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Purchase',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(bag) ttl_bag, ifnull(sum(total_weight),0) ttl_weight,sum(pur_amt),loc_code,:macId,comp_code,dept_id
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and s_rec = false
                and comp_code =:compCode
                and loc_code in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by date(vou_date),vou_no,stock_code,loc_code""";
        String saleSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,sale_qty,sale_wet,sale_rice,sale_bag,sale_weight,sale_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Sale',vou_date vou_date,vou_no,remark,stock_code,sum(qty)*-1 ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(bag)*-1 ttl_bag, ifnull(sum(total_weight),0) ttl_weight,sum(sale_amt) ttl_amt,loc_code,:macId,comp_code,dept_id
                from v_sale
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and s_pay = false
                and comp_code =:compCode
                and loc_code in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (cat_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by date(vou_date),vou_no,stock_code,loc_code""";
        String tfSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,out_wet,out_rice,out_bag,out_weight,out_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Transfer-F',vou_date vou_date,vou_no,remark,stock_code,sum(qty)*-1 ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(bag)*-1 ttl_bag, ifnull(sum(total_weight)*-1,0) ttl_weight,sum(amount) ttl_amt,loc_code_from,:macId,comp_code,dept_id
                from v_transfer
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and loc_code_from in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by date(vou_date),vou_no,stock_code,loc_code_from""";
        String ttSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,in_wet,in_rice,in_bag,in_weight,in_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Transfer-T',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(bag) ttl_bag,ifnull(sum(total_weight),0) ttl_weight,sum(amount) ttl_amt,loc_code_to,:macId,comp_code,dept_id
                from v_transfer
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and loc_code_to in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by date(vou_date),vou_no,stock_code,loc_code_to""";
        String stockIn = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,in_wet,in_rice,in_bag,in_weight,in_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'StockIn',vou_date vou_date,vou_no,remark,stock_code,sum(in_qty) ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(in_bag) ttl_bag,ifnull(sum(total_weight),0) ttl_weight,sum(amount) ttl_amt,loc_code,:macId,comp_code,dept_id
                from v_stock_io
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and loc_code in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (in_qty>0 or in_bag>0)
                group by date(vou_date),vou_no,stock_code,loc_code""";
        String stockOut = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,out_wet,out_rice,out_bag,out_weight,out_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'StockOut',vou_date vou_date,vou_no,remark,stock_code,sum(out_qty)*-1 ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(out_bag)*-1 ttl_bag,ifnull(sum(total_weight)*-1,0) ttl_weight,sum(amount) ttl_amt,loc_code,:macId,comp_code,dept_id
                from v_stock_io
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and loc_code in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (out_qty>0 or out_bag>0)
                group by date(vou_date),vou_no,stock_code,loc_code""";
        String issueSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,out_wet,out_rice,out_bag,out_weight,out_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Issue',vou_date vou_date,vou_no,remark,stock_code,sum(pay_qty)*-1 ttl_qty,0 ttl_wet, 0 ttl_rice, sum(pay_bag)*-1 ttl_bag, 0 ttl_weight,0,loc_code,:macId,comp_code,dept_id
                from v_stock_payment
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and loc_code in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and tran_option='C'
                group by date(vou_date),vou_no,stock_code,loc_code""";
        Mono<Long> opMono = client.sql(opSql).bind("macId", macId).fetch().rowsUpdated();
        Mono<Long> purMono = client.sql(purSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated();
        Mono<Long> saleMono = client.sql(saleSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated();
// Continue with the existing code...

        Mono<Long> tfMono = client.sql(tfSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> ttMono = client.sql(ttSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> stockInMono = client.sql(stockIn)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();

        Mono<Long> stockOutMono = client.sql(stockOut)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();
        Mono<Long> issueMono = client.sql(issueSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();
        return deleteTmpIO(macId)
                .then(opMono)
                .doOnSuccess(rowsUpdated -> log.info("Operation rows updated: " + rowsUpdated))
                .doOnError(e -> log.error("Error in Operation: " + e.getMessage(), e))
                .then(purMono)
                .doOnSuccess(rowsUpdated -> log.info("Purchase rows updated: " + rowsUpdated))
                .doOnError(e -> log.error("Error in Purchase: " + e.getMessage(), e))
                .then(saleMono)
                .doOnSuccess(rowsUpdated -> log.info("Sale rows updated: " + rowsUpdated))
                .doOnError(e -> log.error("Error in Sale: " + e.getMessage(), e))
                .then(tfMono)
                .doOnSuccess(rowsUpdated -> log.info("Transfer-F rows updated: " + rowsUpdated))
                .doOnError(e -> log.error("Error in Transfer-F: " + e.getMessage(), e))
                .then(ttMono)
                .doOnSuccess(rowsUpdated -> log.info("Transfer-T rows updated: " + rowsUpdated))
                .doOnError(e -> log.error("Error in Transfer-T: " + e.getMessage(), e))
                .then(stockInMono)
                .doOnSuccess(rowsUpdated -> log.info("StockIn rows updated: " + rowsUpdated))
                .doOnError(e -> log.error("Error in StockIn: " + e.getMessage(), e))
                .then(stockOutMono)
                .doOnSuccess(rowsUpdated -> log.info("StockOut rows updated: " + rowsUpdated))
                .doOnError(e -> log.error("Error in StockOut: " + e.getMessage(), e))
                .then(issueMono)
                .doOnSuccess(rowsUpdated -> log.info("Issue rows updated: " + rowsUpdated))
                .doOnError(e -> log.error("Error in Issue: " + e.getMessage(), e))
                .doOnError(e -> log.error("calculateClosingByPaddy : " + e.getMessage(), e));

    }

    public Mono<ReturnObject> getStockInOutPaddy(ReportFilter filter) {
        String opDate = filter.getOpDate();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String typeCode = filter.getStockTypeCode();
        String catCode = filter.getCatCode();
        String brandCode = filter.getBrandCode();
        String stockCode = filter.getStockCode();
        String compCode = filter.getCompCode();
        Integer macId = filter.getMacId();
        int type = filter.getReportType();
        Mono<Long> opMono = calculateOpeningByPaddy(opDate, fromDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        Mono<Long> clMono = calculateClosingByPaddy(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        return opMono.then(clMono)
                .then(getResultStockQty(macId, type));
    }

    private Mono<ReturnObject> getResultStockQty(Integer macId, int type) {
        String sql = """
                select a.*,
                sum(ifnull(a.op_qty,0)+ifnull(a.pur_qty,0)+ifnull(a.in_qty,0)+ifnull(a.out_qty,0)+ifnull(a.sale_qty,0)) bal_qty,
                sum(ifnull(a.op_bag,0)+ifnull(a.pur_bag,0)+ifnull(a.in_bag,0)+ifnull(a.out_bag,0)+ifnull(a.sale_bag,0)) bal_bag,
                sum(ifnull(a.op_wet,0)+ifnull(a.pur_wet,0)+ifnull(a.in_wet,0)+ifnull(a.out_wet,0)+ifnull(a.sale_wet,0)) bal_wet,
                sum(ifnull(a.op_rice,0)+ifnull(a.pur_rice,0)+ifnull(a.in_rice,0)+ifnull(a.out_rice,0)+ifnull(a.sale_rice,0)) bal_rice,
                sum(ifnull(a.op_ttl_amt,0)+ifnull(a.pur_ttl_amt,0)+ifnull(a.in_ttl_amt,0)+ifnull(a.out_ttl_amt,0)+ifnull(a.sale_ttl_amt,0)) bal_amt,
                s.user_code s_user_code,s.stock_name,st.user_code st_user_code,
                st.stock_type_name,c.user_code c_user_code,c.cat_name
                from (
                select stock_code,comp_code,op_qty,pur_qty,in_qty,out_qty,sale_qty,op_bag,pur_bag,in_bag,out_bag,sale_bag,
                op_wet,round(op_wet/(iszero(op_qty,op_bag)),2) avg_op_wet,
                pur_wet,round(pur_wet/(iszero(pur_qty,pur_bag)),2)avg_pur_wet,
                in_wet,round(in_wet/(iszero(in_qty,in_bag)),2)avg_in_wet,
                out_wet,round(out_wet/(iszero(out_qty,out_bag)),2)avg_out_wet,
                sale_wet,round(sale_wet/(iszero(sale_qty,sale_bag)),2)avg_sale_wet,
                op_rice,round(op_rice/(iszero(op_qty,op_bag)),2)avg_op_rice,
                pur_rice,round(pur_rice/(iszero(pur_qty,pur_bag)),2)avg_pur_rice,
                in_rice,round(in_rice/(iszero(in_qty,in_bag)),2)avg_in_rice,
                out_rice,round(out_rice/(iszero(out_qty,out_bag)),2) avg_out_rice,
                sale_rice,round(sale_rice/(iszero(sale_qty,sale_bag)),2) avg_sale_rice,
                op_ttl_amt,round(op_ttl_amt/(iszero(op_qty,op_bag)),2)op_price,
                pur_ttl_amt,round(pur_ttl_amt/(iszero(pur_qty,pur_bag)),2)pur_price,
                in_ttl_amt,round(in_ttl_amt/(iszero(in_qty,in_bag)),2)in_price,
                out_ttl_amt,round(out_ttl_amt/(iszero(out_qty,out_bag)),2) out_price,
                sale_ttl_amt,round(sale_ttl_amt/(iszero(sale_qty,sale_bag)),2) sale_price
                from (
                select stock_code,loc_code,comp_code,
                sum(ifnull(op_qty,0)) op_qty,sum(ifnull(pur_qty,0)) pur_qty,
                sum(ifnull(in_qty,0)) in_qty,sum(ifnull(out_qty,0)) out_qty,
                sum(ifnull(sale_qty,0)) sale_qty,sum(ifnull(op_bag,0)) op_bag,
                sum(ifnull(pur_bag,0)) pur_bag,sum(ifnull(in_bag,0)) in_bag,
                sum(ifnull(out_bag,0)) out_bag,sum(ifnull(sale_bag,0)) sale_bag,
                sum(ifnull(op_wet,0)) op_wet,sum(ifnull(pur_wet,0)) pur_wet,
                sum(ifnull(in_wet,0)) in_wet,sum(ifnull(out_wet,0)) out_wet,
                sum(ifnull(sale_wet,0)) sale_wet,sum(ifnull(op_rice,0)) op_rice,
                sum(ifnull(pur_rice,0)) pur_rice,sum(ifnull(in_rice,0)) in_rice,
                sum(ifnull(out_rice,0)) out_rice,sum(ifnull(sale_rice,0)) sale_rice,
                sum(ifnull(op_ttl_amt,0)) op_ttl_amt,sum(ifnull(pur_ttl_amt,0)) pur_ttl_amt,
                sum(ifnull(in_ttl_amt,0)) in_ttl_amt,sum(ifnull(out_ttl_amt,0)) out_ttl_amt,
                sum(ifnull(sale_ttl_amt,0)) sale_ttl_amt
                from tmp_stock_io_column
                where mac_id = :macId
                group by stock_code
                )i)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                left join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join category c on s.category_code = c.cat_code
                and s.comp_code = c.comp_code
                group by a.stock_code
                having %s
                order by c_user_code,s_user_code
                """;
        String filter = null;
        switch (type) {
            case 0 -> filter = "bal_qty<>0";
            case 1 -> filter = "bal_bag<>0";
            case 2 -> filter = "(bal_qty<>0 and bal_bag<>0)";
        }
        sql = String.format(sql, filter);
        return client.sql(sql)
                .bind("macId", macId)
                .map((row) -> {
                    var cl = ClosingBalance.builder()
                            .openQty(Util1.toNull(row.get("op_qty", Double.class)))
                            .purQty(Util1.toNull(row.get("pur_qty", Double.class)))
                            .inQty(Util1.toNull(row.get("in_qty", Double.class)))
                            .saleQty(Util1.toNull(row.get("sale_qty", Double.class)))
                            .outQty(Util1.toNull(row.get("out_qty", Double.class)))
                            .balQty(row.get("bal_qty", Double.class))
                            .openBag(Util1.toNull(row.get("op_bag", Double.class)))
                            .purBag(Util1.toNull(row.get("pur_bag", Double.class)))
                            .inBag(Util1.toNull(row.get("in_bag", Double.class)))
                            .saleBag(Util1.toNull(row.get("sale_bag", Double.class)))
                            .outBag(Util1.toNull(row.get("out_bag", Double.class)))
                            .balBag(row.get("bal_bag", Double.class))
                            .openWet(Util1.toNull(row.get("avg_op_wet", Double.class)))
                            .purWet(Util1.toNull(row.get("avg_pur_wet", Double.class)))
                            .inWet(Util1.toNull(row.get("avg_in_wet", Double.class)))
                            .saleWet(Util1.toNull(row.get("avg_sale_wet", Double.class)))
                            .outWet(Util1.toNull(row.get("avg_out_wet", Double.class)))
                            .balWet(row.get("bal_wet", Double.class))
                            .openRice(Util1.toNull(row.get("avg_op_rice", Double.class)))
                            .purRice(Util1.toNull(row.get("avg_pur_rice", Double.class)))
                            .inRice(Util1.toNull(row.get("avg_in_rice", Double.class)))
                            .saleRice(Util1.toNull(row.get("avg_sale_rice", Double.class)))
                            .outRice(Util1.toNull(row.get("avg_out_rice", Double.class)))
                            .balRice(row.get("bal_rice", Double.class))
                            .opPrice(Util1.toNull(row.get("op_price", Double.class)))
                            .purPrice(Util1.toNull(row.get("pur_price", Double.class)))
                            .inPrice(Util1.toNull(row.get("in_price", Double.class)))
                            .outPrice(Util1.toNull(row.get("out_price", Double.class)))
                            .salePrice(Util1.toNull(row.get("sale_price", Double.class)))
                            .closingAmt(row.get("bal_amt", Double.class))
                            .stockUsrCode(row.get("s_user_code", String.class))
                            .stockName(row.get("stock_name", String.class))
                            .stockCode(row.get("stock_code", String.class))
                            .catName(row.get("cat_name", String.class))
                            .build();
                    double qty = Util1.isZero(cl.getBalQty(), cl.getBalBag());
                    if (qty > 0) {
                        cl.setBalWet(Util1.getDouble(cl.getBalWet()) / qty);
                        cl.setBalRice(Util1.getDouble(cl.getBalRice()) / qty);
                        cl.setClPrice(Util1.getDouble(cl.getClosingAmt()) / qty);
                    }
                    cl.setBalWet(Util1.toNull(cl.getBalWet()));
                    cl.setBalRice(Util1.toNull(cl.getBalRice()));
                    cl.setClPrice(Util1.toNull(cl.getClPrice()));
                    return cl;
                })
                .all()
                .collectList()
                .map(this::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private Mono<Long> deleteTmpOpening(Integer macId) {
        String sql = "delete from tmp_stock_opening where mac_id =:macId";
        return client.sql(sql).bind("macId", macId).fetch().rowsUpdated();
    }

    private Mono<Long> deleteTmpIO(Integer macId) {
        String sql = "delete from tmp_stock_io_column where mac_id=:macId";
        return client.sql(sql).bind("macId", macId).fetch().rowsUpdated();
    }

    public Mono<ReturnObject> getTransferSaleClosing(ReportFilter filter) {
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String compCode = filter.getCompCode();
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        Integer macId = filter.getMacId();
        String opDate = filter.getOpDate();
        String typeCode = filter.getStockTypeCode();
        String catCode = filter.getCatCode();
        String brandCode = filter.getBrandCode();
        String stockCode = filter.getStockCode();
        log.info("opDate Location : " + opDate);
        String sql = """
                select a.stock_code,s.user_code,s.stock_name,s.sale_price_n,
                sum(a.op_qty) op_qty,sum(a.sale_qty) sale_qty,sum(a.transfer_qty) transfer_qty
                from (
                select stock_code,sum(qty)op_qty,0 sale_qty,0 transfer_qty,comp_code
                from tmp_stock_balance
                where mac_id =:macId
                group by stock_code
                    union all
                select stock_code,0,sum(qty) sale_qty, 0 transfer_qty,comp_code
                from v_sale
                where deleted = false
                and comp_code =:compCode
                and date(vou_date) between :fromDate and :toDate
                and (loc_code =:locCode or'-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (cat_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                	union all
                select stock_code,0,0,sum(qty) transfer_qty,comp_code
                from v_transfer
                where deleted = false
                and comp_code =:compCode
                and date(vou_date) between :fromDate and :toDate
                and (loc_code_to =:locCode or'-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                )a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                group by a.stock_code
                order by s.user_code;
                """;
        Mono<ReturnObject> monoReturn = client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("locCode", locCode)
                .bind("macId", macId)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .map((row) -> VStockBalance.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .salePrice(row.get("sale_price_n", Double.class))
                        .opQty(row.get("op_qty", Double.class))
                        .saleQty(row.get("sale_qty", Double.class))
                        .transferQty(row.get("transfer_qty", Double.class))
                        .build())
                .all()
                .collectList()
                .map(this::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
        //stock_code, user_code, stock_name, sale_price_n, sale_qty, transfer_qty
        return deleteTmpClosing(macId)
                .then(calStockBalanceQty(opDate, fromDate, locCode, compCode, macId, typeCode, catCode, brandCode, stockCode))
                .then(monoReturn);
    }

    private Mono<Long> calStockBalanceQty(String opDate, String fromDate,
                                          String locCode, String compCode,
                                          Integer macId, String typeCode, String catCode,
                                          String brandCode, String stockCode) {
        String sql = """
                insert into tmp_stock_balance(stock_code, qty, loc_code,comp_code,mac_id)
                select stock_code,sum(qty) qty,loc_code,comp_code,:macId
                from (
                select stock_code,sum(qty) as qty,loc_code,comp_code
                from v_opening
                where deleted = false
                and tran_source =1
                and comp_code = :compCode
                and date(op_date) =:opDate
                and (loc_code=:locCode or '-' =:locCode)
                and calculate =true
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                	union all
                select stock_code,sum(qty) * - 1 as qty,loc_code,comp_code
                from v_sale
                where deleted = 0
                and date(vou_date)>=:opDate and date(vou_date)< :fromDate
                and comp_code = :compCode
                and (loc_code=:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (cat_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                	union all
                select stock_code,sum(in_qty),loc_code,comp_code
                from v_stock_io
                where in_qty is not null
                and in_unit is not null
                and deleted = 0
                and date(vou_date)>=:opDate and date(vou_date)< :fromDate
                and comp_code = :compCode
                and (loc_code=:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                	union all
                select stock_code,sum(out_qty) * - 1,loc_code,comp_code
                from v_stock_io
                where out_qty is not null
                and out_unit is not null
                and deleted = 0
                and date(vou_date)>=:opDate and date(vou_date)< :fromDate
                and comp_code = :compCode
                and (loc_code=:locCode or '-' =:locCode)
                and calculate =1
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                	union all
                select stock_code,sum(qty) * - 1,loc_code_from,comp_code
                from v_transfer
                where deleted = 0
                and date(vou_date)>=:opDate and date(vou_date)< :fromDate
                and comp_code = :compCode
                and (loc_code_from =:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                	union all
                select stock_code,sum(qty),loc_code_to,comp_code
                from v_transfer
                where deleted = 0
                and date(vou_date)>=:opDate and date(vou_date)< :fromDate
                and comp_code = :compCode
                and (loc_code_to =:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                )a
                group by stock_code
                """;
        return client.sql(sql)
                .bind("opDate", opDate)
                .bind("fromDate", fromDate)
                .bind("locCode", locCode)
                .bind("compCode", compCode)
                .bind("macId", macId)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch()
                .rowsUpdated();
    }

    private Mono<Long> deleteTmpClosing(Integer macId) {
        String delSql = "delete from tmp_stock_balance where mac_id =:macId";
        return client.sql(delSql).bind("macId", macId).fetch().rowsUpdated();
    }

    private byte[] convertToJsonBytes(Object data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            try (Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                Util1.gson.toJson(data, writer);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            // Handle the exception according to your application's error handling strategy
            return new byte[0]; // Or throw a custom exception
        }
    }

    private Mono<Long> calculateOpeningConsign(String opDate, String fromDate, String typeCode,
                                               String catCode, String brandCode, String stockCode,
                                               String traderCode, String locCode, String compCode, Integer macId) {
        //opening
        String sql = """
                insert into tmp_stock_opening(tran_date,trader_code,stock_code,loc_code,ttl_bag,comp_code,mac_id,dept_id)
                select :opDate,trader_code,stock_code,loc_code,sum(bag) bag,comp_code,:macId,1
                from (
                select trader_code,stock_code,loc_code,comp_code,sum(bag) bag
                from v_opening
                where date(op_date) = :opDate
                and comp_code =:compCode
                and deleted = false
                and calculate = true
                and tran_source = 4
                and (loc_code=:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                group by stock_code,trader_code
                	union all
                select trader_code,stock_code,loc_code,comp_code,sum(bag) bag
                from v_consign
                where date(vou_date) >=:opDate and date(vou_date)<:fromDate
                and comp_code =:compCode
                and deleted = false
                and tran_source =2
                and (loc_code=:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                group by stock_code,trader_code
                	union all
                select trader_code,stock_code,loc_code,comp_code,sum(bag)*-1 bag
                from v_consign
                where date(vou_date) >=:opDate and date(vou_date)<:fromDate
                and comp_code =:compCode
                and deleted = false
                and tran_source =1
                and (loc_code=:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                group by stock_code,trader_code
                )a
                group by stock_code,trader_code
                having bag<>0
                """;
        return deleteTmpOpening(macId).then(client.sql(sql)
                .bind("opDate", opDate)
                .bind("fromDate", fromDate)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .bind("compCode", compCode)
                .bind("locCode", locCode)
                .bind("macId", macId)
                .fetch()
                .rowsUpdated());
    }

    private Mono<Long> calculateClosingConsign(String fromDate, String toDate, String typeCode,
                                               String catCode, String brandCode, String stockCode,
                                               String traderCode, String locCode, String compCode, Integer macId) {
        String opSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,stock_code,trader_code,op_bag,loc_code,mac_id,comp_code,dept_id)
                select 'A-Opening',tran_date,'-',stock_code,trader_code,sum(ttl_bag) ttl_bag,'-',mac_id,comp_code,dept_id
                from tmp_stock_opening
                where mac_id =:macId
                group by stock_code,trader_code
                """;
        String inSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,stock_code,trader_code,in_bag,loc_code,mac_id,comp_code,dept_id)
                select 'StockIn',date(vou_date) vou_date,vou_no,stock_code,trader_code,sum(bag) ttl_bag,loc_code,:macId,comp_code,dept_id
                from v_consign
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and tran_source =2
                and (loc_code=:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                group by stock_code,trader_code
                """;
        String outSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,stock_code,trader_code,out_bag,loc_code,mac_id,comp_code,dept_id)
                select 'StockOut',date(vou_date) vou_date,vou_no,stock_code,trader_code,sum(bag)*-1 ttl_bag,loc_code,:macId,comp_code,dept_id
                from v_consign
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and tran_source =1
                and (loc_code=:locCode or '-' =:locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                group by stock_code,trader_code
                """;
        Mono<Long> opMono = client.sql(opSql)
                .bind("macId", macId)
                .fetch().rowsUpdated();
        Mono<Long> inMono = client.sql(inSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .bind("locCode", locCode)
                .fetch().rowsUpdated();
        Mono<Long> outMono = client.sql(outSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .bind("locCode", locCode)
                .fetch().rowsUpdated();
        return deleteTmpIO(macId)
                .then(opMono)
                .then(inMono)
                .then(outMono)
                .doOnError(e -> log.error("calculateClosingConsign : " + e.getMessage()));

    }

    public Mono<ReturnObject> getStockInOutConsign(ReportFilter filter) {
        String opDate = filter.getOpDate();
        String fromDate = filter.getFromDate();
        String toDate = filter.getToDate();
        String typeCode = filter.getStockTypeCode();
        String catCode = filter.getCatCode();
        String brandCode = filter.getBrandCode();
        String stockCode = filter.getStockCode();
        String compCode = filter.getCompCode();
        String locCode = filter.getLocCode();
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        Integer macId = filter.getMacId();
        Mono<Long> opMono = calculateOpeningConsign(opDate, fromDate, typeCode, catCode, brandCode, stockCode, traderCode, locCode, compCode, macId);
        Mono<Long> clMono = calculateClosingConsign(fromDate, toDate, typeCode, catCode, brandCode, stockCode, traderCode, locCode, compCode, macId);
        String sql = """
                select a.*,sum(a.op_bag+a.in_bag+a.out_bag) bal_bag,
                s.user_code s_user_code,s.stock_name,t.user_code t_user_code,t.trader_name
                from (
                select stock_code,trader_code,comp_code,sum(ifnull(op_bag,0)) op_bag,
                sum(ifnull(in_bag,0)) in_bag,sum(ifnull(out_bag,0)) out_bag
                from tmp_stock_io_column
                where mac_id = :macId
                group by stock_code,trader_code)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                and (a.op_bag<>0  or a.in_bag<>0 or a.out_bag <> 0)
                group by a.stock_code,a.trader_code
                order by t.user_code,s.user_code
                """;
        Mono<ReturnObject> monoRo = client.sql(sql)
                .bind("macId", macId)
                .map((row) -> {
                    return ClosingBalance.builder()
                            .openBag(Util1.toNull(row.get("op_bag", Double.class)))
                            .inBag(Util1.toNull(row.get("in_bag", Double.class)))
                            .outBag(Util1.toNull(row.get("out_bag", Double.class)))
                            .balBag(Util1.toNull(row.get("bal_bag", Double.class)))
                            .stockUsrCode(row.get("s_user_code", String.class))
                            .stockName(row.get("stock_name", String.class))
                            .traderUserCode(row.get("t_user_code", String.class))
                            .traderName(row.get("trader_name", String.class))
                            .build();
                    //stock_code, trader_code, comp_code, op_bag, in_bag, out_bag, bal_bag,
                    // s_user_code, stock_name, st_user_code, stock_type_name, c_user_code, cat_name
                }).all().collectList()
                .map(this::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());

        return deleteTmpIO(macId)
                .then(opMono)
                .then(clMono)
                .then(monoRo);

    }
}
