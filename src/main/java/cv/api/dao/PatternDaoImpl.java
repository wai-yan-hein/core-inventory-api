package cv.api.dao;

import cv.api.entity.Pattern;
import cv.api.entity.PatternKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class PatternDaoImpl extends AbstractDao<PatternKey, Pattern> implements PatternDao {

    @Override
    public Pattern findByCode(PatternKey key) {
        return getByKey(key);
    }

    @Override
    public Pattern save(Pattern pattern) {
        saveOrUpdate(pattern,pattern.getKey());
        return pattern;
    }

    @Override
    public void delete(Pattern pattern) {
        PatternKey key = pattern.getKey();
        String sql = "delete from pattern where comp_code ='" + key.getCompCode() + "'\n"
                + "and dept_id =" + key.getDeptId() + " and stock_code ='" + key.getStockCode() + "'\n"
                + "and f_stock_code ='" + key.getMapStockCode() + "' and unique_id =" + key.getUniqueId() + "";
        execSql(sql);
    }


    @Override
    public List<Pattern> search(String stockCode, String compCode, Integer deptId) {
        List<Pattern> listP = new ArrayList<>();
        String sql = "select p.*,s.user_code,s.stock_name,l.loc_name,po.desp,p.price_type\n" +
                "from pattern p join stock s\n" +
                "on p.stock_code = s.stock_code\n" +
                "and p.comp_code = s.comp_code\n" +
                "and p.dept_id = s.dept_id\n" +
                "join location l on p.loc_code = l.loc_code\n" +
                "and p.comp_code = s.comp_code\n" +
                "and p.dept_id = s.dept_id\n" +
                "left join price_option po on p.price_type = po.type\n" +
                "and p.comp_code = po.comp_code\n" +
                "and p.dept_id = po.dept_id\n" +
                "where p.f_stock_code = '" + stockCode + "'\n" +
                "and p.dept_id =" + deptId + "\n" +
                "and p.comp_code ='" + compCode + "'\n" +
                "order by p.unique_id";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    Pattern p = new Pattern();
                    PatternKey key = new PatternKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    key.setStockCode(rs.getString("stock_code"));
                    key.setMapStockCode(rs.getString("f_stock_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    p.setKey(key);
                    p.setUserCode(rs.getString("user_code"));
                    p.setStockName(rs.getString("stock_name"));
                    p.setLocCode(rs.getString("loc_code"));
                    p.setLocName(rs.getString("loc_name"));
                    p.setQty(rs.getFloat("qty"));
                    p.setPrice(rs.getFloat("price"));
                    p.setUnitCode(rs.getString("unit"));
                    p.setPriceTypeCode(rs.getString("price_type"));
                    p.setPriceTypeName(rs.getString("desp"));
                    listP.add(p);
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        return listP;
    }

    @Override
    public List<Pattern> unUpload() {
        String hsql = "select o from Pattern o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }
}
