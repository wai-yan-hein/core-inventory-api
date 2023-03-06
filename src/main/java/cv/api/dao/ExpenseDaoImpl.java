package cv.api.dao;

import cv.api.entity.Expense;
import cv.api.entity.ExpenseKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseDaoImpl extends AbstractDao<ExpenseKey, Expense> implements ExpenseDao {
    @Override
    public Expense findById(ExpenseKey key) {
        return getByKey(key);
    }

    @Override
    public Expense save(Expense exp) {
        persist(exp);
        return exp;
    }

    @Override
    public List<Expense> getExpense(String compCode) {
        String hsql = "select o from Expense o where o.key.compCode ='" + compCode + "' and o.deleted = 0";
        return findHSQL(hsql);
    }

    @Override
    public void delete(ExpenseKey key) {
        String sql = "update expense set deleted = 1 where expense_code ='" + key.getExpenseCode() + "' and comp_code ='" + key.getCompCode() + "'";
        execSQL(sql);
    }
}
