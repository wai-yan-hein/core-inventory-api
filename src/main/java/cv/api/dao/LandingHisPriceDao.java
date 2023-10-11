package cv.api.dao;

import cv.api.entity.LandingHisPrice;
import cv.api.entity.LandingHisPriceKey;

import java.util.List;

public interface LandingHisPriceDao {
    LandingHisPrice save(LandingHisPrice f);
    boolean delete(LandingHisPriceKey key);
    List<LandingHisPrice> getLandingPrice(String vouNo, String compCode);
}
