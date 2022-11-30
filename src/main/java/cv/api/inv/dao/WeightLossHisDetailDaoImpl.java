package cv.api.inv.dao;

import cv.api.inv.entity.WeightLossHisDetail;
import cv.api.inv.entity.WeightLossHisDetailKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class WeightLossHisDetailDaoImpl extends AbstractDao<WeightLossHisDetailKey, WeightLossHisDetail> implements WeightLossHisDetailDao {
    @Override
    public WeightLossHisDetail save(WeightLossHisDetail wd) {
        persist(wd);
        return wd;
    }

    @Override
    public void delete(WeightLossHisDetailKey key) {
        String sql = "delete from weight_loss_his_detail where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id=" + key.getDeptId() + " and unique_id =" + key.getUniqueId() + "";
        execSQL(sql);
    }

    @Override
    public List<WeightLossHisDetail> search(String vouNo, String compCode, Integer deptId) {
        List<WeightLossHisDetail> list = new ArrayList<>();
        String sql = "select w.*,s.user_code,s.stock_name,rel.rel_name,l.loc_name\n" +
                "from weight_loss_his_detail w\n" +
                "join stock s \n" +
                "on w.stock_code = s.stock_code\n" +
                "and w.comp_code =s.comp_code\n" +
                "and w.dept_id =s.dept_id\n" +
                "join unit_relation rel\n" +
                "on s.rel_code = rel.rel_code\n" +
                "and s.comp_code =s.comp_code\n" +
                "and s.dept_id =s.dept_id\n" +
                "join location l\n" +
                "on w.loc_code = l.loc_code\n" +
                "and w.comp_code =l.comp_code\n" +
                "and w.dept_id =l.dept_id\n" +
                "where w.comp_code ='" + compCode + "'\n" +
                "and w.vou_no ='" + vouNo + "'\n" +
                "and w.dept_id =" + deptId + "\n" +
                "order by unique_id";
        try {
            //vou_no, comp_code, dept_id, unique_id, stock_code, qty, unit, price, loss_qty, loss_unit, loss_price, loc_code, user_code, stock_name
            ResultSet rs = getResultSet(sql);
            if (rs != null) {
                while (rs.next()) {
                    WeightLossHisDetail h = new WeightLossHisDetail();
                    WeightLossHisDetailKey key = new WeightLossHisDetailKey();
                    key.setVouNo(vouNo);
                    key.setDeptId(deptId);
                    key.setCompCode(compCode);
                    key.setUniqueId(rs.getInt("unique_id"));
                    h.setKey(key);
                    h.setStockUserCode(rs.getString("user_code"));
                    h.setStockCode(rs.getString("stock_code"));
                    h.setStockName(rs.getString("stock_name"));
                    h.setQty(rs.getFloat("qty"));
                    h.setUnit(rs.getString("unit"));
                    h.setPrice(rs.getFloat("price"));
                    h.setLossQty(rs.getFloat("loss_qty"));
                    h.setLossUnit(rs.getString("loss_unit"));
                    h.setLossPrice(rs.getFloat("loss_price"));
                    h.setLocCode(rs.getString("loc_code"));
                    h.setLocName(rs.getString("loc_name"));
                    h.setRelName(rs.getString("rel_name"));
                    list.add(h);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }
}
