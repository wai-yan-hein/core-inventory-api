/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.SaleDetailKey;
import cv.api.inv.entity.SaleHisDetail;
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
public class SaleHisDetailDaoImpl extends AbstractDao<SaleDetailKey, SaleHisDetail> implements SaleHisDetailDao {

    @Override
    public SaleHisDetail save(SaleHisDetail sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public List<SaleHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<SaleHisDetail> listOP = new ArrayList<>();
        String sql = "select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name\n" +
                "from sale_his_detail op\n" +
                "join location l on op.loc_code = l.loc_code\n" +
                "join stock s on op.stock_code = s.stock_code\n" +
                "join unit_relation rel on s.rel_code = rel.rel_code\n" +
                "left join stock_type st  on s.stock_type_code = st.stock_type_code\n" +
                "left join category cat on s.category_code = cat.cat_code\n" +
                "left join stock_brand sb on s.brand_code = sb.brand_code\n" +
                "where op.vou_no ='" + vouNo + "'\n" +
                "and op.comp_code ='" + compCode + "'\n" +
                "and op.dept_id = " + deptId + "\n" +
                "order by unique_id";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    SaleHisDetail op = new SaleHisDetail();
                    SaleDetailKey key = new SaleDetailKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setDeptId(rs.getInt("dept_id"));
                    key.setSdCode(rs.getString("sd_code"));
                    op.setSdKey(key);
                    op.setStockCode(rs.getString("stock_code"));
                    op.setQty(rs.getFloat("qty"));
                    op.setPrice(rs.getFloat("sale_price"));
                    op.setAmount(rs.getFloat("sale_amt"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUnitCode(rs.getString("sale_unit"));
                    op.setCompCode(rs.getString("comp_code"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setCatName(rs.getString("cat_name"));
                    op.setGroupName(rs.getString("stock_type_name"));
                    op.setBrandName(rs.getString("brand_name"));
                    op.setRelName(rs.getString("rel_name"));
                    op.setUniqueId(rs.getInt("unique_id"));
                    listOP.add(op);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listOP;
    }

    @Override
    public List<SaleHisDetail> searchDetail(String vouNo, String compCode, Integer deptId) {
        List<SaleHisDetail> list = new ArrayList<>();
        //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
        String sql = "select * from sale_his_detail where vou_no='" + vouNo + "' and comp_code ='" + compCode + "' and dept_id ='" + deptId + "' order by unique_id";
        try {
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                while (rs.next()) {
                    SaleHisDetail sh = new SaleHisDetail();
                    SaleDetailKey key = new SaleDetailKey();
                    key.setSdCode(rs.getString("sd_code"));
                    key.setVouNo(rs.getString("vou_no"));
                    key.setDeptId(rs.getInt("dept_id"));
                    sh.setSdKey(key);
                    sh.setStockCode(rs.getString("stock_code"));
                    sh.setExpDate(rs.getDate("expire_date"));
                    sh.setQty(rs.getFloat("qty"));
                    sh.setUnitCode(rs.getString("sale_unit"));
                    sh.setPrice(rs.getFloat("sale_price"));
                    sh.setAmount(rs.getFloat("sale_amt"));
                    sh.setLocCode(rs.getString("loc_code"));
                    sh.setUniqueId(rs.getInt("unique_id"));
                    sh.setCompCode(rs.getString("comp_code"));
                    list.add(sh);
                }
            }
        } catch (Exception e) {
            log.error("searchDetail : "+e.getMessage());
        }
        return list;
    }

    @Override
    public int delete(String code, String compCode, Integer deptId) {
        String strSql = "delete from sale_his_detail where sd_code = '" + code + "' and comp_code ='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(strSql);
        return 1;
    }

}
