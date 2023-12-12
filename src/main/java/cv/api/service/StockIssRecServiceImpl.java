package cv.api.service;
import cv.api.entity.*;
import cv.api.common.Util1;
import cv.api.dao.*;
import cv.api.model.VStockIssueReceive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockIssRecServiceImpl implements StockIssRecService {
    private final StockIssRecDao dao;
    public final StockIssRecDetailDao detailDao;
    private final SeqTableDao seqDao;


    @Override
    public StockIssueReceive save(StockIssueReceive obj) {
        obj.setVouDate(Util1.toDateTime(obj.getVouDate()));
        if (Util1.isNullOrEmpty(obj.getKey().getVouNo())) {
            obj.getKey().setVouNo(getVoucherNo(obj.getDeptId(), obj.getMacId(), obj.getKey().getCompCode()));
        }
        String vouNo = obj.getKey().getVouNo();
        String compCode = obj.getKey().getCompCode();
        //delete detail
        boolean delete = detailDao.deleteStockIssRecDetail(vouNo, compCode);
        if (delete) {
            List<StockIssRecDetail> listDetail = obj.getListIRDetail();
            for (int i = 0; i < listDetail.size(); i++) {
                StockIssRecDetail cSd = listDetail.get(i);
                if (cSd.getStockCode() != null) {
                    if (Util1.isNullOrEmpty(cSd.getKey())) {
                        StockIssRecDetailKey key = new StockIssRecDetailKey();
                        key.setCompCode(obj.getKey().getCompCode());
                        key.setVouNo(vouNo);
                        key.setUniqueId(0);
                        cSd.setDeptId(obj.getDeptId());
                        cSd.setLocCode(obj.getLocation());
                        cSd.setKey(key);
                    }
                    double weight = cSd.getWeight();
                    if (weight > 0) {
                        if (cSd.getKey().getUniqueId() == 0) {
                            if (i == 0) {
                                cSd.getKey().setUniqueId(1);
                            } else {
                                StockIssRecDetail pSd = listDetail.get(i - 1);
                                cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                            }
                        }
                    }
                    detailDao.save(cSd);
                }

//                obj.setListIRDetail(listDetail);
            }
            dao.save(obj);
        }
        return obj;
    }


    @Override
    public StockIssueReceive findById(StockIssueReceiveKey key) {
        return dao.findById(key);
    }

    @Override
    public boolean delete(StockIssueReceiveKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean restore(StockIssueReceiveKey key) {
        return dao.restore(key);
    }

    @Override
    public StockIssRecDetail save(StockIssRecDetail obj) {
        return detailDao.save(obj);
    }

    @Override
    public boolean delete(StockIssRecDetailKey key) {
        return detailDao.delete(key);
    }
    @Override
    public List<StockIssRecDetail> getStockIssRecDetail(String vouNo, String compCode) {
        return detailDao.getStockIssRecDetail(vouNo, compCode);
    }
    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "ISSREC", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
