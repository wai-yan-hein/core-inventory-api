package cv.api.inv.dao;

import cv.api.inv.entity.TransferHisDetail;

import java.util.List;

public interface TransferHisDetailDao {
    TransferHisDetail save(TransferHisDetail th);

    int delete(String code, String compCode, Integer deptId);

    List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId);

}
