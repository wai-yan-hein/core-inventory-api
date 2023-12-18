package cv.api.dao;

import cv.api.entity.PurOrderHis;
import cv.api.entity.PurOrderHisKey;

public interface PurOrderHisDao {
    PurOrderHis save(PurOrderHis obj);

    PurOrderHis findById(PurOrderHisKey key);

    boolean delete(PurOrderHisKey key);

    boolean restore(PurOrderHisKey key);
}
