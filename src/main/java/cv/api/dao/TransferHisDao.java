package cv.api.dao;

import cv.api.entity.TransferHis;
import cv.api.entity.TransferHisKey;

import java.util.Date;
import java.util.List;

public interface TransferHisDao {
    TransferHis save(TransferHis th);

    TransferHis findById(TransferHisKey key);

    List<TransferHis> unUpload(String syncDate);

    void delete(TransferHisKey key);

    void restore(TransferHisKey key);

    List<TransferHis> search(String updatedDate, List<String> location);
    void truncate(TransferHisKey key);


}
