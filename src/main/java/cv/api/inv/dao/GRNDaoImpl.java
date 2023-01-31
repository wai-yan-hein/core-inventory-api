package cv.api.inv.dao;

import cv.api.inv.entity.GRN;
import cv.api.inv.entity.GRNKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GRNDaoImpl extends AbstractDao<GRNKey, GRN> implements GRNDao {
    @Override
    public GRN findByCode(GRNKey key) {
        return getByKey(key);
    }

    @Override
    public GRN save(GRN b) {
        persist(b);
        return b;
    }

    @Override
    public List<GRN> findAll(String compCode, Integer deptId) {
        String hsql = "select o from GRN o where o.key.compCode ='" + compCode + "' and o.key.deptId =" + deptId + "";
        return findHSQL(hsql);
    }

    @Override
    public void delete(GRNKey key) {
        String sql = "update grn set deleted =1 where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSQL(sql);
    }
}
