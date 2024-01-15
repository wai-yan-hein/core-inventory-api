package cv.api.service;
import cv.api.entity.*;
import cv.api.common.Util1;
import cv.api.dao.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ConsignServiceImpl implements ConsignService {
    private final ConsignDao dao;
    public final ConsignDetailDao detailDao;
    private final SeqTableDao seqDao;


    @Override
    public ConsignHis save(ConsignHis obj) {
        obj.setVouDate(Util1.toDateTime(obj.getVouDate()));
        if (Util1.isNullOrEmpty(obj.getKey().getVouNo())) {
            obj.getKey().setVouNo(getVoucherNo(obj.getDeptId(), obj.getMacId(), obj.getKey().getCompCode()));
        }
        String vouNo = obj.getKey().getVouNo();
        String compCode = obj.getKey().getCompCode();
        //delete detail
        boolean delete = detailDao.deleteStockIssRecDetail(vouNo, compCode);
        if (delete) {
            List<ConsignHisDetail> listDetail = obj.getListIRDetail();
            for (int i = 0; i < listDetail.size(); i++) {
                ConsignHisDetail cSd = listDetail.get(i);
                if (cSd.getStockCode() != null) {
                    if (Util1.isNullOrEmpty(cSd.getKey())) {
                        ConsignHisDetailKey key = new ConsignHisDetailKey();
                        key.setCompCode(obj.getKey().getCompCode());
                        key.setVouNo(vouNo);
                        key.setUniqueId(0);
                        cSd.setDeptId(obj.getDeptId());
                        cSd.setLocCode(obj.getLocCode());
                        cSd.setKey(key);
                    }
                    double weight = cSd.getWeight();
                    if (weight > 0) {
                        if (cSd.getKey().getUniqueId() == 0) {
                            if (i == 0) {
                                cSd.getKey().setUniqueId(1);
                            } else {
                                ConsignHisDetail pSd = listDetail.get(i - 1);
                                cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                            }
                        }
                    }
                    detailDao.save(cSd);
                }
            }
            dao.save(obj);
        }
        return obj;
    }


    @Override
    public ConsignHis findById(ConsignHisKey key) {
        return dao.findById(key);
    }

    @Override
    public boolean delete(ConsignHisKey key) {
        return dao.delete(key);
    }

    @Override
    public boolean restore(ConsignHisKey key) {
        return dao.restore(key);
    }

    @Override
    public ConsignHisDetail save(ConsignHisDetail obj) {
        return detailDao.save(obj);
    }

    @Override
    public boolean delete(ConsignHisDetailKey key) {
        return detailDao.delete(key);
    }
    @Override
    public List<ConsignHisDetail> getStockIssRecDetail(String vouNo, String compCode) {
        return detailDao.getStockIssRecDetail(vouNo, compCode);
    }
    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "ISSREC", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}
