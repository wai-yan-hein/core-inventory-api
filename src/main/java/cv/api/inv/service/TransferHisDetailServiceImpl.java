package cv.api.inv.service;

import cv.api.inv.dao.TransferHisDetailDao;
import cv.api.inv.entity.TransferHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransferHisDetailServiceImpl implements TransferHisDetailService {
    @Autowired
    private TransferHisDetailDao dao;

    @Override
    public TransferHisDetail save(TransferHisDetail th) {
        return dao.save(th);
    }

    @Override
    public int delete(String code, String compCode, Integer deptId) {
        return dao.delete(code, compCode, deptId);
    }

    @Override
    public List<TransferHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }
}
