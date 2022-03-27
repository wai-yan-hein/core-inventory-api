package cv.api.inv.dao;

import cv.api.inv.entity.PriceOption;
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
    public List<PriceOption> getPriceOption(String compCode) {
        String hsql = "select o from PriceOption o where o.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }
}
