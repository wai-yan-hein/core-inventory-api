package cv.api.service;

import cv.api.dao.StockFormulaQtyDao;
import cv.api.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public interface StockFormulaService {
    StockFormula save(StockFormula s);

    StockFormula find(StockFormulaKey key);

    boolean delete(StockFormulaKey key);

    List<StockFormula> getFormula(String compCode);

    StockFormulaPrice save(StockFormulaPrice s);
    StockFormulaQty save(StockFormulaQty s);

    boolean delete(StockFormulaPriceKey key);
    boolean delete(StockFormulaQtyKey key);

    boolean delete(GradeDetailKey key);

    List<StockFormulaPrice> getStockFormulaPrice(String formulaCode, String compCode);
    List<StockFormulaQty> getStockFormulaQty(String formulaCode, String compCode);
    List<GradeDetail> getGradeDetail(String formulaCode, String criteriaCode, String compCode);


    List<StockFormula> getStockFormula(LocalDateTime updatedDate);

    List<StockFormulaPrice> getStockFormulaPrice(LocalDateTime updatedDate);

    List<StockFormulaQty> getStockFormulaQty(LocalDateTime updatedDate);
    List<GradeDetail> getGradeDetail(LocalDateTime updatedDate);
    GradeDetail save(GradeDetail g);


    List<GradeDetail> getStockFormulaGrade(String formulaCode, String compCode);
}
