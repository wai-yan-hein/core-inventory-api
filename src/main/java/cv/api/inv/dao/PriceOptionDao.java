package cv.api.inv.dao;

import cv.api.inv.entity.PriceOption;

import java.util.List;

public interface PriceOptionDao {
    PriceOption save(PriceOption p);

    List<PriceOption> getPriceOption(String option,String compCode,Integer deptId);
}
