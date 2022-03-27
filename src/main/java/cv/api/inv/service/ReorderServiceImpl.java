package cv.api.inv.service;

import cv.api.inv.dao.ReorderDao;
import cv.api.inv.entity.ReorderLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReorderServiceImpl implements ReorderService {
    @Autowired
    private ReorderDao reorderDao;

    @Override
    public ReorderLevel save(ReorderLevel rl) {
        return reorderDao.save(rl);
    }
}
