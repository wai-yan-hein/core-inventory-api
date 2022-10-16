package cv.api.inv.dao;

import cv.api.inv.entity.OPHisDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class OPHisDetailDaoImpl extends AbstractDao<String, OPHisDetail> implements OPHisDetailDao {


    @Override
    public OPHisDetail save(OPHisDetail op) {
        persist(op);
        return op;
    }

    @Override
    public List<OPHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<OPHisDetail> listOP = new ArrayList<>();
        String sql = "select op.*,s.user_code,s.stock_name,cat.cat_name,st.stock_type_name,sb.brand_name,rel.rel_name\n" +
                "from op_his_detail op\n" +
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
                //op_code, stock_code, qty, price, amount, loc_code, unit, vou_no, unique_id,
                //comp_code, dept_id, user_code, stock_name, cat_name, stock_type_name, brand_name, rel_name
                while (rs.next()) {
                    OPHisDetail op = new OPHisDetail();
                    op.setOpCode(rs.getString("op_code"));
                    op.setStockCode(rs.getString("stock_code"));
                    op.setQty(rs.getFloat("qty"));
                    op.setPrice(rs.getFloat("price"));
                    op.setAmount(rs.getFloat("amount"));
                    op.setLocCode(rs.getString("loc_code"));
                    op.setUnitCode(rs.getString("unit"));
                    op.setVouNo(rs.getString("vou_no"));
                    op.setCompCode(rs.getString("comp_code"));
                    op.setDeptId(rs.getInt("dept_id"));
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
    public int delete(String opCode) {
        String delSql = "delete from op_his_detail where op_code = '" + opCode + "'";
        execSQL(delSql);
        return 1;
    }
}