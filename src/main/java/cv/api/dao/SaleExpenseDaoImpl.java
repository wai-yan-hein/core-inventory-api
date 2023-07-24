package cv.api.dao;

import cv.api.entity.PurExpense;
import cv.api.entity.PurExpenseKey;
import cv.api.entity.SaleExpense;
import cv.api.entity.SaleExpenseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class SaleExpenseDaoImpl extends AbstractDao<SaleExpenseKey, SaleExpense> implements SaleExpenseDao {
    @Override
    public SaleExpense save(SaleExpense p) {
        saveOrUpdate(p, p.getKey());
        return p;
    }

    @Override
    public List<SaleExpense> search(String vouNo, String compCode) {
        String sql = "select a.*,e.expense_name\n" +
                "from (\n" +
                "select *\n" +
                "from sale_expense\n" +
                "where vou_no ='" + vouNo + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                ")a\n" +
                "join expense e\n" +
                "on a.expense_code = e.expense_code\n" +
                "and a.comp_code = e.comp_code\n" +
                "order by a.unique_id";
        ResultSet rs = getResult(sql);
        List<SaleExpense> list = new ArrayList<>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    SaleExpense e = new SaleExpense();
                    SaleExpenseKey key = new SaleExpenseKey();
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
