package cv.api.dao;

import cv.api.entity.LandingHisPrice;
import cv.api.entity.LandingHisPriceKey;
import cv.api.entity.LandingHisQty;
import cv.api.entity.LandingHisQtyKey;

import java.util.List;

public interface LandingHisQtyDao {
    LandingHisQty save(LandingHisQty f);
    boolean delete(LandingHisQtyKey key);

    List<LandingHisQty> getLandingQty(String vouNo, String compCode);

}
