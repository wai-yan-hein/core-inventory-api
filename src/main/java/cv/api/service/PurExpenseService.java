package cv.api.service;

import cv.api.entity.PurExpense;

import java.util.List;

public interface PurExpenseService {
    PurExpense save(PurExpense p);

    List<PurExpense> search(String vouNo, String compCode);
}
