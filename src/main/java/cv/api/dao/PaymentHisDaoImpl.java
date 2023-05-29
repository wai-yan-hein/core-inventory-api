package cv.api.dao;

import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class PaymentHisDaoImpl extends AbstractDao<PaymentHisKey, PaymentHis> implements PaymentHisDao {
    @Override
    public PaymentHis save(PaymentHis obj) {
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public PaymentHis find(PaymentHisKey key) {
        return getByKey(key);
    }

    @Override
    public void delete(PaymentHisKey key) {
        String sql = "update receive_his set deleted =1 where comp_code ='" + key.getCompCode() + "' and vou_no='" + key.getVouNo() + "'";
        execSql(sql);
    }

    @Override
    public List<PaymentHis> search(String startDate, String endDate, String traderCode, String curCode,
                                   String vouNo, String userCode, String account,
                                   String projectNo, String remark, boolean deleted, String compCode) {
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code ='" + traderCode + "'";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no ='" + projectNo + "'";
        }
        if (!vouNo.equals("-")) {
            filter += "and vou_no ='" + vouNo + "'";
        }
        if (!account.equals("-")) {
            filter += "and account ='" + account + "'";
        }
        if (!userCode.equals("-")) {
            filter += "and created_by ='" + userCode + "'";
        }
        if (!remark.equals("-")) {
            filter += "and remark like '" + remark + "'%";
        }
        String sql = "select a.*,t.trader_name\n" +
                "from (\n" +
                "select *\n" +
                "from payment_his\n" +
                "where deleted =" + deleted + "\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and date(vou_date) between '" + startDate + "' and '" + endDate + "'\n" + filter + "\n" +
                ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code;";
        List<PaymentHis> list = new ArrayList<>();
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                PaymentHis p = new PaymentHis();
                PaymentHisKey key = new PaymentHisKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                key.setDeptId(rs.getInt("dept_id"));
                p.setKey(key);
                p.setVouDate(rs.getDate("vou_date"));
                p.setAmount(rs.getFloat("amount"));
                p.setRemark(rs.getString("remark"));
                p.setDeleted(rs.getBoolean("deleted"));
                p.setCreatedBy(rs.getString("created_by"));
                p.setProjectNo(rs.getString("project_no"));
                p.setTraderName(rs.getString("trader_name"));
                list.add(p);
            }
        } catch (Exception e) {
            log.error("search : " + e.getMessage());
        }

        return list;
    }
}
