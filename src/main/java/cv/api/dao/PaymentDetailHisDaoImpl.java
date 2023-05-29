package cv.api.dao;

import cv.api.entity.PaymentHisDetail;
import cv.api.entity.PaymentHisDetailKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentDetailHisDaoImpl extends AbstractDao<PaymentHisDetailKey, PaymentHisDetail> implements PaymentHisDetailDao {
    @Override
    public PaymentHisDetail save(PaymentHisDetail obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public PaymentHisDetail find(PaymentHisDetailKey key) {
        return getByKey(key);
    }

    @Override
    public List<PaymentHisDetail> search(String vouNo, String compCode,Integer deptId) {
        String hsql = "select o from PaymentHisDetail o where o.key.vouNo='" + vouNo + "' and o.key.compCode ='" + compCode + "' and o.key.deptId ='"+deptId+"'";
        return findHSQL(hsql);
    }

    @Override
    public void delete(PaymentHisDetailKey key) {
        remove(key);
    }
}
