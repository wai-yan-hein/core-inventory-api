package cv.api.inv.dao;

import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisKey;

public interface TransferHisDao {
    TransferHis save(TransferHis th);

    TransferHis findById(TransferHisKey key);
}
