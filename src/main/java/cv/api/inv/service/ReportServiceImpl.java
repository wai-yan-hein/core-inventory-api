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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
    @Autowired
    private WebClient userApi;
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private final HashMap<String, List<UnitRelationDetail>> hmRelation = new HashMap<>();
    private final HashMap<String, String> hmUser = new HashMap<>();

    public String getAppUser(String userCode) {
        String userShort;
        if (hmUser.get(userCode) == null) {
            Mono<AppUser> result = userApi.get().uri(builder -> builder.path("/user/find-appuser").queryParam("userCode", userCode).build()).retrieve().bodyToMono(AppUser.class);
            userShort = result.block() == null ? "" : Objects.requireNonNull(result.block()).getUserShortName();
            hmUser.put(userCode, userShort);
        } else {
            userShort = hmUser.get(userCode);
        }
        return userShort;
    }

    @Override
    public void executeSql(String... sql) throws Exception {
        reportDao.executeSql(sql);
    }

    @Override
    public String getOpeningDate(String compCode, Integer deptId) {
        String opDate = "1998-10-07";
        String sql = "select max(op_date) op_date from op_his where deleted =0 and comp_code ='" + compCode + "' and dept_id =" + deptId + "";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    opDate = Util1.toDateStr(rs.getDate("op_date"), "yyyy-MM-dd");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info(opDate);
        return opDate;
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
        String sql = "select t.trader_name,v.remark,v.vou_no,v.vou_date,v.stock_name, \n" + "v.qty,v.sale_price,v.sale_unit,v.sale_amt,v.vou_total,v.discount,v.paid,v.vou_balance,\n" + "t.phone,t.address,l.loc_name,v.created_by,v.comp_code,c.cat_name\n" + "from v_sale v join trader t\n" + "on v.trader_code = t.code\n" + "join location l on v.loc_code = l.loc_code\n" + "left join category c on v.cat_code = c.cat_code\n" + "where v.vou_no ='" + vouNo + "'";
        ResultSet rs = reportDao.executeSql(sql);
        while (rs.next()) {
            VSale sale = new VSale();
            String remark = rs.getString("remark");
            String refNo = "-";
            if (remark.contains("/")) {
                try {
                    String[] split = remark.split("/");
                    remark = split[0];
                    refNo = split[1];
                } catch (Exception ignored) {
                }
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
            sale.setLocationName(rs.getString("loc_name"));
            sale.setCreatedBy(getAppUser(rs.getString("created_by")));
            sale.setCompCode(rs.getString("comp_code"));
            sale.setCategoryName(rs.getString("cat_name"));
            saleList.add(sale);
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseVoucher(String vouNo) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select t.trader_name,p.remark,p.vou_no,\n" + "p.vou_date,p.stock_name,p.pur_unit,qty,p.pur_price,p.pur_amt,p.vou_total,p.discount,p.paid,p.balance\n" + "from v_purchase p join trader t\n" + "on p.trader_code = t.code\n" + "where p.vou_no ='" + vouNo + "'";
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
    public List<VSale> getSaleByCustomerSummary(String fromDate, String toDate, String curCode, String traderCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select sh.vou_date,sh.vou_no,sh.trader_code,t.trader_name,sh.saleman_code,sm.saleman_name,sh.remark,sh.vou_total\n" + "from sale_his sh join trader t\n" + "on sh.trader_code = t.code\n" + "left join sale_man sm\n" + "on sh.saleman_code = sm.saleman_code\n" + "where sh.trader_code = '" + traderCode + "' or '-' = '" + traderCode + "'\n" + "and sh.deleted =false\n" + "and sh.comp_code = '" + compCode + "'\n" + "and sh.cur_code = '" + curCode + "'\n" + "and date(sh.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by t.trader_name,sh.vou_date";
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
        String sql = "select v.vou_date,v.vou_no,v.saleman_code,sm.saleman_name,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" + "from v_sale v left join sale_man sm on v.saleman_code = sm.saleman_code\n" + "where (v.saleman_code = '" + smCode + "' or '-' = '" + smCode + "')\n" + "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" + "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and v.cur_code = '" + curCode + "'\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by sm.saleman_name,v.vou_date,v.vou_no";
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
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getFloat("sale_price"));
                sale.setSaleAmount(rs.getFloat("sale_amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleBySaleManSummary(String fromDate, String toDate, String curCode, String smCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select sh.vou_date,sh.vou_no,sh.trader_code,t.trader_name,sh.saleman_code,sm.saleman_name,sh.remark,sh.vou_total\n" + "from sale_his sh join trader t\n" + "on sh.trader_code = t.code\n" + "left join sale_man sm\n" + "on sh.saleman_code = sm.saleman_code\n" + "where (sh.saleman_code = '" + smCode + "' or '-' = '" + smCode + "')\n" + "and sh.deleted =false\n" + "and sh.comp_code = '" + compCode + "'\n" + "and sh.cur_code = '" + curCode + "'\n" + "and date(sh.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by sm.saleman_name,sh.vou_date";
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
    public List<VSale> getSaleByCustomerDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" + "from v_sale v join trader t\n" + "on v.trader_code = t.code\n" + "where (v.trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" + "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" + "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and (v.cur_code = '" + curCode + "' or '-' = '" + curCode + "')\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by t.trader_name,v.vou_date,v.vou_no";
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
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getFloat("sale_price"));
                sale.setSaleAmount(rs.getFloat("sale_amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseBySupplierSummary(String fromDate, String toDate, String curCode, String traderCode, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select sh.vou_date,sh.vou_no,sh.trader_code,t.trader_name,sh.remark,sh.vou_total\n" + "from pur_his sh join trader t\n" + "on sh.trader_code = t.code\n" + "where sh.trader_code = '" + traderCode + "' or '-' = '" + traderCode + "'\n" + "and sh.deleted =false\n" + "and sh.comp_code = '" + compCode + "'\n" + "and sh.cur_code = '" + curCode + "'\n" + "and date(sh.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by t.trader_name,sh.vou_date";
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
    public List<VPurchase> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,\n" + "v.stock_name,v.qty,v.pur_unit,v.pur_price,v.pur_amt\n" + "from v_purchase v join trader t\n" + "on v.trader_code = t.code\n" + "where (v.trader_code ='" + traderCode + "' or '-' = '" + traderCode + "')\n" + "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" + "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and (v.cur_code = '" + curCode + "' or '-' ='" + curCode + "')\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by t.trader_name,v.vou_no;";
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
                p.setPurUnit(rs.getString("pur_unit"));
                p.setPurPrice(rs.getFloat("pur_price"));
                p.setPurAmount(rs.getFloat("pur_amt"));
                purchaseList.add(p);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VSale> getSaleByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.s_user_code,v.stock_name,v.remark,v.trader_code,t.trader_name,v.qty,v.sale_unit,v.sale_amt\n" + "from v_sale v join trader t\n" + "on v.trader_code = t.code\n" + "where (v.stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (loc_code = '" + locCode + "' or '-' = '" + locCode + "')\n" + "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" + "and v.cur_code = '" + curCode + "'\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by v.s_user_code,v.vou_date,v.vou_no";
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
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSaleAmount(rs.getFloat("sale_amt"));
                sale.setRemark(rs.getString("remark"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleByStockDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,v.s_user_code,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" + "from v_sale v join trader t\n" + "on v.trader_code = t.code\n" + "where (v.stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (loc_code = '" + locCode + "' or '-' = '" + locCode + "')\n" + "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" + "and v.cur_code = '" + curCode + "'\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by v.s_user_code,v.vou_no";
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
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getFloat("sale_price"));
                sale.setSaleAmount(rs.getFloat("sale_amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseByStockDetail(String fromDate, String toDate, String curCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,\n" + "v.s_user_code,v.stock_name,v.qty,v.pur_unit,v.pur_price,v.pur_amt\n" + "from v_purchase v join trader t\n" + "on v.trader_code = t.code\n" + "where (v.stock_code = '" + stockCode + "' or '-'='" + stockCode + "')\n" + "and (v.stock_type_code = '" + typeCode + "' or '-'='" + typeCode + "')\n" + "and (v.brand_code = '" + brandCode + "' or '-'='" + brandCode + "')\n" + "and (v.category_code = '" + catCode + "' or '-'='" + catCode + "')\n" + "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" + "and v.cur_code = '" + curCode + "'\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "order by v.s_user_code,v.vou_date,v.vou_no;";
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
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" + "from (\n" + "select pur_unit,pur_price/rel.smallest_qty smallest_price,pd.rel_code\n" + "from v_purchase pd\n" + "join v_relation rel on pd.rel_code = rel.rel_code\n" + "and pd.pur_unit =  rel.unit\n" + "where pd.stock_code = '" + stockCode + "' and vou_no = (\n" + "select ph.vou_no\n" + "from pur_his ph, pur_his_detail pd\n" + "where date(ph.vou_date)<= '" + purDate + "' \n" + "and deleted = 0\n" + "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" + "and pd.stock_code = '" + stockCode + "'\n" + "group by ph.vou_no\n" + "order by ph.vou_date desc\n" + "limit 1\n" + "))a\n" + "join v_relation rel\n" + "on a.rel_code =rel.rel_code\n" + "and rel.unit = '" + unit + "'";
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
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" + "from (select sale_unit,sale_price/rel.smallest_qty smallest_price,pd.rel_code\n" + "from v_sale pd\n" + "join v_relation rel on pd.rel_code = rel.rel_code\n" + "and pd.sale_unit =  rel.unit\n" + "and pd.stock_code = '" + stockCode + "'\n" + "where vou_no = (\n" + "select ph.vou_no\n" + "from sale_his ph, sale_his_detail pd\n" + "where date(ph.vou_date)<= '" + saleDate + "' and deleted = 0\n" + "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" + "and pd.stock_code = '" + stockCode + "'\n" + "order by ph.vou_date desc limit 1" + "))a\n" + "join v_relation rel\n" + "on a.rel_code =rel.rel_code\n" + "and rel.unit = '" + unit + "'";
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
        String sql = "select cost_price,stock_code,max(unique_id) \n"
                + "from stock_in_out_detail\n" + "where stock_code = '" + stockCode + "'and (in_unit = '" + unit + "' or out_unit = '" + unit + "')\n" + "and vou_no = (select sio.vou_no \n" + "from stock_in_out sio , stock_in_out_detail siod\n" + "where date(vou_date) <= '" + vouDate + "' and deleted =0\n" + "and sio.vou_no = siod.vou_no\n" + "and cost_price <> 0\n" + "and siod.stock_code = '" + stockCode + "' and (in_unit ='" + unit + "' or out_unit = '" + unit + "')\n" + "order by sio.vou_date desc limit 1)\n";
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

    private void calStockBalanceByLocation(String typeCode, String cateCode, String brandCode, String stockCode, boolean calSale, String compCode, Integer macId) {
        String delSql = "delete from tmp_stock_balance where mac_id = " + macId + "";
        String sql = "insert into tmp_stock_balance(stock_code, qty, unit, loc_code,smallest_qty, mac_id)\n"
                + "select stock_code,qty,unit,loc_code,sum(smallest_qty) smallest_qty," + macId + "\n"
                + "from (\n"
                + "\tselect a.stock_code,sum(a.qty) qty,a.unit,a.loc_code,sum(a.qty)*rel.smallest_qty smallest_qty\n"
                + "\tfrom(\n"
                + "\t\tselect stock_code,sum(qty) as qty,unit,loc_code\n"
                + "\t\tfrom v_opening\n" + "\t\twhere deleted = 0\n"
                + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code, unit , loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(qty) * - 1 as qty,sale_unit,loc_code\n"
                + "\t\tfrom v_sale \n"
                + "\t\twhere deleted = 0\n"
                + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (cat_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (calculate =1 and 0 = " + calSale + ")\n"
                + "\t\tgroup by stock_code ,sale_unit ,loc_code \n"
                + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(qty) as qty,pur_unit,loc_code\n"
                + "\t\tfrom\n" + "\t\tv_purchase \n" + "\t\twhere deleted = 0\n"
                + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code , pur_unit , loc_code \n"
                + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(qty) as qty,unit,loc_code\n"
                + "\t\tfrom v_return_in\n" + "\t\twhere deleted = 0\n" + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code,unit ,loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(qty) * - 1 as qty,unit,loc_code\n" + "\t\tfrom\n" + "\t\tv_return_out\n"
                + "\t\twhere deleted = 0\n" + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code  , unit , loc_code \n"
                + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(in_qty),in_unit,loc_code\n"
                + "\t\tfrom\n" + "\t\tv_stock_io\n" + "\t\twhere in_qty is not null\n"
                + "\t\tand in_unit is not null\n" + "\t\tand deleted = 0\n"
                + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code ,in_unit ,loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(out_qty) * - 1,out_unit,loc_code\n"
                + "\t\tfrom\n" + "\t\tv_stock_io\n" + "\t\twhere out_qty is not null\n"
                + "\t\tand out_unit is not null\n" + "\t\tand deleted = 0\n"
                + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code , out_unit , loc_code\n"
                + "\t\t\tunion all\n" + "\t\tselect stock_code,sum(qty) * - 1,unit,loc_code_from\n"
                + "\t\tfrom v_transfer \n" + "\t\twhere deleted = 0\n"
                + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code, unit , loc_code_from\n"
                + "\t\t\tunion all\n" + "\t\tselect stock_code,sum(qty),unit,loc_code_to\n"
                + "\t\tfrom v_transfer \n" + "\t\twhere deleted = 0\n" + "\t\tand (comp_code = '" + compCode + "' or '-' ='" + compCode + "')\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code , unit , loc_code_to) a\n"
                + "join stock s\n" + "on a.stock_code = s.stock_code\n"
                + "join v_relation rel on s.rel_code = rel.rel_code \n" + "and a.unit = rel.unit\n"
                + "group by a.stock_code,a.unit,a.loc_code) b\n" + "group by b.stock_code,b.loc_code";
        try {
            reportDao.executeSql(delSql, sql);
        } catch (Exception e) {
            log.error(String.format("calStockBalance: %s", e.getMessage()));
        }
    }

    private void calStockBalance(String typeCode, String cateCode, String brandCode, String stockCode, String compCode, Integer macId) {
        String delSql = "delete from tmp_stock_balance where mac_id = " + macId + "";
        String sql = "insert into tmp_stock_balance(stock_code, qty, unit, loc_code,smallest_qty, mac_id)\n" + "select stock_code,qty,unit,loc_code,sum(smallest_qty) smallest_qty," + macId + "\n" + "from (\n" + "\tselect a.stock_code,sum(a.qty) qty,a.unit,a.loc_code,sum(a.qty)*rel.smallest_qty smallest_qty\n" + "\tfrom(\n" + "\t\tselect stock_code,sum(qty) as qty,unit,loc_code\n" + "\t\tfrom v_opening\n" + "\t\twhere deleted = 0\n" + "\t\tand comp_code = '" + compCode + "'\n" + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" + "\t\tand calculate =1\n" + "\t\tgroup by stock_code , unit \n" + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(qty) * - 1 as qty,sale_unit,loc_code\n" + "\t\tfrom v_sale \n" + "\t\twhere deleted = 0\n" + "\t\tand comp_code = '" + compCode + "'\n" + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" + "\t\tand (cat_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" + "\t\tand calculate =1\n" + "\t\tgroup by stock_code ,sale_unit \n" + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(qty) as qty,pur_unit,loc_code\n" + "\t\tfrom\n" + "\t\tv_purchase \n" + "\t\twhere deleted = 0\n" + "\t\tand comp_code = '" + compCode + "'\n" + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" + "\t\tand calculate =1\n" + "\t\tgroup by stock_code, pur_unit \n" + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(qty) as qty,unit,loc_code\n" + "\t\tfrom v_return_in\n" + "\t\twhere deleted = 0\n" + "\t\tand comp_code = '" + compCode + "'\n" + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" + "\t\tand calculate =1\n" + "\t\tgroup by stock_code,unit \n" + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(qty) * - 1 as qty,unit,loc_code\n" + "\t\tfrom\n" + "\t\tv_return_out\n" + "\t\twhere deleted = 0\n" + "\t\tand comp_code = '" + compCode + "'\n" + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" + "\t\tand calculate =1\n" + "\t\tgroup by stock_code, unit \n" + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(in_qty),in_unit,loc_code\n" + "\t\tfrom\n" + "\t\tv_stock_io\n" + "\t\twhere in_qty is not null\n" + "\t\tand in_unit is not null\n" + "\t\tand deleted = 0\n" + "\t\tand comp_code = '" + compCode + "'\n" + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" + "\t\tand calculate =1\n" + "\t\tgroup by stock_code ,in_unit \n" + "\t\t\tunion all \n" + "\t\tselect stock_code,sum(out_qty) * - 1,out_unit,loc_code\n" + "\t\tfrom\n" + "\t\tv_stock_io\n" + "\t\twhere out_qty is not null\n" + "\t\tand out_unit is not null\n" + "\t\tand deleted = 0\n" + "\t\tand comp_code = '" + compCode + "'\n" + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" + "\t\tand calculate =1\n" + "\t\tgroup by stock_code , out_unit) a\n" + "join stock s\n" + "on a.stock_code = s.stock_code\n" + "join v_relation rel on s.rel_code = rel.rel_code \n" + "and a.unit = rel.unit\n" + "group by a.stock_code,a.unit,a.loc_code) b\n" + "group by b.stock_code";
        try {
            reportDao.executeSql(delSql, sql);
        } catch (Exception e) {
            log.error(String.format("calStockBalance: %s", e.getMessage()));
        }
    }

    @Override
    public List<VStockBalance> getStockBalance(String typeCode, String catCode, String brandCode, String stockCode,
                                               boolean calSale, String compCode, Integer deptId, Integer macId) throws Exception {
        calStockBalanceByLocation(typeCode, catCode, brandCode, stockCode, calSale, compCode, macId);
        List<VStockBalance> balances = new ArrayList<>();
        String sql = "select tmp.stock_code,tmp.loc_code,l.loc_name,tmp.unit,tmp.wt,tmp.qty,tmp.smallest_qty,s.user_code,s.rel_code,s.stock_name\n" + "from tmp_stock_balance tmp join location l\n" + "on tmp.loc_code = l.loc_code\n" + "join stock s on tmp.stock_code = s.stock_code\n" + "where tmp.mac_id = " + macId + "";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockBalance b = new VStockBalance();
                b.setUserCode(rs.getString("user_code"));
                b.setStockCode(rs.getString("stock_code"));
                b.setStockName(rs.getString("stock_name"));
                b.setWeight(rs.getFloat("wt"));
                b.setLocationName(rs.getString("loc_name"));
                b.setLocCode(rs.getString("loc_code"));
                float smallQty = rs.getFloat("smallest_qty");
                String relCode = rs.getString("rel_code");
                b.setTotalQty(null);
                b.setUnitName(getRelStr(relCode, compCode, deptId, smallQty));
                balances.add(b);
            }
        }
        return balances;
    }

    private String getRelStr(String relCode, String compCode, Integer deptId, float smallestQty) {
        //generate unit relation.
        StringBuilder relStr = new StringBuilder();
        if (smallestQty != 0 && !Objects.isNull(relCode)) {
            if (hmRelation.get(relCode) == null) {
                hmRelation.put(relCode, relationDao.getRelationDetail(relCode, compCode, deptId));
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
    public List<ClosingBalance> getClosingStock(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception {
        insertPriceDetail(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        insertClosingIntoColumn(macId);
        String sql = "select st.user_code type_user_code,st.stock_type_name,s.user_code,s.stock_name,a.*,\n" + "(a.pur_amt/a.pur_qty) * bal_qty cl_amt\n" + "from (\n" + "select stock_code,\n" + "sum(op_qty) op_qty,sum(op_amt) op_amt,\n" + "sum(pur_qty) pur_qty,sum(pur_amt) pur_amt,\n" + "sum(in_qty) in_qty,sum(in_amt) in_amt,\n" + "sum(out_qty) out_qty,sum(out_amt) out_amt,\n" + "sum(sale_qty) sale_qty,sum(sale_amt) sale_amt,\n" + "sum(op_qty)+sum(pur_qty)+sum(in_qty)+sum(out_qty)+sum(sale_qty) bal_qty,comp_code\n" + "from tmp_closing_column\n" + "where mac_id = " + macId + "\n" + "group by stock_code\n" + ")a\n" + "join stock s on a.stock_code = s.stock_code\n" + "join stock_type st on s.stock_type_code = st.stock_type_code\n" + "order by st.user_code,s.user_code";
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
    public List<ReorderLevel> getReorderLevel(String typeCode, String catCode,
                                              String brandCode, String stockCode,
                                              String compCode, Integer deptId, Integer macId) throws Exception {
        calStockBalance(typeCode, catCode, brandCode, stockCode, compCode, macId);
        String sql1 = "select a.*,if(bal_small_qty < min_small_qty,1,if(bal_small_qty > max_qty,2,3)) sorting\n"
                + "from (\n"
                + "select r.*,r.min_qty*rel.smallest_qty min_small_qty,r.max_qty*rel.smallest_qty max_small_qty,\n"
                + "ifnull(tmp.smallest_qty,0) bal_small_qty,s.user_code,s.stock_name,s.rel_code,rel.rel_name\n"
                + "from reorder_level r join stock s\n"
                + "on r.stock_code = s.stock_code\n"
                + "join v_relation rel on s.rel_code = rel.rel_code \n"
                + "and r.min_unit = rel.unit\n"
                + "and r.max_unit = rel.unit\n"
                + "join tmp_stock_balance tmp\n"
                + "on r.stock_code = tmp.stock_code\n"
                + "and tmp.mac_id =" + macId + "\n"
                + "and r.comp_code = '" + compCode + "' and r.dept_id =" + deptId + "\n" + ")a\n"
                + "order by sorting";
        ResultSet rs = reportDao.executeSql(sql1);
        List<ReorderLevel> reorderLevels = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                ReorderLevel r = new ReorderLevel();
                ReorderKey key = new ReorderKey();
                key.setDeptId(deptId);
                key.setCompCode(compCode);
                key.setStockCode(rs.getString("stock_code"));
                r.setKey(key);
                String relCode = rs.getString("rel_code");
                r.setStockName(rs.getString("stock_name"));
                r.setUserCode(rs.getString("user_code"));
                r.setRelName(rs.getString("rel_name"));
                r.setMinQty(rs.getFloat("min_qty"));
                r.setMinUnitCode(rs.getString("min_unit"));
                r.setMaxQty(rs.getFloat("max_qty"));
                r.setMaxUnitCode(rs.getString("max_unit"));
                //max qty
                r.setMaxSmallQty(rs.getFloat("max_small_qty"));
                //min qty
                r.setMinSmallQty(rs.getFloat("min_small_qty"));
                //bal qty
                float balSmallQty = rs.getFloat("bal_small_qty");
                r.setBalUnit(getRelStr(relCode, compCode, deptId, balSmallQty));
                r.setBalSmallQty(balSmallQty);
                reorderLevels.add(r);
            }
        }
        return reorderLevels;
    }

    @Override
    public void generateReorder(String compCode, Integer deptId) throws Exception {
        //generate reorder
        String rSql = "select s.stock_code,s.pur_unit,rl.stock_code ro_stock_code,s.comp_code,s.dept_id\n" + "from stock s left join reorder_level rl\n" + "on s.stock_code = rl.stock_code\n" + "where s.comp_code= '" + compCode + "' and rl.stock_code is null";
        ResultSet rs = reportDao.executeSql(rSql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                String stockCode = rs.getString("stock_code");
                String roStockCode = rs.getString("ro_stock_code");
                String purUnit = rs.getString("pur_unit");
                if (Objects.isNull(roStockCode)) {
                    ReorderLevel rl = new ReorderLevel();
                    ReorderKey key = new ReorderKey();
                    key.setDeptId(deptId);
                    key.setStockCode(stockCode);
                    key.setCompCode(compCode);
                    rl.setKey(key);
                    rl.setMinQty(0.0f);
                    rl.setMinUnitCode(purUnit);
                    rl.setMaxQty(0.0f);
                    rl.setMaxUnitCode(purUnit);
                    getSession().save(rl);
                }
            }
        }
    }

    @Override
    public List<General> getStockListByGroup(String typeCode, String compCode, Integer macId) throws Exception {
        String sql = "select s.stock_code,s.user_code,s.stock_name,s.stock_type_code,\n" + "st.stock_type_name,b.brand_name,c.cat_name,rel.rel_name\n" + "from stock s \n" + "join stock_type st on s.stock_type_code = st.stock_type_code\n" + "left join stock_brand b on s.brand_code = b.brand_code\n" + "left join category c on s.category_code = c.cat_code\n" + "left join unit_relation rel on s.rel_code = rel.rel_code\n" + "where s.active = true and s.comp_code = '" + compCode + "' \n" + "and (s.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "order by st.user_code";
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
        String sql = "select t.user_code,t.trader_name, sum(sh.vou_total) vou_total,count(*) vou_qty\n" + "from sale_his sh join trader t\n" + "on sh.trader_code = t.code\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and sh.comp_code = '" + compCode + "' and sh.deleted = 0\n" + "group by sh.trader_code\n" + "order by vou_total desc";
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
        String sql = "select s.user_code,s.saleman_name,count(*) vou_qty,sum(sh.vou_total) vou_total\n" + "from sale_his sh left join sale_man s\n" + "on sh.saleman_code = s.saleman_code\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and sh.comp_code = '" + compCode + "' and sh.deleted = 0\n" + "group by sh.saleman_code\n" + "order by vou_total desc";
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
    public List<General> getTopSaleByStock(String fromDate, String toDate, String typeCode, String brandCode, String catCode, String compCode, Integer deptId) throws Exception {
        String sql = "select a.*,sum(ttl_amt) ttl_amt,sum(a.ttl_qty*rel.smallest_qty) smallest_qty\n" + "from (select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,sale_unit,sum(sale_amt) ttl_amt,rel_code\n" + "from v_sale\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" + "and deleted = 0\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "group by stock_code,sale_unit\n" + ")a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.sale_unit = rel.unit\n" + "group by stock_code\n" + "order by smallest_qty desc";
        ResultSet rs = reportDao.executeSql(sql);
        List<General> generals = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                General g = new General();
                g.setStockCode(rs.getString("s_user_code"));
                g.setStockName(rs.getString("stock_name"));
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                g.setQtyRel(getRelStr(relCode, compCode, deptId, smallQty));
                g.setAmount(rs.getFloat("ttl_amt"));
                generals.add(g);
            }
        }
        return generals;
    }

    @Override
    public List<ClosingBalance> getClosingStockDetail(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception {
        insertPriceDetail(fromDate, toDate, typeCode, catCode, brandCode, stockCode, compCode, macId);
        insertClosingIntoColumn(macId);
        String sql = "select s.user_code,s.stock_name,a.*,\n" + "(a.pur_amt/a.pur_qty) * bal_qty cl_amt\n" + "from (\n" + "select stock_code,tran_date vou_date,vou_no,\n" + "sum(op_qty) op_qty,sum(op_amt) op_amt,\n" + "sum(pur_qty) pur_qty,sum(pur_amt) pur_amt,\n" + "sum(in_qty) in_qty,sum(in_amt) in_amt,\n" + "sum(out_qty) out_qty,sum(out_amt) out_amt,\n" + "sum(sale_qty) sale_qty,sum(sale_amt) sale_amt,\n" + "sum(op_qty)+sum(pur_qty)+sum(in_qty)+sum(out_qty)+sum(sale_qty) bal_qty,comp_code\n" + "from tmp_closing_column\n" + "where mac_id = " + macId + "\n" + "group by stock_code,tran_date,vou_no\n" + ")a\n" + "join stock s on a.stock_code = s.stock_code\n" + "order by s.user_code,date(a.vou_date) vou_date";
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
    public List<ClosingBalance> getStockInOutSummary(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode,
                                                     String stockCode, boolean calSale, String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, macId);
        String getSql = "select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,\n" + "s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name\n" + "from (select stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,\n" + "sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty\n" + "from tmp_stock_io_column\n" + "where mac_id = " + macId + "\n" + "group by stock_code)a\n" + "join stock s on a.stock_code = s.stock_code\n" + "join stock_type st on s.stock_type_code = st.stock_type_code\n" + "group by stock_code\n" + "order by s.user_code";
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
                    b.setOpenQty(opQty);
                    b.setOpenRel(getRelStr(relCode, compCode, deptId, opQty));
                    b.setPurQty(purQty);
                    b.setPurRel(getRelStr(relCode, compCode, deptId, purQty));
                    b.setInQty(inQty);
                    b.setInRel(getRelStr(relCode, compCode, deptId, inQty));
                    b.setSaleQty(saleQty);
                    b.setSaleRel(getRelStr(relCode, compCode, deptId, saleQty));
                    b.setOutQty(outQty);
                    b.setOutRel(getRelStr(relCode, compCode, deptId, outQty));
                    b.setBalQty(balQty);
                    b.setBalRel(getRelStr(relCode, compCode, deptId, balQty));
                    b.setStockUsrCode(rs.getString("s_user_code"));
                    b.setStockName(rs.getString("stock_name"));
                    balances.add(b);
                }
            }
        } catch (Exception e) {
            log.error("getStockInOutSummary: " + Arrays.toString(e.getStackTrace()));
        }
        return balances;
    }

    @Override
    public void calculateStockInOutDetail(String opDate, String fromDate, String toDate, String typeCode, String catCode,
                                          String brandCode, String stockCode, boolean calSale, String compCode, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, macId);
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
    public List<ClosingBalance> getStockInOutDetail(String typeCode, String compCode, Integer deptId, Integer macId) {
        String getSql = "select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,\n" + "s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name\n" + "from (select tran_option,tran_date,stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,\n" + "sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,remark\n" + "from tmp_stock_io_column\n" + "where mac_id = " + macId + "\n" + "group by tran_date,stock_code,tran_option)a\n" + "join stock s on a.stock_code = s.stock_code\n" + "join stock_type st on s.stock_type_code = st.stock_type_code\n" + "group by tran_date,stock_code,tran_option\n" + "order by s.user_code,a.tran_date,a.tran_option";
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
                    b.setOpenRel(getRelStr(relCode, compCode, deptId, opQty));
                    b.setPurRel(getRelStr(relCode, compCode, deptId, purQty));
                    b.setInRel(getRelStr(relCode, compCode, deptId, inQty));
                    b.setSaleRel(getRelStr(relCode, compCode, deptId, saleQty));
                    b.setOutRel(getRelStr(relCode, compCode, deptId, outQty));
                    b.setBalRel(getRelStr(relCode, compCode, deptId, balQty));
                    b.setVouDate(Util1.toDateStr(rs.getDate("tran_date"), "dd/MM/yyyy"));
                    b.setStockUsrCode(rs.getString("s_user_code"));
                    b.setStockName(rs.getString("stock_name"));
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
    public List<StockValue> getStockValue(String opDate, String fromDate, String toDate,
                                          String typeCode, String catCode, String brandCode,
                                          String stockCode, boolean calSale, String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, macId);
        calculatePrice(toDate, opDate, compCode, stockCode, macId);
        List<StockValue> values = new ArrayList<>();
        String getSql = "select a.*,\n" + "sum(ifnull(tmp.pur_avg_price,0)) pur_avg_price,bal_qty*sum(ifnull(tmp.pur_avg_price,0)) pur_avg_amt,\n" + "sum(ifnull(tmp.in_avg_price,0)) in_avg_price,bal_qty*sum(ifnull(tmp.in_avg_price,0)) in_avg_amt,\n" + "sum(ifnull(tmp.std_price,0)) std_price,bal_qty*sum(ifnull(tmp.std_price,0)) std_amt,\n" + "sum(ifnull(tmp.pur_recent_price,0)) pur_recent_price,bal_qty*sum(ifnull(tmp.pur_recent_price,0)) pur_recent_amt,\n" + "sum(ifnull(tmp.fifo_price,0)) fifo_price,bal_qty*sum(ifnull(tmp.fifo_price,0)) fifo_amt,\n" + "sum(ifnull(tmp.lifo_price,0)) lifo_price,bal_qty*sum(ifnull(tmp.lifo_price,0)) lifo_amt,\n" + "s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name\n" + "from (\n" + "select stock_code,sum(op_qty)+sum(pur_qty)+sum(in_qty) +sum(out_qty) +sum(sale_qty) bal_qty,mac_id\n" + "from tmp_stock_io_column\n" + "where mac_id = " + macId + "\n" + "group by stock_code)a\n" + "left join tmp_stock_price tmp\n" + "on a.stock_code  = tmp.stock_code\n" + "and a.mac_id = tmp.mac_id\n" + "join stock s on a.stock_code = s.stock_code\n" + "join stock_type st on s.stock_type_code = st.stock_type_code\n" + "group by a.stock_code\n" + "order by s.user_code";
        try {
            ResultSet rs = reportDao.executeSql(getSql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    StockValue value = new StockValue();
                    value.setStockUserCode(rs.getString("s_user_code"));
                    value.setStockName(rs.getString("stock_name"));
                    value.setBalRel(getRelStr(rs.getString("rel_code"), compCode, deptId, rs.getFloat("bal_qty")));
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
    public List<VOpening> getOpeningByLocation(String typeCode, String brandCode, String catCode, String stockCode, Integer macId, String compCode) throws Exception {
        List<VOpening> openings = new ArrayList<>();
        String sql = "select v.op_date,v.vou_no,v.remark,v.stock_code,v.stock_user_code,v.stock_name,l.loc_name,\n" + "v.unit,v.qty,v.price,v.amount\n" + "from v_opening v join location l\n" + "on v.loc_code = l.loc_code\n" + "where v.deleted = 0\n" + "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (v.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (v.brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and v.loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" + "and v.comp_code ='" + compCode + "' order by l.loc_name,v.stock_user_code\n";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOpening opening = new VOpening();
                opening.setVouDate(Util1.toDateStr(rs.getDate("op_date"), "dd/MM/yyyy"));
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
    public List<VOpening> getOpeningByGroup(String typeCode, String stockCode, String catCode, String brandCode, Integer macId, String compCode) throws Exception {
        List<VOpening> openings = new ArrayList<>();
        String sql = "select a.*,t.stock_type_name\n" + "from (select v.op_date,v.remark,v.stock_type_code,v.stock_code,v.stock_user_code,v.stock_name,l.loc_name,\n" + "unit,qty,price,amount \n" + "from v_opening v join location l \n" + "on v.loc_code = l.loc_code\n" + "where v.deleted = 0 \n" + "and v.comp_code = '" + compCode + "'\n" + "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and (v.brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (v.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "'))a\n" + "join stock_type t on a.stock_type_code = t.stock_type_code\n" + "order by t.stock_type_name,a.stock_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOpening opening = new VOpening();
                opening.setVouDate(Util1.toDateStr(rs.getDate("op_date"), "dd/MM/yyyy"));
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
    public List<VStockIO> getStockIODetailByVoucherType(String vouStatus, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception {
        String sql = "select v.vou_date,v.vou_no,v.remark,v.description,s.user_code vs_user_code,s.description vou_status_name,v.s_user_code,v.stock_name,l.loc_name,\n" + "v.out_qty,v.out_unit,v.cur_code,v.cost_price,v.cost_price* v.out_qty out_amt \n" + "from v_stock_io v join vou_status s\n" + "on v.vou_status = s.code\n" + "join location l on v.loc_code = l.loc_code\n" + "where v.comp_code = '" + compCode + "'\n" + "and v.deleted = 0\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (v.category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" + "and (v.brand_code ='" + brandCode + "' or '-'='" + brandCode + "')\n" + "and (v.vou_status = '" + vouStatus + "' or '-' = '" + vouStatus + "')\n" + "and v.out_qty is not null and v.out_unit is not null\n\n" + "group by date(v.vou_date),v.vou_no,v.stock_code,v.in_unit,v.out_unit,v.cur_code\n" + "order by s.user_code,v.cur_code,v.vou_date,v.vou_no,v.s_user_code";
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
    public List<VStockIO> getStockIOPriceCalender(String vouType, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception {
        String sql = "select v.vou_date,v.vou_no,v.stock_code,v.s_user_code,\n" + "v.stock_name,vs.description vou_status_name,if(v.in_unit is null,v.out_unit,v.in_unit) unit, v.cost_price \n" + "from v_stock_io v join vou_status vs\n" + "on v.vou_status = vs.code\n" + "where v.comp_code = '" + compCode + "'\n" + "and v.deleted = 0\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (v.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (v.brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" + "and (v.vou_status ='" + vouType + "' or '-' ='" + vouType + "')\n" + "group by date(v.vou_date),v.stock_code,v.cost_price,unit\n" + "order by v.s_user_code,v.vou_date\n";
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
    public List<VStockIO> getStockIOHistory(String fromDate, String toDate, String vouStatus, String vouNo, String remark, String desp, String userCode, String stockCode, String locCode, String compCode) throws Exception {
        String sql = "select a.*,v.description vou_status_name\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,description,remark,vou_status,created_by,deleted\n" + "from v_stock_io \n" + "where comp_code = '" + compCode + "'\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" + "and (description like '" + desp + "%' or '-%'= '" + desp + "%')\n" + "and (vou_status = '" + vouStatus + "' or '-'='" + vouStatus + "')\n" + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" + "group by vou_no\n" + ")a\n" + "join vou_status v on a.vou_status = v.code\n" + "order by vou_date,vou_no";
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
    public List<VSale> getSaleHistory(String fromDate, String toDate, String traderCode, String saleManCode, String vouNo, String remark, String reference, String userCode, String stockCode, String locCode, String compCode) throws Exception {
        String sql = "select a.*,t.trader_name,l.loc_name\n" + "from (select  vou_no,date(vou_date) vou_date,remark,created_by,paid,vou_total,deleted,trader_code,loc_code\n" + "from v_sale s \n" + "where comp_code = '" + compCode + "'\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" + "and (reference like '" + reference + "%' or '-%'= '" + reference + "%')\n" + "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (saleman_code ='" + saleManCode + "' or '-' ='" + saleManCode + "')\n" + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" + "group by vou_no\n" + ")a\n" + "join trader t on a.trader_code = t.code\n" + "join location l on a.loc_code = l.loc_code\n" + "order by date(vou_date) desc,vou_no desc";
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
    public List<VPurchase> getPurchaseHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String reference, String userCode, String stockCode, String locCode, String compCode) throws Exception {
        String sql = "select a.*,t.trader_name\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code\n" + "from v_purchase p \n" + "where comp_code = '" + compCode + "'\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" + "and (reference like '" + reference + "%' or '-%'= '" + reference + "%')\n" + "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" + "group by vou_no)a\n" + "join trader t on a.trader_code = t.code\n" + "order by date(vou_date),vou_no";
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
    public List<VReturnIn> getReturnInHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String userCode, String stockCode, String locCode, String compCode) throws Exception {
        String sql = "select a.*,t.trader_name\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code \n" + "from v_return_in \n" + "where comp_code = '" + compCode + "'\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" + "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" + "group by vou_no\n" + ")a\n" + "join trader t on a.trader_code = t.code\n" + "order by vou_date,vou_no";
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
    public List<VReturnOut> getReturnOutHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String userCode, String stockCode, String locCode, String compCode) throws Exception {
        String sql = "select a.*,t.trader_name\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code \n" + "from v_return_out \n" + "where comp_code = '" + compCode + "'\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" + "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" + "group by vou_no\n" + ")a\n" + "join trader t on a.trader_code = t.code\n" + "order by vou_date,vou_no";
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

    @Override
    public List<VOpening> getOpeningHistory(String fromDate, String toDate, String vouNo, String remark, String userCode, String stockCode, String locCode, String compCode) throws Exception {
        String sql = "select v.op_date,v.vou_no,v.remark,v.created_by,v.deleted,l.loc_name \n" + "from v_opening v join location l\n" + "on v.loc_code = l.loc_code\n" + "where v.comp_code = '" + compCode + "'\n" + "and date(v.op_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (v.vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" + "and (v.remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" + "and (v.created_by = '" + userCode + "' or '-'='" + userCode + "')\n" + "and (v.stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (v.loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" + "group by v.vou_no\n" + "order by v.op_date,v.vou_no desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VOpening> openingList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOpening s = new VOpening();
                s.setVouDate(Util1.toDateStr(rs.getDate("op_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setRemark(rs.getString("remark"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setDeleted(rs.getBoolean("deleted"));
                s.setLocationName(rs.getString("loc_name"));
                openingList.add(s);
            }
        }
        return openingList;
    }

    @Override
    public List<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark, String userCode, String stockCode, String locCodeFrom, String locCodeTo, String compCode) throws Exception {

        String sql = "select date(v.vou_date) vou_date,v.vou_no,v.remark,v.ref_no,v.created_by,v.deleted,l.loc_name from_loc_name,ll.loc_name to_loc_name\n" + "from v_transfer v join location l\n" + "on v.loc_code_from = l.loc_code\n" + "join location ll on v.loc_code_to = ll.loc_code\n" + "where v.comp_code = '" + compCode + "'\n" + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (v.vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" + "and (v.ref_no like '" + refNo + "%' or '-%'= '" + refNo + "%')\n" + "and (v.remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" + "and (v.created_by = '" + userCode + "' or '-'='" + userCode + "')\n" + "and (v.stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" + "and (v.loc_code_from ='" + locCodeFrom + "' or '-' ='" + locCodeFrom + "')\n" + "and (v.loc_code_to ='" + locCodeTo + "' or '-' ='" + locCodeTo + "')\n" + "group by v.vou_no\n" + "order by v.vou_date,v.vou_no desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VTransfer> openingList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VTransfer s = new VTransfer();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setRemark(rs.getString("remark"));
                s.setRefNo(rs.getString("ref_no"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setDeleted(rs.getBoolean("deleted"));
                s.setFromLocationName(rs.getString("from_loc_name"));
                s.setToLocationName(rs.getString("to_loc_name"));
                openingList.add(s);
            }
        }
        return openingList;
    }

    @Override
    public List<VSale> getSalePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception {
        String sql = "select s.s_user_code,s.vou_date,s.vou_no,s.stock_code,\n" + "s.stock_name,s.sale_unit,s.sale_price,s.remark,t.trader_name,s.cur_code \n" + "from v_sale s join trader t\n" + "on s.trader_code = t.code\n" + "where s.comp_code = '" + compCode + "'\n" + "and s.deleted = 0\n" + "and date(s.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (s.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and (s.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (s.cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (s.brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" + "group by s.stock_code,s.sale_price,s.sale_unit\n" + "order by s.s_user_code,s.vou_date,s.sale_unit\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VSale> saleList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale s = new VSale();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setStockUserCode(rs.getString("s_user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setSaleUnit(rs.getString("sale_unit"));
                s.setSalePrice(rs.getFloat("sale_price"));
                s.setRemark(rs.getString("remark"));
                s.setTraderName(rs.getString("trader_name"));
                s.setCurCode(rs.getString("cur_code"));
                saleList.add(s);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchasePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception {
        String sql = "select s.s_user_code,s.vou_date,s.vou_no,s.stock_code,\n" + "s.stock_name,s.pur_unit,s.pur_price,s.remark,t.trader_name,s.cur_code \n" + "from v_purchase s join trader t\n" + "on s.trader_code = t.code\n" + "where s.comp_code = '" + compCode + "'\n" + "and s.deleted = 0\n" + "and date(s.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and (s.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and (s.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (s.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (s.brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" + "group by s.stock_code,s.pur_price,s.pur_unit\n" + "order by s.s_user_code,s.vou_date,s.pur_unit\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VPurchase> purchaseList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase s = new VPurchase();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setStockUserCode(rs.getString("s_user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setPurUnit(rs.getString("pur_unit"));
                s.setPurPrice(rs.getFloat("pur_price"));
                s.setRemark(rs.getString("remark"));
                s.setTraderName(rs.getString("trader_name"));
                s.setCurCode(rs.getString("cur_code"));
                purchaseList.add(s);
            }
        }
        return purchaseList;
    }

    @Override
    public Float getSmallestQty(String stockCode, String unit) {
        float qty = 1.0f;
        String sql = "select ud.smallest_qty\n" + "from stock s join unit_relation_detail ud\n" + "on s.rel_code = ud.rel_code\n" + "where s.stock_code ='" + stockCode + "'\n" + "and ud.unit ='" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                qty = rs.getFloat("smallest_qty");
            }
        } catch (Exception e) {
            log.error(String.format("getSmallestQty: %s", e.getMessage()));
        }
        return qty;
    }

    @Override
    public List<String> isStockExist(String stockCode, String compCode) {
        return searchDetail(stockCode, "stock_code", compCode);
    }

    private List<String> searchDetail(String code, String column, String compCode) {
        List<String> str = new ArrayList<>();
        HashMap<String, String> hm = new HashMap<>();
        hm.put("sale_his_detail", "Sale");
        hm.put("pur_his_detail", "Purchase");
        hm.put("ret_in_his_detail", "Return In");
        hm.put("ret_out_his_detail", "Return Out");
        hm.put("stock_in_out_detail", "Stock In/Out");
        hm.put("op_his_detail", "Opening");
        hm.forEach((s, s2) -> {
            String sql = "select exists(select " + column + " from " + s + " where " + column + " ='" + code + "' and comp_code ='" + compCode + "') exist";
            try {
                ResultSet rs = reportDao.executeSql(sql);
                if (rs.next()) {
                    if (rs.getBoolean("exist")) {
                        str.add("Transaction exist in " + s2);
                    }
                }
            } catch (Exception e) {
                log.error(String.format("searchTran: %s", e.getMessage()));
            }

        });
        return str;
    }

    private List<String> searchVoucher(String code, String column, String compCode) {
        List<String> str = new ArrayList<>();
        HashMap<String, String> hm = new HashMap<>();
        hm.put("sale_his", "Sale");
        hm.put("pur_his", "Purchase");
        hm.put("ret_in_his", "Return In");
        hm.put("ret_out_his", "Return Out");
        hm.forEach((s, s2) -> {
            String sql = "select exists(select " + column + " from " + s + " where " + column + " ='" + code + "' and comp_code ='" + compCode + "') exist";
            try {
                ResultSet rs = reportDao.executeSql(sql);
                if (rs.next()) {
                    if (rs.getBoolean("exist")) {
                        str.add("Transaction exist in " + s2);
                    }
                }
            } catch (Exception e) {
                log.error(String.format("searchVoucher: %s", e.getMessage()));
            }

        });
        return str;
    }

    @Override
    public List<String> isTraderExist(String traderCode, String compCode) {
        return searchVoucher(traderCode, "trader_code", compCode);
    }

    @Override
    public List<VReturnIn> getReturnInVoucher(String vouNo, String compCode) {
        String sql = "select stock_name,unit,qty,price,amt,t.trader_name,r.remark,date(vou_date) vou_date,\n" + "r.vou_total,r.paid,r.balance,r.vou_no\n" + "from v_return_in r join trader t\n" + "on r.trader_code = t.code\n" + "where r.comp_code = '" + compCode + "'\n" + "and vou_no ='" + vouNo + "'\n" + "order by unique_id\n ";
        List<VReturnIn> riList = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VReturnIn in = new VReturnIn();
                    in.setStockName(rs.getString("stock_name"));
                    in.setUnit(rs.getString("unit"));
                    in.setQty(rs.getFloat("qty"));
                    in.setPrice(rs.getFloat("price"));
                    in.setAmount(rs.getFloat("amt"));
                    in.setRemark(rs.getString("remark"));
                    in.setVouDate(rs.getString("vou_date"));
                    in.setVouTotal(rs.getFloat("vou_total"));
                    in.setPaid(rs.getFloat("paid"));
                    in.setVouBalance(rs.getFloat("balance"));
                    in.setVouNo(rs.getString("r.vou_no"));
                    in.setTraderName(rs.getString("t.trader_name"));
                    riList.add(in);
                }
            }
        } catch (Exception e) {
            log.error(String.format("getReturnInVoucher: %s", e.getMessage()));
        }
        return riList;
    }

    @Override
    public List<VReturnOut> getReturnOutVoucher(String vouNo, String compCode) {
        String sql = "select stock_name,unit,qty,price,amt,t.trader_name,r.remark,date(vou_date) vou_date,\n" + "r.vou_total,r.paid,r.balance,r.vou_no\n" + "from v_return_out r join trader t\n" + "on r.trader_code = t.code\n" + "where r.comp_code = '" + compCode + "'\n" + "and vou_no ='" + vouNo + "'\n" + "order by unique_id\n ";
        List<VReturnOut> riList = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VReturnOut in = new VReturnOut();
                    in.setStockName(rs.getString("stock_name"));
                    in.setUnit(rs.getString("unit"));
                    in.setQty(rs.getFloat("qty"));
                    in.setPrice(rs.getFloat("price"));
                    in.setAmount(rs.getFloat("amt"));
                    in.setRemark(rs.getString("remark"));
                    in.setVouDate(rs.getString("vou_date"));
                    in.setVouTotal(rs.getFloat("vou_total"));
                    in.setPaid(rs.getFloat("paid"));
                    in.setVouBalance(rs.getFloat("balance"));
                    in.setVouNo(rs.getString("r.vou_no"));
                    in.setTraderName(rs.getString("t.trader_name"));
                    riList.add(in);
                }
            }
        } catch (Exception e) {
            log.error(String.format("getReturnInVoucher: %s", e.getMessage()));
        }
        return riList;
    }


    private void insertClosingIntoColumn(Integer macId) throws Exception {
        //delete tmp
        String delSql = "delete from tmp_closing_column where mac_id = " + macId + "";
        executeSql(delSql);
        //opening
        String opSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,op_qty,op_price,op_amt,op_unit,mac_id,comp_code)\n" + "select tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" + "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" + "from tmp_inv_closing\n" + "where tran_option ='Opening' and mac_id = " + macId + "\n" + "group by tran_option,vou_no,tran_date,stock_code,loc_code,mac_id\n";
        executeSql(opSql);
        //purchase
        String purSql = "insert into tmp_closing_column(tran_option,vou_no,tran_date,stock_code,loc_code,pur_qty,pur_price,pur_amt,pur_unit,mac_id,comp_code)\n" + "select tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" + "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" + "from tmp_inv_closing\n" + "where tran_option ='Purchase' and mac_id = " + macId + "\n" + "group by tran_option,vou_no, tran_date,stock_code,loc_code,mac_id\n";
        executeSql(purSql);
        //stock in
        String inSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,in_qty,in_price,in_amt,in_unit,mac_id,comp_code)\n" + "select 'In' tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" + "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" + "from tmp_inv_closing\n" + "where (tran_option ='ReturnIn' or\n" + "tran_option= 'StockIn') and mac_id = " + macId + "\n" + "group by tran_date,vou_no,stock_code,loc_code,mac_id\n";
        executeSql(inSql);
        //return in
        //sale
        String saleSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,sale_qty,sale_price,sale_amt,sale_unit,mac_id,comp_code)\n" + "select tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty)*-1 ttl_qty,sum(price) ttl_price,\n" + "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" + "from tmp_inv_closing\n" + "where tran_option ='Sale' and mac_id = " + macId + "\n" + "group by tran_option,vou_no, tran_date,stock_code,loc_code,mac_id\n";
        executeSql(saleSql);
        //stock out
        String outSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,out_qty,out_price,out_amt,out_unit,mac_id,comp_code)\n" + "select 'Out' tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty)*-1 ttl_qty,sum(price) ttl_price,\n" + "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" + "from tmp_inv_closing\n" + "where (tran_option ='ReturnOut' or\n" + "tran_option= 'StockOut') and mac_id = " + macId + "\n" + "group by tran_date,vou_no,stock_code,loc_code,mac_id\n";
        executeSql(outSql);
        //return out
    }

    private void insertPriceDetail(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws Exception {
        //delete tmp
        String delSql = "delete from tmp_inv_closing where mac_id = " + macId + "";
        executeSql(delSql);
        //opening
        String insertSql = "insert into tmp_inv_closing(tran_option, tran_date, vou_no, stock_code, qty, price, loc_code, unit, mac_id,comp_code)\n";
        String opSql = "select 'Opening' option, op_date, vou_no, stock_code, sum(qty) qty, price, loc_code, unit," + macId + " mac_id,comp_code\n" + "from v_opening\n" + "where date(op_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = false \n" + "and comp_code = '" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by option,op_date,vou_no,stock_code,loc_code,unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, opSql));
        //purchase
        String purSql = "select 'Purchase' option,vou_date,vou_no,stock_code,sum(qty) qty,pur_price,loc_code,pur_unit," + macId + " mac_id,comp_code\n" + "from v_purchase\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = false \n" + "and comp_code = '" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by option,vou_date,vou_no,stock_code,loc_code,pur_unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, purSql));
        //stockIn
        String inSql = "select 'StockIn' option,vou_date,vou_no,stock_code,sum(in_qty) qty,cost_price,loc_code,in_unit," + macId + " mac_id,comp_code\n" + "from v_stock_io\n" + "where in_qty is not null and in_unit is not null\n" + "and deleted = false \n" + "and comp_code = '" + compCode + "'\n" + "and  date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by option,vou_date,vou_no,stock_code,loc_code,in_unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, inSql));
        //return in
        String retInSql = "select 'ReturnIn' option,vou_date,vou_no,stock_code,sum(qty) qty,cost_price,loc_code,unit," + macId + " mac_id,comp_code\n" + "from v_return_in\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = false \n" + "and comp_code = '" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by option,vou_date,vou_no,stock_code,loc_code,unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, retInSql));
        //sale
        String saleSql = "select 'Sale' option,vou_date,vou_no,stock_code,sum(qty) qty,sale_price,loc_code,sale_unit," + macId + " mac_id,comp_code\n" + "from v_sale\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = false \n" + "and comp_code = '" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by option,vou_date,vou_no,stock_code,loc_code,sale_unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, saleSql));
        //stockOut
        String outSql = "select 'StockOut' option,vou_date,vou_no,stock_code,sum(out_qty) qty,cost_price,loc_code,out_unit," + macId + " mac_id,comp_code\n" + "from v_stock_io\n" + "where out_qty is not null and out_unit is not null\n" + "and deleted = false \n" + "and comp_code = '" + compCode + "'\n" + "and  date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by option,vou_date,vou_no,stock_code,loc_code,out_unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, outSql));
        //return out
        String retOutSql = "select 'ReturnOut' option,vou_date,vou_no,stock_code,sum(qty) qty,price,loc_code,unit," + macId + " mac_id,comp_code\n" + "from v_return_out\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = false \n" + "and comp_code = '" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by option,vou_date,vou_no,stock_code,loc_code,unit,mac_id,comp_code";
        executeSql(String.format("%s\n%s", insertSql, retOutSql));
    }

    private void calculateClosing(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, boolean calSale, String compCode, Integer macId) {
        String delSql = "delete from tmp_stock_io_column where mac_id = " + macId + "";
        String opSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,op_qty,loc_code,mac_id)\n" + "select 'Opening',a.tran_date,'Opening',a.stock_code,sum(smallest_qty) smallest_qty,a.loc_code,a.mac_id\n" + "from (\n" + "select tmp.tran_date,tmp.stock_code,tmp.ttl_qty * rel.smallest_qty smallest_qty,tmp.loc_code,tmp.mac_id\n" + "from tmp_stock_opening tmp \n" + "join stock s on tmp.stock_code = s.stock_code\n" + "join v_relation rel on s.rel_code = rel.rel_code\n" + "and tmp.unit = rel.unit\n" + "where tmp.mac_id =" + macId + ")a\n" + "group by tran_date,stock_code,mac_id";
        String purSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,pur_qty,loc_code,mac_id)\n" + "select 'Purchase',a.vou_date vou_date,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + "\n" + "from (\n" + "select date(vou_date) vou_date,remark,stock_code,sum(qty) qty,loc_code, pur_unit,rel_code\n" + "from v_purchase\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,pur_unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.pur_unit = rel.unit\n" + "group by a.vou_date ,a.stock_code";
        //ret in
        String retInSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,in_qty,loc_code,mac_id)\n" + "select 'ReturnIn',a.vou_date,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + "\n" + "from (\n" + "select date(vou_date) vou_date,remark,stock_code,sum(qty) qty,loc_code,rel_code, unit\n" + "from v_return_in\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code";
        String stockInSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,in_qty,loc_code,mac_id)\n" + "select 'StockIn',date(a.vou_date) vou_date,a.description,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + "\n" + "from (\n" + "select date(vou_date) vou_date,description,stock_code,sum(in_qty) qty,loc_code,in_unit,rel_code\n" + "from v_stock_io\n" + "where  in_qty is not null and in_unit is not null\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,in_unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.in_unit = rel.unit\n" + "group by a.vou_date ,a.stock_code";
        String saleSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,sale_qty,loc_code,mac_id)\n"
                + "select 'Sale',a.vou_date ,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + "\n"
                + "from (\n" + "select date(vou_date) vou_date,remark,stock_code,sum(qty) qty,loc_code, sale_unit,rel_code\n"
                + "from v_sale\n"
                + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and deleted = 0 \n" + "and (calculate = 1 and " + calSale + " = 0)\n"
                + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),stock_code,sale_unit)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.sale_unit = rel.unit\n"
                + "group by a.vou_date,a.stock_code";
        String returnOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,out_qty,loc_code,mac_id)\n" + "select 'ReturnOut',a.vou_date,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + "\n" + "from (\n" + "select date(vou_date) vou_date,remark,stock_code,sum(qty) qty,loc_code, unit,rel_code\n" + "from v_return_out\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code";
        String stockOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,out_qty,loc_code,mac_id)\n" + "select 'StockOut',a.vou_date,a.description,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + "\n" + "from (\n" + "select date(vou_date) vou_date,description,stock_code,sum(out_qty) qty,loc_code,out_unit,rel_code\n" + "from v_stock_io\n" + "where  out_qty is not null and out_unit is not null\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,out_unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.out_unit = rel.unit\n" + "group by vou_date,a.stock_code";
        String fFSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,out_qty,loc_code,mac_id)\n" + "select 'Transfer-F',a.vou_date,if(ifnull(a.remark,'')='','Transfer',a.remark),a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code_from," + macId + "\n" + "from (\n" + "select date(vou_date) vou_date,remark,stock_code,sum(qty) qty,loc_code_from,rel_code, unit\n" + "from v_transfer\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code_from in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code";
        String tFSql = "insert into tmp_stock_io_column(tran_option,tran_date,remark,stock_code,in_qty,loc_code,mac_id)\n" + "select 'Transfer-T',a.vou_date,if(ifnull(a.remark,'')='','Transfer',a.remark),a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code_to," + macId + "\n" + "from (\n" + "select date(vou_date) vou_date,remark,stock_code,sum(qty) qty,loc_code_to,rel_code, unit\n" + "from v_transfer\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code_to in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code";
        try {
            reportDao.executeSql(delSql, opSql, purSql, retInSql, stockInSql, stockOutSql, saleSql, returnOutSql, fFSql, tFSql);
        } catch (Exception e) {
            log.error(String.format("calculateClosing: %s", e.getMessage()));
        }
        log.info("calculate closing.");

    }

    private void calculateOpening(String opDate, String fromDate, String typeCode,
                                  String catCode, String brandCode,
                                  String stockCode, boolean calSale,
                                  String compCode, Integer macId) {
        //delete tmp
        String delSql = "delete from tmp_stock_opening where mac_id = " + macId + "";
        //opening
        String opSql = "insert into tmp_stock_opening(tran_date,stock_code,ttl_qty,loc_code,unit,mac_id)\n"
                + "select '" + fromDate + "' op_date ,stock_code,sum(qty) ttl_qty,loc_code,unit," + macId + " \n"
                + "from (\n" + "select stock_code,sum(qty) qty,loc_code, unit\n"
                + "from v_opening\n"
                + "where date(op_date) = '" + opDate + "'\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and deleted = 0 \n" + "and calculate = 1 \n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty) qty,loc_code, pur_unit\n"
                + "from v_purchase\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and deleted = 0 \n" + "and calculate = 1 \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,pur_unit\n" + "\tunion all\n"
                + "select stock_code,sum(qty) qty,loc_code, unit\n"
                + "from v_return_in\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n"
                + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(in_qty) qty,loc_code, in_unit\n"
                + "from v_stock_io\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" + "and deleted = 0\n"
                + "and calculate = 1 \n" + "and in_qty is not null and in_unit is not null\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,in_unit\n"
                + "\tunion all \n"
                + "select stock_code,sum(out_qty)*-1 qty,loc_code, out_unit\n"
                + "from v_stock_io\n" + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and deleted = 0\n" + "and calculate = 1 \n" + "and out_qty is not null and out_unit is not null\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,out_unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty)*-1 qty,loc_code, unit\n"
                + "from v_return_out\n" + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and deleted = false \n" + "and calculate = 0 \n" + "and comp_code ='" + compCode + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty)*-1 qty,loc_code, sale_unit\n"
                + "from v_sale\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and deleted = 0 \n" + "and (calculate = 1 and " + calSale + "=0) \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,sale_unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty)*-1 qty,loc_code_from, unit\n" + "from v_transfer\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n"
                + "and loc_code_from in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty) qty,loc_code_to, unit\n"
                + "from v_transfer\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" + "and deleted = 0 \n"
                + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n"
                + "and loc_code_to in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,unit\n" + ")a\n" + "group by stock_code,unit";
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
                String sql = "insert into " + taleName + "(f_code,mac_id)\n" + "select '" + str + "'," + macId + "";
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
            String purSql = "insert into tmp_stock_price(tran_option,stock_code,pur_avg_price,mac_id)\n" + "select 'PUR-AVG',stock_code,avg(avg_price)," + macId + "\n" + "from (\n" + "select 'PUR-AVG',pur.stock_code,avg(pur.pur_price/rel.smallest_qty) avg_price\n" + "from v_purchase pur\n" + "join v_relation rel\n" + "on pur.rel_code = rel.rel_code\n" + "and pur.pur_unit = rel.unit\n" + "where deleted = 0 and comp_code ='" + compCode + "'\n" + "and date(pur.vou_date) <= '" + toDate + "'\n" + "group by pur.stock_code\n" + "\tunion all\n" + "select 'OP',op.stock_code,avg(op.price/rel.smallest_qty) avg_price\n" + "from v_opening op\n" + "join v_relation rel\n" + "on op.rel_code = rel.rel_code\n" + "and op.unit = rel.unit\n" + "where op.price > 0\n" + "and op.deleted =0 and op.comp_code ='" + compCode + "'\n" + "and date(op.op_date) = '" + opDate + "'\n" + "and (op.stock_code = '-' or '-' = '-')\n" + "group by op.stock_code)a\n" + "group by stock_code";
            String sInSql = "insert into tmp_stock_price(tran_option,stock_code,in_avg_price,mac_id)\n" + "select 'SIN-AVG',stock_code,avg(avg_price)," + macId + "\n" + "from(\n" + "select 'SIN-AVG',sio.stock_code,avg(sio.cost_price/rel.smallest_qty) avg_price\n" + "from v_stock_io sio\n" + "join v_relation rel\n" + "on sio.rel_code = rel.rel_code\n" + "and sio.in_unit = rel.unit\n" + "where in_qty is not null and in_unit is not null and cost_price >0\n" + "and deleted = 0 and comp_code ='" + compCode + "'\n" + "and date(sio.vou_date) <= '" + toDate + "'\n" + "and (sio.stock_code = '-' or '-' = '-')\n" + "group by sio.stock_code\n" + "\tunion all\n" + "select 'OP',op.stock_code,avg(op.price/rel.smallest_qty) avg_price\n" + "from v_opening op\n" + "join v_relation rel\n" + "on op.rel_code = rel.rel_code\n" + "and op.unit = rel.unit\n" + "where op.price > 0\n" + "and op.deleted =0 and op.comp_code ='" + compCode + "'\n" + "and date(op.op_date) = '" + opDate + "'\n" + "and (op.stock_code = '-' or '-' = '-')\n" + "group by op.stock_code\n" + "\tunion all\n" + "select 'SOUT-AVG',sio.stock_code,avg(sio.cost_price/rel.smallest_qty) avg_price\n" + "from v_stock_io sio\n" + "join v_relation rel\n" + "on sio.rel_code = rel.rel_code\n" + "and sio.out_unit = rel.unit\n" + "where out_qty is not null and out_unit is not null and cost_price >0\n" + "and deleted = 0 and comp_code ='" + compCode + "'\n" + "and date(sio.vou_date) <= '" + toDate + "'\n" + "and (sio.stock_code = '-' or '-' = '-')\n" + "group by sio.stock_code\n" + ")a\n" + "group by stock_code";
            String purRecentSql = "insert into tmp_stock_price(stock_code,tran_option,pur_recent_price,mac_id)\n" + "select stock_code,'PUR_RECENT',avg(pur.pur_price/rel.smallest_qty)," + macId + "\n" + "from v_purchase pur\n" + "join v_relation rel on \n" + "pur.rel_code = rel.rel_code\n" + "and pur.pur_unit = rel.unit\n" + "where vou_no in (\n" + "select max(vou_no)\n" + "from v_purchase\n" + "where deleted = 0 and comp_code ='" + compCode + "'\n" + "and date(vou_date) <= '" + toDate + "'\n" + "group by stock_code)\n" + "group by stock_code";
            reportDao.executeSql(delSql, purSql, sInSql, purRecentSql);
        } catch (Exception e) {
            log.error(String.format("calculatePrice: %s", e.getMessage()));
        }
    }
}
