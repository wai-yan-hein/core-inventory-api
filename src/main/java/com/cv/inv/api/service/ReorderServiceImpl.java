package com.cv.inv.api.service;

import com.cv.inv.api.dao.ReorderDao;
import com.cv.inv.api.entity.ReorderLevel;
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
