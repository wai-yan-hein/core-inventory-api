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
                 select :opDate op_date ,stock_code,sum(qty) ttl_qty, sum(wet) wet, sum(rice) rice, sum(bag) bag, sum(weight) ttl_weight,ttl_amt,loc_code,ifnull(weight_unit,'-') weight_unit,:compCode,1,:macId
                 from (
                 select stock_code,sum(total_weight) weight,sum(qty) qty, sum(wet) wet, sum(rice) rice, sum(bag) bag,loc_code, weight_unit,sum(amount) ttl_amt
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
                 select stock_code,sum(total_weight) weight,sum(in_qty) qty, sum(wet) wet, sum(rice) rice, sum(in_bag) bag, loc_code, weight_unit, sum(amount) ttl_amt
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
                 select stock_code,sum(total_weight)*-1 weight,sum(out_qty)*-1 qty, sum(wet) wet, sum(rice) rice, sum(out_bag)*-1 bag, loc_code, weight_unit, sum(amount) ttl_amt
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
                 group by stock_code, loc_code)a
                 group by stock_code, loc_code;
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
                select 'Transfer-F',vou_date vou_date,vou_no,remark,stock_code,sum(qty)*-1 ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(bag)*-1 ttl_bag, sum(total_weight)*-1 ttl_weight,sum(amount) ttl_amt,loc_code_from,:macId,comp_code,dept_id
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
                select 'StockIn',vou_date vou_date,vou_no,remark,stock_code,sum(in_qty) ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(in_bag) ttl_bag,sum(total_weight) ttl_weight,sum(amount) ttl_amt,loc_code,:macId,comp_code,dept_id
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
                insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,wet,rice,out_bag,out_weight,ttl_amt,loc_code,mac_id,comp_code,dept_id)
                select 'StockOut',vou_date vou_date,vou_no,remark,stock_code,sum(out_qty)*-1 ttl_qty,sum(wet) ttl_wet, sum(rice) ttl_rice, sum(out_bag)*-1 ttl_bag,sum(total_weight)*-1 ttl_weight,sum(amount) ttl_amt,loc_code,:macId,comp_code,dept_id
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
        return deleteTmpIO(macId)
                .then(opMono)
                .then(purMono)
                .then(tfMono)
                .then(ttMono)
                .then(stockInMono)
                .then(stockOutMono)
                .doOnError(e -> log.error("calculateClosingByPaddy : " + e.getMessage()));
    }

    public Mono<ReturnObject> getStockInOutPaddy(ReportFilter filter) {
        String opDate = filter.getOpDate();
        log.info(opDate);
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
        if (type == 1) {
            return opMono.then(clMono)
                    .then(getResultStockBag(macId));
        } else {
            return opMono.then(clMono)
                    .then(getResultStockQty(macId));
        }
    }

    private Mono<ReturnObject> getResultStockQty(Integer macId) {
        String sql = """
                select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty) bal_qty,
                s.user_code s_user_code,s.stock_name,st.user_code st_user_code,
                st.stock_type_name,l.loc_name, w.description,c.user_code c_user_code,c.cat_name
                from (
                select stock_code,loc_code,sum(ifnull(op_qty,0)) op_qty,sum(ifnull(pur_qty,0)) pur_qty,
                sum(ifnull(in_qty,0)) in_qty,sum(ifnull(out_qty,0)) out_qty,sum(ifnull(sale_qty,0)) sale_qty,comp_code,
                sum(ifnull(wet,0)) wet, sum(ifnull(rice,0)) rice, sum(ifnull(ttl_amt,0)) ttl_amt
                from tmp_stock_io_column
                where mac_id = :macId
                group by stock_code)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                join category c on s.category_code = c.cat_code
                and s.comp_code = c.comp_code
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                join warehouse w on l.warehouse_code = w.code
                and a.comp_code = l.comp_code
                group by a.stock_code
                having bal_qty <>0
                order by c_user_code, s_user_code
                """;
        return client.sql(sql)
                .bind("macId", macId)
                .map((row) -> ClosingBalance.builder()
                        .openQty(Util1.toNull(row.get("op_qty", Double.class)))
                        .purQty(Util1.toNull(row.get("pur_qty", Double.class)))
                        .inQty(Util1.toNull(row.get("in_qty", Double.class)))
                        .saleQty(Util1.toNull(row.get("sale_qty", Double.class)))
                        .outQty(Util1.toNull(row.get("out_qty", Double.class)))
                        .balQty(Util1.toNull(row.get("bal_qty", Double.class)))
                        .stockUsrCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .catName(row.get("cat_name", String.class))
                        .locName(row.get("loc_name", String.class))
                        .wet(row.get("wet", Double.class))
                        .rice(row.get("rice", Double.class))
                        .warehouse(row.get("description", String.class))
                        .build())
                .all()
                .collectList()
                .map(this::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private Mono<ReturnObject> getResultStockBag(Integer macId) {
        String sql = """
                select a.*,sum(a.op_bag+a.pur_bag+a.in_bag+a.out_bag) bal_bag,
                s.user_code s_user_code,s.stock_name,st.user_code st_user_code,
                st.stock_type_name,l.loc_name, w.description,c.user_code c_user_code,c.cat_name
                from (
                select stock_code,loc_code,sum(ifnull(op_bag,0)) op_bag,sum(ifnull(pur_bag,0)) pur_bag,
                sum(ifnull(in_bag,0)) in_bag,sum(ifnull(out_bag,0)) out_bag,sum(ifnull(sale_bag,0)) sale_bag,comp_code,
                sum(ifnull(wet,0)) wet, sum(ifnull(rice,0)) rice, sum(ifnull(ttl_amt,0)) ttl_amt
                from tmp_stock_io_column
                where mac_id = :macId
                group by stock_code)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                join category c on s.category_code = c.cat_code
                and s.comp_code = c.comp_code
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                join warehouse w on l.warehouse_code = w.code
                and a.comp_code = l.comp_code
                group by a.stock_code
                having bal_bag <>0
                order by c_user_code, s_user_code""";
        return client.sql(sql)
                .bind("macId", macId)
                .map((row) -> ClosingBalance.builder()
                        .openBag(Util1.toNull(row.get("op_bag", Double.class)))
                        .purBag(Util1.toNull(row.get("pur_bag", Double.class)))
                        .inBag(Util1.toNull(row.get("in_bag", Double.class)))
                        .saleBag(Util1.toNull(row.get("sale_bag", Double.class)))
                        .outBag(Util1.toNull(row.get("out_bag", Double.class)))
                        .balBag(Util1.toNull(row.get("bal_bag", Double.class)))
                        .stockUsrCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .catName(row.get("cat_name", String.class))
                        .locName(row.get("loc_name", String.class))
                        .wet(row.get("wet", Double.class))
                        .rice(row.get("rice", Double.class))
                        .warehouse(row.get("description", String.class))
                        .build())
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

}
