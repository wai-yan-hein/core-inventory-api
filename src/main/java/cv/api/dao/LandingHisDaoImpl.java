package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.LandingHisKey;
import cv.api.entity.LandingHis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class LandingHisDaoImpl extends AbstractDao<LandingHisKey, LandingHis> implements LandingHisDao {
    @Override
    public LandingHis findByCode(LandingHisKey key) {
        LandingHis grn = getByKey(key);
        if (grn != null) {
            grn.setVouDateTime(Util1.toZonedDateTime(grn.getVouDate()));
        }
        return grn;
    }

    @Override
    public LandingHis save(LandingHis b) {
        saveOrUpdate(b, b.getKey());
        return b;
    }

    @Override
    public List<LandingHis> findAll(String compCode, Integer deptId) {
        String hsql = "select o from GradeHis o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public boolean delete(LandingHisKey key) {
        String sql = "update grade_his set deleted = true where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
        return true;
    }

    @Override
    public boolean restore(LandingHisKey key) {
        String sql = "update grade_his set deleted = false where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
        return true;
    }

    @Override
    public List<LandingHis> search(String compCode, Integer deptId) {
        List<LandingHis> list = new ArrayList<>();
        String sql = """
                select t.trader_name,a.vou_no
                from (
                select trader_code,comp_code,dept_id,vou_no
                from grade_his
                where comp_code=?
                and (dept_id =? or 0=?)
                and deleted = false
                and closed =0
                limit 20
                )a
                join trader t on
                a.trader_code = t.code
                and a.comp_code = t.comp_code
                and a.dept_id = t.dept_id""";
        try {
            ResultSet rs = getResult(sql, compCode, deptId, deptId + "%");
            if (rs != null) {
                while (rs.next()) {
                    LandingHis g = new LandingHis();
                    LandingHisKey key = new LandingHisKey();
                    key.setVouNo(rs.getString("vou_no"));
                    g.setKey(key);
//                    g.setBatchNo(rs.getString("batch_no"));
                    g.setTraderName(rs.getString("trader_name"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error("search : " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean open(LandingHisKey key) {
        String sql = "update grade_his set closed =0 where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
        return true;
    }

}
