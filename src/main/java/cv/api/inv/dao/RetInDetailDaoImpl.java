/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.RetInHisDetail;
import cv.api.inv.entity.RetInKey;
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
public class RetInDetailDaoImpl extends AbstractDao<RetInKey, RetInHisDetail> implements RetInDetailDao {

    @Override
    public RetInHisDetail save(RetInHisDetail pd) {
        persist(pd);
        return pd;
    }

    @Override
    public List<RetInHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<RetInHisDetail> listOP = new ArrayList<>();
        String sql = "select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name\n" +
                "from ret_in_his_detail op\n" +
                "join location l on op.loc_code = l.loc_code\n" +
                "and op.comp_code =l.comp_code\n" +
                "and op.dept_id = l.dept_id\n" +
                "join stock s on op.stock_code = s.stock_code\n" +
                "and op.comp_code =s.comp_code\n" +
                "and op.dept_id = s.dept_id\n" +
                "join unit_relation rel on s.rel_code = rel.rel_code\n" +
                "and op.comp_code =rel.comp_code\n" +
                "and op.dept_id = rel.dept_id\n" +
                "left join stock_type st  on s.stock_type_code = st.stock_type_code\n" +
                "and op.comp_code =st.comp_code\n" +
                "and op.dept_id =st.dept_id\n" +
                "left join category cat on s.category_code = cat.cat_code\n" +
                "and op.comp_code =cat.comp_code\n" +
                "and op.dept_id = cat.dept_id\n" +
                "left join stock_brand sb on s.brand_code = sb.brand_code\n" +
                "and op.comp_code =sb.comp_code\n" +
                "and op.dept_id = sb.dept_id\n" +
                "where op.vou_no ='" + vouNo + "'\n" +
                "and op.comp_code ='" + compCode + "'\n" +
                "and op.dept_id = " + deptId + "\n" +
                "order by unique_id";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    RetInHisDetail op = new RetInHisDetail();
                    RetInKey key = new RetInKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setDeptId(rs.getInt("dept_id"));
                    key.setRdCode(rs.getString("rd_code"));
                    op.setRiKey(key);
                    op.setStockCode(rs.getString("stock_code"));
                    op.setQty(rs.getFloat("qty"));
                    op.setAvgQty(rs.getFloat("avg_qty"));
                    op.setPrice(rs.getFloat("price"));
                    op.setAmount(rs.getFloat("amt"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUnitCode(rs.getString("unit"));
                    op.setUniqueId(rs.getInt("unique_id"));
                    op.setCompCode(rs.getString("comp_code"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setCatName(rs.getString("cat_name"));
                    op.setGroupName(rs.getString("stock_type_name"));
                    op.setBrandName(rs.getString("brand_name"));
                    op.setRelName(rs.getString("rel_name"));
                    listOP.add(op);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return listOP;
    }

    @Override
    public int delete(String id, String compCode, Integer deptId) throws Exception {
        String strSql = "delete from ret_in_his_detail where rd_code = '" + id + "' and comp_code ='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(strSql);
        return 1;
    }
}
