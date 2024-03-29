package cv.api.dao;

import cv.api.entity.GRNDetail;
import cv.api.entity.GRNDetailKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class GRNDetailDaoImpl extends AbstractDao<GRNDetailKey, GRNDetail> implements GRNDetailDao {
    @Override
    public GRNDetail save(GRNDetail b) {
        saveOrUpdate(b, b.getKey());
        return b;
    }

    @Override
    public void delete(GRNDetailKey key) {
        remove(key);
    }

    @Override
    public List<GRNDetail> search(String vouNo, String compCode, Integer deptId) {
        List<GRNDetail> list = new ArrayList<>();
        try {
            String sql = "select g.*,s.user_code,s.stock_name,s.weight std_weight,rel.rel_name,l.loc_name\n" +
                    "from grn_detail g join stock s\n" +
                    "on g.stock_code = s.stock_code\n" +
                    "and g.comp_code =s.comp_code\n" +
                    "left join unit_relation rel\n" +
                    "on s.rel_code = rel.rel_code\n" +
                    "and s.comp_code =rel.comp_code\n" +
                    "join location l\n" +
                    "on g.loc_code = l.loc_code\n" +
                    "and g.comp_code =l.comp_code\n" +
                    "where g.vou_no='" + vouNo + "'\n" +
                    "and g.comp_code ='" + compCode + "'\n" +
                    "order by unique_id;";
            ResultSet rs = getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    GRNDetail g = new GRNDetail();
                    GRNDetailKey key = new GRNDetailKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    g.setKey(key);
                    g.setDeptId(rs.getInt("dept_id"));
                    g.setStockCode(rs.getString("stock_code"));
                    g.setUserCode(rs.getString("user_code"));
                    g.setStockName(rs.getString("stock_name"));
                    g.setStdWeight(rs.getDouble("std_weight"));
                    g.setTotalWeight(rs.getDouble("total_weight"));
                    g.setRelName(rs.getString("rel_name"));
                    g.setQty(rs.getDouble("qty"));
                    g.setUnit(rs.getString("unit"));
                    g.setLocCode(rs.getString("loc_code"));
                    g.setLocName(rs.getString("loc_name"));
                    g.setWeight(rs.getFloat("weight"));
                    g.setWeightUnit(rs.getString("weight_unit"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error("search : " + e.getMessage());
        }
        return list;
    }
}
