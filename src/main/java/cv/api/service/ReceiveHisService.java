package cv.api.service;

import cv.api.entity.ReceiveHis;
import cv.api.entity.ReceiveHisKey;

public interface ReceiveHisService {
    ReceiveHis save(ReceiveHis obj);

    ReceiveHis find(ReceiveHisKey key);

    void delete(ReceiveHisKey key);
}
