package cv.api.dao;

import cv.api.entity.THDetailKey;
import cv.api.entity.TransferHisDetail;

import java.util.List;

public interface TransferHisDetailDao {
    TransferHisDetail save(TransferHisDetail th);

    int delete(THDetailKey key);

    List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId);

    List<TransferHisDetail> searchDetail(String vouNo, String compCode, Integer deptId);


}
