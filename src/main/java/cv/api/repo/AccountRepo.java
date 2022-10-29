package cv.api.repo;

import cv.api.common.ReturnObject;
import cv.api.common.Util1;
import cv.api.inv.entity.*;
import cv.api.inv.service.ReportService;
import cv.api.inv.service.TraderService;
import cv.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Component
@Slf4j
public class AccountRepo {
    private final String ACK = "ACK";
    private final String appName = "SM";
    private final Integer macId = 98;
    @Autowired
    private WebClient accountApi;
    @Autowired
    private HashMap<String, AccSetting> hmAccSetting;
    @Autowired
    private ReportService reportService;
    @Autowired
    private Environment environment;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TraderService traderService;

    private void sendAccount(List<Gl> glList) {
        if (!glList.isEmpty()) {
            Mono<Response> result = accountApi.post().uri("/account/save-gl-list").body(Mono.just(glList), List.class).retrieve().bodyToMono(Response.class);
            result.subscribe(response -> {
                if (response != null) {
                    String vouNo = response.getVouNo();
                    String compCode = response.getCompCode();
                    switch (response.getTranSource()) {
                        case "SALE" -> updateSale(vouNo, compCode, ACK);
                        case "PURCHASE" -> updatePurchase(vouNo, compCode, ACK);
                        case "RETURN_IN" -> updateReturnIn(vouNo, compCode, ACK);
                        case "RETURN_OUT" -> updateReturnOut(vouNo, compCode, ACK);
                    }
                }
            }, (e) -> {
                Gl gl = glList.get(0);
                String vouNo = gl.getRefNo();
                String compCode = gl.getKey().getCompCode();
                String tranSource = gl.getTranSource();
                switch (tranSource) {
                    case "SALE" -> updateSale(vouNo, compCode, null);
                    case "PURCHASE" -> updatePurchase(vouNo, compCode, null);
                    case "RETURN_IN" -> updateReturnIn(vouNo, compCode, null);
                    case "RETURN_OUT" -> updateReturnOut(vouNo, compCode, null);
                }
                throw new IllegalStateException(e.getMessage());
            });
        }
    }

    private void updateSale(String vouNo, String compCode, String status) {
        String sql = "update sale_his set intg_upd_status = '" + status + "' where vou_no ='" + vouNo + "' and '" + compCode + "'";
        try {
            reportService.executeSql(sql);
            log.error(String.format("updateSale: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updateSale: %s", e.getMessage()));
        }
    }

    private void updatePurchase(String vouNo, String compCode, String status) {
        String sql = "update pur_his set intg_upd_status = '" + status + "' where vou_no ='" + vouNo + "' and '" + compCode + "'";
        try {
            reportService.executeSql(sql);
            log.error(String.format("updatePurchase: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updatePurchase: %s", e.getMessage()));
        }
    }

    private void updateReturnIn(String vouNo, String compCode, String status) {
        String sql = "update ret_in_his set intg_upd_status = '" + status + "' where vou_no ='" + vouNo + "' and '" + compCode + "'";
        try {
            reportService.executeSql(sql);
            log.info(String.format("updateReturnIn: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updateReturnIn: %s", e.getMessage()));
        }
    }

    private void updateReturnOut(String vouNo, String compCode, String status) {
        String sql = "update ret_out_his set intg_upd_status = '" + status + "' where vou_no ='" + vouNo + "' and '" + compCode + "'";
        try {
            reportService.executeSql(sql);
            log.info(String.format("updateReturnOut: %s", vouNo));
        } catch (Exception e) {
            log.error(String.format("updateReturnOut: %s", e.getMessage()));
        }
    }

    private void updateTrader(String traderCode, String account, String compCode) {
        String sql = "update trader set intg_upd_status = '" + "ACK" + "',account = '" + account + "' where code ='" + traderCode + "' and '" + compCode + "'";
        try {
            reportService.executeSql(sql);
            log.info(String.format("updateTrader: %s", traderCode));
        } catch (Exception e) {
            log.error(String.format("updateTrader: %s", e.getMessage()));
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
                accTrader.setActive(true);
                accTrader.setAppName(appName);
                accTrader.setMacId(macId);

                switch (traderType) {
                    case "CUS" -> {
                        accTrader.setTraderType("C");
                        accTrader.setAccCode(Util1.isNull(t.getAccount(), getCustomerAcc(key.getCompCode())));
                    }
                    case "SUP" -> {
                        accTrader.setAccCode(Util1.isNull(t.getAccount(), getSupplierAcc(key.getCompCode())));
                        accTrader.setTraderType("S");
                    }
                    default -> accTrader.setTraderType("D");
                }
                Mono<AccTrader> result = accountApi.post().uri("/account/save-trader").body(Mono.just(accTrader), AccTrader.class).retrieve().bodyToMono(AccTrader.class).doOnError((e) -> log.error(e.getMessage()));
                AccTrader trader = result.block();
                assert trader != null;
                updateTrader(trader.getKey().getCode(), trader.getAccCode(), trader.getKey().getCompCode());

            }
        }
    }

    public void deleteTrader(AccTraderKey key) {
        Mono<ReturnObject> result = accountApi.post().uri("/account/delete-trader")
                .body(Mono.just(key), AccTraderKey.class)
                .retrieve().bodyToMono(ReturnObject.class)
                .doOnError((e) -> log.error(e.getMessage()));
        result.block();
    }

    private String getCustomerAcc(String compCode) {
        SystemProperty p = userRepo.findProperty("customer.account", compCode);
        return p == null ? null : p.getPropValue();
    }

    private String getSupplierAcc(String compCode) {
        SystemProperty p = userRepo.findProperty("supplier.account", compCode);
        return p == null ? null : p.getPropValue();
    }

    public void sendSale(SaleHis sh) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            String tranSource = "SALE";
            AccSetting setting = hmAccSetting.get(tranSource);
            if (!Objects.isNull(sh)) {
                String srcAcc = setting.getSourceAcc();
                String payAcc = setting.getPayAcc();
                String disAcc = setting.getDiscountAcc();
                String balAcc = setting.getBalanceAcc();
                String taxAcc = setting.getTaxAcc();
                String deptCode = setting.getDeptCode();
                Date vouDate = sh.getVouDate();
                String traderCode = sh.getTraderCode();
                String curCode = sh.getCurCode();
                String remark = sh.getRemark();
                boolean deleted = sh.isDeleted();
                String vouNo = sh.getKey().getVouNo();
                String compCode = sh.getKey().getCompCode();
                double vouBal = Util1.getDouble(sh.getBalance());
                double vouDis = Util1.getDouble(sh.getDiscount());
                double vouPaid = Util1.getDouble(sh.getPaid());
                double vouTax = Util1.getDouble(sh.getTaxAmt());
                double vouTotal = Util1.getDouble(sh.getVouTotal());
                double taxPercent = Util1.getDouble(sh.getTaxPercent());
                TraderKey k = new TraderKey();
                k.setCode(traderCode);
                k.setCompCode(compCode);
                Trader t = traderService.findById(k);
                String traderName = t == null ? "" : t.getTraderName();
                List<Gl> listGl = new ArrayList<>();
                //income
                if (vouBal > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Balance");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //discount
                if (vouDis > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    if (vouPaid > 0) {
                        gl.setSrcAccCode(payAcc);
                        gl.setCash(true);
                    } else {
                        gl.setSrcAccCode(balAcc);
                        gl.setTraderCode(traderCode);
                    }
                    gl.setCrAmt(vouDis);
                    gl.setAccCode(disAcc);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Discount : " + traderName);
                    gl.setTraderCode(traderCode);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //payment
                if (vouPaid > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Paid : " + traderName);
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(srcAcc);
                    gl.setDrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //tax
                if (vouTax > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    if (vouPaid > 0) {
                        gl.setSrcAccCode(payAcc);
                        gl.setCash(true);
                    } else {
                        gl.setSrcAccCode(balAcc);
                        gl.setTraderCode(traderCode);
                    }
                    gl.setAccCode(taxAcc);
                    gl.setDrAmt(vouTax);
                    gl.setGlDate(vouDate);
                    gl.setDescription(String.format("Sale Voucher Tax (%s)", taxPercent));
                    gl.setTraderCode(traderCode);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);

                }
                sendAccount(listGl);
            }
        }
    }

    public void sendPurchase(PurHis ph) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {
            String tranSource = "PURCHASE";
            AccSetting setting = hmAccSetting.get(tranSource);
            if (ph != null) {
                String srcAcc = setting.getSourceAcc();
                String payAcc = setting.getPayAcc();
                String disAcc = setting.getDiscountAcc();
                String balAcc = setting.getBalanceAcc();
                String deptCode = setting.getDeptCode();
                Date vouDate = ph.getVouDate();
                String traderCode = ph.getTraderCode();
                String compCode = ph.getKey().getCompCode();
                String curCode = ph.getCurCode();
                String remark = ph.getRemark();
                boolean deleted = ph.getDeleted();
                double vouTotal = Util1.getDouble(ph.getVouTotal());
                double vouDis = Util1.getDouble(ph.getDiscount());
                double vouPaid = Util1.getDouble(ph.getPaid());
                double vouBal = Util1.getDouble(ph.getBalance());
                String vouNo = ph.getKey().getVouNo();
                TraderKey k = new TraderKey();
                k.setCode(traderCode);
                k.setCompCode(compCode);
                Trader t = traderService.findById(k);
                String traderName = t == null ? "" : t.getTraderName();
                List<Gl> listGl = new ArrayList<>();
                //income
                if (vouBal > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Voucher Balance");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //discount
                if (vouDis > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    if (vouPaid > 0) {
                        gl.setSrcAccCode(payAcc);
                        gl.setCash(true);
                    } else {
                        gl.setSrcAccCode(balAcc);
                        gl.setTraderCode(traderCode);
                    }
                    gl.setAccCode(disAcc);
                    gl.setDrAmt(vouDis);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Voucher Discount : " + traderName);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //payment
                if (vouPaid > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Voucher Paid : " + traderName);
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(srcAcc);
                    gl.setCrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                sendAccount(listGl);
            }
        }
    }

    public void sendReturnIn(RetInHis ri) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {

            String tranSource = "RETURN_IN";
            AccSetting setting = hmAccSetting.get(tranSource);
            if (ri != null) {
                String srcAcc = setting.getSourceAcc();
                String payAcc = setting.getPayAcc();
                String disAcc = setting.getDiscountAcc();
                String balAcc = setting.getBalanceAcc();
                String deptCode = setting.getDeptCode();
                Date vouDate = ri.getVouDate();
                String traderCode = ri.getTraderCode();
                String compCode = ri.getKey().getCompCode();
                String curCode = ri.getCurCode();
                String remark = ri.getRemark();
                boolean deleted = ri.getDeleted();
                String vouNo = ri.getKey().getVouNo();
                double vouBal = Util1.getDouble(ri.getBalance());
                double vouDis = Util1.getDouble(ri.getDiscount());
                double vouPaid = Util1.getDouble(ri.getPaid());
                double vouTotal = Util1.getDouble(ri.getVouTotal());
                TraderKey k = new TraderKey();
                k.setCode(traderCode);
                k.setCompCode(compCode);
                Trader t = traderService.findById(k);
                String traderName = t == null ? "" : t.getTraderName();
                List<Gl> listGl = new ArrayList<>();
                //income
                if (vouBal > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return In Voucher Balance");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //discount
                if (vouDis > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return In Voucher Discount : " + traderName);
                    gl.setSrcAccCode(disAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(vouDis);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //payment
                if (vouPaid > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return In Voucher Paid : " + traderName);
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setCrAmt(vouPaid);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                sendAccount(listGl);
            }
        }
    }

    public void sendReturnOut(RetOutHis ro) {
        if (Util1.getBoolean(environment.getProperty("integration"))) {

            String tranSource = "RETURN_OUT";
            AccSetting setting = hmAccSetting.get(tranSource);
            if (ro != null) {
                String srcAcc = setting.getSourceAcc();
                String payAcc = setting.getPayAcc();
                String disAcc = setting.getDiscountAcc();
                String balAcc = setting.getBalanceAcc();
                String deptCode = setting.getDeptCode();
                Date vouDate = ro.getVouDate();
                String traderCode = ro.getTraderCode();
                String compCode = ro.getKey().getCompCode();
                String curCode = ro.getCurCode();
                String remark = ro.getRemark();
                boolean deleted = ro.getDeleted();
                String vouNo = ro.getKey().getVouNo();
                double vouBal = Util1.getDouble(ro.getBalance());
                double vouDis = Util1.getDouble(ro.getDiscount());
                double vouPaid = Util1.getDouble(ro.getPaid());
                double vouTotal = Util1.getDouble(ro.getVouTotal());
                TraderKey k = new TraderKey();
                k.setCode(traderCode);
                k.setCompCode(compCode);
                Trader t = traderService.findById(k);
                String traderName = t == null ? "" : t.getTraderName();
                List<Gl> listGl = new ArrayList<>();
                //income
                if (vouBal > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return Out Voucher Balance");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(vouTotal);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //discount
                if (vouDis > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return Out Voucher Discount : " + traderName);
                    gl.setSrcAccCode(disAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(vouDis);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //payment
                if (vouPaid > 0) {
                    Gl gl = new Gl();
                    GlKey key = new GlKey();
                    key.setCompCode(compCode);
                    gl.setKey(key);
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return Out Voucher Paid : " + traderName);
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setDrAmt(vouPaid);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                sendAccount(listGl);
            }
        }
    }
}
