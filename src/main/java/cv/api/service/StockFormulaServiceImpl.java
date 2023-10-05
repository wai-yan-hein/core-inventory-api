package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.StockFormulaDao;
import cv.api.dao.StockFormulaDetailDao;
import cv.api.entity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StockFormulaServiceImpl implements StockFormulaService {
    private final StockFormulaDao formulaDao;
    private final StockFormulaDetailDao formulaDetailDao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public StockFormula save(StockFormula s) {
        if (Util1.isNullOrEmpty(s.getKey().getCode())) {
            s.getKey().setCode(getCode(s.getKey().getCompCode()));
            s.setCreatedDate(Util1.getTodayLocalDate());
        } else {
            s.setUpdatedDate(Util1.getTodayLocalDate());
        }
        List<StockFormulaDetail> listSD = s.getListDtl();
        String vouNo = s.getKey().getCode();
        for (int i = 0; i < listSD.size(); i++) {
            StockFormulaDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                StockFormulaDetailKey key = new StockFormulaDetailKey();
                key.setCompCode(s.getKey().getCompCode());
                key.setCode(vouNo);
                key.setUniqueId(0);
                cSd.setKey(key);
            }
            if (cSd.getKey().getCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        StockFormulaDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                formulaDetailDao.save(cSd);
            }
        }
        formulaDao.save(s);
        s.setListDtl(listSD);
        return s;
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "StockFormula", "-", compCode);
        return String.format("%0" + 3 + "d", seqNo);
    }

    @Override
    public boolean delete(StockFormulaKey key) {
        return formulaDao.delete(key);
    }

    @Override
    public List<StockFormula> getFormula(String compCode) {
        return formulaDao.getFormula(compCode);
    }

    @Override
    public StockFormulaDetail save(StockFormulaDetail s) {
        return formulaDetailDao.save(s);
    }

    @Override
    public boolean delete(StockFormulaDetailKey key) {
        return formulaDetailDao.delete(key);
    }

    @Override
    public List<StockFormulaDetail> getFormulaDetail(String code, String compCode) {
        return formulaDetailDao.getFormulaDetail(code, compCode);
    }
}
