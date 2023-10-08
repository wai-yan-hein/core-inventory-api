package cv.api.service;

import cv.api.entity.StockFormula;
import cv.api.entity.StockFormulaDetail;
import cv.api.entity.StockFormulaDetailKey;
import cv.api.entity.StockFormulaKey;

import java.util.List;

public interface StockFormulaService {
    StockFormula save(StockFormula s);
    StockFormula find(StockFormulaKey key);

    boolean delete(StockFormulaKey key);

    List<StockFormula> getFormula(String compCode);

    StockFormulaDetail save(StockFormulaDetail s);

    boolean delete(StockFormulaDetailKey key);

    List<StockFormulaDetail> getFormulaDetail(String code, String compCode);

}
