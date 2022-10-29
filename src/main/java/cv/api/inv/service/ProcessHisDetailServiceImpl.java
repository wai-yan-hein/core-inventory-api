package cv.api.inv.service;

import cv.api.inv.dao.ProcessHisDetailDao;
import cv.api.inv.entity.ProcessHisDetail;
import cv.api.inv.entity.ProcessHisDetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProcessHisDetailServiceImpl implements ProcessHisDetailService {
    @Autowired
    private ProcessHisDetailDao dao;

    @Override
    public ProcessHisDetail save(ProcessHisDetail ph) {
        return dao.save(ph);
    }

    @Override
    public ProcessHisDetail findById(ProcessHisDetailKey key) {
        return dao.findById(key);
    }

    @Override
    public List<ProcessHisDetail> search(String vouNo, String stockCode, String compCode, Integer deptId) {
        return dao.search(vouNo, stockCode, compCode, deptId);
    }

    @Override
    public void delete(ProcessHisDetailKey key) {
        dao.delete(key);
    }
}
