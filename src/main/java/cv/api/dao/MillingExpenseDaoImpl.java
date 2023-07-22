package cv.api.dao;

import cv.api.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class MillingExpenseDaoImpl extends AbstractDao<MillingExpenseKey, MillingExpense> implements MillingExpenseDao {
    @Override
    public MillingExpense save(MillingExpense p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    @Override
    public List<MillingExpense> search(String vouNo, String compCode) {
        String sql = "select a.*,e.expense_name\n" +
                "from (\n" +
                "select *\n" +
                "from milling_expense\n" +
                "where vou_no ='" + vouNo + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                ")a\n" +
                "join expense e\n" +
                "on a.expense_code = e.expense_code\n" +
                "and a.comp_code = e.comp_code\n" +
                "order by a.unique_id";
        ResultSet rs = getResult(sql);
        List<MillingExpense> list = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    MillingExpense e = new MillingExpense();
                    MillingExpenseKey key = new MillingExpenseKey();
                    key.setExpenseCode(rs.getString("expense_code"));
                    key.setCompCode(rs.getString("comp_code"));
                    key.setUniqueId(rs.getInt("unique_id"));
                    key.setVouNo(rs.getString("vou_no"));
                    e.setKey(key);
                    e.setExpenseName(rs.getString("expense_name"));
                    e.setAmount(rs.getFloat("amount"));
//                    e.setPercent(rs.getFloat("percent"));
                    list.add(e);
                }
            } catch (Exception e) {
                log.error("search : " + e.getMessage());
            }
        }
        return list;
    }

    @Override
    public void delete(MillingExpenseKey key) {
        String sql = "update milling_expense set deleted = true where expense_code ='" + key.getExpenseCode() + "' and comp_code ='" + key.getCompCode() + "'";
        execSql(sql);
    }
}
