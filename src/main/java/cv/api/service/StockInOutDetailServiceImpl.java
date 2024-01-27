/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.StockInOutDetailDao;
import cv.api.dto.StockInOutDetailDto;
import cv.api.dto.StockInOutKeyDto;
import cv.api.entity.StockInOutDetail;
import cv.api.entity.StockInOutKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

/**
 * @author wai yan
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StockInOutDetailServiceImpl implements StockInOutDetailService {

    private final StockInOutDetailDao dao;
    private final DatabaseClient client;

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        return dao.save(stock);
    }

    @Override
    public int delete(StockInOutKey key) {
        return dao.delete(key);
    }

    @Override
    public Flux<StockInOutDetailDto> search(String vouNo, String compCode) {
        String sql = """
                select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,
                rel.rel_name,l.loc_name
                from stock_in_out_detail op
                join location l on op.loc_code = l.loc_code
                and op.comp_code = l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and op.comp_code = rel.comp_code
                left join stock_type st  on s.stock_type_code = st.stock_type_code
                and op.comp_code = st.comp_code
                left join category cat on s.category_code = cat.cat_code
                and op.comp_code = cat.comp_code
                left join stock_brand sb on s.brand_code = sb.brand_code
                and op.comp_code = sb.comp_code
                where op.vou_no =:vouNo
                and op.comp_code =:compCode
                order by unique_id""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row) -> StockInOutDetailDto.builder()
                        .key(StockInOutKeyDto.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .inQty(row.get("in_qty", Double.class))
                        .inUnitCode(row.get("in_unit", String.class))
                        .outQty(row.get("out_qty", Double.class))
                        .outUnitCode(row.get("out_unit", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .catName(row.get("cat_name", String.class))
                        .groupName(row.get("stock_type_name", String.class))
                        .brandName(row.get("brand_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .costPrice(row.get("cost_price", Double.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .wet(row.get("wet", Double.class))
                        .rice(row.get("rice", Double.class))
                        .inBag(row.get("in_bag", Double.class))
                        .outBag(row.get("out_bag", Double.class))
                        .amount(row.get("amount", Double.class))
                        .build()).all();
    }

    @Override
    public Flux<StockInOutDetailDto> searchByJob(String jobCode, String compCode) {
        String sql = """
                select sum(op.total_weight) as tot_weight, sum(op.in_qty) as in_tot_qty, sum(op.out_qty) as out_tot_qty,op.*,s.user_code,s.stock_name,st.finished_group
                from stock_in_out_detail op
                join stock_in_out l on op.vou_no = l.vou_no
                and op.comp_code = l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                where l.job_code =:jobCode
                and l.comp_code =:compCOde
                and l.deleted = false
                group by op.stock_code,weight_unit,in_unit,out_unit
                order by st.finished_group desc,vou_no,unique_id;
                """;
        return client.sql(sql)
                .bind("jobCode", jobCode)
                .bind("compCode", compCode)
                .map((row) -> StockInOutDetailDto.builder()
                        .key(StockInOutKeyDto.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .inQty(row.get("in_tot_qty", Double.class))
                        .inUnitCode(row.get("in_unit", String.class))
                        .outQty(row.get("out_tot_qty", Double.class))
                        .outUnitCode(row.get("out_unit", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .costPrice(row.get("cost_price", Double.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .totalWeight(row.get("tot_weight", Double.class))
                        .build()).all();

    }

}
