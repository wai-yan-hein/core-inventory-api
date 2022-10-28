package cv.api.inv.dao;

import cv.api.inv.entity.TmpStockIO;

import java.util.List;

public interface TmpDao {
    TmpStockIO save(TmpStockIO io);

    List<TmpStockIO> getStockIO(String stockCode, String compCode, Integer deptId, Integer macId);
}
