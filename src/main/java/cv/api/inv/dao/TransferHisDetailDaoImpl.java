package cv.api.inv.dao;

import cv.api.inv.entity.TransferHisDetail;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransferHisDetailDaoImpl extends AbstractDao<String, TransferHisDetail> implements TransferHisDetailDao {
    @Override
    public TransferHisDetail save(TransferHisDetail th) {
        persist(th);
        return th;
    }

    @Override
    public int delete(String code) {
        String delSql = "delete from transfer_his_detail  where td_code = '" + code + "'";
        execSQL(delSql);
        return 1;
    }

    @Override
    public List<TransferHisDetail> search(String vouNo) {
        String hsql = "select o from TransferHisDetail o where o.vouNo ='" + vouNo + "' order by o.uniqueId";
        return findHSQL(hsql);
    }
}
