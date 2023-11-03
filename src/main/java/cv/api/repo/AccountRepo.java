package cv.api.repo;

import cv.api.auto.LocationSetting;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.dao.SaleExpenseDao;
import cv.api.entity.*;
import cv.api.model.*;
import cv.api.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class AccountRepo {
    private final String ACK = "ACK";
    private final String appName = "SM";
    private final Integer macId = 98;
    @Autowired
    private WebClient accountApi;
    @Autowired
    private ReportService reportService;
    @Autowired
    private Environment environment;
    @Autowired
    private TraderService traderService;
    @Autowired
    private TraderGroupService groupService;
    @Autowired
    private GRNService grnService;
    @Autowired
    private PurExpenseService purExpenseService;
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private AccSettingService settingService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private SaleExpenseDao saleExpenseDao;

    private void sendAccount(List<Gl> glList) {
        if (!glList.isEmpty()) {
            accountApi.post().uri("/account/saveGlList")
                    .body(Mono.just(glList), List.class)
                    .retrieve()
                    .bodyToMono(Response.class)
                    .subscribe(response -> {
                        if (response != null) {
                            String vouNo = response.getVouNo();
                            String compCode = response.getCompCode();
                            switch (response.getTranSource()) {
                                case "SALE" -> updateSale(vouNo, compCode);
                                case "PURCHASE" -> updatePurchase(vouNo, compCode);
                                case "RETURN_IN" -> updateReturnIn(vouNo, compCode);
                                case "RETURN_OUT" -> updateReturnOut(vouNo, compCode);
                                case "PAYMENT" -> updatePayment(vouNo, compCode, ACK);
                            }
                        }
                    }, (e) -> {
                        Gl gl = glList.get(0);
                        String vouNo = gl.getRefNo();
                        String compCode = gl.getKey().getCompCode();
                        String tranSource = gl.getTranSource();
                        switch (tranSource) {
                            case "SALE" -> updateSaleNull(vouNo, compCode);
                            case "PURCHASE" -> updatePurchaseNull(vouNo, compCode);
                            case "RETURN_IN" -> updateReturnInNull(vouNo, compCode);
                            case "RETURN_OUT" -> updateReturnOutNull(vouNo, compCode);
                            case "PAYMENT" -> updatePaymentNull(vouNo, compCode);
                        }
                        log.error(e.getMessage());
                    });
        }
    }

    private void updateSale(String vouNo, String compCode) {
        String sql = "update sale_his set intg_upd_status = '" + ACK + "' where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
            //log.info(String.format("updateSale: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updateSale: %s", e.getMessage()));
        }
    }

    private void updateSaleNull(String vouNo, String compCode) {
        String sql = "update sale_his set intg_upd_status = null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
        } catch (Exception e) {
            log.error(String.format("updateSale: %s", e.getMessage()));
        }
    }

    private void updatePurchase(String vouNo, String compCode) {
        String sql = "update pur_his set intg_upd_status = '" + ACK + "' where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
            //log.info(String.format("updatePurchase: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updatePurchase: %s", e.getMessage()));
        }
    }


    private void updatePurchaseNull(String vouNo, String compCode) {
        String sql = "update pur_his set intg_upd_status = null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
            //log.info(String.format("updatePurchase: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updatePurchase: %s", e.getMessage()));
        }
    }

    private void updateReturnInNull(String vouNo, String compCode) {
        String sql = "update ret_in_his set intg_upd_status = null where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "'";
        try {
            reportService.executeSql(sql);
            //log.info(String.format("updateReturnIn: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updateReturnIn: %s", e.getMessage()));
        }
    }

    private void updateReturnIn(String vouNo, String compCode) {
        String sql = "update ret_in_his set intg_upd_status = '" + ACK + "' where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "'";
        try {
            reportService.executeSql(sql);
            //log.info(String.format("updateReturnIn: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updateReturnIn: %s", e.getMessage()));
        }
    }

    private void updateReturnOutNull(String vouNo, String compCode) {
        String sql = "update ret_out_his set intg_upd_status = null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
            //log.info(String.format("updateReturnOut: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updateReturnOut: %s", e.getMessage()));
        }
    }

    private void updatePaymentNull(String vouNo, String compCode) {
        String sql = "update payment_his set intg_upd_status = null where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
        } catch (Exception e) {
            log.error(String.format("updatePaymentNull: %s", e.getMessage()));
        }
    }

    private void updateReturnOut(String vouNo, String compCode) {
        String sql = "update ret_out_his set intg_upd_status = '" + ACK + "' where vou_no ='" + vouNo + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
            //log.info(String.format("updateReturnOut: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updateReturnOut: %s", e.getMessage()));
        }
    }

    private void updateTrader(String traderCode, String account, String compCode) {
        String sql = "update trader set intg_upd_status = '" + ACK + "',account = '" + account + "' where code ='" + traderCode + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
            //log.info(String.format("updateTrader: %s", traderCode));
        } catch (Exception e) {
            log.error(String.format("updateTrader: %s", e.getMessage()));
        }
    }

    private void updatePayment(String vouNo, String compCode, String status) {
        String sql = "update payment_his set intg_upd_status ='" + status + "' where vou_no='" + vouNo + "' and comp_code='" + compCode + "'";
        try {
            reportService.executeSql(sql);
        } catch (Exception e) {
            log.error("updatePayment : " + e.getMessage());
        }
    }

    public void sendTrader(Trader t) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            if (t != null) {
                String traderType = t.getType();
                AccTrader accTrader = new AccTrader();
                AccTraderKey key = new AccTraderKey();
                key.setCode(t.getKey().getCode());
                key.setCompCode(t.getKey().getCompCode());
                accTrader.setKey(key);
                accTrader.setTraderName(t.getTraderName());
                accTrader.setUserCode(t.getUserCode());
                accTrader.setActive(t.isActive());
                accTrader.setAppName(appName);
                accTrader.setMacId(macId);
                accTrader.setAccount(t.getAccount());
                accTrader.setDeleted(t.isDeleted());
                switch (traderType) {
                    case "CUS" -> accTrader.setTraderType("C");
                    case "SUP" -> accTrader.setTraderType("S");
                    default -> accTrader.setTraderType("D");
                }
                accountApi.post().uri("/account/saveTrader").body(Mono.just(accTrader), AccTrader.class).retrieve().bodyToMono(AccTrader.class).subscribe((response) -> updateTrader(response.getKey().getCode(), response.getAccount(), response.getKey().getCompCode()), (e) -> log.error("send Trader : " + e.getMessage()));
            }
        }
    }

    public void deleteTrader(AccTraderKey key) {
        accountApi.post().uri("/account/deleteTrader")
                .body(Mono.just(key), AccTraderKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class)
                .subscribe((t) -> log.info("deleted."), (e) -> log.error("deleteTrader : " + e.getMessage()));
    }

    public void sendSale(SaleHis sh) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            String tranSource = "SALE";
            String compCode = sh.getKey().getCompCode();
            String locCode = sh.getLocCode();
            AccSetting setting = settingService.findByCode(new AccKey(tranSource, compCode));
            if (!Objects.isNull(setting)) {
                LocationSetting ls = getLocationSetting(locCode, compCode);
                String srcAcc = setting.getSourceAcc();
                srcAcc = Util1.isNull(sh.getAccount(), srcAcc);
                String payAcc = Util1.isNull(ls.getCashAcc(), setting.getPayAcc());
                String deptCode = Util1.isNull(ls.getDeptCode(), setting.getDeptCode());
                String disAcc = setting.getDiscountAcc();
                String balAcc = setting.getBalanceAcc();
                String taxAcc = setting.getTaxAcc();
                LocalDateTime vouDate = sh.getVouDate();
                String traderCode = sh.getTraderCode();
                String curCode = sh.getCurCode();
                String remark = sh.getRemark();
                boolean deleted = sh.isDeleted();
                String vouNo = sh.getKey().getVouNo();
                String projectNo = sh.getProjectNo();
                double vouTotal = Util1.getDouble(sh.getVouTotal());
                double vouDis = Util1.getDouble(sh.getDiscount());
                double vouPaid = Util1.getDouble(sh.getPaid());
                double vouTax = Util1.getDouble(sh.getTaxAmt());
                double taxPercent = Util1.getDouble(sh.getTaxPercent());
                Integer deptId = sh.getDeptId();
                TraderKey k = new TraderKey();
                k.setCode(traderCode);
                k.setCompCode(compCode);
                Trader t = traderService.findById(k);
                if (t != null) {
                    //income by trader group
                    String groupCode = t.getGroupCode();
                    TraderGroupKey key = new TraderGroupKey();
                    key.setGroupCode(groupCode);
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    TraderGroup g = groupService.findById(key);
                    if (g != null) {
                        if (!Util1.isNullOrEmpty(g.getAccount())) {
                            srcAcc = g.getAccount();
                        }
                    }
                    if (!Util1.isNullOrEmpty(t.getAccount())) {
                        balAcc = t.getAccount();
                    }
                }
                String batchNo = "";
                String grnVouNo = sh.getGrnVouNo();
                if (grnVouNo != null) {
                    GRNKey grnKey = new GRNKey();
                    grnKey.setVouNo(grnVouNo);
                    grnKey.setCompCode(compCode);
                    GRN grn = grnService.findByCode(grnKey);
                    if (grn != null) {
                        batchNo = grn.getBatchNo();
                    }
                }
                List<Gl> listGl = new ArrayList<>();
                //income
                if (vouTotal > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Total");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                //discount
                if (vouDis > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setSrcAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouDis);
                    gl.setAccCode(disAcc);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Discount");
                    gl.setTraderCode(traderCode);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                //payment cash down
                if (vouPaid > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    if (vouPaid == vouTotal) {
                        gl.setDescription("Sale Voucher Full Paid");
                    } else {
                        gl.setDescription("Sale Voucher Partial Paid");
                    }
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(vouPaid);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                //tax
                if (vouTax > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setSrcAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setAccCode(taxAcc);
                    gl.setDrAmt(vouTax);
                    gl.setGlDate(vouDate);
                    gl.setDescription(String.format("Sale Voucher Tax (%s)", taxPercent));
                    gl.setTraderCode(traderCode);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                List<SaleExpense> listExp = saleExpenseDao.search(vouNo, compCode);
                for (SaleExpense e : listExp) {
                    String expCode = e.getKey().getExpenseCode();
                    ExpenseKey ek = new ExpenseKey();
                    ek.setExpenseCode(expCode);
                    ek.setCompCode(compCode);
                    Expense expense = expenseService.findById(ek);
                    if (expense != null) {
                        String account = expense.getAccountCode();
                        double amt = Util1.getDouble(e.getAmount());
                        if (!Util1.isNullOrEmpty(account)) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setGlDate(vouDate);
                            gl.setSrcAccCode(account);
                            gl.setAccCode(balAcc);
                            gl.setDrAmt(amt);
                            gl.setTraderCode(traderCode);
                            gl.setCurCode(curCode);
                            gl.setDescription(expense.getExpenseName());
                            gl.setReference(remark);
                            gl.setDeptCode(deptCode);
                            gl.setCreatedDate(LocalDateTime.now());
                            gl.setCreatedBy(appName);
                            gl.setTranSource(tranSource);
                            gl.setRefNo(vouNo);
                            gl.setDeleted(deleted);
                            gl.setMacId(macId);
                            gl.setBatchNo(batchNo);
                            gl.setProjectNo(projectNo);
                            listGl.add(gl);
                        }
                    }
                }
                sendAccount(listGl);
            }
        }
    }


    public void sendPurchase(PurHis ph) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            String tranSource = "PURCHASE";
            String compCode = ph.getKey().getCompCode();
            String locCode = ph.getLocCode();
            AccSetting setting = settingService.findByCode(new AccKey(tranSource, compCode));
            if (setting != null) {
                LocationSetting ls = getLocationSetting(locCode, compCode);
                String payAcc = Util1.isNull(ls.getCashAcc(), setting.getPayAcc());
                String deptCode = Util1.isNull(ls.getDeptCode(), setting.getDeptCode());
                String srcAcc = setting.getSourceAcc();
                String balAcc = setting.getBalanceAcc();
                String commAcc = setting.getCommAcc();
                String disAcc = setting.getDiscountAcc();
                LocalDateTime vouDate = ph.getVouDate();
                String traderCode = ph.getTraderCode();
                String curCode = ph.getCurCode();
                String remark = ph.getRemark();
                boolean deleted = ph.isDeleted();
                double vouTotal = Util1.getDouble(ph.getVouTotal());
                double vouPaid = Util1.getDouble(ph.getPaid());
                double vouComm = Util1.getDouble(ph.getCommAmt());
                double vouCommP = Util1.getDouble(ph.getCommP());
                double vouDis = Util1.getDouble(ph.getDiscount());
                String vouNo = ph.getKey().getVouNo();
                String batchNo = ph.getBatchNo();
                String projectNo = ph.getProjectNo();
                Integer deptId = ph.getDeptId();
                TraderKey k = new TraderKey();
                k.setCode(traderCode);
                k.setCompCode(compCode);
                Trader t = traderService.findById(k);
                if (t != null) {
                    balAcc = Util1.isNull(t.getAccount(), balAcc);
                }
                balAcc = Util1.isNull(ph.getPayableAcc(), balAcc);
                List<Gl> listGl = new ArrayList<>();
                //income
                if (vouTotal > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Voucher Total");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setBatchNo(batchNo);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                //payment
                if (vouPaid > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    if (vouPaid == vouTotal) {
                        gl.setDescription("Purchase Voucher Full Paid");
                    } else {
                        gl.setDescription("Purchase Voucher Partial Paid");
                    }
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouPaid);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setBatchNo(batchNo);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                //discount
                if (vouDis > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Discount Received");
                    gl.setSrcAccCode(disAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouDis);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setBatchNo(batchNo);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                //comm
                if (vouComm > 0) {
                    String des = "Purchase Voucher Commission";
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription(vouCommP > 0 ? des + " : " + vouCommP + "%" : des);
                    gl.setSrcAccCode(commAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouComm);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setBatchNo(batchNo);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                List<PurExpense> listExp = purExpenseService.search(vouNo, compCode);
                for (PurExpense e : listExp) {
                    String expCode = e.getKey().getExpenseCode();
                    ExpenseKey ek = new ExpenseKey();
                    ek.setExpenseCode(expCode);
                    ek.setCompCode(compCode);
                    Expense expense = expenseService.findById(ek);
                    if (expense != null) {
                        String account = expense.getAccountCode();
                        double amt = Util1.getDouble(e.getAmount());
                        if (!Util1.isNullOrEmpty(account)) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setGlDate(vouDate);
                            //gl.setDescription("Purchase Voucher Paid : " + traderName);
                            gl.setSrcAccCode(account);
                            gl.setAccCode(balAcc);
                            gl.setCrAmt(amt);
                            gl.setTraderCode(traderCode);
                            gl.setCurCode(curCode);
                            gl.setReference(remark);
                            gl.setDeptCode(deptCode);
                            gl.setCreatedDate(LocalDateTime.now());
                            gl.setCreatedBy(appName);
                            gl.setTranSource(tranSource);
                            gl.setRefNo(vouNo);
                            gl.setDeleted(deleted);
                            gl.setMacId(macId);
                            gl.setBatchNo(batchNo);
                            gl.setProjectNo(projectNo);
                            listGl.add(gl);
                        }
                    }
                }
                sendAccount(listGl);
            }
        }
    }

    public void sendReturnIn(RetInHis ri) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            String tranSource = "RETURN_IN";
            String compCode = ri.getKey().getCompCode();
            String locCode = ri.getLocCode();
            AccSetting setting = settingService.findByCode(new AccKey(tranSource, compCode));
            if (setting != null) {
                LocationSetting ls = getLocationSetting(locCode, compCode);
                String payAcc = Util1.isNull(ls.getCashAcc(), setting.getPayAcc());
                String deptCode = Util1.isNull(ls.getDeptCode(), setting.getDeptCode());
                String srcAcc = setting.getSourceAcc();
                String balAcc = setting.getBalanceAcc();
                LocalDateTime vouDate = ri.getVouDate();
                String traderCode = ri.getTraderCode();
                String curCode = ri.getCurCode();
                String remark = ri.getRemark();
                boolean deleted = ri.isDeleted();
                String vouNo = ri.getKey().getVouNo();
                String projectNo = ri.getProjectNo();
                double vouTotal = Util1.getDouble(ri.getVouTotal());
                double vouPaid = Util1.getDouble(ri.getPaid());
                Integer deptId = ri.getDeptId();
                TraderKey k = new TraderKey();
                k.setCode(traderCode);
                k.setCompCode(compCode);
                Trader t = traderService.findById(k);
                String traderName = t == null ? "" : t.getTraderName();
                if (t != null) {
                    balAcc = Util1.isNull(t.getAccount(), balAcc);
                }
                List<Gl> listGl = new ArrayList<>();
                //income
                if (vouTotal > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return In Voucher Total");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                //discount
                //payment
                if (vouPaid > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return In Voucher Paid : " + traderName);
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouPaid);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                sendAccount(listGl);
            }
        }
    }

    public void sendReturnOut(RetOutHis ro) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            String tranSource = "RETURN_OUT";
            String compCode = ro.getKey().getCompCode();
            String locCode = ro.getLocCode();
            AccSetting setting = settingService.findByCode(new AccKey(tranSource, compCode));
            if (setting != null) {
                LocationSetting ls = getLocationSetting(locCode, compCode);
                String payAcc = Util1.isNull(ls.getCashAcc(), setting.getPayAcc());
                String deptCode = Util1.isNull(ls.getDeptCode(), setting.getDeptCode());
                String srcAcc = setting.getSourceAcc();
                String balAcc = setting.getBalanceAcc();
                LocalDateTime vouDate = ro.getVouDate();
                String traderCode = ro.getTraderCode();
                String curCode = ro.getCurCode();
                String remark = ro.getRemark();
                boolean deleted = ro.isDeleted();
                String vouNo = ro.getKey().getVouNo();
                String projectNo = ro.getProjectNo();
                double vouTotal = Util1.getDouble(ro.getVouTotal());
                double vouPaid = Util1.getDouble(ro.getPaid());
                Integer deptId = ro.getDeptId();
                TraderKey k = new TraderKey();
                k.setCode(traderCode);
                k.setCompCode(compCode);
                Trader t = traderService.findById(k);
                String traderName = t == null ? "" : t.getTraderName();
                if (t != null) {
                    balAcc = Util1.isNull(t.getAccount(), balAcc);
                }
                List<Gl> listGl = new ArrayList<>();
                //income
                if (vouTotal > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return Out Voucher Total");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                //discount
                //payment
                if (vouPaid > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    key.setDeptId(deptId);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return Out Voucher Paid : " + traderName);
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(vouPaid);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(LocalDateTime.now());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    gl.setProjectNo(projectNo);
                    listGl.add(gl);
                }
                sendAccount(listGl);
            }
        }
    }

    public void sendPayment(PaymentHis ph) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            if (ph != null) {
                String account = ph.getAccount();
                if (!Util1.isNullOrEmpty(account)) {
                    String compCode = ph.getKey().getCompCode();
                    Integer deptId = ph.getDeptId();
                    String traderCode = ph.getTraderCode();
                    LocalDateTime vouDate = ph.getVouDate();
                    double payAmt = ph.getAmount();
                    String curCode = ph.getCurCode();
                    String remark = ph.getRemark();
                    String vouNo = ph.getKey().getVouNo();
                    String tranOption = ph.getTranOption();
                    boolean deleted = ph.isDeleted();
                    String projectNo = ph.getProjectNo();
                    TraderKey k = new TraderKey();
                    k.setCode(traderCode);
                    k.setCompCode(compCode);
                    Trader t = traderService.findById(k);
                    AccSetting setting = settingService.findByCode(new AccKey("SALE", compCode));
                    if (!Util1.isNullOrEmpty(t.getAccount())) {
                        List<Gl> list = new ArrayList<>();
                        Gl gl = new Gl();
                        GlKey key = new GlKey();
                        key.setCompCode(compCode);
                        key.setDeptId(deptId);
                        gl.setKey(key);
                        gl.setGlDate(vouDate);
                        if (tranOption.equals("C")) {
                            gl.setDescription("Cash Received.");
                            gl.setSrcAccCode(account);
                            gl.setAccCode(t.getAccount());
                            gl.setTraderCode(traderCode);
                            gl.setDrAmt(payAmt);
                        } else if (tranOption.equals("S")) {
                            gl.setDescription("Cash Payment.");
                            gl.setSrcAccCode(account);
                            gl.setAccCode(t.getAccount());
                            gl.setTraderCode(traderCode);
                            gl.setCrAmt(payAmt);
                        }
                        gl.setCurCode(curCode);
                        gl.setReference(remark);
                        gl.setDeptCode(setting != null ? setting.getDeptCode() : null);
                        gl.setCreatedDate(LocalDateTime.now());
                        gl.setCreatedBy(appName);
                        gl.setTranSource("PAYMENT");
                        gl.setRefNo(vouNo);
                        gl.setDeleted(deleted);
                        gl.setMacId(macId);
                        gl.setProjectNo(projectNo);
                        list.add(gl);
                        sendAccount(list);
                    } else {
                        log.info(String.format("sendPayment : %s debtor account empty", traderCode));
                    }
                } else {
                    updatePayment(ph.getKey().getVouNo(), ph.getKey().getCompCode(), "NN");
                }
            }
        }
    }

    public void deleteInvVoucher(SaleHisKey key) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(key.getCompCode());
        gl.setKey(glKey);
        gl.setTranSource("SALE");
        gl.setRefNo(key.getVouNo());
        deleteGlByVoucher(gl);
    }


    public void deleteInvVoucher(PurHisKey key) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(key.getCompCode());
        gl.setKey(glKey);
        gl.setTranSource("PURCHASE");
        gl.setRefNo(key.getVouNo());
        deleteGlByVoucher(gl);
    }

    public void deleteInvVoucher(RetInHisKey key) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(key.getCompCode());
        gl.setKey(glKey);
        gl.setTranSource("RETURN_IN");
        gl.setRefNo(key.getVouNo());
        deleteGlByVoucher(gl);
    }

    public void deleteInvVoucher(RetOutHisKey key) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(key.getCompCode());
        gl.setKey(glKey);
        gl.setTranSource("RETURN_OUT");
        gl.setRefNo(key.getVouNo());
        deleteGlByVoucher(gl);
    }

    public void deleteInvVoucher(PaymentHisKey key) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(key.getCompCode());
        gl.setKey(glKey);
        gl.setTranSource("PAYMENT");
        gl.setRefNo(key.getVouNo());
        deleteGlByVoucher(gl);
    }


    private void deleteGlByVoucher(Gl gl) {
        accountApi.post()
                .uri("/account/deleteGlByVoucher")
                .body(Mono.just(gl), Gl.class)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(s -> {
                    String vouNo = gl.getRefNo();
                    String compCode = gl.getKey().getCompCode();
                    switch (gl.getTranSource()) {
                        case "SALE" -> updateSale(vouNo, compCode);
                        case "PURCHASE" -> updatePurchase(vouNo, compCode);
                        case "RETURN_IN" -> updateReturnIn(vouNo, compCode);
                        case "RETURN_OUT" -> updateReturnOut(vouNo, compCode);
                        case "PAYMENT" -> updatePayment(vouNo, compCode, ACK);
                    }
                }, (e) -> {
                    String vouNo = gl.getRefNo();
                    String compCode = gl.getKey().getCompCode();
                    String tranSource = gl.getTranSource();
                    switch (tranSource) {
                        case "SALE" -> updateSaleNull(vouNo, compCode);
                        case "PURCHASE" -> updatePurchaseNull(vouNo, compCode);
                        case "RETURN_IN" -> updateReturnInNull(vouNo, compCode);
                        case "RETURN_OUT" -> updateReturnOutNull(vouNo, compCode);
                        case "PAYMENT" -> updatePaymentNull(vouNo, compCode);
                    }
                    log.error("deleteGlByVoucher : " + e.getMessage());
                });
    }

    private LocationSetting getLocationSetting(String locCode, String compCode) {
        LocationKey key = new LocationKey();
        key.setCompCode(compCode);
        key.setLocCode(locCode);
        Location location = locationService.findByCode(key);
        if (location != null) {
            return LocationSetting.builder().cashAcc(location.getCashAcc()).deptCode(location.getDeptCode()).build();
        }
        return LocationSetting.builder().build();
    }
}
