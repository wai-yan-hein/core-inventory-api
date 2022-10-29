package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.ProcessHisDao;
import cv.api.inv.dao.SeqTableDao;
import cv.api.inv.entity.ProcessHis;
import cv.api.inv.entity.ProcessHisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProcessHisServiceImpl implements ProcessHisService {
    @Autowired
    private ProcessHisDao dao;
    @Autowired
    private SeqTableDao seqDao;

    @Override
    public ProcessHis save(ProcessHis ph) {
        ph.setVouDate(Util1.toDateTime(ph.getVouDate()));
        if (Util1.isNullOrEmpty(ph.getKey().getVouNo())) {
            ph.getKey().setVouNo(getVoucherNo(ph.getMacId(), ph.getKey().getCompCode()));
        }
        return dao.save(ph);
    }

    @Override
    public ProcessHis findById(ProcessHisKey key) {
        return dao.findById(key);
    }

    @Override
    public List<ProcessHis> search(String fromDate, String toDate, String vouNo, String processNo, String remark,
                                   String stockCode, String pt, String locCode, boolean finish, boolean deleted, String compCode, Integer deptId) {
        return dao.search(fromDate, toDate, vouNo, processNo, remark, stockCode, pt, locCode, finish, deleted, compCode, deptId);
    }

    @Override
    public void delete(ProcessHisKey key) {
        dao.delete(key);
    }

    private String getVoucherNo(Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "PROCESS", period, compCode);
        return String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }
}
