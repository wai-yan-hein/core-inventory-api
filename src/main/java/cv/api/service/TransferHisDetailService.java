package cv.api.service;

import cv.api.entity.THDetailKey;
import cv.api.entity.TransferHisDetail;

import java.util.List;

public interface TransferHisDetailService {
    TransferHisDetail save(TransferHisDetail th);

    int delete(THDetailKey key);

    List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId);
}
