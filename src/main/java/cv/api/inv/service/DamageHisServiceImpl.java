/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.DamageDetailHisDao;
import cv.api.inv.dao.DamageHisDao;
import cv.api.inv.entity.DamageDetailHis;
import cv.api.inv.entity.DamageHis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class DamageHisServiceImpl implements DamageHisService {

    private static final Logger log = LoggerFactory.getLogger(DamageHisServiceImpl.class);

    @Autowired
    private DamageHisDao dao;
    @Autowired
    private DamageDetailHisDao detailDao;

    @Override
    public DamageHis save(DamageHis sdh) {
        return dao.save(sdh);
    }

    @Override
    public void save(DamageHis sdh, List<DamageDetailHis> listDamageDetail, String vouStatus, List<String> delList) {
        if (vouStatus.equals("EDIT")) {
            if (delList != null) {
                for (String detailId : delList) {
                    detailDao.delete(detailId);
                }
            }
        }
        dao.save(sdh);
        for (DamageDetailHis dh : listDamageDetail) {
            if (dh.getStock().getStockCode() != null) {
                dh.setDmgVouId(sdh.getDmgVouId());
                detailDao.save(dh);
            }
        }
    }

    @Override
    public List<DamageHis> search(String from, String to, String location, String remark, String vouNo) {
        return dao.search(from, to, location, remark, vouNo);
    }

    @Override
    public DamageHis findById(String id) {
        return dao.findById(id);
    }

    @Override
    public int delete(String vouNo) {
        return dao.delete(vouNo);
    }

}
