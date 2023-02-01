package cv.api.service;

import cv.api.entity.WeightLossHis;
import cv.api.entity.WeightLossHisKey;

import java.util.List;

public interface WeightLossService {
    WeightLossHis save(WeightLossHis l);

    WeightLossHis findById(WeightLossHisKey key);

    void delete(WeightLossHisKey key);

    void restore(WeightLossHisKey key);

    List<WeightLossHis> search(String fromDate, String toDate, String locCode, String compCode, Integer deptId);
}
