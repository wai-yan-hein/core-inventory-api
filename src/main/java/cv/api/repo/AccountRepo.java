package cv.api.repo;

import cv.api.common.Message;
import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.dto.LabourPaymentDto;
import cv.api.entity.*;
import cv.api.model.*;
import cv.api.service.*;
import cv.api.user.SystemPropertyDto;
import cv.api.user.SystemPropertyKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountRepo {
    private final String ACK = "ACK";
    private final String appName = "SM";
    private final Integer macId = 98;
    private final WebClient accountApi;
    private final ReportService reportService;
    private final Environment environment;
    private final TraderService traderService;
    private final PurExpenseService purExpenseService;
    private final ExpenseService expenseService;
    private final AccSettingService settingService;
    private final SaleExpenseService saleExpenseService;
    private final LabourPaymentService labourPaymentService;
    private final UserRepo userRepo;
    private final SaleHisService saleHisService;
    private final PurHisService purHisService;
    private final RetInService retInService;
    private final RetOutService retOutService;

    private boolean isIntegrate() {
        return Util1.getBoolean(environment.getProperty("integration"));
    }

    private void sendAccount(List<Gl> glList) {
        if (!glList.isEmpty()) {
            accountApi.post().uri("/account/saveGlList")
                    .body(Mono.just(glList), List.class)
                    .retrieve()
                    .bodyToMono(Response.class)
                    .doOnSuccess(response -> {
                        if (response != null) {
                            String vouNo = response.getVouNo();
                            String compCode = response.getCompCode();
                            switch (response.getTranSource()) {
                                case "SALE" -> updateSale(vouNo, compCode);
                                case "PURCHASE" -> updatePurchase(vouNo, compCode);
                                case "RETURN_IN" -> updateReturnIn(vouNo, compCode);
                                case "RETURN_OUT" -> updateReturnOut(vouNo, compCode);
                                case "PAYMENT" -> updatePayment(vouNo, compCode, ACK);
                                case "LABOUR_PAYMENT" -> updateLabourPayment(vouNo, compCode, ACK);
                            }
                        }
                    }).onErrorResume(e -> {
                        Gl gl = glList.getFirst();
                        String vouNo = gl.getRefNo();
                        String compCode = gl.getKey().getCompCode();
                        String tranSource = gl.getTranSource();
                        switch (tranSource) {
                            case "SALE" -> updateSaleNull(vouNo, compCode);
                            case "PURCHASE" -> updatePurchaseNull(vouNo, compCode);
                            case "RETURN_IN" -> updateReturnInNull(vouNo, compCode);
                            case "RETURN_OUT" -> updateReturnOutNull(vouNo, compCode);
                            case "PAYMENT" -> updatePaymentNull(vouNo, compCode);
                            case "LABOUR_PAYMENT" -> updateLabourPayment(vouNo, compCode, null);
                        }
                        log.error(e.getMessage());
                        return Mono.empty();
                    }).subscribe();
        }
    }

    public Mono<String> sendDownloadMessage(String entity, String message) {
        Message mg = new Message();
        mg.setHeader("DOWNLOAD");
        mg.setEntity(entity);
        mg.setMessage(message);
        return accountApi.post()
                .uri("/message/send")
                .body(Mono.just(mg), Message.class)
                .retrieve()
                .bodyToMono(String.class);
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

    private void updateLabourPayment(String vouNo, String compCode, String status) {
        labourPaymentService.update(vouNo, compCode, status).doOnSuccess(update -> {
            if (update) {
                log.info("updateLabourPayment : " + vouNo);
            }
        }).subscribe();

    }


    public void sendTrader(Trader t) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            if (t != null) {
                String traderType = t.getType();
                AccTrader trader = new AccTrader();
                AccTraderKey key = new AccTraderKey();
                key.setCode(t.getKey().getCode());
                key.setCompCode(t.getKey().getCompCode());
                trader.setKey(key);
                trader.setTraderName(t.getTraderName());
                trader.setUserCode(t.getUserCode());
                trader.setActive(t.getActive());
                trader.setAppName(appName);
                trader.setMacId(macId);
                trader.setAccount(t.getAccount());
                trader.setDeleted(t.getDeleted());
                trader.setCreatedDate(t.getCreatedDate());
                trader.setCreatedBy(t.getCreatedBy());
                trader.setRegCode(t.getRegCode());
                trader.setPhone(t.getPhone());
                trader.setAddress(t.getAddress());
                trader.setEmail(t.getEmail());
                switch (traderType) {
                    case "CUS" -> trader.setTraderType("C");
                    case "SUP" -> trader.setTraderType("S");
                    default -> trader.setTraderType("D");
                }
                String account = t.getAccount();
                String compCode = t.getKey().getCompCode();
                if (account == null) {
                    getAccount(compCode).flatMap(acc -> {
                        trader.setAccount(acc);
                        return saveTrader(trader);
                    });
                } else {
                    saveTrader(trader).subscribe();
                }
            }
        }
    }

    private Mono<AccTrader> saveTrader(AccTrader t) {
        return accountApi.post().uri("/account/saveTrader")
                .body(Mono.just(t), AccTrader.class)
                .retrieve().bodyToMono(AccTrader.class)
                .onErrorResume(e -> {
                    log.error("saveTrader :" + e.getMessage());
                    return Mono.empty();
                }).doOnSuccess(response -> {
                    if (response != null) {
                        updateTrader(response.getKey().getCode(), response.getAccount(), response.getKey().getCompCode());
                        sendDownloadMessage("TRADER_ACC", response.getTraderName()).subscribe();
                    }
                });
    }

    private Mono<String> getAccount(String compCode) {
        String DEBTOR_ACC = "debtor.account";
        var key = SystemPropertyKey.builder()
                .propKey(DEBTOR_ACC)
                .compCode(compCode)
                .build();
        return userRepo.findSystemProperty(key).map(SystemPropertyDto::getPropValue);
    }

    public void deleteTrader(AccTraderKey key) {
        accountApi.post().uri("/account/deleteTrader")
                .body(Mono.just(key), AccTraderKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class)
                .subscribe((t) -> log.info("deleted."), (e) -> log.error("deleteTrader : " + e.getMessage()));
    }

    public Mono<Void> sendSaleAsync(SaleHis obj) {
        if (isIntegrate()) {
            String vouNo = obj.getKey().getVouNo();
            String compCode = obj.getKey().getCompCode();
            if (obj.getDeleted()) {
                deleteInvVoucher(obj.getKey());
                return Mono.empty();
            }
            return saleHisService.generateForAcc(vouNo, compCode)
                    .flatMap(sh -> {
                        String tranSource = "SALE";
                        String payAcc = sh.getCashAcc();
                        String deptCode = sh.getDeptCode();
                        String srcAcc = sh.getSaleAcc();
                        String balAcc = sh.getDebtorAcc();
                        String disAcc = sh.getDisAcc();
                        String taxAcc = sh.getTaxAcc();
                        LocalDateTime vouDate = sh.getVouDate();
                        String curCode = sh.getCurCode();
                        String traderCode = sh.getTraderCode();
                        String batchNo = sh.getGrnVouNo();
                        String remark = Util1.isNull(sh.getReference(), sh.getRemark());
                        boolean deleted = sh.getDeleted();
                        String projectNo = sh.getProjectNo();
                        double vouTotal = Util1.getDouble(sh.getVouTotal());
                        double vouDis = Util1.getDouble(sh.getDiscount());
                        double vouPaid = Util1.getDouble(sh.getPaid());
                        double vouTax = Util1.getDouble(sh.getTaxAmt());
                        double taxPercent = Util1.getDouble(sh.getTaxPercent());
                        double totalPayment = Util1.getDouble(sh.getTotalPayment());
                        Integer deptId = sh.getDeptId();
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
                            gl.setBatchNo(batchNo);
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
                            gl.setBatchNo(batchNo);
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
                            gl.setBatchNo(batchNo);
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
                            gl.setBatchNo(batchNo);
                            listGl.add(gl);
                        }
                        if (totalPayment > 0) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setGlDate(vouDate);
                            gl.setDescription("Sale Voucher Received");
                            gl.setSrcAccCode(payAcc);
                            gl.setAccCode(balAcc);
                            gl.setTraderCode(traderCode);
                            gl.setDrAmt(totalPayment);
                            gl.setCurCode(curCode);
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
                            gl.setBatchNo(batchNo);
                            listGl.add(gl);
                        }
                        return saleExpenseService.search(vouNo, compCode)
                                .collectList()
                                .map(list -> {
                                    for (SaleExpense e : list) {
                                        String expCode = e.getKey().getExpenseCode();
                                        ExpenseKey ek = ExpenseKey.builder().build();
                                        ek.setExpenseCode(expCode);
                                        ek.setCompCode(compCode);
                                        Expense expense = expenseService.findById(ek).block();
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
                                    return listGl;
                                });
                    }).doOnNext(this::sendAccount).then();
        } else {
            return Mono.empty();
        }
    }

    public Mono<Void> sendPurchaseAsync(PurHis obj) {
        if (isIntegrate()) {
            String vouNo = obj.getKey().getVouNo();
            String compCode = obj.getKey().getCompCode();
            String tranSource = "PURCHASE";
            if (obj.getDeleted()) {
                deleteInvVoucher(obj.getKey());
                return Mono.empty();
            }
            return purHisService.generateForAcc(vouNo, compCode)
                    .flatMap(ph -> {
                        String payAcc = ph.getCashAcc();
                        String deptCode = ph.getDeptCode();
                        String srcAcc = ph.getPurchaseAcc();
                        String balAcc = ph.getPayableAcc();
                        String commAcc = ph.getCommAcc();
                        String disAcc = ph.getDisAcc();
                        LocalDateTime vouDate = ph.getVouDate();
                        String curCode = ph.getCurCode();
                        String remark = ph.getRemark();
                        String traderCode = ph.getTraderCode();
                        boolean deleted = ph.getDeleted();
                        double vouTotal = Util1.getDouble(ph.getVouTotal());
                        double grandTotal = Util1.getDouble(ph.getGrandTotal());
                        double vouPaid = Util1.getDouble(ph.getPaid());
                        double vouComm = Util1.getDouble(ph.getCommAmt());
                        double vouCommP = Util1.getDouble(ph.getCommP());
                        double vouDis = Util1.getDouble(ph.getDiscount());
                        String batchNo = ph.getBatchNo();
                        String projectNo = ph.getProjectNo();
                        Integer deptId = ph.getDeptId();
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
                            if (vouPaid == grandTotal) {
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
                        return purExpenseService.search(vouNo, compCode)
                                .collectList()
                                .map(list -> {
                                    for (PurExpense e : list) {
                                        String expCode = e.getKey().getExpenseCode();
                                        ExpenseKey ek = ExpenseKey.builder().build();
                                        ek.setExpenseCode(expCode);
                                        ek.setCompCode(compCode);
                                        Expense expense = expenseService.findById(ek).block();
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
                                                gl.setCrAmt(amt);
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
                                    return listGl;
                                });
                    }).doOnNext(this::sendAccount).then();
        } else {
            return Mono.empty();
        }
    }

    public Mono<Void> sendReturnInSync(RetInHis obj) {
        if (isIntegrate()) {
            String tranSource = "RETURN_IN";
            String vouNo = obj.getKey().getVouNo();
            String compCode = obj.getKey().getCompCode();
            if (obj.getDeleted()) {
                deleteInvVoucher(obj.getKey());
                return Mono.empty();
            }
            return retInService.generateForAcc(vouNo, compCode)
                    .map(ri -> {
                        String payAcc = ri.getCashAcc();
                        String deptCode = ri.getDeptCode();
                        String srcAcc = ri.getSrcAcc();
                        String balAcc = ri.getDebtorAcc();
                        String taxAcc = ri.getTaxAcc();
                        String disAcc = ri.getDisAcc();
                        String traderCode = ri.getTraderCode();
                        LocalDateTime vouDate = ri.getVouDate();
                        String curCode = ri.getCurCode();
                        String remark = ri.getRemark();
                        boolean deleted = ri.getDeleted();
                        String projectNo = ri.getProjectNo();
                        double vouTotal = Util1.getDouble(ri.getVouTotal());
                        double vouPaid = Util1.getDouble(ri.getPaid());
                        double vouDis = Util1.getDouble(ri.getDiscount());
                        double vouDisPercent = Util1.getDouble(ri.getDiscP());
                        double vouTax = Util1.getDouble(ri.getTaxAmt());
                        double vouTaxPercent = Util1.getDouble(ri.getTaxP());
                        Integer deptId = ri.getDeptId();
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

                        //payment
                        if (vouPaid > 0) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setGlDate(vouDate);
                            gl.setDescription("Return In Voucher Paid");
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

                        //discount
                        if (vouDis > 0) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setSrcAccCode(balAcc);
                            gl.setTraderCode(traderCode);
                            gl.setDrAmt(vouDis);
                            gl.setAccCode(disAcc);
                            gl.setGlDate(vouDate);
                            gl.setDescription(String.format("Return In Voucher Discount (%s)", vouDisPercent));
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
                        if (vouTax > 0) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setSrcAccCode(balAcc);
                            gl.setTraderCode(traderCode);
                            gl.setAccCode(taxAcc);
                            gl.setCrAmt(vouTax);
                            gl.setGlDate(vouDate);
                            gl.setDescription(String.format("Return In Voucher Tax (%s)", vouTaxPercent));
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
                        return listGl;
                    }).doOnNext(this::sendAccount).then();
        } else {
            return Mono.empty();
        }
    }


    public Mono<Void> sendReturnOutSync(RetOutHis obj) {
        if (isIntegrate()) {
            String tranSource = "RETURN_OUT";
            String vouNo = obj.getKey().getVouNo();
            String compCode = obj.getKey().getCompCode();
            if (obj.getDeleted()) {
                deleteInvVoucher(obj.getKey());
                return Mono.empty();
            }
            return retOutService.generateForAcc(vouNo, compCode)
                    .map(ri -> {
                        String payAcc = ri.getCashAcc();
                        String deptCode = ri.getDeptCode();
                        String srcAcc = ri.getSrcAcc();
                        String balAcc = ri.getPayableAcc();
                        String taxAcc = ri.getTaxAcc();
                        String disAcc = ri.getDisAcc();
                        String traderCode = ri.getTraderCode();
                        LocalDateTime vouDate = ri.getVouDate();
                        String curCode = ri.getCurCode();
                        String remark = ri.getRemark();
                        boolean deleted = ri.getDeleted();
                        String projectNo = ri.getProjectNo();
                        double vouTotal = Util1.getDouble(ri.getVouTotal());
                        double vouPaid = Util1.getDouble(ri.getPaid());
                        double vouDis = Util1.getDouble(ri.getDiscount());
                        double vouDisPercent = Util1.getDouble(ri.getDiscP());
                        double vouTax = Util1.getDouble(ri.getTaxAmt());
                        double vouTaxPercent = Util1.getDouble(ri.getTaxP());
                        Integer deptId = ri.getDeptId();
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

                        //payment
                        if (vouPaid > 0) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setGlDate(vouDate);
                            gl.setDescription("Return Out Voucher Paid");
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

                        //discount
                        if (vouDis > 0) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setSrcAccCode(balAcc);
                            gl.setTraderCode(traderCode);
                            gl.setDrAmt(vouDis);
                            gl.setAccCode(disAcc);
                            gl.setGlDate(vouDate);
                            gl.setDescription(String.format("Return Out Voucher Discount (%s)", vouDisPercent));
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
                        if (vouTax > 0) {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setSrcAccCode(balAcc);
                            gl.setTraderCode(traderCode);
                            gl.setAccCode(taxAcc);
                            gl.setCrAmt(vouTax);
                            gl.setGlDate(vouDate);
                            gl.setDescription(String.format("Return Out Voucher Tax (%s)", vouTaxPercent));
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
                        return listGl;
                    }).doOnNext(this::sendAccount).then();
        } else {
            return Mono.empty();
        }
    }

    public void sendPayment(PaymentHis ph) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            if (ph != null) {
                String account = ph.getAccount();
                if (!Util1.isNullOrEmpty(account)) {
                    String compCode = ph.getCompCode();
                    Integer deptId = ph.getDeptId();
                    String traderCode = ph.getTraderCode();
                    LocalDateTime vouDate = ph.getVouDate();
                    double payAmt = ph.getAmount();
                    String curCode = ph.getCurCode();
                    String remark = ph.getRemark();
                    String vouNo = ph.getVouNo();
                    String tranOption = ph.getTranOption();
                    boolean deleted = ph.getDeleted();
                    String projectNo = ph.getProjectNo();
                    TraderKey k = TraderKey.builder().build();
                    k.setCode(traderCode);
                    k.setCompCode(compCode);
                    traderService.findById(k).doOnNext(t -> {
                        AccSetting setting = settingService.findByCode(new AccKey("SALE", compCode)).block();
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
                    }).subscribe();
                } else {
                    updatePayment(ph.getVouNo(), ph.getCompCode(), "NN");
                }
            }
        }
    }

    public void sendLabourPayment(LabourPaymentDto ph) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            if (ph != null) {
                String sourceAcc = ph.getSourceAcc();
                String compCode = ph.getCompCode();
                String vouNo = ph.getVouNo();
                if (!Util1.isNullOrEmpty(sourceAcc) && ph.getPost()) {
                    Integer deptId = ph.getDeptId();
                    LocalDateTime vouDate = ph.getVouDate();
                    String curCode = ph.getCurCode();
                    String remark = ph.getRemark();
                    boolean deleted = ph.getDeleted();
                    labourPaymentService.getDetail(vouNo, compCode)
                            .flatMap(detail -> {
                                Gl gl = new Gl();
                                GlKey key = new GlKey();
                                key.setCompCode(compCode);
                                key.setDeptId(deptId);
                                gl.setKey(key);
                                gl.setGlDate(vouDate);
                                gl.setDescription(detail.getDescription());
                                gl.setSrcAccCode(sourceAcc);
                                gl.setAccCode(Util1.isNull(detail.getAccount(), ph.getExpenseAcc()));
                                gl.setCrAmt(detail.getAmount());
                                gl.setCurCode(curCode);
                                gl.setReference(remark);
                                gl.setDeptCode(Util1.isNull(detail.getDeptCode(), ph.getDeptCode()));
                                gl.setCreatedDate(LocalDateTime.now());
                                gl.setCreatedBy(appName);
                                gl.setTranSource("LABOUR_PAYMENT");
                                gl.setRefNo(vouNo);
                                gl.setDeleted(deleted);
                                gl.setMacId(macId);
                                return Mono.just(gl);
                            }).collectList()
                            .subscribe(this::sendAccount);


                } else {
                    updateLabourPayment(vouNo, compCode, "IGNORE");
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

    public void deleteVoucher(String vouNo, String compCode, String tranSource) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(compCode);
        gl.setKey(glKey);
        gl.setTranSource(tranSource);
        gl.setRefNo(vouNo);
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

    public void deleteInvVoucher(String vouNo, String compCode) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(compCode);
        gl.setKey(glKey);
        gl.setTranSource("PAYMENT");
        gl.setRefNo(vouNo);
        deleteGlByVoucher(gl);
    }


    private void deleteGlByVoucher(Gl gl) {
        accountApi.post()
                .uri("/account/deleteGlByVoucher")
                .body(Mono.just(gl), Gl.class)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(s -> {
                    String vouNo = gl.getRefNo();
                    String compCode = gl.getKey().getCompCode();
                    switch (gl.getTranSource()) {
                        case "SALE" -> updateSale(vouNo, compCode);
                        case "PURCHASE" -> updatePurchase(vouNo, compCode);
                        case "RETURN_IN" -> updateReturnIn(vouNo, compCode);
                        case "RETURN_OUT" -> updateReturnOut(vouNo, compCode);
                        case "PAYMENT" -> updatePayment(vouNo, compCode, ACK);
                    }
                }).doOnError(e -> {
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
                }).subscribe();
    }


}
