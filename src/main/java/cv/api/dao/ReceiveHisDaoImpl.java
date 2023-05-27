package cv.api.dao;

import cv.api.entity.ReceiveHis;
import cv.api.entity.ReceiveHisKey;
import org.springframework.stereotype.Repository;

@Repository
public class ReceiveHisDaoImpl extends AbstractDao<ReceiveHisKey, ReceiveHis> implements ReceiveHisDao {
    @Override
    public ReceiveHis save(ReceiveHis obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public ReceiveHis find(ReceiveHisKey key) {
        return getByKey(key);
    }

    @Override
    public void delete(ReceiveHisKey key) {
        String sql = "update receive_his set deleted =1 where comp_code ='" + key.getCompCode() + "' and vou_no='" + key.getVouNo() + "'";
        execSql(sql);
    }
}
