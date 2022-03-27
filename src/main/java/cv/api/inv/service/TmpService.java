package cv.api.inv.service;

import cv.api.inv.entity.TmpStockIO;

import java.util.List;

public interface TmpService {
    TmpStockIO save(TmpStockIO io);

    List<TmpStockIO> getStockIO(String stockCode, Integer macId);
}
