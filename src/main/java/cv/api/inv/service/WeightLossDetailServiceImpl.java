package cv.api.inv.service;

import cv.api.inv.dao.WeightLossHisDetailDao;
import cv.api.inv.entity.WeightLossHisDetail;
import cv.api.inv.entity.WeightLossHisDetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WeightLossDetailServiceImpl implements WeightLossDetailService {
    @Autowired
    private WeightLossHisDetailDao dao;

    @Override
    public WeightLossHisDetail save(WeightLossHisDetail wd) {
        return dao.save(wd);
    }

    @Override
    public void delete(WeightLossHisDetailKey key) {
        dao.delete(key);
    }

    @Override
    public List<WeightLossHisDetail> search(String vouNo, String compCode, Integer deptId) {
        return dao.search(vouNo, compCode, deptId);
    }
}
