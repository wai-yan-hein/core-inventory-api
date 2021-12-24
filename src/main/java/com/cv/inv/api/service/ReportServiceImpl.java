/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.common.ClosingBalance;
import com.cv.inv.api.common.General;
import com.cv.inv.api.common.ReportFilter;
import com.cv.inv.api.common.Util1;
import com.cv.inv.api.dao.ReportDao;
import com.cv.inv.api.entity.ReorderLevel;
import com.cv.inv.api.entity.Stock;
import com.cv.inv.api.entity.StockUnit;
import com.cv.inv.api.entity.VStockBalance;
import com.cv.inv.api.view.VPurchase;
import com.cv.inv.api.view.VSale;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lenovo
 */
@Service
@Transactional
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ReportDao reportDao;
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void executeSql(String... sql) throws Exception {
        reportDao.executeSql(sql);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void saveReportFilter(ReportFilter filter) throws Exception {
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
        String sql = "select comp_name,comp_address,comp_phone,\n" +
                "trader_name,remark,vou_no,vou_date,stock_name,\n" +
                "qty,sale_price,sale_unit,sale_amt,vou_total,discount,paid,vou_balance\n" +
                "from v_sale\n" +
                "where vou_no ='" + vouNo + "'";
        ResultSet rs = reportDao.executeSql(sql);
        while (rs.next()) {
            VSale sale = new VSale();
            sale.setCompName(rs.getString("comp_name"));
            sale.setCompAddress(rs.getString("comp_address"));
            sale.setCompPhone(rs.getString("comp_phone"));
            sale.setTraderName(rs.getString("trader_name"));
            sale.setRemark(rs.getString("remark"));
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
            saleList.add(sale);
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseVoucher(String vouNo) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select comp_name,comp_address,comp_phone,trader_name,remark,vou_no,\n" +
                "vou_date,stock_name,qty,pur_price,pur_amt,vou_total,discount,paid,balance\n" +
                "from v_purchase\n" +
                "where vou_no ='" + vouNo + "'";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase purchase = new VPurchase();
                purchase.setCompName(rs.getString("comp_name"));
                purchase.setCompAddress(rs.getString("comp_address"));
                purchase.setCompPhone(rs.getString("comp_phone"));
                purchase.setTraderName(rs.getString("trader_name"));
                purchase.setRemark(rs.getString("remark"));
                purchase.setVouNo(rs.getString("vou_no"));
                purchase.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                purchase.setStockName(rs.getString("stock_name"));
                purchase.setQty(rs.getFloat("qty"));
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
    public List<VSale> getSaleByCustomerDetail(String fromDate, String toDate,
                                               String curCode, String vouNo,
                                               String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select vou_date,vou_no,trader_code,trader_name,stock_name,qty,sale_wt,sale_unit,sale_price,sale_amt,\n" +
                "comp_name\n" +
                "from v_sale\n" +
                "where trader_code in (select f_code from f_trader where mac_id = " + macId + ")\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by trader_name,vou_no";
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
                sale.setCompName(rs.getString("comp_name"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode, String vouNo, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select vou_date,vou_no,trader_code,trader_name,\n" +
                "stock_name,qty,avg_wt,pur_unit,pur_price,pur_amt,\n" +
                "comp_name\n" +
                "from v_purchase\n" +
                "where trader_code in (select f_code from f_trader where mac_id = " + macId + ")\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
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
                p.setCompName(rs.getString("comp_name"));
                purchaseList.add(p);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VSale> getSaleByStockDetail(String fromDate, String toDate, String curCode, String vouNo, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select vou_date,vou_no,trader_code,trader_name,stock_name,qty,sale_wt,sale_unit,sale_price,sale_amt,\n" +
                "comp_name\n" +
                "from v_sale\n" +
                "where trader_code in (select f_code from f_trader where mac_id = " + macId + ")\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by stock_name,vou_no";
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
                sale.setCompName(rs.getString("comp_name"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseByStockDetail(String fromDate, String toDate, String curCode, String vouNo, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select vou_date,vou_no,trader_code,trader_name,\n" +
                "stock_name,qty,avg_wt,pur_unit,pur_price,pur_amt,\n" +
                "comp_name\n" +
                "from v_purchase\n" +
                "where trader_code in (select f_code from f_trader where mac_id = " + macId + ")\n" +
                "and deleted = false\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by stock_name,vou_no;";
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
                p.setCompName(rs.getString("comp_name"));
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
    public List<VStockBalance> getStockBalance(String stockCode) throws Exception {
        List<VStockBalance> balances = new ArrayList<>();
        String sql = "select stock_code, stock_name,ttl_qty, wt, unit, loc_code,loc_name\n" +
                "from v_stock_balance\n" +
                "where stock_code ='" + stockCode + "'";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockBalance b = new VStockBalance();
                b.setStockCode(rs.getString("stock_code"));
                b.setTotalQty(rs.getFloat("ttl_qty"));
                b.setWeight(rs.getFloat("wt"));
                b.setUnitName(rs.getString("unit"));
                b.setLocCode(rs.getString("loc_code"));
                b.setLocationName(rs.getString("loc_name"));
                b.setStockName(rs.getString("stock_name"));
                balances.add(b);
            }
            return balances;
        }
        return null;
    }

    @Override
    public List<ClosingBalance> getClosingStock(String fromDate, String toDate, Integer macId) throws Exception {
        insertPriceDetail(fromDate, toDate, macId);
        insertClosingIntoColumn(macId);
        String sql = "select st.user_code type_user_code,st.stock_type_name,s.user_code,s.stock_name,a.*,\n" +
                "a.op_pur_amt/a.op_pur_qty avg_cost,com.name comp_name,\n" +
                "com.address comp_address,com.phone comp_phone\n" +
                "from (\n" +
                "select stock_code,\n" +
                "sum(op_qty) op_qty,sum(op_amt) op_amt,\n" +
                "sum(pur_qty) pur_qty,sum(pur_amt) pur_amt,\n" +
                "sum(in_qty) in_qty,sum(in_amt) in_amt,\n" +
                "sum(out_qty) out_qty,sum(out_amt) out_amt,\n" +
                "sum(sale_qty) sale_qty,sum(sale_amt) sale_amt,\n" +
                "sum(op_amt)+sum(pur_amt) op_pur_amt,\n" +
                "sum(op_qty)+sum(pur_qty) op_pur_qty,\n" +
                "sum(op_qty)+sum(pur_qty)+sum(in_qty)+sum(out_qty)+sum(sale_qty) bal_qty,comp_code\n" +
                "from tmp_closing_column\n" +
                "where mac_id = " + macId + "\n" +
                "group by stock_code\n" +
                ")a\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "join company_info com on a.comp_code = com.comp_code\n" +
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
                cb.setAvgCost(rs.getFloat("avg_cost"));
                cb.setCompName(rs.getString("comp_name"));
                cb.setCompPhone(rs.getString("comp_phone"));
                cb.setCompAddress(rs.getString("comp_address"));
                balanceList.add(cb);
            }
        }
        return balanceList;
    }

    @Override
    public List<ReorderLevel> getReorderLevel(String compCode) throws Exception {
        //update stock balance
        String sql = "select stock_code,stock_name,sum(ttl_qty) ttl_qty,unit\n" +
                "from v_stock_balance\n" +
                "group by stock_code,unit";
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
        log.info("reorder : update stock balance ");
        String hsql = "select o from ReorderLevel o join Stock s on o.stock.stockCode = s.stockCode\n" +
                " where o.compCode ='" + compCode + "'\n" +
                "order by s.userCode";
        return getSession().createQuery(hsql, ReorderLevel.class).list();
    }

    @Override
    public void generateReorder(String compCode) throws Exception {
        //generate reorder
        String rSql = "select s.stock_code,s.pur_unit,rl.stock_code ro_stock_code\n" +
                "from stock s left join reorder_level rl\n" +
                "on s.stock_code = rl.stock_code\n" +
                "where s.comp_code= '" + compCode + "'";
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

    private void insertClosingIntoColumn(Integer macId) throws Exception {
        //delete tmp
        String delSql = "delete from tmp_closing_column where mac_id = " + macId + "";
        executeSql(delSql);
        //opening
        String opSql = "insert into tmp_closing_column(tran_option, tran_date,stock_code,loc_code,op_qty,op_price,op_amt,op_unit,mac_id,comp_code)\n" +
                "select tran_option,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where tran_option ='Opening' and mac_id = " + macId + "\n" +
                "group by tran_option, tran_date,stock_code,loc_code,mac_id\n";
        executeSql(opSql);
        //purchase
        String purSql = "insert into tmp_closing_column(tran_option, tran_date,stock_code,loc_code,pur_qty,pur_price,pur_amt,pur_unit,mac_id,comp_code)\n" +
                "select tran_option,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where tran_option ='Purchase' and mac_id = " + macId + "\n" +
                "group by tran_option, tran_date,stock_code,loc_code,mac_id\n";
        executeSql(purSql);
        //stock in
        String inSql = "insert into tmp_closing_column(tran_option, tran_date,stock_code,loc_code,in_qty,in_price,in_amt,in_unit,mac_id,comp_code)\n" +
                "select 'In' tran_option,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where (tran_option ='ReturnIn' or\n" +
                "tran_option= 'StockIn') and mac_id = " + macId + "\n" +
                "group by tran_date,stock_code,loc_code,mac_id\n";
        executeSql(inSql);
        //return in
        //sale
        String saleSql = "insert into tmp_closing_column(tran_option, tran_date,stock_code,loc_code,sale_qty,sale_price,sale_amt,sale_unit,mac_id,comp_code)\n" +
                "select tran_option,tran_date, stock_code,loc_code,sum(qty)*-1 ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where tran_option ='Sale' and mac_id = " + macId + "\n" +
                "group by tran_option, tran_date,stock_code,loc_code,mac_id\n";
        executeSql(saleSql);
        //stock out
        String outSql = "insert into tmp_closing_column(tran_option, tran_date,stock_code,loc_code,out_qty,out_price,out_amt,out_unit,mac_id,comp_code)\n" +
                "select 'Out' tran_option,tran_date, stock_code,loc_code,sum(qty)*-1 ttl_qty,sum(price) ttl_price,\n" +
                "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" +
                "from tmp_inv_closing\n" +
                "where (tran_option ='ReturnOut' or\n" +
                "tran_option= 'StockOut') and mac_id = " + macId + "\n" +
                "group by tran_date,stock_code,loc_code,mac_id\n";
        executeSql(outSql);
        //return out
    }

    private void insertPriceDetail(String fromDate, String toDate, Integer macId) throws Exception {
        //delete tmp
        String delSql = "delete from tmp_inv_closing where mac_id = " + macId + "";
        executeSql(delSql);
        //opening
        String insertSql = "insert into tmp_inv_closing(tran_option, tran_date, vou_no, stock_code, qty, price, loc_code, unit, mac_id,comp_code)\n";
        String opSql = "select 'Opening', op_date, vou_no, stock_code, qty, price, loc_code, unit," + macId + ",comp_code\n" +
                "from v_opening\n" +
                "where date(op_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and stock_type_code in (select f_code from f_stock_type where mac_id = " + macId + ")";
        executeSql(String.format("%s\n%s", insertSql, opSql));
        //purchase
        String purSql = "select 'Purchase',vou_date,vou_no,stock_code,qty,pur_price,loc_code,pur_unit," + macId + ",comp_code\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and stock_type_code in (select f_code from f_stock_type where mac_id = " + macId + ")";
        executeSql(String.format("%s\n%s", insertSql, purSql));
        //stockIn
        String inSql = "select 'StockIn',vou_date,vou_no,stock_code,in_qty,cost_price,loc_code,in_unit," + macId + ",comp_code\n" +
                "from v_stock_io\n" +
                "where in_qty is not null and in_unit is not null\n" +
                "and deleted = false \n" +
                "and  date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and stock_type_code in (select f_code from f_stock_type where mac_id = " + macId + ");";
        executeSql(String.format("%s\n%s", insertSql, inSql));
        //return in
        String retInSql = "select 'ReturnIn',vou_date,vou_no,stock_code,qty,cost_price,loc_code,unit," + macId + ",comp_code\n" +
                "from v_return_in\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and stock_type_code in (select f_code from f_stock_type where mac_id=" + macId + ")";
        executeSql(String.format("%s\n%s", insertSql, retInSql));
        //sale
        String saleSql = "select 'Sale',vou_date,vou_no,stock_code,qty,sale_price,loc_code,sale_unit," + macId + ",comp_code\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and stock_type_code in (select f_code from f_stock_type where mac_id = " + macId + ")";
        executeSql(String.format("%s\n%s", insertSql, saleSql));
        //stockOut
        String outSql = "select 'StockOut',vou_date,vou_no,stock_code,out_qty,cost_price,loc_code,out_unit," + macId + ",comp_code\n" +
                "from v_stock_io\n" +
                "where out_qty is not null and out_unit is not null\n" +
                "and deleted = false \n" +
                "and  date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and stock_type_code in (select f_code from f_stock_type where mac_id = " + macId + ")";
        executeSql(String.format("%s\n%s", insertSql, outSql));
        //return out
        String retOutSql = "select 'ReturnOut',vou_date,vou_no,stock_code,qty,price,loc_code,unit," + macId + ",comp_code\n" +
                "from v_return_out\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and stock_type_code in (select f_code from f_stock_type where mac_id=" + macId + ")";
        executeSql(String.format("%s\n%s", insertSql, retOutSql));
    }

    private void insertTmp(List<String> listStr, Integer macId, String taleName) throws Exception {
        deleteTmp(taleName, macId);
        for (String str : listStr) {
            String sql = "insert into " + taleName + "(f_code,mac_id)\n" +
                    "select '" + str + "'," + macId + "";
            executeSql(sql);
            //log.info(String.format("insertTmp : %s", sql));
        }
    }

    private void deleteTmp(String tableName, Integer macId) throws Exception {
        String delSql = "delete from " + tableName + " where mac_id =" + macId + "";
        executeSql(delSql);
        //log.info(String.format("deleteTmp : %s", delSql));
    }
}
