/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.PurHisDetailDao;
import cv.api.inv.entity.PurDetailKey;
import cv.api.inv.entity.PurHis;
import cv.api.inv.entity.PurHisDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Service
@Transactional
public class PurHisDetailServiceImpl implements PurHisDetailService {
 
    @Autowired
    private PurHisService purService;
    @Autowired
    private PurHisDetailDao dao;

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
                        log.error("delete purchase detail :" + ex.getMessage());
                    }
                });
            }
            purService.save(pur);
            String vouNo = pur.getVouNo();
            for (PurHisDetail pd : listPD) {
                if (pd.getStock() != null) {
                    if (pd.getPdKey() != null) {
                        pd.setPdKey(pd.getPdKey());
                    } else {
                        retInDetailId = vouNo + '-' + pd.getUniqueId();
                        pd.setPdKey(new PurDetailKey(vouNo, retInDetailId));
                    }
                    //  pd.setLocation(pur.getLocationId());
                    dao.save(pd);
                }
            }
        } catch (Exception ex) {
            log.error("Save Purchase Detail :" + ex.getMessage());
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
                        log.error("delete purchase detail :" + ex.getMessage());
                    }
                });
            }
            purService.save(pur);
            String vouNo = pur.getVouNo();
            for (PurHisDetail pd : listPD) {
                if (pd.getStock() != null) {
                    if (pd.getPdKey() != null) {
                        pd.setPdKey(pd.getPdKey());
                    } else {
                        retInDetailId = vouNo + '-' + pd.getUniqueId();
                        pd.setPdKey(new PurDetailKey(vouNo, retInDetailId));
                    }
                    //  pd.setLocation(pur.getLocationId());
                    dao.save(pd);
                }
            }
        } catch (Exception ex) {
            log.error("Save Purchase :" + ex.getMessage());
        }
    }

    @Override
    public int delete(String code) throws Exception {
        return dao.delete(code);

    }
}
