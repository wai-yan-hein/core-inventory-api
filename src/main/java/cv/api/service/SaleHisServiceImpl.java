/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.General;
import cv.api.common.ReportFilter;
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
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
    private final DatabaseClient client;
    private final SaleNoteService saleNoteService;

    @Override
    public SaleHis save(@NotNull SaleHis sh) {
        Integer deptId = sh.getDeptId();
        if (deptId == null) {
            log.error("deptId is null from mac id : " + sh.getMacId());
            return null;
        }
        sh.setVouDate(Util1.toDateTime(sh.getVouDate()));
        if (Util1.isNullOrEmpty(sh.getKey().getVouNo())) {
            sh.getKey().setVouNo(getVoucherNo(deptId, sh.getMacId(), sh.getKey().getCompCode()));
        }
        List<SaleHisDetail> listSD = sh.getListSH();
        List<SaleDetailKey> listDel = sh.getListDel();
        List<SaleExpenseKey> listDelExp = sh.getListDelExpense();
        List<VouDiscountKey> listDelVouDiscount = sh.getListDelVouDiscount();
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
        List<SaleExpense> listExp = sh.getListExpense();
        List<VouDiscount> listDiscount = sh.getListVouDiscount();
        List<String> listOrder = sh.getListOrder();
        List<SaleNote> listSaleNote = sh.getListSaleNote();
        //save expense
        saveSaleExpense(listExp, sh);
        //save detail
        saveDetail(listSD, sh);
        //save vou discount
        saveVouDiscount(listDiscount, sh);
        //save sale order join
        saveSaleOrderJoin(listOrder, sh);
        //save note
        saveNote(listSaleNote, sh);
        shDao.save(sh);
        sh.setListSH(listSD);
        return sh;
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

    private void saveNote(List<SaleNote> list, SaleHis sh) {
        String vouNo = sh.getKey().getVouNo();
        String compCode = sh.getKey().getCompCode();
        if (list != null && !list.isEmpty()) {
            saleNoteService.delete(vouNo, compCode)
                    .flatMap(delete -> Flux.fromIterable(list)
                            .concatMap(cSd -> {
                                if (!Util1.isNullOrEmpty(cSd.getDescription())) {
                                    int uniqueId = list.indexOf(cSd) + 1;
                                    cSd.setUniqueId(uniqueId);
                                    cSd.setVouNo(vouNo);
                                    cSd.setCompCode(compCode);
                                    return saleNoteService.insert(cSd);
                                }
                                return Mono.empty();
                            }).then()).subscribe();
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
    public void update(SaleHis saleHis) {
        shDao.update(saleHis);
    }

    @Override
    public List<SaleHis> search(String fromDate, String toDate, String cusCode, String vouNo, String remark, String
            userCode) {
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
    public void truncate(SaleHisKey key) {
        shDao.truncate(key);
    }

    @Override
    public General getVoucherInfo(String vouDate, String compCode, Integer depId) {
        return shDao.getVoucherInfo(vouDate, compCode, depId);
    }

    @Override
    public Flux<VouDiscount> getVoucherDiscount(String vouNo, String compCode) {
        return vouDiscountDao.getVoucherDiscount(vouNo, compCode);
    }

    @Override
    public Flux<SaleNote> getSaleNote(String vouNo, String compCode) {
        return saleNoteService.getSaleNote(vouNo, compCode);

    }

    @Override
    public List<VouDiscount> searchDiscountDescription(String str, String compCode) {
        return vouDiscountDao.getDescription(str, compCode);
    }

    @Override
    public Flux<VSale> getSale(ReportFilter filterObject) {
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
        StringBuilder filter = new StringBuilder();
        if (Boolean.parseBoolean(nullBatch)) {
            filter.append(" and (batch_no is null or batch_no ='')\n");
        }
        String sql = """
                select a.*,t.trader_name,t.user_code t_user_code
                from (
                select vou_no,vou_date,remark,reference,created_by,paid,vou_total,vou_balance,
                deleted,trader_code,loc_code,comp_code,dept_id,post,sum(qty) qty,sum(bag) bag,
                opening,outstanding,total_payment,total_balance
                from v_sale s
                where comp_code = :compCode
                and (dept_id = :deptId or 0 = :deptId)
                and deleted = :deleted
                and date(vou_date) between :fromDate and :toDate
                and (vou_no = :vouNo or '-' = :vouNo)
                and (remark REGEXP :remark or '-' = :remark)
                and (reference REGEXP :reference or '-' = :reference)
                and (trader_code = :traderCode or '-' = :traderCode)
                and (created_by = :userCode or '-' = :userCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (saleman_code = :saleManCode or '-' = :saleManCode)
                and (loc_code = :locCode or '-' = :locCode)
                and (batch_no = :batchNo or '-' = :batchNo)
                and (project_no = :projectNo or '-' = :projectNo)
                and (cur_code = :curCode or '-' = :curCode)
                """ + filter + """
                group by vou_no)a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by vou_date desc""";

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("deleted", deleted)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("vouNo", vouNo)
                .bind("remark", remark)
                .bind("reference", reference)
                .bind("traderCode", traderCode)
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("saleManCode", saleManCode)
                .bind("locCode", locCode)
                .bind("batchNo", batchNo)
                .bind("projectNo", projectNo)
                .bind("curCode", curCode)
                .map((row) -> VSale.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDateTime.class), "dd/MM/yyyy"))
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouNo(row.get("vou_no", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .traderUserCode(row.get("t_user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .remark(row.get("remark", String.class))
                        .reference(row.get("reference", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .paid(row.get("paid", Double.class))
                        .vouTotal(row.get("vou_total", Double.class))
                        .vouBalance(row.get("vou_balance", Double.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .post(row.get("post", Boolean.class))
                        .qty(row.get("qty", Double.class))
                        .bag(row.get("bag", Double.class))
                        .opening(row.get("opening", Double.class))
                        .outstanding(row.get("outstanding", Double.class))
                        .totalBalance(row.get("total_balance", Double.class))
                        .totalPayment(row.get("total_payment", Double.class))
                        .build()
                ).all();
    }

    @Override
    public Mono<Boolean> updatePost(String vouNo, String compCode, boolean post) {
        String sql = """
                update sale_his
                set post = :post
                where vou_no=:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("post", post)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated().thenReturn(true);
    }

}
