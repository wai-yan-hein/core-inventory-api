package cv.api.inv.service;

import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisKey;

import java.util.Date;
import java.util.List;

public interface TransferHisService {
    TransferHis save(TransferHis th);

    TransferHis findById(TransferHisKey key);

    List<TransferHis> unUpload(String syncDate);

    void delete(TransferHisKey key);

    void restore(TransferHisKey key);

    Date getMaxDate();

    List<TransferHis> search(String updatedDate, List<String> keys);


}
