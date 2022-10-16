package cv.api.inv.dao;

import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransferHisDaoImpl extends AbstractDao<TransferHisKey, TransferHis> implements TransferHisDao {
    @Autowired
    private TransferHisDetailDao dao;

    @Override
    public TransferHis save(TransferHis th) {
        persist(th);
        return th;
    }

    @Override
    public TransferHis findById(TransferHisKey key) {
        return getByKey(key);
    }

    @Override
    public List<TransferHis> unUpload() {
        String hsql = "select o from TransferHis o where o.intgUpdStatus is null";
        List<TransferHis> list = findHSQL(hsql);
        list.forEach((o) -> {
            String vouNo = o.getKey().getVouNo();
            String compCode = o.getKey().getCompCode();
            Integer depId = o.getKey().getDeptId();
            o.setListTD(dao.search(vouNo, compCode, depId));
        });
        return list;
    }

    @Override
    public void delete(TransferHisKey key) {
        String vouNo = key.getVouNo();
        String compCode = key.getCompCode();
        Integer deptId = key.getDeptId();
        String sql = "update transfer_his set deleted =1 where vou_no ='" + vouNo + "' and comp_code='" + compCode + "' and dept_id =" + deptId + "";
        execSQL(sql);
    }
}
