package cv.api.dao;

import cv.api.entity.PurExpense;
import cv.api.entity.SaleExpense;

import java.util.List;

public interface SaleExpenseDao {
    SaleExpense save(SaleExpense p);

    List<SaleExpense> search(String vouNo, String compCode);
}
