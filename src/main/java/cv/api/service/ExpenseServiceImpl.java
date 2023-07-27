package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.ExpenseDao;
import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseDao dao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public Expense findById(ExpenseKey key) {
        return dao.findById(key);
    }

    @Override
    public Expense save(Expense exp) {
        if (Util1.isNullOrEmpty(exp.getKey().getExpenseCode())) {
            exp.getKey().setExpenseCode(getCode(exp.getKey().getCompCode()));
        }
        return dao.save(exp);
    }

    @Override
    public List<Expense> getExpense(String compCode) {
        return dao.getExpense(compCode);
    }

    @Override
    public void delete(ExpenseKey key) {
        dao.delete(key);
    }

    private String getCode(String compCode) {
        int seqNo = seqService.getSequence(0, "Expense", "-", compCode);
        return String.format("%0" + 3 + "d", seqNo);
    }
}
