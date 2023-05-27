package cv.api.dao;

import cv.api.entity.PriceOption;
import cv.api.entity.PriceOptionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class PriceOptionDaoImpl extends AbstractDao<PriceOptionKey, PriceOption> implements PriceOptionDao {
    @Override
    public PriceOption save(PriceOption p) {
        saveOrUpdate(p,p.getKey());
        return p;
    }

    @Override
    public List<PriceOption> getPriceOption(String updatedDate) {
        String hsql = "select o from PriceOption o where o.updatedDate > '" + updatedDate + "'";
        return findHSQL(hsql);
    }

    @Override
    public List<PriceOption> getPriceOption(String option, String compCode, Integer deptId) {
        List<PriceOption> list = new ArrayList<>();
        String sql = "select * \n" +
                "from price_option\n" +
                "where comp_code ='" + compCode + "'\n" +
                "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and tran_option='" + option + "'\n" +
                "order by unique_id;";
        ResultSet rs = getResult(sql);
        if (rs != null) {
            try {
                while (rs.next()) {
                    //tran_type, desp, comp_code, unique_id, dept_id, tran_option
                    PriceOption op = new PriceOption();
                    PriceOptionKey key = new PriceOptionKey();
                    key.setPriceType(rs.getString("type"));
                    key.setCompCode(rs.getString("comp_code"));
                    key.setDeptId(rs.getInt("dept_id"));
                    op.setKey(key);
                    op.setTranOption(rs.getString("tran_option"));
                    op.setDescription(rs.getString("desp"));
                    op.setUniqueId(rs.getInt("unique_id"));
                    list.add(op);
                }
            } catch (Exception e) {
                log.error("getPriceOption : " + e.getMessage());
            }
        }
        return list;
    }
}
