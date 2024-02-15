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
        obj.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(obj, obj.getKey());
        return obj;
    }

    @Override
    public PaymentHis find(PaymentHisKey key) {
        PaymentHis byKey = getByKey(key);
        if (byKey != null) {
            byKey.setVouDateTime(Util1.toZonedDateTime(byKey.getVouDate()));
        }
        return byKey;
    }

    @Override
    public void restore(PaymentHisKey key) {
        PaymentHis ph = getByKey(key);
        ph.setDeleted(false);
        updateEntity(ph);
    }

    @Override
    public void delete(PaymentHisKey key) {
        PaymentHis ph = getByKey(key);
        ph.setDeleted(true);
        updateEntity(ph);
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
                VSale s = VSale.builder().build();
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
