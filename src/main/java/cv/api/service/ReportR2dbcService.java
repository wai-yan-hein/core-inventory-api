package cv.api.service;

import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.entity.ReorderKey;
import cv.api.entity.ReorderLevel;
import cv.api.model.VPurchase;
import cv.api.report.model.Income;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportR2dbcService {
    private final DatabaseClient client;

    public Mono<ReturnObject> getTopPurchase(String fromDate, String toDate, String compCode, String stockCode,
                                             String groupCode, String catCode, String brandCode, String locCode) {
        return getTopPurchaseList(fromDate, toDate, compCode, stockCode, groupCode, catCode, brandCode, locCode)
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    public Mono<List<VPurchase>> getTopPurchaseList(String fromDate, String toDate, String compCode, String stockCode,
                                                    String groupCode, String catCode, String brandCode, String locCode) {
        String sql = """
                select a.*,c.cat_name,round(sum(total_wet)/sum(qty),2) avg_wet,
                round(sum(total_rice)/sum(qty),2) avg_rice,
                round(sum(vou_total)/sum(qty),2) avg_price,sum(vou_total) ttl_vou_total,sum(qty) total_qty
                from (
                select category_code,stock_code,s_user_code,stock_name,wet,rice, qty,bag,pur_price,
                wet*qty total_wet,rice*qty total_rice, pur_amt,comp_code,vou_no,vou_total
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and deleted =false
                and comp_code = :compCode
                and (stock_type_code= :typeCode or '-'=:typeCode)
                and (brand_code= :brandCode or '-'=:brandCode)
                and (category_code=:catCode or '-'=:catCode)
                and (loc_code= :locCode or '-'=:locCode)
                and (stock_code= :stockCode or '-'=:stockCode)
                )a
                left join category c on a.category_code = c.cat_code
                and a.comp_code = c.comp_code
                group by stock_code
                order by total_qty desc
                """;
        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", groupCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("locCode", locCode)
                .bind("stockCode", stockCode)
                .map((row) -> VPurchase.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .stockUserCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .avgWet(row.get("avg_wet", Double.class))
                        .avgPrice(row.get("avg_price", Double.class))
                        .avgRice(row.get("avg_rice", Double.class))
                        .groupName(row.get("cat_name", String.class))
                        .vouTotal(row.get("ttl_vou_total", Double.class))
                        .qty(row.get("total_qty", Double.class))
                        .build())
                .all()
                .collectList()
                .map(list -> {
                    double totalQty = list.stream()
                            .filter(v -> Objects.nonNull(v.getQty())) // Filter out null values
                            .mapToDouble(VPurchase::getQty) // Map to double
                            .sum(); // Perform the sum operation
                    if (!list.isEmpty()) {
                        list.forEach(t -> t.setQtyPercent((t.getQty() / totalQty) * 100));
                    }
                    return list;
                });

    }

    public Flux<Income> getIncome(String fromDate, String toDate, String compCode,String headCode) {
        String sql = """
                select 'IC' tran_group,'SALE' tran_option,date(vou_date) vou_date,sum(vou_total) vou_total,sum(paid) vou_paid,count(*) vou_count,cur_code,comp_code
                from sale_his
                where deleted =false
                and comp_code =:compCode
                and date(vou_date) between :fromDate and :toDate
                group by date(vou_date),cur_code,comp_code
                	union
                select 'PC','PURCHASE' tran_option,date(vou_date) vou_date,sum(vou_total) vou_total,sum(paid) paid,count(*) vou_count,cur_code,comp_code
                from pur_his
                where deleted =false
                and comp_code =:compCode
                and date(vou_date) between :fromDate and :toDate
                group by date(vou_date),cur_code,comp_code
                	union
                select 'RI','RETURN_IN' tran_option,date(vou_date) vou_date,sum(vou_total) vou_total,sum(paid) paid,count(*) vou_count,cur_code,comp_code
                from ret_in_his
                where deleted =false
                and comp_code =:compCode
                and date(vou_date) between :fromDate and :toDate
                group by date(vou_date),cur_code,comp_code
                	union
                select 'RO','RETURN_OUT' tran_option,date(vou_date) vou_date,sum(vou_total) vou_total,sum(paid) paid,count(*) vou_count,cur_code,comp_code
                from ret_out_his
                where deleted =false
                and comp_code =:compCode
                and date(vou_date) between :fromDate and :toDate
                group by date(vou_date),cur_code,comp_code
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map((row) -> Income.builder()
                        .compCode(headCode)
                        .tranGroup(row.get("tran_group", String.class))
                        .tranOption(row.get("tran_option", String.class))
                        .currency(row.get("cur_code", String.class))
                        .tranDate(row.get("vou_date", LocalDate.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .vouPaid(row.get("vou_paid", Double.class))
                        .vouCount(row.get("vou_count", Integer.class))
                        .build()).all();
    }
    public Flux<ReorderLevel> getReorderLevel(String opDate, String clDate, String typeCode, String catCode, String brandCode,
                                              String stockCode, boolean calSale, boolean calPur, boolean calRI,
                                              boolean calRo, String locCode, String compCode,
                                              Integer deptId, Integer macId) {
        String sql = """
                select *,if(small_bal_qty<small_min_qty,1,if(small_bal_qty>small_min_qty,2,if(small_bal_qty<small_max_qty,3,if(small_bal_qty> small_max_qty,4,5)))) position
                from (
                select a.*,rel.rel_name,bal_qty*rel.smallest_qty small_bal_qty,min_qty*ifnull(rel1.smallest_qty,0) small_min_qty,max_qty*ifnull(rel2.smallest_qty,0) small_max_qty
                from (
                select tmp.stock_code,tmp.loc_code,tmp.smallest_qty bal_qty, tmp.unit bal_unit,ifnull(min_qty,0) min_qty,min_unit,
                ifnull(max_qty,0) max_qty,max_unit,tmp.comp_code,tmp.dept_id,s.rel_code,s.user_code,s.stock_name,l.loc_name
                from tmp_stock_balance tmp
                left join reorder_level r
                on tmp.stock_code= r.stock_code
                and tmp.comp_code = r.comp_code
                and tmp.loc_code = r.loc_code
                and tmp.mac_id = :macId
                and tmp.comp_code = :compCode
                join stock s on tmp.stock_code = s.stock_code
                and tmp.comp_code = s.comp_code
                join location l on tmp.loc_code = l.loc_code
                and tmp.comp_code = l.comp_code ) a
                join v_relation rel
                on a.rel_code = rel.rel_code
                and a.bal_unit = rel.unit
                and a.comp_code = rel.comp_code
                left join v_relation rel1
                on a.rel_code = rel1.rel_code
                and a.min_unit = rel1.unit
                and a.comp_code = rel1.comp_code
                left join v_relation rel2
                on a.rel_code = rel2.rel_code
                and a.max_unit = rel2.unit
                and a.comp_code = rel2.comp_code )b
                order by position,small_bal_qty
                """;

        return client.sql(sql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .map(row -> ReorderLevel.builder()
                        .key(ReorderKey.builder()
                                .compCode(compCode)
                                .stockCode(row.get("stock_code", String.class))
                                .locCode(row.get("loc_code", String.class))
                                .build())
                        .deptId(deptId)
                        .stockName(row.get("stock_name", String.class))
                        .userCode(row.get("user_code", String.class))
                        .relName(row.get("rel_name", String.class))
                        .locName(row.get("loc_name", String.class))
                        .minQty(row.get("min_qty", Double.class))
                        .minUnitCode(row.get("min_unit", String.class))
                        .maxQty(row.get("max_qty", Double.class))
                        .position(row.get("position", Integer.class))
                        .maxUnitCode(row.get("max_unit", String.class))
                        .maxSmallQty(row.get("small_max_qty", Double.class))
                        .minSmallQty(row.get("small_min_qty", Double.class))
                        .balSmallQty(row.get("small_bal_qty", Double.class))
                        .build())
                .all();
    }
}
