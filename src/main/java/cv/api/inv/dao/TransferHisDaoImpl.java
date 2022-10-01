package cv.api.inv.dao;

import cv.api.inv.entity.TransferHis;
import cv.api.inv.entity.TransferHisKey;
import cv.api.inv.service.TransferHisService;
import org.springframework.stereotype.Repository;

@Repository
public class TransferHisDaoImpl extends AbstractDao<TransferHisKey, TransferHis> implements TransferHisDao {
    @Override
    public TransferHis save(TransferHis th) {
        persist(th);
        return th;
    }

    @Override
    public TransferHis findById(TransferHisKey key) {
        return getByKey(key);
    }
}
