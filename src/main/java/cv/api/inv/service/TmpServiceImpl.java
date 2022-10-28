package cv.api.inv.service;

import cv.api.inv.dao.TmpDao;
import cv.api.inv.entity.TmpStockIO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class TmpServiceImpl implements TmpService{
    @Autowired
    private TmpDao dao;
    @Override
    public TmpStockIO save(TmpStockIO io) {
        return dao.save(io);
    }

    @Override
    public List<TmpStockIO> getStockIO(String stockCode, String compCode, Integer deptId, Integer macId) {
        return dao.getStockIO(stockCode,compCode,deptId,macId);
    }
}
