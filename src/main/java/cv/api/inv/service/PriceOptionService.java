package cv.api.inv.service;

import cv.api.inv.entity.PriceOption;

import java.util.List;

public interface PriceOptionService {
    PriceOption save(PriceOption p);

    List<PriceOption> getPriceOption(String option,String compCode,Integer deptId);
}
