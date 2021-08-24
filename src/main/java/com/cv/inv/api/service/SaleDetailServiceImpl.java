/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.SaleDetailDao;
import com.cv.inv.api.dao.SaleHisDao;
import com.cv.inv.api.entity.SaleDetailKey;
import com.cv.inv.api.entity.SaleHis;
import com.cv.inv.api.entity.SaleHisDetail;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Service
@Transactional
public class SaleDetailServiceImpl implements SaleDetailService {

    @Autowired
    private SaleDetailDao dao;

    @Autowired
    private SaleHisDao hisDao;

    @Override
    public SaleHisDetail save(SaleHisDetail sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<SaleHisDetail> search(String vouId) {
        return dao.search(vouId);
    }

    @Override
    public void save(SaleHis saleHis, List<SaleHisDetail> listSaleDetail,
            String vouStatus, List<String> deleteList) throws Exception {
        String retInDetailId;
        //serialize unique id
        for (int i = 0; i < listSaleDetail.size(); i++) {
            SaleHisDetail cSd = listSaleDetail.get(i);
            if (cSd.getUniqueId() == null) {
                if (i == 0) {
                    cSd.setUniqueId(1);
                } else {
                    SaleHisDetail pSd = listSaleDetail.get(i - 1);
                    cSd.setUniqueId(pSd.getUniqueId() + 1);
                }
            }
        }
        if (vouStatus.equals("EDIT")) {

            if (deleteList != null) {
                deleteList.forEach(detailId -> {
                    if (detailId != null) {
                        dao.delete(detailId);
                    }
                });
            }
        }
        hisDao.save(saleHis);
        String vouNo = saleHis.getVouNo();
        for (SaleHisDetail sd : listSaleDetail) {
            if (sd.getStock() != null) {
                if (sd.getSdKey()!= null) {
                    sd.setSdKey(sd.getSdKey());
                } else {
                    retInDetailId = vouNo + '-' + sd.getUniqueId();
                    sd.setSdKey(new SaleDetailKey(vouNo, retInDetailId));
                }
                //  pd.setLocation(pur.getLocationId());
                dao.save(sd);
            }
        }
        //save to account 
        //saveGl(saleHis);
    }

    @Override
    public void saveH2(SaleHis saleHis, List<SaleHisDetail> listSaleDetail,
            String vouStatus, List<String> deleteList) throws Exception {
        String retInDetailId;
        //serialize unique id
        for (int i = 0; i < listSaleDetail.size(); i++) {
            SaleHisDetail cSd = listSaleDetail.get(i);
            if (cSd.getUniqueId() == null) {
                if (i == 0) {
                    cSd.setUniqueId(1);
                } else {
                    SaleHisDetail pSd = listSaleDetail.get(i - 1);
                    cSd.setUniqueId(pSd.getUniqueId() + 1);
                }
            }
        }
        if (vouStatus.equals("EDIT")) {

            if (deleteList != null) {
                deleteList.forEach(detailId -> {
                    if (detailId != null) {
                        dao.delete(detailId);
                    }
                });
            }
        }
        hisDao.save(saleHis);
        String vouNo = saleHis.getVouNo();
        for (SaleHisDetail sd : listSaleDetail) {
            if (sd.getStock() != null) {
                if (sd.getSdKey()!= null) {
                    sd.setSdKey(sd.getSdKey());
                } else {
                    retInDetailId = vouNo + '-' + sd.getUniqueId();
                    sd.setSdKey(new SaleDetailKey(vouNo, retInDetailId));
                }
                //  pd.setLocation(pur.getLocationId());
                dao.save(sd);
            }
        }

    }
}
