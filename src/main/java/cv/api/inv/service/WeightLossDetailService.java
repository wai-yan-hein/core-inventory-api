package cv.api.inv.service;

import cv.api.inv.entity.WeightLossHisDetail;
import cv.api.inv.entity.WeightLossHisDetailKey;

import java.util.List;

public interface WeightLossDetailService {
    WeightLossHisDetail save(WeightLossHisDetail wd);

    void delete(WeightLossHisDetailKey key);

    List<WeightLossHisDetail> search(String vouNo, String compCode, Integer deptId);
}
