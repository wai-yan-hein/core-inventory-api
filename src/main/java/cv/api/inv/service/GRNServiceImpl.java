package cv.api.inv.service;

import cv.api.common.Util1;
import cv.api.inv.dao.GRNDao;
import cv.api.inv.entity.GRN;
import cv.api.inv.entity.GRNKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GRNServiceImpl implements GRNService {
    @Autowired
    private SeqTableService seqTableService;
    @Autowired
    private GRNDao dao;

    @Override
    public GRN findByCode(GRNKey key) {
        return dao.findByCode(key);
    }

    @Override
    public GRN save(GRN b) {
        if (Util1.isNull(b.getKey().getVouNo())) {
            String compCode = b.getKey().getCompCode();
            Integer deptId = b.getKey().getDeptId();
            Integer macId = b.getMacId();
            String vouNo = getVoucherNo(deptId, macId, compCode);
            b.getKey().setVouNo(vouNo);
        }
        return dao.save(b);
    }

    @Override
    public List<GRN> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public void delete(GRNKey key) {
        dao.delete(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqTableService.getSequence(macId, "GRN", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }
}
