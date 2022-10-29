package cv.api.inv.dao;

import cv.api.inv.entity.ProcessHisDetail;
import cv.api.inv.entity.ProcessHisDetailKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProcessHisDetailDaoImpl extends AbstractDao<ProcessHisDetailKey, ProcessHisDetail> implements ProcessHisDetailDao {
    @Override
    public ProcessHisDetail save(ProcessHisDetail ph) {
        persist(ph);
        return ph;
    }

    @Override
    public ProcessHisDetail findById(ProcessHisDetailKey key) {
        return getByKey(key);
    }

    @Override
    public List<ProcessHisDetail> search(String vouNo, String stockCode, String compCode, Integer deptId) {
        return null;
    }

    @Override
    public void delete(ProcessHisDetailKey key) {
        String sql = "delete from process_his_detail where vou_no='" + key.getVouNo() + "'\n"
                + "and unique_id =" + key.getUniqueId() + " and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSQL(sql);
    }
}
