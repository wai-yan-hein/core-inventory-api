package cv.api.service;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.dao.PaymentHisDao;
import cv.api.dao.PaymentHisDetailDao;
import cv.api.dao.SaleHisDao;
import cv.api.dao.SeqTableDao;
import cv.api.entity.*;
import cv.api.model.VSale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentHisServiceImpl implements PaymentHisService {
    private final PaymentHisDao dao;
    private final PaymentHisDetailDao detailDao;
    private final SeqTableDao seqDao;
    private final SaleHisDao saleHisDao;
    private final DatabaseClient client;

    @Override
    public PaymentHis save(PaymentHis obj) {
        obj.setVouDate(Util1.toDateTime(obj.getVouDate()));
        if (Util1.isNullOrEmpty(obj.getKey().getVouNo())) {
            obj.getKey().setVouNo(getVoucherNo(obj.getMacId(), obj.getKey().getCompCode(), obj.getDeptId(), obj.getTranOption()));
        }
        List<PaymentHisDetail> listDetail = obj.getListDetail();
        List<PaymentHisDetailKey> listDel = obj.getListDelete();
        String vouNo = obj.getKey().getVouNo();
        if (listDel != null) {
            listDel.forEach(detailDao::delete);
        }
        for (int i = 0; i < listDetail.size(); i++) {
            PaymentHisDetail cSd = listDetail.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                PaymentHisDetailKey key = new PaymentHisDetailKey();
                key.setCompCode(obj.getKey().getCompCode());
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                key.setDeptId(obj.getDeptId());
                cSd.setKey(key);
            }
            if (Util1.getFloat(cSd.getPayAmt()) > 0) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        PaymentHisDetail pSd = listDetail.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                detailDao.save(cSd);
                updateSale(cSd, true);
            }
            dao.save(obj);
            obj.setListDetail(listDetail);
        }
        return obj;
    }

    private void updateSale(PaymentHisDetail ph, boolean post) {
        String saleVouNo = ph.getSaleVouNo();
        if (!Util1.isNullOrEmpty(saleVouNo)) {
            SaleHisKey key = new SaleHisKey();
            key.setVouNo(saleVouNo);
            key.setCompCode(ph.getKey().getCompCode());
            SaleHis wh = saleHisDao.findById(key);
            if (wh != null) {
                wh.setPost(post);
                saleHisDao.save(wh);
            }
        }
    }

    @Override
    public PaymentHis find(PaymentHisKey key) {
        return dao.find(key);
    }

    @Override
    public void delete(PaymentHisKey key) {
        dao.delete(key);
        List<PaymentHisDetail> list = detailDao.search(key.getVouNo(), key.getCompCode());
        list.forEach(t -> updateSale(t, false));
    }

    @Override
    public void restore(PaymentHisKey key) {
        dao.restore(key);
    }

    @Override
    public Flux<PaymentHis> search(FilterObject filter) {
        String tranOption = Util1.isNull(filter.getTranOption(), "-");
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String saleVouNo = Util1.isNull(filter.getSaleVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String account = Util1.isAll(filter.getAccount());
        String traderCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String compCode = filter.getCompCode();
        boolean deleted = filter.isDeleted();
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        if (tranOption.equals("C") || tranOption.equals("S")) {
            String sql = """
                    select a.*,t.trader_name
                    from (
                    select ph.*
                    from payment_his ph,payment_his_detail phd
                    where ph.vou_no = phd.vou_no
                    and ph.comp_code = phd.comp_code
                    and ph.deleted =:deleted
                    and ph.comp_code =:compCode
                    and ph.cur_code = :curCode
                    and ph.tran_option =:tranOption
                    and (ph.trader_code = :traderCode or '-' =:traderCode)
                    and (ph.project_no =:projectNo or '-' =:projectNo)
                    and (ph.vou_no =:vouNo or '-' =:vouNo)
                    and (phd.sale_vou_no = :saleVouNo or '-'=:saleVouNo)
                    and (ph.created_by = :createdBy or '-' =:createdBy)
                    and (ph.account = :account or '-' =:account)
                    and (ph.remark REGEXP :remark or '-' = :remark)
                    and date(ph.vou_date) between :fromDate and :toDate)a
                    join trader t on a.trader_code = t.code
                    and a.comp_code = t.comp_code
                    group by a.vou_no
                    order by a.vou_date desc""";
            return client.sql(sql)
                    .bind("deleted", deleted)
                    .bind("compCode", compCode)
                    .bind("curCode", curCode)
                    .bind("tranOption", tranOption)
                    .bind("traderCode", traderCode)
                    .bind("projectNo", projectNo)
                    .bind("vouNo", vouNo)
                    .bind("saleVouNo", saleVouNo)
                    .bind("fromDate", fromDate)
                    .bind("toDate", toDate)
                    .bind("createdBy", userCode)
                    .bind("account", account)
                    .bind("remark", remark)
                    .map((row) -> {
                        PaymentHis p = new PaymentHis();
                        PaymentHisKey key = new PaymentHisKey();
                        key.setCompCode(row.get("comp_code", String.class));
                        key.setVouNo(row.get("vou_no", String.class));
                        p.setKey(key);
                        p.setDeptId(row.get("dept_id", Integer.class));
                        p.setVouDate(row.get("vou_date", LocalDateTime.class));
                        p.setVouDateTime(Util1.toZonedDateTime(p.getVouDate()));
                        p.setAmount(row.get("amount", Double.class));
                        p.setRemark(row.get("remark", String.class));
                        p.setDeleted(row.get("deleted", Boolean.class));
                        p.setCreatedBy(row.get("created_by", String.class));
                        p.setProjectNo(row.get("project_no", String.class));
                        p.setTraderCode(row.get("trader_code", String.class));
                        p.setTraderName(row.get("trader_name", String.class));
                        p.setAccount(row.get("account", String.class));
                        p.setCurCode(row.get("cur_code", String.class));
                        return p;
                    }).all();
        }
        return Flux.empty();
    }

    @Override
    public List<PaymentHis> unUploadVoucher(LocalDateTime syncDate) {
        return dao.unUploadVoucher(syncDate);
    }

    @Override
    public List<VSale> getPaymentVoucher(String vouNo, String compCode) {
        return dao.getPaymentVoucher(vouNo, compCode);
    }

    @Override
    public boolean checkPaymentExists(String vouNo, String traderCode, String compCode, String tranOption) {
        return dao.checkPaymentExists(vouNo, traderCode, compCode, tranOption);
    }

    private String getVoucherNo(Integer macId, String compCode, Integer deptId, String tranOption) {
        String option = tranOption.equals("C") ? "RECEIVE" : "PAYMENT";
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, option, period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return tranOption + "-" + deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }

    @Override
    public Flux<PaymentHisDetail> getTraderBalance(String traderCode, String tranOption, String compCode) {
        if (tranOption.equals("C") || tranOption.equals("S")) {
            String sql;
            if (tranOption.equals("C")) {
                sql = """
                        select sh.vou_date,sh.reference,sh.remark,sh.vou_total,b.vou_no,b.cur_code,b.outstanding
                        from (
                        select vou_no,cur_code,sum(vou_balance) outstanding,comp_code
                        from (
                        select vou_no,cur_code,round(vou_balance,0) vou_balance,comp_code
                        from sale_his
                        where trader_code=:traderCode
                        and comp_code =:compCode
                        and deleted = false
                        and vou_balance>0
                          union all
                        select phd.sale_vou_no,phd.cur_code,round(phd.pay_amt,0)*-1,pd.comp_code
                        from payment_his pd join payment_his_detail phd
                        on pd.vou_no = phd.vou_no
                        and pd.comp_code = phd.comp_code
                        where pd.trader_code=:traderCode
                        and pd.comp_code =:compCode
                        and pd.tran_option =:tranOption
                        and pd.deleted = false
                        )a
                        group by vou_no,cur_code
                        )b
                        join sale_his sh
                        on b.vou_no = sh.vou_no
                        and b.comp_code = sh.comp_code
                        where outstanding<>0
                        order by vou_date""";
            } else {
                sql = """
                        select sh.vou_date,sh.reference,sh.remark,sh.vou_total,b.vou_no,b.cur_code,b.outstanding
                        from (
                        select vou_no,cur_code,sum(balance) outstanding,comp_code
                        from (
                        select vou_no,cur_code,round(balance,0) balance,comp_code
                        from pur_his
                        where trader_code=:traderCode
                        and comp_code =:compCode
                        and deleted = false
                        and balance>0
                            union all
                        select phd.sale_vou_no,phd.cur_code,round(phd.pay_amt,0)*-1,pd.comp_code
                        from payment_his pd join payment_his_detail phd
                        on pd.vou_no = phd.vou_no
                        and pd.comp_code = phd.comp_code
                        where pd.trader_code=:traderCode
                        and pd.comp_code =:compCode
                        and pd.tran_option =:tranOption
                        and pd.deleted = false
                        )a
                        group by vou_no,cur_code
                        )b
                        join pur_his sh
                        on b.vou_no = sh.vou_no
                        and b.comp_code = sh.comp_code
                        where outstanding<>0
                        order by vou_date;""";

            }
            return client.sql(sql)
                    .bind("traderCode", traderCode)
                    .bind("compCode", compCode)
                    .bind("tranOption", tranOption)
                    .map((row, rowMetadata) -> {
                        PaymentHisDetail pd = new PaymentHisDetail();
                        pd.setSaleDate(row.get("vou_date", LocalDate.class));
                        pd.setSaleVouNo(row.get("vou_no", String.class));
                        pd.setRemark(row.get("remark", String.class));
                        pd.setVouTotal(row.get("vou_total", Double.class));
                        pd.setVouBalance(row.get("outstanding", Double.class));
                        pd.setCurCode(row.get("cur_code", String.class));
                        pd.setReference(row.get("reference", String.class));
                        return pd;
                    }).all();
        }
        return Flux.empty();
    }
}
