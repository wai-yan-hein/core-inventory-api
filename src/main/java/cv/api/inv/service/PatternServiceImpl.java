package cv.api.inv.service;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.inv.dao.PatternDao;
import cv.api.inv.entity.Pattern;
import cv.api.inv.entity.PatternDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatternServiceImpl implements PatternService {
    @Autowired
    private PatternDao dao;
    @Autowired
    private SeqTableService seqService;
    @Autowired
    private ReportService reportService;

    @Override
    public Pattern findByCode(String code) {
        return dao.findByCode(code);
    }

    @Override
    public Pattern save(Pattern p) {
        if (Util1.isNull(p.getPatternCode())) {
            Integer macId = p.getMacId();
            String compCode = p.getCompCode();
            String catCode = getPatterCode(macId, compCode);
            Pattern valid = findByCode(catCode);
            if (valid == null) {
                p.setPatternCode(catCode);
            } else {
                throw new IllegalStateException("Duplicate Pattern Code");
            }
        }
        return dao.save(p);
    }

    @Override
    public PatternDetail save(PatternDetail pd) {
        pd.setPtCode(String.format("%s-%s", pd.getPatternCode(), pd.getUniqueId()));
        return dao.save(pd);
    }

    @Override
    public List<Pattern> search(String compCode, Boolean active) {
        return dao.search(compCode, active);
    }

    @Override
    public List<PatternDetail> searchDetail(String code) throws Exception {
        List<PatternDetail> listPD = dao.searchDetail(code);
        if (!listPD.isEmpty()) {
            for (PatternDetail pd : listPD) {
                General g = reportService.getPurchaseAvgPrice(pd.getStock().getStockCode());
                pd.setCostPrice(g.getAmount());
            }
        }
        return listPD;
    }

    private String getPatterCode(Integer macId, String compCode) {
        int seqNo = seqService.getSequence(macId, "Pattern", "-", compCode);
        return String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 4 + "d", seqNo);
    }
}
