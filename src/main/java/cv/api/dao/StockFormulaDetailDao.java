package cv.api.dao;

import cv.api.entity.StockFormulaDetail;
import cv.api.entity.StockFormulaDetailKey;
import cv.api.service.StockFormulaService;

import java.util.List;

public interface StockFormulaDetailDao {
    StockFormulaDetail save(StockFormulaDetail s);

    boolean delete(StockFormulaDetailKey key);

    List<StockFormulaDetail> getFormulaDetail(String code, String compCode);
}
