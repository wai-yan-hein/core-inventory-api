package cv.api.dao;

import cv.api.entity.SaleExpense;
import cv.api.entity.SaleExpenseKey;

import java.util.List;

public interface SaleExpenseDao {
    SaleExpense save(SaleExpense p);

    List<SaleExpense> search(String vouNo, String compCode);
    void delete(SaleExpenseKey key);
}
