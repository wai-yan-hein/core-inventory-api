package cv.api.dao;

import cv.api.entity.*;
import cv.api.service.StockFormulaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class StockFormulaDetailDaoImpl extends AbstractDao<StockFormulaDetailKey, StockFormulaDetail> implements StockFormulaDetailDao {
    @Override
    public StockFormulaDetail save(StockFormulaDetail s) {
        saveOrUpdate(s, s.getKey());
        return s;
    }

    @Override
    public boolean delete(StockFormulaDetailKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<StockFormulaDetail> getFormulaDetail(String code, String compCode) {
        String sql = """
                select s.*,sc.criteria_name,sc.user_code
                from stock_formula_detail s
                join stock_criteria sc on s.criteria_code = sc.criteria_code
                and s.comp_code = s.comp_code
                where s.comp_code =?
                and s.formula_code = ?
                order by s.unique_id
                """;
        ResultSet rs = getResult(sql, compCode, code);
        List<StockFormulaDetail> list = new ArrayList<>();
        try {
            while (rs.next()) {
                //formula_code, comp_code, unique_id, criteria_code, percent, price
                StockFormulaDetail d = new StockFormulaDetail();
                StockFormulaDetailKey key = new StockFormulaDetailKey();
                key.setFormulaCode(rs.getString("formula_code"));
                key.setCompCode(rs.getString("comp_code"));
                key.setUniqueId(rs.getInt("unique_id"));
                d.setKey(key);
                d.setCriteriaCode(rs.getString("criteria_code"));
                d.setUserCode(rs.getString("user_code"));
                d.setCriteriaName(rs.getString("criteria_name"));
                d.setPercent(rs.getDouble("percent"));
                d.setPrice(rs.getDouble("price"));
                d.setPercentAllow(rs.getDouble("percent_allow"));
                list.add(d);
            }
        } catch (Exception e) {
            log.error("getFormulaDetail : "+e.getMessage());
        }
        return list;
    }

    @Override
    public List<StockFormulaDetail> getFormulaDetail(String code) {
        String sql = """
                select s.*,sc.criteria_name,sc.user_code
                from stock_formula_detail s
                join stock_criteria sc on s.criteria_code = sc.criteria_code
                and s.comp_code = s.comp_code
                where s.formula_code = ?
                """;
        ResultSet rs = getResult(sql, code);
        List<StockFormulaDetail> list = new ArrayList<>();
        try {
            while (rs.next()) {
                //formula_code, comp_code, unique_id, criteria_code, percent, price
                StockFormulaDetail d = new StockFormulaDetail();
                StockFormulaDetailKey key = new StockFormulaDetailKey();
                key.setFormulaCode(rs.getString("formula_code"));
                key.setCompCode(rs.getString("comp_code"));
                key.setUniqueId(rs.getInt("unique_id"));
                d.setKey(key);
                d.setCriteriaCode(rs.getString("criteria_code"));
                d.setUserCode(rs.getString("user_code"));
                d.setCriteriaName(rs.getString("criteria_name"));
                d.setPercent(rs.getDouble("percent"));
                d.setPrice(rs.getDouble("price"));
                list.add(d);
            }
        } catch (Exception e) {
            log.error("getFormulaDetail : "+e.getMessage());
        }
        return list;
    }

}
