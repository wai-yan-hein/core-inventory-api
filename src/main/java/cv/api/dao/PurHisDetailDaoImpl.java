/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.dao;

import cv.api.entity.PurDetailKey;
import cv.api.entity.PurHisDetail;
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
public class PurHisDetailDaoImpl extends AbstractDao<PurDetailKey, PurHisDetail> implements PurHisDetailDao {


    @Override
    public PurHisDetail save(PurHisDetail pd) {
        saveOrUpdate(pd, pd.getKey());
        return pd;
    }

    @Override
    public List<PurHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<PurHisDetail> listOP = new ArrayList<>();
        String sql = """
                select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name,l.loc_name
                from pur_his_detail op
                join location l on op.loc_code = l.loc_code
                and op.comp_code =l.comp_code
                join stock s on op.stock_code = s.stock_code
                and op.comp_code =s.comp_code
                join unit_relation rel on s.rel_code = rel.rel_code
                and op.comp_code =rel.comp_code
                left join stock_type st  on s.stock_type_code = st.stock_type_code
                and op.comp_code =st.comp_code
                left join category cat on s.category_code = cat.cat_code
                and op.comp_code =cat.comp_code
                left join stock_brand sb on s.brand_code = sb.brand_code
                and op.comp_code =sb.comp_code
                where op.vou_no =?
                and op.comp_code =?
                order by unique_id;
                """;
        ResultSet rs = getResult(sql, vouNo, compCode);
        if (rs != null) {
            try {
                //sd_code, vou_no, stock_code, expire_date, qty, sale_unit, sale_price, sale_amt, loc_code, unique_id, comp_code, dept_id
                while (rs.next()) {
                    PurHisDetail op = new PurHisDetail();
                    PurDetailKey key = new PurDetailKey();
                    key.setVouNo(rs.getString("vou_no"));
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    op.setKey(key);
                    op.setDeptId(rs.getInt("dept_id"));
                    op.setStockCode(rs.getString("stock_code"));
                    op.setQty(rs.getDouble("qty"));
                    op.setWeightLoss(rs.getDouble("avg_qty"));
                    op.setOrgPrice(rs.getDouble("org_price"));
                    op.setWeight(rs.getDouble("weight"));
                    op.setStdWeight(rs.getDouble("std_weight"));
                    op.setWeightUnit(rs.getString("weight_unit"));
                    op.setTotalWeight(rs.getDouble("total_weight"));
                    op.setPrice(rs.getDouble("pur_price"));
                    op.setAmount(rs.getDouble("pur_amt"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setLocName(rs.getString("loc_name"));
                    op.setUnitCode(rs.getString("pur_unit"));
                    op.setUserCode(rs.getString("user_code"));
                    op.setStockName(rs.getString("stock_name"));
                    op.setCatName(rs.getString("cat_name"));
                    op.setGroupName(rs.getString("stock_type_name"));
                    op.setBrandName(rs.getString("brand_name"));
                    op.setRelName(rs.getString("rel_name"));
                    op.setLength(rs.getDouble("length"));
                    op.setWidth(rs.getDouble("width"));
                    op.setMPercent(rs.getString("m_percent"));
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
    public int delete(PurDetailKey key) {
        remove(key);
        return 1;
    }
}
