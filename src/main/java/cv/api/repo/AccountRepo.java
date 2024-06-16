package cv.api.repo;

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
    private final Environment environment;
    private final TraderService traderService;
    private final PurExpenseService purExpenseService;
    private final SaleExpenseService saleExpenseService;
    private final LabourPaymentService labourPaymentService;
    private final PaymentHisService paymentHisService;
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
                            String tranSource = response.getTranSource();
                            updateAck(ACK, tranSource, vouNo, compCode);
                        }
                    }).onErrorResume(e -> {
                        Gl gl = glList.getFirst();
                        String vouNo = gl.getRefNo();
                        String compCode = gl.getKey().getCompCode();
                        String tranSource = gl.getTranSource();
                        updateAck(null, tranSource, vouNo, compCode);
                        log.error(e.getMessage());
                        return Mono.empty();
                    }).subscribe();
        }
    }

    private void updateAck(String ack, String tranSource, String vouNo, String compCode) {
        switch (tranSource) {
            case "SALE" -> updateSale(ack, vouNo, compCode);
            case "PURCHASE" -> updatePurchase(ack, vouNo, compCode);
            case "RETURN_IN" -> updateReturnIn(ack, vouNo, compCode);
            case "RETURN_OUT" -> updateReturnOut(ack, vouNo, compCode);
            case "PAYMENT" -> updatePayment(ack, vouNo, compCode);
            case "LABOUR_PAYMENT" -> updateLabourPayment(ack, vouNo, compCode);
        }
    }


    private void updateSale(String ack, String vouNo, String compCode) {
        saleHisService.updateACK(ack, vouNo, compCode).subscribe();
    }

    private void updatePurchase(String ack, String vouNo, String compCode) {
        purHisService.updateACK(ack, vouNo, compCode).subscribe();
    }

    private void updateReturnIn(String ack, String vouNo, String compCode) {
        retInService.updateACK(ack, vouNo, compCode).subscribe();
    }

    private void updateReturnOut(String ack, String vouNo, String compCode) {
        retOutService.updateACK(ack, vouNo, compCode).subscribe();
    }


    private void updateTrader(String traderCode, String account, String compCode) {
        traderService.updateACK(traderCode, account, compCode).subscribe();
    }

    private void updatePayment(String ack, String vouNo, String compCode) {
        paymentHisService.updateACK(ack, vouNo, compCode).subscribe();
    }

    private void updateLabourPayment(String ack, String vouNo, String compCode) {
        labourPaymentService.updateACK(ack, vouNo, compCode).subscribe();
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
                    log.error("saveTrader :{}", e.getMessage());
                    return Mono.empty();
                }).doOnSuccess(response -> {
                    if (response != null) {
                        updateTrader(response.getKey().getCode(), response.getAccount(), response.getKey().getCompCode());
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
                .subscribe((t) -> log.info("deleted."), (e) -> log.error("deleteTrader : {}", e.getMessage()));
    }

    public Mono<Void> sendSaleAsync(SaleHis obj) {
        if (isIntegrate()) {
            String vouNo = obj.getKey().getVouNo();
            String compCode = obj.getKey().getCompCode();
            if (Util1.getBoolean(obj.getDeleted())) {
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
                        String remark = Util1.isNull(sh.getRemark(), "");
                        String ref = Util1.isNull(sh.getReference(), "");
                        String reference = String.format("%s : %s", remark, ref);
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
                            gl.setReference(reference);
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
                            gl.setReference(reference);
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
                            gl.setReference(reference);
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
                            gl.setReference(reference);
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
                            gl.setReference(reference);
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
                                .flatMap(e -> {
                                    String account = e.getAccount();
                                    double amt = e.getAmount();
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
                                        gl.setDescription(e.getExpenseName());
                                        gl.setReference(reference);
                                        gl.setDeptCode(deptCode);
                                        gl.setCreatedDate(LocalDateTime.now());
                                        gl.setCreatedBy(appName);
                                        gl.setTranSource(tranSource);
                                        gl.setRefNo(vouNo);
                                        gl.setDeleted(deleted);
                                        gl.setMacId(macId);
                                        gl.setBatchNo(batchNo);
                                        gl.setProjectNo(projectNo);
                                        return Mono.just(gl);
                                    } else {
                                        return Mono.empty();
                                    }
                                })
                                .collectList()
                                .map(listExp -> {
                                    listGl.addAll(listExp);
                                    return listGl;
                                })
                                .switchIfEmpty(Mono.just(listGl));
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
            if (Util1.getBoolean(obj.getDeleted())) {
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
                                .flatMap(e -> {
                                    String expCode = e.getKey().getExpenseCode();
                                    ExpenseKey ek = ExpenseKey.builder().build();
                                    ek.setExpenseCode(expCode);
                                    ek.setCompCode(compCode);
                                    String account = e.getAccount();
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
                                        gl.setDescription(e.getExpenseName());
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
                                        return Mono.just(gl);
                                    } else {
                                        return Mono.empty();
                                    }
                                })
                                .collectList().map(listExp -> {
                                    listGl.addAll(listExp);
                                    return listGl;
                                }).switchIfEmpty(Mono.just(listGl));
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
            if (Util1.getBoolean(obj.getDeleted())) {
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
                            gl.setAccCode(disAcc);
                            gl.setCrAmt(vouDis);
                            gl.setTraderCode(traderCode);
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
            if (Util1.getBoolean(obj.getDeleted())) {
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
                            gl.setAccCode(disAcc);
                            gl.setTraderCode(traderCode);
                            gl.setDrAmt(vouDis);
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

    public Mono<Void> sendPayment(PaymentHis obj) {
        if (isIntegrate()) {
            String vouNo = obj.getVouNo();
            String compCode = obj.getCompCode();
            String tranSource = vouNo.startsWith("C") ? "RECEIVE" : "PAYMENT";
            if (Util1.getBoolean(obj.getDeleted())) {
                deletePayment(vouNo, compCode,tranSource);
                return Mono.empty();
            }
            return paymentHisService.generateForAcc(vouNo, compCode)
                    .map(ph -> {
                        String account = ph.getAccount();
                        List<Gl> list = new ArrayList<>();
                        if (!Util1.isNullOrEmpty(account)) {
                            Integer deptId = ph.getDeptId();
                            String traderCode = ph.getTraderCode();
                            LocalDateTime vouDate = ph.getVouDate();
                            double payAmt = ph.getAmount();
                            String curCode = ph.getCurCode();
                            String remark = ph.getRemark();
                            String tranOption = ph.getTranOption();
                            boolean deleted = ph.getDeleted();
                            String projectNo = ph.getProjectNo();
                            String deptCode = ph.getDeptCode();
                            String debtorAcc = ph.getDebtorAcc();
                            if (!Util1.isNullOrEmpty(debtorAcc)) {
                                Gl gl = new Gl();
                                GlKey key = new GlKey();
                                key.setCompCode(compCode);
                                key.setDeptId(deptId);
                                gl.setKey(key);
                                gl.setGlDate(vouDate);
                                if (tranOption.equals("C")) {
                                    gl.setDescription("Cash Received.");
                                    gl.setSrcAccCode(account);
                                    gl.setAccCode(debtorAcc);
                                    gl.setTraderCode(traderCode);
                                    gl.setDrAmt(payAmt);
                                } else if (tranOption.equals("S")) {
                                    gl.setDescription("Cash Payment.");
                                    gl.setSrcAccCode(account);
                                    gl.setAccCode(debtorAcc);
                                    gl.setTraderCode(traderCode);
                                    gl.setCrAmt(payAmt);
                                }
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
                                list.add(gl);
                            } else {
                                log.info(String.format("sendPayment : %s debtor account empty", traderCode));
                            }
                        } else {
                            updatePayment(ph.getVouNo(), ph.getCompCode(), "NN");
                        }
                        return list;
                    }).doOnNext(this::sendAccount).then();
        } else {
            return Mono.empty();
        }
    }


    public Mono<Void> sendLabourPayment(LabourPaymentDto obj) {
        if (isIntegrate()) {
            String sourceAcc = obj.getSourceAcc();
            String compCode = obj.getCompCode();
            String vouNo = obj.getVouNo();
            if (Util1.getBoolean(obj.getDeleted())) {
                deleteLabourPayment(vouNo, compCode);
                return Mono.empty();
            }
            if (!Util1.isNullOrEmpty(sourceAcc) && obj.getPost()) {
                Integer deptId = obj.getDeptId();
                LocalDateTime vouDate = obj.getVouDate();
                String curCode = obj.getCurCode();
                String remark = obj.getRemark();
                boolean deleted = Util1.getBoolean(obj.getDeleted());
                return labourPaymentService.getDetail(vouNo, compCode)
                        .flatMap(detail -> {
                            Gl gl = new Gl();
                            GlKey key = new GlKey();
                            key.setCompCode(compCode);
                            key.setDeptId(deptId);
                            gl.setKey(key);
                            gl.setGlDate(vouDate);
                            gl.setDescription(detail.getDescription());
                            gl.setSrcAccCode(sourceAcc);
                            gl.setAccCode(Util1.isNull(detail.getAccount(), obj.getExpenseAcc()));
                            gl.setCrAmt(detail.getAmount());
                            gl.setCurCode(curCode);
                            gl.setReference(remark);
                            gl.setDeptCode(Util1.isNull(detail.getDeptCode(), obj.getDeptCode()));
                            gl.setCreatedDate(LocalDateTime.now());
                            gl.setCreatedBy(appName);
                            gl.setTranSource("LABOUR_PAYMENT");
                            gl.setRefNo(vouNo);
                            gl.setDeleted(deleted);
                            gl.setMacId(macId);
                            return Mono.just(gl);
                        }).collectList()
                        .doOnNext(this::sendAccount).then();
            } else {
                updateLabourPayment("IGNORE", vouNo, compCode);
                return Mono.empty();
            }
        } else {
            return Mono.empty();
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

    public void deletePayment(String vouNo, String compCode, String tranSource) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(compCode);
        gl.setKey(glKey);
        gl.setTranSource(tranSource);
        gl.setRefNo(vouNo);
        deleteGlByVoucher(gl);
    }

    public void deleteLabourPayment(String vouNo, String compCode) {
        Gl gl = new Gl();
        GlKey glKey = new GlKey();
        glKey.setCompCode(compCode);
        gl.setKey(glKey);
        gl.setTranSource("LABOUR_PAYMENT");
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
                    String tranSource = gl.getTranSource();
                    updateAck(ACK, tranSource, vouNo, compCode);
                }).doOnError(e -> {
                    String vouNo = gl.getRefNo();
                    String compCode = gl.getKey().getCompCode();
                    String tranSource = gl.getTranSource();
                    updateAck(null, tranSource, vouNo, compCode);
                    log.error("deleteGlByVoucher : {}", e.getMessage());
                }).subscribe();
    }


}
