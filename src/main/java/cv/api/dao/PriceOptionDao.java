package cv.api.dao;

import cv.api.entity.PriceOption;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceOptionDao {
    PriceOption save(PriceOption p);
    List<PriceOption> getPriceOption(LocalDateTime updatedDate);

    List<PriceOption> getPriceOption(String option, String compCode, Integer deptId);
}
