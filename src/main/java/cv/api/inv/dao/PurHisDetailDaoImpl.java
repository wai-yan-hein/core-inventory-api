/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.PurDetailKey;
import cv.api.inv.entity.PurHisDetail;
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
public class PurHisDetailDaoImpl extends AbstractDao<String, PurHisDetail> implements PurHisDetailDao {


    @Override
    public PurHisDetail save(PurHisDetail pd) {
        persist(pd);
        return pd;
    }

    @Override
    public List<PurHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<PurHisDetail> listOP = new ArrayList<>();
        String sql = "select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name\n" +
                "from pur_his_detail op\n" + "join location l on op.loc_code = l.loc_code\n" + "join stock s on op.stock_code = s.stock_code\n" +
                "join unit_relation rel on s.rel_code = rel.rel_code\n" + "left join stock_type st  on s.stock_type_code = st.stock_type_code\n" +
                "left join category cat on s.category_code = cat.cat_code\n" + "left join stock_brand sb on s.brand_code = sb.brand_code\n" +
                "where op.vou_no ='" + vouNo + "'\n" + "and op.comp_code ='" + compCode + "'\n" + "and op.dept_id = " + deptId + "\n" +
                "order by unique_id;\n";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    PurHisDetail op = new PurHisDetail();
                    PurDetailKey key = new PurDetailKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setDeptId(rs.getInt("dept_id"));
                    key.setPdCode(rs.getString("pd_code"));
                    op.setPdKey(key);
                    op.setStockCode(rs.getString("stock_code"));
                    op.setQty(rs.getFloat("qty"));
                    op.setAvgQty(rs.getFloat("avg_qty"));
                    op.setPrice(rs.getFloat("pur_price"));
                    op.setAmount(rs.getFloat("pur_amt"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUnitCode(rs.getString("pur_unit"));
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
    public int delete(String id) throws Exception {
        String strSql = "delete from pur_his_detail where pd_code = '" + id + "'";
        execSQL(strSql);
        return 1;
    }
}
