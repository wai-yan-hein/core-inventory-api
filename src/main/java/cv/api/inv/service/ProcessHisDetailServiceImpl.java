package cv.api.inv.service;

import cv.api.inv.dao.ProcessHisDetailDao;
import cv.api.inv.entity.ProcessHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProcessHisDetailServiceImpl implements ProcessHisDetailService {
    @Autowired
    private ProcessHisDetailDao dao;

    @Override
    public ProcessHisDetail save(ProcessHisDetail phd) {
        return dao.save(phd);
    }
}
