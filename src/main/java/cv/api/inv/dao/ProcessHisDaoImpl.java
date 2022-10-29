package cv.api.inv.dao;

import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisKey;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProcessHisDaoImpl extends AbstractDao<ProcessHisKey, ProcessHis> implements ProcessHisDao {
    @Override
    public ProcessHis save(ProcessHis ph) {
        persist(ph);
        return ph;
    }

    @Override
    public ProcessHis findById(ProcessHisKey key) {

        return getByKey(key);
    }

    @Override
    public List<ProcessHis> search(String fromDate, String toDate, String vouNo, String processNo, String remark,
                                   String stockCode, String pt, String locCode, boolean finish, boolean deleted, String compCode, Integer deptId) {
        List<ProcessHis> list = new ArrayList<>();
        return list;
    }

    @Override
    public void delete(ProcessHisKey key) {
        String sql = "update process_his set deleted =1 where vou_no ='" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "' and dept_id =" + key.getDeptId() + "";
        execSQL(sql);
    }
}
