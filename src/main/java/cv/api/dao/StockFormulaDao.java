package cv.api.dao;

import cv.api.entity.StockFormula;
import cv.api.entity.StockFormulaKey;

import java.time.LocalDateTime;
import java.util.List;

public interface StockFormulaDao {
    StockFormula save(StockFormula s);

    boolean delete(StockFormulaKey key);

    List<StockFormula> getFormula(String compCode);

    List<StockFormula> getStockFormula(LocalDateTime updatedDate);
}
