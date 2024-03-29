package cv.api.service;

import cv.api.entity.TransferHis;
import cv.api.entity.TransferHisKey;

import java.util.Date;
import java.util.List;

public interface TransferHisService {
    TransferHis save(TransferHis th);

    TransferHis findById(TransferHisKey key);

    List<TransferHis> unUpload(String syncDate);

    void delete(TransferHisKey key);

    void restore(TransferHisKey key);


    List<TransferHis> search(String updatedDate, List<String> keys);


    void truncate(TransferHisKey key);
}
