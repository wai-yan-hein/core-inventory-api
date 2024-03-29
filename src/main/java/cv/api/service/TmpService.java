package cv.api.service;

import cv.api.entity.TmpStockIO;

import java.util.List;

public interface TmpService {
    TmpStockIO save(TmpStockIO io);

    List<TmpStockIO> getStockIO(String stockCode, String compCode, Integer deptId, Integer macId);
}
