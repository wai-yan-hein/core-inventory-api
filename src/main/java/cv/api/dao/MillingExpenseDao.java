package cv.api.dao;

import cv.api.entity.MillingExpense;
import cv.api.entity.MillingExpenseKey;

import java.util.List;

public interface MillingExpenseDao {
    MillingExpense save(MillingExpense p);

    List<MillingExpense> search(String vouNo, String compCode);

    void delete(MillingExpenseKey key);
}
