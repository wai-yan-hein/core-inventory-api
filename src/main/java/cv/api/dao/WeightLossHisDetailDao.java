package cv.api.dao;

import cv.api.entity.WeightLossHisDetail;
import cv.api.entity.WeightLossHisDetailKey;

import java.util.List;

public interface WeightLossHisDetailDao {
    WeightLossHisDetail save(WeightLossHisDetail wd);

    void delete(WeightLossHisDetailKey key);

    List<WeightLossHisDetail> search(String vouNo, String compCode, Integer deptId);
}
