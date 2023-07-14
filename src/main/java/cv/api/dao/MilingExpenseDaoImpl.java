package cv.api.dao;

import cv.api.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class MilingExpenseDaoImpl extends AbstractDao<MilingExpenseKey, MilingExpense> implements MilingExpenseDao {
    @Override
    public MilingExpense findById(MilingExpenseKey key) {
        return getByKey(key);
    }

    @Override
    public MilingExpense save(MilingExpense exp) {
        saveOrUpdate(exp,exp.getKey());
        return exp;
    }

    @Override
    public List<MilingExpense> getExpense(String compCode) {
        String hsql = "select o from miling_expense o where o.key.compCode ='" + compCode + "' and o.deleted = false";
        return findHSQL(hsql);
    }

    @Override
    public void delete(MilingExpenseKey key) {
        String sql = "update miling_expense set deleted = true where expense_code ='" + key.getExpenseCode() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
    }
    @Override
    public List<MilingExpense> search(String vouNo, String compCode) {
        String sql = "select a.*,e.expense_name\n" +
                "from (\n" +
                "select *\n" +
                "from miling_expense\n" +
                "where vou_no ='" + vouNo + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                ")a\n" +
                "join expense e\n" +
                "on a.expense_code = e.expense_code\n" +
                "and a.comp_code = e.comp_code\n" +
                "order by a.unique_id";
        ResultSet rs = getResult(sql);
        List<MilingExpense> list = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    MilingExpense e = new MilingExpense();
                    MilingExpenseKey key = new MilingExpenseKey();
                    key.setExpenseCode(rs.getString("expense_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    e.setKey(key);
                    e.setExpenseName(rs.getString("expense_name"));
                    e.setAmount(rs.getFloat("amount"));
                    list.add(e);
                }
            } catch (Exception e) {
                log.error("search : " + e.getMessage());
            }
        }
        return list;
    }
}
