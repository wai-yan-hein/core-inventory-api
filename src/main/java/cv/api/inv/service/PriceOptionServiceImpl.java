package cv.api.inv.service;

import cv.api.inv.dao.PriceOptionDao;
import cv.api.inv.entity.PriceOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PriceOptionServiceImpl implements PriceOptionService {
    @Autowired
    private PriceOptionDao dao;

    @Override
    public PriceOption save(PriceOption p) {
        return dao.save(p);
    }

    @Override
    public List<PriceOption> getPriceOption(String option, String compCode, Integer deptId) {
        return dao.getPriceOption(option, compCode, deptId);
    }
}
