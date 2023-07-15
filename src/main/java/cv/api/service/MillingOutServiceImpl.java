/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.MillingOutDao;
import cv.api.entity.MillingOutDetail;
import cv.api.entity.MillingOutDetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class MillingOutServiceImpl implements MillingOutService {

    @Autowired
    private MillingOutDao dao;

    @Override
    public MillingOutDetail save(MillingOutDetail sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<MillingOutDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(MillingOutDetailKey key) {
        return dao.delete(key);
    }
}
