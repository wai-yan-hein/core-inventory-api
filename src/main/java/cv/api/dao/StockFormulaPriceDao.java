package cv.api.dao;

import cv.api.entity.StockFormulaPrice;
import cv.api.entity.StockFormulaPriceKey;

import java.util.List;

public interface StockFormulaPriceDao {
    StockFormulaPrice save(StockFormulaPrice s);

    boolean delete(StockFormulaPriceKey key);

    List<StockFormulaPrice> getFormulaDetail(String code, String compCode);

    List<StockFormulaPrice> getFormulaDetail(String code);

}
