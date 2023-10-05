package cv.api.dao;

import cv.api.entity.GRNDetailFormula;
import cv.api.entity.GRNDetailFormulaKey;

import java.util.List;

public interface GRNDetailFormulaDao {
    GRNDetailFormula save(GRNDetailFormula f);
    boolean delete(GRNDetailFormulaKey key);

    List<GRNDetailFormula> getGRNDetailFormula(String vouNo,int uniqueId,String compCode);
}
