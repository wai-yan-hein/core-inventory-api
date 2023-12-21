package cv.api.dao;

import cv.api.entity.PurOrderHis;
import cv.api.entity.PurOrderHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class PurOrderHisDaoImpl extends AbstractDao<PurOrderHisKey, PurOrderHis> implements PurOrderHisDao {
    @Override
    public PurOrderHis save(PurOrderHis obj) {
        obj.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public PurOrderHis findById(PurOrderHisKey key) {
        return getByKey(key);
    }

    @Override
    public boolean delete(PurOrderHisKey key) {
        PurOrderHis his = findById(key);
        if (his != null) {
            his.setDeleted(true);
            his.setUpdatedDate(LocalDateTime.now());
            updateEntity(his);
        }
        return true;
    }

    @Override
    public boolean restore(PurOrderHisKey key) {
        PurOrderHis his = findById(key);
        if (his != null) {
            his.setDeleted(false);
            his.setUpdatedDate(LocalDateTime.now());
            updateEntity(his);
        }
        return true;
    }
}
