package cv.api.dao;

import cv.api.entity.LandingHisPrice;
import cv.api.entity.LandingHisPriceKey;
import cv.api.entity.LandingHisQty;
import cv.api.entity.LandingHisQtyKey;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class LandingHisQtyDaoImpl extends AbstractDao<LandingHisQtyKey, LandingHisQty> implements LandingHisQtyDao {
    @Override
    public LandingHisQty save(LandingHisQty f) {
        saveOrUpdate(f, f.getKey());
        return f;
    }

    @Override
    public boolean delete(LandingHisQtyKey key) {
        remove(key);
        return true;
    }

    @Override
    public List<LandingHisQty> getLandingQty(String vouNo, String compCode) {
        List<LandingHisQty> list = new ArrayList<>();
        String sql = """
                select l.*,sc.criteria_name,sc.user_code
                from landing_his_qty l join stock_criteria sc
                on l.criteria_code = sc.criteria_code
                and l.comp_code = sc.comp_code
                where l.vou_no =?
                and l.comp_code =?
                """;
        try {
            ResultSet rs = getResult(sql, vouNo, compCode);
            while (rs.next()) {
                LandingHisQty l = new LandingHisQty();
                LandingHisQtyKey key = new LandingHisQtyKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                key.setUniqueId(rs.getInt("unique_id"));
                l.setKey(key);
                l.setCriteriaCode(rs.getString("criteria_code"));
                l.setCriteriaUserCode(rs.getString("user_code"));
                l.setCriteriaName(rs.getString("criteria_name"));
                l.setPercent(rs.getDouble("percent"));
                l.setPercentAllow(rs.getDouble("percent_allow"));
                l.setQty(rs.getDouble("qty"));
                l.setTotalQty(rs.getDouble("total_qty"));
                l.setUnit(rs.getString("unit"));
                list.add(l);
            }
        } catch (Exception e) {
            log.error("getLandingDetailCriteria : " + e.getMessage());
        }
        return list;
    }



}
