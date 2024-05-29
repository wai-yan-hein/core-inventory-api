package cv.api.dao;

import cv.api.entity.GradeDetail;
import cv.api.entity.GradeDetailKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class GradeDetailDaoImpl extends AbstractDao<GradeDetailKey, GradeDetail> implements GradeDetailDao {
    @Override
    public GradeDetail save(GradeDetail s) {
        saveOrUpdate(s, s.getKey());
        return s;
    }

    @Override
    public boolean delete(GradeDetailKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<GradeDetail> getGradeDetail(String formulaCode, String criteriaCode, String compCode) {
        List<GradeDetail> list = new ArrayList<>();
        String sql = """
                select g.*,s.stock_name
                from grade_detail g left join stock s on g.grade_stock_code = s.stock_code
                and g.comp_code = s.comp_code
                where g.comp_code = :compCode
                and g.formula_code = :formulaCode
                and g.criteria_code = :criteriaCode
                order by g.unique_id
                """;
        try {
            ResultSet rs = getResult(sql, compCode, formulaCode, criteriaCode);
            while (rs.next()) {
                GradeDetail d = GradeDetail.builder().build();
                GradeDetailKey key = GradeDetailKey.builder().build();
                key.setFormulaCode(rs.getString("formula_code"));
                key.setCriteriaCode(rs.getString("criteria_code"));
                key.setCompCode(rs.getString("comp_code"));
                key.setUniqueId(rs.getInt("unique_id"));
                d.setKey(key);
                d.setMinPercent(rs.getDouble("min_percent"));
                d.setMaxPercent(rs.getDouble("max_percent"));
                d.setGradeStockCode(rs.getString("grade_stock_code"));
                d.setStockName(rs.getString("stock_name"));
                list.add(d);
            }
        } catch (Exception e) {
            log.error("getGradeDetail : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<GradeDetail> getStockFormulaGrade(String formulaCode, String compCode) {
        List<GradeDetail> list = new ArrayList<>();
        String sql = """
                select g.*,s.stock_name
                from grade_detail g join stock s
                on g.grade_stock_code = s.stock_code
                and g.comp_code = s.comp_code
                where g.formula_code =?
                and g.comp_code =?
                order by g.unique_id""";
        try {
            ResultSet rs = getResult(sql, formulaCode, compCode);
            while (rs.next()) {
                GradeDetail d = GradeDetail.builder().build();
                GradeDetailKey key = GradeDetailKey.builder().build();
                key.setFormulaCode(rs.getString("formula_code"));
                key.setCriteriaCode(rs.getString("criteria_code"));
                key.setCompCode(rs.getString("comp_code"));
                key.setUniqueId(rs.getInt("unique_id"));
                d.setKey(key);
                d.setMinPercent(rs.getDouble("min_percent"));
                d.setMaxPercent(rs.getDouble("max_percent"));
                d.setGradeStockCode(rs.getString("grade_stock_code"));
                d.setStockName(rs.getString("stock_name"));
                list.add(d);
            }
        } catch (Exception e) {
            log.error("getStockFormulaGrade : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<GradeDetail> getGradeDetail(LocalDateTime updatedDate) {
        String hsql = "select o from GradeDetail o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
