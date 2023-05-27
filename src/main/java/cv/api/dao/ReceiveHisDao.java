package cv.api.dao;

import cv.api.entity.ReceiveHis;
import cv.api.entity.ReceiveHisKey;

public interface ReceiveHisDao {
    ReceiveHis save(ReceiveHis obj);

    ReceiveHis find(ReceiveHisKey key);

    void delete(ReceiveHisKey key);
}
