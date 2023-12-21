package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.WeightLossHis;
import cv.api.entity.WeightLossHisKey;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class WeightLossDaoImpl extends AbstractDao<WeightLossHisKey, WeightLossHis> implements WeightLossDao {
    @Override
    public WeightLossHis save(WeightLossHis l) {
        saveOrUpdate(l, l.getKey());
        return l;
    }

    @Override
    public WeightLossHis findById(WeightLossHisKey key) {
        WeightLossHis byKey = getByKey(key);
        if (byKey != null) {
            byKey.setVouDateTime(Util1.toZonedDateTime(byKey.getVouDate()));
        }
        return byKey;
    }

    @Override
    public void delete(WeightLossHisKey key) {
        WeightLossHis th = findById(key);
        th.setDeleted(true);
        th.setUpdatedDate(LocalDateTime.now());
        updateEntity(th);
    }

    @Override
    public void restore(WeightLossHisKey key) {
        WeightLossHis th = findById(key);
        th.setDeleted(false);
        th.setUpdatedDate(LocalDateTime.now());
        updateEntity(th);
    }

    @Override
    public List<WeightLossHis> search(String fromDate, String toDate, String locCode, String compCode, Integer deptId) {
        return null;
    }
}
