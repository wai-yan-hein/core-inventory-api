/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.OrderDetailKey;
import cv.api.entity.OrderHisDetail;
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
public class OrderHisDetailDaoImpl extends AbstractDao<OrderDetailKey, OrderHisDetail> implements OrderHisDetailDao {

    @Override
    public OrderHisDetail save(OrderHisDetail sdh) {
        saveOrUpdate(sdh, sdh.getKey());
        return sdh;
    }

    @Override
    public List<OrderHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<OrderHisDetail> listOP = new ArrayList<>();
        String sql = "select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name,t.trader_name\n" +
                "from order_his_detail op\n" +
                "join location l on op.loc_code = l.loc_code\n" +
                "and op.comp_code = l.comp_code\n" +
                "join stock s on op.stock_code = s.stock_code\n" +
                "and op.comp_code = s.comp_code\n" +
                "join unit_relation rel on s.rel_code = rel.rel_code\n" +
                "and op.comp_code = rel.comp_code\n" +
                "left join stock_type st  on s.stock_type_code = st.stock_type_code\n" +
                "and op.comp_code = st.comp_code\n" +
                "left join category cat on s.category_code = cat.cat_code\n" +
                "and op.comp_code = cat.comp_code\n" +
                "left join stock_brand sb on s.brand_code = sb.brand_code\n" +
                "and op.comp_code = sb.comp_code\n" +
                "left join grn g on " +
                "op.comp_code = g.comp_code\n" +
                "and g.deleted = false\n" +
                "left join trader t on g.trader_code = t.code\n" +
                "and g.comp_code = t.comp_code\n" +
                "where op.vou_no ='" + vouNo + "'\n" +
                "and op.comp_code ='" + compCode + "'\n" +
                "order by unique_id";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    OrderHisDetail op = new OrderHisDetail();
                    OrderDetailKey key = new OrderDetailKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setVouNo(rs.getString("vou_no"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    op.setKey(key);
                    op.setDeptId(rs.getInt("dept_id"));
                    op.setStockCode(rs.getString("stock_code"));
                    op.setWeight(rs.getFloat("weight"));
                    op.setWeightUnit(rs.getString("weight_unit"));
                    op.setStdWeight(rs.getFloat("std_weight"));
                    op.setQty(rs.getFloat("qty"));
                    op.setPrice(rs.getFloat("price"));
                    op.setAmount(rs.getFloat("amt"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUnitCode(rs.getString("unit"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setCatName(rs.getString("cat_name"));
                    op.setGroupName(rs.getString("stock_type_name"));
                    op.setBrandName(rs.getString("brand_name"));
                    op.setRelName(rs.getString("rel_name"));
                    op.setTraderName(rs.getString("trader_name"));
                    listOP.add(op);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listOP;
    }

    @Override
    public List<OrderHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        List<OrderHisDetail> list = new ArrayList<>();
        //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
        String sql = "select *\n" +
                "from order_his_detail\n" +
                "where vou_no='" + vouNo + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and dept_id ='" + deptId + "'\n" +
                "order by unique_id";
        try {
            ResultSet rs = getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    OrderHisDetail sh = new OrderHisDetail();
                    OrderDetailKey key = new OrderDetailKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    sh.setKey(key);
                    sh.setDeptId(rs.getInt("dept_id"));
                    sh.setStockCode(rs.getString("stock_code"));
                    sh.setQty(rs.getFloat("qty"));
                    sh.setUnitCode(rs.getString("sale_unit"));
                    sh.setPrice(rs.getFloat("sale_price"));
                    sh.setAmount(rs.getFloat("sale_amt"));
                    sh.setLocCode(rs.getString("loc_code"));
                    list.add(sh);
                }
            }
        } catch (Exception e) {
            log.error("searchDetail : " + e.getMessage());
        }
        return list;
    }

    @Override
    public int delete(OrderDetailKey key) {
        remove(key);
        return 1;
    }


}
