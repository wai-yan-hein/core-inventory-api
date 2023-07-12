package cv.api.dao;

import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
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
    public void restore(PaymentHisKey key) {
        PaymentHis ph = getByKey(key);
        ph.setDeleted(false);
        update(ph);
    }

    @Override
    public void delete(PaymentHisKey key) {
        PaymentHis ph = getByKey(key);
        ph.setDeleted(true);
        update(ph);
    }

    @Override
    public List<PaymentHis> search(String startDate, String endDate, String traderCode, String curCode,
                                   String vouNo,String saleVouNo, String userCode, String account, String projectNo,
                                   String remark, boolean deleted, String compCode) {
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and ph.trader_code ='" + traderCode + "'";
        }
        if (!projectNo.equals("-")) {
            filter += "and  ph.project_no ='" + projectNo + "'";
        }
        if (!vouNo.equals("-")) {
            filter += "and  ph.vou_no ='" + vouNo + "'";
        }
        if (!account.equals("-")) {
            filter += "and  ph.account ='" + account + "'";
        }
        if (!userCode.equals("-")) {
            filter += "and  ph.created_by ='" + userCode + "'";
        }
        if (!remark.equals("-")) {
            filter += "and  ph.remark like '" + remark + "'%";
        }
        if (!saleVouNo.equals("-")) {
            filter += "and  phd.sale_vou_no = '" + saleVouNo + "'";
        }
        String sql = "select a.*,t.trader_name\n" +
                "from (\n" +
                "select ph.*\n" +
                "from payment_his ph,payment_his_detail phd\n" +
                "where ph.vou_no = phd.vou_no\n" +
                "and ph.comp_code = phd.comp_code\n"+
                "and ph.deleted =" + deleted + "\n" +
                "and ph.comp_code ='" + compCode + "'\n" +
                "and date(ph.vou_date) between '" + startDate + "' and '" + endDate + "'\n" + filter + "\n" + ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "group by a.vou_no\n"+
                "order by a.vou_date,a.vou_no desc";
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
                p.setVouDate(rs.getTimestamp("vou_date").toLocalDateTime());
                p.setAmount(rs.getFloat("amount"));
                p.setRemark(rs.getString("remark"));
                p.setDeleted(rs.getBoolean("deleted"));
                p.setCreatedBy(rs.getString("created_by"));
                p.setProjectNo(rs.getString("project_no"));
                p.setTraderCode(rs.getString("trader_code"));
                p.setTraderName(rs.getString("trader_name"));
                p.setAccount(rs.getString("account"));
                p.setCurCode(rs.getString("cur_code"));
                list.add(p);
            }
        } catch (Exception e) {
            log.error("search : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<PaymentHis> unUploadVoucher(LocalDateTime syncDate) {
        String hsql = "select o from PaymentHis o where o.intgUpdStatus is null and o.vouDate >= :syncDate";
        return createQuery(hsql).setParameter("syncDate", syncDate).getResultList();
    }
}
