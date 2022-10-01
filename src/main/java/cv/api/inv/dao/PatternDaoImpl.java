package cv.api.inv.dao;

import cv.api.inv.entity.Pattern;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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
    public List<Pattern> search(String stockCode) {
        String hsql = "select o from Pattern o where o.stockCode ='" + stockCode + "' order by o.uniqueId";
        return findHSQL(hsql);
    }
}
