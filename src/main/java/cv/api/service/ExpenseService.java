package cv.api.service;

import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;

import java.util.List;

public interface ExpenseService {
    Expense findById(ExpenseKey key);
    Expense save(Expense exp);

    List<Expense> getExpense(String compCode);

    void delete(ExpenseKey key);
}
