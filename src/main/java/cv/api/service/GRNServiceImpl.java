package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.GRNDao;
import cv.api.dao.GRNDetailDao;
import cv.api.entity.GRN;
import cv.api.entity.GRNDetail;
import cv.api.entity.GRNDetailKey;
import cv.api.entity.GRNKey;
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
    @Autowired
    private GRNDetailDao gdDao;

    @Override
    public GRN findByCode(GRNKey key) {
        return dao.findByCode(key);
    }

    @Override
    public GRN save(GRN g) {
        g.setVouDate(Util1.toDateTime(g.getVouDate()));
        if (Util1.isNullOrEmpty(g.getKey().getVouNo())) {
            g.getKey().setVouNo(getVoucherNo(g.getKey().getDeptId(), g.getMacId(), g.getKey().getCompCode()));
        }
        List<GRNDetail> listDetail = g.getListDetail();
        List<GRNDetailKey> listDel = g.getListDel();
        //backup
        if (listDel != null) {
            listDel.forEach(key -> gdDao.delete(key));
        }
        for (int i = 0; i < listDetail.size(); i++) {
            GRNDetail cSd = listDetail.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                GRNDetailKey key = new GRNDetailKey();
                key.setDeptId(g.getKey().getDeptId());
                key.setCompCode(g.getKey().getCompCode());
                key.setVouNo(g.getKey().getVouNo());
                key.setUniqueId(null);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == null) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        GRNDetail pSd = listDetail.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                gdDao.save(cSd);

            }
        }
        g.setListDetail(listDetail);
        dao.save(g);
        return g;
    }

    @Override
    public List<GRN> findAll(String compCode, Integer deptId) {
        return dao.findAll(compCode, deptId);
    }

    @Override
    public List<GRN> search(String batchNo, String compCode, Integer deptId) {
        return dao.search(batchNo, compCode, deptId);
    }

    @Override
    public boolean delete(GRNKey key) {
        return dao.delete(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqTableService.getSequence(macId, "GRN", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }
}
