package cv.api.dao;

import cv.api.entity.*;

import java.util.List;

public interface MillingExpenseDao {
    MillingExpense save(MillingExpense p);

    List<MillingExpense> search(String vouNo, String compCode);

    void delete(MillingExpenseKey key);
}
