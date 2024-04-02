package cv.api.service;

import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.model.VPurchase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
}