package cv.api.inv.service;

import cv.api.inv.entity.TransferHisDetail;

import java.util.List;

public interface TransferHisDetailService {
    TransferHisDetail save(TransferHisDetail th);

    int delete(String code, String compCode, Integer deptId);

    List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId);
}
