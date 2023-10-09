package cv.api.service;

import cv.api.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public interface StockFormulaService {
    StockFormula save(StockFormula s);
    StockFormula find(StockFormulaKey key);

    boolean delete(StockFormulaKey key);

    List<StockFormula> getFormula(String compCode);

    StockFormulaDetail save(StockFormulaDetail s);

    boolean delete(StockFormulaDetailKey key);

    List<StockFormulaDetail> getFormulaDetail(String code, String compCode);

    List<StockFormulaDetail> getFormulaDetail(String code);

    List<StockFormula> getStockFormula(LocalDateTime updatedDate);
    GradeDetail save(GradeDetail g);
    List<GradeDetail> getGradeDetail(String formulaCode,String criteriaCode, String compCode);


}
