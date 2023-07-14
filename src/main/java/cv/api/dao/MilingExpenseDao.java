package cv.api.dao;

import cv.api.entity.*;

import java.util.List;

public interface MilingExpenseDao {
    MilingExpense findById(MilingExpenseKey key);
    MilingExpense save(MilingExpense p);
    List<MilingExpense> search(String vouNo, String compCode);
    List<MilingExpense> getExpense(String compCode);
    void delete(MilingExpenseKey key);
}
