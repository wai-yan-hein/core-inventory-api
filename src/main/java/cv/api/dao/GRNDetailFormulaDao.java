package cv.api.dao;

import cv.api.entity.LandingHisPrice;
import cv.api.entity.LandingHisPriceKey;

import java.util.List;

public interface GRNDetailFormulaDao {
    LandingHisPrice save(LandingHisPrice f);
    boolean delete(LandingHisPriceKey key);

    List<LandingHisPrice> getGRNDetailFormula(String vouNo, int uniqueId, String compCode);
}
