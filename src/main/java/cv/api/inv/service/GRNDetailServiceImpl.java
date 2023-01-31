package cv.api.inv.service;

import cv.api.inv.dao.GRNDetailDao;
import cv.api.inv.entity.GRNDetail;
import cv.api.inv.entity.GRNDetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
