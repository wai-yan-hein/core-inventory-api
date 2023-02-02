package cv.api.dao;

import cv.api.entity.GRN;
import cv.api.entity.GRNKey;
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
    public boolean delete(GRNKey key) {
        String sql = "update grn set deleted =1 where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSQL(sql);
        return true;
    }

    @Override
    public List<GRN> search(String str, String compCode, Integer deptId) {
        return null;
    }

}
