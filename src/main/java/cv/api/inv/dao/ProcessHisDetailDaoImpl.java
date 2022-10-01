package cv.api.inv.dao;

import cv.api.inv.entity.ProcessHisDetail;
import org.springframework.stereotype.Repository;

@Repository
public class ProcessHisDetailDaoImpl extends AbstractDao<String, ProcessHisDetail> implements ProcessHisDetailDao {
    @Override
    public ProcessHisDetail save(ProcessHisDetail phd) {
        persist(phd);
        return phd;
    }
}
