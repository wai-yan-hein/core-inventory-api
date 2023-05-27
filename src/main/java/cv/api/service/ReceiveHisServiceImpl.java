package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.ReceiveHisDao;
import cv.api.dao.ReceiveHisDetailDao;
import cv.api.dao.SeqTableDao;
import cv.api.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReceiveHisServiceImpl implement ReceiveHisService {
    @Autowired
    private ReceiveHisDao dao;
    @Autowired
    private ReceiveHisDetailDao detailDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public ReceiveHis save(ReceiveHis obj) {
        obj.setVouDate(Util1.toDateTime(obj.getVouDate()));
        if (Util1.isNullOrEmpty(obj.getKey().getVouNo())) {
            obj.getKey().setVouNo(getVoucherNo(obj.getMacId(), obj.getKey().getCompCode()));
        }
        List<ReceiveHisDetail> listDetail = obj.getListDetail();
        List<ReceiveHisDetailKey> listDel = obj.getListDelete();
        String vouNo = obj.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(key -> detailDao.delete(key));
        }
        List<PurExpense> listExp = obj.getListExpense();
        if (listExp != null) {
            for (int i = 0; i < listExp.size(); i++) {
                PurExpense e = listExp.get(i);
                if (Util1.getFloat(e.getAmount()) > 0) {
                    if (e.getKey().getExpenseCode() != null) {
                        if (e.getKey().getUniqueId() == null) {
                            if (i == 0) {
                                e.getKey().setUniqueId(1);
                            } else {
                                PurExpense pe = listExp.get(i - 1);
                                e.getKey().setUniqueId(pe.getKey().getUniqueId() + 1);
                            }
                        }
                        e.getKey().setVouNo(vouNo);
                        purExpenseDao.save(e);
                    }
                }
            }
        }
        for (int i = 0; i < listSD.size(); i++) {
            PurHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                PurDetailKey key = new PurDetailKey();
                key.setDeptId(obj.getKey().getDeptId());
                key.setCompCode(obj.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        PurHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                pdDao.save(cSd);

            }
            phDao.save(obj);
            obj.setListPD(listSD);
        }
        return obj;
    }

    @Override
    public ReceiveHis find(ReceiveHisKey key) {
        return dao.find(key);
    }

    @Override
    public void delete(ReceiveHisKey key) {
        dao.delete(key);
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "RECEIVE", period, compCode);
        return "R-" + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }
}
