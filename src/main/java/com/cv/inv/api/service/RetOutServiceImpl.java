/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.RetOutDao;
import com.cv.inv.api.entity.RetOutCompoundKey;
import com.cv.inv.api.entity.RetOutHis;
import com.cv.inv.api.entity.RetOutHisDetail;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author lenovo
 */
@Service
@Transactional
public class RetOutServiceImpl implements RetOutService {

    private static final Logger log = LoggerFactory.getLogger(RetOutServiceImpl.class);
    @Autowired
    private RetOutDao retOutDao;
    @Autowired
    private RetOutDetailService outDetailService;

    @Override
    public void save(RetOutHis retOut, List<RetOutHisDetail> listRetIn, List<String> delList) {
        for (int i = 0; i < listRetIn.size(); i++) {
            RetOutHisDetail cRD = listRetIn.get(i);
            if (cRD.getUniqueId() == null) {
                if (i == 0) {
                    cRD.setUniqueId(1);
                } else {
                    RetOutHisDetail pRD = listRetIn.get(i - 1);
                    cRD.setUniqueId(pRD.getUniqueId() + 1);
                }
            }
        }
        if (delList != null) {
            delList.forEach(detailId -> {
                try {
                    outDetailService.delete(detailId);
                } catch (Exception ex) {
                    log.error("Delete Return Out Detail :" + ex.getMessage());
                }
            });
        }
        retOutDao.save(retOut);
        String vouNo = retOut.getVouNo();
        listRetIn.stream().filter(rd -> (rd.getStock() != null)).map(rd -> {
            if (rd.getOutCompoundKey() != null) {
                rd.setOutCompoundKey(rd.getOutCompoundKey());
            } else {
                String retInDetailId = vouNo + '-' + rd.getUniqueId();
                rd.setOutCompoundKey(new RetOutCompoundKey(retInDetailId, vouNo));
            }
            return rd;
        }).forEachOrdered(rd -> {
            outDetailService.save(rd);
        });
    }

    @Override
    public void delete(String retInId) throws Exception {
        retOutDao.delete(retInId);
    }

    @Override
    public List<RetOutHis> search(String fromDate, String toDate, String cusId, String locId, String vouNo, String filterCode) {
        return retOutDao.search(fromDate, toDate, cusId, locId, vouNo, filterCode);
    }

    @Override
    public RetOutHis findById(String id) {
        return retOutDao.findById(id);
    }
}
