package cv.api.inv.dao;

import cv.api.inv.entity.LocationKey;
import cv.api.inv.entity.OPHis;
import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisKey;

import java.util.Date;
import java.util.List;

public interface TransferHisDao {
    TransferHis save(TransferHis th);

    TransferHis findById(TransferHisKey key);

    List<TransferHis> unUpload();

    void delete(TransferHisKey key);

    void restore(TransferHisKey key);

    Date getMaxDate();

    List<TransferHis> search(String updatedDate, List<String> location);


}
