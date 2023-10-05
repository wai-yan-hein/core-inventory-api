package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.GRN;
import cv.api.entity.GradeHisKey;
import cv.api.entity.GradeHis;
import cv.api.entity.GradeHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class GradeHisDaoImpl extends AbstractDao<GradeHisKey, GradeHis> implements GradeHisDao {
    @Override
    public GradeHis findByCode(GradeHisKey key) {
        GradeHis grn = getByKey(key);
        if (grn != null) {
            grn.setVouDateTime(Util1.toZonedDateTime(grn.getVouDate()));
        }
        return grn;
    }

    @Override
    public GradeHis save(GradeHis b) {
        saveOrUpdate(b, b.getKey());
        return b;
    }

    @Override
    public List<GradeHis> findAll(String compCode, Integer deptId) {
        String hsql = "select o from GradeHis o where o.key.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public boolean delete(GradeHisKey key) {
        String sql = "update grade_his set deleted = true where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
        return true;
    }

    @Override
    public boolean restore(GradeHisKey key) {
        String sql = "update grade_his set deleted = false where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
        return true;
    }

    @Override
    public List<GradeHis> search(String compCode, Integer deptId) {
        List<GradeHis> list = new ArrayList<>();
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
                    GradeHis g = new GradeHis();
                    GradeHisKey key = new GradeHisKey();
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
    public boolean open(GradeHisKey key) {
        String sql = "update grade_his set closed =0 where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
        return true;
    }

}
