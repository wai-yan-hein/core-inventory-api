/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.dao.SaleHisDao;
import cv.api.inv.dao.SaleHisDetailDao;
import cv.api.inv.entity.SaleDetailKey;
import cv.api.inv.entity.SaleHis;
import cv.api.inv.entity.SaleHisDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
public class SaleDetailServiceImpl implements SaleDetailService {

    @Autowired
    private SaleHisDetailDao dao;

    @Autowired
    private SaleHisDao hisDao;

    @Override
    public SaleHisDetail save(SaleHisDetail sdh) {
        return dao.save(sdh);
    }

    @Override
    public List<SaleHisDetail> search(String vouNo) {
        return dao.search(vouNo);
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
        String vouNo = saleHis.getKey().getVouNo();
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
                       String vouStatus, List<String> deleteList) {
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
        String vouNo = saleHis.getKey().getVouNo();
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

    @Override
    public int delete(String code) {
        return dao.delete(code);
    }
}
