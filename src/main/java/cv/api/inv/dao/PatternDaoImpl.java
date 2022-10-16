package cv.api.inv.dao;

import cv.api.inv.entity.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class PatternDaoImpl extends AbstractDao<String, Pattern> implements PatternDao {

    @Override
    public Pattern findByCode(String code) {
        return getByKey(code);
    }

    @Override
    public Pattern save(Pattern pattern) {
        persist(pattern);
        return pattern;
    }

    @Override
    public void delete(String stockCode) {
        String sql = "delete from pattern where stock_code ='" + stockCode + "'";
        execSQL(sql);
    }


    @Override
    public List<Pattern> search(String stockCode, String compCode, Integer deptId) {
        List<Pattern> listP = new ArrayList<>();
        String sql = "select p.*,s.user_code,s.stock_name,l.loc_name\n" +
                "from pattern p join stock s\n" +
                "on p.stock_code = s.stock_code\n" +
                "join location l on p.loc_code = l.loc_code\n" +
                "where p.f_stock_code = '" + stockCode + "'\n" +
                "and p.dept_id =" + deptId + "\n" +
                "and p.comp_code ='" + compCode + "'\n" +
                "order by p.unique_id";
        ResultSet rs = getResultSet(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    Pattern p = new Pattern();
                    p.setStockCode(rs.getString("stock_code"));
                    p.setUserCode(rs.getString("user_code"));
                    p.setStockName(rs.getString("stock_name"));
                    p.setLocCode(rs.getString("loc_code"));
                    p.setLocName(rs.getString("loc_name"));
                    p.setQty(rs.getFloat("qty"));
                    p.setPrice(rs.getFloat("price"));
                    p.setUnitCode(rs.getString("unit"));
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
