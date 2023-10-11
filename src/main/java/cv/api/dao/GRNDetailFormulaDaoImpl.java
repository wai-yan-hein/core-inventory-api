package cv.api.dao;

import cv.api.entity.LandingHisPrice;
import cv.api.entity.LandingHisPriceKey;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class GRNDetailFormulaDaoImpl extends AbstractDao<LandingHisPriceKey, LandingHisPrice> implements GRNDetailFormulaDao {
    @Override
    public LandingHisPrice save(LandingHisPrice f) {
        return f;
    }

    @Override
    public boolean delete(LandingHisPriceKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<LandingHisPrice> getGRNDetailFormula(String vouNo, int uniqueId, String compCode) {
        String hsql = "select o from GRNDetailFormula o where o.key.vouNo ='" + vouNo + "'\n" +
                " and o.key.fUniqueId =" + uniqueId + " and o.key.compCode='" + compCode + "'";
        return findHSQL(hsql);
    }
}
