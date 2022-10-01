package cv.api.inv.service;

import cv.api.inv.dao.ProcessHisDao;
import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcessHisServiceImpl implements ProcessHisService {
    @Autowired
    private ProcessHisDao dao;

    @Override
    public ProcessHis save(ProcessHis ph) {
        return dao.save(ph);
    }

    @Override
    public ProcessHis findById(ProcessHisKey key) {
        return dao.findById(key);
    }
}
