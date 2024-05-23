package cv.api.service;

import cv.api.common.*;
import cv.api.entity.VStockBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockReportService {
    private final DatabaseClient client;
    private final LocationService locationService;
    private final OPHisService opHisService;
    private final TransactionalOperator operator;

    @Transactional
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
                 and (tran_source = 3 or tran_source =1)
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code,loc_code
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
                 group by stock_code,loc_code
                    union all
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(ttl_wet) wet, sum(ttl_rice) rice, sum(bag) bag, loc_code, weight_unit,sum(amt) ttl_amt
                 from v_return_in
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
                 group by stock_code,loc_code
                    union all
                 select stock_code,sum(total_weight)*-1 weight,sum(qty)*-1 qty, sum(ttl_wet)*-1 wet, sum(ttl_rice)*-1 rice, sum(bag)*-1 bag, loc_code, weight_unit,sum(amt) ttl_amt
                 from v_return_out
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and s_pay = false
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code,loc_code
                    union all
                 select stock_code,sum(total_weight) weight,sum(qty)*-1 qty, sum(ttl_wet)*-1 wet, sum(ttl_rice)*-1 rice, sum(bag)*-1 bag, loc_code, weight_unit,sum(sale_amt) ttl_amt
                 from v_sale
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and s_pay = false
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (cat_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code,loc_code
                    union all
                 select stock_code,0,sum(pay_qty)*-1 qty,0,0,sum(pay_bag)*-1, loc_code, '-',0
                 from v_stock_payment
                 where deleted = false
                 and comp_code =:compCode
                 and calculate = true
                 and tran_option ='C'
                 and date(vou_date) >=:opDate and date(vou_date)<:fromDate
                 and loc_code in (select f_code from f_location where mac_id =:macId)
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code,loc_code
                    union all
                 select stock_code,0,sum(pay_qty) qty,0,0,sum(pay_bag), loc_code, '-',0
                 from v_stock_payment
                 where deleted = false
                 and comp_code =:compCode
                 and calculate = true
                 and tran_option ='S'
                 and date(vou_date) >=:opDate and date(vou_date)<:fromDate
                 and loc_code in (select f_code from f_location where mac_id =:macId)
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code,loc_code
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
                 and skip_inv = false
                 group by stock_code,loc_code_from
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
                 and skip_inv = false
                 group by stock_code,loc_code_to
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
                 group by stock_code,loc_code
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
                 group by stock_code,loc_code)a
                 group by stock_code,loc_code;
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

    @Transactional
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
        String retInSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,in_wet,in_rice,in_bag,in_weight,in_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'ReturnIn',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(bag) ttl_bag, ifnull(sum(total_weight),0) ttl_weight,sum(amt),loc_code,:macId,comp_code,dept_id
                from v_return_in
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
        String retOutSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,out_wet,out_rice,out_bag,out_weight,out_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'ReturnOut',vou_date vou_date,vou_no,remark,stock_code,sum(qty)*-1 ttl_qty,sum(ttl_wet)*-1 ttl_wet, sum(ttl_rice)*-1 ttl_rice, sum(bag)*-1 ttl_bag, ifnull(sum(total_weight),0)*-1 ttl_weight,sum(amt)*-1,loc_code,:macId,comp_code,dept_id
                from v_return_out
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and s_pay = false
                and comp_code =:compCode
                and loc_code in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
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
                and skip_inv = false
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
                and skip_inv = false
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
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,sale_qty,sale_wet,sale_rice,sale_bag,sale_weight,sale_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Issue',date(vou_date) vou_date,vou_no,remark,stock_code,sum(pay_qty)*-1 ttl_qty,
                0,0, sum(pay_bag)*-1 ttl_bag,0 ttl_weight,0,loc_code,:macId,comp_code,dept_id
                from v_stock_payment
                where deleted = false
                and comp_code =:compCode
                and calculate = true
                and tran_option ='C'
                and date(vou_date) >= :fromDate and date(vou_date)<:toDate
                and loc_code in (select f_code from f_location where mac_id =:macId )
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by date(vou_date),vou_no,stock_code,loc_code
                """;
        String recSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,pur_qty,pur_wet,pur_rice,pur_bag,pur_weight,pur_ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Receive',date(vou_date) vou_date,vou_no,remark,stock_code,sum(pay_qty)*-1 ttl_qty,
                0 ttl_wet,0 ttl_rice, sum(pay_bag)*-1 ttl_bag,
                0 ttl_weight,0 amount,loc_code,:macId,comp_code,dept_id
                from v_stock_payment
                where deleted = false
                and comp_code =:compCode
                and calculate = true
                and tran_option ='S'
                and date(vou_date) >= :fromDate and date(vou_date)<:toDate
                and loc_code in (select f_code from f_location where mac_id =:macId )
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by date(vou_date),vou_no,stock_code,loc_code
                """;
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
        Mono<Long> retInMono = client.sql(retInSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated();
        Mono<Long> retOutMono = client.sql(retOutSql)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated();
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
        Mono<Long> recMono = client.sql(recSql)
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
                .then(purMono)
                .then(retInMono)
                .then(retOutMono)
                .then(saleMono)
                .then(tfMono)
                .then(ttMono)
                .then(stockInMono)
                .then(stockOutMono)
                .then(issueMono)
                .then(recMono);

    }

    public Mono<ReturnObject> getStockInOutPaddy(ReportFilter filter, boolean detail) {
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
                .flatMap(aLong -> {
                    if (detail) {
                        return getResultStockDetail(macId);
                    }
                    return getResultStockQty(macId, type);
                });
    }

    private Mono<ReturnObject> getResultStockQty(Integer macId, int type) {
        String sql = """
                select a.*,
                sum(ifnull(a.op_qty,0)+ifnull(a.pur_qty,0)+ifnull(a.in_qty,0)+ifnull(a.out_qty,0)+ifnull(a.sale_qty,0)) bal_qty,
                sum(ifnull(a.op_bag,0)+ifnull(a.pur_bag,0)+ifnull(a.in_bag,0)+ifnull(a.out_bag,0)+ifnull(a.sale_bag,0)) bal_bag,
                sum(ifnull(a.op_wet,0)+ifnull(a.pur_wet,0)+ifnull(a.in_wet,0)) bal_wet,
                sum(ifnull(a.op_rice,0)+ifnull(a.pur_rice,0)+ifnull(a.in_rice,0)) bal_rice,
                sum(ifnull(a.op_ttl_amt,0)+ifnull(a.pur_ttl_amt,0)+ifnull(a.in_ttl_amt,0))bal_amt,
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
            case 0 -> filter = "(op_qty<>0 or pur_qty<>0 or in_qty<>0 or out_qty<>0 or sale_qty<>0)";
            case 1 -> filter = "(op_bag<>0 or pur_bag<>0 or in_bag<>0 or out_bag<>0 or sale_bag<>0)";
            case 2 -> filter = "stock_code is not null";
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
                            .balAmount(row.get("bal_amt", Double.class))
                            .stockUsrCode(row.get("s_user_code", String.class))
                            .stockName(row.get("stock_name", String.class))
                            .stockCode(row.get("stock_code", String.class))
                            .catName(row.get("cat_name", String.class))
                            .build();
                    double qty = Util1.isZero(cl.getBalQty(), cl.getBalBag());
                    if (qty > 0) {
                        cl.setBalWet(Util1.getDouble(cl.getBalWet()) / qty);
                        cl.setBalRice(Util1.getDouble(cl.getBalRice()) / qty);
                        cl.setClPrice(Util1.getDouble(cl.getBalAmount()) / qty);
                    }
                    cl.setBalWet(Util1.toNull(cl.getBalWet()));
                    cl.setBalRice(Util1.toNull(cl.getBalRice()));
                    cl.setClPrice(Util1.toNull(cl.getClPrice()));
                    return cl;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private Mono<ReturnObject> getResultStockDetail(Integer macId) {
        String sql = """
                select a.*,ttl_wet/iszero(bal_qty,bal_bag) avg_bal_wet,
                ttl_rice/iszero(bal_qty,bal_bag) avg_bal_rice,
                ttl_amt/iszero(bal_qty,bal_bag) avg_bal_price
                from (
                select *,
                round(op_wet/(iszero(op_qty,op_bag)),2) avg_op_wet,
                round(pur_wet/(iszero(pur_qty,pur_bag)),2)avg_pur_wet,
                round(in_wet/(iszero(in_qty,in_bag)),2)avg_in_wet,
                round(out_wet/(iszero(out_qty,out_bag)),2)avg_out_wet,
                round(sale_wet/(iszero(sale_qty,sale_bag)),2)avg_sale_wet,
                round(op_rice/(iszero(op_qty,op_bag)),2)avg_op_rice,
                round(pur_rice/(iszero(pur_qty,pur_bag)),2)avg_pur_rice,
                round(in_rice/(iszero(in_qty,in_bag)),2)avg_in_rice,
                round(out_rice/(iszero(out_qty,out_bag)),2) avg_out_rice,
                round(sale_rice/(iszero(sale_qty,sale_bag)),2) avg_sale_rice,
                round(op_ttl_amt/(iszero(op_qty,op_bag)),2)op_price,
                round(pur_ttl_amt/(iszero(pur_qty,pur_bag)),2)pur_price,
                round(in_ttl_amt/(iszero(in_qty,in_bag)),2)in_price,
                round(out_ttl_amt/(iszero(out_qty,out_bag)),2) out_price,
                round(sale_ttl_amt/(iszero(sale_qty,sale_bag)),2) sale_price,
                ifnull(op_qty,0)+ifnull(pur_qty,0)+ifnull(in_qty,0)+ifnull(out_qty,0)+ifnull(sale_qty,0) bal_qty,
                ifnull(op_bag,0)+ifnull(pur_bag,0)+ifnull(in_bag,0)+ifnull(out_bag,0)+ifnull(sale_bag,0) bal_bag,
                ifnull(op_wet,0)+ifnull(pur_wet,0)+ifnull(in_wet,0) ttl_wet,
                ifnull(op_rice,0)+ifnull(pur_rice,0)+ifnull(in_rice,0) ttl_rice,
                ifnull(op_ttl_amt,0)+ifnull(pur_ttl_amt,0)+ifnull(in_ttl_amt,0) ttl_amt
                from tmp_stock_io_column
                where mac_id =:macId
                )a
                order by a.tran_date,a.tran_option,a.vou_no
                """;
        return client.sql(sql)
                .bind("macId", macId)
                .map((row) -> {
                    var cl = ClosingBalance.builder()
                            .vouDate(Util1.toDateStr(row.get("tran_date", LocalDate.class), "dd/MM/yyyy"))
                            .tranOption(row.get("tran_option", String.class))
                            .remark(row.get("remark", String.class))
                            .vouNo(row.get("vou_no", String.class))
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
                            .purWetTotal(row.get("pur_wet", Double.class))
                            .inWet(Util1.toNull(row.get("avg_in_wet", Double.class)))
                            .inWetTotal(row.get("in_wet", Double.class))
                            .saleWet(Util1.toNull(row.get("avg_sale_wet", Double.class)))
                            .outWet(Util1.toNull(row.get("avg_out_wet", Double.class)))
                            .balWet(row.get("avg_bal_wet", Double.class))
                            .openRice(Util1.toNull(row.get("avg_op_rice", Double.class)))
                            .purRice(Util1.toNull(row.get("avg_pur_rice", Double.class)))
                            .purRiceTotal(Util1.toNull(row.get("pur_rice", Double.class)))
                            .inRice(Util1.toNull(row.get("avg_in_rice", Double.class)))
                            .inRiceTotal(row.get("in_rice", Double.class))
                            .saleRice(Util1.toNull(row.get("avg_sale_rice", Double.class)))
                            .outRice(Util1.toNull(row.get("avg_out_rice", Double.class)))
                            .balRice(row.get("avg_bal_rice", Double.class))
                            .opPrice(Util1.toNull(row.get("op_price", Double.class)))
                            .purPrice(Util1.toNull(row.get("pur_price", Double.class)))
                            .purPriceTotal(row.get("pur_ttl_amt", Double.class))
                            .inPrice(Util1.toNull(row.get("in_price", Double.class)))
                            .inPriceTotal(row.get("in_ttl_amt", Double.class))
                            .outPrice(Util1.toNull(row.get("out_price", Double.class)))
                            .salePrice(Util1.toNull(row.get("sale_price", Double.class)))
                            .clPrice(row.get("avg_bal_price", Double.class))
                            .balWetTotal(row.get("ttl_wet", Double.class))
                            .balRiceTotal(row.get("ttl_rice", Double.class))
                            .balAmount(row.get("ttl_amt", Double.class))
                            .openAmt(row.get("op_ttl_amt", Double.class))
                            .build();
                    cl.setRemark(Util1.isNull(cl.getRemark(), cl.getTranOption()));
                    return cl;
                })
                .all()
                .collectList()
                .map(this::calOpCl)
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private List<ClosingBalance> calOpCl(List<ClosingBalance> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                ClosingBalance prv = list.get(i - 1);
                double balQty = Util1.getDouble(prv.getBalQty());
                double balBag = Util1.getDouble(prv.getBalBag());
                double balWet = Util1.getDouble(prv.getBalWetTotal());
                double balRice = Util1.getDouble(prv.getBalRiceTotal());
                double balAmt = Util1.getDouble(prv.getBalAmount());
                ClosingBalance c = list.get(i);
                c.setOpenQty(balQty);
                c.setOpenBag(balBag);
                c.setOpWetTotal(balWet);
                c.setOpRiceTotal(balRice);
                c.setOpenAmt(balAmt);
                //qty
                double opQty = Util1.getDouble(c.getOpenQty());
                double purQty = Util1.getDouble(c.getPurQty());
                double inQty = Util1.getDouble(c.getInQty());
                double outQty = Util1.getDouble(c.getOutQty());
                double saleQty = Util1.getDouble(c.getSaleQty());
                double clQty = opQty + purQty + inQty + outQty + saleQty;
                c.setBalQty(clQty);
                //bag
                double opBag = Util1.getDouble(c.getOpenBag());
                double purBag = Util1.getDouble(c.getPurBag());
                double inBag = Util1.getDouble(c.getInBag());
                double outBag = Util1.getDouble(c.getOutBag());
                double saleBag = Util1.getDouble(c.getSaleBag());
                double clBag = opBag + purBag + inBag + outBag + saleBag;
                c.setBalBag(clBag);
                //wet
                double opWet = Util1.getDouble(c.getOpWetTotal());
                double clWet = opWet + Util1.getDouble(c.getBalWetTotal());
                c.setBalWetTotal(clWet);
                //rice
                double opRice = Util1.getDouble(c.getOpRiceTotal());
                double clRice = opRice + Util1.getDouble(c.getBalRiceTotal());
                c.setBalRiceTotal(clRice);
                //price
                double opAmt = Util1.getDouble(c.getOpenAmt());
                double clAmt = opAmt + Util1.getDouble(c.getBalAmount());
                c.setBalAmount(clAmt);
                double qty = Util1.isZero(c.getBalQty(), c.getBalBag());
                if (qty > 0) {
                    c.setOpenWet(prv.getBalWet());
                    c.setOpenRice(prv.getBalRice());
                    c.setOpPrice(prv.getClPrice());
                    c.setBalWet(Util1.getDouble(c.getBalWetTotal()) / qty);
                    c.setBalRice(Util1.getDouble(c.getBalRiceTotal()) / qty);
                    c.setClPrice(Util1.getDouble(c.getBalAmount()) / qty);
                }
                c.setBalWet(Util1.toNull(c.getBalWet()));
                c.setBalRice(Util1.toNull(c.getBalRice()));
                c.setClPrice(Util1.toNull(c.getClPrice()));
            }

        }
        return list;
    }

    @Transactional
    private Mono<Long> deleteTmpOpening(Integer macId) {
        String sql = "delete from tmp_stock_opening where mac_id =:macId";
        return client.sql(sql).bind("macId", macId).fetch().rowsUpdated();
    }

    @Transactional
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
        log.info("opDate Location : {}", opDate);
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
                and skip_inv = false
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
                .map(Util1::convertToJsonBytes)
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

    @Transactional
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
                and skip_inv = false
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
                and skip_inv = false
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

    @Transactional
    private Mono<Long> deleteTmpClosing(Integer macId) {
        String delSql = "delete from tmp_stock_balance where mac_id =:macId";
        return client.sql(delSql).bind("macId", macId).fetch().rowsUpdated();
    }

    @Transactional
    private Mono<Long> calculateOpeningConsign(String opDate, String fromDate, String typeCode,
                                               String catCode, String brandCode, String stockCode,
                                               String traderCode, String locCode, String compCode, Integer macId) {
        //opening
        String sql = """
                insert into tmp_stock_opening(tran_date,trader_code,stock_code,loc_code,ttl_bag,comp_code,mac_id,dept_id,unit)
                select :opDate,trader_code,stock_code,loc_code,sum(bag) bag,comp_code,:macId,1,'-' unit
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

    @Transactional
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
                .doOnError(e -> log.error("calculateClosingConsign : {}", e.getMessage()));

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
                .map(Util1::convertToJsonBytes)
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

    public Mono<ReturnObject> getStockValueRO(ReportFilter filter) {
        return getStockValue(filter).collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    public Flux<StockValue> getStockValue(ReportFilter filter) {
        String opDate = filter.getOpDate();
        String toDate = Util1.addDay(filter.getToDate(), 1);
        String typeCode = Util1.isNull(filter.getStockTypeCode(), "-");
        String catCode = Util1.isNull(filter.getCatCode(), "-");
        String brandCode = Util1.isNull(filter.getBrandCode(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        Integer macId = filter.getMacId();
        Mono<Long> monoPrice = calculatePrice(toDate, opDate, stockCode, typeCode, catCode, brandCode, compCode, macId);
        Mono<Long> monoOp = calculateOpeningByPaddy(opDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        return monoPrice.then(monoOp)
                .thenMany(getStockValueResult(macId));
    }

    public Flux<StockValue> getStockBalanceByLocation(ReportFilter filter) {
        String opDate = filter.getOpDate();
        String toDate = Util1.addDay(filter.getToDate(), 1);
        String typeCode = Util1.isNull(filter.getStockTypeCode(), "-");
        String catCode = Util1.isNull(filter.getCatCode(), "-");
        String brandCode = Util1.isNull(filter.getBrandCode(), "-");
        String stockCode = Util1.isNull(filter.getStockCode(), "-");
        String compCode = filter.getCompCode();
        Integer macId = filter.getMacId();
        Mono<Long> monoOp = calculateOpeningByPaddy(opDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        return monoOp.thenMany(getStockBalanceByLocation(macId));
    }

    public Flux<ClosingBalance> getStockBalance(ReportFilter filter) {
        String opDate = filter.getOpDate();
        String toDate = Util1.addDay(filter.getToDate(), 1);
        String typeCode = Util1.isNull(filter.getStockTypeCode(), "-");
        String catCode = Util1.isNull(filter.getCatCode(), "-");
        String brandCode = Util1.isNull(filter.getBrandCode(), "-");
        String stockCode = filter.getStockCode();
        String compCode = filter.getCompCode();
        Integer macId = filter.getMacId();
        boolean summary = filter.isSummary();
        Mono<Long> monoOp = calculateOpeningByPaddy(opDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        String sql;
        if (summary) {
            sql = """
                    select a.*,s.stock_name,l.loc_name
                    from (
                    select stock_code,loc_code,sum(ttl_weight)ttl_weight,sum(ttl_qty) ttl_qty,
                    sum(ttl_wet)ttl_wet,sum(ttl_rice)ttl_rice,sum(ttl_bag)ttl_bag,sum(ttl_amt)ttl_amt,comp_code
                    from tmp_stock_opening
                    where mac_id =:macId
                    group by stock_code
                    )a
                    join stock s on a.stock_code = s.stock_code
                    and a.comp_code = s.comp_code
                    join location l on a.loc_code = l.loc_code
                    and a.comp_code = l.comp_code
                    """;
        } else {
            sql = """
                    select a.*,s.stock_name,l.loc_name
                    from (
                    select stock_code,loc_code,ttl_weight,ttl_qty,ttl_wet,ttl_rice,ttl_bag,ttl_amt,comp_code
                    from tmp_stock_opening
                    where mac_id =:macId
                    )a
                    join stock s on a.stock_code = s.stock_code
                    and a.comp_code = s.comp_code
                    join location l on a.loc_code = l.loc_code
                    and a.comp_code = l.comp_code
                    """;
        }
        Flux<ClosingBalance> flux = client.sql(sql)
                .bind("macId", macId)
                .map((row) -> ClosingBalance.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .locName(row.get("loc_name", String.class))
                        .balWeight(row.get("ttl_weight", Double.class))
                        .balQty(row.get("ttl_qty", Double.class))
                        .balWet(row.get("ttl_wet", Double.class))
                        .balRice(row.get("ttl_rice", Double.class))
                        .balBag(row.get("ttl_bag", Double.class))
                        .balAmount(row.get("ttl_amt", Double.class))
                        .build()).all()
                .switchIfEmpty(Flux.defer(() -> Flux.just(ClosingBalance.builder()
                        .stockName("No Stock.")
                        .locName("No Stock.")
                        .build())));
        return monoOp.thenMany(flux);
    }

    private Flux<StockValue> getStockValueResult(Integer macId) {
        String sql = """
                 select c.*,s.user_code,s.stock_name,st.stock_type_name,ct.cat_name
                from (
                select a.*,b.pur_avg_price,b.pur_recent_price,
                a.ttl_qty*b.pur_avg_price pur_avg_amt,a.ttl_qty*b.pur_recent_price pur_recent_amt
                from (
                select stock_code,sum(ttl_qty) ttl_qty,comp_code,mac_id
                from tmp_stock_opening
                where mac_id =:macId
                group by stock_code
                )a left join (
                select stock_code,sum(ifnull(pur_avg_price,0))pur_avg_price,
                sum(ifnull(pur_recent_price,0))pur_recent_price,mac_id
                from tmp_stock_price
                where mac_id =:macId
                group by stock_code
                )b
                on a.stock_code = b.stock_code
                and a.mac_id  = b.mac_id
                where a.mac_id =:macId
                and a.ttl_qty<>0
                )c
                join stock s on c.stock_code = s.stock_code
                and s.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join category ct on s.category_code= ct.cat_code
                and s.comp_code = ct.comp_code
                order by user_code
                """;
        return client.sql(sql).bind("macId", macId)
                .map((row, rowMetadata) -> StockValue.builder()
                        .stockTypeName(row.get("stock_type_name", String.class))
                        .catName(row.get("cat_name", String.class))
                        .stockUserCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .qty(row.get("ttl_qty", Double.class))
                        .purAvgPrice(Util1.toNull(row.get("pur_avg_price", Double.class)))
                        .purAvgAmount(Util1.toNull(row.get("pur_avg_amt", Double.class)))
                        .recentPrice(Util1.toNull(row.get("pur_recent_price", Double.class)))
                        .recentAmt(Util1.toNull(row.get("pur_recent_amt", Double.class)))
                        .build()).all();
    }

    private Flux<StockValue> getStockBalanceByLocation(Integer macId) {
        String sql = """
                select a.*,s.user_code,s.stock_name,st.stock_type_name,ct.cat_name,l.loc_name
                from (
                select stock_code,loc_code,comp_code,ttl_qty,ttl_bag
                from tmp_stock_opening
                where mac_id =:macId
                )a
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                join stock s on a.stock_code = s.stock_code
                and s.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join category ct on s.category_code= ct.cat_code
                and s.comp_code = ct.comp_code
                """;
        return client.sql(sql).bind("macId", macId)
                //ttl_qty, ttl_bag, user_code, stock_name, stock_type_name, cat_name, loc_name
                .map((row, rowMetadata) -> StockValue.builder()
                        .qty(row.get("ttl_qty", Double.class))
                        .bag(row.get("ttl_bag", Double.class))
                        .stockUserCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .stockTypeName(row.get("stock_type_name", String.class))
                        .catName(row.get("cat_name", String.class))
                        .locName(row.get("loc_name", String.class))
                        .build()).all();
    }

    private Mono<Long> calculatePrice(String toDate, String opDate, String stockCode,
                                      String typeCode, String catCode, String brandCode,
                                      String compCode, Integer macId) {
        String delSql = """
                delete from tmp_stock_price where mac_id =:macId
                """;
        String purSql = """
                insert into tmp_stock_price(tran_option,stock_code,pur_avg_price,mac_id)
                select 'PUR-AVG',stock_code,avg(pur_price),:macId
                from (
                select 'PUR-AVG',stock_code,pur_price
                from v_purchase
                where deleted = false
                and date(vou_date) <=:toDate
                and comp_code =:compCode
                and (stock_type_code =:typeCode or '-' =:typeCode)
                and (category_code =:catCode or '-' =:catCode)
                and (brand_code =:brandCode or '-' =:brandCode)
                and (stock_code =:stockCode or '-' =:stockCode)
                group by stock_code,pur_price
                	union all
                select 'OP',stock_code,price
                from v_opening
                where price > 0
                and deleted = false
                and date(op_date) <=:opDate
                and comp_code =:compCode
                and (stock_type_code =:typeCode or '-' =:typeCode)
                and (category_code =:catCode or '-' =:catCode)
                and (brand_code =:brandCode or '-' =:brandCode)
                and (stock_code =:stockCode or '-' =:stockCode)
                )a
                group by stock_code
                """;
        String purRecentSql = """
                insert into tmp_stock_price(stock_code,tran_option,pur_recent_price,mac_id)
                select a.stock_code,'PUR_RECENT',a.pur_price,:macId
                from (
                with rows_and_position as
                (
                select stock_code, pur_price,row_number() over (partition by stock_code order by vou_date desc) as position,comp_code,dept_id
                from v_purchase
                where deleted = false
                and date(vou_date) between :opDate and :toDate
                and comp_code =:compCode
                and (stock_type_code =:typeCode or '-' =:typeCode)
                and (category_code =:catCode or '-' =:catCode)
                and (brand_code =:brandCode or '-' =:brandCode)
                and (stock_code =:stockCode or '-' =:stockCode))
                select stock_code,pur_price,comp_code
                from rows_and_position
                where position =1
                )a
                """;
        Mono<Long> delMono = client.sql(delSql)
                .bind("macId", macId)
                .fetch().rowsUpdated();
        Mono<Long> purMono = client.sql(purSql)
                .bind("toDate", toDate)
                .bind("opDate", opDate)
                .bind("compCode", compCode)
                .bind("macId", macId)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("typeCode", typeCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated();
        Mono<Long> purRecentMono = client.sql(purRecentSql)
                .bind("toDate", toDate)
                .bind("opDate", opDate)
                .bind("compCode", compCode)
                .bind("macId", macId)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("typeCode", typeCode)
                .bind("stockCode", stockCode)
                .fetch().rowsUpdated();
        return delMono.then(purMono).then(purRecentMono);
    }

    public Flux<ClosingBalance> getStockBalanceQty(ReportFilter filter) {
        String compCode = filter.getCompCode();
        Integer macId = filter.getMacId();
        return operator.transactional(Flux.defer(() -> locationService.insertTmp(filter.getListLocation(), compCode, macId, "-")
                .flatMapMany(aBoolean -> opHisService.getOpeningDateByLocation(compCode, "-")
                        .flatMapMany(opDate -> {
                            filter.setOpDate(opDate);
                            return getStockBalance(filter);
                        }))));
    }
}
