/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.common.*;
import cv.api.inv.dao.ReportDao;
import cv.api.inv.dao.TmpDao;
import cv.api.inv.dao.UnitRelationDao;
import cv.api.inv.entity.*;
import cv.api.inv.view.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author wai yan
 */
@Service
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDao reportDao;
    @Autowired
    private UnitRelationDao relationDao;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private TmpDao tmpService;
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private final HashMap<String, List<UnitRelationDetail>> hmRelation = new HashMap<>();

    @Override
    public void executeSql(String... sql) throws Exception {
        reportDao.executeSql(sql);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void saveReportFilter(ReportFilter filter) {
        Integer macId = filter.getMacId();
        List<String> listBrand = filter.getListBrand();
        List<String> listLocation = filter.getListLocation();
        List<String> listCategory = filter.getListCategory();
        List<String> listSaleMan = filter.getListSaleMan();
        List<String> listTrader = filter.getListTrader();
        List<String> listRegion = filter.getListRegion();
        List<String> listStockType = filter.getListStockType();
        List<String> listStock = filter.getListStock();
        insertTmp(listBrand, macId, "f_brand");
        insertTmp(listLocation, macId, "f_location");
        insertTmp(listCategory, macId, "f_category");
        insertTmp(listSaleMan, macId, "f_sale_man");
        insertTmp(listTrader, macId, "f_trader");
        insertTmp(listRegion, macId, "f_region");
        insertTmp(listStockType, macId, "f_stock_type");
        insertTmp(listStock, macId, "f_stock");
    }

    @Override
    public List<VSale> getSaleVoucher(String vouNo) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select v.trader_name,v.remark,v.vou_no,v.vou_date,v.stock_name, \n" +
                "v.qty,v.sale_price,v.sale_unit,v.sale_amt,v.vou_total,v.discount,v.paid,v.vou_balance,\n" +
                "t.phone,t.address\n" +
                "from v_sale v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "where v.vou_no ='" + vouNo + "'";
        ResultSet rs = reportDao.executeSql(sql);
        while (rs.next()) {
            VSale sale = new VSale();
            String remark = rs.getString("remark");
            String refNo = "-";
            if (remark.contains("/")) {
                String[] split = remark.split("/");
                remark = split[0];
                refNo = split[1];
            }
            sale.setTraderName(rs.getString("trader_name"));
            sale.setRemark(remark);
            sale.setRefNo(refNo);
            sale.setVouNo(rs.getString("vou_no"));
            sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
            sale.setStockName(rs.getString("stock_name"));
            sale.setQty(rs.getFloat("qty"));
            sale.setSalePrice(rs.getFloat("sale_price"));
            sale.setSaleAmount(rs.getFloat("sale_amt"));
            sale.setVouTotal(rs.getFloat("vou_total"));
            sale.setDiscount(rs.getFloat("discount"));
            sale.setPaid(rs.getFloat("paid"));
            sale.setVouBalance(rs.getFloat("vou_balance"));
            sale.setSaleUnit(rs.getString("sale_unit"));
            sale.setCusAddress(Util1.isNull(rs.getString("phone"), "") + "/" + Util1.isNull(rs.getString("address"), ""));
            saleList.add(sale);
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseVoucher(String vouNo) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select trader_name,remark,vou_no,\n" +
                "vou_date,stock_name,pur_unit,qty,pur_price,pur_amt,vou_total,discount,paid,balance\n" +
                "from v_purchase\n" +
                "where vou_no ='" + vouNo + "'";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase purchase = new VPurchase();
                purchase.setTraderName(rs.getString("trader_name"));
                purchase.setRemark(rs.getString("remark"));
                purchase.setVouNo(rs.getString("vou_no"));
                purchase.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                purchase.setStockName(rs.getString("stock_name"));
                purchase.setQty(rs.getFloat("qty"));
                purchase.setPurUnit(rs.getString("pur_unit"));
                purchase.setPurPrice(rs.getFloat("pur_price"));
                purchase.setPurAmount(rs.getFloat("pur_amt"));
                purchase.setVouTotal(rs.getFloat("vou_total"));
                purchase.setDiscount(rs.getFloat("discount"));
                purchase.setPaid(rs.getFloat("paid"));
                purchase.setBalance(rs.getFloat("balance"));
                purchaseList.add(purchase);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VSale> getSaleByCustomerSummary(String fromDate, String toDate,
                                                String curCode, String traderCode,
                                                String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select sh.vou_date,sh.vou_no,sh.trader_code,t.trader_name,sh.saleman_code,sm.saleman_name,sh.remark,sh.vou_total\n" +
                "from sale_his sh join trader t\n" +
                "on sh.trader_code = t.code\n" +
                "left join sale_man sm\n" +
                "on sh.saleman_code = sm.saleman_code\n" +
                "where sh.trader_code = '" + traderCode + "' or '-' = '" + traderCode + "'\n" +
                "and sh.deleted =false\n" +
                "and sh.comp_code = '" + compCode + "'\n" +
                "and sh.cur_code = '" + curCode + "'\n" +
                "and date(sh.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by t.trader_name,sh.vou_date";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = new VSale();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setTraderCode(rs.getString("trader_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setSaleManCode(rs.getString("saleman_code"));
                sale.setSaleManName(rs.getString("saleman_name"));
                sale.setSaleAmount(rs.getFloat("vou_total"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleBySaleManDetail(String fromDate, String toDate, String curCode, String smCode, String stockCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.saleman_code,sm.saleman_name,v.stock_name,v.qty,v.sale_wt,v.sale_unit,v.sale_price,v.sale_amt\n" +
                "from v_sale v left join sale_man sm on v.saleman_code = sm.saleman_code\n" +
                "where v.saleman_code = '" + smCode + "' or '-' = '" + smCode + "'\n" +
                "and v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by sm.saleman_name,v.vou_date,v.vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = new VSale();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setSaleManCode(rs.getString("saleman_code"));
                sale.setSaleManName(Util1.isNull(rs.getString("saleman_name"), "Other"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getFloat("qty"));
                sale.setSaleWt(rs.getFloat("sale_wt"));
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getFloat("sale_price"));
                sale.setSaleAmount(rs.getFloat("sale_amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleBySaleManSummary(String fromDate, String toDate, String curCode,
                                               String smCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select sh.vou_date,sh.vou_no,sh.trader_code,t.trader_name,sh.saleman_code,sm.saleman_name,sh.remark,sh.vou_total\n" +
                "from sale_his sh join trader t\n" +
                "on sh.trader_code = t.code\n" +
                "left join sale_man sm\n" +
                "on sh.saleman_code = sm.saleman_code\n" +
                "where sh.saleman_code = '" + smCode + "' or '-' = '" + smCode + "'\n" +
                "and sh.deleted =false\n" +
                "and sh.comp_code = '" + compCode + "'\n" +
                "and sh.cur_code = '" + curCode + "'\n" +
                "and date(sh.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by sm.saleman_name,sh.vou_date";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = new VSale();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setTraderCode(rs.getString("trader_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setSaleManCode(rs.getString("saleman_code"));
                sale.setSaleManName(Util1.isNull(rs.getString("saleman_name"), "Other"));
                sale.setSaleAmount(rs.getFloat("vou_total"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleByCustomerDetail(String fromDate, String toDate,
                                               String curCode, String traderCode, String stockCode,
                                               String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select vou_date,vou_no,trader_code,trader_name,stock_name,qty,sale_wt,sale_unit,sale_price,sale_amt\n" +
                "from v_sale\n" +
                "where trader_code = '" + traderCode + "' or '-' = '" + traderCode + "'\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by trader_name,vou_date,vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = new VSale();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setTraderCode(rs.getString("trader_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getFloat("qty"));
                sale.setSaleWt(rs.getFloat("sale_wt"));
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getFloat("sale_price"));
                sale.setSaleAmount(rs.getFloat("sale_amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseBySupplierSummary(String fromDate, String toDate, String curCode,
                                                        String traderCode, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select sh.vou_date,sh.vou_no,sh.trader_code,t.trader_name,sh.remark,sh.vou_total\n" +
                "from pur_his sh join trader t\n" +
                "on sh.trader_code = t.code\n" +
                "where sh.trader_code = '" + traderCode + "' or '-' = '" + traderCode + "'\n" +
                "and sh.deleted =false\n" +
                "and sh.comp_code = '" + compCode + "'\n" +
                "and sh.cur_code = '" + curCode + "'\n" +
                "and date(sh.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by t.trader_name,sh.vou_date";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = new VPurchase();
                p.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                p.setVouNo(rs.getString("vou_no"));
                p.setTraderCode(rs.getString("trader_code"));
                p.setTraderName(rs.getString("trader_name"));
                p.setPurAmount(rs.getFloat("vou_total"));
                p.setRemark(rs.getString("remark"));
                purchaseList.add(p);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VPurchase> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode,
                                                       String traderCode, String stockCode, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select vou_date,vou_no,trader_code,trader_name,\n" +
                "stock_name,qty,avg_wt,pur_unit,pur_price,pur_amt\n" +
                "from v_purchase\n" +
                "where (trader_code ='" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by trader_name,vou_no;";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = new VPurchase();
                p.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                p.setVouNo(rs.getString("vou_no"));
                p.setTraderCode(rs.getString("trader_code"));
                p.setTraderName(rs.getString("trader_name"));
                p.setStockName(rs.getString("stock_name"));
                p.setQty(rs.getFloat("qty"));
                p.setAvgWt(rs.getFloat("avg_wt"));
                p.setPurUnit(rs.getString("pur_unit"));
                p.setPurPrice(rs.getFloat("pur_price"));
                p.setPurAmount(rs.getFloat("pur_amt"));
                purchaseList.add(p);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VSale> getSaleByStockSummary(String fromDate, String toDate, String curCode, String stockCode,
                                             String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select vou_date,vou_no,s_user_code,stock_name,remark,trader_code,trader_name,qty,sale_wt,sale_unit,sale_amt\n" +
                "from v_sale\n" +
                "where (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by s_user_code,vou_date,vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = new VSale();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setTraderCode(rs.getString("trader_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setStockUserCode(rs.getString("s_user_code"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getFloat("qty"));
                sale.setSaleWt(rs.getFloat("sale_wt"));
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSaleAmount(rs.getFloat("sale_amt"));
                sale.setRemark(rs.getString("remark"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleByStockDetail(String fromDate, String toDate, String curCode, String stockCode,
                                            String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select vou_date,vou_no,trader_code,trader_name,s_user_code,stock_name,qty,sale_wt,sale_unit,sale_price,sale_amt\n" +
                "from v_sale\n" +
                "where (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by s_user_code,vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = new VSale();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setTraderCode(rs.getString("trader_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setStockUserCode(rs.getString("s_user_code"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getFloat("qty"));
                sale.setSaleWt(rs.getFloat("sale_wt"));
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getFloat("sale_price"));
                sale.setSaleAmount(rs.getFloat("sale_amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseByStockDetail(String fromDate, String toDate, String curCode,
                                                    String stockCode, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select vou_date,vou_no,trader_code,trader_name,\n" +
                "s_user_code,stock_name,qty,avg_wt,pur_unit,pur_price,pur_amt\n" +
                "from v_purchase\n" +
                "where stock_code = '" + stockCode + "' or '-'='" + stockCode + "'\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by s_user_code,vou_date,vou_no;";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = new VPurchase();
                p.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                p.setVouNo(rs.getString("vou_no"));
                p.setTraderCode(rs.getString("trader_code"));
                p.setTraderName(rs.getString("trader_name"));
                p.setStockUserCode(rs.getString("s_user_code"));
                p.setStockName(rs.getString("stock_name"));
                p.setQty(rs.getFloat("qty"));
                p.setAvgWt(rs.getFloat("avg_wt"));
                p.setPurUnit(rs.getString("pur_unit"));
                p.setPurPrice(rs.getFloat("pur_price"));
                p.setPurAmount(rs.getFloat("pur_amt"));
                purchaseList.add(p);
            }
        }
        return purchaseList;
    }

    @Override
    public General getPurchaseAvgPrice(String stockCode) throws Exception {
        General general = new General();
        String sql = "select avg_pur_price from v_avg_pur_price where stock_code = '" + stockCode + "'";
        ResultSet rs = reportDao.executeSql(sql);
        if (rs.next()) {
            general.setAmount(rs.getFloat("avg_pur_price"));
        }
        return general;
    }

    @Override
    public General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode) {
        General general = new General();
        general.setAmount(0.0f);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" +
                "from (select pur_unit,pur_price/rel.smallest_qty smallest_price,pd.rel_code\n" +
                "from v_purchase pd\n" +
                "join v_relation rel on pd.rel_code = rel.rel_code\n" +
                "and pd.pur_unit =  rel.unit\n" +
                "where pd.stock_code = '" + stockCode + "' and vou_no in (\n" +
                "select max(ph.vou_no)\n" +
                "from pur_his ph, pur_his_detail pd\n" +
                "where date(ph.vou_date)<= '" + purDate + "' and deleted = 0\n" +
                "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "))a\n" +
                "join v_relation rel\n" +
                "on a.rel_code =rel.rel_code\n" +
                "and rel.unit = '" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getFloat("price"));
            }
        } catch (Exception e) {
            log.error(String.format("getPurchaseRecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public General getSaleRecentPrice(String stockCode, String saleDate, String unit, String compCode) {
        General general = new General();
        general.setAmount(0.0f);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" +
                "from (select sale_unit,sale_price/rel.smallest_qty smallest_price,pd.rel_code\n" +
                "from v_sale pd\n" +
                "join v_relation rel on pd.rel_code = rel.rel_code\n" +
                "and pd.sale_unit =  rel.unit\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "where vou_no in (\n" +
                "select max(ph.vou_no)\n" +
                "from sale_his ph, sale_his_detail pd\n" +
                "where date(ph.vou_date)<= '" + saleDate + "' and deleted = 0\n" +
                "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "))a\n" +
                "join v_relation rel\n" +
                "on a.rel_code =rel.rel_code\n" +
                "and rel.unit = '" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getFloat("price"));
            }
        } catch (Exception e) {
            log.error(String.format("getPurchaseRecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public General getStockIORecentPrice(String stockCode, String vouDate, String unit) {
        General general = new General();
        general.setAmount(0.0f);
        String sql = "select cost_price,stock_code \n" +
                "from stock_in_out_detail\n" +
                "where stock_code = '" + stockCode + "'and (in_unit = '" + unit + "' or out_unit = '" + unit + "')\n" +
                "and vou_no = (select max(sio.vou_no) \n" +
                "from stock_in_out sio , stock_in_out_detail siod\n" +
                "where date(vou_date) <= '" + vouDate + "' and deleted =0\n" +
                "and sio.vou_no = siod.vou_no\n" +
                "and siod.stock_code = '" + stockCode + "' and (in_unit ='" + unit + "' or out_unit = '" + unit + "'))\n";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getFloat("cost_price"));
            }
        } catch (Exception e) {
            log.error(String.format("getStockIORecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public List<VStockBalance> getStockBalance(String stockCode, boolean relation, Integer macId) throws Exception {
        List<VStockBalance> balances = new ArrayList<>();
        String delSql = "delete from tmp_stock_balance where mac_id = " + macId + "";
        String sql = "insert into tmp_stock_balance(stock_code, qty, wt, unit, loc_code, mac_id)\n" +
                "    select \n" +
                "        a.stock_code,\n" +
                "        SUM(a.qty) AS ttl_qty,\n" +
                "        a.wt ,\n" +
                "        a.unit ,\n" +
                "        a.loc_code,\n" +
                "        " + macId + "\n" +
                "\tfrom\n" +
                "        (select \n" +
                "            stock_code AS stock_code,\n" +
                "                SUM(qty) AS qty,\n" +
                "                ifnull(std_wt,1) AS wt,\n" +
                "                unit AS unit,\n" +
                "                loc_code AS loc_code\n" +
                "        from\n" +
                "            v_opening\n" +
                "        where\n" +
                "            deleted = 0\n" +
                "        and stock_code = '" + stockCode + "'\n" +
                "        group by stock_code , std_wt , unit , loc_code \n" +
                "\t\t\tunion all \n" +
                "        select \n" +
                "\t\t\t\ts.stock_code,\n" +
                "                SUM(s.qty) * - 1 AS qty,\n" +
                "                ifnull(s.sale_wt,1) AS sale_wt,\n" +
                "                s.sale_unit,\n" +
                "                s.loc_code\n" +
                "        from\n" +
                "            v_sale s\n" +
                "        where\n" +
                "            s.deleted = 0\n" +
                "        and s.stock_code = '" + stockCode + "'\n" +
                "        group by s.stock_code,s.sale_wt ,s.sale_unit ,s.loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect \n" +
                "\t\t\t\tp.stock_code,\n" +
                "                SUM(p.qty) AS qty,\n" +
                "                ifnull(p.std_wt,1) AS std_wt,\n" +
                "                pur_unit,\n" +
                "                loc_code\n" +
                "        from\n" +
                "            v_purchase p\n" +
                "        where\n" +
                "            p.deleted = 0\n" +
                "        and p.stock_code = '" + stockCode + "'\n" +
                "        group by p.stock_code , p.std_wt , p.pur_unit , p.loc_code \n" +
                "\t\t\tunion all \n" +
                "        select \n" +
                "\t\t\t\tri.stock_code,\n" +
                "                SUM(ri.qty) AS qty,\n" +
                "                ifnull(ri.wt,1) AS wt,\n" +
                "                ri.unit,\n" +
                "                ri.loc_code\n" +
                "        from\n" +
                "            v_return_in ri\n" +
                "        where\n" +
                "            ri.deleted = 0\n" +
                "        and ri.stock_code = '" + stockCode + "'\n" +
                "        group by ri.stock_code , ri.wt , ri.unit , ri.loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect \n" +
                "\t\t\t\tro.stock_code,\n" +
                "                SUM(ro.qty) * - 1 AS qty,\n" +
                "                ifnull(ro.wt,1) AS wt,\n" +
                "                ro.unit,\n" +
                "                ro.loc_code\n" +
                "        from\n" +
                "            v_return_out ro\n" +
                "        where\n" +
                "            ro.deleted = 0\n" +
                "        and ro.stock_code = '" + stockCode + "'\n" +
                "        group by ro.stock_code , ro.wt , ro.unit , ro.loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect \n" +
                "\t\t\t\tsio.stock_code,\n" +
                "                SUM(sio.in_qty) AS qty,\n" +
                "                ifnull(sio.in_wt,1) AS in_wt,\n" +
                "                sio.in_unit,\n" +
                "                sio.loc_code\n" +
                "        from\n" +
                "            v_stock_io sio\n" +
                "        where\n" +
                "            sio.in_qty is not null\n" +
                "\t\t\tand sio.in_unit is not null\n" +
                "\t\t\tand sio.deleted = 0\n" +
                "        and sio.stock_code = '" + stockCode + "'\n" +
                "        group by sio.stock_code ,sio.in_wt ,sio.in_unit ,sio.loc_code \n" +
                "\t\t\tunion all \n" +
                "        select \n" +
                "\t\t\t\tsio.stock_code,\n" +
                "                SUM(sio.out_qty) * - 1 AS qty,\n" +
                "                ifnull(sio.out_wt,1) AS out_wt,\n" +
                "                sio.out_unit,\n" +
                "                sio.loc_code\n" +
                "        from\n" +
                "            v_stock_io sio\n" +
                "        where\n" +
                "\t\t\t\tsio.out_qty is not null\n" +
                "                and sio.out_unit is not null\n" +
                "                and sio.deleted = 0\n" +
                "        and sio.stock_code = '" + stockCode + "'\n" +
                "        group by sio.stock_code , sio.out_wt , sio.out_unit , sio.loc_code) a\n" +
                "    group by a.stock_code , a.wt , a.unit , a.loc_code";
        reportDao.executeSql(delSql, sql);
        String getSql;
        if (!relation) {
            getSql = "select tmp.stock_code,l.loc_name,tmp.unit,tmp.wt,tmp.qty,tmp.smallest_qty\n" +
                    "from tmp_stock_balance tmp join location l\n" +
                    "on tmp.loc_code = l.loc_code\n" +
                    "where tmp.mac_id = " + macId + "";
        } else {
            getSql = "select a.stock_code,a.rel_code,l.loc_name,a.unit,a.wt,a.qty,sum(a.small_qty) smallest_qty\n" +
                    "from (select tmp.stock_code,s.rel_code,tmp.loc_code,rel.unit,tmp.wt,tmp.qty,rel.smallest_qty,tmp.qty*rel.smallest_qty small_qty\n" +
                    "from tmp_stock_balance tmp join stock s\n" +
                    "on tmp.stock_code = s.stock_code\n" +
                    "join v_relation rel on s.rel_code = rel.rel_code \n" +
                    "and tmp.unit = rel.unit\n" +
                    "where tmp.mac_id = " + macId + ") a\n" +
                    "join location l on a.loc_code = l.loc_code\n" +
                    "group by a.stock_code,a.loc_code,a.wt";
        }
        ResultSet rs = reportDao.executeSql(getSql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockBalance b = new VStockBalance();
                b.setStockCode(rs.getString("stock_code"));
                b.setWeight(rs.getFloat("wt"));
                b.setUnitName(rs.getString("unit"));
                b.setTotalQty(rs.getFloat("qty"));
                b.setLocationName(rs.getString("loc_name"));
                if (relation) {
                    float smallQty = rs.getFloat("smallest_qty");
                    String relCode = rs.getString("rel_code");
                    b.setTotalQty(null);
                    b.setUnitName(getRelStr(relCode, smallQty));
                }
                balances.add(b);
            }
        }
        return balances;
    }

    private String getRelStr(String relCode, float smallestQty) {
        StringBuilder relStr = new StringBuilder();
        if (smallestQty != 0 && !Objects.isNull(relCode)) {
            if (hmRelation.get(relCode) == null) {
                hmRelation.put(relCode, relationDao.getRelationDetail(relCode));
            }
            List<UnitRelationDetail> detailList = hmRelation.get(relCode);
            if (detailList != null) {
                for (UnitRelationDetail unitRelationDetail : detailList) {
                    float smallQty = unitRelationDetail.getSmallestQty();
                    float divider = smallestQty / smallQty;
                    smallestQty = smallestQty % smallQty;
                    String str;
                    if (smallQty == 1) {
                        if (divider != 0) {
                            str = formatter.format(divider);
                            relStr.append(String.format("%s%s%s", str, unitRelationDetail.getUnit(), "*"));
                        }
                    } else {
                        int first = (int) divider;
                        if (first != 0) {
                            str = formatter.format(first);
                            relStr.append(String.format("%s%s%s", str, unitRelationDetail.getUnit(), "*"));
                        }
                    }
                }
            } else {
                log.info(String.format("non relation: %s", relCode));
            }
        }
        String str = relStr.toString();
        if (str.contains("-")) {
            str = str.replaceAll("-", "");
            str = String.format("%s%s", "-", str);
        }
        if (str.isEmpty()) {
            str = "*";
        }
        str = str.substring(0, str.length() - 1);
        if (str.contains("-")) {
            str = str.replaceAll("-", "");
            str = String.format("(%s)", str);
        }
        return str;

    }

    @Override
    public List<ClosingBalance> getClosingStock(String fromDate, String toDate,
                                                String typeCode, String catCode,
                                                String brandCode, String stockCode,
                                                String compCode, Integer macId) throws Exception {
        insertPriceDetail(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        insertClosingIntoColumn(macId);
        String sql = "select st.user_code type_user_code,st.stock_type_name,s.user_code,s.stock_name,a.*,\n" +
                "(a.pur_amt/a.pur_qty) * bal_qty cl_amt\n" +
                "from (\n" +
                "select stock_code,\n" +
                "sum(op_qty) op_qty,sum(op_amt) op_amt,\n" +
                "sum(pur_qty) pur_qty,sum(pur_amt) pur_amt,\n" +
                "sum(in_qty) in_qty,sum(in_amt) in_amt,\n" +
                "sum(out_qty) out_qty,sum(out_amt) out_amt,\n" +
                "sum(sale_qty) sale_qty,sum(sale_amt) sale_amt,\n" +
                "sum(op_qty)+sum(pur_qty)+sum(in_qty)+sum(out_qty)+sum(sale_qty) bal_qty,comp_code\n" +
                "from tmp_closing_column\n" +
                "where mac_id = " + macId + "\n" +
                "group by stock_code\n" +
                ")a\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "order by st.user_code,s.user_code";
        ResultSet rs = reportDao.executeSql(sql);
        List<ClosingBalance> balanceList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                ClosingBalance cb = new ClosingBalance();
                cb.setTypeUserCode(rs.getString("type_user_code"));
                cb.setTypeName(rs.getString("stock_type_name"));
                cb.setStockName(rs.getString("stock_name"));
                cb.setStockUsrCode(rs.getString("user_code"));
                cb.setStockCode(rs.getString("stock_code"));
                cb.setOpenQty(rs.getFloat("op_qty"));
                cb.setOpenAmt(rs.getFloat("op_amt"));
                cb.setPurQty(rs.getFloat("pur_qty"));
                cb.setPurAmt(rs.getFloat("pur_amt"));
                cb.setInQty(rs.getFloat("in_qty"));
                cb.setInAmt(rs.getFloat("in_amt"));
                cb.setOutQty(rs.getFloat("out_qty"));
                cb.setOutAmt(rs.getFloat("out_amt"));
                cb.setSaleQty(rs.getFloat("sale_qty"));
                cb.setSaleAmt(rs.getFloat("sale_amt"));
                cb.setBalQty(rs.getFloat("bal_qty"));
                cb.setClosingAmt(rs.getFloat("cl_amt"));
                balanceList.add(cb);
            }
        }
        return balanceList;
    }

    @Override
    public List<ReorderLevel> getReorderLevel(String compCode) throws Exception {
        //update stock balance
        String sql = """
                select stock_code,stock_name,sum(ttl_qty) ttl_qty,unit
                from v_stock_balance
                group by stock_code,unit""";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                String stockCode = rs.getString("stock_code");
                Float ttlQty = rs.getFloat("ttl_qty");
                String unit = rs.getString("unit");
                ReorderLevel rl = getSession().get(ReorderLevel.class, stockCode);
                rl.setBalQty(ttlQty);
                rl.setBalUnit(new StockUnit(unit));
                getSession().saveOrUpdate(rl);
            }
        }
        String sql1 = """
                select l.stock_code, l.min_qty, l.min_unit, l.max_qty, l.max_unit, l.bal_qty, l.bal_unit, l.comp_code,
                s.stock_name,s.user_code
                from reorder_level l join stock s on l.stock_code = s.stock_code
                order by s.user_code""";
        rs = reportDao.executeSql(sql1);
        List<ReorderLevel> reorderLevels = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                ReorderLevel r = new ReorderLevel();
                Stock s = new Stock();
                s.setStockCode(rs.getString("stock_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setUserCode(rs.getString("user_code"));
                r.setStock(s);
                r.setMinQty(rs.getFloat("min_qty"));
                r.setMinUnit(new StockUnit(rs.getString("min_unit")));
                r.setMaxQty(rs.getFloat("max_qty"));
                r.setMaxUnit(new StockUnit(rs.getString("max_unit")));
                r.setBalQty(rs.getFloat("bal_qty"));
                r.setBalUnit(new StockUnit(rs.getString("bal_unit")));
                r.setCompCode(rs.getString("comp_code"));
                reorderLevels.add(r);
            }
        }
        return reorderLevels;
    }

    @Override
    public void generateReorder(String compCode) throws Exception {
        //generate reorder
        String rSql = "select s.stock_code,s.pur_unit,rl.stock_code ro_stock_code\n" +
                "from stock s left join reorder_level rl\n" +
                "on s.stock_code = rl.stock_code\n" +
                "where s.comp_code= '" + compCode + "' and rl.stock_code is null";
        ResultSet rs = reportDao.executeSql(rSql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                String stockCode = rs.getString("stock_code");
                String roStockCode = rs.getString("ro_stock_code");
                String purUnit = rs.getString("pur_unit");
                if (Objects.isNull(roStockCode)) {
                    StockUnit unit = new StockUnit(purUnit);
                    ReorderLevel rl = new ReorderLevel();
                    rl.setStock(new Stock(stockCode));
                    rl.setMinQty(0.0f);
                    rl.setMinUnit(unit);
                    rl.setMaxQty(0.0f);
                    rl.setMaxUnit(unit);
                    rl.setBalQty(0.0f);
                    rl.setBalUnit(unit);
                    rl.setCompCode(compCode);
                    getSession().save(rl);
                    log.info("reorder : generate reorder stock");
                }
            }
        }
    }

    @Override
    public List<General> getStockListByGroup(String typeCode, String compCode, Integer macId) throws Exception {
        String sql = "select s.stock_code,s.user_code,s.stock_name,s.stock_type_code,\n" +
                "st.stock_type_name,b.brand_name,c.cat_name,rel.rel_name\n" +
                "from stock s \n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "left join stock_brand b on s.brand_code = b.brand_code\n" +
                "left join category c on s.category_code = c.cat_code\n" +
                "left join unit_relation rel on s.rel_code = rel.rel_code\n" +
                "where s.active = true and s.comp_code = '" + compCode + "' \n" +
                "and (s.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "order by st.user_code";
        ResultSet rs = reportDao.executeSql(sql);
        List<General> generalList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                General g = new General();
                g.setStockCode(rs.getString("user_code"));
                g.setStockName(rs.getString("stock_name"));
                g.setSysCode(rs.getString("stock_code"));
                g.setStockTypeName(rs.getString("stock_type_name"));
                g.setBrandName(rs.getString("brand_name"));
                g.setCategoryName(rs.getString("cat_name"));
                g.setQtyRel(rs.getString("rel_name"));
                generalList.add(g);
            }
        }
        return generalList;
    }

    @Override
    public List<General> getTopSaleByCustomer(String fromDate, String toDate, String compCode) throws Exception {
        String sql = "select t.user_code,t.trader_name, sum(sh.vou_total) vou_total,count(*) vou_qty\n" +
                "from sale_his sh join trader t\n" +
                "on sh.trader_code = t.code\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and sh.comp_code = '" + compCode + "' and sh.deleted = 0\n" +
                "group by sh.trader_code\n" +
                "order by vou_total desc";
        ResultSet rs = reportDao.executeSql(sql);
        List<General> generals = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                General g = new General();
                g.setTraderCode(rs.getString("user_code"));
                g.setTraderName(rs.getString("trader_name"));
                g.setAmount(rs.getFloat("vou_total"));
                g.setTotalQty(rs.getFloat("vou_qty"));
                generals.add(g);
            }
        }
        return generals;
    }

    @Override
    public List<General> getTopSaleBySaleMan(String fromDate, String toDate, String compCode) throws Exception {
        String sql = "select s.user_code,s.saleman_name,count(*) vou_qty,sum(sh.vou_total) vou_total\n" +
                "from sale_his sh left join sale_man s\n" +
                "on sh.saleman_code = s.saleman_code\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and sh.comp_code = '" + compCode + "' and sh.deleted = 0\n" +
                "group by sh.saleman_code\n" +
                "order by vou_total desc";
        List<General> generals = new ArrayList<>();
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                General g = new General();
                g.setSaleManName(rs.getString("saleman_name"));
                g.setSaleManCode(rs.getString("user_code"));
                g.setTotalQty(rs.getFloat("vou_qty"));
                g.setAmount(rs.getFloat("vou_total"));
                generals.add(g);
            }
        }
        return generals;
    }

    @Override
    public List<General> getTopSaleByStock(String fromDate, String toDate, String typeCode, String compCode) throws Exception {
        String sql = "select a.*,sum(ttl_amt) ttl_amt,sum(a.ttl_qty*rel.smallest_qty) smallest_qty\n" +
                "from (select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,sale_unit,sum(sale_amt) ttl_amt,rel_code\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "group by stock_code,sale_unit\n" +
                ")a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.sale_unit = rel.unit\n" +
                "group by stock_code\n" +
                "order by smallest_qty desc";
        ResultSet rs = reportDao.executeSql(sql);
        List<General> generals = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                General g = new General();
                g.setStockCode(rs.getString("s_user_code"));
                g.setStockName(rs.getString("stock_name"));
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                g.setQtyRel(getRelStr(relCode, smallQty));
                g.setAmount(rs.getFloat("ttl_amt"));
                generals.add(g);
            }
        }
        return generals;
    }

    @Override
    public List<ClosingBalance> getClosingStockDetail(String fromDate, String toDate,
                                                      String typeCode, String catCode,
                                                      String brandCode, String stockCode,
                                                      String compCode, Integer macId) throws Exception {
        insertPriceDetail(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        insertClosingIntoColumn(macId);
        String sql = "select s.user_code,s.stock_name,a.*,\n" +
                "(a.pur_amt/a.pur_qty) * bal_qty cl_amt\n" +
                "from (\n" +
                "select stock_code,tran_date vou_date,vou_no,\n" +
                "sum(op_qty) op_qty,sum(op_amt) op_amt,\n" +
                "sum(pur_qty) pur_qty,sum(pur_amt) pur_amt,\n" +
                "sum(in_qty) in_qty,sum(in_amt) in_amt,\n" +
                "sum(out_qty) out_qty,sum(out_amt) out_amt,\n" +
                "sum(sale_qty) sale_qty,sum(sale_amt) sale_amt,\n" +
                "sum(op_qty)+sum(pur_qty)+sum(in_qty)+sum(out_qty)+sum(sale_qty) bal_qty,comp_code\n" +
                "from tmp_closing_column\n" +
                "where mac_id = " + macId + "\n" +
                "group by stock_code,tran_date,vou_no\n" +
                ")a\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "order by s.user_code,a.vou_date";
        ResultSet rs = reportDao.executeSql(sql);
        List<ClosingBalance> balanceList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                ClosingBalance cb = new ClosingBalance();
                cb.setStockName(rs.getString("stock_name"));
                cb.setStockUsrCode(rs.getString("user_code"));
                cb.setStockCode(rs.getString("stock_code"));
                cb.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                cb.setVouNo(rs.getString("vou_no"));
                cb.setOpenQty(rs.getFloat("op_qty"));
                cb.setOpenAmt(rs.getFloat("op_amt"));
                cb.setPurQty(rs.getFloat("pur_qty"));
                cb.setPurAmt(rs.getFloat("pur_amt"));
                cb.setInQty(rs.getFloat("in_qty"));
                cb.setInAmt(rs.getFloat("in_amt"));
                cb.setOutQty(rs.getFloat("out_qty"));
                cb.setOutAmt(rs.getFloat("out_amt"));
                cb.setSaleQty(rs.getFloat("sale_qty"));
                cb.setSaleAmt(rs.getFloat("sale_amt"));
                cb.setBalQty(rs.getFloat("bal_qty"));
                cb.setClosingAmt(rs.getFloat("cl_amt"));
                balanceList.add(cb);
            }
        }
        return balanceList;
    }

    @Override
    public List<ClosingBalance> getStockInOutSummary(String opDate, String fromDate, String toDate, String typeCode,
                                                     String catCode, String brandCode, String stockCode, String compCode,
                                                     Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        String getSql = "select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,\n" +
                "s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name\n" +
                "from (select stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,\n" +
                "sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty\n" +
                "from tmp_stock_io_column\n" +
                "where mac_id = " + macId + "\n" +
                "group by stock_code)a\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "group by stock_code\n" +
                "order by s.user_code";
        List<ClosingBalance> balances = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(getSql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    ClosingBalance b = new ClosingBalance();
                    float opQty = rs.getFloat("op_qty");
                    float purQty = rs.getFloat("pur_qty");
                    float inQty = rs.getFloat("in_qty");
                    float saleQty = rs.getFloat("sale_qty");
                    float outQty = rs.getFloat("out_qty");
                    float balQty = rs.getFloat("bal_qty");
                    String relCode = rs.getString("rel_code");
                    b.setOpenRel(getRelStr(relCode, opQty));
                    b.setPurRel(getRelStr(relCode, purQty));
                    b.setInRel(getRelStr(relCode, inQty));
                    b.setSaleRel(getRelStr(relCode, saleQty));
                    b.setOutRel(getRelStr(relCode, outQty));
                    b.setBalRel(getRelStr(relCode, balQty));
                    b.setStockUsrCode(rs.getString("s_user_code"));
                    b.setStockName(rs.getString("stock_name"));
                    String groupName = rs.getString("stock_type_name");
                    b.setTypeName(typeCode.equals("-") ? "All" : groupName);
                    balances.add(b);
                }
            }
        } catch (Exception e) {
            log.error("getStockInOutSummary: " + Arrays.toString(e.getStackTrace()));
        }
        return balances;
    }

    @Override
    public void calculateStockInOutDetail(String opDate, String fromDate, String toDate, String typeCode,
                                          String catCode, String brandCode, String stockCode, String compCode,
                                          Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        String sql = "select distinct stock_code from tmp_stock_io_column where mac_id = " + macId + "";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    String code = rs.getString("stock_code");
                    List<TmpStockIO> listIO = tmpService.getStockIO(code, macId);
                    if (!listIO.isEmpty()) {
                        float clAmt;
                        for (int i = 0; i < listIO.size(); i++) {
                            if (i > 0) {
                                TmpStockIO io = listIO.get(i - 1);
                                clAmt = io.getOpQty() + io.getPurQty() + io.getInQty() + io.getOutQty() + io.getSaleQty();
                                TmpStockIO io1 = listIO.get(i);
                                io1.setOpQty(clAmt);
                                //tmpService.save(io);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public List<ClosingBalance> getStockInOutDetail(String typeCode, Integer macId) {
        String getSql = "select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,\n" +
                "s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name\n" +
                "from (select tran_option,tran_date,stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,\n" +
                "sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,remark\n" +
                "from tmp_stock_io_column\n" +
                "where mac_id = " + macId + "\n" +
                "group by tran_date,stock_code,tran_option)a\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "group by tran_date,stock_code,tran_option\n" +
                "order by s.user_code,a.tran_date,a.tran_option";
        List<ClosingBalance> balances = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(getSql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    ClosingBalance b = new ClosingBalance();
                    float opQty = rs.getFloat("op_qty");
                    float purQty = rs.getFloat("pur_qty");
                    float inQty = rs.getFloat("in_qty");
                    float saleQty = rs.getFloat("sale_qty");
                    float outQty = rs.getFloat("out_qty");
                    float balQty = rs.getFloat("bal_qty");
                    String relCode = rs.getString("rel_code");
                    b.setOpenRel(getRelStr(relCode, opQty));
                    b.setPurRel(getRelStr(relCode, purQty));
                    b.setInRel(getRelStr(relCode, inQty));
                    b.setSaleRel(getRelStr(relCode, saleQty));
                    b.setOutRel(getRelStr(relCode, outQty));
                    b.setBalRel(getRelStr(relCode, balQty));
                    b.setVouDate(Util1.toDateStr(rs.getDate("tran_date"), "dd/MM/yyyy"));
                    b.setStockUsrCode(rs.getString("s_user_code"));
                    b.setStockName(rs.getString("stock_name"));
                    b.setTypeName(typeCode.equals("-") ? "All" : rs.getString("stock_type_name"));
                    b.setTypeUserCode(rs.getString("st_user_code"));
                    b.setRemark(rs.getString("remark"));
                    balances.add(b);
                }
            }

        } catch (Exception e) {
            log.error(String.format("getStockInOutDetail: %s", e.getMessage()));
        }
        return balances;
    }

    @Override
    public List<StockValue> getStockValue(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        calculatePrice(toDate, opDate, compCode, stockCode, macId);
        List<StockValue> values = new ArrayList<>();
        String getSql = "select a.*,\n" +
                "sum(ifnull(tmp.pur_avg_price,0)) pur_avg_price,bal_qty*sum(ifnull(tmp.pur_avg_price,0)) pur_avg_amt,\n" +
                "sum(ifnull(tmp.in_avg_price,0)) in_avg_price,bal_qty*sum(ifnull(tmp.in_avg_price,0)) in_avg_amt,\n" +
                "sum(ifnull(tmp.std_price,0)) std_price,bal_qty*sum(ifnull(tmp.std_price,0)) std_amt,\n" +
                "sum(ifnull(tmp.pur_recent_price,0)) pur_recent_price,bal_qty*sum(ifnull(tmp.pur_recent_price,0)) pur_recent_amt,\n" +
                "sum(ifnull(tmp.fifo_price,0)) fifo_price,bal_qty*sum(ifnull(tmp.fifo_price,0)) fifo_amt,\n" +
                "sum(ifnull(tmp.lifo_price,0)) lifo_price,bal_qty*sum(ifnull(tmp.lifo_price,0)) lifo_amt,\n" +
                "s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name\n" +
                "from (\n" +
                "select stock_code,sum(op_qty)+sum(pur_qty)+sum(in_qty) +sum(out_qty) +sum(sale_qty) bal_qty,mac_id\n" +
                "from tmp_stock_io_column\n" +
                "where mac_id = " + macId + "\n" +
                "group by stock_code)a\n" +
                "left join tmp_stock_price tmp\n" +
                "on a.stock_code  = tmp.stock_code\n" +
                "and a.mac_id = tmp.mac_id\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "group by a.stock_code\n" +
                "order by s.user_code";
        try {
            ResultSet rs = reportDao.executeSql(getSql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    StockValue value = new StockValue();
                    value.setStockUserCode(rs.getString("s_user_code"));
                    value.setStockName(rs.getString("stock_name"));
                    value.setStockTypeName(typeCode.equals("-") ? "All" : rs.getString("stock_type_name"));
                    value.setStockTypeUserCode(rs.getString("st_user_code"));
                    value.setBalRel(getRelStr(rs.getString("rel_code"), rs.getFloat("bal_qty")));
                    value.setPurAvgPrice(rs.getFloat("pur_avg_price"));
                    value.setPurAvgAmount(rs.getFloat("pur_avg_amt"));
                    value.setInAvgPrice(rs.getFloat("in_avg_price"));
                    value.setInAvgAmount(rs.getFloat("in_avg_amt"));
                    value.setStdPrice(rs.getFloat("std_price"));
                    value.setStdAmount(rs.getFloat("std_amt"));
                    value.setRecentPrice(rs.getFloat("pur_recent_price"));
                    value.setRecentAmt(rs.getFloat("pur_recent_amt"));
                    value.setFifoPrice(rs.getFloat("fifo_price"));
                    value.setFifoAmt(rs.getFloat("fifo_amt"));
                    value.setLifoPrice(rs.getFloat("lifo_price"));
                    value.setLifoAmt(rs.getFloat("lifo_amt"));
                    values.add(value);
                }
            }
        } catch (Exception e) {
            log.error(String.format("getStockValue: %s", e.getMessage()));
        }
        return values;
    }

    @Override
    public List<VOpening> getOpeningByLocation(String stockCode, Integer macId, String compCode) throws Exception {
        List<VOpening> openings = new ArrayList<>();
        String sql = "select op_date,vou_no,remark,stock_code,stock_user_code,stock_name,loc_name,\n" +
                "unit,qty,price,amount\n" +
                "from v_opening\n" +
                "where deleted = false\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and comp_code ='" + compCode + "' order by loc_name,stock_user_code\n";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOpening opening = new VOpening();
                opening.setOpDate(Util1.toDateStr(rs.getDate("op_date"), "dd/MM/yyyy"));
                opening.setVouNo(rs.getString("vou_no"));
                opening.setRemark(rs.getString("remark"));
                opening.setStockCode(rs.getString("stock_code"));
                opening.setUnit(rs.getString("unit"));
                opening.setStockUserCode(rs.getString("stock_user_code"));
                opening.setStockName(rs.getString("stock_name"));
                opening.setLocationName(rs.getString("loc_name"));
                opening.setQty(rs.getFloat("qty"));
                opening.setPrice(rs.getFloat("price"));
                opening.setAmount(rs.getFloat("amount"));
                openings.add(opening);
            }
        }
        return openings;
    }

    @Override
    public List<VOpening> getOpeningByGroup(String typeCode, String stockCode, Integer macId, String compCode) throws Exception {
        List<VOpening> openings = new ArrayList<>();
        String sql = "select a.*,t.stock_type_name\n" +
                "from (select op_date,remark,stock_type_code,stock_code,stock_user_code,stock_name,loc_name, \n" +
                "                unit,qty,price,amount \n" +
                "from v_opening \n" +
                "where deleted = false \n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "'))a\n" +
                "join stock_type t on a.stock_type_code = t.stock_type_code\n" +
                "order by t.stock_type_name,a.stock_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOpening opening = new VOpening();
                opening.setOpDate(Util1.toDateStr(rs.getDate("op_date"), "dd/MM/yyyy"));
                opening.setRemark(rs.getString("remark"));
                opening.setStockTypeName(rs.getString("stock_type_name"));
                opening.setStockCode(rs.getString("stock_code"));
                opening.setUnit(rs.getString("unit"));
                opening.setStockUserCode(rs.getString("stock_user_code"));
                opening.setStockName(rs.getString("stock_name"));
                opening.setLocationName(rs.getString("loc_name"));
                opening.setQty(rs.getFloat("qty"));
                opening.setPrice(rs.getFloat("price"));
                opening.setAmount(rs.getFloat("amount"));
                openings.add(opening);
            }
        }
        return openings;
    }

    @Override
    public List<VStockIO> getStockIODetailByVoucherType(String vouStatus, String fromDate, String toDate, String typeCode,
                                                        String catCode, String brandCode, String stockCode, String compCode,
                                                        Integer macId) throws Exception {
        String sql = "select vou_date,vou_no,remark,description,vs_user_code,vou_status_name,s_user_code,stock_name,loc_name,\n" +
                "out_qty,out_unit,cur_code,cost_price,cost_price* out_qty out_amt \n" +
                "from v_stock_io\n" +
                "where comp_code = '" + compCode + "'\n" +
                "and deleted = 0\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" +
                "and (brand_code ='" + brandCode + "' or '-'='" + brandCode + "')\n" +
                "and (vou_status = '" + vouStatus + "' or '-' = '" + vouStatus + "')\n" +
                "and out_qty is not null and out_unit is not null\n\n" +
                "group by vou_date,vou_no,stock_code,in_unit,out_unit,cur_code\n" +
                "order by vs_user_code,cur_code,vou_date,vou_no,s_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        List<VStockIO> list = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockIO io = new VStockIO();
                io.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                io.setVouNo(rs.getString("vou_no"));
                io.setStockUsrCode(rs.getString("s_user_code"));
                io.setStockName(rs.getString("stock_name"));
                io.setRemark(rs.getString("remark"));
                io.setDescription(rs.getString("description"));
                io.setVouTypeUserCode(rs.getString("vs_user_code"));
                io.setVouTypeName(rs.getString("vou_status_name"));
                io.setStockUsrCode(rs.getString("s_user_code"));
                io.setStockName(rs.getString("stock_name"));
                io.setLocName(rs.getString("loc_name"));
                io.setOutQty(rs.getFloat("out_qty"));
                io.setOutUnit(rs.getString("out_unit"));
                io.setCurCode(rs.getString("cur_code"));
                io.setCostPrice(rs.getFloat("cost_price"));
                io.setOutAmt(rs.getFloat("out_amt"));
                list.add(io);
            }
        }
        return list;
    }

    @Override
    public List<VStockIO> getStockIOPriceCalender(String vouType, String fromDate, String toDate,
                                                  String typeCode, String catCode, String brandCode,
                                                  String stockCode, String compCode, Integer macId) throws Exception {
        String sql = "select vou_date,vou_no,stock_code,s_user_code,\n" +
                "stock_name,vou_status_name,if(in_unit is null,out_unit,in_unit) unit, cost_price \n" +
                "from v_stock_io\n" +
                "where comp_code = '" + compCode + "'\n" +
                "and deleted = 0\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "and (vou_status ='" + vouType + "' or '-' ='" + vouType + "')\n" +
                "group by stock_code,cost_price,unit\n" +
                "order by s_user_code,vou_date\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VStockIO> ioList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockIO io = new VStockIO();
                io.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyy"));
                io.setVouNo(rs.getString("vou_no"));
                io.setStockUsrCode(rs.getString("s_user_code"));
                io.setStockName(rs.getString("stock_name"));
                io.setVouTypeName(rs.getString("vou_status_name"));
                io.setInUnit(rs.getString("unit"));
                io.setCostPrice(rs.getFloat("cost_price"));
                ioList.add(io);
            }
        }
        return ioList;
    }

    @Override
    public List<VStockIO> getStockIOHistory(String fromDate, String toDate, String vouStatus, String vouNo, String remark,
                                            String desp, String userCode, String stockCode, String compCode) throws Exception {
        String sql = "select vou_date,vou_no,description,remark,vou_status_name,created_by,deleted\n" +
                "from v_stock_io\n" +
                "where comp_code = '" + compCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" +
                "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" +
                "and (description like '" + desp + "%' or '-%'= '" + desp + "%')\n" +
                "and (vou_status = '" + vouStatus + "' or '-'='" + vouStatus + "')\n" +
                "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "group by vou_no\n" +
                "order by vou_date,vou_no desc";
        ResultSet rs = reportDao.executeSql(sql);
        List<VStockIO> ioList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockIO io = new VStockIO();
                io.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                io.setVouNo(rs.getString("vou_no"));
                io.setDescription(rs.getString("description"));
                io.setRemark(rs.getString("remark"));
                io.setVouTypeName(rs.getString("vou_status_name"));
                io.setCreatedBy(rs.getString("created_by"));
                io.setDeleted(rs.getBoolean("deleted"));
                ioList.add(io);
            }
        }
        return ioList;
    }

    @Override
    public List<VSale> getSaleHistory(String fromDate, String toDate, String traderCode, String saleManCode,
                                      String vouNo, String remark, String reference, String userCode, String stockCode, String compCode) throws Exception {
        String sql = "select vou_date,vou_no,trader_name,remark,created_by,paid,vou_total,deleted \n" +
                "from v_sale\n" +
                "where comp_code = '" + compCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" +
                "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" +
                "and (reference like '" + reference + "%' or '-%'= '" + reference + "%')\n" +
                "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" +
                "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "and (saleman_code ='" + saleManCode + "' or '-' ='" + saleManCode + "')\n" +
                "group by vou_no\n" +
                "order by vou_date desc,vou_no desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VSale> saleList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale s = new VSale();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setRemark(rs.getString("remark"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setPaid(rs.getFloat("paid"));
                s.setVouTotal(rs.getFloat("vou_total"));
                s.setDeleted(rs.getBoolean("deleted"));
                saleList.add(s);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseHistory(String fromDate, String toDate, String traderCode,
                                              String vouNo, String remark, String reference,
                                              String userCode, String stockCode, String compCode) throws Exception {
        String sql = "select vou_date,vou_no,trader_name,remark,created_by,paid,vou_total,deleted \n" +
                "from v_purchase\n" +
                "where comp_code = '" + compCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" +
                "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" +
                "and (remark like '" + reference + "%' or '-%'= '" + reference + "%')\n" +
                "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" +
                "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "group by vou_no\n" +
                "order by vou_date desc,vou_no desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VPurchase> purchaseList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase s = new VPurchase();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setRemark(rs.getString("remark"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setPaid(rs.getFloat("paid"));
                s.setVouTotal(rs.getFloat("vou_total"));
                s.setDeleted(rs.getBoolean("deleted"));
                purchaseList.add(s);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VReturnIn> getReturnInHistory(String fromDate, String toDate, String traderCode, String vouNo,
                                              String remark, String userCode, String stockCode, String compCode) throws Exception {
        String sql = "select vou_date,vou_no,trader_name,remark,created_by,paid,vou_total,deleted \n" +
                "from v_return_in\n" +
                "where comp_code = '" + compCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" +
                "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" +
                "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" +
                "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "group by vou_no\n" +
                "order by vou_date,vou_no desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VReturnIn> returnInList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VReturnIn s = new VReturnIn();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setRemark(rs.getString("remark"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setPaid(rs.getFloat("paid"));
                s.setVouTotal(rs.getFloat("vou_total"));
                s.setDeleted(rs.getBoolean("deleted"));
                returnInList.add(s);
            }
        }
        return returnInList;
    }

    @Override
    public List<VReturnOut> getReturnOutHistory(String fromDate, String toDate, String traderCode, String vouNo,
                                                String remark, String userCode, String stockCode, String compCode) throws Exception {
        String sql = "select vou_date,vou_no,trader_name,remark,created_by,paid,vou_total,deleted \n" +
                "from v_return_out\n" +
                "where comp_code = '" + compCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" +
                "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" +
                "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" +
                "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "group by vou_no\n" +
                "order by vou_date,vou_no desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VReturnOut> returnInList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VReturnOut s = new VReturnOut();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setRemark(rs.getString("remark"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setPaid(rs.getFloat("paid"));
                s.setVouTotal(rs.getFloat("vou_total"));
                s.setDeleted(rs.getBoolean("deleted"));
                returnInList.add(s);
            }
        }
        return returnInList;
    }

    private void insertClosingIntoColumn(Integer macId) throws Exception {
        //delete tmp
        String delSql = "delete from tmp_closing_column where mac_id = " + macId + "";
        executeSql(delSql);
        //opening
        String opSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,op_qty,op_price,op_amt,op_unit,mac_id,comp_code)\n" +
                "select tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where tran_option ='Opening' and mac_id = " + macId + "\n" +
                "group by tran_option,vou_no,tran_date,stock_code,loc_code,mac_id\n";
        executeSql(opSql);
        //purchase
        String purSql = "insert into tmp_closing_column(tran_option,vou_no,tran_date,stock_code,loc_code,pur_qty,pur_price,pur_amt,pur_unit,mac_id,comp_code)\n" +
                "select tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where tran_option ='Purchase' and mac_id = " + macId + "\n" +
                "group by tran_option,vou_no, tran_date,stock_code,loc_code,mac_id\n";
        executeSql(purSql);
        //stock in
        String inSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,in_qty,in_price,in_amt,in_unit,mac_id,comp_code)\n" +
                "select 'In' tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where (tran_option ='ReturnIn' or\n" +
                "tran_option= 'StockIn') and mac_id = " + macId + "\n" +
                "group by tran_date,vou_no,stock_code,loc_code,mac_id\n";
        executeSql(inSql);
        //return in
        //sale
        String saleSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,sale_qty,sale_price,sale_amt,sale_unit,mac_id,comp_code)\n" +
                "select tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty)*-1 ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where tran_option ='Sale' and mac_id = " + macId + "\n" +
                "group by tran_option,vou_no, tran_date,stock_code,loc_code,mac_id\n";
        executeSql(saleSql);
        //stock out
        String outSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,out_qty,out_price,out_amt,out_unit,mac_id,comp_code)\n" +
                "select 'Out' tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty)*-1 ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where (tran_option ='ReturnOut' or\n" +
                "tran_option= 'StockOut') and mac_id = " + macId + "\n" +
                "group by tran_date,vou_no,stock_code,loc_code,mac_id\n";
        executeSql(outSql);
        //return out
    }

    private void insertPriceDetail(String fromDate, String toDate,
                                   String typeCode, String catCode,
                                   String brandCode, String stockCode,
                                   String compCode, Integer macId) throws Exception {
        //delete tmp
        String delSql = "delete from tmp_inv_closing where mac_id = " + macId + "";
        executeSql(delSql);
        //opening
        String insertSql = "insert into tmp_inv_closing(tran_option, tran_date, vou_no, stock_code, qty, price, loc_code, unit, mac_id,comp_code)\n";
        String opSql = "select 'Opening' option, op_date, vou_no, stock_code, sum(qty) qty, price, loc_code, unit," + macId + " mac_id,comp_code\n" +
                "from v_opening\n" +
                "where date(op_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code = '" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by option,op_date,vou_no,stock_code,loc_code,unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, opSql));
        //purchase
        String purSql = "select 'Purchase' option,vou_date,vou_no,stock_code,sum(qty) qty,pur_price,loc_code,pur_unit," + macId + " mac_id,comp_code\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code = '" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by option,vou_date,vou_no,stock_code,loc_code,pur_unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, purSql));
        //stockIn
        String inSql = "select 'StockIn' option,vou_date,vou_no,stock_code,sum(in_qty) qty,cost_price,loc_code,in_unit," + macId + " mac_id,comp_code\n" +
                "from v_stock_io\n" +
                "where in_qty is not null and in_unit is not null\n" +
                "and deleted = false \n" +
                "and comp_code = '" + compCode + "'\n" +
                "and  date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by option,vou_date,vou_no,stock_code,loc_code,in_unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, inSql));
        //return in
        String retInSql = "select 'ReturnIn' option,vou_date,vou_no,stock_code,sum(qty) qty,cost_price,loc_code,unit," + macId + " mac_id,comp_code\n" +
                "from v_return_in\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code = '" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by option,vou_date,vou_no,stock_code,loc_code,unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, retInSql));
        //sale
        String saleSql = "select 'Sale' option,vou_date,vou_no,stock_code,sum(qty) qty,sale_price,loc_code,sale_unit," + macId + " mac_id,comp_code\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code = '" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by option,vou_date,vou_no,stock_code,loc_code,sale_unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, saleSql));
        //stockOut
        String outSql = "select 'StockOut' option,vou_date,vou_no,stock_code,sum(out_qty) qty,cost_price,loc_code,out_unit," + macId + " mac_id,comp_code\n" +
                "from v_stock_io\n" +
                "where out_qty is not null and out_unit is not null\n" +
                "and deleted = false \n" +
                "and comp_code = '" + compCode + "'\n" +
                "and  date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by option,vou_date,vou_no,stock_code,loc_code,out_unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, outSql));
        //return out
        String retOutSql = "select 'ReturnOut' option,vou_date,vou_no,stock_code,sum(qty) qty,price,loc_code,unit," + macId + " mac_id,comp_code\n" +
                "from v_return_out\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code = '" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by option,vou_date,vou_no,stock_code,loc_code,unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, retOutSql));
    }

    private void calculateClosing(String fromDate, String toDate,
                                  String typeCode, String catCode,
                                  String brandCode, String stockCode,
                                  String compCode, Integer macId) {
        String delSql = "delete from tmp_stock_io_column where mac_id = " + macId + "";
        String opSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,op_qty,loc_code,mac_id)\n" +
                "select 'Opening',a.tran_date,'Opening',a.stock_code,sum(smallest_qty) smallest_qty,a.loc_code,a.mac_id\n" +
                "from (\n" +
                "select tmp.tran_date,tmp.stock_code,tmp.ttl_qty * rel.smallest_qty smallest_qty,tmp.loc_code,tmp.mac_id\n" +
                "from tmp_stock_opening tmp \n" +
                "join stock s on tmp.stock_code = s.stock_code\n" +
                "join v_relation rel on s.rel_code = rel.rel_code\n" +
                "and tmp.unit = rel.unit\n" +
                "where tmp.mac_id =" + macId + ")a\n" +
                "group by tran_date,stock_code,mac_id";
        String purSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,pur_qty,loc_code,mac_id)\n" +
                "select 'Purchase',a.vou_date,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + "\n" +
                "from (\n" +
                "select vou_date,remark,stock_code,sum(qty) qty,loc_code, pur_unit,rel_code\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by vou_date,stock_code,pur_unit)a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.pur_unit = rel.unit\n" +
                "group by a.vou_date,a.stock_code";
        //ret in
        String retInSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,in_qty,loc_code,mac_id)\n" +
                "select 'ReturnIn',a.vou_date,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + "\n" +
                "from (\n" +
                "select vou_date,remark,stock_code,sum(qty) qty,loc_code,rel_code, unit\n" +
                "from v_return_in\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by vou_date,stock_code,unit)a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.unit = rel.unit\n" +
                "group by vou_date,stock_code";
        String stockInSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,in_qty,loc_code,mac_id)\n" +
                "select 'StockIn',a.vou_date,a.description,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + "\n" +
                "from (\n" +
                "select vou_date,description,stock_code,sum(in_qty) qty,loc_code,in_unit,rel_code\n" +
                "from v_stock_io\n" +
                "where  in_qty is not null and in_unit is not null\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by vou_date,stock_code,in_unit)a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.in_unit = rel.unit\n" +
                "group by a.vou_date,a.stock_code";
        String saleSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,sale_qty,loc_code,mac_id)\n" +
                "select 'Sale',a.vou_date,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + "\n" +
                "from (\n" +
                "select vou_date,remark,stock_code,sum(qty) qty,loc_code, sale_unit,rel_code\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by vou_date,stock_code,sale_unit)a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.sale_unit = rel.unit\n" +
                "group by a.vou_date,a.stock_code";
        String returnOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,out_qty,loc_code,mac_id)\n" +
                "select 'ReturnOut',a.vou_date,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + "\n" +
                "from (\n" +
                "select vou_date,remark,stock_code,sum(qty) qty,loc_code, unit,rel_code\n" +
                "from v_return_out\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by vou_date,stock_code,unit)a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.unit = rel.unit\n" +
                "group by vou_date,stock_code";
        String stockOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,out_qty,loc_code,mac_id)\n" +
                "select 'StockOut',a.vou_date,a.description,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + "\n" +
                "from (\n" +
                "select vou_date,description,stock_code,sum(out_qty) qty,loc_code,out_unit,rel_code\n" +
                "from v_stock_io\n" +
                "where  out_qty is not null and out_unit is not null\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by vou_date,stock_code,out_unit)a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.out_unit = rel.unit\n" +
                "group by a.vou_date,a.stock_code";
        try {
            reportDao.executeSql(delSql, opSql, purSql, retInSql, stockInSql, stockOutSql, saleSql, returnOutSql);
        } catch (Exception e) {
            log.error(String.format("calculateClosing: %s", e.getMessage()));
        }
        log.info("calculate closing.");

    }

    private void calculateOpening(String opDate, String fromDate,
                                  String typeCode, String catCode,
                                  String brandCode, String stockCode,
                                  String compCode, Integer macId) {
        //delete tmp
        String delSql = "delete from tmp_stock_opening where mac_id = " + macId + "";
        //opening
        String opSql = "insert into tmp_stock_opening(tran_date,stock_code,ttl_qty,loc_code,unit,mac_id)\n" +
                "select '" + fromDate + "' op_date ,stock_code,sum(qty) ttl_qty,loc_code,unit," + macId + " \n" +
                "from (\n" +
                "select stock_code,sum(qty) qty,loc_code, unit\n" +
                "from v_opening\n" +
                "where date(op_date) = '" + opDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty) qty,loc_code, pur_unit\n" +
                "from v_purchase\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,pur_unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty) qty,loc_code, unit\n" +
                "from v_return_in\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(in_qty) qty,loc_code, in_unit\n" +
                "from v_stock_io\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = false\n" +
                "and in_qty is not null and in_unit is not null\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,in_unit\n" +
                "\tunion all \n" +
                "select stock_code,sum(out_qty)*-1 qty,loc_code, out_unit\n" +
                "from v_stock_io\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = false\n" +
                "and out_qty is not null and out_unit is not null\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,out_unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty)*-1 qty,loc_code, unit\n" +
                "from v_return_out\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty)*-1 qty,loc_code, sale_unit\n" +
                "from v_sale\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,sale_unit)a\n" +
                "group by stock_code,unit";
        try {
            reportDao.executeSql(delSql, opSql);
        } catch (Exception e) {
            log.error(String.format("calculateOpening: %s", e.getMessage()));
        }
        log.info("calculate opening.");
    }

    @Override
    public void insertTmp(List<String> listStr, Integer macId, String taleName) {
        try {
            deleteTmp(taleName, macId);
            for (String str : listStr) {
                String sql = "insert into " + taleName + "(f_code,mac_id)\n" +
                        "select '" + str + "'," + macId + "";
                executeSql(sql);
            }
        } catch (Exception e) {
            log.error(String.format("insertTmp: %s", e.getMessage()));
        }
    }

    private void deleteTmp(String tableName, Integer macId) throws Exception {
        String delSql = "delete from " + tableName + " where mac_id =" + macId + "";
        executeSql(delSql);
    }

    private void calculatePrice(String toDate, String opDate, String compCode, String stockCode, Integer macId) {
        try {
            String delSql = "delete from tmp_stock_price where mac_id = " + macId + "";
            String purSql = "insert into tmp_stock_price(tran_option,stock_code,pur_avg_price,mac_id)\n" +
                    "select 'PUR-AVG',stock_code,avg(avg_price)," + macId + "\n" +
                    "from (\n" +
                    "select 'PUR-AVG',pur.stock_code,avg(pur.pur_price/rel.smallest_qty) avg_price\n" +
                    "from v_purchase pur\n" +
                    "join v_relation rel\n" +
                    "on pur.rel_code = rel.rel_code\n" +
                    "and pur.pur_unit = rel.unit\n" +
                    "where deleted = 0 and comp_code ='" + compCode + "'\n" +
                    "and date(pur.vou_date) <= '" + toDate + "'\n" +
                    "group by pur.stock_code\n" +
                    "\tunion all\n" +
                    "select 'OP',op.stock_code,avg(op.price/rel.smallest_qty) avg_price\n" +
                    "from v_opening op\n" +
                    "join v_relation rel\n" +
                    "on op.rel_code = rel.rel_code\n" +
                    "and op.unit = rel.unit\n" +
                    "where op.price > 0\n" +
                    "and op.deleted =0 and op.comp_code ='" + compCode + "'\n" +
                    "and date(op.op_date) = '" + opDate + "'\n" +
                    "and (op.stock_code = '-' or '-' = '-')\n" +
                    "group by op.stock_code)a\n" +
                    "group by stock_code";
            String sInSql = "insert into tmp_stock_price(tran_option,stock_code,in_avg_price,mac_id)\n" +
                    "select 'SIN-AVG',stock_code,avg(avg_price)," + macId + "\n" +
                    "from(\n" +
                    "select 'SIN-AVG',sio.stock_code,avg(sio.cost_price/rel.smallest_qty) avg_price\n" +
                    "from v_stock_io sio\n" +
                    "join v_relation rel\n" +
                    "on sio.rel_code = rel.rel_code\n" +
                    "and sio.in_unit = rel.unit\n" +
                    "where in_qty is not null and in_unit is not null and cost_price >0\n" +
                    "and deleted = 0 and comp_code ='" + compCode + "'\n" +
                    "and date(sio.vou_date) <= '" + toDate + "'\n" +
                    "and (sio.stock_code = '-' or '-' = '-')\n" +
                    "group by sio.stock_code\n" +
                    "\tunion all\n" +
                    "select 'OP',op.stock_code,avg(op.price/rel.smallest_qty) avg_price\n" +
                    "from v_opening op\n" +
                    "join v_relation rel\n" +
                    "on op.rel_code = rel.rel_code\n" +
                    "and op.unit = rel.unit\n" +
                    "where op.price > 0\n" +
                    "and op.deleted =0 and op.comp_code ='" + compCode + "'\n" +
                    "and date(op.op_date) = '" + opDate + "'\n" +
                    "and (op.stock_code = '-' or '-' = '-')\n" +
                    "group by op.stock_code\n" +
                    "\tunion all\n" +
                    "select 'SOUT-AVG',sio.stock_code,avg(sio.cost_price/rel.smallest_qty) avg_price\n" +
                    "from v_stock_io sio\n" +
                    "join v_relation rel\n" +
                    "on sio.rel_code = rel.rel_code\n" +
                    "and sio.out_unit = rel.unit\n" +
                    "where out_qty is not null and out_unit is not null and cost_price >0\n" +
                    "and deleted = 0 and comp_code ='" + compCode + "'\n" +
                    "and date(sio.vou_date) <= '" + toDate + "'\n" +
                    "and (sio.stock_code = '-' or '-' = '-')\n" +
                    "group by sio.stock_code\n" +
                    ")a\n" +
                    "group by stock_code";
            String purRecentSql = "insert into tmp_stock_price(stock_code,tran_option,pur_recent_price,mac_id)\n" +
                    "select stock_code,'PUR_RECENT',avg(pur.pur_price/rel.smallest_qty)," + macId + "\n" +
                    "from v_purchase pur\n" +
                    "join v_relation rel on \n" +
                    "pur.rel_code = rel.rel_code\n" +
                    "and pur.pur_unit = rel.unit\n" +
                    "where vou_no in (\n" +
                    "select max(vou_no)\n" +
                    "from v_purchase\n" +
                    "where deleted = 0 and comp_code ='" + compCode + "'\n" +
                    "and date(vou_date) <= '" + toDate + "'\n" +
                    "group by stock_code)\n" +
                    "group by stock_code";
            reportDao.executeSql(delSql, purSql, sInSql, purRecentSql);
        } catch (Exception e) {
            log.error(String.format("calculatePrice: %s", e.getMessage()));
        }
    }
}
