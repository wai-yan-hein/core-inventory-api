/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.StockInOutDetail;
import cv.api.entity.StockInOutKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wai yan
 */
@Repository
@Slf4j
public class StockInOutDetailDaoImpl extends AbstractDao<StockInOutKey, StockInOutDetail> implements StockInOutDetailDao {

    @Override
    public StockInOutDetail save(StockInOutDetail stock) {
        saveOrUpdate(stock, stock.getKey());
        return stock;
    }

    @Override
    public int delete(StockInOutKey key) {
        remove(key);
        return 1;
    }

    @Override
    public List<StockInOutDetail> search(String vouNo, String compCode) {
        List<StockInOutDetail> listOP = new ArrayList<>();
        String sql = """
                select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name
                from stock_in_out_detail op
                join location l on op.loc_code = l.loc_code
                and op.comp_code = l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                join unit_relation rel on s.rel_code = rel.rel_code
                and op.comp_code = rel.comp_code
                left join stock_type st  on s.stock_type_code = st.stock_type_code
                and op.comp_code = st.comp_code
                left join category cat on s.category_code = cat.cat_code
                and op.comp_code = cat.comp_code
                left join stock_brand sb on s.brand_code = sb.brand_code
                and op.comp_code = sb.comp_code
                where op.vou_no =?
                and op.comp_code =?
                order by unique_id""";
        ResultSet rs = getResult(sql,vouNo,compCode);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    StockInOutDetail op = new StockInOutDetail();
                    StockInOutKey key = new StockInOutKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setCompCode(rs.getString("comp_code"));
                    op.setKey(key);
                    op.setDeptId(rs.getInt("dept_id"));
                    op.setStockCode(rs.getString("stock_code"));
                    op.setInQty(rs.getDouble("in_qty"));
                    op.setInUnitCode(rs.getString("in_unit"));
                    op.setOutQty(rs.getDouble("out_qty"));
                    op.setOutUnitCode(rs.getString("out_unit"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setCatName(rs.getString("cat_name"));
                    op.setGroupName(rs.getString("stock_type_name"));
                    op.setBrandName(rs.getString("brand_name"));
                    op.setRelName(rs.getString("rel_name"));
                    op.setCostPrice(rs.getDouble("cost_price"));
                    op.setWeight(rs.getDouble("weight"));
                    op.setWeightUnit(rs.getString("weight_unit"));
                    op.setTotalWeight(rs.getDouble("total_weight"));
                    listOP.add(op);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listOP;
    }

    @Override
    public List<StockInOutDetail> searchByJob(String jobId, String compCode) {
        List<StockInOutDetail> listOP = new ArrayList<>();
        String sql = """
                select sum(op.total_weight) as tot_weight, sum(op.in_qty) as in_tot_qty, sum(op.out_qty) as out_tot_qty,op.*,s.user_code,s.stock_name
                from stock_in_out_detail op
                join stock_in_out l on op.vou_no = l.vou_no
                and op.comp_code = l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code = s.comp_code
                where l.job_code =?
                and l.comp_code =?
                group by op.stock_code,weight_unit,in_unit,out_unit
                order by vou_no,unique_id;
                """;
        ResultSet rs = getResult(sql,jobId,compCode);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    StockInOutDetail op = new StockInOutDetail();
                    StockInOutKey key = new StockInOutKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setCompCode(rs.getString("comp_code"));
                    op.setKey(key);
                    op.setDeptId(rs.getInt("dept_id"));
                    op.setStockCode(rs.getString("stock_code"));
                    op.setInQty(rs.getDouble("in_tot_qty"));
                    op.setInUnitCode(rs.getString("in_unit"));
                    op.setOutQty(rs.getDouble("out_tot_qty"));
                    op.setOutUnitCode(rs.getString("out_unit"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setCostPrice(rs.getDouble("cost_price"));
                    op.setWeight(rs.getDouble("weight"));
                    op.setWeightUnit(rs.getString("weight_unit"));
                    op.setTotalWeight(rs.getDouble("total_weight"));
                    listOP.add(op);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listOP;
    }
}
