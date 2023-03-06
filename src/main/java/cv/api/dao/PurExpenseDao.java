package cv.api.dao;

import cv.api.entity.PurExpense;

import java.util.List;

public interface PurExpenseDao {
    PurExpense save(PurExpense p);

    List<PurExpense> search(String vouNo, String compCode);
}
