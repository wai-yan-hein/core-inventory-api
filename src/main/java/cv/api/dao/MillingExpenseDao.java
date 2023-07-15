package cv.api.dao;

import cv.api.entity.*;

import java.util.List;

public interface MillingExpenseDao {
    MillingExpense findById(MillingExpenseKey key);
    MillingExpense save(MillingExpense p);
    List<MillingExpense> search(String vouNo, String compCode);
    List<MillingExpense> getExpense(String compCode);
    void delete(MillingExpenseKey key);
}
