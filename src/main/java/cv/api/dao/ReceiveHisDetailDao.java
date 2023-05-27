package cv.api.dao;

import cv.api.entity.ReceiveHis;
import cv.api.entity.ReceiveHisDetail;
import cv.api.entity.ReceiveHisDetailKey;
import cv.api.entity.ReceiveHisKey;

import java.util.List;

public interface ReceiveHisDetailDao {
    ReceiveHisDetail save(ReceiveHisDetail obj);

    ReceiveHisDetail find(ReceiveHisDetailKey key);

    List<ReceiveHisDetail> search(String vouNo, String compCode);

    void delete(ReceiveHisDetailKey key);
}
