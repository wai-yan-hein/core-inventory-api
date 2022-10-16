package cv.api.inv.dao;

import cv.api.inv.entity.OPHis;
import cv.api.inv.entity.OPHisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OPHisDaoImpl extends AbstractDao<OPHisKey, OPHis> implements OPHisDao {
    @Autowired
    private OPHisDetailDao dao;

    @Override
    public OPHis save(OPHis op) {
        persist(op);
        return op;
    }

    @Override
    public List<OPHis> search(String compCode) {
        String hsql = "select o from OPHis o where o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public OPHis findByCode(OPHisKey key) {
        return getByKey(key);
    }

    @Override
    public List<OPHis> unUpload() {
        String hsql = "select o from OPHis o where o.intgUpdStatus is null";
        List<OPHis> list = findHSQL(hsql);
        list.forEach(o -> {
            String compCode = o.getKey().getCompCode();
            String vouNo = o.getKey().getVouNo();
            Integer deptId = o.getKey().getDeptId();
            o.setDetailList(dao.search(vouNo, compCode, deptId));
        });
        return list;
    }

    @Override
    public void delete(OPHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update op_his set deleted =1 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(sql);
    }

}
