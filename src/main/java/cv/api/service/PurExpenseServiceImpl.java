package cv.api.service;

import cv.api.dao.PurExpenseDao;
import cv.api.entity.PurExpense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PurExpenseServiceImpl implements PurExpenseService {
    @Autowired
    private PurExpenseDao dao;

    @Override
    public PurExpense save(PurExpense p) {
        return dao.save(p);
    }

    @Override
    public List<PurExpense> search(String vouNo, String compCode) {
        return dao.search(vouNo,compCode);
    }
}
