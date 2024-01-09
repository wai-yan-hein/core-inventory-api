/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.Util1;
import cv.api.dao.*;
import cv.api.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final WeightDao weightDao;

    @Override
    public SaleHis save(@NotNull SaleHis saleHis) {
        Integer deptId = saleHis.getDeptId();
        if (deptId == null) {
            log.error("deptId is null from mac id : " + saleHis.getMacId());
            return null;
        }
        saleHis.setVouDate(Util1.toDateTime(saleHis.getVouDate()));
        if (Util1.isNullOrEmpty(saleHis.getKey().getVouNo())) {
            saleHis.getKey().setVouNo(getVoucherNo(deptId, saleHis.getMacId(), saleHis.getKey().getCompCode()));
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
        updateWeight(saleHis, true);
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
        int depId = sh.getDeptId() == null ? 0 : sh.getDeptId();
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

    private void updateWeight(SaleHis sh, boolean status) {
        String weightVouNo = sh.getWeightVouNo();
        if (!Util1.isNullOrEmpty(weightVouNo)) {
            WeightHisKey key = new WeightHisKey();
            key.setVouNo(weightVouNo);
            key.setCompCode(sh.getKey().getCompCode());
            WeightHis wh = weightDao.findById(key);
            if (wh != null) {
                wh.setPost(status);
                weightDao.save(wh);
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
        SaleHis saleHis = findById(key);
        if (saleHis != null) {
            updateWeight(saleHis, false);
            List<SaleOrderJoin> list = saleOrderJoinDao.getSaleOrder(key.getVouNo(), key.getCompCode());
            list.forEach(order -> {
                OrderHisKey orderKey = new OrderHisKey();
                orderKey.setVouNo(order.getKey().getOrderVouNo());
                orderKey.setCompCode(order.getKey().getCompCode());
                updateOrder(orderKey, false);
            });
            shDao.delete(key);
        }
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


}
