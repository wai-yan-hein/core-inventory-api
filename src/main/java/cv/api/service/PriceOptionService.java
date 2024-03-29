package cv.api.service;

import cv.api.entity.PriceOption;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceOptionService {
    PriceOption save(PriceOption p);

    List<PriceOption> getPriceOption(String option, String compCode, Integer deptId);
    List<PriceOption> getPriceOption(LocalDateTime updatedDate);

}
