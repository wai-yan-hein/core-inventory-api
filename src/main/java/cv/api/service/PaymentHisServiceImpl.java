package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.PaymentHisDao;
import cv.api.dao.PaymentHisDetailDao;
import cv.api.dao.SeqTableDao;
import cv.api.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class PaymentHisServiceImpl implements PaymentHisService {
    @Autowired
    private PaymentHisDao dao;
    @Autowired
    private PaymentHisDetailDao detailDao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public PaymentHis save(PaymentHis obj) {
        obj.setVouDate(Util1.toDateTime(obj.getVouDate()));
        if (Util1.isNullOrEmpty(obj.getKey().getVouNo())) {
            obj.getKey().setVouNo(getVoucherNo(obj.getMacId(), obj.getKey().getCompCode(), obj.getDeptId(), obj.getTranOption()));
        }
        List<PaymentHisDetail> listDetail = obj.getListDetail();
        List<PaymentHisDetailKey> listDel = obj.getListDelete();
        String vouNo = obj.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(key -> detailDao.delete(key));
        }
        for (int i = 0; i < listDetail.size(); i++) {
            PaymentHisDetail cSd = listDetail.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                PaymentHisDetailKey key = new PaymentHisDetailKey();
                key.setCompCode(obj.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(null);
                key.setDeptId(obj.getDeptId());
                cSd.setKey(key);
            }
            if (Util1.getFloat(cSd.getPayAmt()) > 0) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        PaymentHisDetail pSd = listDetail.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                detailDao.save(cSd);

            }
            dao.save(obj);
            obj.setListDetail(listDetail);
        }
        return obj;
    }

    @Override
    public PaymentHis find(PaymentHisKey key) {
        return dao.find(key);
    }

    @Override
    public void delete(PaymentHisKey key) {
        dao.delete(key);
    }

    @Override
    public void restore(PaymentHisKey key) {
        dao.restore(key);
    }

    @Override
    public List<PaymentHis> search(String startDate, String endDate, String traderCode, String curCode, String vouNo,
                                   String saleVouNo, String userCode, String account, String projectNo, String remark,
                                   boolean deleted, String compCode,String tranOption) {
        return dao.search(startDate, endDate, traderCode, curCode, vouNo, saleVouNo, userCode, account,
                projectNo, remark, deleted, compCode,tranOption);
    }

    @Override
    public List<PaymentHis> unUploadVoucher(LocalDateTime syncDate) {
        return dao.unUploadVoucher(syncDate);
    }

    private String getVoucherNo(Integer macId, String compCode, Integer deptId, String tranOption) {
        String option = tranOption.equals("C") ? "RECEIVE" : "PAYMENT";
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, option, period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return tranOption + "-" + deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }
}
