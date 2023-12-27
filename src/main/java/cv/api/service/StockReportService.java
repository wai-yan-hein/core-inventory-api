package cv.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockReportService {
    private final R2dbcEntityTemplate template;
    private final DatabaseClient client;

    private Mono<Long> calculateOpeningByPaddy(String opDate, String fromDate, String typeCode,
                                               String catCode, String brandCode, String stockCode,
                                               String compCode, Integer macId) {
        //delete tmp
        //opening
        String sql = """
                 insert into tmp_stock_opening(tran_date,stock_code,ttl_qty,ttl_wet,ttl_rice,ttl_bag,ttl_weight,ttl_amt,loc_code,unit,comp_code,dept_id,mac_id)
                 select :opDate op_date ,stock_code,sum(qty) ttl_qty, sum(wet) wet, sum(rice) rice, sum(bag) bag, sum(weight) ttl_weight,ttl_amt,loc_code,ifnull(weight_unit,'-') weight_unit,:compCode,  deptId  , :macId
                 from (
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(wet) wet, sum(rice) rice, sum(bag) bag,loc_code, weight_unit,sum(amount) ttl_amt
                 from v_opening
                 where date(op_date) = :opDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and tran_source = 1
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code,loc_code
                 union all
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(wet) wet, sum(rice) rice, sum(bag) bag, loc_code, weight_unit,sum(pur_amt) ttl_amt
                 from v_purchase
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code, loc_code
                 union all
                 select stock_code,sum(total_weight)*-1 weight,sum(qty)*-1 qty, sum(wet) wet, sum(rice) rice, sum(bag) bag, loc_code_from, weight_unit, sum(amount) ttl_amt
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
                 group by stock_code, loc_code_from
                 union all
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(wet) wet, sum(rice) rice, sum(bag) bag, loc_code_to, weight_unit, sum(amount) ttl_amt
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
                 group by stock_code, loc_code_to
                 union all
                 select stock_code,sum(total_weight) weight,sum(in_qty) qty, sum(wet) wet, sum(rice) rice, sum(bag) bag, loc_code, weight_unit, sum(amount) ttl_amt
                 from v_stock_io
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 and in_qty>0
                 group by stock_code, loc_code
                 union all
                 select stock_code,sum(total_weight)*-1 weight,sum(out_qty)*-1 qty, sum(wet) wet, sum(rice) rice, sum(bag) bag, loc_code, weight_unit, sum(amount) ttl_amt
                 from v_stock_io
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 and out_qty>0
                 group by stock_code, loc_code
                 union all
                 select stock_code,sum(total_weight) weight,sum(qty)*-1 qty, 0 wet, 0 rice, sum(bag)*-1 bag, loc_code, weight_unit,0 ttl_amt
                 from v_sale
                 where date(vou_date) >= :opDate and date(vou_date)<:fromDate
                 and comp_code =:compCode
                 and deleted = false
                 and calculate = true
                 and loc_code in (select f_code from f_location where mac_id =:macId )
                 and (stock_type_code = :typeCode or '-' = :typeCode)
                 and (brand_code = :brandCode or '-' = :brandCode)
                 and (category_code = :catCode or '-' = :catCode)
                 and (stock_code = :stockCode or '-' = :stockCode)
                 group by stock_code, loc_code
                 )a
                 group by stock_code, loc_code;
                """;
        return client.sql(sql)
                .bind("opDate", opDate)
                .bind("fromDate", fromDate)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .bind("compCode", compCode)
                .bind("macId", macId)
                .fetch()
                .rowsUpdated();
    }

    private Mono<Long> calculateClosingByPaddy(String fromDate, String toDate, String typeCode,
                                               String catCode, String brandCode, String stockCode,
                                               String compCode, Integer macId) {
        String opSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,op_qty,wet,rice,op_bag,op_weight,ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'A-Opening',tran_date,'-','Opening',stock_code,sum(ttl_qty) ttl_qty,sum(ttl_wet) ttl_wet, sum(ttl_rice) ttl_rice, sum(ttl_bag) ttl_bag, sum(ttl_weight) ttl_weight,sum(ttl_amt),loc_code,mac_id,comp_code,dept_id
                from tmp_stock_opening tmp
                where mac_id =:macId
                group by tran_date,stock_code,mac_id,loc_code""";
        String purSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,pur_qty,wet,rice,pur_bag,pur_weight,ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Purchase',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(bag) ttl_bag, sum(total_weight) ttl_weight,sum(pur_price),loc_code,:macId,comp_code,dept_id
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and loc_code in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by date(vou_date),vou_no,stock_code,loc_code""";
        String tfSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,wet,rice,out_bag,out_weight,ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Transfer-F',vou_date vou_date,vou_no,remark,stock_code,sum(qty)*-1 ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(bag) ttl_bag, sum(total_weight)*-1 ttl_weight,sum(amount) ttl_amt,loc_code_from,:macId,comp_code,dept_id
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
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,wet,rice,in_bag,in_weight,ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Transfer-T',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(bag) ttl_bag,sum(total_weight) ttl_weight,sum(amount) ttl_amt,loc_code_to,:macId,comp_code,dept_id
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
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,wet,rice,in_bag,in_weight,ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'StockIn',vou_date vou_date,vou_no,remark,stock_code,sum(in_qty) ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(bag) ttl_bag,sum(total_weight) ttl_weight,sum(amount) ttl_amt,loc_code,:macId,comp_code,dept_id
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
                and in_qty>0
                group by date(vou_date),vou_no,stock_code,loc_code""";
        String stockOut = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,wet,rice,out_bag,out_weight,ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'StockOut',vou_date vou_date,vou_no,remark,stock_code,sum(out_qty)*-1 ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(bag) ttl_bag,sum(total_weight)*-1 ttl_weight,sum(amount) ttl_amt,loc_code,:macId,comp_code,dept_id
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
                and out_qty>0
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
        String sqlSql = """
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,pur_qty,wet,rice,pur_bag,pur_weight,ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'Sale',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(bag) ttl_bag, sum(total_weight) ttl_weight,sum(pur_price),loc_code,:macId,comp_code,dept_id
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and calculate = true
                and comp_code =:compCode
                and loc_code in (select f_code from f_location where mac_id =:macId)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by date(vou_date),vou_no,stock_code,loc_code""";
// Continue with the existing code...
        return deleteTmpIO(macId)
                .then(opMono)
                .then(purMono)
                .then(tfMono)
                .then(ttMono)
                .then(stockInMono)
                .then(stockOutMono)
                .doOnError(e -> {
                    log.error("calculateClosingByPaddy : " + e.getMessage());
                });
    }

    private Mono<Long> deleteTmpOpening(Integer macId) {
        String sql = "delete from tmp_stock_opening where mac_id = " + macId;
        return client.sql(sql).bind("macId", macId).fetch().rowsUpdated();
    }

    private Mono<Long> deleteTmpIO(Integer macId) {
        String sql = "delete from tmp_stock_io_column where mac_id = " + macId;
        return client.sql(sql).bind("macId", macId).fetch().rowsUpdated();
    }
}
