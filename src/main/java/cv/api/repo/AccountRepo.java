package cv.api.repo;

import cv.api.common.Util1;
import cv.api.inv.entity.*;
import cv.api.inv.service.ReportService;
import cv.api.model.AccTrader;
import cv.api.model.Gl;
import cv.api.model.Response;
import cv.api.model.TraderKey;
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
                        case "TRADER" -> updateTrader(vouNo, compCode);
                    }
                }
            }, (e) -> {
                Gl gl = glList.get(0);
                String vouNo = gl.getRefNo();
                String compCode = gl.getCompCode();
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

    private void updateTrader(String traderCode, String compCode) {
        String sql = "update trader set intg_upd_status = '" + "ACK" + "' where code ='" + traderCode + "' and '" + compCode + "'";
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
                TraderKey key = new TraderKey();
                key.setCode(t.getKey().getCode());
                key.setCompCode(t.getKey().getCompCode());
                accTrader.setKey(key);
                accTrader.setTraderName(t.getTraderName());
                accTrader.setUserCode(t.getUserCode());
                accTrader.setActive(true);
                accTrader.setAppName(appName);
                accTrader.setMacId(macId);
                switch (traderType) {
                    case "CUS" -> accTrader.setTraderType("C");
                    case "SUP" -> accTrader.setTraderType("S");
                    default -> accTrader.setTraderType("D");
                }
                Mono<Response> result = accountApi.post().uri("/account/save-trader").body(Mono.just(accTrader), AccTrader.class).retrieve().bodyToMono(Response.class).doOnError((e) -> {
                    log.error(e.getMessage());
                });
                Response trader = result.block();
                assert trader != null;
                //updateTrader(trader.getVouNo(), trader.getCompCode());

            }
        }
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
                List<Gl> listGl = new ArrayList<>();
                //income
                if (Util1.getDouble(sh.getVouTotal()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Balance");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(Util1.getDouble(sh.getVouTotal()));
                    gl.setDrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //discount
                if (Util1.getDouble(sh.getDiscount()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Discount");
                    gl.setSrcAccCode(disAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(Util1.getDouble(sh.getDiscount()));
                    gl.setCrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //payment
                if (Util1.getDouble(sh.getPaid()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Paid");
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(Util1.getDouble(sh.getPaid()));
                    gl.setCrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //tax
                if (Util1.getDouble(sh.getTaxAmt()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Sale Voucher Tax");
                    gl.setSrcAccCode(taxAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(Util1.getDouble(sh.getTaxAmt()));
                    gl.setDrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
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
                String taxAcc = setting.getTaxAcc();
                String deptCode = setting.getDeptCode();
                Date vouDate = ph.getVouDate();
                String traderCode = ph.getTrader().getKey().getCode();
                String compCode = ph.getTrader().getKey().getCompCode();
                String curCode = ph.getCurrency().getCurCode();
                String remark = ph.getRemark();
                boolean deleted = ph.getDeleted();
                String vouNo = ph.getVouNo();
                List<Gl> listGl = new ArrayList<>();
                //income
                if (Util1.getDouble(ph.getVouTotal()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Voucher Balance");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(Util1.getDouble(ph.getVouTotal()));
                    gl.setCrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //discount
                if (Util1.getDouble(ph.getDiscount()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Voucher Discount");
                    gl.setSrcAccCode(disAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(Util1.getDouble(ph.getDiscount()));
                    gl.setDrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //payment
                if (Util1.getDouble(ph.getPaid()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Voucher Paid");
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(Util1.getDouble(ph.getPaid()));
                    gl.setDrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                if (Util1.getDouble(ph.getTaxAmt()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Purchase Voucher Tax");
                    gl.setSrcAccCode(taxAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(Util1.getDouble(ph.getTaxAmt()));
                    gl.setCrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
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
                String traderCode = ri.getTrader().getKey().getCode();
                String compCode = ri.getTrader().getKey().getCompCode();
                String curCode = ri.getCurrency().getCurCode();
                String remark = ri.getRemark();
                boolean deleted = ri.getDeleted();
                String vouNo = ri.getVouNo();
                List<Gl> listGl = new ArrayList<>();
                //income
                if (Util1.getDouble(ri.getVouTotal()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return In Voucher Balance");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(Util1.getDouble(ri.getVouTotal()));
                    gl.setCrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //discount
                if (Util1.getDouble(ri.getDiscount()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return In Voucher Discount");
                    gl.setSrcAccCode(disAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(Util1.getDouble(ri.getDiscount()));
                    gl.setCrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //payment
                if (Util1.getDouble(ri.getPaid()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return In Voucher Paid");
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(Util1.getDouble(ri.getPaid()));
                    gl.setDrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
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
                String traderCode = ro.getTrader().getKey().getCode();
                String compCode = ro.getTrader().getKey().getCompCode();
                String curCode = ro.getCurrency().getCurCode();
                String remark = ro.getRemark();
                boolean deleted = ro.getDeleted();
                String vouNo = ro.getVouNo();
                List<Gl> listGl = new ArrayList<>();
                //income
                if (Util1.getDouble(ro.getVouTotal()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return Out Voucher Balance");
                    gl.setSrcAccCode(srcAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setCrAmt(Util1.getDouble(ro.getVouTotal()));
                    gl.setDrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //discount
                if (Util1.getDouble(ro.getDiscount()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return Out Voucher Discount");
                    gl.setSrcAccCode(disAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(Util1.getDouble(ro.getDiscount()));
                    gl.setCrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
                    gl.setCreatedDate(Util1.getTodayDate());
                    gl.setCreatedBy(appName);
                    gl.setTranSource(tranSource);
                    gl.setRefNo(vouNo);
                    gl.setDeleted(deleted);
                    gl.setMacId(macId);
                    listGl.add(gl);
                }
                //payment
                if (Util1.getDouble(ro.getPaid()) > 0) {
                    Gl gl = new Gl();
                    gl.setGlDate(vouDate);
                    gl.setDescription("Return Out Voucher Paid");
                    gl.setSrcAccCode(payAcc);
                    gl.setAccCode(balAcc);
                    gl.setTraderCode(traderCode);
                    gl.setDrAmt(Util1.getDouble(ro.getPaid()));
                    gl.setCrAmt(0.0);
                    gl.setCurCode(curCode);
                    gl.setReference(remark);
                    gl.setDeptCode(deptCode);
                    gl.setCompCode(compCode);
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
