/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;



import com.cv.inv.api.dao.RetInDao;
import com.cv.inv.api.dao.RetInDetailDao;
import com.cv.inv.api.entity.RetInCompoundKey;
import com.cv.inv.api.entity.RetInHis;
import com.cv.inv.api.entity.RetInHisDetail;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Wai Yan
 */
@Service
@Transactional
public class RetInServiceImpl implements RetInService {

    private static final Logger log = LoggerFactory.getLogger(RetInServiceImpl.class);
    private final String SOURCE_PROG = "ACCOUNT";
    private final String DELETE_OPTION = "INV_DELETE";

    @Autowired
    private RetInDao retInDao;
    @Autowired
    private AccSettingService settingService;
   

    @Autowired
    private RetInDetailDao dao;

    @Override
    public void save(RetInHis retIn, List<RetInHisDetail> listRetIn, List<String> delList) {
        String retInDetailId;
        try {
            for (int i = 0; i < listRetIn.size(); i++) {
                RetInHisDetail cRD = listRetIn.get(i);
                if (cRD.getUniqueId() == null) {
                    if (i == 0) {
                        cRD.setUniqueId(1);
                    } else {
                        RetInHisDetail pRD = listRetIn.get(i - 1);
                        cRD.setUniqueId(pRD.getUniqueId() + 1);
                    }
                }
            }
            if (delList != null) {
                delList.forEach(detailId -> {
                    try {
                        dao.delete(detailId);
                    } catch (Exception ex) {
                        log.error("Delete RetIn :" + ex.getMessage());
                    }
                });
            }
            retInDao.save(retIn);
            String vouNo = retIn.getVouNo();
            for (RetInHisDetail rd : listRetIn) {
                if (rd.getStock() != null) {
                    if (rd.getRetInKey() != null) {
                        rd.setRetInKey(rd.getRetInKey());
                    } else {
                        retInDetailId = vouNo + '-' + rd.getUniqueId();
                        rd.setRetInKey(new RetInCompoundKey(retInDetailId, vouNo));
                    }
                    dao.save(rd);
                }
            }
        } catch (Exception ex) {
            log.error("saveRetIn : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());

        }

    }

    @Override
    public void delete(String retInId) throws Exception {
        dao.delete(retInId);
    }

    @Override
    public List<RetInHis> search(String fromDate, String toDate, String cusId, String locId, String vouNo, String filterCode) {
        return retInDao.search(fromDate, toDate, cusId, locId, vouNo, filterCode);
    }

    
    @Override
    public RetInHis findById(String id) {
        return retInDao.findById(id);
    }

    @Override
    public RetInHis saveM(RetInHis retIn) {
        return retInDao.save(retIn);
    }

    
}
