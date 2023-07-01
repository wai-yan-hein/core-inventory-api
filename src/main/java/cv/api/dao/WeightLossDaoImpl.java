package cv.api.dao;

import cv.api.entity.WeightLossHis;
import cv.api.entity.WeightLossHisKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WeightLossDaoImpl extends AbstractDao<WeightLossHisKey, WeightLossHis> implements WeightLossDao {
    @Override
    public WeightLossHis save(WeightLossHis l) {
        saveOrUpdate(l,l.getKey());
        return l;
    }

    @Override
    public WeightLossHis findById(WeightLossHisKey key) {
        return getByKey(key);
    }

    @Override
    public void delete(WeightLossHisKey key) {
        String sql = "update weight_loss_his set deleted = true where vou_no = '" + key.getVouNo() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
    }

    @Override
    public void restore(WeightLossHisKey key) {
        String sql = "update weight_loss_his set deleted = false where vou_no = '" + key.getVouNo() + "' and comp_code =" + key.getCompCode() + "'";
        execSql(sql);
    }

    @Override
    public List<WeightLossHis> search(String fromDate, String toDate, String locCode, String compCode, Integer deptId) {
        return null;
    }
}
