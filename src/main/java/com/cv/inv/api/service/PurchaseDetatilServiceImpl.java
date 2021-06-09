/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.AccSettingDao;
import com.cv.inv.api.dao.PurchaseDetailDao;
import com.cv.inv.api.entity.PurDetailKey;
import com.cv.inv.api.entity.PurHis;
import com.cv.inv.api.entity.PurHisDetail;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Service
@Transactional
public class PurchaseDetatilServiceImpl implements PurchaseDetailService {

    private static final Logger logger = LoggerFactory.getLogger(RetInServiceImpl.class);
    private final String DELETE_OPTION = "INV_DELETE";
    private final String SOURCE_PROG = "ACCOUNT";
    @Autowired
    private AccSettingDao settingDao;

    @Autowired
    private PurchaseHisService purService;
    @Autowired
    private PurchaseDetailDao dao;

    @Override
    public PurHisDetail save(PurHisDetail pd) {

        return dao.save(pd);
    }

    @Override
    public List<PurHisDetail> search(String glCode) {
        return dao.search(glCode);
    }

    @Override
    public void saveH2(PurHis pur, List<PurHisDetail> listPD, List<String> delList) {
        try {
            String retInDetailId;
            for (int i = 0; i < listPD.size(); i++) {
                PurHisDetail cPD = listPD.get(i);
                if (cPD.getUniqueId() == null) {
                    if (i == 0) {
                        cPD.setUniqueId(1);
                    } else {
                        PurHisDetail pSd = listPD.get(i - 1);
                        cPD.setUniqueId(pSd.getUniqueId() + 1);
                    }
                }
            }
            if (delList != null) {
                delList.forEach(detailId -> {
                    try {
                        dao.delete(detailId);
                    } catch (Exception ex) {
                        logger.error("delete purchase detail :" + ex.getMessage());
                    }
                });
            }
            purService.save(pur);
            String vouNo = pur.getVouNo();
            for (PurHisDetail pd : listPD) {
                if (pd.getStock() != null) {
                    if (pd.getPurDetailKey() != null) {
                        pd.setPurDetailKey(pd.getPurDetailKey());
                    } else {
                        retInDetailId = vouNo + '-' + pd.getUniqueId();
                        pd.setPurDetailKey(new PurDetailKey(vouNo, retInDetailId));
                    }
                    //  pd.setLocation(pur.getLocationId());
                    dao.save(pd);
                }
            }
        } catch (Exception ex) {
            logger.error("Save Purchase Detail :" + ex.getMessage());
        }

    }

    @Override
    public void save(PurHis pur, List<PurHisDetail> listPD, List<String> delList) {
        try {
            String retInDetailId;
            for (int i = 0; i < listPD.size(); i++) {
                PurHisDetail cPD = listPD.get(i);
                if (cPD.getUniqueId() == null) {
                    if (i == 0) {
                        cPD.setUniqueId(1);
                    } else {
                        PurHisDetail pSd = listPD.get(i - 1);
                        cPD.setUniqueId(pSd.getUniqueId() + 1);
                    }
                }
            }
            if (delList != null) {
                delList.forEach(detailId -> {
                    try {
                        delete(detailId);
                    } catch (Exception ex) {
                        logger.error("delete purchase detail :" + ex.getMessage());
                    }
                });
            }
            purService.save(pur);
            String vouNo = pur.getVouNo();
            for (PurHisDetail pd : listPD) {
                if (pd.getStock() != null) {
                    if (pd.getPurDetailKey() != null) {
                        pd.setPurDetailKey(pd.getPurDetailKey());
                    } else {
                        retInDetailId = vouNo + '-' + pd.getUniqueId();
                        pd.setPurDetailKey(new PurDetailKey(vouNo, retInDetailId));
                    }
                    //  pd.setLocation(pur.getLocationId());
                    dao.save(pd);
                }
            }
        } catch (Exception ex) {
            logger.error("Save Purchase :" + ex.getMessage());
        }
    }

    @Override
    public int delete(String code) throws Exception {
        return dao.delete(code);

    }
}
