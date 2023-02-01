package cv.api.service;

import cv.api.dao.GRNDetailDao;
import cv.api.entity.GRNDetail;
import cv.api.entity.GRNDetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GRNDetailServiceImpl implements GRNDetailService {
    @Autowired
    private GRNDetailDao dao;

    @Override
    public GRNDetail save(GRNDetail b) {
        return dao.save(b);
    }

    @Override
    public void delete(GRNDetailKey key) {
        dao.delete(key);
    }

    @Override
    public List<GRNDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo,compCode,deptId);
    }
}
