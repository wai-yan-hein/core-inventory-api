package cv.api.dao;

import cv.api.entity.WeightHis;
import cv.api.entity.WeightHisDetail;
import cv.api.entity.WeightHisDetailKey;
import org.springframework.stereotype.Repository;

@Repository
public class WeightHisDetailDaoImpl extends AbstractDao<WeightHisDetailKey, WeightHisDetail> implements WeightDetailDao {
    @Override
    public WeightHisDetail save(WeightHisDetail obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public boolean delete(WeightHisDetailKey key) {
        remove(key);
        return true;
    }

    @Override
    public boolean deleteWeightHisDetail(String vouNo, String compCode) {
        String sql = "delete from weight_his_detail where vou_no =? and comp_code =?";
        deleteRecords(sql, vouNo, compCode);
        return true;
    }
}
