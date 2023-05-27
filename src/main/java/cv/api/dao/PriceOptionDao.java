package cv.api.dao;

import cv.api.entity.PriceOption;

import java.util.List;

public interface PriceOptionDao {
    PriceOption save(PriceOption p);
    List<PriceOption> getPriceOption(String updatedDate);

    List<PriceOption> getPriceOption(String option, String compCode, Integer deptId);
}
