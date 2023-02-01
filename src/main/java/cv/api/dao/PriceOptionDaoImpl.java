package cv.api.dao;

import cv.api.entity.PriceOption;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceOptionDaoImpl extends AbstractDao<String, PriceOption> implements PriceOptionDao {
    @Override
    public PriceOption save(PriceOption p) {
        persist(p);
        return p;
    }

    @Override
    public List<PriceOption> getPriceOption(String option, String compCode, Integer deptId) {
        String hsql = "select o from PriceOption o where (o.tranOption ='" + option + "' or '-' = '" + option + "') and o.key.compCode = '" + compCode + "' and (o.key.deptId =" + deptId + " or 0 =" + deptId + ") order by o.uniqueId";
        return findHSQL(hsql);
    }
}
