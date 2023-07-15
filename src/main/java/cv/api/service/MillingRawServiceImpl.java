/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.dao.MillingRawDao;
import cv.api.entity.MillingRawDetail;
import cv.api.entity.MillingRawDetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class MillingRawServiceImpl implements MillingRawService {

    @Autowired
    private MillingRawDao dao;

    @Override
    public MillingRawDetail save(MillingRawDetail sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<MillingRawDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }

    @Override
    public int delete(MillingRawDetailKey key) {
        return dao.delete(key);
    }

}
