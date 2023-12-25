/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.FilterObject;
import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.dao.*;
import cv.api.entity.*;
import cv.api.model.VSale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author wai yan
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SaleHisServiceImpl implements SaleHisService {

    private final SaleHisDao shDao;
    private final SaleHisDetailDao sdDao;
    private final SeqTableDao seqDao;
    private final SaleExpenseDao saleExpenseDao;
    private final VouDiscountDao vouDiscountDao;
    private final SaleOrderJoinDao saleOrderJoinDao;
    private final OrderHisDao orderHisDao;
    private final DatabaseClient databaseClient;

    @Override
    public SaleHis save(@NotNull SaleHis saleHis) {
        saleHis.setVouDate(Util1.toDateTime(saleHis.getVouDate()));
        if (Util1.isNullOrEmpty(saleHis.getKey().getVouNo())) {
            saleHis.getKey().setVouNo(getVoucherNo(saleHis.getDeptId(), saleHis.getMacId(), saleHis.getKey().getCompCode()));
        }
        List<SaleHisDetail> listSD = saleHis.getListSH();
        List<SaleDetailKey> listDel = saleHis.getListDel();
        List<SaleExpenseKey> listDelExp = saleHis.getListDelExpense();
        List<VouDiscountKey> listDelVouDiscount = saleHis.getListDelVouDiscount();
        //backup
        if (listDel != null) {
            listDel.forEach(sdDao::delete);
        }
        if (listDelExp != null) {
            listDelExp.forEach(saleExpenseDao::delete);
        }
        if (listDelVouDiscount != null) {
            listDelVouDiscount.forEach(vouDiscountDao::delete);
        }
        List<SaleExpense> listExp = saleHis.getListExpense();
        List<VouDiscount> listDiscount = saleHis.getListVouDiscount();
        List<String> listOrder = saleHis.getListOrder();
        //save expense
        saveSaleExpense(listExp, saleHis);
        //save detail
        saveDetail(listSD, saleHis);
        //save vou discount
        saveVouDiscount(listDiscount, saleHis);
        //save sale order join
        saveSaleOrderJoin(listOrder, saleHis);
        shDao.save(saleHis);
        saleHis.setListSH(listSD);
        return saleHis;
    }

    private void saveSaleExpense(List<SaleExpense> listExp, SaleHis sh) {
        String vouNo = sh.getKey().getVouNo();
        if (listExp != null) {
            for (int i = 0; i < listExp.size(); i++) {
                SaleExpense e = listExp.get(i);
                if (Util1.getDouble(e.getAmount()) > 0) {
                    if (e.getKey().getExpenseCode() != null) {
                        if (e.getKey().getUniqueId() == 0) {
                            if (i == 0) {
                                e.getKey().setUniqueId(1);
                            } else {
                                SaleExpense pe = listExp.get(i - 1);
                                e.getKey().setUniqueId(pe.getKey().getUniqueId() + 1);
                            }
                        }
                        e.getKey().setVouNo(vouNo);
                        saleExpenseDao.save(e);
                    }
                }
            }
        }
    }

    private void saveDetail(List<SaleHisDetail> listSD, SaleHis sh) {
        String compCode = sh.getKey().getCompCode();
        String vouNo = sh.getKey().getVouNo();
        int depId = sh.getDeptId();
        for (int i = 0; i < listSD.size(); i++) {
            SaleHisDetail cSd = listSD.get(i);
            if (Util1.isNullOrEmpty(cSd.getKey())) {
                SaleDetailKey key = new SaleDetailKey();
                key.setCompCode(compCode);
                key.setVouNo(vouNo);
                key.setUniqueId(0);
                cSd.setDeptId(depId);
                cSd.setKey(key);
            }
            if (cSd.getStockCode() != null) {
                if (cSd.getKey().getUniqueId() == 0) {
                    if (i == 0) {
                        cSd.getKey().setUniqueId(1);
                    } else {
                        SaleHisDetail pSd = listSD.get(i - 1);
                        cSd.getKey().setUniqueId(pSd.getKey().getUniqueId() + 1);
                    }
                }
                cSd.setTotalWeight(Util1.getDouble(cSd.getWeight()) * cSd.getQty());
                sdDao.save(cSd);
            }
        }
    }

    private void saveVouDiscount(List<VouDiscount> list, SaleHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                VouDiscount e = list.get(i);
                if (Util1.getDouble(e.getAmount()) > 0) {
                    if (e.getKey().getUniqueId() == 0) {
                        if (i == 0) {
                            e.getKey().setUniqueId(1);
                        } else {
                            VouDiscount pe = list.get(i - 1);
                            e.getKey().setUniqueId(pe.getKey().getUniqueId() + 1);
                        }
                    }
                    e.getKey().setVouNo(vouNo);
                    e.getKey().setCompCode(compCode);
                    vouDiscountDao.save(e);

                }
            }
        }
    }

    private void saveSaleOrderJoin(List<String> list, SaleHis sh) {
        if (list != null) {
            List<SaleOrderJoin> listJoin = saleOrderJoinDao.getSaleOrder(sh.getKey().getVouNo(), sh.getKey().getCompCode());
            listJoin.forEach(join -> saleOrderJoinDao.deleteOrder(join.getKey()));
            String saleVouNo = sh.getKey().getVouNo();
            String compCode = sh.getKey().getCompCode();
            for (String orderNo : list) {
                SaleOrderJoin obj = new SaleOrderJoin();
                SaleOrderJoinKey key = new SaleOrderJoinKey();
                key.setSaleVouNo(saleVouNo);
                key.setOrderVouNo(orderNo);
                key.setCompCode(compCode);
                obj.setKey(key);
                saleOrderJoinDao.save(obj);
                //update order
                OrderHisKey orderKey = new OrderHisKey();
                orderKey.setVouNo(orderNo);
                orderKey.setCompCode(compCode);
                updateOrder(orderKey, true);
            }
        }
    }

    private void updateOrder(OrderHisKey key, boolean post) {
        OrderHis oh = orderHisDao.findById(key);
        if (oh != null) {
            oh.setPost(post);
            orderHisDao.update(oh);
        }
    }

    @Override
    public SaleHis update(SaleHis saleHis) {
        return shDao.save(saleHis);
    }

    @Override
    public List<SaleHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String userCode) {
        return shDao.search(fromDate, toDate, cusCode, vouNo, remark, userCode);
    }

    @Override
    public SaleHis findById(SaleHisKey id) {
        return shDao.findById(id);
    }

    @Override
    public void delete(SaleHisKey key) {
        shDao.delete(key);
        List<SaleOrderJoin> list = saleOrderJoinDao.getSaleOrder(key.getVouNo(), key.getCompCode());
        list.forEach(order -> {
            OrderHisKey orderKey = new OrderHisKey();
            orderKey.setVouNo(order.getKey().getOrderVouNo());
            orderKey.setCompCode(order.getKey().getCompCode());
            updateOrder(orderKey, false);
        });
    }

    @Override
    public void restore(SaleHisKey key) throws Exception {
        shDao.restore(key);
    }

    private String getVoucherNo(Integer deptId, Integer macId, String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqDao.getSequence(macId, "SALE", period, compCode);
        String deptCode = String.format("%0" + 2 + "d", deptId) + "-";
        return deptCode + String.format("%0" + 2 + "d", macId) + String.format("%0" + 5 + "d", seqNo) + "-" + period;
    }


    @Override
    public List<SaleHis> unUploadVoucher(LocalDateTime syncDate) {
        return shDao.unUploadVoucher(syncDate);
    }

    @Override
    public List<SaleHis> unUpload(String syncDate) {
        return shDao.unUpload(syncDate);
    }

    @Override
    public Date getMaxDate() {
        return shDao.getMaxDate();
    }


    @Override
    public void truncate(SaleHisKey key) {
        shDao.truncate(key);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        return shDao.getVoucherInfo(vouDate, compCode, depId);
    }

    @Override
    public List<VouDiscount> getVoucherDiscount(String vouNo, String compCode) {
        return vouDiscountDao.getVoucherDiscount(vouNo, compCode);
    }

    @Override
    public List<VouDiscount> searchDiscountDescription(String str, String compCode) {
        return vouDiscountDao.getDescription(str, compCode);
    }

    @Override
    public Flux<?> getSale(FilterObject filterObject) {
        String fromDate = Util1.isNull(filterObject.getFromDate(), "-");
        String toDate = Util1.isNull(filterObject.getToDate(), "-");
        String vouNo = Util1.isNull(filterObject.getVouNo(), "-");
        String userCode = Util1.isNull(filterObject.getUserCode(), "-");
        String traderCode = Util1.isNull(filterObject.getTraderCode(), "-");
        String remark = Util1.isNull(filterObject.getRemark(), "-");
        String stockCode = Util1.isNull(filterObject.getStockCode(), "-");
        String saleManCode = Util1.isNull(filterObject.getSaleManCode(), "-");
        String reference = Util1.isNull(filterObject.getReference(), "-");
        String compCode = filterObject.getCompCode();
        String locCode = Util1.isNull(filterObject.getLocCode(), "-");
        Integer deptId = filterObject.getDeptId();
        String deleted = String.valueOf(filterObject.isDeleted());
        String nullBatch = String.valueOf(filterObject.isNullBatch());
        String batchNo = Util1.isNull(filterObject.getBatchNo(), "-");
        String projectNo = Util1.isAll(filterObject.getProjectNo());
        String curCode = Util1.isAll(filterObject.getCurCode());

        String filter = "";
        if (!vouNo.equals("-")) {
            filter += "and vou_no = :vouNo\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like :remark%\n";
        }
        if (!reference.equals("-")) {
            filter += "and reference like :reference%\n";
        }
        if (!traderCode.equals("-")) {
            filter += "and trader_code = :traderCode\n";
        }
        if (!userCode.equals("-")) {
            filter += "and created_by = :userCode\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code = :stockCode\n";
        }
        if (!saleManCode.equals("-")) {
            filter += "and saleman_code = :saleManCode\n";
        }
        if (!locCode.equals("-")) {
            filter += "and loc_code = :locCode\n";
        }
        if (nullBatch.equals("true")) {
            filter += "and (batch_no is null or batch_no ='') \n";
        }
        if (!batchNo.equals("-")) {
            filter += "and batch_no = :batchNo\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no = :projectNo\n";
        }
        if (!curCode.equals("-")) {
            filter += "and cur_code = :curCode\n";
        }
        return null;
//        String sql = "select a.*,t.trader_name,t.user_code\n" +
//                "from (\n" +
//                "select  vou_no,vou_date,remark,reference,created_by,paid,vou_total,vou_balance,\n" +
//                "deleted,trader_code,loc_code,comp_code,dept_id\n" +
//                "from v_sale s \n" + "where comp_code = :compCode\n" +
//                "and (dept_id = :deptId or 0 = :deptId)\n" +
//                "and deleted = :deleted\n" +
//                "and date(vou_date) between :fromDate and :toDate\n" + filter + "\n" +
//                "group by vou_no\n" + ")a\n" +
//                "join trader t on a.trader_code = t.code\n" +
//                "and a.comp_code = t.comp_code\n" +
//                "order by vou_date desc";
//        return databaseClient.sql(sql)
//                .bind("vouNo", vouNo)
//                .bind("remark", remark)
//                .bind("reference", reference)
//                .bind("traderCode", traderCode)
//                .bind("userCode", userCode)
//                .bind("stockCode", stockCode)
//                .bind("saleManCode", saleManCode)
//                .bind("locCode", locCode)
//                .bind("batchNo", batchNo)
//                .bind("projectNo", projectNo)
//                .bind("curCode", curCode)
//                .bind("deptId", deptId)
//                .bind("deleted", deleted)
//                .bind("fromDate", fromDate)
//                .bind("toDate", toDate)
//                .map((row) -> VSale.builder()
//                        .vouDate(Util1.toDateStr(row.get("vou_date", Date.class), "dd/MM/yyyy"))
//                        .build()
//                ).all();
    }


}
