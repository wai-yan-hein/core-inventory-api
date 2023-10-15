package cv.api.dao;

import cv.api.entity.LandingHisGrade;
import cv.api.entity.LandingHisGradeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class LandingHisGradeDaoImpl extends AbstractDao<LandingHisGradeKey, LandingHisGrade> implements LandingHisGradeDao {
    @Override
    public LandingHisGrade save(LandingHisGrade f) {
        saveOrUpdate(f, f.getKey());
        return f;
    }

    @Override
    public List<LandingHisGrade> getLandingGrade(String vouNo, String compCode) {
        List<LandingHisGrade> list = new ArrayList<>();
        String sql = """
                select g.*,s.stock_name,s.user_code
                from landing_his_grade g join stock s
                on g.stock_code = s.stock_code
                and g.comp_code = s.comp_code
                where g.vou_no =?
                and g.comp_code =?
                order by g.unique_id
                """;
        try {
            ResultSet rs = getResult(sql, vouNo, compCode);
            while (rs.next()) {
                LandingHisGrade g = new LandingHisGrade();
                LandingHisGradeKey key = new LandingHisGradeKey();
                key.setVouNo(rs.getString("vou_no"));
                key.setCompCode(rs.getString("comp_code"));
                key.setUniqueId(rs.getInt("unique_id"));
                g.setKey(key);
                g.setUserCode(rs.getString("user_code"));
                g.setStockName(rs.getString("stock_name"));
                g.setChoose(rs.getBoolean("choose"));
                g.setMatchCount(rs.getDouble("match_count"));
                g.setStockCode(rs.getString("stock_code"));
                g.setMatchPercent(rs.getDouble("match_percent"));
                list.add(g);
            }
        } catch (Exception e) {
            log.error("getLandingGrade : " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean delete(LandingHisGradeKey key) {
        remove(key);
        return true;
    }

    @Override
    public boolean delete(String vouNo, String compCode) {
        getLandingGrade(vouNo,compCode).forEach((t)-> remove(t.getKey()));
        return true;
    }
}
