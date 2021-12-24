package com.cv.inv.api.service;

import com.cv.inv.api.dao.SysPropertyDao;
import com.cv.inv.api.entity.SysProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SysPropertyServiceImpl implements SysPropertyService {
    @Autowired
    private SysPropertyDao dao;

    @Override
    public SysProperty save(SysProperty property) throws Exception {
        return dao.save(property);
    }

    @Override
    public List<SysProperty> search(String compCode) {
        return dao.search(compCode);
    }
}
