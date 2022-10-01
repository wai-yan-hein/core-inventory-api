package cv.api.inv.service;

import cv.api.inv.dao.PatternDao;
import cv.api.inv.entity.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatternServiceImpl implements PatternService {
    @Autowired
    private PatternDao dao;


    @Override
    public Pattern findByCode(String code) {
        return dao.findByCode(code);
    }

    @Override
    public Pattern save(Pattern p) {
        return dao.save(p);
    }


    @Override
    public List<Pattern> search(String stockCode) {
        return dao.search(stockCode);
    }

    @Override
    public void delete(String stockCode) {
        dao.delete(stockCode);
    }
}
