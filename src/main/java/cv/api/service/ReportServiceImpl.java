/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.*;
import cv.api.dao.ReportDao;
import cv.api.dao.UnitRelationDao;
import cv.api.entity.*;
import cv.api.model.*;
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
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private final HashMap<String, List<UnitRelationDetail>> hmRelation = new HashMap<>();
    private final HashMap<String, String> hmUser = new HashMap<>();
    @Autowired
    private ReportDao reportDao;
    @Autowired
    private UnitRelationDao relationDao;
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private WebClient userApi;

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
    public ResultSet getResult(String sql) throws Exception {
        return reportDao.executeSql(sql);
    }

    @Override
    public String getOpeningDate(String compCode, Integer deptId) {
        String opDate = null;
        String sql = "select max(op_date) op_date\n" +
                "from op_his \n" +
                "where deleted =0 and comp_code ='" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    Date date = rs.getDate("op_date");
                    if (date != null) {
                        opDate = Util1.toDateStr(date, "yyyy-MM-dd");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.isNull(opDate, "1998-10-07");
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
        String sql = "select t.trader_name,t.rfid,v.remark,v.vou_no,v.vou_date,v.stock_name, \n" +
                "v.qty,v.sale_price,v.sale_unit,v.sale_amt,v.vou_total,v.discount,v.paid,v.vou_balance,\n" +
                "t.user_code t_user_code,t.phone,t.address,l.loc_name,v.created_by,v.comp_code,c.cat_name\n" +
                "from v_sale v join trader t\n" + "on v.trader_code = t.code\n" +
                "join location l on v.loc_code = l.loc_code\n" +
                "left join category c on v.cat_code = c.cat_code\n" +
                "where v.vou_no ='" + vouNo + "'";
        ResultSet rs = reportDao.executeSql(sql);
        while (rs.next()) {
            VSale sale = new VSale();
            String remark = rs.getString("remark");
            String refNo = "-";

            if (remark != null) {
                if (remark.contains("/")) {
                    try {
                        String[] split = remark.split("/");
                        remark = split[0];
                        refNo = split[1];
                    } catch (Exception ignored) {
                    }
                }
            }
            sale.setTraderCode(rs.getString("t_user_code"));
            sale.setTraderName(rs.getString("trader_name"));
            sale.setRemark(remark);
            sale.setRefNo(refNo);
            sale.setRfId(rs.getString("rfid"));
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
    public List<VSale> getSaleByCustomerSummary(String fromDate, String toDate, String typeCode,
                                                String catCode, String brandCode,
                                                String stockCode, String traderCode, String compCode, Integer deptId) {
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.trader_name,rel.rel_name\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,sale_unit,sum(sale_amt) ttl_amt,rel_code,trader_code,comp_code,dept_id\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and dept_id =" + deptId + "\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "group by stock_code,sale_unit,trader_code\n" +
                ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.sale_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "and a.dept_id =rel.dept_id\n" +
                "join trader t\n" +
                "on a.trader_code = t.code\n" +
                "and a.comp_code =t.comp_code\n" +
                "and a.dept_id =t.dept_id\n" +
                "order by t.user_code,t.trader_name";
        List<VSale> list = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                VSale s = new VSale();
                //stock_code, s_user_code, stock_name, ttl_qty, sale_unit, ttl_amt,
                // rel_code, trader_code, comp_code, dept_id, ttl_amt, smallest_qty, user_code, trader_name
                String userCode = rs.getString("s_user_code");
                String sCode = rs.getString("stock_code");
                String traderUsr = rs.getString("user_code");
                String tCode = rs.getString("trader_code");
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                s.setTraderCode(Util1.isNull(traderUsr, tCode));
                s.setStockCode(Util1.isNull(userCode, sCode));
                s.setStockName(rs.getString("stock_name"));
                s.setSaleAmount(rs.getFloat("ttl_amt"));
                s.setRelName(rs.getString("rel_name"));
                s.setQtyStr(getRelStr(relCode, compCode, deptId, smallQty));
                s.setTraderName(rs.getString("trader_name"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error(String.format("getSaleSummaryByStock : %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<VSale> getSaleBySaleManSummary(String fromDate, String toDate, String typeCode,
                                               String catCode, String brandCode,
                                               String stockCode, String smCode, String compCode, Integer deptId) {
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.saleman_name,rel.rel_name\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,sale_unit,sum(sale_amt) ttl_amt,rel_code,saleman_code,comp_code,dept_id\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and dept_id =" + deptId + "\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (saleman_code = '" + smCode + "' or '-' = '" + smCode + "')\n" +
                "group by stock_code,sale_unit,saleman_code\n" +
                ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.sale_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "and a.dept_id =rel.dept_id\n" +
                "left join sale_man t\n" +
                "on a.saleman_code = t.saleman_code\n" +
                "and a.comp_code =t.comp_code\n" +
                "and a.dept_id =t.dept_id\n" +
                "order by t.user_code,t.saleman_name";
        List<VSale> list = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                VSale s = new VSale();
                //stock_code, s_user_code, stock_name, ttl_qty, sale_unit, ttl_amt,
                // rel_code, saleman_code, comp_code, dept_id, ttl_amt, smallest_qty, user_code, trader_name
                String userCode = rs.getString("s_user_code");
                String sCode = rs.getString("stock_code");
                String smUsr = rs.getString("user_code");
                String tCode = rs.getString("saleman_code");
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                s.setSaleManCode(Util1.isNull(smUsr, tCode));
                s.setStockCode(Util1.isNull(userCode, sCode));
                s.setStockName(rs.getString("stock_name"));
                s.setSaleAmount(rs.getFloat("ttl_amt"));
                s.setRelName(rs.getString("rel_name"));
                s.setQtyStr(getRelStr(relCode, compCode, deptId, smallQty));
                s.setSaleManName(Util1.isNull(rs.getString("saleman_name"), "Other"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error(String.format("getSaleSummaryByStock : %s", e.getMessage()));
        }
        return list;
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
    public List<VPurchase> getPurchaseBySupplierSummary(String fromDate, String toDate,
                                                        String typCode, String brandCode, String catCode,
                                                        String stockCode,
                                                        String traderCode, String compCode, Integer deptId) throws Exception {
        List<VPurchase> list = new ArrayList<>();
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.trader_name,rel.rel_name\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,pur_unit,sum(pur_amt) ttl_amt,rel_code,trader_code,comp_code,dept_id\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and dept_id =" + deptId + "\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typCode + "' or '-' = '" + typCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "group by stock_code,pur_unit,trader_code\n" +
                ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.pur_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "and a.dept_id =rel.dept_id\n" +
                "join trader t\n" +
                "on a.trader_code = t.code\n" +
                "and a.comp_code =t.comp_code\n" +
                "and a.dept_id =t.dept_id\n" +
                "order by t.user_code,t.trader_name";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase s = new VPurchase();
                String userCode = rs.getString("s_user_code");
                String sCode = rs.getString("stock_code");
                String traderUsr = rs.getString("user_code");
                String tCode = rs.getString("trader_code");
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                s.setTraderCode(Util1.isNull(traderUsr, tCode));
                s.setStockCode(Util1.isNull(userCode, sCode));
                s.setStockName(rs.getString("stock_name"));
                s.setPurAmount(rs.getFloat("ttl_amt"));
                s.setRelName(rs.getString("rel_name"));
                s.setQtyStr(getRelStr(relCode, compCode, deptId, smallQty));
                s.setTraderName(rs.getString("trader_name"));
                list.add(s);
            }
        }
        return list;
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
    public List<VSale> getSaleByStockSummary(String fromDate, String toDate, String curCode, String stockCode,
                                             String typeCode, String brandCode,
                                             String catCode, String locCode,
                                             String compCode, Integer deptId, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty,rel.rel_name\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,sale_unit,sum(sale_amt) ttl_amt,rel_code,comp_code,dept_id\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and dept_id =" + deptId + "\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,sale_unit\n" +
                ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.sale_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "and a.dept_id =rel.dept_id\n" +
                "order by s_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = new VSale();
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                sale.setStockCode(rs.getString("s_user_code"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setRelName(rs.getString("rel_name"));
                sale.setSaleAmount(rs.getFloat("ttl_amt"));
                sale.setQtyStr(getRelStr(relCode, compCode, deptId, smallQty));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleByVoucherDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode,
                                              String brandCode, String catCode, String locCode, String batchNo, String compCode,
                                              Integer deptId, Integer macId) throws Exception {
        String filter = "";
        if (!typeCode.equals("-")) {
            filter += "and stock_type_code='" + typeCode + "'\n";
        }
        if (!brandCode.equals("-")) {
            filter += "and brand_code='" + brandCode + "'\n";
        }
        if (!catCode.equals("-")) {
            filter += "and cat_code='" + catCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code='" + stockCode + "'\n";
        }
        if (!batchNo.equals("-")) {
            filter += "and batch_no='" + batchNo + "'\n";
        }
        List<VSale> list = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.vou_total,v.paid,v.remark,v.reference,v.batch_no,sup.trader_name sup_name,\n" +
                "t.user_code,t.trader_name,t.address,v.s_user_code,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" +
                "from v_sale v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "left join grn g\n" +
                "on v.batch_no = g.batch_no\n" +
                "and v.comp_code = g.comp_code\n" +
                "left join trader sup\n" +
                "on g.trader_code = sup.code\n" +
                "and g.comp_code = sup.comp_code\n" +
                "where v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "" + filter + "" +
                "order by v.vou_date,v.vou_no,v.unique_id";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                //vou_date, vou_no, remark, reference, batch_no, sup_name, trader_code,
                // trader_name, s_user_code, stock_name, qty, sale_unit, sale_price, sale_amt
                VSale s = new VSale();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
                s.setBatchNo(rs.getString("batch_no"));
                s.setSupplierName(rs.getString("sup_name"));
                s.setTraderCode((rs.getString("user_code")));
                s.setTraderName(rs.getString("trader_name"));
                s.setCusAddress(rs.getString("address"));
                s.setStockUserCode(rs.getString("s_user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setQty(rs.getFloat("qty"));
                s.setSaleUnit(rs.getString("sale_unit"));
                s.setSalePrice(rs.getFloat("sale_price"));
                s.setSaleAmount(rs.getFloat("sale_amt"));
                s.setVouTotal(rs.getFloat("vou_total"));
                s.setPaid(rs.getFloat("paid"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByBatchDetail(String fromDate, String toDate, String curCode, String stockCode,
                                            String typeCode, String brandCode, String catCode,
                                            String locCode, String batchNo, String compCode,
                                            Integer deptId, Integer macId) throws Exception {
        String filter = "";
        if (!typeCode.equals("-")) {
            filter += "and stock_type_code='" + typeCode + "'\n";
        }
        if (!brandCode.equals("-")) {
            filter += "and brand_code='" + brandCode + "'\n";
        }
        if (!catCode.equals("-")) {
            filter += "and cat_code='" + catCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code='" + stockCode + "'\n";
        }
        if (!batchNo.equals("-")) {
            filter += "and v.batch_no='" + batchNo + "'\n";
        }
        List<VSale> list = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.vou_total,v.paid,v.remark,v.reference,v.batch_no,sup.trader_name sup_name,\n" +
                "t.user_code,t.trader_name,t.address,v.s_user_code,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" +
                "from v_sale v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "left join grn g\n" +
                "on v.batch_no = g.batch_no\n" +
                "and v.comp_code = g.comp_code\n" +
                "left join trader sup\n" +
                "on g.trader_code = sup.code\n" +
                "and g.comp_code = sup.comp_code\n" +
                "where v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and v.batch_no is not null\n" +
                "" + filter + "" +
                "order by v.vou_date,v.batch_no,v.unique_id";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                //vou_date, vou_no, remark, reference, batch_no, sup_name, trader_code,
                // trader_name, s_user_code, stock_name, qty, sale_unit, sale_price, sale_amt
                VSale s = new VSale();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
                s.setBatchNo(rs.getString("batch_no"));
                s.setSupplierName(rs.getString("sup_name"));
                s.setTraderCode((rs.getString("user_code")));
                s.setTraderName(rs.getString("trader_name"));
                s.setCusAddress(rs.getString("address"));
                s.setStockUserCode(rs.getString("s_user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setQty(rs.getFloat("qty"));
                s.setSaleUnit(rs.getString("sale_unit"));
                s.setSalePrice(rs.getFloat("sale_price"));
                s.setSaleAmount(rs.getFloat("sale_amt"));
                s.setVouTotal(rs.getFloat("vou_total"));
                s.setPaid(rs.getFloat("paid"));
                list.add(s);
            }
        }
        return list;
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
    public List<VPurchase> getPurchaseByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception {
        List<VPurchase> list = new ArrayList<>();
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty,rel.rel_name\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,pur_unit,sum(pur_amt) ttl_amt,rel_code,comp_code,dept_id\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and dept_id =" + deptId + "\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,pur_unit\n" +
                ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.pur_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "and a.dept_id =rel.dept_id\n" +
                "order by s_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = new VPurchase();
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                p.setStockCode(rs.getString("s_user_code"));
                p.setStockName(rs.getString("stock_name"));
                p.setRelName(rs.getString("rel_name"));
                p.setPurAmount(rs.getFloat("ttl_amt"));
                p.setQtyStr(getRelStr(relCode, compCode, deptId, smallQty));
                list.add(p);
            }
        }
        return list;
    }


    @Override
    public General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId) {
        General general = new General();
        general.setAmount(0.0f);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n"
                + "from (\n"
                + "select pur_unit,pur_price/rel.smallest_qty smallest_price,pd.rel_code,pd.comp_code,pd.dept_id\n"
                + "from v_purchase pd\n"
                + "join v_relation rel on pd.rel_code = rel.rel_code\n"
                + "and pd.pur_unit =  rel.unit\n"
                + "where pd.stock_code = '" + stockCode + "' and vou_no = (\n"
                + "select ph.vou_no\n"
                + "from pur_his ph, pur_his_detail pd\n"
                + "where date(ph.vou_date)<= '" + purDate + "' \n"
                + "and deleted = 0\n"
                + "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n"
                + "and ph.dept_id = " + deptId + "\n"
                + "and pd.stock_code = '" + stockCode + "'\n" + "group by ph.vou_no\n"
                + "order by ph.vou_date desc\n" + "limit 1\n" + "))a\n"
                + "join v_relation rel\n"
                + "on a.rel_code =rel.rel_code\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "and rel.unit = '" + unit + "'";
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
    public General getWeightLossRecentPrice(String stockCode, String vouDate, String unit, String compCode, Integer deptId) {
        General general = new General();
        general.setAmount(0.0f);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" +
                "from (\n" +
                "select v.loss_price/rel.smallest_qty smallest_price,v.rel_code,v.comp_code,v.dept_id\n" +
                "from v_weight_loss v\n" +
                "join v_relation rel\n" +
                "on v.rel_code = rel.rel_code\n" +
                "and v.loss_unit = rel.unit\n" +
                "and v.comp_code = rel.comp_code\n" +
                "and v.dept_id = rel.dept_id\n" +
                "and v.stock_code ='" + stockCode + "'\n" +
                "where vou_no =(\n" +
                "select ph.vou_no\n" +
                "from weight_loss_his ph, weight_loss_his_detail pd\n" +
                "where date(ph.vou_date)<= '" + vouDate + "' \n" +
                "and deleted = 0\n" +
                "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" +
                "and ph.dept_id = 1\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "group by ph.vou_no\n" +
                "order by ph.vou_date desc\n" +
                "limit 1)\n" +
                ")a\n" +
                "join v_relation rel\n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and a.dept_id = rel.dept_id\n" +
                "and rel.unit ='" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getFloat("price"));
            }
        } catch (Exception e) {
            log.error(String.format("getWeightLossRecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public General getProductionRecentPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId) {
        General general = new General();
        general.setAmount(0.0f);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" +
                "from (\n" +
                "select pd.unit,price/rel.smallest_qty smallest_price,pd.rel_code,pd.comp_code,pd.dept_id\n" +
                "from v_process_his pd\n" +
                "join v_relation rel on pd.rel_code = rel.rel_code\n" +
                "and pd.unit =  rel.unit\n" +
                "where pd.stock_code = '" + stockCode + "' \n" +
                "and pd.comp_code ='" + compCode + "'\n" +
                "and pd.dept_id =" + deptId + "\n" +
                "and vou_no = (\n" +
                "select ph.vou_no\n" +
                "from process_his ph\n" +
                "where date(ph.vou_date)<= '" + purDate + "' \n" +
                "and deleted = 0\n" +
                "and ph.comp_code = '" + compCode + "' \n" +
                "and ph.dept_id = " + deptId + "\n" +
                "and ph.stock_code = '" + stockCode + "'\n" +
                "group by ph.vou_no\n" +
                "order by ph.vou_date desc\n" +
                "limit 1\n" +
                "))a\n" +
                "join v_relation rel\n" +
                "on a.rel_code =rel.rel_code\n" +
                "and a.comp_code =rel.comp_code\n" +
                "and a.dept_id = rel.dept_id\n" +
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
    public General getPurchaseAvgPrice(String stockCode, String purDate, String unit, String compCode, Integer deptId) {
        General g = new General();
        String sql = "select stock_code,round(avg(avg_price)*rel.smallest_qty,2) price\n" +
                "from (\n" +
                "select 'PUR-AVG',pur.stock_code,avg(pur.pur_price/rel.smallest_qty) avg_price,pur.rel_code,pur.comp_code,pur.dept_id\n" +
                "from v_purchase pur\n" +
                "join v_relation rel\n" +
                "on pur.rel_code = rel.rel_code\n" +
                "and pur.pur_unit = rel.unit\n" +
                "where deleted = 0 \n" +
                "and pur.comp_code ='" + compCode + "'\n" +
                "and pur.stock_code ='" + stockCode + "'\n" +
                "and date(pur.vou_date) <= '" + purDate + "'\n" +
                "group by pur.stock_code\n" +
                "\tunion all\n" +
                "select 'OP',op.stock_code,avg(op.price/rel.smallest_qty) avg_price,op.rel_code,op.comp_code,op.dept_id\n" +
                "from v_opening op\n" +
                "join v_relation rel\n" +
                "on op.rel_code = rel.rel_code\n" +
                "and op.unit = rel.unit\n" +
                "where op.price > 0\n" +
                "and op.deleted =0 \n" +
                "and op.comp_code ='" + compCode + "'\n" +
                "and date(op.op_date) = '" + purDate + "'\n" +
                "and op.stock_code ='" + stockCode + "'\n" +
                "group by op.stock_code)a\n" +
                "join v_relation rel on\n" +
                "a.rel_code = rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and a.dept_id = rel.dept_id\n" +
                "and rel.unit ='" + unit + "'\n" +
                "group by stock_code";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    g.setAmount(rs.getFloat("price"));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return g;
    }

    @Override
    public General getSaleRecentPrice(String stockCode, String saleDate, String unit, String compCode) {
        General general = new General();
        general.setAmount(0.0f);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" +
                "from (select sale_unit,sale_price/rel.smallest_qty smallest_price,pd.rel_code,pd.comp_code,pd.dept_id\n" +
                "from v_sale pd\n" +
                "join v_relation rel on pd.rel_code = rel.rel_code\n" + "and pd.sale_unit =  rel.unit\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "where vou_no = (\n" + "select ph.vou_no\n" +
                "from sale_his ph, sale_his_detail pd\n" + "where date(ph.vou_date)<= '" + saleDate + "' and deleted = 0\n" +
                "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "order by ph.vou_date desc limit 1" + "))a\n" +
                "join v_relation rel\n" +
                "on a.rel_code =rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and a.dept_id = rel.dept_id\n" +
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

    private void calStockBalanceByLocation(String typeCode, String cateCode, String brandCode, String stockCode,
                                           boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                           String locCode, String compCode, Integer deptId, Integer macId) {
        String delSql = "delete from tmp_stock_balance where mac_id = " + macId + "";
        String sql = "insert into tmp_stock_balance(stock_code, qty, unit, loc_code,smallest_qty, mac_id,comp_code,dept_id)\n"
                + "select stock_code,qty,unit,loc_code,sum(smallest_qty) smallest_qty," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "\tselect a.stock_code,sum(a.qty) qty,a.unit,a.loc_code,sum(a.qty)*rel.smallest_qty smallest_qty,a.comp_code,a.dept_id\n"
                + "\tfrom(\n"
                + "\t\tselect stock_code,sum(qty) as qty,unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom v_opening\n"
                + "\t\twhere deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand calculate =1\n"
                + "\t\tgroup by stock_code, unit , loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(qty) * - 1 as qty,sale_unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom v_sale \n"
                + "\t\twhere deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (cat_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand (calculate =1 and 0 = " + calSale + ")\n"
                + "\t\tgroup by stock_code ,sale_unit ,loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(qty) as qty,pur_unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom\n"
                + "\t\tv_purchase \n" + "\t\twhere deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (calculate =1 and 0 = " + calPur + ")\n"
                + "\t\tgroup by stock_code , pur_unit , loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(qty) as qty,unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom v_return_in\n"
                + "\t\twhere deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand (calculate =1 and 0 = " + calRI + ")\n"
                + "\t\tgroup by stock_code,unit ,loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(qty) * - 1 as qty,unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom\n" + "\t\tv_return_out\n"
                + "\t\twhere deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand (calculate =1 and 0 = " + calRO + ")\n"
                + "\t\tgroup by stock_code  , unit , loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(in_qty),in_unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom\n"
                + "\t\tv_stock_io\n"
                + "\t\twhere in_qty is not null\n"
                + "\t\tand in_unit is not null\n" + "\t\tand deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand calculate =1\n" + "\t\tgroup by stock_code ,in_unit ,loc_code \n"
                + "\t\t\tunion all \n"
                + "\t\tselect stock_code,sum(out_qty) * - 1,out_unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom\n"
                + "\t\tv_stock_io\n"
                + "\t\twhere out_qty is not null\n"
                + "\t\tand out_unit is not null\n"
                + "\t\tand deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand calculate =1\n"
                + "\t\tgroup by stock_code , out_unit , loc_code\n"
                + "\t\t\tunion all\n"
                + "\t\tselect stock_code,sum(qty) * - 1,unit,loc_code_from,comp_code,dept_id\n"
                + "\t\tfrom v_transfer \n"
                + "\t\twhere deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code_from = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand calculate =1\n"
                + "\t\tgroup by stock_code, unit , loc_code_from\n"
                + "\t\t\tunion all\n" +
                "\t\tselect stock_code,sum(qty),unit,loc_code_to,comp_code,dept_id\n"
                + "\t\tfrom v_transfer \n"
                + "\t\twhere deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code_to = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand calculate =1\n"
                + "\t\tgroup by stock_code , unit , loc_code_to\n"
                + "\t\t\tunion all\n"
                + "\t\tselect stock_code,sum(qty),unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom v_process_his\n"
                + "\t\twhere  deleted = 0\n"
                + "\t\tand finished =1\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand calculate =1\n"
                + "\t\tgroup by stock_code , unit , loc_code\n"
                + "\t\t\tunion all\n"
                + "\t\tselect stock_code,sum(qty)*-1,unit,loc_code,comp_code,dept_id\n"
                + "\t\tfrom v_process_his_detail\n"
                + "\t\twhere  deleted = 0\n"
                + "\t\tand comp_code = '" + compCode + "'\n"
                + "\t\tand dept_id = " + deptId + "\n"
                + "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n"
                + "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n"
                + "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n"
                + "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n"
                + "\t\tand calculate =1\n"
                + "\t\tgroup by stock_code , unit , loc_code"
                + ") a\n"
                + "join stock s\n"
                + "on a.stock_code = s.stock_code\n"
                + "and a.comp_code = s.comp_code\n"
                + "and a.dept_id = s.dept_id\n"
                + "join v_relation rel on s.rel_code = rel.rel_code \n"
                + "and a.unit = rel.unit\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "group by a.stock_code,a.unit,a.loc_code) b\n"
                + "group by b.stock_code,b.loc_code";
        try {
            reportDao.executeSql(delSql, sql);
        } catch (Exception e) {
            log.error(String.format("calStockBalance: %s", e.getMessage()));
        }
    }

    @Override
    public List<VStockBalance> getStockBalance(String typeCode, String catCode, String brandCode, String stockCode,
                                               boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                               String locCode, String compCode, Integer deptId, Integer macId) throws Exception {
        calStockBalanceByLocation(typeCode, catCode, brandCode, stockCode, calSale, calPur, calRI, calRO, locCode, compCode, deptId, macId);
        List<VStockBalance> balances = new ArrayList<>();
        String sql = "select tmp.stock_code,tmp.loc_code,l.loc_name,tmp.unit,tmp.qty,tmp.smallest_qty,s.user_code,s.rel_code,s.stock_name\n" +
                "from tmp_stock_balance tmp join location l\n" +
                "on tmp.loc_code = l.loc_code\n" +
                "join stock s on tmp.stock_code = s.stock_code\n" +
                "where tmp.mac_id = " + macId + "";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockBalance b = new VStockBalance();
                b.setUserCode(rs.getString("user_code"));
                b.setStockCode(rs.getString("stock_code"));
                b.setStockName(rs.getString("stock_name"));
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
                                              boolean calSale, boolean calPur, boolean calRI, boolean calRo,
                                              String locCode, String compCode, Integer deptId, Integer macId) throws Exception {

        calStockBalanceByLocation(typeCode, catCode, brandCode, stockCode, calSale, calPur, calRI, calRo, locCode, compCode, deptId, macId);
        String sql1 = "select *,if(small_bal_qty<small_min_qty,1,if(small_bal_qty>small_min_qty,2,if(small_bal_qty<small_max_qty,3,if(small_bal_qty> small_max_qty,4,5)))) position\n\n\n" +
                "from (\n" +
                "select a.*,rel.rel_name,bal_qty*rel.smallest_qty small_bal_qty,min_qty*ifnull(rel1.smallest_qty,0) small_min_qty,max_qty*ifnull(rel2.smallest_qty,0) small_max_qty\n" +
                "from (\n" +
                "select tmp.stock_code,tmp.loc_code,tmp.smallest_qty bal_qty, tmp.unit bal_unit,ifnull(min_qty,0) min_qty,min_unit,\n" +
                "ifnull(max_qty,0) max_qty,max_unit,tmp.comp_code,tmp.dept_id,s.rel_code,s.user_code,s.stock_name,l.loc_name\n" +
                "from tmp_stock_balance tmp\n" +
                "left join reorder_level r\n" +
                "on tmp.stock_code= r.stock_code\n" +
                "and tmp.comp_code = r.comp_code\n" +
                "and tmp.dept_id = r.dept_id\n" +
                "and tmp.loc_code = r.loc_code\n" +
                "and tmp.mac_id =" + macId + "\n" +
                "and tmp.dept_id =" + deptId + "\n" +
                "and tmp.comp_code ='" + compCode + "'\n" +
                "join stock s on tmp.stock_code = s.stock_code\n" +
                "and tmp.comp_code = s.comp_code\n" +
                "and tmp.dept_id = s.dept_id\n" +
                "join location l on tmp.loc_code = l.loc_code\n" +
                "and tmp.comp_code = l.comp_code\n" +
                "and tmp.dept_id = l.dept_id\n" +
                ")a\n" +
                "join v_relation rel\n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.bal_unit = rel.unit\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and a.dept_id = rel.dept_id\n" +
                "left join v_relation rel1\n" +
                "on a.rel_code = rel1.rel_code\n" +
                "and a.min_unit = rel1.unit\n" +
                "and a.comp_code = rel1.comp_code\n" +
                "and a.dept_id = rel1.dept_id\n" +
                "left join v_relation rel2\n" +
                "on a.rel_code = rel2.rel_code\n" +
                "and a.max_unit = rel2.unit\n" +
                "and a.comp_code = rel2.comp_code\n" +
                "and a.dept_id = rel2.dept_id\n" +
                ")b\n" +
                "order by position,small_bal_qty";
        ResultSet rs = reportDao.executeSql(sql1);
        List<ReorderLevel> reorderLevels = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                ReorderLevel r = new ReorderLevel();
                ReorderKey key = new ReorderKey();
                key.setDeptId(deptId);
                key.setCompCode(compCode);
                key.setStockCode(rs.getString("stock_code"));
                key.setLocCode(rs.getString("loc_code"));
                r.setKey(key);
                String relCode = rs.getString("rel_code");
                r.setStockName(rs.getString("stock_name"));
                r.setUserCode(rs.getString("user_code"));
                r.setRelName(rs.getString("rel_name"));
                r.setLocName(rs.getString("loc_name"));
                r.setMinQty(rs.getFloat("min_qty"));
                r.setMinUnitCode(rs.getString("min_unit"));
                r.setMaxQty(rs.getFloat("max_qty"));
                r.setPosition(rs.getInt("position"));
                r.setMaxUnitCode(rs.getString("max_unit"));
                //max qty
                r.setMaxSmallQty(rs.getFloat("small_max_qty"));
                //min qty
                r.setMinSmallQty(rs.getFloat("small_min_qty"));
                //bal qty
                float balSmallQty = rs.getFloat("small_bal_qty");
                r.setBalUnit(getRelStr(relCode, compCode, deptId, balSmallQty));
                r.setBalSmallQty(balSmallQty);
                reorderLevels.add(r);
            }
        }
        return reorderLevels;
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
                                                     String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
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
                                          String brandCode, String stockCode, String vouStatus,
                                          boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                          String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
    }

    @Override
    public List<ClosingBalance> getStockInOutDetail(String typeCode, String compCode, Integer deptId, Integer macId) {
        String getSql = "select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,\n"
                + "s.rel_code,s.user_code s_user_code,a.stock_code,s.stock_name\n"
                + "from (\n"
                + "select tran_option,tran_date,stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,\n"
                + "sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,remark,vou_no,comp_code,dept_id\n"
                + "from tmp_stock_io_column\n"
                + "where mac_id = " + macId + "\n"
                + "and comp_code = '" + compCode + "'\n"
                + "and dept_id = " + deptId + "\n"
                + "group by tran_date,stock_code,tran_option,vou_no)a\n"
                + "join stock s on a.stock_code = s.stock_code\n"
                + "and a.comp_code = s.comp_code\n"
                + "and a.dept_id = s.dept_id\n"
                + "group by tran_date,stock_code,vou_no,tran_option\n"
                + "order by s.user_code,a.tran_date,a.tran_option,a.vou_no";
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
                    b.setPurQty(purQty);
                    b.setInQty(inQty);
                    b.setSaleQty(saleQty);
                    b.setOutQty(outQty);
                    b.setBalQty(balQty);
                    b.setRelCode(relCode);
                    b.setCompCode(compCode);
                    b.setDeptId(deptId);
                    b.setVouDate(Util1.toDateStr(rs.getDate("tran_date"), "dd/MM/yyyy"));
                    b.setStockUsrCode(Util1.isNull(rs.getString("s_user_code"), rs.getString("stock_code")));
                    b.setStockName(rs.getString("stock_name"));
                    b.setRemark(rs.getString("remark"));
                    balances.add(b);
                }
            }
            for (int i = 0; i < balances.size(); i++) {
                if (i > 0) {
                    ClosingBalance prv = balances.get(i - 1);
                    float prvCl = prv.getBalQty();
                    ClosingBalance c = balances.get(i);
                    String relCode = c.getRelCode();
                    c.setOpenQty(prvCl);
                    float opQty = c.getOpenQty();
                    float purQty = c.getPurQty();
                    float inQty = c.getInQty();
                    float outQty = c.getOutQty();
                    float saleQty = c.getSaleQty();
                    float clQty = opQty + purQty + inQty + outQty + saleQty;
                    c.setBalQty(clQty);
                    c.setOpenRel(getRelStr(relCode, compCode, deptId, opQty));
                    c.setPurRel(getRelStr(relCode, compCode, deptId, purQty));
                    c.setInRel(getRelStr(relCode, compCode, deptId, inQty));
                    c.setSaleRel(getRelStr(relCode, compCode, deptId, saleQty));
                    c.setOutRel(getRelStr(relCode, compCode, deptId, outQty));
                    c.setBalRel(getRelStr(relCode, compCode, deptId, clQty));
                } else {
                    ClosingBalance c = balances.get(i);
                    String relCode = c.getRelCode();
                    float opQty = c.getOpenQty();
                    float purQty = c.getPurQty();
                    float inQty = c.getInQty();
                    float outQty = c.getOutQty();
                    float saleQty = c.getSaleQty();
                    float clQty = opQty + purQty + inQty + outQty + saleQty;
                    c.setOpenRel(getRelStr(relCode, compCode, deptId, opQty));
                    c.setPurRel(getRelStr(relCode, compCode, deptId, purQty));
                    c.setInRel(getRelStr(relCode, compCode, deptId, inQty));
                    c.setSaleRel(getRelStr(relCode, compCode, deptId, saleQty));
                    c.setOutRel(getRelStr(relCode, compCode, deptId, outQty));
                    c.setBalRel(getRelStr(relCode, compCode, deptId, clQty));
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
                                          String stockCode, String vouStatus,
                                          boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                          String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculatePrice(toDate, opDate, stockCode, typeCode, catCode, brandCode, compCode, deptId, macId);
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
    public List<VOpening> getOpeningByLocation(String typeCode, String brandCode, String catCode,
                                               String stockCode, Integer macId, String compCode, Integer deptId) throws Exception {
        List<VOpening> list = new ArrayList<>();
        String sql = "select v.op_date,v.vou_no,v.remark,v.stock_code,v.stock_user_code,v.stock_name,l.loc_name,\n"
                + "v.unit,v.qty,v.price,v.amount,v.comp_code,v.dept_id\n"
                + "from v_opening v join location l\n" + "on v.loc_code = l.loc_code\n"
                + "where v.deleted = 0\n" + "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (v.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (v.brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and v.loc_code in (select f_code from f_location where mac_id = " + macId + ")\n"
                + "and v.comp_code ='" + compCode + "'\n"
                + "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                + "order by l.loc_name,v.stock_user_code\n";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOpening op = new VOpening();
                op.setVouDate(Util1.toDateStr(rs.getDate("op_date"), "dd/MM/yyyy"));
                op.setRemark(rs.getString("remark"));
                op.setStockCode(rs.getString("stock_code"));
                op.setUnit(rs.getString("unit"));
                op.setStockUserCode(rs.getString("stock_user_code"));
                op.setStockName(rs.getString("stock_name"));
                op.setLocationName(rs.getString("loc_name"));
                op.setQty(rs.getFloat("qty"));
                op.setPrice(rs.getFloat("price"));
                op.setAmount(rs.getFloat("amount"));
                list.add(op);
            }
        }
        return list;
    }

    @Override
    public List<VOpening> getOpeningByGroup(String typeCode, String stockCode, String catCode, String brandCode, Integer macId, String compCode, Integer deptId) throws Exception {
        List<VOpening> openings = new ArrayList<>();
        String sql = "select a.*,t.stock_type_name\n"
                + "from (select v.op_date,v.remark,v.stock_type_code,v.stock_code,v.stock_user_code,v.stock_name,l.loc_name,\n"
                + "unit,qty,price,amount \n" + "from v_opening v join location l \n"
                + "on v.loc_code = l.loc_code\n" + "where v.deleted = 0 \n"
                + "and v.comp_code = '" + compCode + "'\n"
                + "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                + "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "and (v.brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (v.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "'))a\n"
                + "join stock_type t on a.stock_type_code = t.stock_type_code\n"
                + "order by t.stock_type_name,a.stock_user_code";
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
    public List<VStockIO> getStockIOPriceCalender(String vouType, String fromDate, String toDate, String typeCode,
                                                  String catCode, String brandCode, String stockCode, String compCode, Integer deptId) throws Exception {
        String sql = "select v.vou_date,v.vou_no,v.stock_code,v.s_user_code,\n" +
                "v.stock_name,vs.description vou_status_name,if(v.in_unit is null,v.out_unit,v.in_unit) unit, v.cost_price \n" +
                "from v_stock_io v join vou_status vs\n" +
                "on v.vou_status = vs.code\n" +
                "where v.comp_code = '" + compCode + "'\n" +
                "and v.dept_id  = " + deptId + "\n" +
                "and v.deleted = 0\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (v.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (v.brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "and (v.vou_status ='" + vouType + "' or '-' ='" + vouType + "')\n" +
                "group by date(v.vou_date),v.stock_code,v.cost_price,unit\n" +
                "order by v.s_user_code,v.vou_date\n";
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
    public List<VStockIO> getStockIOHistory(String fromDate, String toDate, String vouStatus,
                                            String vouNo, String remark, String desp,
                                            String userCode, String stockCode,
                                            String locCode, String compCode, Integer deptId, String deleted) throws Exception {
        String sql = "select a.*,v.description vou_status_name\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,description,remark,vou_status,created_by,deleted,comp_code,dept_id\n"
                + "from v_stock_io \n"
                + "where comp_code = '" + compCode + "'\n"
                + "and deleted = " + deleted + "\n"
                + "and dept_id = " + deptId + "\n"
                + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n"
                + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n"
                + "and (description like '" + desp + "%' or '-%'= '" + desp + "%')\n"
                + "and (vou_status = '" + vouStatus + "' or '-'='" + vouStatus + "')\n"
                + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n"
                + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n"
                + "group by vou_no\n" + ")a\n"
                + "join vou_status v on a.vou_status = v.code\n"
                + "and a.comp_code = v.comp_code\n"
                + "order by vou_date,vou_no";
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
                io.setDeptId(rs.getInt("dept_id"));
                ioList.add(io);
            }
        }
        return ioList;
    }

    @Override
    public List<VSale> getSaleHistory(String fromDate, String toDate, String traderCode, String saleManCode,
                                      String vouNo, String remark, String reference, String userCode, String stockCode,
                                      String locCode, String compCode, Integer deptId, String deleted, String nullBatch, String batchNo) {
        List<VSale> saleList = new ArrayList<>();
        String filter = "";
        if (!vouNo.equals("-")) {
            filter += "and vou_no = '" + vouNo + "'\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like '" + remark + "%'\n";
        }
        if (!reference.equals("-")) {
            filter += "and reference like '" + reference + "%'\n";
        }
        if (!traderCode.equals("-")) {
            filter += "and trader_code = '" + traderCode + "'\n";
        }
        if (!userCode.equals("-")) {
            filter += "and created_by = '" + userCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code = '" + stockCode + "'\n";
        }
        if (!saleManCode.equals("-")) {
            filter += "and saleman_code = '" + saleManCode + "'\n";
        }
        if (!locCode.equals("-")) {
            filter += "and loc_code = '" + locCode + "'\n";
        }
        if (nullBatch.equals("true")) {
            filter += "and (batch_no is null or batch_no ='') \n";
        }
        if (!batchNo.equals("-")) {
            filter += "and batch_no = '" + batchNo + "'\n";
        }
        String sql = "select a.*,t.trader_name,t.user_code\n"
                + "from (\n"
                + "select  vou_no,date(vou_date) vou_date,remark,created_by,paid,vou_total,deleted,trader_code,loc_code,comp_code,dept_id\n"
                + "from v_sale s \n"
                + "where comp_code = '" + compCode + "'\n"
                + "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                + "and deleted = " + deleted + "\n"
                + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "" + filter + "\n"
                + "group by vou_no\n" + ")a\n"
                + "join trader t on a.trader_code = t.code\n"
                + "and a.comp_code = t.comp_code\n"
                + "order by date(vou_date) desc,vou_no desc";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VSale s = new VSale();
                    s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    s.setVouNo(rs.getString("vou_no"));
                    s.setTraderCode(rs.getString("user_code"));
                    s.setTraderName(rs.getString("trader_name"));
                    s.setRemark(rs.getString("remark"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setPaid(rs.getFloat("paid"));
                    s.setVouTotal(rs.getFloat("vou_total"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    s.setDeptId(rs.getInt("dept_id"));
                    saleList.add(s);
                }
            }
        } catch (Exception e) {
            log.error("getSaleHistory : " + e.getMessage());
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseHistory(String fromDate, String toDate, String traderCode, String vouNo,
                                              String remark, String reference, String userCode,
                                              String stockCode, String locCode, String compCode, Integer deptId, String deleted) throws Exception {
        String sql = "select a.*,t.trader_name\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code,comp_code,dept_id\n"
                + "from v_purchase p \n"
                + "where comp_code = '" + compCode + "'\n"
                + "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                + "and deleted =" + deleted + "\n"
                + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n"
                + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n"
                + "and (reference like '" + reference + "%' or '-%'= '" + reference + "%')\n"
                + "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n"
                + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n"
                + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n"
                + "group by vou_no)a\n"
                + "join trader t on a.trader_code = t.code\n"
                + "and a.comp_code = t.comp_code\n"
                + "order by date(vou_date),vou_no";
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
                s.setDeptId(rs.getInt("dept_id"));
                purchaseList.add(s);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VReturnIn> getReturnInHistory(String fromDate, String toDate, String traderCode,
                                              String vouNo, String remark, String userCode,
                                              String stockCode, String locCode, String compCode, Integer deptId, String deleted) throws Exception {
        String sql = "select a.*,t.trader_name\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code,comp_code,dept_id \n"
                + "from v_return_in \n"
                + "where comp_code = '" + compCode + "'\n"
                + "and deleted = " + deleted + "\n"
                + "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n"
                + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n"
                + "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n"
                + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n"
                + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n"
                + "group by vou_no\n" + ")a\n"
                + "join trader t on a.trader_code = t.code\n"
                + "and a.comp_code = t.comp_code\n"
                + "order by vou_date,vou_no";
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
                s.setDeptId(rs.getInt("dept_id"));
                returnInList.add(s);
            }
        }
        return returnInList;
    }

    @Override
    public List<VReturnOut> getReturnOutHistory(String fromDate, String toDate, String traderCode,
                                                String vouNo, String remark, String userCode,
                                                String stockCode, String locCode, String compCode,
                                                Integer deptId, String deleted) throws Exception {
        String sql = "select a.*,t.trader_name\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code,comp_code,dept_id \n"
                + "from v_return_out \n"
                + "where comp_code = '" + compCode + "'\n"
                + "and deleted = " + deleted + "\n"
                + "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n"
                + "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n"
                + "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n"
                + "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n"
                + "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n"
                + "group by vou_no\n" + ")a\n"
                + "join trader t on a.trader_code = t.code\n"
                + "and a.comp_code= t.comp_code\n"
                + "order by vou_date,vou_no";
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
                s.setDeptId(rs.getInt("dept_id"));
                returnInList.add(s);
            }
        }
        return returnInList;
    }

    @Override
    public List<OPHis> getOpeningHistory(String fromDate, String toDate, String vouNo, String remark, String userCode,
                                         String stockCode, String locCode, String compCode, Integer deptId) throws Exception {
        String sql = "select sum(v.amount) amount,v.op_date,v.vou_no,v.remark,v.created_by,v.deleted,l.loc_name,v.comp_code,v.dept_id \n"
                + "from v_opening v join location l\n" + "on v.loc_code = l.loc_code\n"
                + "where v.comp_code = '" + compCode + "'\n"
                + "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                + "and date(v.op_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and (v.vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n"
                + "and (v.remark like '" + remark + "%' or '-%'= '" + remark + "%')\n"
                + "and (v.created_by = '" + userCode + "' or '-'='" + userCode + "')\n"
                + "and (v.stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "and (v.loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n"
                + "group by v.vou_no\n"
                + "order by v.op_date,v.vou_no desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<OPHis> list = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                OPHis s = new OPHis();
                OPHisKey key = new OPHisKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                key.setDeptId(rs.getInt("dept_id"));
                s.setKey(key);
                s.setOpAmt(rs.getFloat("amount"));
                s.setVouDateStr(Util1.toDateStr(rs.getDate("op_date"), "dd/MM/yyyy"));
                s.setRemark(rs.getString("remark"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setDeleted(rs.getBoolean("deleted"));
                s.setLocName(rs.getString("loc_name"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark,
                                              String userCode, String stockCode, String locCode, String compCode, Integer deptId, String deleted) throws Exception {

        String filter = "";
        if (!vouNo.equals("-")) {
            filter += "and vou_no ='" + vouNo + "'\n";
        }
        if (!refNo.equals("-")) {
            filter += "and ref_no like '" + refNo + "%'\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like '" + remark + "%'\n";
        }
        if (!userCode.equals("-")) {
            filter += "and created_by ='" + userCode + "'\n";
        }
        if (!vouNo.equals("-")) {
            filter += "and stock_code ='" + stockCode + "'\n";
        }
        if (!locCode.equals("-")) {
            filter += "and (loc_code_from ='" + locCode + "' or loc_code_to ='" + locCode + "')\n";
        }
        String sql = "select date(v.vou_date) vou_date,v.vou_no,v.remark,v.ref_no,v.created_by,v.deleted,v.dept_id,l.loc_name from_loc_name,ll.loc_name to_loc_name\n"
                + "from v_transfer v join location l\n"
                + "on v.loc_code_from = l.loc_code\n"
                + "and v.comp_code = l.comp_code\n"
                + "join location ll on v.loc_code_to = ll.loc_code\n"
                + "and v.comp_code = ll.comp_code\n"
                + "where v.comp_code = '" + compCode + "'\n"
                + "and v.deleted = " + deleted + "\n"
                + "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n"
                + "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "" + filter + ""
                + "group by v.vou_no\n"
                + "order by v.vou_date,v.vou_no desc\n";
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
                s.setDeptId(rs.getInt("dept_id"));
                openingList.add(s);
            }
        }
        return openingList;
    }

    @Override
    public List<WeightLossHis> getWeightLossHistory(String fromDate, String toDate, String refNo, String vouNo,
                                                    String remark, String stockCode,
                                                    String locCode, String compCode,
                                                    Integer deptId, String deleted) {
        List<WeightLossHis> list = new ArrayList<>();
        String sql = "select vou_no,date(vou_date) vou_date,remark,ref_no,created_by,deleted\n" +
                "from v_weight_loss\n" +
                "where deleted =" + deleted + "\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and (vou_no ='" + vouNo + "' or '-' ='" + vouNo + "')\n" +
                "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-'='" + stockCode + "')\n" +
                "and (remark like '" + remark + "%' or '-%'='" + remark + "%')\n" +
                "and (ref_no like '" + refNo + "%' or '-%'='" + refNo + "%')\n" +
                "group by vou_no\n";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                WeightLossHis his = new WeightLossHis();
                WeightLossHisKey key = new WeightLossHisKey();
                key.setCompCode(compCode);
                key.setDeptId(deptId);
                key.setVouNo(rs.getString("vou_no"));
                his.setKey(key);
                his.setVouDate(rs.getDate("vou_date"));
                his.setRemark(rs.getString("remark"));
                his.setRefNo(rs.getString("ref_no"));
                his.setCreatedBy(rs.getString("created_by"));
                his.setDeleted(rs.getBoolean("deleted"));
                list.add(his);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
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
    public Float getSmallestQty(String stockCode, String unit, String compCode, Integer deptId) {
        float qty = 1.0f;
        String sql = "select ud.smallest_qty\n" +
                "from stock s join unit_relation_detail ud\n" +
                "on s.rel_code = ud.rel_code\n" +
                "and s.comp_code =ud.comp_code\n" +
                "and s.dept_id =ud.dept_id\n" +
                "where s.stock_code ='" + stockCode + "'\n" +
                "and s.comp_code ='" + compCode + "'\n" +
                "and s.dept_id =" + deptId + "\n" +
                "and ud.unit ='" + unit + "'";
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
        return searchDetail(stockCode, compCode);
    }

    private List<String> searchDetail(String code, String compCode) {
        List<String> str = new ArrayList<>();
        HashMap<String, String> hm = new HashMap<>();
        hm.put("sale_his_detail", "Sale");
        hm.put("pur_his_detail", "Purchase");
        hm.put("ret_in_his_detail", "Return In");
        hm.put("ret_out_his_detail", "Return Out");
        hm.put("stock_in_out_detail", "Stock In/Out");
        hm.put("op_his_detail", "Opening");
        hm.forEach((s, s2) -> {
            String sql = "select exists(select " + "stock_code" + " from " + s + " where " + "stock_code" + " ='" + code + "' and comp_code ='" + compCode + "') exist";
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

    private List<String> searchVoucher(String code, String compCode) {
        List<String> str = new ArrayList<>();
        HashMap<String, String> hm = new HashMap<>();
        hm.put("sale_his", "Sale");
        hm.put("pur_his", "Purchase");
        hm.put("ret_in_his", "Return In");
        hm.put("ret_out_his", "Return Out");
        hm.forEach((s, s2) -> {
            String sql = "select exists(select " + "trader_code" + " from " + s + " where " + "trader_code" + " ='" + code + "' and comp_code ='" + compCode + "') exist";
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
        return searchVoucher(traderCode, compCode);
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

    @Override
    public List<VStockIO> getProcessOutputDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode,
                                                 String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        List<VStockIO> list = new ArrayList<>();
        String sql = "select ifnull(v.user_code,v.stock_code) stock_code,v.stock_name,date(end_date) end_date, qty,unit,price,remark,process_no,vs.description,l.loc_name\n" +
                "from v_process_his v\n" +
                "join vou_status vs\n" +
                "on v.pt_code =vs.code\n" +
                "and v.comp_code = vs.comp_code\n" +
                "and v.dept_id = vs.dept_id\n" +
                "join location l on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "and v.dept_id = l.dept_id\n" +
                "where v.comp_code ='" + compCode + "'\n" +
                "and v.dept_id =" + deptId + "\n" +
                "and v.calculate=1\n" +
                "and v.finished =1\n" +
                "and v.loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and date(v.end_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (v.pt_code ='" + ptCode + "' or '-'='" + ptCode + "')\n" +
                "and (v.stock_type_code ='" + typeCode + "'or'-'='" + typeCode + "')\n" +
                "and (v.category_code ='" + catCode + "'or'-'='" + catCode + "')\n" +
                "and (v.brand_code ='" + brandCode + "'or'-'='" + brandCode + "')\n" +
                "and (v.stock_code ='" + stockCode + "' or '-'='" + stockCode + "')\n" +
                "order by vs.description,v.end_date\n";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            //stock_code, stock_name, end_date, qty, unit, price, remark, process_no, description
            if (rs != null) {
                while (rs.next()) {
                    VStockIO s = new VStockIO();
                    s.setStockCode(rs.getString("stock_code"));
                    s.setStockName(rs.getString("stock_name"));
                    s.setVouDate(Util1.toDateStr(rs.getDate("end_date"), "dd/MM/yyyy"));
                    s.setQty(rs.getFloat("qty"));
                    s.setUnit(rs.getString("unit"));
                    s.setPrice(rs.getFloat("price"));
                    s.setRemark(rs.getString("remark"));
                    s.setProcessNo(rs.getString("process_no"));
                    s.setDescription(rs.getString("description"));
                    s.setLocName(rs.getString("loc_name"));
                    list.add(s);
                }
            }
        } catch (Exception e) {
            log.error(String.format("getProcessOutputDetail : %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<VStockIO> getProcessOutputSummary(String fromDate, String toDate, String ptCode, String typeCode,
                                                  String catCode, String brandCode, String stockCode,
                                                  String compCode, Integer deptId, Integer macId) {
        List<VStockIO> list = new ArrayList<>();
        String sql = "select a.*,l.loc_name,vs.description\n" +
                "from (\n" +
                "select ifnull(user_code,stock_code) stock_code,stock_name,sum(qty) qty,unit,avg(price) avg_price,loc_code,pt_code,comp_code,dept_id\n" +
                "from v_process_his \n" +
                "where comp_code ='" + compCode + "'\n" +
                "and dept_id =" + deptId + "\n" +
                "and calculate=1\n" +
                "and finished =1\n" +
                "and deleted =0\n" +
                "and date(end_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (pt_code ='" + ptCode + "' or '-'='" + ptCode + "')\n" +
                "and (stock_type_code ='" + typeCode + "'or'-'='" + typeCode + "')\n" +
                "and (category_code ='" + catCode + "'or'-'='" + catCode + "')\n" +
                "and (brand_code ='" + brandCode + "'or'-'='" + brandCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-'='" + stockCode + "')\n" +
                "group by stock_code,loc_code,pt_code,unit)a\n" +
                "join location l on a.loc_code =l.loc_code\n" +
                "and a.comp_code = l.comp_code\n" +
                "and a.dept_id = l.dept_id\n" +
                "join vou_status vs\n" +
                "on a.pt_code =vs.code\n" +
                "and a.comp_code = vs.comp_code\n" +
                "and a.dept_id = vs.dept_id\n" +
                "order by vs.description,l.loc_name\n";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    VStockIO io = new VStockIO();
                    io.setStockCode(rs.getString("stock_code"));
                    io.setStockName(rs.getString("stock_name"));
                    io.setQty(rs.getFloat("qty"));
                    io.setUnit(rs.getString("unit"));
                    io.setPrice(rs.getFloat("avg_price"));
                    io.setLocName(rs.getString("loc_name"));
                    io.setDescription(rs.getString("description"));
                    list.add(io);
                }
            }

        } catch (Exception e) {
            log.error(String.format("getProcessOutputSummary : %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<VStockIO> getProcessUsageSummary(String fromDate, String toDate, String ptCode,
                                                 String typeCode, String catCode, String brandCode,
                                                 String stockCode, String compCode, Integer deptId, Integer macId) {
        List<VStockIO> list = new ArrayList<>();
        String sql = "select ifnull(v.user_code,v.stock_code) stock_code,v.stock_name,date(vou_date) vou_date, qty,unit,price,vs.description,l.loc_name\n" +
                "from v_process_his_detail v\n" +
                "join vou_status vs\n" +
                "on v.pt_code =vs.code\n" +
                "and v.comp_code = vs.comp_code\n" +
                "and v.dept_id = vs.dept_id\n" +
                "join location l on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "and v.dept_id = l.dept_id\n" +
                "where v.comp_code ='" + compCode + "'\n" +
                "and v.dept_id =" + deptId + "\n" +
                "and v.calculate=1\n" +
                "and v.loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (v.pt_code ='" + ptCode + "' or '-'='" + ptCode + "')\n" +
                "and (v.stock_type_code ='" + typeCode + "'or'-'='" + typeCode + "')\n" +
                "and (v.category_code ='" + catCode + "'or'-'='" + catCode + "')\n" +
                "and (v.brand_code ='" + brandCode + "'or'-'='" + brandCode + "')\n" +
                "and (v.stock_code ='" + stockCode + "' or '-'='" + stockCode + "')\n" +
                "order by vs.description,v.vou_date\n";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    //vou_date, stock_code, stock_name, qty, unit, price, loc_code, pt_code, comp_code, dept_id, loc_name, description
                    VStockIO io = new VStockIO();
                    io.setStockCode(rs.getString("stock_code"));
                    io.setStockName(rs.getString("stock_name"));
                    io.setQty(rs.getFloat("qty"));
                    io.setUnit(rs.getString("unit"));
                    io.setPrice(rs.getFloat("price"));
                    io.setLocName(rs.getString("loc_name"));
                    io.setDescription(rs.getString("description"));
                    list.add(io);
                }
            }

        } catch (Exception e) {
            log.error(String.format("getProcessUsageSummary : %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<VStockIO> getProcessUsageDetail(String fromDate, String toDate, String ptCode,
                                                String typeCode, String catCode, String brandCode,
                                                String stockCode, String compCode, Integer deptId,
                                                Integer macId) {
        List<VStockIO> list = new ArrayList<>();
        //vou_no, stock_code, comp_code, dept_id, unique_id, vou_date, qty, unit, price,
        // loc_code, deleted, pt_code, user_code, stock_name, stock_type_code, brand_code, category_code, calculate, rel_code, loc_name, description
        String sql = "select v.*,l.loc_name,vs.description\n" +
                "from v_process_his_detail v\n" +
                "join location l\n" +
                "on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "and v.dept_id = l.dept_id\n" +
                "join vou_status vs\n" +
                "on v.pt_code = vs.code\n" +
                "and v.comp_code = vs.comp_code\n" +
                "and v.dept_id = vs.dept_id\n" +
                "where v.comp_code ='" + compCode + "'\n" +
                "and v.dept_id =" + deptId + "\n" +
                "and v.deleted =0\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and v.loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (pt_code ='" + ptCode + "' or '-'='" + ptCode + "')\n" +
                "and (stock_type_code ='" + typeCode + "'or'-'='" + typeCode + "')\n" +
                "and (category_code ='" + catCode + "'or'-'='" + catCode + "')\n" +
                "and (brand_code ='" + brandCode + "'or'-'='" + brandCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-'='" + stockCode + "')\n" +
                "order by v.vou_date,vs.description,v.unique_id";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    VStockIO io = new VStockIO();
                    io.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    String sCode = rs.getString("stock_code");
                    String userCode = rs.getString("user_code");
                    io.setStockCode(Util1.isNull(userCode, sCode));
                    io.setStockName(rs.getString("stock_name"));
                    io.setQty(rs.getFloat("qty"));
                    io.setUnit(rs.getString("unit"));
                    io.setPrice(rs.getFloat("price"));
                    io.setLocName(rs.getString("loc_name"));
                    io.setDescription(rs.getString("description"));
                    list.add(io);
                }
            }

        } catch (Exception e) {
            log.error(String.format("getProcessUsageDetail : %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<GRN> getGRNHistory(String fromDate, String toDate, String traderCode, String vouNo,
                                   String remark, String userCode, String stockCode, String locCode,
                                   String compCode, Integer deptId, String deleted, String close, boolean orderByBatch) {
        List<GRN> list = new ArrayList<>();
        String orderBy = "order by vou_no desc";
        if (orderByBatch) {
            orderBy = "order by batch_no";
        }
        String filter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            filter += "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n";
        }
        if (!vouNo.equals("-")) {
            filter += "and g.vou_no = '" + vouNo + "'\n";
        }
        if (!traderCode.equals("-")) {
            filter += "and g.trader_code = '" + traderCode + "'\n";
        }
        if (!remark.equals("-")) {
            filter += "and g.remark like '" + traderCode + "%'\n";
        }
        if (!userCode.equals("-")) {
            filter += "and g.created_by = '" + userCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and gd.stock_code = '" + stockCode + "'\n";
        }
        if (!locCode.equals("-")) {
            filter += "and gd.loc_code = '" + locCode + "'\n";
        }
        String sql = "select a.*,t.user_code,t.trader_name\n" +
                "from (\n" +
                "select vou_date,g.vou_no,g.comp_code,g.dept_id,g.created_by,g.batch_no,remark,g.trader_code,g.deleted,g.closed\n" +
                "from grn g join grn_detail gd\n" +
                "on g.vou_no = gd.vou_no\n" +
                "and g.comp_code = gd.comp_code\n" +
                "and g.dept_id = gd.dept_id\n" +
                "where g.comp_code ='" + compCode + "'\n" +
                "and (g.dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted =" + deleted + "\n" +
                "and closed =" + close + "\n" +
                "" + filter + "" +
                "group by g.vou_no\n" +
                ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "and a.dept_id = t.dept_id\n" +
                "" + orderBy + "";
        try {
            //vou_date, vou_no, comp_code, dept_id, created_by, batch_no, remark, trader_code, user_code, trader_name
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    GRN g = new GRN();
                    GRNKey key = new GRNKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setVouNo(rs.getString("vou_no"));
                    key.setDeptId(rs.getInt("dept_id"));
                    g.setKey(key);
                    g.setVouDate(rs.getDate("vou_date"));
                    g.setBatchNo(rs.getString("batch_no"));
                    g.setRemark(rs.getString("remark"));
                    g.setTraderCode(rs.getString("trader_code"));
                    g.setTraderUserCode(rs.getString("user_code"));
                    g.setTraderName(rs.getString("trader_name"));
                    g.setDeleted(rs.getBoolean("deleted"));
                    g.setClosed(rs.getBoolean("closed"));
                    g.setCreatedBy(rs.getString("created_by"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error("getGRNHistory : " + e.getMessage());
        }
        return list;
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

    private void calculateClosing(String fromDate, String toDate, String typeCode, String catCode, String brandCode,
                                  String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                  String compCode, Integer deptId, Integer macId) {
        String delSql = "delete from tmp_stock_io_column where mac_id = " + macId + "";
        String opSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,op_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'Opening',a.tran_date,'-','Opening',a.stock_code,sum(smallest_qty) smallest_qty,a.loc_code,a.mac_id,'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select tmp.tran_date,tmp.stock_code,tmp.ttl_qty * rel.smallest_qty smallest_qty,tmp.loc_code,tmp.mac_id\n"
                + "from tmp_stock_opening tmp \n"
                + "join stock s on tmp.stock_code = s.stock_code\n"
                + "and tmp.comp_code = s.comp_code\n"
                + "and tmp.dept_id = s.dept_id\n"
                + "join v_relation rel on s.rel_code = rel.rel_code\n"
                + "and tmp.comp_code = rel.comp_code\n"
                + "and tmp.dept_id = rel.dept_id\n"
                + "and tmp.unit = rel.unit\n"
                + "where tmp.mac_id =" + macId + ")a\n"
                + "group by tran_date,stock_code,mac_id";
        String purSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,pur_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'Purchase',a.vou_date vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code, pur_unit,rel_code,comp_code,dept_id\n"
                + "from v_purchase\n"
                + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and deleted = 0 \n"
                + "and (calculate = 1 and " + calPur + " = 0)\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),vou_no,stock_code,pur_unit)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "and a.pur_unit = rel.unit\n"
                + "group by a.vou_date ,a.stock_code,a.vou_no";
        //ret in
        String retInSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'ReturnIn',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,rel_code, unit,comp_code,dept_id\n"
                + "from v_return_in\n"
                + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n"
                + "and (calculate = 1 and " + calRI + " = 0)\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),stock_code,vou_no,unit)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "and a.unit = rel.unit\n"
                + "group by vou_date,stock_code,vou_no";
        String stockInSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'StockIn',date(a.vou_date) vou_date,vou_no,a.description,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,description,stock_code,sum(in_qty) qty,loc_code,in_unit,rel_code,comp_code,dept_id\n"
                + "from v_stock_io\n"
                + "where ifnull(in_qty,0)<>0 and in_unit is not null\n"
                + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and deleted = 0 \n"
                + "and calculate = 1 \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and (vou_status ='" + vouStatus + "' or '-'='" + vouStatus + "')\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),stock_code,in_unit,vou_no)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "and a.in_unit = rel.unit\n"
                + "group by a.vou_date ,a.stock_code,a.vou_no";
        String saleSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,sale_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'Sale',a.vou_date ,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code, sale_unit,rel_code,comp_code,dept_id\n"
                + "from v_sale\n"
                + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and deleted = 0 \n" + "and (calculate = 1 and " + calSale + " = 0)\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),stock_code,sale_unit,vou_no)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "and a.sale_unit = rel.unit\n"
                + "group by a.vou_date,a.stock_code,a.vou_no";
        String returnOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'ReturnOut',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code, unit,rel_code,comp_code,dept_id\n"
                + "from v_return_out\n"
                + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and deleted = 0 \n"
                + "and (calculate = 1 and " + calRO + " = 0)\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),stock_code,unit,vou_no)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "and a.unit = rel.unit\n"
                + "group by vou_date,stock_code,vou_no";
        String stockOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'StockOut',a.vou_date,a.vou_no,a.description,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,description,stock_code,sum(out_qty) qty,loc_code,out_unit,rel_code,comp_code,dept_id\n"
                + "from v_stock_io\n"
                + "where ifnull(out_qty,0)<>0 and out_unit is not null\n"
                + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and deleted = 0 \n"
                + "and calculate = 1 \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and (vou_status ='" + vouStatus + "' or '-'='" + vouStatus + "')\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),stock_code,out_unit,vou_no)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "and a.out_unit = rel.unit\n"
                + "group by vou_date,a.stock_code,vou_no";
        String fFSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'Transfer-F',a.vou_date,a.vou_no,if(ifnull(a.remark,'')='','Transfer',a.remark),a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,\n"
                + "loc_code_from," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code_from,rel_code, unit,comp_code,dept_id\n"
                + "from v_transfer\n"
                + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n"
                + "and calculate = 1 \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and loc_code_from in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),stock_code,unit,vou_no)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n"
                + "and a.comp_code = rel.comp_code\n"
                + "and a.dept_id = rel.dept_id\n"
                + "and a.unit = rel.unit\n"
                + "group by vou_date,stock_code,vou_no";
        String tFSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n"
                + "select 'Transfer-T',a.vou_date,a.vou_no,if(ifnull(a.remark,'')='','Transfer',a.remark),a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,\n"
                + "loc_code_to," + macId + ",'" + compCode + "'," + deptId + "\n"
                + "from (\n"
                + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code_to,rel_code, unit\n"
                + "from v_transfer\n"
                + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n"
                + "and deleted = 0 \n" + "and calculate = 1 \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and loc_code_to in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by date(vou_date),stock_code,unit,vou_no)a\n"
                + "join v_relation rel on a.rel_code = rel.rel_code\n"
                + "and a.unit = rel.unit\n"
                + "group by vou_date,stock_code,vou_no";
        String pIn = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'P-IN',a.end_date ,a.vou_no,v.description,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from (\n" +
                "select date(end_date) end_date,vou_no,pt_code,stock_code,sum(qty) qty,loc_code, unit,rel_code,comp_code,dept_id\n" +
                "from v_process_his\n" +
                "where date(end_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = 0 \n" +
                "and calculate = 1\n" +
                "and finished = 1\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and dept_id =" + deptId + "\n" +
                "and (pt_code ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(end_date),stock_code,unit,vou_no)a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and a.dept_id = rel.dept_id\n" +
                "and a.unit = rel.unit\n" +
                "join vou_status v on a.pt_code = v.code\n" +
                "and a.comp_code = v.comp_code\n" +
                "and a.dept_id = v.dept_id\n" +
                "group by a.end_date,a.stock_code,a.vou_no";
        String pOut = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'P-OUT',a.vou_date ,a.vou_no,v.description,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from (\n" +
                "select date(vou_date) vou_date,vou_no,pt_code,stock_code,sum(qty) qty,loc_code, unit,rel_code,comp_code,dept_id\n" +
                "from v_process_his_detail\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = 0 \n" +
                "and calculate = 1\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and dept_id =" + deptId + "\n" +
                "and (pt_code ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),stock_code,unit,vou_no)a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and a.dept_id = rel.dept_id\n" +
                "and a.unit = rel.unit\n" +
                "join vou_status v on a.pt_code = v.code\n" +
                "and a.comp_code = v.comp_code\n" +
                "and a.dept_id = v.dept_id\n" +
                "group by a.vou_date,a.stock_code,a.vou_no";
        try {
            reportDao.executeSql(delSql, opSql, purSql, retInSql, stockInSql, stockOutSql, saleSql, returnOutSql, fFSql, tFSql, pIn, pOut);
        } catch (Exception e) {
            log.error(String.format("calculateClosing: %s", e.getMessage()));
        }
        log.info("calculate closing.");

    }

    private void calculateOpening(String opDate, String fromDate, String typeCode,
                                  String catCode, String brandCode, String stockCode, String vouStatus,
                                  boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                  String compCode, Integer deptId, Integer macId) {
        //delete tmp
        String delSql = "delete from tmp_stock_opening where mac_id = " + macId + "";
        //opening
        String opSql = "insert into tmp_stock_opening(tran_date,stock_code,ttl_qty,loc_code,unit,comp_code,dept_id,mac_id)\n"
                + "select '" + fromDate + "' op_date ,stock_code,sum(qty) ttl_qty,loc_code,unit,'" + compCode + "'," + deptId + "," + macId + " \n"
                + "from (\n"
                + "select stock_code,sum(qty) qty,loc_code, unit\n"
                + "from v_opening\n"
                + "where date(op_date) = '" + opDate + "'\n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
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
                + "and deleted = 0 \n"
                + "and dept_id ='" + deptId + "'\n"
                + "and (calculate = 1 and " + calPur + "=0) \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,pur_unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty) qty,loc_code, unit\n"
                + "from v_return_in\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and deleted = 0 \n"
                + "and (calculate = 1 and " + calRI + "=0) \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
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
                + "and dept_id ='" + deptId + "'\n"
                + "and (vou_status ='" + vouStatus + "' or '-'='" + vouStatus + "')\n"
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
                + "and dept_id ='" + deptId + "'\n"
                + "and (vou_status ='" + vouStatus + "' or '-'='" + vouStatus + "')\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,out_unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty)*-1 qty,loc_code, unit\n"
                + "from v_return_out\n" + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and deleted = false \n"
                + "and (calculate = 1 and " + calRO + "=0) \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
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
                + "and deleted = 0 \n"
                + "and (calculate = 1 and " + calSale + "=0) \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,sale_unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty)*-1 qty,loc_code_from, unit\n"
                + "from v_transfer\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and deleted = 0 \n"
                + "and calculate = 1 \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
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
                + "and calculate = 1 \n"
                + "and comp_code ='" + compCode + "'\n"
                + "and dept_id ='" + deptId + "'\n"
                + "and loc_code_to in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty)*-1 qty,loc_code, unit\n"
                + "from v_process_his_detail\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n"
                + "and dept_id =" + deptId + "\n"
                + "and (pt_code ='" + vouStatus + "' or '-'='" + vouStatus + "')\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,unit\n"
                + "\tunion all\n"
                + "select stock_code,sum(qty) qty,loc_code, unit\n"
                + "from v_process_his\n"
                + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n"
                + "and deleted =0\n"
                + "and (pt_code ='" + vouStatus + "' or '-'='" + vouStatus + "')\n"
                + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n"
                + "and dept_id =" + deptId + "\n"
                + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n"
                + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n"
                + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n"
                + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n"
                + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n"
                + "group by stock_code,unit\n"
                + ")a\n"
                + "group by stock_code,unit";
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

    private void calculatePrice(String toDate, String opDate, String stockCode, String typeCode, String catCode, String brandCode, String compCode, Integer deptId, Integer macId) {
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
                    "where deleted =0\n" +
                    "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                    "and (stock_type_code ='" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                    "and (brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                    "and (category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" +
                    "and date(vou_date) <='" + toDate + "'\n" +
                    "and pur.dept_id =" + deptId + "\n" +
                    "and pur.comp_code ='" + compCode + "'\n" +
                    "group by pur.stock_code\n" +
                    "\tunion all\n" +
                    "select 'OP',op.stock_code,avg(op.price/rel.smallest_qty) avg_price\n" +
                    "from v_opening op\n" +
                    "join v_relation rel\n" +
                    "on op.rel_code = rel.rel_code\n" +
                    "and op.unit = rel.unit\n" +
                    "where op.price > 0\n" +
                    "and deleted =0\n" +
                    "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                    "and (stock_type_code ='" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                    "and (brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                    "and (category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" +
                    "and date(op_date) <='" + toDate + "'\n" +
                    "and op.dept_id =" + deptId + "\n" +
                    "and op.comp_code ='" + compCode + "'\n" +
                    "group by op.stock_code)a\n" +
                    "group by stock_code";
            String sInSql = "insert into tmp_stock_price(tran_option,stock_code,in_avg_price,mac_id)\n" +
                    "select 'SIN-AVG',stock_code,avg(avg_price)," + macId + "\n" +
                    "from(\n" +
                    "select 'SIN-AVG',sio.stock_code,avg(sio.cost_price/rel.smallest_qty) avg_price\n" +
                    "from v_stock_io sio\n" + "join v_relation rel\n" +
                    "on sio.rel_code = rel.rel_code\n" +
                    "and sio.in_unit = rel.unit\n" +
                    "where in_qty is not null and in_unit is not null and cost_price >0\n" +
                    "and sio.deleted =0\n" +
                    "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                    "and (stock_type_code ='" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                    "and (brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                    "and (category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" +
                    "and date(vou_date) <='" + toDate + "'\n" +
                    "and sio.dept_id =" + deptId + "\n" +
                    "and sio.comp_code ='" + compCode + "'\n" +
                    "group by sio.stock_code\n" +
                    "\tunion all\n" +
                    "select 'OP',op.stock_code,avg(op.price/rel.smallest_qty) avg_price\n" + "from v_opening op\n" +
                    "join v_relation rel\n" + "on op.rel_code = rel.rel_code\n" + "and op.unit = rel.unit\n" +
                    "where op.price > 0\n" +
                    "and op.deleted =0\n" +
                    "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                    "and (stock_type_code ='" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                    "and (brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                    "and (category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" +
                    "and date(op_date) ='" + opDate + "'\n" +
                    "and op.dept_id =" + deptId + "\n" +
                    "and op.comp_code ='" + compCode + "'\n" +
                    "group by op.stock_code\n" +
                    "\tunion all\n" +
                    "select 'SOUT-AVG',sio.stock_code,avg(sio.cost_price/rel.smallest_qty) avg_price\n" +
                    "from v_stock_io sio\n" + "join v_relation rel\n" +
                    "on sio.rel_code = rel.rel_code\n" +
                    "and sio.out_unit = rel.unit\n" +
                    "where out_qty is not null and out_unit is not null and cost_price >0\n" +
                    "and sio.deleted =0\n" +
                    "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                    "and (stock_type_code ='" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                    "and (brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                    "and (category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" +
                    "and date(vou_date) <='" + toDate + "'\n" +
                    "and sio.dept_id =" + deptId + "\n" +
                    "and sio.comp_code ='" + compCode + "'\n" +
                    "group by sio.stock_code\n" + ")a\n" +
                    "group by stock_code";
            String purRecentSql = "insert into tmp_stock_price(stock_code,tran_option,pur_recent_price,mac_id)\n" +
                    "select a.stock_code,'PUR_RECENT',a.pur_price/rel.smallest_qty pur_price," + macId + "\n" +
                    "from (\n" +
                    "with rows_and_position as \n" +
                    "  ( \n" +
                    "    select stock_code, pur_price,pur_unit,row_number() over (partition by stock_code order by vou_date desc) as position,rel_code,comp_code,dept_id\n" +
                    "    from v_purchase\n" +
                    "    where (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                    "    and (stock_type_code ='" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                    "    and (brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                    "    and (category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" +
                    "    and date(vou_date) <='" + toDate + "'\n" +
                    "    and dept_id =" + deptId + "\n" +
                    "    and comp_code ='" + compCode + "'\n" +
                    "    and deleted =0\n" +
                    "  )\n" +
                    "select stock_code, pur_price,pur_unit,rel_code,comp_code,dept_id\n" +
                    "from  rows_and_position\n" +
                    "where position =1\n" +
                    ")a\n" +
                    "join v_relation rel\n" +
                    "on a.rel_code = rel.rel_code\n" +
                    "and a.pur_unit = rel.unit\n" +
                    "and a.comp_code = rel.comp_code\n" +
                    "and a.dept_id = rel.dept_id";
            reportDao.executeSql(delSql, purSql, sInSql, purRecentSql);
        } catch (Exception e) {
            log.error(String.format("calculatePrice: %s", e.getMessage()));
        }
    }
}
