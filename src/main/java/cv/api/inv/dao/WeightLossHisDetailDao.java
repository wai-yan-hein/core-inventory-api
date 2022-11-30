package cv.api.inv.dao;

import cv.api.inv.entity.WeightLossHisDetail;
import cv.api.inv.entity.WeightLossHisDetailKey;

import java.util.List;

public interface WeightLossHisDetailDao {
    WeightLossHisDetail save(WeightLossHisDetail wd);

    void delete(WeightLossHisDetailKey key);

    List<WeightLossHisDetail> search(String vouNo, String compCode, Integer deptId);
}
