package cv.api.dao;

import cv.api.entity.ReceiveHisDetail;
import cv.api.entity.ReceiveHisDetailKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReceiveDetailHisDaoImpl extends AbstractDao<ReceiveHisDetailKey, ReceiveHisDetail> implements ReceiveHisDetailDao {
    @Override
    public ReceiveHisDetail save(ReceiveHisDetail obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public ReceiveHisDetail find(ReceiveHisDetailKey key) {
        return getByKey(key);
    }

    @Override
    public List<ReceiveHisDetail> search(String vouNo, String compCode) {
        String hsql = "select o from ReceiveHisDetail where o.vouNo='" + vouNo + "' and o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }

    @Override
    public void delete(ReceiveHisDetailKey key) {
        remove(key);
    }
}
