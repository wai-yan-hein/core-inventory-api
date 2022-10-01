package cv.api.inv.dao;

import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisKey;
import org.springframework.stereotype.Repository;

@Repository
public class ProcessHisDaoImpl extends AbstractDao<ProcessHisKey, ProcessHis> implements ProcessHisDao {
    @Override
    public ProcessHis save(ProcessHis ph) {
        persist(ph);
        return ph;
    }

    @Override
    public ProcessHis findById(ProcessHisKey key) {
        return getByKey(key);
    }
}
