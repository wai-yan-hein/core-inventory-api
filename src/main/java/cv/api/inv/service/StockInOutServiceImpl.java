/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.dao.StockInOutDao;
import cv.api.inv.dao.StockInOutDetailDao;
import cv.api.inv.entity.StockIOKey;
import cv.api.inv.entity.StockInOut;
import cv.api.inv.entity.StockInOutDetail;
import cv.api.inv.entity.StockInOutKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author wai yan
 */
@Service
@Transactional
@Slf4j
public class StockInOutServiceImpl implements StockInOutService {

    @Autowired
    private StockInOutDao ioDao;
    @Autowired
    private StockInOutDetailDao iodDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public StockInOut save(StockInOut io) throws Exception {
        io.setVouDate(Util1.toDateTime(io.getVouDate()));
        if (Util1.isNullOrEmpty(io.getKey().getVouNo())) {
            io.getKey().setVouNo(getVoucherNo(io.getMacId(), io.getKey().getCompCode()));
        }
        if (Util1.getBoolean(io.getDeleted())) {
            ioDao.save(io);
        } else {
            List<StockInOutDetail> listSD = io.getListSH();
            List<String> listDel = io.getListDel();
            String vouNo = io.getKey().getVouNo();
            if (listDel != null) {
                listDel.forEach(detailId -> {
                    if (detailId != null) {
                        iodDao.delete(detailId);
                    }
                });
            }
            for (int i = 0; i < listSD.size(); i++) {
                StockInOutDetail cSd = listSD.get(i);
                if (cSd.getStockCode() != null) {
                    if (cSd.getUniqueId() == null) {
                        if (i == 0) {
                            cSd.setUniqueId(1);
                        } else {
                            StockInOutDetail pSd = listSD.get(i - 1);
                            cSd.setUniqueId(pSd.getUniqueId() + 1);
                        }
                    }
                    String sdCode = vouNo + "-" + cSd.getUniqueId();
                    cSd.setIoKey(new StockInOutKey(sdCode, vouNo, io.getKey().getDeptId()));
                    cSd.setCompCode(io.getKey().getCompCode());
                    iodDao.save(cSd);
                }
            }
            ioDao.save(io);
            io.setListSH(listSD);
        }
        return io;
    }

    @Override
    public List<StockInOut> search(String fromDate, String toDate, String remark, String desp, String vouNo, String userCode, String vouStatus) {
        return ioDao.search(fromDate, toDate, remark, desp, vouNo, userCode, vouStatus);
    }

    @Override
    public StockInOut findById(StockIOKey id) {
        return ioDao.findById(id);
    }

    @Override
    public int delete(String vouNo) throws Exception {
        return ioDao.delete(vouNo);
    }

    @Override
    public List<StockInOut> unUpload() {
        return ioDao.unUpload();
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "STOCKIO", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

}
