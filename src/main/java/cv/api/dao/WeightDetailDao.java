package cv.api.dao;

import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisDetail;
import cv.api.entity.WeightHisDetailKey;
import cv.api.entity.WeightHisKey;

public interface WeightDetailDao {
    WeightHisDetail save(WeightHisDetail obj);

    boolean delete(WeightHisDetailKey key);

}
