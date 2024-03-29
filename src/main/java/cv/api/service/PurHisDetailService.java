package cv.api.service;

import cv.api.entity.PurHisDetail;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PurHisDetailService {
    private final DatabaseClient client;
    public Mono<PurHisDetail> insert(PurHisDetail dto) {
        String sql = """
                INSERT INTO pur_his_detail (
                    vou_no, unique_id, comp_code, dept_id, stock_code, exp_date,
                    qty, pur_unit, pur_price, pur_amt, loc_code, org_price,
                    avg_qty, avg_price, std_weight, weight_unit, weight,
                    length, width, total_weight, m_percent, rice, wet, bag
                ) VALUES (
                    :vouNo, :uniqueId, :compCode, :deptId, :stockCode, :expDate,
                    :qty, :purUnit, :purPrice, :purAmt, :locCode, :orgPrice,
                    :avgQty, :avgPrice, :stdWeight, :weightUnit, :weight,
                    :length, :width, :totalWeight, :mPercent, :rice, :wet, :bag
                )
                """;

        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("stockCode", dto.getStockCode())
                .bind("expDate", Parameters.in(R2dbcType.DATE,dto.getExpDate()))
                .bind("qty", dto.getQty())
                .bind("purUnit", dto.getUnitCode())
                .bind("purPrice", dto.getPrice())
                .bind("purAmt", dto.getAmount())
                .bind("locCode", dto.getLocCode())
                .bind("orgPrice", Parameters.in(R2dbcType.DOUBLE,dto.getOrgPrice()))
                .bind("avgQty", Parameters.in(R2dbcType.DOUBLE,dto.getAvgQty()))
                .bind("avgPrice", Parameters.in(R2dbcType.DOUBLE,dto.getAvgPrice()))
                .bind("stdWeight", Parameters.in(R2dbcType.DOUBLE,dto.getStdWeight()))
                .bind("weightUnit", Parameters.in(R2dbcType.VARCHAR,dto.getWeightUnit()))
                .bind("weight", Parameters.in(R2dbcType.DOUBLE,dto.getWeight()))
                .bind("length", Parameters.in(R2dbcType.DOUBLE,dto.getLength()))
                .bind("width", Parameters.in(R2dbcType.DOUBLE,dto.getWidth()))
                .bind("totalWeight", Parameters.in(R2dbcType.DOUBLE,dto.getTotalWeight()))
                .bind("mPercent", Parameters.in(R2dbcType.DOUBLE,dto.getMPercent()))
                .bind("rice",Parameters.in(R2dbcType.DOUBLE, dto.getRice()))
                .bind("wet", Parameters.in(R2dbcType.DOUBLE,dto.getWet()))
                .bind("bag", Parameters.in(R2dbcType.DOUBLE,dto.getBag()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }
    public Flux<PurHisDetail> search(String vouNo, String compCode) {
        String sql = """
                select op.*,s.user_code,s.stock_name,s.calculate,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name
                from pur_his_detail op
                join location l on op.loc_code = l.loc_code
                and op.comp_code =l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code =s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and op.comp_code =rel.comp_code
                left join stock_type st  on s.stock_type_code = st.stock_type_code
                and op.comp_code =st.comp_code
                left join category cat on s.category_code = cat.cat_code
                and op.comp_code =cat.comp_code
                left join stock_brand sb on s.brand_code = sb.brand_code
                and op.comp_code =sb.comp_code
                where op.vou_no = :vouNo
                and op.comp_code = :compCode
                order by unique_id
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> PurHisDetail.builder()
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .qty(row.get("qty", Double.class))
                        .weightLoss(row.get("avg_qty", Double.class))
                        .orgPrice(row.get("org_price", Double.class))
                        .weight(row.get("weight", Double.class))
                        .stdWeight(row.get("std_weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .price(row.get("pur_price", Double.class))
                        .amount(row.get("pur_amt", Double.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .unitCode(row.get("pur_unit", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .calculate(row.get("calculate",Boolean.class))
                        .catName(row.get("cat_name", String.class))
                        .groupName(row.get("stock_type_name", String.class))
                        .brandName(row.get("brand_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .length(row.get("length", Double.class))
                        .width(row.get("width", Double.class))
                        .mPercent(row.get("m_percent", String.class))
                        .wet(row.get("wet", Double.class))
                        .rice(row.get("rice", Double.class))
                        .bag(row.get("bag", Double.class))
                        .build())
                .all();
    }
    @Transactional
    public Mono<Boolean> delete(String vouNo, String compCode) {
        String sql = """
                delete from pur_his_detail where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }
}
