package cv.api.dao;

import cv.api.entity.ConsignHis;
import cv.api.entity.ConsignHisKey;

public interface ConsignDao {
    ConsignHis save(ConsignHis obj);

    ConsignHis findById(ConsignHisKey key);

    boolean delete(ConsignHisKey key);

    boolean restore(ConsignHisKey key);
}
