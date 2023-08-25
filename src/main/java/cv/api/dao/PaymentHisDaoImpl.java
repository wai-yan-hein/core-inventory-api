package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.PaymentHis;
import cv.api.entity.PaymentHisKey;
import cv.api.model.VSale;
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
                                   String vouNo, String saleVouNo, String userCode, String account, String projectNo,
                                   String remark, boolean deleted, String compCode, String tranOption) {
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and ph.trader_code =?";
        }
        if (!projectNo.equals("-")) {
            filter += "and  ph.project_no =?";
        }
        if (!vouNo.equals("-")) {
            filter += "and  ph.vou_no =?";
        }
        if (!account.equals("-")) {
            filter += "and  ph.account =?";
        }
        if (!userCode.equals("-")) {
            filter += "and  ph.created_by =?";
        }
        if (!remark.equals("-")) {
            filter += "and  ph.remark like ?";
        }
        if (!saleVouNo.equals("-")) {
            filter += "and  phd.sale_vou_no = ?";
        }
        String sql = "select a.*,t.trader_name\n" +
                "from (\n" +
                "select ph.*\n" +
                "from payment_his ph,payment_his_detail phd\n" +
                "where ph.vou_no = phd.vou_no\n" +
                "and ph.comp_code = phd.comp_code\n" +
                "and ph.deleted =?\n" +
                "and ph.comp_code =?\n" +
                "and ph.cur_code = ?\n"+
                "and ph.tran_option =?\n" +
                "and date(ph.vou_date) between ? and ?\n" + filter + "\n" + ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "group by a.vou_no\n" +
                "order by a.vou_date,a.vou_no desc";
        List<PaymentHis> list = new ArrayList<>();
        try {
            ResultSet rs = getResult(sql, deleted, compCode, curCode, tranOption, startDate, endDate,
                    traderCode, projectNo,vouNo,account,userCode,remark + "%",saleVouNo);
            while (rs.next()) {
                PaymentHis p = new PaymentHis();
                PaymentHisKey key = new PaymentHisKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                p.setKey(key);
                p.setDeptId(rs.getInt("dept_id"));
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

    @Override
    public List<VSale> getPaymentVoucher(String vouNo, String compCode) {
        List<VSale> list = new ArrayList<>();
        String sql = """
                select a.*,t.user_code,t.trader_name,t.address
                from (
                select ph.vou_date,ph.vou_no,ph.trader_code,ph.cur_code,ph.amount,phd.sale_vou_no,
                phd.sale_vou_date,phd.vou_total,phd.pay_amt,phd.vou_balance,phd.unique_id,ph.comp_code,ph.tran_option
                from payment_his ph, payment_his_detail phd
                where ph.vou_no = phd.vou_no
                and ph.comp_code = phd.comp_code
                and ph.vou_no =?
                and ph.comp_code =?
                )a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by unique_id""";
        ResultSet rs = getResult(sql, vouNo, compCode);
        try {
            while (rs.next()) {
                VSale s = new VSale();
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderCode(rs.getString("trader_code"));
                s.setCurCode(rs.getString("cur_code"));
                s.setPaid(rs.getDouble("pay_amt"));
                s.setVouBalance(rs.getDouble("vou_balance"));
                s.setUserCode(rs.getString("user_code"));
                s.setTraderName(rs.getString("trader_name"));
                s.setAddress(rs.getString("address"));
                s.setTranOption(rs.getString("tran_option"));
                s.setSaleVouNo(rs.getString("sale_vou_no"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setPayDate(Util1.toDateStr(rs.getTimestamp("vou_date").toLocalDateTime(), "dd/MM/yyyy"));
                s.setVouDate(Util1.toDateStr(rs.getDate("sale_vou_date"), "dd/MM/yyyy"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error("getPaymentVoucher : " + e.getMessage());
        }
        return list;
    }

    @Override
    public boolean checkPaymentExists(String vouNo, String traderCode, String compCode, String tranOption) {
        String sql = """
                select *
                from payment_his ph,payment_his_detail phd
                where ph.vou_no = phd.vou_no
                and ph.deleted = false
                and phd.sale_vou_no =?
                and ph.comp_code =?
                and ph.trader_code =?
                and ph.tran_option=?""";
        ResultSet rs = getResult(sql, vouNo, compCode, traderCode, tranOption);
        try {
            return rs.next();
        } catch (Exception e) {
            log.error("checkPaymentExists : " + e.getMessage());
        }
        return false;
    }
}
