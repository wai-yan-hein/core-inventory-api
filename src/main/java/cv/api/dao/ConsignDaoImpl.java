package cv.api.dao;

import cv.api.entity.ConsignHis;
import cv.api.entity.ConsignHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@Slf4j
public class ConsignDaoImpl extends AbstractDao<ConsignHisKey, ConsignHis> implements ConsignDao {
    @Override
    public ConsignHis save(ConsignHis obj) {
        obj.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public ConsignHis findById(ConsignHisKey key) {
        return getByKey(key);
    }

    @Override
    public boolean delete(ConsignHisKey key) {
        ConsignHis his = findById(key);
        if (his != null) {
            his.setDeleted(true);
            his.setUpdatedDate(LocalDateTime.now());
            updateEntity(his);
        }
        return true;
    }

    @Override
    public boolean restore(ConsignHisKey key) {
        ConsignHis his = findById(key);
        if (his != null) {
            his.setDeleted(false);
            his.setUpdatedDate(LocalDateTime.now());
            updateEntity(his);
        }
        return true;
    }
}
