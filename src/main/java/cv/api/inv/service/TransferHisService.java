package cv.api.inv.service;

import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisKey;

public interface TransferHisService {
    TransferHis save(TransferHis th);

    TransferHis findById(TransferHisKey key);
}
