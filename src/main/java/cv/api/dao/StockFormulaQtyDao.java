package cv.api.dao;

import cv.api.entity.StockFormulaQty;
import cv.api.entity.StockFormulaQtyKey;

import java.time.LocalDateTime;
import java.util.List;

public interface StockFormulaQtyDao {
    StockFormulaQty save(StockFormulaQty s);

    boolean delete(StockFormulaQtyKey key);

    List<StockFormulaQty> getStockFormulaQty(String code, String compCode);
    List<StockFormulaQty> getStockFormulaQty(LocalDateTime updatedDate);

}
