/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.*;
import cv.api.dao.LandingHisPriceDao;
import cv.api.dao.ReportDao;
import cv.api.dao.UnitRelationDetailDao;
import cv.api.entity.*;
import cv.api.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author wai yan
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private final HashMap<String, List<UnitRelationDetail>> hmRelation = new HashMap<>();
    private final ReportDao reportDao;
    private final UnitRelationDetailDao detailDao;
    private final LandingHisPriceDao landingHisPriceDao;
    private final DatabaseClient client;
    private final OPHisService opHisService;

    @Override
    public void executeSql(String... sql) {
        reportDao.executeSql(sql);
    }

    @Override
    public ResultSet getResult(String sql, Object... params) throws Exception {
        return reportDao.getResultSql(sql, params);
    }

    @Override
    public ResultSet getResult(String sql) throws Exception {
        return reportDao.executeSql(sql);
    }

    @Override
    public String getOpeningDate(String compCode, int tranSource) {
        String opDate = null;
        String sql = """
                select
                case
                when exists (
                            select 1 from op_his
                            where deleted = false
                            and comp_code =?
                            and tran_source =?
                        ) then op_date
                        else max(op_date)
                    end as op_date
                from op_his
                where deleted = false
                and comp_code =?
                """;
        try {
            ResultSet rs = reportDao.getResultSql(sql, compCode, tranSource, compCode);
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
    public String getOpeningDateByLocation(String compCode, String locCode) {
        String opDate = null;
        String sql = """
                select max(op_date) op_date
                from op_his
                where deleted = false
                and comp_code =?
                and (loc_code =? or '-'=?)
                and (tran_source=1 or tran_source=3)
                """;
        try {
            ResultSet rs = reportDao.getResultSql(sql, compCode, locCode, locCode);
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
    public List<VSale> getSaleVoucher(String vouNo, String compCode) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = """
                select t.trader_name,t.rfid,t.phone,t.address,v.remark,v.reference,v.vou_no,v.vou_date,v.stock_name,
                v.qty,v.bag,v.weight,v.weight_unit,v.sale_price,v.sale_unit,v.sale_amt,v.vou_total,v.discount,
                v.paid,v.vou_balance,t.user_code t_user_code,t.phone,t.address,l.loc_name,v.created_by,
                v.comp_code,c.cat_name,r.reg_name,u1.unit_name sale_unit_name,u2.unit_name weight_unit_name
                from v_sale v join trader t
                on v.trader_code = t.code
                and v.comp_code = t.comp_code
                left join region r on t.reg_code = r.reg_code
                and t.comp_code = r.comp_code
                join location l on v.loc_code = l.loc_code
                and v.comp_code = l.comp_code
                left join category c on v.cat_code = c.cat_code
                and v.comp_code = c.comp_code
                left join stock_unit u1 on v.sale_unit = u1.unit_code
                and v.comp_code = u1.comp_code
                left join stock_unit u2 on v.weight_unit = u2.unit_code
                and v.comp_code = u2.comp_code
                where v.vou_no =?
                and v.comp_code =?""";
        ResultSet rs = reportDao.getResultSql(sql, vouNo, compCode);
        while (rs.next()) {
            VSale sale = VSale.builder().build();
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
            sale.setReference(rs.getString("reference"));
            sale.setPhoneNo(rs.getString("phone"));
            sale.setAddress(rs.getString("address"));
            sale.setRfId(rs.getString("rfid"));
            sale.setVouNo(rs.getString("vou_no"));
            sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
            sale.setStockName(rs.getString("stock_name"));
            sale.setQty(rs.getDouble("qty"));
            sale.setBag(rs.getDouble("bag"));
            sale.setSalePrice(rs.getDouble("sale_price"));
            sale.setSaleAmount(rs.getDouble("sale_amt"));
            sale.setVouTotal(rs.getDouble("vou_total"));
            sale.setDiscount(rs.getDouble("discount"));
            sale.setPaid(rs.getDouble("paid"));
            sale.setVouBalance(rs.getDouble("vou_balance"));
            sale.setSaleUnit(rs.getString("sale_unit"));
            sale.setCusAddress(Util1.isNull(rs.getString("phone"), "") + "/" + Util1.isNull(rs.getString("address"), ""));
            sale.setLocationName(rs.getString("loc_name"));
            sale.setCreatedBy(rs.getString("created_by"));
            sale.setCompCode(rs.getString("comp_code"));
            sale.setCategoryName(rs.getString("cat_name"));
            sale.setRegionName(rs.getString("reg_name"));
            sale.setSaleUnitName(rs.getString("sale_unit_name"));
            sale.setWeightUnitName(rs.getString("weight_unit_name"));
            double weight = rs.getDouble("weight");
            if (weight > 0) {
                sale.setWeight(weight);
                sale.setWeightUnit(rs.getString("weight_unit"));
            }
            saleList.add(sale);
        }
        if (!saleList.isEmpty()) {
            //this is hla chan myae
            //List<VouDiscount> listDis = saleHisService.getVoucherDiscount(vouNo, compCode).collectList().block();
            // saleList.getFirst().setListDiscount(listDis);
        }
        return saleList;
    }

    @Override
    public List<VOrder> getOrderVoucher(String vouNo, String compCode) throws Exception {
        List<VOrder> orderList = new ArrayList<>();
        String sql = """
                select t.trader_name,t.rfid,t.phone,t.address,v.remark,v.vou_no,v.vou_date,v.stock_name,
                v.qty,v.weight,v.weight_unit,v.price,v.unit,v.amt,t.user_code t_user_code,t.phone,t.address,
                l.loc_name,v.created_by,v.comp_code,os.description
                from v_order v join trader t
                on v.trader_code = t.code
                and v.comp_code = t.comp_code
                join location l on v.loc_code = l.loc_code
                and  v.comp_code = l.comp_code
                join order_status os on v.order_status = os.code
                and v.comp_code = os.comp_code
                where v.vou_no =?
                and v.comp_code =?""";
        ResultSet rs = reportDao.getResultSql(sql, vouNo, compCode);
        while (rs.next()) {
            VOrder order = new VOrder();
            String remark = rs.getString("remark");
            order.setTraderCode(rs.getString("t_user_code"));
            order.setTraderName(rs.getString("trader_name"));
            order.setRemark(remark);
            order.setPhoneNo(rs.getString("phone"));
            order.setAddress(rs.getString("address"));
            order.setRfId(rs.getString("rfid"));
            order.setVouNo(rs.getString("vou_no"));
            order.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
            order.setStockName(rs.getString("stock_name"));
            order.setQty(rs.getFloat("qty"));
            order.setSalePrice(rs.getFloat("price"));
            order.setSaleAmount(rs.getFloat("amt"));
            order.setSaleUnit(rs.getString("unit"));
            order.setLocationName(rs.getString("loc_name"));
            order.setCreatedBy(rs.getString("created_by"));
            order.setCompCode(rs.getString("comp_code"));
            float weight = rs.getFloat("weight");
            if (weight > 0) {
                order.setWeight(weight);
                order.setWeightUnit(rs.getString("weight_unit"));
            }
            order.setOrderStatusName(rs.getString("description"));
            orderList.add(order);
        }
        return orderList;
    }

    @Override
    public List<VPurchase> getPurchaseVoucher(String vouNo, String compCode) throws Exception {
        List<VPurchase> list = new ArrayList<>();
        String sql = """
                select t.trader_name,t.phone,p.remark,p.vou_no,
                p.batch_no,p.vou_date,p.stock_name,p.pur_unit,p.qty,p.pur_price,p.pur_amt,
                p.vou_total,p.discount,p.paid,p.balance,
                p.weight,p.weight_unit,l.labour_name,p.land_vou_no,u1.unit_name weight_unit_name,
                u2.unit_name pur_unit_name,loc.loc_name,r.reg_name
                from v_purchase p join trader t
                on p.trader_code = t.code
                and p.comp_code = t.comp_code
                join location loc on p.loc_code = loc.loc_code
                and p.comp_code =loc.comp_code
                left join labour_group l on p.labour_group_code = l.code
                and p.comp_code = l.comp_code
                left join stock_unit u1 on p.weight_unit = u1.unit_code
                and p.comp_code = u1.comp_code
                join stock_unit u2 on p.pur_unit = u2.unit_code
                and p.comp_code = u2.comp_code
                left join region r on t.reg_code = r.reg_code
                and t.comp_code = r.comp_code
                where p.vou_no =?
                and p.comp_code =?""";
        ResultSet rs = reportDao.getResultSql(sql, vouNo, compCode);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = VPurchase.builder().build();
                p.setTraderName(rs.getString("trader_name"));
                p.setRemark(rs.getString("remark"));
                p.setVouNo(rs.getString("vou_no"));
                p.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                p.setStockName(rs.getString("stock_name"));
                p.setQty(rs.getDouble("qty"));
                p.setPurUnit(rs.getString("pur_unit"));
                p.setPurPrice(rs.getDouble("pur_price"));
                p.setPurAmount(rs.getDouble("pur_amt"));
                p.setVouTotal(rs.getDouble("vou_total"));
                p.setDiscount(rs.getDouble("discount"));
                p.setPaid(rs.getDouble("paid"));
                p.setBalance(rs.getDouble("balance"));
                p.setBatchNo(rs.getString("batch_no"));
                p.setWeight(rs.getDouble("weight"));
                p.setWeightUnit(rs.getString("weight_unit"));
                p.setLabourGroupName(rs.getString("labour_name"));
                p.setLandVouNo(rs.getString("land_vou_no"));
                p.setWeightUnitName(rs.getString("weight_unit_name"));
                p.setPurUnitName(rs.getString("pur_unit_name"));
                p.setLocationName(rs.getString("loc_name"));
                p.setPhoneNo(rs.getString("phone"));
                p.setRegionName(rs.getString("reg_name"));
                list.add(p);
            }
            if (!list.isEmpty()) {
                String landVouNo = list.getFirst().getLandVouNo();
                if (!Util1.isNullOrEmpty(landVouNo)) {
                    list.getFirst().setListPrice(landingHisPriceDao.getLandingPrice(landVouNo, compCode));
                }
            }
        }
        return list;
    }

    @Override
    public List<VPurchase> getGRNVoucher(String vouNo, String compCode) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = """
                select a.*, t.trader_name, t.address, l.loc_name, s.stock_name
                from (
                select p.stock_code, g.vou_no, g.vou_date, p.weight, p.weight_unit, p.qty, p.unit, g.loc_code, g.trader_code, g.comp_code, g.remark, g.batch_no
                from grn g, grn_detail p
                where g.vou_no = p.vou_no
                and g.comp_code = p.comp_code
                and g.vou_no =?
                and g.comp_code =? ) a
                join trader t
                on a.trader_code = t.code
                and a.comp_code = t.comp_code
                join location l
                on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                join stock s
                on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                """;
        ResultSet rs = getResult(sql, vouNo, compCode);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase purchase = VPurchase.builder().build();
                purchase.setVouNo(rs.getString("vou_no"));
                purchase.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                purchase.setLocationName(rs.getString("loc_name"));
                purchase.setTraderName(rs.getString("trader_name"));
                purchase.setCompAddress(rs.getString("address"));
                purchase.setRemark(rs.getString("remark"));
                purchase.setBatchNo(rs.getString("batch_no"));
                purchase.setStockCode(rs.getString("stock_code"));
                purchase.setStockName(rs.getString("stock_name"));
                purchase.setQty(rs.getDouble("qty"));
                purchase.setPurUnit(rs.getString("unit"));
                purchase.setWeight(rs.getDouble("weight"));
                purchase.setWeightUnit(rs.getString("weight_unit"));
                purchase.setTotal(rs.getDouble("weight") * rs.getDouble("qty"));
                purchaseList.add(purchase);
            }
        }
        return purchaseList;
    }


    @Override
    public List<VSale> getSaleBySaleManDetail(String fromDate, String toDate, String curCode, String smCode, String stockCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.saleman_code,sm.saleman_name,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" +
                "from v_sale v left join sale_man sm on v.saleman_code = sm.saleman_code\n" +
                "where (v.saleman_code = '" + smCode + "' or '-' = '" + smCode + "')\n" +
                "and v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by sm.saleman_name,v.vou_date,v.vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = VSale.builder().build();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setSaleManCode(rs.getString("saleman_code"));
                sale.setSaleManName(Util1.isNull(rs.getString("saleman_name"), "Other"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getDouble("qty"));
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getDouble("sale_price"));
                sale.setSaleAmount(rs.getDouble("sale_amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleByCustomerSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId) {
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.trader_name,rel.rel_name,t.address,rel.unit\n" +
                "from (\n" + "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,sale_unit,sum(sale_amt) ttl_amt,rel_code,trader_code,comp_code,dept_id\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = 0\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" + "group by stock_code,sale_unit,trader_code\n" + ")a\n" + "join v_relation rel \n" + "on a.rel_code = rel.rel_code\n" + "and a.sale_unit = rel.unit\n" + "and a.comp_code =rel.comp_code\n" +
                "join trader t\n" +
                "on a.trader_code = t.code\n" +
                "and a.comp_code =t.comp_code\n" +
                "order by t.user_code,t.trader_name";
        List<VSale> list = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                VSale s = VSale.builder().build();
                //stock_code, s_user_code, stock_name, ttl_qty, sale_unit, ttl_amt,
                // rel_code, trader_code, comp_code, dept_id, ttl_amt, smallest_qty, user_code, trader_name
                String userCode = rs.getString("s_user_code");
                String sCode = rs.getString("stock_code");
                String traderUsr = rs.getString("user_code");
                String tCode = rs.getString("trader_code");
                String relCode = rs.getString("rel_code");
                double smallQty = rs.getDouble("smallest_qty");
                s.setTraderCode(Util1.isNull(traderUsr, tCode));
                s.setStockCode(Util1.isNull(userCode, sCode));
                s.setStockName(rs.getString("stock_name"));
                s.setSaleAmount(rs.getDouble("ttl_amt"));
                s.setRelName(rs.getString("rel_name"));
                s.setQtyStr(getRelStr(relCode, compCode, smallQty));
                s.setTraderName(rs.getString("trader_name"));
                s.setAddress(rs.getString("address"));
                s.setTotalQty(smallQty);
                s.setSaleUnit(rs.getString("unit"));

                list.add(s);
            }
        } catch (Exception e) {
            log.error(String.format("getSaleSummaryByStock : %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByProjectSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId, String projectNo) {
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.trader_name,rel.rel_name\n" + "from (\n" + "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,sale_unit,sum(sale_amt) ttl_amt,rel_code,trader_code,comp_code,dept_id,project_no\n" + "from v_sale\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "and (project_no = '" + projectNo + "' or '-' = '" + projectNo + "')\n" +
                "and project_no is not null\n" + "group by stock_code,sale_unit,project_no\n" + ")a\n" +
                "join v_relation rel \n" + "on a.rel_code = rel.rel_code\n" + "and a.sale_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "join trader t\n" +
                "on a.trader_code = t.code\n" +
                "and a.comp_code =t.comp_code\n" +
                "order by a.project_no";
        List<VSale> list = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                VSale s = VSale.builder().build();
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
                s.setSaleAmount(rs.getDouble("ttl_amt"));
                s.setRelName(rs.getString("rel_name"));
                s.setQtyStr(getRelStr(relCode, compCode, smallQty));
                s.setTraderName(rs.getString("trader_name"));
                s.setProjectNo(rs.getString("project_no"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error(String.format("getSaleSummaryByProject : %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<VOrder> getOrderByProjectSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId, String projectNo) {
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.trader_name,rel.rel_name\n" + "from (\n" + "select stock_code,user_code as s_user_code,stock_name,sum(qty) ttl_qty,unit,sum(amt) ttl_amt,rel_code,trader_code,comp_code,dept_id,project_no\n" + "from v_order\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "and project_no is not null\n" +
                "group by stock_code,unit,project_no\n" + ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "join trader t\n" + "on a.trader_code = t.code\n" +
                "and a.comp_code =t.comp_code\n" +
                "order by a.project_no";
        List<VOrder> list = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                VOrder s = new VOrder();
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
                s.setQtyStr(getRelStr(relCode, compCode, smallQty));
                s.setTraderName(rs.getString("trader_name"));
                s.setProjectNo(rs.getString("project_no"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error(String.format("getOrderSummaryByProject : %s", e.getMessage()));
        }
        return list;
    }

    @Override
    public List<VSale> getSaleBySaleManSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String smCode, String compCode, Integer deptId) {
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.saleman_name,rel.rel_name,rel.unit\n" + "from (\n" + "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,sale_unit,sum(sale_amt) ttl_amt,rel_code,saleman_code,comp_code,dept_id\n" + "from v_sale\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (saleman_code = '" + smCode + "' or '-' = '" + smCode + "')\n" +
                "group by stock_code,sale_unit,saleman_code\n" + ")a\n" +
                "join v_relation rel \n" + "on a.rel_code = rel.rel_code\n" +
                "and a.sale_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "left join sale_man t\n" +
                "on a.saleman_code = t.saleman_code\n" +
                "and a.comp_code =t.comp_code\n" +
                "order by t.user_code,t.saleman_name";
        List<VSale> list = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                VSale s = VSale.builder().build();
                //stock_code, s_user_code, stock_name, ttl_qty, sale_unit, ttl_amt,
                // rel_code, saleman_code, comp_code, dept_id, ttl_amt, smallest_qty, user_code, trader_name
                String userCode = rs.getString("s_user_code");
                String sCode = rs.getString("stock_code");
                String smUsr = rs.getString("user_code");
                String tCode = rs.getString("saleman_code");
                String relCode = rs.getString("rel_code");
                double smallQty = rs.getDouble("smallest_qty");
                s.setSaleManCode(Util1.isNull(smUsr, tCode));
                s.setStockCode(Util1.isNull(userCode, sCode));
                s.setStockName(rs.getString("stock_name"));
                s.setSaleAmount(rs.getDouble("ttl_amt"));
                s.setRelName(rs.getString("rel_name"));
                s.setQtyStr(getRelStr(relCode, compCode, smallQty));
                s.setSaleManName(Util1.isNull(rs.getString("saleman_name"), "Other"));
                s.setTotalQty(smallQty);
                s.setSaleUnit(rs.getString("unit"));

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
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,t.address,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" +
                "from v_sale v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where (v.trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "and v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (v.cur_code = '" + curCode + "' or '-' = '" + curCode + "')\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by t.trader_name,v.vou_date,v.vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = VSale.builder().build();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setTraderCode(rs.getString("trader_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getDouble("qty"));
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getDouble("sale_price"));
                sale.setSaleAmount(rs.getDouble("sale_amt"));
                sale.setAddress(rs.getString("address"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseBySupplierSummary(String fromDate, String toDate, String typCode, String brandCode, String catCode, String stockCode, String traderCode, String compCode, Integer deptId) throws Exception {
        List<VPurchase> list = new ArrayList<>();
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.trader_name,rel.rel_name,rel.unit, t.address\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,pur_unit,sum(pur_amt) ttl_amt,rel_code,trader_code,comp_code,dept_id\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typCode + "' or '-' = '" + typCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "group by stock_code,pur_unit,trader_code\n" + ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.pur_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "join trader t\n" + "on a.trader_code = t.code\n" +
                "and a.comp_code =t.comp_code\n" +
                "order by t.user_code,t.trader_name";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase s = VPurchase.builder().build();
                String userCode = rs.getString("s_user_code");
                String sCode = rs.getString("stock_code");
                String traderUsr = rs.getString("user_code");
                String tCode = rs.getString("trader_code");
                String relCode = rs.getString("rel_code");
                double smallQty = rs.getDouble("smallest_qty");
                s.setTraderCode(Util1.isNull(traderUsr, tCode));
                s.setStockCode(Util1.isNull(userCode, sCode));
                s.setStockName(rs.getString("stock_name"));
                s.setPurAmount(rs.getDouble("ttl_amt"));
                s.setRelName(rs.getString("rel_name"));
                s.setQtyStr(getRelStr(relCode, compCode, smallQty));
                s.setTraderName(rs.getString("trader_name"));
                s.setTotalQty(smallQty);
                s.setPurUnit(rs.getString("unit"));
                s.setAddress(rs.getString("address"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VPurchase> getPurchaseByProjectSummary(String fromDate, String toDate, String typCode, String brandCode, String catCode, String stockCode, String traderCode, String compCode, Integer deptId, String projectNo) throws Exception {
        List<VPurchase> list = new ArrayList<>();
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.trader_name,rel.rel_name\n" + "from (\n" + "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,pur_unit,sum(pur_amt) ttl_amt,rel_code,trader_code,comp_code,dept_id,project_no\n" + "from v_purchase\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted = 0\n" +
                "and (stock_type_code = '" + typCode + "' or '-' = '" + typCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (trader_code = '" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "group by stock_code,pur_unit,project_no\n" + ")a\n" + "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" +
                "and a.pur_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "join trader t\n" + "on a.trader_code = t.code\n" +
                "and a.comp_code =t.comp_code\n" +
                "order by t.user_code,t.trader_name";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase s = VPurchase.builder().build();
                String userCode = rs.getString("s_user_code");
                String sCode = rs.getString("stock_code");
                String traderUsr = rs.getString("user_code");
                String tCode = rs.getString("trader_code");
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                s.setTraderCode(Util1.isNull(traderUsr, tCode));
                s.setStockCode(Util1.isNull(userCode, sCode));
                s.setStockName(rs.getString("stock_name"));
                s.setPurAmount(rs.getDouble("ttl_amt"));
                s.setRelName(rs.getString("rel_name"));
                s.setQtyStr(getRelStr(relCode, compCode, smallQty));
                s.setTraderName(rs.getString("trader_name"));
                s.setProjectNo(rs.getString("project_no"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VPurchase> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,t.address,\n" +
                "v.stock_name,v.qty,v.pur_unit,v.pur_price,v.pur_amt\n" +
                "from v_purchase v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where (v.trader_code ='" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" +
                "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (v.cur_code = '" + curCode + "' or '-' ='" + curCode + "')\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by t.trader_name,v.vou_no;";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = VPurchase.builder().build();
                p.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                p.setVouNo(rs.getString("vou_no"));
                p.setTraderCode(rs.getString("trader_code"));
                p.setTraderName(rs.getString("trader_name"));
                p.setStockName(rs.getString("stock_name"));
                p.setQty(rs.getDouble("qty"));
                p.setPurUnit(rs.getString("pur_unit"));
                p.setPurPrice(rs.getDouble("pur_price"));
                p.setPurAmount(rs.getDouble("pur_amt"));
                p.setAddress(rs.getString("address"));
                purchaseList.add(p);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VPurchase> getPurchaseByProjectDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId, String projectNo) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,\n" +
                "v.stock_name,v.qty,v.pur_unit,v.pur_price,v.pur_amt,v.project_no\n" +
                "from v_purchase v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where (v.trader_code ='" + traderCode + "' or '-' = '" + traderCode + "')\n" +
                "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" +
                "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (v.cur_code = '" + curCode + "' or '-' ='" + curCode + "')\n" +
                "and (v.project_no = '" + projectNo + "' or '-' ='" + projectNo + "')\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and v.project_no is not null\n order by t.trader_name,v.vou_no;";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = VPurchase.builder().build();
                p.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                p.setVouNo(rs.getString("vou_no"));
                p.setTraderCode(rs.getString("trader_code"));
                p.setTraderName(rs.getString("trader_name"));
                p.setStockName(rs.getString("stock_name"));
                p.setQty(rs.getDouble("qty"));
                p.setPurUnit(rs.getString("pur_unit"));
                p.setPurPrice(rs.getDouble("pur_price"));
                p.setPurAmount(rs.getDouble("pur_amt"));
                p.setProjectNo(rs.getString("project_no"));
                purchaseList.add(p);
            }
        }
        return purchaseList;
    }

    @Override
    public Mono<ReturnObject> getSaleByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode,
                                                    String catCode, String locCode, String compCode, Integer deptId, Integer macId) {
        String sql = """
                select stock_code,s_user_code,stock_name,sum(qty) qty,sum(bag) bag,sum(sale_amt) amount,comp_code,dept_id
                from v_sale
                where deleted = false
                and date(vou_date) between :fromDate and :toDate
                and comp_code = :compCode
                and cur_code = :curCode
                and (dept_id =:deptId or 0 =:deptId)
                and (loc_code = :locCode or '-' = :locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (cat_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code
                order by s_user_code;
                """;
        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("locCode", locCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .map((row) -> VSale.builder()
                        .stockCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .saleAmount(row.get("amount", Double.class))
                        .qty(Util1.toNull(row.get("qty", Double.class)))
                        .bag(Util1.toNull(row.get("bag", Double.class)))
                        .build()).all()
                .collectList()
                .map(this::calPercent)
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private List<VSale> calPercent(List<VSale> list) {
        double totalQty = list.stream()
                .filter(v -> Objects.nonNull(v.getQty())) // Filter out null values
                .mapToDouble(VSale::getQty) // Map to double
                .sum(); // Perform the sum operation
        if (!list.isEmpty()) {
            list.forEach(t -> t.setQtyPercent((t.getQty() / totalQty) * 100));
        }
        return list;
    }

    @Override
    public List<VOrder> getOrderByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception {
        List<VOrder> saleList = new ArrayList<>();
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty,rel.rel_name\n" + "from (\n" + "select stock_code,user_code,stock_name,sum(qty) ttl_qty,unit,sum(amt) ttl_amt,rel_code,comp_code,dept_id\n" + "from v_order\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and comp_code = '" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted = 0\n" + "and (loc_code = '" + locCode + "' or '-' = '" + locCode + "')\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" + ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" + "and a.unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "order by user_code";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOrder sale = new VOrder();
                String relCode = rs.getString("rel_code");
                float smallQty = rs.getFloat("smallest_qty");
                sale.setStockCode(rs.getString("user_code"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setRelName(rs.getString("rel_name"));
                sale.setSaleAmount(rs.getFloat("ttl_amt"));
                sale.setQtyStr(getRelStr(relCode, compCode, smallQty));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleByVoucherDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception {
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
        if (!locCode.equals("-")) {
            filter += "and v.loc_code='" + locCode + "'\n";
        }
        List<VSale> list = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.vou_total,v.paid,v.remark,v.reference,v.batch_no,sup.trader_name sup_name,\n" +
                "t.user_code,t.trader_name,t.address,v.s_user_code,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" +
                "from v_sale v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "left join grn g\n" +
                "on v.batch_no = g.batch_no\n" +
                "and v.comp_code = g.comp_code\n" +
                "left join trader sup\n" +
                "on g.trader_code = sup.code\n" +
                "and g.comp_code = sup.comp_code\n" +
                "where v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + filter +
                "order by v.vou_date,v.vou_no,v.unique_id";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                //vou_date, vou_no, remark, reference, batch_no, sup_name, trader_code,
                // trader_name, s_user_code, stock_name, qty, sale_unit, sale_price, sale_amt
                VSale s = VSale.builder().build();
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
                s.setQty(rs.getDouble("qty"));
                s.setSaleUnit(rs.getString("sale_unit"));
                s.setSalePrice(rs.getDouble("sale_price"));
                s.setSaleAmount(rs.getDouble("sale_amt"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setPaid(rs.getDouble("paid"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByVoucherSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception {
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
        if (!locCode.equals("-")) {
            filter += "and loc_code='" + locCode + "'\n";
        }
        List<VSale> list = new ArrayList<>();
        String sql = "select a.*,t.trader_name\n" +
                "from (\n" +
                "select vou_no,vou_date,trader_code,vou_total,comp_code\n" +
                "from sale_his\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and cur_code ='" + curCode + "'\n" + filter + "\n" + ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "order by vou_date,vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                //vou_date, vou_no, remark, reference, batch_no, sup_name, trader_code,
                // trader_name, s_user_code, stock_name, qty, sale_unit, sale_price, sale_amt
                VSale s = VSale.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setVouTotal(rs.getDouble("vou_total"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByBatchDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) throws Exception {
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
                "and v.comp_code = t.comp_code\n" +
                "left join grn g\n" + "on v.batch_no = g.batch_no\n" +
                "and v.comp_code = g.comp_code\n" +
                "left join trader sup\n" +
                "on g.trader_code = sup.code\n" +
                "and g.comp_code = sup.comp_code\n" +
                "where v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and v.batch_no is not null\n" + filter +
                "order by v.vou_date,v.batch_no,v.unique_id";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                //vou_date, vou_no, remark, reference, batch_no, sup_name, trader_code,
                // trader_name, s_user_code, stock_name, qty, sale_unit, sale_price, sale_amt
                VSale s = VSale.builder().build();
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
                s.setQty(rs.getDouble("qty"));
                s.setSaleUnit(rs.getString("sale_unit"));
                s.setSalePrice(rs.getDouble("sale_price"));
                s.setSaleAmount(rs.getDouble("sale_amt"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setPaid(rs.getDouble("paid"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByProjectDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId, String projectNo) throws Exception {
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
        if (!projectNo.equals("-")) {
            filter += "and v.project_no='" + projectNo + "'\n";
        }
        List<VSale> list = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.vou_total,v.paid,v.remark,v.reference,v.batch_no,sup.trader_name sup_name,\n" +
                "t.user_code,t.trader_name,t.address,v.s_user_code,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt,v.project_no\n" +
                "from v_sale v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "left join grn g\n" + "on v.batch_no = g.batch_no\n" +
                "and v.comp_code = g.comp_code\n" +
                "left join trader sup\n" +
                "on g.trader_code = sup.code\n" +
                "and g.comp_code = sup.comp_code\n" +
                "where v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and v.project_no is not null\n" + filter +
                "order by v.vou_date,v.project_no,v.unique_id";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                //vou_date, vou_no, remark, reference, batch_no, sup_name, trader_code,
                // trader_name, s_user_code, stock_name, qty, sale_unit, sale_price, sale_amt
                VSale s = VSale.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
                s.setBatchNo(rs.getString("batch_no"));
                s.setProjectNo(rs.getString("project_no"));
                s.setSupplierName(rs.getString("sup_name"));
                s.setTraderCode((rs.getString("user_code")));
                s.setTraderName(rs.getString("trader_name"));
                s.setCusAddress(rs.getString("address"));
                s.setStockUserCode(rs.getString("s_user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setQty(rs.getDouble("qty"));
                s.setSaleUnit(rs.getString("sale_unit"));
                s.setSalePrice(rs.getDouble("sale_price"));
                s.setSaleAmount(rs.getDouble("sale_amt"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setPaid(rs.getDouble("paid"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VOrder> getOrderByProjectDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId, String projectNo) throws Exception {
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
        if (!projectNo.equals("-")) {
            filter += "and v.project_no='" + projectNo + "'\n";
        }
        List<VOrder> list = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.vou_total,v.paid,v.remark,v.reference,sup.trader_name sup_name,\n" +
                "t.user_code,t.trader_name,t.address,v.user_code,v.stock_name,v.qty,v.unit,v.price,v.amt,v.project_no\n" +
                "from v_order v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "left join grn g\n" +
                "on v.comp_code = g.comp_code\n" +
                "left join trader sup\n" +
                "on g.trader_code = sup.code\n" +
                "and g.comp_code = sup.comp_code\n" +
                "where v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and v.project_no is not null\n" + filter +
                "order by v.vou_date,v.project_no,v.unique_id";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                //vou_date, vou_no, remark, reference, batch_no, sup_name, trader_code,
                // trader_name, s_user_code, stock_name, qty, sale_unit, sale_price, sale_amt
                VOrder s = new VOrder();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
//                s.setBatchNo(rs.getString("batch_no"));
                s.setProjectNo(rs.getString("project_no"));
                s.setSupplierName(rs.getString("sup_name"));
                s.setTraderCode((rs.getString("user_code")));
                s.setTraderName(rs.getString("trader_name"));
                s.setCusAddress(rs.getString("address"));
                s.setStockUserCode(rs.getString("user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setQty(rs.getFloat("qty"));
                s.setSaleUnit(rs.getString("unit"));
                s.setSalePrice(rs.getFloat("price"));
                s.setSaleAmount(rs.getFloat("amt"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setPaid(rs.getDouble("paid"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByStockDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,v.s_user_code,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" +
                "from v_sale v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where (v.stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (loc_code = '" + locCode + "' or '-' = '" + locCode + "')\n" +
                "and v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by v.s_user_code,v.vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = VSale.builder().build();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setTraderCode(rs.getString("trader_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setStockUserCode(rs.getString("s_user_code"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getDouble("qty"));
                sale.setSaleUnit(rs.getString("sale_unit"));
                sale.setSalePrice(rs.getDouble("sale_price"));
                sale.setSaleAmount(rs.getDouble("sale_amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VOrder> getOrderByStockDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) throws Exception {
        List<VOrder> saleList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,v.user_code,v.stock_name,v.qty,v.unit,v.price,v.amt\n" +
                "from v_order v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where (v.stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (loc_code = '" + locCode + "' or '-' = '" + locCode + "')\n" +
                "and v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by v.user_code,v.vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOrder sale = new VOrder();
                sale.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                sale.setVouNo(rs.getString("vou_no"));
                sale.setTraderCode(rs.getString("trader_code"));
                sale.setTraderName(rs.getString("trader_name"));
                sale.setStockUserCode(rs.getString("user_code"));
                sale.setStockName(rs.getString("stock_name"));
                sale.setQty(rs.getFloat("qty"));
                sale.setSaleUnit(rs.getString("unit"));
                sale.setSalePrice(rs.getFloat("price"));
                sale.setSaleAmount(rs.getFloat("amt"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VPurchase> getPurchaseByStockDetail(String fromDate, String toDate, String curCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId, String locCode) throws Exception {
        List<VPurchase> purchaseList = new ArrayList<>();
        String sql = "select v.vou_date,v.vou_no,v.trader_code,t.trader_name,\n" +
                "v.s_user_code,v.stock_name,v.qty,v.pur_unit,v.pur_price,v.pur_amt\n" +
                "from v_purchase v join trader t\n" +
                "on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where (v.stock_code = '" + stockCode + "' or '-'='" + stockCode + "')\n" +
                "and (v.stock_type_code = '" + typeCode + "' or '-'='" + typeCode + "')\n" +
                "and (v.brand_code = '" + brandCode + "' or '-'='" + brandCode + "')\n" +
                "and v.loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (v.category_code = '" + catCode + "' or '-'='" + catCode + "')\n" +
                "and v.deleted = false\n" + "and v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "order by v.s_user_code,v.vou_date,v.vou_no;";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = VPurchase.builder().build();
                p.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                p.setVouNo(rs.getString("vou_no"));
                p.setTraderCode(rs.getString("trader_code"));
                p.setTraderName(rs.getString("trader_name"));
                p.setStockUserCode(rs.getString("s_user_code"));
                p.setStockName(rs.getString("stock_name"));
                p.setQty(rs.getDouble("qty"));
                p.setPurUnit(rs.getString("pur_unit"));
                p.setPurPrice(rs.getDouble("pur_price"));
                p.setPurAmount(rs.getDouble("pur_amt"));
                purchaseList.add(p);
            }
        }
        return purchaseList;
    }

    @Override
    public List<VPurchase> getPurchaseByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception {
        List<VPurchase> list = new ArrayList<>();
        String sql = "select a.*,a.ttl_qty*rel.smallest_qty smallest_qty,rel.rel_name, rel.unit\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,pur_unit,sum(pur_amt) ttl_amt,rel_code,comp_code,dept_id\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = false\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "group by stock_code,pur_unit\n" + ")a\n" +
                "join v_relation rel \n" +
                "on a.rel_code = rel.rel_code\n" + "and a.pur_unit = rel.unit\n" +
                "and a.comp_code =rel.comp_code\n" +
                "order by s_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = VPurchase.builder().build();
                String relCode = rs.getString("rel_code");
                double smallQty = rs.getDouble("smallest_qty");
                p.setStockCode(rs.getString("s_user_code"));
                p.setStockName(rs.getString("stock_name"));
                p.setRelName(rs.getString("rel_name"));
                p.setPurAmount(rs.getDouble("ttl_amt"));
                p.setQtyStr(getRelStr(relCode, compCode, smallQty));
                p.setTotalQty(smallQty);
                p.setPurUnit(rs.getString("unit"));
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public List<VPurchase> getPurchaseByStockWeightSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception {
        List<VPurchase> list = new ArrayList<>();
        String sql = "select a.*,u1.unit_name,u2.unit_name weight_unit_name\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) qty,sum(ifnull(total_weight,0)) total_weight,\n" +
                "sum(pur_amt) pur_amt,pur_unit,weight_unit,comp_code\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = false\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,weight_unit,pur_unit\n" +
                ")a\n" +
                "join stock_unit u1 on a.pur_unit = u1.unit_code\n" +
                "and a.comp_code = u1.comp_code\n" +
                "join stock_unit u2 on a.weight_unit = u2.unit_code\n" +
                "and a.comp_code = u2.comp_code\n" +
                "order by s_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase p = VPurchase.builder().build();
                //s_user_code, stock_name, qty, total_weight, pur_unit, weight_unit, comp_code, unit_name, weight_unit_name
                p.setStockCode(rs.getString("stock_code"));
                p.setStockUserCode(rs.getString("s_user_code"));
                p.setStockName(rs.getString("stock_name"));
                p.setPurAmount(rs.getDouble("pur_amt"));
                p.setTotalQty(rs.getDouble("qty"));
                p.setTotalWeight(rs.getDouble("total_weight"));
                p.setPurUnitName(rs.getString("unit_name"));
                p.setWeightUnitName(rs.getString("weight_unit_name"));
                list.add(p);
            }
        }
        return list;
    }


    @Override
    public General getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode) {
        General general = General.builder().build();
        general.setAmount(0.0);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" +
                "from (\n" +
                "select pur_unit,pur_price/rel.smallest_qty smallest_price,pd.rel_code,pd.comp_code,pd.dept_id\n" +
                "from v_purchase pd\n" +
                "join v_relation rel on pd.rel_code = rel.rel_code\n" +
                "and pd.pur_unit =  rel.unit\n" +
                "and pd.comp_code = rel.comp_code\n" +
                "where pd.stock_code = '" + stockCode + "' and vou_no = (\n" +
                "select ph.vou_no\n" +
                "from pur_his ph, pur_his_detail pd\n" +
                "where date(ph.vou_date)<= '" + purDate + "' \n" +
                "and deleted = 0\n" +
                "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "group by ph.vou_no\n" +
                "order by ph.vou_date desc\n" +
                "limit 1\n" + "))a\n" +
                "join v_relation rel\n" +
                "on a.rel_code =rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and rel.unit = '" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getDouble("price"));
            }
        } catch (Exception e) {
            log.error(String.format("getPurchaseRecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public General getWeightLossRecentPrice(String stockCode, String vouDate, String unit, String compCode) {
        General g = General.builder().build();
        g.setAmount(0.0);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" + "from (\n" + "select v.loss_price/rel.smallest_qty smallest_price,v.rel_code,v.comp_code,v.dept_id\n" + "from v_weight_loss v\n" + "join v_relation rel\n" + "on v.rel_code = rel.rel_code\n" + "and v.loss_unit = rel.unit\n" + "and v.comp_code = rel.comp_code\n" +
                "and v.stock_code ='" + stockCode + "'\n" + "where vou_no =(\n" + "select ph.vou_no\n" + "from weight_loss_his ph, weight_loss_his_detail pd\n" + "where date(ph.vou_date)<= '" + vouDate + "' \n" + "and deleted = 0\n" + "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "group by ph.vou_no\n" +
                "order by ph.vou_date desc\n" + "limit 1)\n" + ")a\n" +
                "join v_relation rel\n" + "on a.rel_code = rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and rel.unit ='" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                g.setAmount(rs.getDouble("price"));
            }
        } catch (Exception e) {
            log.error(String.format("getWeightLossRecentPrice: %s", e.getMessage()));
        }
        return g;
    }

    @Override
    public General getProductionRecentPrice(String stockCode, String purDate, String unit, String compCode) {
        General general = General.builder().build();
        general.setAmount(0.0);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" + "from (\n" +
                "select pd.unit,price/rel.smallest_qty smallest_price,pd.rel_code,pd.comp_code,pd.dept_id\n" +
                "from v_process_his pd\n" +
                "join v_relation rel on pd.rel_code = rel.rel_code\n" +
                "and pd.unit = rel.unit\n" +
                "and pd.comp_code = rel.comp_code\n" +
                "where pd.stock_code = '" + stockCode + "' \n" +
                "and pd.comp_code ='" + compCode + "'\n" +
                "and vou_no = (\n" +
                "select ph.vou_no\n" +
                "from process_his ph\n" +
                "where date(ph.vou_date)<= '" + purDate + "' \n" +
                "and deleted = false\n" +
                "and ph.comp_code = '" + compCode + "' \n" +
                "and ph.stock_code = '" + stockCode + "'\n" +
                "group by ph.vou_no\n" +
                "order by ph.vou_date desc\n" +
                "limit 1\n" + "))a\n" +
                "join v_relation rel\n" +
                "on a.rel_code =rel.rel_code\n" +
                "and a.comp_code =rel.comp_code\n" +
                "and rel.unit = '" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getDouble("price"));
            }
        } catch (Exception e) {
            log.error(String.format("getPurchaseRecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public General getPurchaseAvgPrice(String stockCode, String purDate, String unit, String compCode) {
        General g = General.builder().build();
        String sql = "select stock_code,round(avg(avg_price)*rel.smallest_qty,2) price\n" +
                "from (\n" +
                "select 'PUR-AVG',pur.stock_code,avg(pur.pur_price/rel.smallest_qty) avg_price,pur.rel_code,pur.comp_code,pur.dept_id\n" +
                "from v_purchase pur\n" +
                "join v_relation rel\n" +
                "on pur.rel_code = rel.rel_code\n" +
                "and pur.pur_unit = rel.unit\n" +
                "and pur.comp_code = rel.comp_code\n" +
                "where deleted = false \n" +
                "and pur.comp_code ='" + compCode + "'\n" +
                "and pur.stock_code ='" + stockCode + "'\n" +
                "and date(pur.vou_date) <= '" + purDate + "'\n" +
                "group by pur.stock_code\n" + "\tunion all\n" +
                "select 'OP',op.stock_code,avg(op.price/rel.smallest_qty) avg_price,op.rel_code,op.comp_code,op.dept_id\n" +
                "from v_opening op\n" +
                "join v_relation rel\n" +
                "on op.rel_code = rel.rel_code\n" +
                "and op.unit = rel.unit\n" +
                "and op.comp_code = rel.comp_code\n" +
                "where op.price > 0\n" + "and op.deleted = false \n" +
                "and op.comp_code ='" + compCode + "'\n" +
                "and date(op.op_date) = '" + purDate + "'\n" +
                "and op.stock_code ='" + stockCode + "'\n" + "group by op.stock_code)a\n" +
                "join v_relation rel on\n" +
                "a.rel_code = rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and rel.unit ='" + unit + "'\n" +
                "group by stock_code";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    g.setAmount(rs.getDouble("price"));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return g;
    }

    @Override
    public General getSaleRecentPrice(String stockCode, String saleDate, String unit, String compCode) {
        General general = General.builder().build();
        general.setAmount(0.0);
        String sql = "select rel.smallest_qty * smallest_price price,rel.unit\n" +
                "from (select sale_unit,sale_price/rel.smallest_qty smallest_price,pd.rel_code,pd.comp_code,pd.dept_id\n" +
                "from v_sale pd\n" +
                "join v_relation rel on pd.rel_code = rel.rel_code\n" +
                "and pd.sale_unit = rel.unit\n" +
                "and pd.comp_code = rel.comp_code\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "where vou_no = (\n" +
                "select ph.vou_no\n" +
                "from sale_his ph, sale_his_detail pd\n" +
                "where date(ph.vou_date)<= '" + saleDate + "' and deleted = 0\n" +
                "and ph.comp_code = '" + compCode + "' and ph.vou_no = pd.vou_no\n" +
                "and pd.stock_code = '" + stockCode + "'\n" +
                "order by ph.vou_date desc limit 1" + "))a\n" +
                "join v_relation rel\n" +
                "on a.rel_code =rel.rel_code\n" +
                "and a.comp_code = rel.comp_code\n" +
                "and rel.unit = '" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getDouble("price"));
            }
        } catch (Exception e) {
            log.error(String.format("getPurchaseRecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public General getStockIORecentPrice(String stockCode, String vouDate, String unit) {
        General general = General.builder().build();
        general.setAmount(0.0);
        String sql = "select cost_price,stock_code,max(unique_id) \n" +
                "from stock_in_out_detail\n" +
                "where stock_code = '" + stockCode + "'and (in_unit = '" + unit + "' or out_unit = '" + unit + "')\n" +
                "and vou_no = (select sio.vou_no \n" +
                "from stock_in_out sio , stock_in_out_detail siod\n" +
                "where date(vou_date) <= '" + vouDate + "' and deleted = false\n" +
                "and sio.vou_no = siod.vou_no\n" +
                "and cost_price <> 0\n" +
                "and siod.stock_code = '" + stockCode + "' and (in_unit ='" + unit + "' or out_unit = '" + unit + "')\n" +
                "order by sio.vou_date desc limit 1)\n";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                general.setAmount(rs.getDouble("cost_price"));
            }
        } catch (Exception e) {
            log.error(String.format("getStockIORecentPrice: %s", e.getMessage()));
        }
        return general;
    }

    @Override
    public Mono<General> getWeightAvgPrice(String stockCode, String locCode, String compCode) {
        String sql = """
                select stock_code,comp_code,sum(amount)/sum(qty) price
                from  (
                select stock_code,comp_code,sum(qty) qty,sum(amount) amount
                from v_opening
                where deleted =false
                and date(op_date)=:opDate
                and comp_code =:compCode
                and stock_code =:stockCode
                group by stock_code,comp_code
                	union all
                select stock_code,comp_code,sum(qty) qty,sum(pur_amt) amount
                from v_purchase
                where deleted =false
                and comp_code =:compCode
                and stock_code =:stockCode
                group by stock_code,comp_code
                )a
                group by stock_code,comp_code
                """;
        return opHisService.getOpeningDateByLocation(compCode, locCode)
                .flatMap(opDate -> client.sql(sql)
                        .bind("opDate", opDate)
                        .bind("compCode", compCode)
                        .bind("stockCode", stockCode)
                        .map((row) -> General.builder()
                                .amount(Util1.getDouble(row.get("price", Double.class)))
                                .build())
                        .one());
    }

    private void calStockBalanceByLocation(String opDate, String clDate, String typeCode, String cateCode,
                                           String brandCode, String stockCode,
                                           boolean calSale, boolean calPur, boolean calRI,
                                           boolean calRO, String locCode, String compCode,
                                           Integer deptId, Integer macId) {
        String delSql = "delete from tmp_stock_balance where mac_id = " + macId;
        String sql = "insert into tmp_stock_balance(stock_code, qty, unit, loc_code,smallest_qty, mac_id,comp_code,dept_id)\n" +
                "select stock_code,qty,unit,loc_code,sum(smallest_qty) smallest_qty," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from (\n" +
                "\tselect a.stock_code,sum(a.qty) qty,a.unit,a.loc_code,sum(a.qty)*rel.smallest_qty smallest_qty,a.comp_code,a.dept_id\n" +
                "\tfrom(\n" + "\t\tselect stock_code,sum(qty) as qty,unit,loc_code,comp_code,dept_id\n" +
                "\t\tfrom v_opening\n" +
                "\t\twhere deleted = false\n" +
                "\t\tand tran_source =1\n" +
                "\t\tand date(op_date) ='" + opDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand calculate =true\n" +
                "\t\tgroup by stock_code, unit , loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect stock_code,sum(qty) * - 1 as qty,sale_unit,loc_code,comp_code,dept_id\n" +
                "\t\tfrom v_sale \n" +
                "\t\twhere deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (cat_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand (calculate =1 and 0 = " + calSale + ")\n" +
                "\t\tgroup by stock_code ,sale_unit ,loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect stock_code,sum(qty) as qty,pur_unit,loc_code,comp_code,dept_id\n" +
                "\t\tfrom\n" +
                "\t\tv_purchase \n" +
                "\t\twhere deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (calculate =1 and 0 = " + calPur + ")\n" +
                "\t\tgroup by stock_code , pur_unit , loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect stock_code,sum(qty) as qty,unit,loc_code,comp_code,dept_id\n" +
                "\t\tfrom v_return_in\n" +
                "\t\twhere deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand (calculate =1 and 0 = " + calRI + ")\n" +
                "\t\tgroup by stock_code,unit ,loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect stock_code,sum(qty) * - 1 as qty,unit,loc_code,comp_code,dept_id\n" + "\t\tfrom\n" +
                "\t\tv_return_out\n" +
                "\t\twhere deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand (calculate =1 and 0 = " + calRO + ")\n" +
                "\t\tgroup by stock_code  , unit , loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect stock_code,sum(in_qty),in_unit,loc_code,comp_code,dept_id\n" +
                "\t\tfrom\n" + "\t\tv_stock_io\n" + "\t\twhere in_qty is not null\n" +
                "\t\tand in_unit is not null\n" +
                "\t\tand deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand calculate =1\n" + "\t\tgroup by stock_code ,in_unit ,loc_code \n" +
                "\t\t\tunion all \n" +
                "\t\tselect stock_code,sum(out_qty) * - 1,out_unit,loc_code,comp_code,dept_id\n" +
                "\t\tfrom\n" + "\t\tv_stock_io\n" + "\t\twhere out_qty is not null\n" +
                "\t\tand out_unit is not null\n" +
                "\t\tand deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand calculate =1\n" + "\t\tgroup by stock_code , out_unit , loc_code\n" +
                "\t\t\tunion all\n" +
                "\t\tselect stock_code,sum(qty) * - 1,unit,loc_code_from,comp_code,dept_id\n" +
                "\t\tfrom v_transfer \n" +
                "\t\twhere deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code_from = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand calculate =1\n" +
                "\t\tgroup by stock_code, unit , loc_code_from\n" +
                "\t\t\tunion all\n" +
                "\t\tselect stock_code,sum(qty),unit,loc_code_to,comp_code,dept_id\n" +
                "\t\tfrom v_transfer \n" +
                "\t\twhere deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code_to = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand calculate =1\n" + "\t\tgroup by stock_code , unit , loc_code_to\n" +
                "\t\t\tunion all\n" +
                "\t\tselect stock_code,sum(qty),unit,loc_code,comp_code,dept_id\n" +
                "\t\tfrom v_process_his\n" +
                "\t\twhere  deleted = 0\n" +
                "\t\tand finished =1\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand calculate =1\n" +
                "\t\tgroup by stock_code , unit , loc_code\n" +
                "\t\t\tunion all\n" +
                "\t\tselect stock_code,sum(qty)*-1,unit,loc_code,comp_code,dept_id\n" +
                "\t\tfrom v_process_his_detail\n" +
                "\t\twhere  deleted = 0\n" +
                "\t\tand date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "\t\tand comp_code = '" + compCode + "'\n" +
                "\t\tand (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "\t\tand (stock_type_code = '" + typeCode + "' or '-' ='" + typeCode + "')\n" +
                "\t\tand (category_code = '" + cateCode + "' or '-' ='" + cateCode + "')\n" +
                "\t\tand (brand_code = '" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "\t\tand (loc_code = '" + locCode + "' or '-' ='" + locCode + "')\n" +
                "\t\tand calculate =1\n" + "\t\tgroup by stock_code , unit , loc_code" + ") a\n" +
                "join stock s\n" + "on a.stock_code = s.stock_code\n" +
                "and a.comp_code = s.comp_code\n" +
                "join v_relation rel on s.rel_code = rel.rel_code \n" +
                "and a.unit = rel.unit\n" +
                "and a.comp_code = rel.comp_code\n" +
                "group by a.stock_code,a.unit,a.loc_code) b\n" +
                "group by b.stock_code,b.loc_code";
        try {

            reportDao.executeSql(delSql, sql);
        } catch (Exception e) {
            log.error(String.format("calStockBalance: %s", e.getMessage()));
        }
    }

    @Override
    public List<VStockBalance> getStockBalance(String opDate, String clDate, String typeCode, String catCode, String brandCode, String stockCode,
                                               boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                               String locCode, String compCode, Integer deptId, Integer macId, boolean summary) {
        calStockBalanceByLocation(opDate, clDate, typeCode, catCode, brandCode, stockCode, calSale, calPur, calRI, calRO, locCode, compCode, deptId, macId);
        List<VStockBalance> balances = new ArrayList<>();
        if (summary) {
            String sql = "select a.stock_code,a.qty,s.user_code,s.rel_code,s.stock_name\n" +
                    "from (\n" +
                    "select stock_code,sum(smallest_qty)qty,comp_code,dept_id\n" +
                    "from tmp_stock_balance\n" +
                    "where mac_id =" + macId + "\n" +
                    "group by stock_code)a\n" +
                    "join stock s on a.stock_code = s.stock_code\n" +
                    "and a.comp_code = s.comp_code";
            ResultSet rs = reportDao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                try {
                    while (rs.next()) {
                        VStockBalance b = VStockBalance.builder()
                                .userCode(rs.getString("user_code"))
                                .stockCode(rs.getString("stock_code"))
                                .stockName(rs.getString("stock_name"))
                                .unitName(getRelStr(rs.getString("rel_code"), compCode, rs.getFloat("qty")))
                                .locationName("All")
                                .build();
                        balances.add(b);
                    }
                } catch (Exception e) {
                    log.error("getStockBalance : " + e.getMessage());
                }
            }
        } else {
            String sql = "select tmp.stock_code,tmp.loc_code,l.loc_name,tmp.unit,tmp.qty,tmp.smallest_qty,s.user_code,s.rel_code,s.stock_name\n" +
                    "from tmp_stock_balance tmp join location l\n" +
                    "on tmp.loc_code = l.loc_code\n" +
                    "and tmp.comp_code = l.comp_code\n" +
                    "join stock s on tmp.stock_code = s.stock_code\n" +
                    "and tmp.comp_code = s.comp_code\n" +
                    "where tmp.mac_id = " + macId;
            ResultSet rs = reportDao.executeSql(sql);
            if (!Objects.isNull(rs)) {
                try {
                    while (rs.next()) {
                        VStockBalance b = VStockBalance.builder()
                                .userCode(rs.getString("user_code"))
                                .stockCode(rs.getString("stock_code"))
                                .stockName(rs.getString("stock_name"))
                                .locationName(rs.getString("loc_name"))
                                .locCode(rs.getString("loc_code"))
                                .unitName(getRelStr(rs.getString("rel_code"), compCode, rs.getFloat("smallest_qty")))
                                .build();
                        balances.add(b);

                    }
                } catch (Exception e) {
                    log.error("getStockBalance : " + e.getMessage());
                }
            }
        }
        return balances;
    }

    @Override
    public List<VStockBalance> getStockBalanceByWeight(String opDate, String clDate, String stockCode,
                                                       boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                                       boolean calMill,
                                                       String compCode, Integer macId, boolean summary) {
        calculateStockBalanceByWeight(opDate, clDate, stockCode, compCode, macId, calSale, calPur, calRI, calRO, calMill);
        List<VStockBalance> list = new ArrayList<>();
        if (summary) {
            String sql = """
                    select a.*,s.stock_name,s.user_code
                    from (
                    select stock_code, comp_code,sum(qty) qty, sum(weight) weight
                    from tmp_stock_balance
                    where mac_id=?
                    group by stock_code
                    )a
                    join stock s on a.stock_code = s.stock_code
                    and a.comp_code = s.comp_code
                    """;
            ResultSet rs = reportDao.getResultSql(sql, macId);
            try {
                while (rs.next()) {
                    VStockBalance b = VStockBalance.builder()
                            .userCode(rs.getString("user_code"))
                            .stockName(rs.getString("stock_name"))
                            .totalQty(rs.getDouble("qty"))
                            .weight(rs.getDouble("weight"))
                            .locationName("All")
                            .build();
                    list.add(b);

                }
            } catch (Exception e) {
                log.error("getStockBalanceByWeight : " + e.getMessage());
            }
        } else {
            String sql = """
                    select t.*,s.user_code,s.stock_name,l.loc_name
                    from tmp_stock_balance t join stock s
                    on t.stock_code = s.stock_code
                    and t.comp_code = s.comp_code
                    join location l on t.loc_code= l.loc_code
                    and t.comp_code = l.comp_code
                    where t.mac_id =?""";
            ResultSet rs = reportDao.getResultSql(sql, macId);
            try {
                while (rs.next()) {
                    VStockBalance b = VStockBalance.builder()
                            .userCode(rs.getString("user_code"))
                            .stockName(rs.getString("stock_name"))
                            .totalQty(rs.getDouble("qty"))
                            .weight(rs.getDouble("weight"))
                            .locationName(rs.getString("loc_name"))
                            .build();

                    list.add(b);

                }
            } catch (Exception e) {
                log.error("getStockBalanceByWeight : " + e.getMessage());
            }
        }
        return list;
    }

    private void calculateStockBalanceByWeight(String opDate, String clDate, String stockCode,
                                               String compCode, int macId, boolean calSale, boolean calPur,
                                               boolean calRI, boolean calRo, boolean calMill) {
        String sale = String.valueOf(calSale);
        String purchase = String.valueOf(calPur);
        String retIn = String.valueOf(calRI);
        String retOut = String.valueOf(calRo);
        String milling = String.valueOf(calMill);
        String delSql = "delete from tmp_stock_balance where mac_id = " + macId;
        String sql = "insert into tmp_stock_balance(stock_code, loc_code, weight, qty, comp_code,mac_id)\n" +
                "select stock_code,loc_code,sum(total_weight) total_weight,sum(ttl_qty) ttl_qty,comp_code," + macId + "\n" +
                "from (\n" +
                "select stock_code,loc_code,sum(total_weight) total_weight,sum(qty) ttl_qty,comp_code\n" +
                "from v_opening\n" +
                "where deleted = false\n" +
                "and tran_source = 1\n" +
                "and date(op_date)='" + opDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "group by loc_code\n" +
                "\tunion all\n" +
                "select stock_code,loc_code,sum(total_weight)*-1 total_weight,sum(qty)*-1 ttl_qty,comp_code\n" +
                "from v_sale\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "and (calculate = true and false = " + sale + ")\n" +
                "group by loc_code\n" +
                "\tunion all\n" +
                "select stock_code,loc_code,sum(total_weight) total_weight,sum(qty) ttl_qty,comp_code\n" +
                "from v_purchase\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "and (calculate = true and false = " + purchase + ")\n" +
                "group by loc_code\n" +
                "\tunion all\n" +
                "select stock_code,loc_code,sum(total_weight) total_weight,sum(qty) ttl_qty,comp_code\n" +
                "from v_return_in\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "and (calculate = true and false = " + retIn + ")\n" +
                "group by loc_code\n" +
                "\tunion all\n" +
                "select stock_code,loc_code,sum(total_weight)*-1 total_weight,sum(qty)*-1 ttl_qty,comp_code\n" +
                "from v_return_out\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "and (calculate = true and false = " + retOut + ")\n" +
                "group by loc_code\n" +
                "\tunion all\n" +
                "select stock_code,loc_code,sum(total_weight) total_weight,sum(in_qty) ttl_qty,comp_code\n" +
                "from v_stock_io\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "and in_qty>0\n" +
                "group by loc_code\n" +
                "\tunion all\n" +
                "select stock_code,loc_code,sum(total_weight)*-1 total_weight,sum(out_qty)*-1 ttl_qty,comp_code\n" +
                "from v_stock_io\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "and out_qty>0\n" +
                "group by loc_code\n" +
                "\tunion all\n" +
                "select stock_code,loc_code_from,sum(total_weight)*-1 total_weight,sum(qty)*-1 ttl_qty,comp_code\n" +
                "from v_transfer\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "group by loc_code_from\n" +
                "\tunion all\n" +
                "select stock_code,loc_code_to,sum(total_weight) total_weight,sum(qty)ttl_qty,comp_code\n" +
                "from v_transfer\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "group by loc_code_to\n" +
                "\tunion all\n" +
                "select stock_code,loc_code,sum(tot_weight)*-1 total_weight,sum(qty)*-1 ttl_qty,comp_code\n" +
                "from v_milling_raw\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "and (calculate = true and false = " + milling + ")\n" +
                "group by loc_code\n" +
                "\tunion all\n" +
                "select stock_code,loc_code,sum(tot_weight) total_weight,sum(qty) ttl_qty,comp_code\n" +
                "from v_milling_output\n" +
                "where deleted = false\n" +
                "and date(vou_date) between '" + opDate + "' and '" + clDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and stock_code ='" + stockCode + "'\n" +
                "and (calculate = true and false = " + milling + ")\n" +
                "group by loc_code\n" +
                ")a\n" +
                "group by loc_code";
        reportDao.executeSql(delSql, sql);
    }

    private String getRelStr(String relCode, String compCode, double smallestQty) {
        //generate unit relation.
        StringBuilder relStr = new StringBuilder();
        if (smallestQty != 0 && !Objects.isNull(relCode)) {
            if (hmRelation.get(relCode) == null) {
                hmRelation.put(relCode, detailDao.getRelationDetail(relCode, compCode));
            }
            List<UnitRelationDetail> detailList = hmRelation.get(relCode);
            if (detailList != null) {
                for (UnitRelationDetail unitRelationDetail : detailList) {
                    double smallQty = unitRelationDetail.getSmallestQty();
                    double divider = smallestQty / smallQty;
                    smallestQty = smallestQty % smallQty;
                    String str;
                    if (smallQty == 1) {
                        if (divider != 0) {
                            str = formatter.format(divider);
                            relStr.append(String.format("%s %s%s", str, unitRelationDetail.getUnit(), "*"));
                        }
                    } else {
                        int first = (int) divider;
                        if (first != 0) {
                            str = formatter.format(first);
                            relStr.append(String.format("%s %s%s", str, unitRelationDetail.getUnit(), "*"));
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
                "group by stock_code\n" + ")a\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "a.comp_code = s.comp_code\n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "a.comp_code = st.comp_code\n" +
                "order by st.user_code,s.user_code";
        ResultSet rs = reportDao.executeSql(sql);
        List<ClosingBalance> balanceList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                ClosingBalance cb = ClosingBalance.builder().build();
                cb.setTypeUserCode(rs.getString("type_user_code"));
                cb.setTypeName(rs.getString("stock_type_name"));
                cb.setStockName(rs.getString("stock_name"));
                cb.setStockUsrCode(rs.getString("user_code"));
                cb.setStockCode(rs.getString("stock_code"));
                cb.setOpenQty(rs.getDouble("op_qty"));
                cb.setOpenAmt(rs.getDouble("op_amt"));
                cb.setPurQty(rs.getDouble("pur_qty"));
                cb.setPurAmt(rs.getDouble("pur_amt"));
                cb.setInQty(rs.getDouble("in_qty"));
                cb.setInAmt(rs.getDouble("in_amt"));
                cb.setOutQty(rs.getDouble("out_qty"));
                cb.setOutAmt(rs.getDouble("out_amt"));
                cb.setSaleQty(rs.getDouble("sale_qty"));
                cb.setSaleAmt(rs.getDouble("sale_amt"));
                cb.setBalQty(rs.getDouble("bal_qty"));
                cb.setBalAmount(rs.getDouble("cl_amt"));
                balanceList.add(cb);
            }
        }
        return balanceList;
    }

    @Override
    public List<ReorderLevel> getReorderLevel(String opDate, String clDate, String typeCode, String catCode, String brandCode,
                                              String stockCode, boolean calSale, boolean calPur, boolean calRI,
                                              boolean calRo, String locCode, String compCode,
                                              Integer deptId, Integer macId) throws Exception {
        calStockBalanceByLocation(opDate, clDate, typeCode, catCode, brandCode,
                stockCode, calSale, calPur,
                calRI, calRo, locCode, compCode, deptId, macId);
        String sql1 = "select *,if(small_bal_qty<small_min_qty,1,if(small_bal_qty>small_min_qty,2,if(small_bal_qty<small_max_qty,3,if(small_bal_qty> small_max_qty,4,5)))) position\n\n\n" + "from (\n" + "select a.*,rel.rel_name,bal_qty*rel.smallest_qty small_bal_qty,min_qty*ifnull(rel1.smallest_qty,0) small_min_qty,max_qty*ifnull(rel2.smallest_qty,0) small_max_qty\n" + "from (\n" + "select tmp.stock_code,tmp.loc_code,tmp.smallest_qty bal_qty, tmp.unit bal_unit,ifnull(min_qty,0) min_qty,min_unit,\n" + "ifnull(max_qty,0) max_qty,max_unit,tmp.comp_code,tmp.dept_id,s.rel_code,s.user_code,s.stock_name,l.loc_name\n" + "from tmp_stock_balance tmp\n" + "left join reorder_level r\n" + "on tmp.stock_code= r.stock_code\n" + "and tmp.comp_code = r.comp_code\n" +
                "and tmp.loc_code = r.loc_code\n" +
                "and tmp.mac_id =" + macId + "\n" +
                "and tmp.comp_code ='" + compCode + "'\n" +
                "join stock s on tmp.stock_code = s.stock_code\n" +
                "and tmp.comp_code = s.comp_code\n" +
                "join location l on tmp.loc_code = l.loc_code\n" +
                "and tmp.comp_code = l.comp_code \n) a\n" +
                "join v_relation rel\n" + "on a.rel_code = rel.rel_code\n" +
                "and a.bal_unit = rel.unit\n" + "and a.comp_code = rel.comp_code\n" +
                "left join v_relation rel1\n" + "on a.rel_code = rel1.rel_code\n" +
                "and a.min_unit = rel1.unit\n" + "and a.comp_code = rel1.comp_code\n" +
                "left join v_relation rel2\n" +
                "on a.rel_code = rel2.rel_code\n" +
                "and a.max_unit = rel2.unit\n" +
                "and a.comp_code = rel2.comp_code\n)b\n" +
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
                r.setBalUnit(getRelStr(relCode, compCode, balSmallQty));
                r.setBalSmallQty(balSmallQty);
                reorderLevels.add(r);
            }
        }
        return reorderLevels;
    }

    @Override
    public List<General> getStockListByGroup(String typeCode, String compCode, Integer macId) throws Exception {
        String sql = "select s.stock_code,s.user_code,s.stock_name,s.stock_type_code,\n" +
                "st.stock_type_name,b.brand_name,c.cat_name,rel.rel_name\n" +
                "from stock s \n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "and s.comp_code = st.comp_code\n" +
                "left join stock_brand b on s.brand_code = b.brand_code\n" +
                "and s.comp_code = b.comp_code\n" +
                "left join category c on s.category_code = c.cat_code\n" +
                "and s.comp_code = c.comp_code\n" +
                "left join unit_relation rel on s.rel_code = rel.rel_code\n" +
                "and s.comp_code = rel.comp_code\n" +
                "where s.active = true and s.comp_code = '" + compCode + "' \n" +
                "and (s.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "order by st.stock_type_code";
        ResultSet rs = reportDao.executeSql(sql);
        List<General> generalList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                General g = General.builder().build();
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
    public Mono<ReturnObject> getTopSaleByCustomer(String fromDate, String toDate, Integer deptId, String compCode) {
        String sql = """
                select t.user_code,t.trader_name,t.address, sum(sh.vou_total) vou_total,count(*) vou_qty
                from sale_his sh join trader t
                on sh.trader_code = t.code
                and sh.comp_code = t.comp_code
                where sh.deleted = false
                and sh.comp_code = :compCode
                and (sh.dept_id =:deptId or 0 =:deptId)
                and date(vou_date) between :fromDate and :toDate
                group by sh.trader_code
                order by vou_total desc""";
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("deptId", deptId)
                .map((row) -> General.builder()
                        .traderCode(row.get("user_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .amount(row.get("vou_total", Double.class))
                        .totalQty(row.get("vou_qty", Double.class))
                        .address(row.get("address", String.class))
                        .build()).all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    @Override
    public List<General> getTopSaleBySaleMan(String fromDate, String toDate, String compCode) throws Exception {
        String sql = "select s.user_code,s.saleman_name,count(*) vou_qty,sum(sh.vou_total) vou_total\n" +
                "from sale_his sh left join sale_man s\n" +
                "on sh.saleman_code = s.saleman_code\n" +
                "and sh.comp_code = s.comp_code\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and sh.comp_code = '" + compCode + "' and sh.deleted = false\n" +
                "group by sh.saleman_code\n" + "order by vou_total desc";
        List<General> generals = new ArrayList<>();
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                General g = General.builder().build();
                g.setSaleManName(rs.getString("saleman_name"));
                g.setSaleManCode(rs.getString("user_code"));
                g.setTotalQty(rs.getDouble("vou_qty"));
                g.setAmount(rs.getDouble("vou_total"));
                generals.add(g);
            }
        }
        return generals;
    }

    @Override
    public Mono<ReturnObject> getTopSaleByStock(String fromDate, String toDate, String typeCode,
                                                String brandCode, String catCode, String compCode,
                                                Integer deptId) {
        String sql = """
                select a.*,sum(amount) amount,sum(a.qty) qty,sum(a.bag) bag
                from (
                select stock_code,s_user_code,stock_name,sum(qty) qty,sum(bag)bag,sum(sale_amt) amount,comp_code
                from v_sale
                where deleted = false
                and date(vou_date) between :fromDate and :toDate
                and comp_code = :compCode
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (cat_code =:catCode or '-' = :catCode)
                and (dept_id =:deptId or 0 = :deptId)
                group by stock_code
                )a
                group by stock_code
                order by qty desc,bag desc""";
        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("deptId", deptId)
                .map((row) -> VSale.builder()
                        .stockCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .saleAmount(row.get("amount", Double.class))
                        .qty(Util1.toNull(row.get("qty", Double.class)))
                        .bag(Util1.toNull(row.get("bag", Double.class)))
                        .build()).all()
                .collectList()
                .map(this::calPercent)
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    @Override
    public List<ClosingBalance> getStockInOutSummary(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        String getSql = """
                select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,
                s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name
                from (select stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,
                sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,comp_code
                from tmp_stock_io_column
                where mac_id =?
                group by stock_code)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and a.comp_code = st.comp_code
                group by stock_code
                order by s.user_code""";
        List<ClosingBalance> balances = new ArrayList<>();
        try {
            ResultSet rs = reportDao.getResultSql(getSql, macId);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    ClosingBalance b = ClosingBalance.builder().build();
                    double opQty = rs.getFloat("op_qty");
                    double purQty = rs.getFloat("pur_qty");
                    double inQty = rs.getFloat("in_qty");
                    double saleQty = rs.getFloat("sale_qty");
                    double outQty = rs.getFloat("out_qty");
                    double balQty = rs.getFloat("bal_qty");
                    String relCode = rs.getString("rel_code");
                    b.setOpenQty(opQty);
                    b.setOpenRel(getRelStr(relCode, compCode, opQty));
                    b.setPurQty(purQty);
                    b.setPurRel(getRelStr(relCode, compCode, purQty));
                    b.setInQty(inQty);
                    b.setInRel(getRelStr(relCode, compCode, inQty));
                    b.setSaleQty(saleQty);
                    b.setSaleRel(getRelStr(relCode, compCode, saleQty));
                    b.setOutQty(outQty);
                    b.setOutRel(getRelStr(relCode, compCode, outQty));
                    b.setBalQty(balQty);
                    b.setBalRel(getRelStr(relCode, compCode, balQty));
                    b.setStockUsrCode(rs.getString("s_user_code"));
                    b.setStockName(rs.getString("stock_name"));
                    b.setStockCode(rs.getString("stock_code"));
                    balances.add(b);
                }
            }
        } catch (Exception e) {
            log.error("getStockInOutSummary: " + Arrays.toString(e.getStackTrace()));
        }
        return balances;
    }

    @Override
    public void calculateStockInOutDetail(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
    }

    @Override
    public void calculateStockInOutDetailByWeight(String opDate, String fromDate, String toDate, String typeCode,
                                                  String catCode, String brandCode, String stockCode, String vouStatus,
                                                  boolean calSale, boolean calPur, boolean calRI, boolean calRO, boolean calMill,
                                                  String compCode, Integer deptId, Integer macId) {
        calculateOpeningByWeight(opDate, fromDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, deptId, macId);
        calculateClosingByWeight(fromDate, toDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, deptId, macId);
    }

    @Override
    public List<ClosingBalance> getStockInOutDetail(String typeCode, String compCode, Integer deptId, Integer macId) {
        String getSql = """
                select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,
                s.rel_code,s.user_code s_user_code,a.stock_code,s.stock_name
                from (
                select tran_option,tran_date,stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,
                sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,remark,vou_no,comp_code,dept_id
                from tmp_stock_io_column
                where mac_id = ?
                and comp_code = ?
                group by tran_date,stock_code,tran_option,vou_no)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                group by tran_date,stock_code,vou_no,tran_option
                order by s.user_code,a.tran_date,a.tran_option,a.vou_no""";
        List<ClosingBalance> balances = new ArrayList<>();
        try {
            ResultSet rs = reportDao.getResultSql(getSql, macId, compCode);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    ClosingBalance b = ClosingBalance.builder().build();
                    double opQty = rs.getFloat("op_qty");
                    double purQty = rs.getFloat("pur_qty");
                    double inQty = rs.getFloat("in_qty");
                    double saleQty = rs.getFloat("sale_qty");
                    double outQty = rs.getFloat("out_qty");
                    double balQty = rs.getFloat("bal_qty");
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
                    b.setVouNo(rs.getString("vou_no"));
                    balances.add(b);
                }
            }
            for (int i = 0; i < balances.size(); i++) {
                if (i > 0) {
                    ClosingBalance prv = balances.get(i - 1);
                    double prvCl = prv.getBalQty();
                    ClosingBalance c = balances.get(i);
                    String relCode = c.getRelCode();
                    c.setOpenQty(prvCl);
                    double opQty = c.getOpenQty();
                    double purQty = c.getPurQty();
                    double inQty = c.getInQty();
                    double outQty = c.getOutQty();
                    double saleQty = c.getSaleQty();
                    double clQty = opQty + purQty + inQty + outQty + saleQty;
                    c.setBalQty(clQty);
                    c.setOpenRel(getRelStr(relCode, compCode, opQty));
                    c.setPurRel(getRelStr(relCode, compCode, purQty));
                    c.setInRel(getRelStr(relCode, compCode, inQty));
                    c.setSaleRel(getRelStr(relCode, compCode, saleQty));
                    c.setOutRel(getRelStr(relCode, compCode, outQty));
                    c.setBalRel(getRelStr(relCode, compCode, clQty));
                } else {
                    ClosingBalance c = balances.get(i);
                    String relCode = c.getRelCode();
                    double opQty = c.getOpenQty();
                    double purQty = c.getPurQty();
                    double inQty = c.getInQty();
                    double outQty = c.getOutQty();
                    double saleQty = c.getSaleQty();
                    double clQty = opQty + purQty + inQty + outQty + saleQty;
                    c.setOpenRel(getRelStr(relCode, compCode, opQty));
                    c.setPurRel(getRelStr(relCode, compCode, purQty));
                    c.setInRel(getRelStr(relCode, compCode, inQty));
                    c.setSaleRel(getRelStr(relCode, compCode, saleQty));
                    c.setOutRel(getRelStr(relCode, compCode, outQty));
                    c.setBalRel(getRelStr(relCode, compCode, clQty));
                }
            }
        } catch (Exception e) {
            log.error(String.format("getStockInOutDetail: %s", e.getMessage()));
        }
        return balances;
    }

    @Override
    public List<ClosingBalance> getStockInOutDetailByWeight(String typeCode, String compCode, Integer deptId, Integer macId) {
        String getSql = """
                select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,
                sum(a.op_weight+a.pur_weight+a.in_weight+a.out_weight+a.sale_weight) bal_weight,
                s.weight_unit,s.user_code s_user_code,a.stock_code,s.stock_name
                from (
                select tran_option,tran_date,stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,
                sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,remark,vou_no,comp_code,dept_id,
                sum(op_weight) op_weight,sum(pur_weight) pur_weight,
                sum(in_weight) in_weight,sum(out_weight) out_weight,sum(sale_weight) sale_weight
                from tmp_stock_io_column
                where mac_id = ?
                and comp_code = ?
                group by tran_date,stock_code,tran_option,vou_no)a
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                group by tran_date,stock_code,vou_no,tran_option
                order by a.tran_option,a.tran_date,a.vou_no""";
        List<ClosingBalance> balances = new ArrayList<>();
        try {
            ResultSet rs = reportDao.getResultSql(getSql, macId, compCode);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    ClosingBalance b = ClosingBalance.builder().build();
                    double opQty = rs.getDouble("op_qty");
                    double purQty = rs.getDouble("pur_qty");
                    double inQty = rs.getDouble("in_qty");
                    double saleQty = rs.getDouble("sale_qty");
                    double outQty = rs.getDouble("out_qty");
                    double balQty = rs.getDouble("bal_qty");

                    double opWeight = rs.getDouble("op_Weight");
                    double purWeight = rs.getDouble("pur_Weight");
                    double inWeight = rs.getDouble("in_Weight");
                    double saleWeight = rs.getDouble("sale_Weight");
                    double outWeight = rs.getDouble("out_Weight");
                    double balWeight = rs.getDouble("bal_Weight");
                    b.setOpenQty(opQty);
                    b.setPurQty(purQty);
                    b.setInQty(inQty);
                    b.setSaleQty(saleQty);
                    b.setOutQty(outQty);
                    b.setBalQty(balQty);

                    b.setOpenWeight(opWeight);
                    b.setPurWeight(purWeight);
                    b.setInWeight(inWeight);
                    b.setSaleWeight(saleWeight);
                    b.setOutWeight(outWeight);
                    b.setBalWeight(balWeight);

                    b.setCompCode(compCode);
                    b.setDeptId(deptId);
                    b.setVouDate(Util1.toDateStr(rs.getDate("tran_date"), "dd/MM/yyyy"));
                    b.setStockUsrCode(Util1.isNull(rs.getString("s_user_code"), rs.getString("stock_code")));
                    b.setStockName(rs.getString("stock_name"));
                    b.setRemark(rs.getString("remark"));
                    b.setWeightUnit(rs.getString("weight_unit"));
                    b.setVouNo(rs.getString("vou_no"));
                    balances.add(b);
                }
            }
            for (int i = 0; i < balances.size(); i++) {
                if (i > 0) {
                    ClosingBalance prv = balances.get(i - 1);
                    double prvCl = prv.getBalQty();
                    double prvWCl = prv.getBalWeight();
                    ClosingBalance c = balances.get(i);
                    c.setOpenQty(prvCl);
                    c.setOpenWeight(prvWCl);
                    double opQty = c.getOpenQty();
                    double purQty = c.getPurQty();
                    double inQty = c.getInQty();
                    double outQty = c.getOutQty();
                    double saleQty = c.getSaleQty();
                    double clQty = opQty + purQty + inQty + outQty + saleQty;

                    double opWeight = c.getOpenWeight();
                    double purWeight = c.getPurWeight();
                    double inWeight = c.getInWeight();
                    double outWeight = c.getOutWeight();
                    double saleWeight = c.getSaleWeight();
                    double clWeight = opWeight + purWeight + inWeight + outWeight + saleWeight;
                    c.setOpenQty(opQty);
                    c.setOpenRel(opQty == 0 ? null : Util1.format(opQty));
                    c.setPurQty(purQty);
                    c.setPurRel(purQty == 0 ? null : Util1.format(purQty));
                    c.setInQty(inQty);
                    c.setInRel(inQty == 0 ? null : Util1.format(inQty));
                    c.setSaleQty(saleQty);
                    c.setSaleRel(saleQty == 0 ? null : Util1.format(saleQty));
                    c.setOutQty(outQty);
                    c.setOutRel(outQty == 0 ? null : Util1.format(outQty));
                    c.setBalQty(clQty);
                    c.setBalRel(clQty == 0 ? null : Util1.format(clQty));

                    c.setOpenWeight(opWeight);
                    c.setPurWeight(purWeight);
                    c.setInWeight(inWeight);
                    c.setSaleWeight(saleWeight);
                    c.setOutWeight(outWeight);
                    c.setBalWeight(clWeight);
                } else {
                    ClosingBalance c = balances.get(i);
                    double opQty = c.getOpenQty();
                    double purQty = c.getPurQty();
                    double inQty = c.getInQty();
                    double outQty = c.getOutQty();
                    double saleQty = c.getSaleQty();
                    double clQty = opQty + purQty + inQty + outQty + saleQty;

                    double opWeight = c.getOpenWeight();
                    double purWeight = c.getPurWeight();
                    double inWeight = c.getInWeight();
                    double outWeight = c.getOutWeight();
                    double saleWeight = c.getSaleWeight();
                    double clWeight = opWeight + purWeight + inWeight + outWeight + saleWeight;
                    c.setOpenQty(opQty);
                    c.setOpenRel(opQty == 0 ? null : Util1.format(opQty));
                    c.setPurQty(purQty);
                    c.setPurRel(purQty == 0 ? null : Util1.format(purQty));
                    c.setInQty(inQty);
                    c.setInRel(inQty == 0 ? null : Util1.format(inQty));
                    c.setSaleQty(saleQty);
                    c.setSaleRel(saleQty == 0 ? null : Util1.format(saleQty));
                    c.setOutQty(outQty);
                    c.setOutRel(outQty == 0 ? null : Util1.format(outQty));
                    c.setBalQty(clQty);
                    c.setBalRel(clQty == 0 ? null : Util1.format(clQty));
                    c.setOpenWeight(opWeight);
                    c.setPurWeight(purWeight);
                    c.setInWeight(inWeight);
                    c.setSaleWeight(saleWeight);
                    c.setOutWeight(outWeight);
                    c.setBalWeight(clWeight);
                }
            }
        } catch (Exception e) {
            log.error(String.format("getStockInOutDetailByWeight: %s", e.getMessage()));
        }
        return balances;
    }

    @Override
    public List<StockValue> getStockValue(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculatePrice(toDate, opDate, stockCode, typeCode, catCode, brandCode, compCode, macId);
        List<StockValue> values = new ArrayList<>();
        String getSql = """
                select a.*,
                sum(ifnull(tmp.pur_avg_price,0)) pur_avg_price,bal_qty*sum(ifnull(tmp.pur_avg_price,0)) pur_avg_amt,
                sum(ifnull(tmp.in_avg_price,0)) in_avg_price,bal_qty*sum(ifnull(tmp.in_avg_price,0)) in_avg_amt,
                sum(ifnull(tmp.std_price,0)) std_price,bal_qty*sum(ifnull(tmp.std_price,0)) std_amt,
                sum(ifnull(tmp.pur_recent_price,0)) pur_recent_price,bal_qty*sum(ifnull(tmp.pur_recent_price,0)) pur_recent_amt,
                sum(ifnull(tmp.fifo_price,0)) fifo_price,bal_qty*sum(ifnull(tmp.fifo_price,0)) fifo_amt,
                sum(ifnull(tmp.lifo_price,0)) lifo_price,bal_qty*sum(ifnull(tmp.lifo_price,0)) lifo_amt,
                sum(ifnull(tmp.io_recent_price,0)) io_recent_price,bal_qty*sum(ifnull(tmp.io_recent_price,0)) io_recent_amt,
                s.rel_code,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name,rel.rel_name
                from (
                select stock_code,sum(op_qty)+sum(pur_qty)+sum(in_qty) +sum(out_qty) +sum(sale_qty) bal_qty,mac_id,comp_code
                from tmp_stock_io_column
                where mac_id = ?
                and comp_code= ?
                group by stock_code)a
                left join tmp_stock_price tmp
                on a.stock_code  = tmp.stock_code
                and a.mac_id = tmp.mac_id
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and a.comp_code = rel.comp_code
                join stock_type st on s.stock_type_code = st.stock_type_code
                and a.comp_code = st.comp_code
                group by a.stock_code
                order by s.user_code""";
        try {
            ResultSet rs = reportDao.getResultSql(getSql, macId, compCode);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    StockValue value = StockValue.builder().build();
                    value.setStockUserCode(rs.getString("s_user_code"));
                    value.setStockName(rs.getString("stock_name"));
                    value.setBalRel(getRelStr(rs.getString("rel_code"), compCode, rs.getFloat("bal_qty")));
                    value.setQty(rs.getDouble("bal_qty"));
                    value.setRelation(rs.getString("rel_name"));
                    value.setPurAvgPrice(rs.getDouble("pur_avg_price"));
                    value.setPurAvgAmount(rs.getDouble("pur_avg_amt"));
                    value.setInAvgPrice(rs.getDouble("in_avg_price"));
                    value.setInAvgAmount(rs.getDouble("in_avg_amt"));
                    value.setStdPrice(rs.getDouble("std_price"));
                    value.setStdAmount(rs.getDouble("std_amt"));
                    value.setRecentPrice(rs.getDouble("pur_recent_price"));
                    value.setRecentAmt(rs.getDouble("pur_recent_amt"));
                    value.setFifoPrice(rs.getDouble("fifo_price"));
                    value.setFifoAmt(rs.getDouble("fifo_amt"));
                    value.setLifoPrice(rs.getDouble("lifo_price"));
                    value.setLifoAmt(rs.getDouble("lifo_amt"));
                    value.setIoRecentPrice(rs.getDouble("io_recent_price"));
                    value.setIoRecentAmt(rs.getDouble("io_recent_amt"));
                    values.add(value);
                }
            }
        } catch (Exception e) {
            log.error(String.format("getStockValue: %s", e.getMessage()));
        }
        return values;
    }

    @Override
    public List<VOpening> getOpeningByLocation(String typeCode, String brandCode, String catCode, String stockCode, Integer macId, String compCode, Integer deptId) throws Exception {
        List<VOpening> list = new ArrayList<>();
        String sql = "select v.op_date,v.vou_no,v.remark,v.stock_code,v.stock_user_code,v.stock_name,l.loc_name,\n" +
                "v.unit,v.qty,v.price,v.amount,v.comp_code,v.dept_id\n" +
                "from v_opening v join location l\n" +
                "on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "where v.deleted = false\n" +
                "and v.tran_source = 1\n" +
                "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (v.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (v.brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and v.loc_code in (select f_code from f_location where mac_id = " + macId + ")\n" +
                "and v.comp_code ='" + compCode + "'\n" +
                "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "order by l.loc_name,v.stock_user_code\n";
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
        String sql = "select a.*,t.stock_type_name\n" +
                "from (select v.op_date,v.remark,v.stock_type_code,v.stock_code,v.stock_user_code,v.stock_name,l.loc_name,\n" +
                "unit,qty,price,amount,v.comp_code\n" +
                "from v_opening v join location l \n" +
                "on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "where v.deleted = false \n" +
                "and v.tran_source = 1 \n" +
                "and v.comp_code = '" + compCode + "'\n" +
                "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and (v.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (v.brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (v.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "'))a\n" +
                "join stock_type t on a.stock_type_code = t.stock_type_code\n" +
                "and a.comp_code = t.comp_code\n" +
                "order by t.stock_type_name,a.stock_user_code";
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
        String sql = "select v.vou_date,v.vou_no,v.remark,v.description,s.user_code vs_user_code,s.description vou_status_name,v.s_user_code,v.stock_name,l.loc_name,\n" +
                "v.out_qty,v.out_unit,v.cur_code,v.cost_price,v.cost_price* v.out_qty out_amt \n" +
                "from v_stock_io v join vou_status s\n" +
                "on v.vou_status = s.code\n" +
                "and v.comp_code = s.comp_code\n" +
                "join location l on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "where v.comp_code = '" + compCode + "'\n" + "and v.deleted = 0\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (v.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (v.category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" +
                "and (v.brand_code ='" + brandCode + "' or '-'='" + brandCode + "')\n" +
                "and (v.vou_status = '" + vouStatus + "' or '-' = '" + vouStatus + "')\n" +
                "and v.out_qty is not null and v.out_unit is not null\n\n" +
                "group by date(v.vou_date),v.vou_no,v.stock_code,v.in_unit,v.out_unit,v.cur_code\n" +
                "order by s.user_code,v.cur_code,v.vou_date,v.vou_no,v.s_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        List<VStockIO> list = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockIO io = VStockIO.builder().build();
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
                io.setOutQty(rs.getDouble("out_qty"));
                io.setOutUnit(rs.getString("out_unit"));
                io.setCurCode(rs.getString("cur_code"));
                io.setCostPrice(rs.getDouble("cost_price"));
                io.setOutAmt(rs.getDouble("out_amt"));
                list.add(io);
            }
        }
        return list;
    }

    @Override
    public List<VStockIO> getStockIOPriceCalender(String vouType, String fromDate, String toDate, String typeCode,
                                                  String catCode, String brandCode, String stockCode, String compCode,
                                                  Integer deptId) throws Exception {
        String filter = "";
        if (!vouType.equals("-")) {
            filter += " and vou_status ='" + vouType + "'\n";
        }
        if (!typeCode.equals("-")) {
            filter += " and stock_type_code ='" + typeCode + "'\n";
        }
        if (!catCode.equals("-")) {
            filter += " and category_code ='" + catCode + "'\n";
        }
        if (!brandCode.equals("-")) {
            filter += " and brand_code ='" + brandCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += " and stock_code ='" + stockCode + "'\n";
        }
        String sql = "select vou_date, vou_no, remark, stock_code, stock_user_code, stock_name, price, unit, rel_name, small_price, description\n" +
                "from (\n" +
                "select a.*,rel.rel_name,a.price/rel.smallest_qty small_price,null description\n" +
                "from (\n" +
                "select date(op_date) vou_date,vou_no,null vou_status,'Opening' remark,stock_code,stock_user_code,stock_name,rel_code,\n" +
                "price, unit,comp_code\n" +
                "from v_opening \n" +
                "where price > 0\n" +
                "and deleted = false\n" +
                "and date(op_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" + filter +
                "group by stock_code,unit\n" +
                ")a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.unit = rel.unit\n" +
                "and a.comp_code = rel.comp_code\n" +
                "\tunion\n" +
                "select a.*,rel.rel_name,a.cost_price/rel.smallest_qty small_price,vs.description\n" +
                "from (\n" +
                "select date(vou_date) vou_date,vou_no,vou_status,remark,stock_code,s_user_code,stock_name,rel_code,\n" +
                "cost_price,ifnull(in_unit,out_unit) unit,comp_code\n" +
                "from v_stock_io\n" +
                "where deleted = 0\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" + filter +
                "group by stock_code,cost_price,ifnull(in_unit,out_unit)\n" +
                ")a\n" +
                "join v_relation rel on a.rel_code = rel.rel_code\n" +
                "and a.unit = rel.unit\n" +
                "and a.comp_code = rel.comp_code\n" +
                "join vou_status vs on a.vou_status = vs.code\n" +
                "and a.comp_code = vs.comp_code\n" +
                ")a\n" +
                "group by small_price\n" +
                "order by stock_user_code,vou_date,vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        List<VStockIO> ioList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VStockIO io = VStockIO.builder().build();
                io.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyy"));
                io.setVouNo(rs.getString("vou_no"));
                io.setStockUsrCode(rs.getString("stock_user_code"));
                io.setStockCode(rs.getString("stock_code"));
                io.setStockName(rs.getString("stock_name"));
                io.setVouTypeName(rs.getString("description"));
                io.setRelName(rs.getString("rel_name"));
                io.setInUnit(rs.getString("unit"));
                io.setCostPrice(rs.getDouble("price"));
                io.setSmallPrice(rs.getDouble("small_price"));
                io.setRemark(rs.getString("remark"));
                ioList.add(io);
            }
        }
        return ioList;
    }


    @Override
    public List<VPurchase> getPurchaseHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String reference, String userCode, String stockCode, String locCode,
                                              String compCode, Integer deptId, String deleted,
                                              String projectNo, String curCode) throws Exception {
        remark = remark.concat("%");
        reference = reference.concat("%");
        String sql = """
                select a.*,t.trader_name
                from (
                select vou_date,vou_no,remark,reference,created_by,paid,vou_total,deleted,trader_code,comp_code,dept_id
                from v_purchase
                where comp_code = ?
                and (dept_id = ? or 0 = ?)
                and deleted =?
                and date(vou_date) between ? and ?
                and cur_code = ?
                and (vou_no = ? or '-' = ?)
                and (remark like ? or '-%'= ?)
                and (reference like ? or '-%'= ?)
                and (trader_code = ? or '-'= ?)
                and (created_by = ? or '-'= ?)
                and (stock_code =? or '-' =?)
                and (loc_code =? or '-' =?)
                and (project_no =? or '-' =?)
                group by vou_no)a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by vou_date desc""";
        ResultSet rs = getResult(sql, compCode, deptId, deptId, Util1.getBoolean(deleted), fromDate, toDate, curCode, vouNo, vouNo,
                remark, remark, reference, reference,
                traderCode, traderCode,
                userCode, userCode,
                stockCode, stockCode,
                locCode, locCode,
                projectNo, projectNo);
        List<VPurchase> purchaseList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase s = VPurchase.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setPaid(rs.getDouble("paid"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setDeleted(rs.getBoolean("deleted"));
                s.setDeptId(rs.getInt("dept_id"));
                purchaseList.add(s);
            }
        }
        return purchaseList;
    }


    @Override
    public List<VReturnIn> getReturnInHistory(String fromDate, String toDate, String traderCode, String vouNo,
                                              String remark, String userCode, String stockCode,
                                              String locCode, String compCode, Integer deptId,
                                              String deleted, String projectNo, String curCode) throws Exception {
        String sql = "select a.*,t.trader_name\n" +
                "from (\n" +
                "select vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code,comp_code,dept_id \n" +
                "from v_return_in \n" + "where comp_code = '" + compCode + "'\n" +
                "and deleted = " + deleted + "\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" +
                "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" +
                "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" +
                "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" +
                "and (project_no ='" + projectNo + "' or '-' ='" + projectNo + "')\n" +
                "group by vou_no\n" + ")a\n" + "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "order by vou_date desc";
        ResultSet rs = reportDao.executeSql(sql);
        List<VReturnIn> returnInList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VReturnIn s = VReturnIn.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setRemark(rs.getString("remark"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setPaid(rs.getDouble("paid"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setDeleted(rs.getBoolean("deleted"));
                s.setDeptId(rs.getInt("dept_id"));
                returnInList.add(s);
            }
        }
        return returnInList;
    }

    @Override
    public List<VReturnOut> getReturnOutHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark,
                                                String userCode, String stockCode, String locCode, String compCode, Integer deptId,
                                                String deleted, String projectNo, String curCode) throws Exception {
        String sql = "select a.*,t.trader_name\n" +
                "from (\n" +
                "select vou_date,vou_no,remark,created_by,paid,vou_total,deleted,trader_code,comp_code,dept_id \n" +
                "from v_return_out \n" +
                "where comp_code = '" + compCode + "'\n" +
                "and deleted = " + deleted + "\n" +
                "and cur_code = '" + curCode + "'\n" +
                "and (dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" +
                "and (remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" +
                "and (trader_code = '" + traderCode + "' or '-'= '" + traderCode + "')\n" +
                "and (created_by = '" + userCode + "' or '-'='" + userCode + "')\n" +
                "and (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "and (loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" +
                "and (project_no ='" + projectNo + "' or '-' ='" + projectNo + "')\n" +
                "group by vou_no\n" + ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code= t.comp_code\n" +
                "order by vou_date desc";
        ResultSet rs = reportDao.executeSql(sql);
        List<VReturnOut> returnInList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VReturnOut s = new VReturnOut();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
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
    public List<OPHis> getOpeningHistory(String fromDate, String toDate, String vouNo, String remark, String userCode, String stockCode,
                                         String locCode, String compCode, Integer deptId, String curCode, String deleted, int type, String traderCode) throws Exception {
        String filter = "";
        if (type == 2) {
            filter = "and (v.trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n";
        }

        String sql = "select sum(v.qty) qty,sum(v.bag) bag,sum(v.amount) amount,v.op_date,v.vou_no,v.remark,v.created_by," +
                "v.deleted,l.loc_name,v.comp_code,v.dept_id \n" +
                "from v_opening v join location l\n" +
                "on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "where v.comp_code = '" + compCode + "'\n" +
                "and v.cur_code = '" + curCode + "'\n" +
                "and v.deleted = " + deleted + "\n" +
                "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and date(v.op_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (v.vou_no = '" + vouNo + "' or '-' = '" + vouNo + "')\n" +
                "and (v.remark like '" + remark + "%' or '-%'= '" + remark + "%')\n" +
                "and (v.created_by = '" + userCode + "' or '-'='" + userCode + "')\n" +
                "and (v.stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                "and (v.loc_code ='" + locCode + "' or '-' ='" + locCode + "')\n" +
                "and v.tran_source = " + type + "\n" + filter +
                "group by v.vou_no\n" +
                "order by v.op_date,v.vou_no desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<OPHis> list = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                OPHis s = new OPHis();
                OPHisKey key = new OPHisKey();
                key.setCompCode(rs.getString("comp_code"));
                key.setVouNo(rs.getString("vou_no"));
                s.setKey(key);
                s.setDeptId(rs.getInt("dept_id"));
                s.setQty(rs.getDouble("qty"));
                s.setBag(rs.getDouble("bag"));
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
    public List<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark, String userCode, String stockCode,
                                              String locCode, String compCode, Integer deptId, String deleted, String traderCode) throws Exception {

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
            filter += "and v.created_by ='" + userCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code ='" + stockCode + "'\n";
        }
        if (!traderCode.equals("-")) {
            filter += "and trader_code = '" + traderCode + "'\n";
        }
        if (!locCode.equals("-")) {
            filter += "and (loc_code_from ='" + locCode + "' or loc_code_to ='" + locCode + "')\n";
        }
        String sql = "select v.vou_date,v.vou_no,v.remark,v.ref_no,v.created_by," +
                "v.deleted,v.dept_id,l.loc_name from_loc_name,ll.loc_name to_loc_name,t.trader_name, v.labour_group_code\n" +
                "from v_transfer v join location l\n" +
                "on v.loc_code_from = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "join location ll on v.loc_code_to = ll.loc_code\n" +
                "and v.comp_code = ll.comp_code\n" +
                "left join trader t on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where v.comp_code = '" + compCode + "'\n" +
                "and v.deleted = " + deleted + "\n" +
                "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + filter +
                "group by v.vou_no\n" +
                "order by v.vou_date desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VTransfer> openingList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VTransfer s = VTransfer.builder()
                        .vouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()))
                        .vouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"))
                        .vouNo(rs.getString("vou_no"))
                        .remark(rs.getString("remark"))
                        .refNo(rs.getString("ref_no"))
                        .createdBy(rs.getString("created_by"))
                        .deleted(rs.getBoolean("deleted"))
                        .fromLocationName(rs.getString("from_loc_name"))
                        .toLocationName(rs.getString("to_loc_name"))
                        .deptId(rs.getInt("dept_id"))
                        .traderName(rs.getString("trader_name"))
                        .build();
                openingList.add(s);
            }
        }
        return openingList;
    }

    @Override
    public List<WeightLossHis> getWeightLossHistory(String fromDate, String toDate, String refNo, String vouNo, String remark, String stockCode, String locCode, String compCode, Integer deptId, String deleted) {
        List<WeightLossHis> list = new ArrayList<>();
        String sql = "select vou_no,vou_date,remark,ref_no,created_by,deleted\n" +
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
                "group by vou_no\n" +
                "order by vou_date";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            while (rs.next()) {
                WeightLossHis his = new WeightLossHis();
                WeightLossHisKey key = new WeightLossHisKey();
                key.setCompCode(compCode);
                key.setVouNo(rs.getString("vou_no"));
                his.setKey(key);
                his.setDeptId(deptId);
                his.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                his.setVouDate(rs.getTimestamp("vou_date").toLocalDateTime());
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
        String sql = "select s.s_user_code,s.vou_date,s.vou_no,s.stock_code,\n" +
                "s.stock_name,s.sale_unit,s.sale_price,s.remark,t.trader_name,s.cur_code \n" +
                "from v_sale s join trader t\n" +
                "on s.trader_code = t.code\n" +
                "and s.comp_code = t.comp_code\n" +
                "where s.comp_code = '" + compCode + "'\n" +
                "and s.deleted = false\n" +
                "and date(s.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (s.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (s.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (s.cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (s.brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "group by s.stock_code,s.sale_price,s.sale_unit\n" +
                "order by s.s_user_code,s.vou_date,s.sale_unit\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VSale> saleList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale s = VSale.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setStockUserCode(rs.getString("s_user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setSaleUnit(rs.getString("sale_unit"));
                s.setSalePrice(rs.getDouble("sale_price"));
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
        String sql = "select s.s_user_code,s.vou_date,s.vou_no,s.stock_code,\n" +
                "s.stock_name,s.pur_unit,s.pur_price,s.remark,t.trader_name,s.cur_code \n" +
                "from v_purchase s join trader t\n" +
                "on s.trader_code = t.code\n" +
                "and s.comp_code = t.comp_code\n" +
                "where s.comp_code = '" + compCode + "'\n" +
                "and s.deleted = false\n" +
                "and date(s.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (s.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and (s.stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (s.category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (s.brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" +
                "group by s.stock_code,s.pur_price,s.pur_unit\n" +
                "order by s.s_user_code,s.vou_date,s.pur_unit\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VPurchase> purchaseList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VPurchase s = VPurchase.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setStockUserCode(rs.getString("s_user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setPurUnit(rs.getString("pur_unit"));
                s.setPurPrice(rs.getDouble("pur_price"));
                s.setRemark(rs.getString("remark"));
                s.setTraderName(rs.getString("trader_name"));
                s.setCurCode(rs.getString("cur_code"));
                purchaseList.add(s);
            }
        }
        return purchaseList;
    }

    @Override
    public General getSmallestQty(String stockCode, String unit, String compCode, Integer deptId) {
        General g = General.builder().build();
        g.setSmallQty(1.0);
        String sql = "select ud.qty,ud.smallest_qty\n" +
                "from stock s join unit_relation_detail ud\n" + "on s.rel_code = ud.rel_code\n" +
                "and s.comp_code =ud.comp_code\n" +
                "where s.stock_code ='" + stockCode + "'\n" +
                "and s.comp_code ='" + compCode + "'\n" +
                "and ud.unit ='" + unit + "'";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs.next()) {
                g.setQty(rs.getDouble("qty"));
                g.setSmallQty(rs.getDouble("smallest_qty"));
            }
        } catch (Exception e) {
            log.error(String.format("getSmallestQty: %s", e.getMessage()));
        }
        return g;
    }

    @Override
    public List<General> isStockExist(String stockCode, String compCode) {
        return searchDetail(stockCode, compCode);
    }

    private List<General> searchDetail(String code, String compCode) {
        List<General> str = new ArrayList<>();
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
                        General g = General.builder().build();
                        g.setMessage("Transaction exist in " + s2);
                        str.add(g);
                    }
                }
            } catch (Exception e) {
                log.error(String.format("searchTran: %s", e.getMessage()));
            }

        });
        return str;
    }

    private List<General> searchVoucher(String code, String compCode) {
        List<General> list = new ArrayList<>();
        HashMap<String, String> hm = new HashMap<>();
        hm.put("sale_his", "Sale");
        hm.put("pur_his", "Purchase");
        hm.put("ret_in_his", "Return In");
        hm.put("ret_out_his", "Return Out");
        hm.forEach((s, s2) -> {
            String sql = "select exists(select " + "trader_code" + " from " + s + " where deleted = false and " + "trader_code" + " ='" + code + "' and comp_code ='" + compCode + "') exist";
            try {
                ResultSet rs = reportDao.executeSql(sql);
                if (rs.next()) {
                    if (rs.getBoolean("exist")) {
                        General g = General.builder().build();
                        g.setMessage("Transaction exist in " + s2);
                        list.add(g);
                    }
                }
            } catch (Exception e) {
                log.error(String.format("searchVoucher: %s", e.getMessage()));
            }
        });
        return list;
    }

    @Override
    public List<VStockIO> getStockInOutVoucher(String vouNo, String compCode) {
        String sql = """
                select vou_no,vou_date,v.remark,description,s_user_code,stock_name,weight,in_qty,
                in_unit,out_qty,out_unit,in_bag,out_bag,received_name, received_phone, car_no,
                l.loc_name,g.labour_name,t.trader_name,t.phone,r.reg_name,j.job_name,
                s1.unit_name weight_unit_name,s2.unit_name in_unit_name,s3.unit_name out_unit_name
                from v_stock_io v join location l
                on v.loc_code = l.loc_code
                and v.comp_code =l.comp_code
                left join labour_group g on v.labour_group_code = g.code
                and v.comp_code = g.comp_code
                left join trader t on v.trader_code = t.code
                and v.comp_code = t.comp_code
                left join region r on t.reg_code = r.reg_code
                and t.comp_code = r.comp_code
                left join job j on v.job_code = j.job_no
                and v.comp_code = j.comp_code
                left join stock_unit s1 on v.weight_unit = s1.unit_code
                and v.comp_code = s1.comp_code
                left join stock_unit s2 on v.in_unit = s2.unit_code
                and v.comp_code = s2.comp_code
                left join stock_unit s3 on v.out_unit = s3.unit_code
                and v.comp_code  = s3.comp_code
                where v.vou_no =?
                and v.comp_code=?
                order by unique_id;
                """;
        List<VStockIO> riList = new ArrayList<>();
        try {
            ResultSet rs = getResult(sql, vouNo, compCode);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VStockIO in = VStockIO.builder().build();                     //vou_no, vou_date, remark, description, s_user_code, stock_name, in_qty, in_unit, out_qty, out_unit, loc_name
                    in.setStockName(rs.getString("stock_name"));
                    in.setInUnit(rs.getString("in_unit"));
                    in.setInQty(Util1.toNull(rs.getDouble("in_qty")));
                    in.setOutUnit(rs.getString("out_unit"));
                    in.setOutQty(Util1.toNull(rs.getDouble("out_qty")));
                    in.setVouNo(rs.getString("vou_no"));
                    in.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    in.setLocName(rs.getString("loc_name"));
                    in.setStockCode(rs.getString("s_user_code"));
                    in.setRemark(rs.getString("remark"));
                    in.setDescription(rs.getString("description"));
                    in.setJobName(rs.getString("job_name"));
                    in.setLabourGroupName(rs.getString("labour_name"));
                    in.setTraderName(rs.getString("trader_name"));
                    in.setPhoneNo(rs.getString("phone"));
                    in.setReceivedName(rs.getString("received_name"));
                    in.setReceivedPhone(rs.getString("received_phone"));
                    in.setCarNo(rs.getString("car_no"));
                    in.setRegionName(rs.getString("reg_name"));
                    in.setUnit(Util1.isNull(in.getInUnit(), in.getOutUnit()));
                    in.setInUnitName(rs.getString("in_unit_name"));
                    in.setOutUnitName(rs.getString("out_unit_name"));
                    in.setWeightUnitName(rs.getString("weight_unit_name"));
                    in.setWeight(Util1.toNull(rs.getDouble("weight")));
                    in.setInBag(Util1.toNull(rs.getDouble("in_bag")));
                    in.setOutBag(Util1.toNull(rs.getDouble("out_bag")));
                    riList.add(in);
                }
            }
        } catch (Exception e) {
            log.error(String.format("getStockInOutVoucher: %s", e.getMessage()));
        }
        return riList;
    }

    @Override
    public List<VReturnIn> getReturnInVoucher(String vouNo, String compCode) {
        String sql = """
                select stock_name,unit,qty,price,amt,t.trader_name,r.remark,date(vou_date) vou_date,
                r.vou_total,r.paid,r.balance,r.vou_no
                from v_return_in r join trader t
                on r.trader_code = t.code
                and r.comp_code = t.comp_code
                where r.comp_code = ?
                and vou_no =?
                order by unique_id""";
        List<VReturnIn> riList = new ArrayList<>();
        try {
            ResultSet rs = reportDao.getResultSql(sql, compCode, vouNo);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VReturnIn in = VReturnIn.builder().build();
                    in.setStockName(rs.getString("stock_name"));
                    in.setUnit(rs.getString("unit"));
                    in.setQty(rs.getDouble("qty"));
                    in.setPrice(rs.getDouble("price"));
                    in.setAmount(rs.getDouble("amt"));
                    in.setRemark(rs.getString("remark"));
                    in.setVouDate(rs.getString("vou_date"));
                    in.setVouTotal(rs.getDouble("vou_total"));
                    in.setPaid(rs.getDouble("paid"));
                    in.setVouBalance(rs.getDouble("balance"));
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
        String sql = """
                select stock_name,unit,qty,price,amt,t.trader_name,r.remark,date(vou_date) vou_date,
                r.vou_total,r.paid,r.balance,r.vou_no
                from v_return_out r join trader t
                on r.trader_code = t.code
                and r.comp_code = t.comp_code
                where r.comp_code = ?
                and vou_no =?
                order by unique_id""";
        List<VReturnOut> riList = new ArrayList<>();
        try {
            ResultSet rs = reportDao.getResultSql(sql, compCode, vouNo);
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
    public List<VStockIO> getProcessOutputDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        List<VStockIO> list = new ArrayList<>();
        String sql = "select v.user_code stock_code,v.stock_name,date(end_date) end_date, qty,unit,price,remark,process_no,vs.description,l.loc_name\n" + "from v_process_his v\n" + "join vou_status vs\n" + "on v.pt_code =vs.code\n" + "and v.comp_code = vs.comp_code\n" +
                "join location l on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "where v.comp_code ='" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and v.calculate=true\n" + "and v.finished =true\n" +
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
                    VStockIO s = VStockIO.builder().build();
                    s.setStockCode(rs.getString("stock_code"));
                    s.setStockName(rs.getString("stock_name"));
                    s.setVouDate(Util1.toDateStr(rs.getDate("end_date"), "dd/MM/yyyy"));
                    s.setQty(rs.getDouble("qty"));
                    s.setUnit(rs.getString("unit"));
                    s.setPrice(rs.getDouble("price"));
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
    public List<VStockIO> getProcessOutputSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        List<VStockIO> list = new ArrayList<>();
        String sql = "select a.*,l.loc_name,vs.description\n" + "from (\n" + "select ifnull(user_code,stock_code) stock_code,stock_name,sum(qty) qty,unit,avg(price) avg_price,loc_code,pt_code,comp_code,dept_id\n" + "from v_process_his \n" + "where comp_code ='" + compCode + "'\n" +
                "and (dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and calculate=true\n" + "and finished =true\n" +
                "and deleted = false\n" +
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
                "join vou_status vs\n" + "on a.pt_code =vs.code\n" +
                "and a.comp_code = vs.comp_code\n" +
                "order by vs.description,l.loc_name\n";
        try {
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    VStockIO io = VStockIO.builder().build();
                    io.setStockCode(rs.getString("stock_code"));
                    io.setStockName(rs.getString("stock_name"));
                    io.setQty(rs.getDouble("qty"));
                    io.setUnit(rs.getString("unit"));
                    io.setPrice(rs.getDouble("avg_price"));
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
    public List<VStockIO> getProcessUsageSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        List<VStockIO> list = new ArrayList<>();
        String sql = "select ifnull(v.user_code,v.stock_code) stock_code,v.stock_name,date(vou_date) vou_date, qty,unit,price,vs.description,l.loc_name\n" +
                "from v_process_his_detail v\n" +
                "join vou_status vs\n" + "on v.pt_code =vs.code\n" +
                "and v.comp_code = vs.comp_code\n" +
                "join location l on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "where v.comp_code ='" + compCode + "'\n" +
                "and (v.dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and v.calculate=true\n" +
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
                    VStockIO io = VStockIO.builder().build();
                    io.setStockCode(rs.getString("stock_code"));
                    io.setStockName(rs.getString("stock_name"));
                    io.setQty(rs.getDouble("qty"));
                    io.setUnit(rs.getString("unit"));
                    io.setPrice(rs.getDouble("price"));
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
    public List<VStockIO> getProcessUsageDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        List<VStockIO> list = new ArrayList<>();
        //vou_no, stock_code, comp_code, dept_id, unique_id, vou_date, qty, unit, price,
        // loc_code, deleted, pt_code, user_code, stock_name, stock_type_code, brand_code, category_code, calculate, rel_code, loc_name, description
        String sql = "select v.*,l.loc_name,vs.description\n" +
                "from v_process_his_detail v\n" +
                "join location l\n" +
                "on v.loc_code = l.loc_code\n" + "and v.comp_code = l.comp_code\n" +
                "join vou_status vs\n" +
                "on v.pt_code = vs.code\n" + "and v.comp_code = vs.comp_code\n" +
                "where v.comp_code ='" + compCode + "'\n" +
                "and (v.dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and v.deleted = false\n" +
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
                    VStockIO io = VStockIO.builder().build();
                    io.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    io.setStockCode(rs.getString("user_code"));
                    io.setStockName(rs.getString("stock_name"));
                    io.setQty(rs.getDouble("qty"));
                    io.setUnit(rs.getString("unit"));
                    io.setPrice(rs.getDouble("price"));
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
    public List<GRN> getGRNHistory(String fromDate, String toDate, String batchNo, String traderCode, String vouNo, String remark, String userCode, String stockCode, String locCode, String compCode, Integer deptId, String deleted, String close, boolean orderByBatch) {
        List<GRN> list = new ArrayList<>();
        String orderBy = "order by vou_date desc";
        if (orderByBatch) {
            orderBy = "order by batch_no";
        }
        String filter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            filter += "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n";
        }
        if (!batchNo.equals("-")) {
            filter += "and g.batch_no = '" + batchNo + "'\n";
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
                "select vou_date,g.vou_no,g.comp_code,g.dept_id,g.loc_code,g.created_by,g.batch_no,remark,g.trader_code,g.deleted,g.closed\n" +
                "from grn g join grn_detail gd\n" +
                "on g.vou_no = gd.vou_no\n" +
                "and g.comp_code = gd.comp_code\n" +
                "where g.comp_code ='" + compCode + "'\n" +
                "and (g.dept_id =" + deptId + " or 0 =" + deptId + ")\n" +
                "and deleted =" + deleted + "\n" +
                "and closed =" + close + "\n" + filter +
                "group by g.vou_no\n" + ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" + orderBy;
        try {
            //vou_date, vou_no, comp_code, dept_id, created_by, batch_no, remark, trader_code, user_code, trader_name
            ResultSet rs = reportDao.executeSql(sql);
            if (rs != null) {
                while (rs.next()) {
                    GRN g = new GRN();
                    GRNKey key = new GRNKey();
                    key.setCompCode(rs.getString("comp_code"));
                    key.setVouNo(rs.getString("vou_no"));
                    g.setKey(key);
                    g.setDeptId(rs.getInt("dept_id"));
                    g.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                    g.setVouDate(rs.getTimestamp("vou_date").toLocalDateTime());
                    g.setBatchNo(rs.getString("batch_no"));
                    g.setRemark(rs.getString("remark"));
                    g.setTraderCode(rs.getString("trader_code"));
                    g.setTraderUserCode(rs.getString("user_code"));
                    g.setTraderName(rs.getString("trader_name"));
                    g.setDeleted(rs.getBoolean("deleted"));
                    g.setClosed(rs.getBoolean("closed"));
                    g.setCreatedBy(rs.getString("created_by"));
                    g.setLocCode(rs.getString("loc_code"));
                    list.add(g);
                }
            }
        } catch (Exception e) {
            log.error("getGRNHistory : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<VPurchase> getPurchaseByWeightVoucher(String vouNo, String batchNo, String compCode) {
        List<VPurchase> list = new ArrayList<>();
        String sql = "select 'I' group_name,user_code,stock_name,qty,unit,weight,weight_unit,0 price,0 amount,qty*weight ttl_qty,qty*weight ttl\n" +
                "from v_grn\n" +
                "where batch_no ='" + batchNo + "'\n" +
                "and stock_code in (select stock_code from pur_his_detail where vou_no ='" + vouNo + "' and comp_code ='" + compCode + "')\n" +
                "and comp_code ='" + compCode + "'\n" +
                "union all\n" +
                "select 'R',s_user_code,stock_name,qty,pur_unit,weight,weight_unit,pur_price,pur_amt,qty*weight ttl_qty,(qty*weight)*-1 ttl\n" +
                "from v_purchase\n" +
                "where vou_no ='" + vouNo + "'\n" +
                "and comp_code ='" + compCode + "'\n";
        try {
            ResultSet rs = getResult(sql);
            if (rs != null) {
                while (rs.next()) {
                    VPurchase p = VPurchase.builder().build();
                    p.setGroupName(rs.getString("group_name"));
                    p.setStockUserCode(rs.getString("user_code"));
                    p.setStockName(rs.getString("stock_name"));
                    p.setQty(Util1.toNull(rs.getDouble("qty")));
                    p.setPurUnit(rs.getString("unit"));
                    p.setWeight(Util1.toNull(rs.getDouble("weight")));
                    p.setWeightUnit(rs.getString("weight_unit"));
                    p.setPurAmount(Util1.toNull(rs.getDouble("amount")));
                    p.setTotalQty(Util1.toNull(rs.getDouble("ttl_qty")));
                    p.setPurPrice(Util1.toNull(rs.getDouble("price")));
                    p.setTotal(Util1.toNull(rs.getDouble("ttl")));
                    list.add(p);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }

    @Override
    public List<VSale> getProfitMarginByStock(String fromDate, String toDate, String curCode, String stockCode, String compCode, Integer deptId) throws Exception {
        List<VSale> saleList = new ArrayList<>();
        String sql = "select  \n" +
                "s_user_code stock_code, stock_name,\n" +
                "rel.unit unit,\n" +
                "sum(avg_sale_price) sale_price,\n" +
                "sum(avg_pur_price) purchase_price,\n" +
                "round((sum(avg_sale_price)- sum(avg_pur_price)),2) diff_amount,\n" +
                "concat(cast(round(((sum(avg_sale_price)-sum(avg_pur_price))/(sum(avg_pur_price)))*100,2) as char),'%') diff_percent_amount\n" +
                "from (\n" +
                "select s.stock_code,s.s_user_code,s.stock_name, avg(s.sale_price/rel.smallest_qty) avg_sale_price,rel.unit sale_unit,0 avg_pur_price,null pur_unit,s.rel_code, s.comp_code, s.dept_id\n" +
                "from v_sale s\n" +
                "join v_relation rel \n" +
                "on s.rel_code = rel.rel_code\n" +
                "and s.comp_code = rel.comp_code\n" +
                "and s.sale_unit = rel.unit\n" +
                "where s.deleted = false\n" +
                "and (s.comp_code = '" + compCode + "' or '-' = '" + compCode + "')\n" +
                "and (s.dept_id = '" + deptId + "' or '-' = '" + deptId + "')\n" +
                "and (s.cur_code = '" + curCode + "' or '-' = '" + curCode + "')\n" +
                "and  date(s.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (s.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by s.stock_code\n" +
                "\tunion all\n" +
                "select pur.stock_code,pur.s_user_code,pur.stock_name,0,null,avg(pur.pur_price/rel.smallest_qty) avg_pur_price,rel.unit pur_unit, pur.rel_code, pur.comp_code, pur.dept_id\n" +
                "from v_purchase pur\n" +
                "join v_relation rel on pur.rel_code = rel.rel_code \n" +
                "and pur.pur_unit = rel.unit\n" +
                "where pur.deleted = false\n" +
                "and (pur.comp_code = '" + compCode + "' or '-' = '" + compCode + "')\n" +
                "and (pur.dept_id = '" + deptId + "' or '-' = '" + deptId + "')\n" +
                "and (pur.cur_code = '" + curCode + "' or '-' = '" + curCode + "')\n" +
                "and  date(pur.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and (pur.stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by pur.stock_code\n" +
                ")a\n" +
                "join v_relation rel\n" +
                "on a.rel_code = rel.rel_code\n" +
                "where rel.smallest_qty =1 \n" +
                "group by stock_code \n" +
                "order by stock_code desc";

        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale sale = VSale.builder().build();
                sale.setStockName(rs.getString("stock_name"));
                sale.setStockCode(rs.getString("stock_code"));
                sale.setSaleUnit(rs.getString("unit"));
                sale.setSalePrice(rs.getDouble("sale_price"));
                sale.setSaleAmount(rs.getDouble("purchase_price"));
                sale.setPaid(rs.getDouble("diff_amount"));
                sale.setVouNo(rs.getString("diff_percent_amount"));
                saleList.add(sale);
            }
        }
        return saleList;
    }

    @Override
    public List<VSale> getSaleByDueDate(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
                                        String brandCode, String catCode, String locCode, String batchNo, String compCode,
                                        Integer deptId, Integer macId) throws Exception {
        String filter = "";
        if (!fromDueDate.equals("-") && !toDueDate.equals("-")) {
            filter += "and date(credit_term) between '" + fromDueDate + "' and '" + toDueDate + "'\n";
        }
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
        if (!locCode.equals("-")) {
            filter += "and loc_code='" + locCode + "'\n";
        }
        List<VSale> list = new ArrayList<>();
        String sql = "select a.*,t.trader_name\n" +
                "from (select vou_date,credit_term,vou_no,trader_code,vou_total,comp_code\n" +
                "from sale_his\n" +
                "where deleted = false\n" + filter +
                "and comp_code ='" + compCode + "'\n" +
                "and cur_code ='" + curCode + "'\n" +
                "and dept_id = " + deptId + "\n" +
                ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "order by credit_term,vou_date,vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale s = VSale.builder().build();
                s.setCreditTerm(Util1.toDateStr(rs.getDate("credit_term"), "dd/MM/yyyy"));
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setVouTotal(rs.getDouble("vou_total"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByDueDateDetail(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
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
            filter += "and v.batch_no='" + batchNo + "'\n";
        }
        List<VSale> list = new ArrayList<>();
        String sql = "select v.credit_term,v.vou_date,v.vou_no,v.vou_total,v.paid,v.remark,v.reference,v.batch_no,sup.trader_name sup_name,\n" +
                "t.user_code,t.trader_name,t.address,v.s_user_code,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt\n" +
                "from v_sale v join trader t\n" + "on v.trader_code = t.code\n" + "left join grn g\n" +
                "on v.batch_no = g.batch_no\n" + "and v.comp_code = g.comp_code\n" + "left join trader sup\n" +
                "on g.trader_code = sup.code\n" + "and g.comp_code = sup.comp_code\n" + "where v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" + "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.credit_term) between '" + fromDueDate + "' and '" + toDueDate + "'\n" +
//                "and v.project_no is not null\n" + "" +
                filter +
                "order by v.credit_term,v.vou_date,v.unique_id";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale s = VSale.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setCreditTerm(Util1.toDateStr(rs.getDate("credit_term"), "dd/MM/yyyy"));
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
                s.setQty(rs.getDouble("qty"));
                s.setSaleUnit(rs.getString("sale_unit"));
                s.setSalePrice(rs.getDouble("sale_price"));
                s.setSaleAmount(rs.getDouble("sale_amt"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setPaid(rs.getDouble("paid"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VOrder> getOrderByDueDate(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
                                          String brandCode, String catCode, String locCode, String batchNo, String compCode,
                                          Integer deptId, Integer macId) throws Exception {
        String filter = "";
        if (!fromDueDate.equals("-") && !toDueDate.equals("-")) {
            filter += "and date(credit_term) between '" + fromDueDate + "' and '" + toDueDate + "'\n";
        }
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
        if (!locCode.equals("-")) {
            filter += "and loc_code='" + locCode + "'\n";
        }
        List<VOrder> list = new ArrayList<>();
        String sql = "select a.*,t.trader_name\n" +
                "from (select vou_date,credit_term,vou_no,trader_code,vou_total,comp_code\n" +
                "from order_his\n" +
                "where deleted = false\n" + filter +
                "and comp_code ='" + compCode + "'\n" +
                "and cur_code ='" + curCode + "'\n" +
                "and dept_id = " + deptId + "\n" +
                ")a\n" +
                "join trader t on a.trader_code = t.code\n" +
                "and a.comp_code = t.comp_code\n" +
                "order by credit_term,vou_date,vou_no";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOrder s = new VOrder();
                s.setCreditTerm(Util1.toDateStr(rs.getDate("credit_term"), "dd/MM/yyyy"));
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setTraderName(rs.getString("trader_name"));
                s.setVouTotal(rs.getDouble("vou_total"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VOrder> getOrderByDueDateDetail(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
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
            filter += "and v.batch_no='" + batchNo + "'\n";
        }
        List<VOrder> list = new ArrayList<>();
        String sql = "select v.credit_term,v.vou_date,v.vou_no,v.vou_total,v.paid,v.remark,v.reference,sup.trader_name sup_name,\n" +
                "t.user_code,t.trader_name,t.address,v.user_code s_user_code,v.stock_name,v.qty,v.unit,v.price,v.amt\n" +
                "from v_order v join trader t\n" + "on v.trader_code = t.code\n" +
                "left join grn g\n" +
//                "on v.batch_no = g.batch_no\n" +
                "on v.comp_code = g.comp_code\n" + "left join trader sup\n" +
                "on g.trader_code = sup.code\n" + "and g.comp_code = sup.comp_code\n" + "where v.deleted = false\n" +
                "and v.comp_code = '" + compCode + "'\n" + "and v.cur_code = '" + curCode + "'\n" +
                "and date(v.credit_term) between '" + fromDueDate + "' and '" + toDueDate + "'\n" +
//                "and v.project_no is not null\n" + "" +
                filter +
                "order by v.credit_term,v.vou_date,v.unique_id";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VOrder s = new VOrder();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setCreditTerm(Util1.toDateStr(rs.getDate("credit_term"), "dd/MM/yyyy"));
                s.setVouNo(rs.getString("vou_no"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
                s.setTraderCode((rs.getString("user_code")));
                s.setTraderName(rs.getString("trader_name"));
                s.setCusAddress(rs.getString("address"));
                s.setStockUserCode(rs.getString("s_user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setQty(rs.getFloat("qty"));
                s.setSaleUnit(rs.getString("unit"));
                s.setSalePrice(rs.getFloat("price"));
                s.setSaleAmount(rs.getFloat("amt"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setPaid(rs.getDouble("paid"));
                list.add(s);
            }
        }
        return list;
    }

    @Override
    public List<VSale> getSaleSummaryByDepartment(String fromDate, String toDate, String compCode) {
        List<VSale> list = new ArrayList<>();
        String sql = """
                select sum(vou_total) vou_total,sum(vou_balance) vou_balance,sum(paid) paid,cur_code,dept_id,count(*) vou_count
                from sale_his
                where date(vou_date) between ? and ?
                and deleted = false
                and comp_code =?
                group by dept_id,cur_code""";
        try {
            ResultSet rs = getResult(sql, fromDate, toDate, compCode);
            while (rs.next()) {
                VSale s = VSale.builder().build();
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setVouBalance(rs.getDouble("vou_balance"));
                s.setPaid(rs.getDouble("paid"));
                s.setDeptId(rs.getInt("dept_id"));
                s.setVouCount(rs.getInt("vou_count"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error("getSaleSummaryByDepartment : " + e.getMessage());
        }
        return list;
    }


    @Override
    public List<VOrder> getOrderSummaryByDepartment(String fromDate, String toDate, String compCode) {
        List<VOrder> list = new ArrayList<>();
        String sql = """
                select sum(vou_total) vou_total,cur_code,dept_id,count(*) vou_count
                from order_his
                where date(vou_date) between ? and ?
                and deleted = false
                and comp_code =?
                group by dept_id,cur_code""";
        try {
            ResultSet rs = getResult(sql, fromDate, toDate, compCode);
            while (rs.next()) {
                VOrder s = new VOrder();
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setDeptId(rs.getInt("dept_id"));
                s.setVouCount(rs.getInt("vou_count"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error("getOrderSummaryByDepartment : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByBatchReport(String vouNo, String grnVouNo, String compCode) {
        List<VSale> list = new ArrayList<>();
        String sql = """
                select 'I' group_name,user_code,stock_name,qty,unit,weight,weight_unit,0 price,0 amount,qty*weight ttl_qty\s
                from v_grn\s
                where vou_no =?\s
                and comp_code =?\s
                union all\s
                select 'R',s_user_code,stock_name,qty,sale_unit,weight,weight_unit,sale_price,sale_amt,qty*weight ttl_qty\s
                from v_sale
                where vou_no =?\s
                and comp_code =?;""";
        try {
            ResultSet rs = getResult(sql, grnVouNo, compCode, vouNo, compCode);
            while (rs.next()) {
                //group_name, user_code, stock_name, qty, unit, weight,
                // weight_unit, price, amount, ttl_qty, ttl
                VSale s = VSale.builder().build();
                s.setGroupName(rs.getString("group_name"));
                s.setStockUserCode(rs.getString("user_code"));
                s.setStockName(rs.getString("stock_name"));
                s.setQty(Util1.toNull(rs.getDouble("qty")));
                s.setSaleUnit(rs.getString("unit"));
                s.setWeight(Util1.toNull(rs.getDouble("weight")));
                s.setWeightUnit(rs.getString("weight_unit"));
                s.setSalePrice(Util1.toNull(rs.getDouble("price")));
                s.setSaleAmount(Util1.toNull(rs.getDouble("amount")));
                s.setTotalQty(Util1.toNull(rs.getDouble("ttl_qty")));
                list.add(s);
            }
        } catch (Exception e) {
            log.error("getSaleByBatchReport : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<ClosingBalance> getStockInOutSummaryByWeight(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode,
                                                             String stockCode, String vouTypeCode, boolean calSale, boolean calPur, boolean calRI, boolean calRO,
                                                             boolean calMill, String compCode, Integer deptId, Integer macId) {
        calculateOpeningByWeight(opDate, fromDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, deptId, macId);
        calculateClosingByWeight(fromDate, toDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, deptId, macId);
        String getSql = "select a.*,sum(a.op_qty+a.pur_qty+a.in_qty+a.out_qty+a.sale_qty) bal_qty,\n" +
                "sum(a.op_weight+a.pur_weight+a.in_weight+a.out_weight+a.sale_weight) bal_weight, \n" +
                "s.weight_unit,s.pur_unit,s.user_code s_user_code,s.stock_name,st.user_code st_user_code,st.stock_type_name\n" +
                "from (select stock_code,loc_code,sum(op_qty) op_qty,sum(pur_qty) pur_qty,\n" +
                "sum(in_qty) in_qty,sum(out_qty) out_qty,sum(sale_qty) sale_qty,comp_code,\n" +
                "sum(op_weight) op_weight,sum(pur_weight) pur_weight,\n" +
                "sum(in_weight) in_weight,sum(out_weight) out_weight,sum(sale_weight) sale_weight \n" +
                "from tmp_stock_io_column\n" +
                "where mac_id = " + macId + "\n" +
                "group by stock_code)a\n" +
                "join stock s on a.stock_code = s.stock_code\n" +
                "and a.comp_code = s.comp_code\n" +
                "join stock_type st on s.stock_type_code = st.stock_type_code\n" +
                "and s.comp_code = st.comp_code\n" +
                "group by a.stock_code\n" +
                "order by s.user_code";
        List<ClosingBalance> balances = new ArrayList<>();
        try {
            ResultSet rs = reportDao.executeSql(getSql);
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    ClosingBalance b = ClosingBalance.builder().build();
                    double opQty = rs.getDouble("op_qty");
                    double purQty = rs.getDouble("pur_qty");
                    double inQty = rs.getDouble("in_qty");
                    double saleQty = rs.getDouble("sale_qty");
                    double outQty = rs.getDouble("out_qty");
                    double balQty = rs.getDouble("bal_qty");
                    double opWeight = rs.getDouble("op_Weight");
                    double purWeight = rs.getDouble("pur_Weight");
                    double inWeight = rs.getDouble("in_Weight");
                    double saleWeight = rs.getDouble("sale_Weight");
                    double outWeight = rs.getDouble("out_Weight");
                    double balWeight = rs.getDouble("bal_Weight");

                    b.setOpenQty(opQty);
                    b.setOpenRel(opQty == 0 ? null : Util1.format(opQty));
                    b.setPurQty(purQty);
                    b.setPurRel(purQty == 0 ? null : Util1.format(purQty));
                    b.setInQty(inQty);
                    b.setInRel(inQty == 0 ? null : Util1.format(inQty));
                    b.setSaleQty(saleQty);
                    b.setSaleRel(saleQty == 0 ? null : Util1.format(saleQty));
                    b.setOutQty(outQty);
                    b.setOutRel(outQty == 0 ? null : Util1.format(outQty));
                    b.setBalQty(balQty);
                    b.setBalRel(balQty == 0 ? null : Util1.format(balQty));

                    b.setOpenWeight(opWeight);
                    b.setPurWeight(purWeight);
                    b.setInWeight(inWeight);
                    b.setSaleWeight(saleWeight);
                    b.setOutWeight(outWeight);
                    b.setBalWeight(balWeight);
                    b.setStockUsrCode(rs.getString("s_user_code"));
                    b.setStockName(rs.getString("stock_name"));
                    b.setStockCode(rs.getString("stock_code"));
                    balances.add(b);
                }
            }
        } catch (Exception e) {
            log.error("getStockInOutSummaryByWeight: " + Arrays.toString(e.getStackTrace()));
        }
        return balances;
    }

    @Override
    public VLanding getLandingReport(String vouNo, String compCode) {
        VLanding header = new VLanding();
        List<VLanding> listPrice = new ArrayList<>();
        List<VLanding> listQty = new ArrayList<>();
        String sql = """
                select a.*,t.trader_name,t.phone,r.reg_name,l.loc_name,s.stock_name,s1.stock_name grade_stock_name,u.unit_name pur_unit_name
                from (
                select lh.vou_no,vou_date,trader_code,loc_code,gross_qty,
                price,amount,remark,cargo,lh.comp_code,lh.stock_code,lhg.stock_code grade_stock_code
                from landing_his lh join landing_his_grade lhg
                where lh.vou_no = lhg.vou_no
                and lh.comp_code = lhg.comp_code
                and lhg.choose = true
                and lh.vou_no=?
                and lh.comp_code=?
                )a
                join trader t on a.trader_code = t.code
                and t.comp_code = t.comp_code
                left join region r on t.reg_code = r.reg_code
                and t.comp_code = r.comp_code
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                join stock s on a.stock_code = s.stock_code
                and a.comp_code = s.comp_code
                left join stock_unit u on s.pur_unit = u.unit_code
                and s.comp_code = u.comp_code
                join stock s1 on a.grade_stock_code = s1.stock_code
                and a.comp_code = s1.comp_code""";
        try {
            ResultSet rs = getResult(sql, vouNo, compCode);
            //vou_no, vou_date, trader_code, loc_code, gross_qty, qty, unit, weight, total_weight, price,
            // amount, remark, cargo, comp_code, stock_code, trader_name, loc_name, stock_name
            if (rs.next()) {
                header.setVouNo(rs.getString("vou_no"));
                header.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                header.setPurPrice(rs.getDouble("price"));
                header.setPurAmt(rs.getDouble("amount"));
                header.setRemark(rs.getString("remark"));
                header.setCargo(rs.getString("cargo"));
                header.setTraderName(rs.getString("trader_name"));
                header.setRegionName(rs.getString("reg_name"));
                header.setTraderPhoneNo(rs.getString("phone"));
                header.setRemark(rs.getString("remark"));
                header.setLocName(rs.getString("loc_name"));
                header.setStockName(rs.getString("stock_name"));
                header.setGradeStockName(rs.getString("grade_stock_name"));
                header.setGrossQty(rs.getDouble("gross_qty"));
                header.setPurUnitName(rs.getString("pur_unit_name"));
            }
        } catch (Exception e) {
            log.error("getLandingReport : " + e.getMessage());
        }
        String sql2 = """
                select s.criteria_name,percent,percent_allow,price,amount
                from landing_his_price l join stock_criteria s
                on l.criteria_code = s.criteria_code
                and l.comp_code =s.comp_code
                where l.vou_no=?
                and l.comp_code=?
                and l.percent>0
                order by l.unique_id""";
        try {
            ResultSet rs = getResult(sql2, vouNo, compCode);
            while (rs.next()) {
                VLanding l = new VLanding();
                l.setCriteriaName(rs.getString("criteria_name"));
                l.setPercent(rs.getDouble("percent"));
                l.setPercentAllow(rs.getDouble("percent_allow"));
                l.setPrice(rs.getDouble("price"));
                l.setAmount(rs.getDouble("amount"));
                listPrice.add(l);
            }
        } catch (Exception e) {
            log.error("getLandingReport : " + e.getMessage());
        }
        String sql3 = """
                select s.criteria_name,percent,percent_allow,qty,total_qty
                from landing_his_qty l join stock_criteria s
                on l.criteria_code = s.criteria_code
                and l.comp_code =s.comp_code
                where l.vou_no=?
                and l.comp_code=?
                and l.percent>0
                order by l.unique_id""";
        try {
            ResultSet rs = getResult(sql3, vouNo, compCode, vouNo, compCode);
            while (rs.next()) {
                VLanding l = new VLanding();
                l.setCriteriaName(rs.getString("criteria_name"));
                l.setPercent(rs.getDouble("percent"));
                l.setPercentAllow(rs.getDouble("percent_allow"));
                l.setQty(rs.getDouble("qty"));
                l.setTotalQty(rs.getDouble("total_qty"));
                l.setGrossQty(header.getGrossQty());
                l.setUnitName(header.getUnitName());
                listQty.add(l);
            }
        } catch (Exception e) {
            log.error("getLandingReport : " + e.getMessage());
        }
        if (!listQty.isEmpty()) {
            header.setWetPercent(listQty.getFirst().getPercent());
        }
        header.setListPrice(listPrice);
        header.setListQty(listQty);
        return header;
    }

    @Override
    public List<ClosingBalance> getStockPayableByTrader(String opDate, String fromDate, String toDate,
                                                        String traderCode, String stockCode, String compCode,
                                                        int macId, boolean summary) {
        calculateOpeningByTrader(opDate, fromDate, traderCode, stockCode, compCode, macId);
        calculateClosingByTrader(fromDate, toDate, traderCode, stockCode, compCode, macId);
        List<ClosingBalance> list = new ArrayList<>();
        if (summary) {
            String sql = """
                    select a.*,sum(a.op_qty+a.out_qty+a.sale_qty) bal_qty,
                    sum(a.op_weight+a.out_weight+a.sale_weight) bal_weight,
                    s.weight_unit,s.user_code s_user_code,a.stock_code,s.stock_name,
                    t.user_code t_user_code,t.trader_name
                    from (
                    select tran_option,tran_date,stock_code,trader_code,
                    remark,vou_no,comp_code,dept_id,
                    sum(op_qty) op_qty,sum(op_weight) op_weight,sum(out_qty) out_qty,sum(out_weight) out_weight,
                    sum(sale_qty) sale_qty,sum(sale_weight) sale_weight
                    from tmp_stock_io_column
                    where mac_id = ?
                    and comp_code = ?
                    group by stock_code,trader_code)a
                    join stock s on a.stock_code = s.stock_code
                    and a.comp_code = s.comp_code
                    join trader t on a.trader_code = t.code
                    and a.comp_code = t.comp_code
                    group by stock_code,trader_code
                    order by t.user_code,a.tran_option,a.tran_date,a.vou_no
                    """;
            ResultSet rs = reportDao.getResultSql(sql, macId, compCode);
            try {
                while (rs.next()) {
                    ClosingBalance b = ClosingBalance.builder().build();
                    b.setStockCode(rs.getString("stock_code"));
                    b.setStockName(rs.getString("stock_name"));
                    b.setStockUsrCode(rs.getString("s_user_code"));
                    b.setTraderUserCode(rs.getString("t_user_code"));
                    b.setTraderCode(rs.getString("trader_code"));
                    b.setTraderName(rs.getString("trader_name"));
                    b.setOpenQty(rs.getDouble("op_qty"));
                    b.setOpenWeight(rs.getDouble("op_weight"));
                    b.setSaleQty(rs.getDouble("sale_qty"));
                    b.setSaleWeight(rs.getDouble("sale_weight"));
                    b.setOutQty(rs.getDouble("out_qty"));
                    b.setOutWeight(rs.getDouble("out_weight"));
                    b.setBalQty(rs.getDouble("bal_qty"));
                    b.setBalWeight(rs.getDouble("bal_weight"));
                    list.add(b);
                }
            } catch (Exception e) {
                log.error("getStockBalanceByTraderSummary : " + e.getMessage());
            }
        } else {
            String sql = """
                    select a.*,sum(a.op_qty+a.out_qty+a.sale_qty) bal_qty,
                    sum(a.op_weight+a.out_weight+a.sale_weight) bal_weight,
                    s.weight_unit,s.user_code s_user_code,a.stock_code,s.stock_name,
                    t.user_code t_user_code,t.trader_name
                    from (
                    select tran_option,tran_date,stock_code,trader_code,
                    remark,vou_no,comp_code,dept_id,
                    sum(op_qty) op_qty,sum(op_weight) op_weight,sum(out_qty) out_qty,sum(out_weight) out_weight,
                    sum(sale_qty) sale_qty,sum(sale_weight) sale_weight
                    from tmp_stock_io_column
                    where mac_id = ?
                    and comp_code = ?
                    group by tran_date,stock_code,trader_code,tran_option,vou_no)a
                    join stock s on a.stock_code = s.stock_code
                    and a.comp_code = s.comp_code
                    join trader t on a.trader_code = t.code
                    and a.comp_code = t.comp_code
                    group by tran_date,stock_code,trader_code,vou_no,tran_option
                    order by a.tran_option,a.tran_date,a.vou_no
                    """;
            try {
                ResultSet rs = reportDao.getResultSql(sql, macId, compCode);
                if (!Objects.isNull(rs)) {
                    while (rs.next()) {
                        ClosingBalance b = ClosingBalance.builder().build();
                        double opQty = rs.getDouble("op_qty");
                        double outQty = rs.getDouble("out_qty");
                        double balQty = rs.getDouble("bal_qty");
                        double saleQty = rs.getDouble("sale_qty");
                        double opWeight = rs.getDouble("op_weight");
                        double saleWeight = rs.getDouble("sale_weight");
                        double outWeight = rs.getDouble("out_weight");
                        double balWeight = rs.getDouble("bal_Weight");
                        b.setOpenQty(opQty);
                        b.setSaleQty(saleQty);
                        b.setOutQty(outQty);
                        b.setBalQty(balQty);
                        b.setOpenWeight(opWeight);
                        b.setSaleWeight(saleWeight);
                        b.setOutWeight(outWeight);
                        b.setBalWeight(balWeight);
                        b.setCompCode(compCode);
                        b.setVouDate(Util1.toDateStr(rs.getDate("tran_date"), "dd/MM/yyyy"));
                        b.setStockUsrCode(Util1.isNull(rs.getString("s_user_code"), rs.getString("stock_code")));
                        b.setStockName(rs.getString("stock_name"));
                        b.setRemark(rs.getString("remark"));
                        b.setWeightUnit(rs.getString("weight_unit"));
                        b.setVouNo(rs.getString("vou_no"));
                        list.add(b);
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        ClosingBalance prv = list.get(i - 1);
                        double prvCl = prv.getBalQty();
                        double prvWCl = prv.getBalWeight();
                        ClosingBalance c = list.get(i);
                        c.setOpenQty(prvCl);
                        c.setOpenWeight(prvWCl);
                        double opQty = c.getOpenQty();
                        double purQty = c.getPurQty();
                        double inQty = c.getInQty();
                        double outQty = c.getOutQty();
                        double saleQty = c.getSaleQty();
                        double clQty = opQty + purQty + inQty + outQty + saleQty;

                        double opWeight = c.getOpenWeight();
                        double outWeight = c.getOutWeight();
                        double saleWeight = c.getSaleWeight();
                        double clWeight = opWeight + outWeight + saleWeight;
                        c.setOpenQty(opQty);
                        c.setPurQty(purQty);
                        c.setInQty(inQty);
                        c.setSaleQty(saleQty);
                        c.setOutQty(outQty);
                        c.setBalQty(clQty);
                        c.setOpenWeight(opWeight);
                        c.setSaleWeight(saleWeight);
                        c.setOutWeight(outWeight);
                        c.setBalWeight(clWeight);
                    } else {
                        ClosingBalance c = list.get(i);
                        double opQty = c.getOpenQty();
                        double outQty = c.getOutQty();
                        double saleQty = c.getSaleQty();
                        double opWeight = c.getOpenWeight();
                        double outWeight = c.getOutWeight();
                        double saleWeight = c.getSaleWeight();
                        double clWeight = opWeight + outWeight + saleWeight;
                        double clQty = opQty + outQty + saleQty;
                        c.setOpenQty(opQty);
                        c.setSaleQty(saleQty);
                        c.setOutQty(outQty);
                        c.setBalQty(clQty);
                        c.setOpenWeight(opWeight);
                        c.setSaleWeight(saleWeight);
                        c.setOutWeight(outWeight);
                        c.setBalWeight(clWeight);
                    }
                }
            } catch (Exception e) {
                log.error(String.format("getStockInOutDetailByWeight: %s", e.getMessage()));
            }
        }
        return list;
    }

    @Override
    public List<VSale> getSaleByStockWeightSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) throws Exception {
        List<VSale> list = new ArrayList<>();
        String sql = "select a.*,u1.unit_name,u2.unit_name weight_unit_name\n" +
                "from (\n" +
                "select stock_code,s_user_code,stock_name,sum(qty) qty,sum(ifnull(total_weight,0)) total_weight,\n" +
                "sum(sale_amt) sale_amt,sale_unit,weight_unit,comp_code\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code = '" + compCode + "'\n" +
                "and deleted = false\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,weight_unit,sale_unit\n" +
                ")a\n" +
                "join stock_unit u1 on a.sale_unit = u1.unit_code\n" +
                "and a.comp_code = u1.comp_code\n" +
                "join stock_unit u2 on a.weight_unit = u2.unit_code\n" +
                "and a.comp_code = u2.comp_code\n" +
                "order by s_user_code";
        ResultSet rs = reportDao.executeSql(sql);
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                VSale p = VSale.builder().build();
                //s_user_code, stock_name, qty, total_weight, pur_unit, weight_unit, comp_code, unit_name, weight_unit_name
                p.setStockCode(rs.getString("stock_code"));
                p.setStockUserCode(rs.getString("s_user_code"));
                p.setStockName(rs.getString("stock_name"));
                p.setSaleAmount(rs.getDouble("sale_amt"));
                p.setTotalQty(rs.getDouble("qty"));
                p.setTotalWeight(rs.getDouble("total_weight"));
                p.setSaleUnitName(rs.getString("unit_name"));
                p.setWeightUnitName(rs.getString("weight_unit_name"));
                list.add(p);
            }
        }
        return list;
    }

    @Override
    public List<ClosingBalance> getStockPayableConsignor(String opDate, String fromDate, String toDate,
                                                         String traderCode, String stockCode,
                                                         String compCode, int macId, boolean summary) {
        if (summary) {
            calculateStockIOOpeningByTrader(opDate, fromDate, traderCode, compCode, macId);
            calculateStockIOClosingByTrader(fromDate, toDate, traderCode, compCode, macId);
        }
        List<ClosingBalance> list = new ArrayList<>();
        if (summary) {
            String sql = """
                    select a.*,t.user_code t_user_code,t.trader_name,s.user_code s_user_code,s.stock_name
                    from (
                    select trader_code,stock_code,sum(op_qty)op_qty,sum(op_weight) op_weight,
                    sum(in_qty) in_qty,sum(in_weight) in_weight,sum(out_qty) out_qty,sum(out_weight) out_weight,
                    sum(op_qty+out_qty+sale_qty) bal_qty,
                    sum(op_weight+out_weight+in_weight) bal_weight,comp_code
                    from tmp_stock_io_column
                    where mac_id =?
                    and comp_code =?
                    group by trader_code,stock_code
                    )a
                    join trader t on a.trader_code = t.code
                    and a.comp_code =t.comp_code
                    join stock s on a.stock_code = s.stock_code
                    and a.comp_code = t.comp_code
                    """;
            ResultSet rs = reportDao.getResultSql(sql, macId, compCode);
            try {
                while (rs.next()) {
                    ClosingBalance b = ClosingBalance.builder().build();
                    b.setStockCode(rs.getString("stock_code"));
                    b.setStockName(rs.getString("stock_name"));
                    b.setStockUsrCode(rs.getString("s_user_code"));
                    b.setTraderUserCode(rs.getString("t_user_code"));
                    b.setTraderCode(rs.getString("trader_code"));
                    b.setTraderName(rs.getString("trader_name"));
                    b.setOpenQty(rs.getDouble("op_qty"));
                    b.setOpenWeight(rs.getDouble("op_weight"));
                    b.setInQty(rs.getDouble("in_qty"));
                    b.setInWeight(rs.getDouble("in_weight"));
                    b.setOutQty(rs.getDouble("out_qty"));
                    b.setOutWeight(rs.getDouble("out_weight"));
                    b.setBalQty(rs.getDouble("bal_qty"));
                    b.setBalWeight(rs.getDouble("bal_weight"));
                    list.add(b);
                }
            } catch (Exception e) {
                log.error("getStockPayableConsignorSummary : " + e.getMessage());
            }
        } else {
            String sql = """
                    select a.*,sum(a.op_qty+a.out_qty+a.in_qty) bal_qty,
                    sum(a.op_weight+a.out_weight+a.in_weight) bal_weight,
                    s.weight_unit,s.user_code s_user_code,a.stock_code,s.stock_name,
                    t.user_code t_user_code,t.trader_name
                    from (
                    select tran_option,tran_date,stock_code,trader_code,
                    remark,vou_no,comp_code,dept_id,
                    sum(op_qty) op_qty,sum(op_weight) op_weight,
                    sum(out_qty) out_qty,sum(out_weight) out_weight,
                    sum(in_qty) in_qty,sum(in_weight) in_weight
                    from tmp_stock_io_column
                    where mac_id = ?
                    and comp_code = ?
                    and stock_code =?
                    and trader_code =?
                    group by tran_date,stock_code,trader_code,tran_option,vou_no)a
                    join stock s on a.stock_code = s.stock_code
                    and a.comp_code = s.comp_code
                    join trader t on a.trader_code = t.code
                    and a.comp_code = t.comp_code
                    group by tran_date,stock_code,trader_code,vou_no,tran_option
                    order by a.tran_option,a.tran_date,a.vou_no
                    """;
            try {
                ResultSet rs = reportDao.getResultSql(sql, macId, compCode, stockCode, traderCode);
                if (!Objects.isNull(rs)) {
                    while (rs.next()) {
                        ClosingBalance b = ClosingBalance.builder().build();
                        double opQty = rs.getDouble("op_qty");
                        double outQty = rs.getDouble("out_qty");
                        double balQty = rs.getDouble("bal_qty");
                        double inQty = rs.getDouble("in_qty");
                        double opWeight = rs.getDouble("op_weight");
                        double inWeight = rs.getDouble("in_weight");
                        double outWeight = rs.getDouble("out_weight");
                        double balWeight = rs.getDouble("bal_Weight");
                        b.setOpenQty(opQty);
                        b.setInQty(inQty);
                        b.setOutQty(outQty);
                        b.setBalQty(balQty);
                        b.setOpenWeight(opWeight);
                        b.setInWeight(inWeight);
                        b.setOutWeight(outWeight);
                        b.setBalWeight(balWeight);
                        b.setCompCode(compCode);
                        b.setVouDate(Util1.toDateStr(rs.getDate("tran_date"), "dd/MM/yyyy"));
                        b.setStockUsrCode(Util1.isNull(rs.getString("s_user_code"), rs.getString("stock_code")));
                        b.setStockName(rs.getString("stock_name"));
                        b.setRemark(rs.getString("remark"));
                        b.setWeightUnit(rs.getString("weight_unit"));
                        b.setVouNo(rs.getString("vou_no"));
                        list.add(b);
                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        ClosingBalance prv = list.get(i - 1);
                        double prvCl = prv.getBalQty();
                        double prvWCl = prv.getBalWeight();
                        ClosingBalance c = list.get(i);
                        c.setOpenQty(prvCl);
                        c.setOpenWeight(prvWCl);
                        double opQty = c.getOpenQty();
                        double inQty = c.getInQty();
                        double outQty = c.getOutQty();
                        double clQty = opQty + inQty + outQty;
                        double opWeight = c.getOpenWeight();
                        double outWeight = c.getOutWeight();
                        double inWeight = c.getInWeight();
                        double clWeight = opWeight + outWeight + inWeight;
                        c.setOpenQty(opQty);
                        c.setInQty(inQty);
                        c.setOutQty(outQty);
                        c.setBalQty(clQty);
                        c.setOpenWeight(opWeight);
                        c.setOutWeight(outWeight);
                        c.setBalWeight(clWeight);
                    } else {
                        ClosingBalance c = list.get(i);
                        double opQty = c.getOpenQty();
                        double outQty = c.getOutQty();
                        double inQty = c.getInQty();
                        double opWeight = c.getOpenWeight();
                        double outWeight = c.getOutWeight();
                        double inWeight = c.getInWeight();
                        double clWeight = opWeight + outWeight + inWeight;
                        double clQty = opQty + outQty + inQty;
                        c.setOpenQty(opQty);
                        c.setOutQty(outQty);
                        c.setBalQty(clQty);
                        c.setOpenWeight(opWeight);
                        c.setInWeight(inWeight);
                        c.setOutWeight(outWeight);
                        c.setBalWeight(clWeight);
                    }
                }
            } catch (Exception e) {
                log.error(String.format("getStockPayableConsignorDetail: %s", e.getMessage()));
            }
        }
        return list;

    }

    @Override
    public List<VPurchase> getPurchaseList(String fromDate, String toDate, String compCode, String stockCode,
                                           String groupCode, String catCode, String brandCode, String locCode,
                                           String labourGroupCode) {
        List<VPurchase> list = new ArrayList<>();
        String sql = """
                select a.*,t.trader_name,l.loc_name
                from (
                select date(vou_date) vou_date,trader_code,stock_code,stock_name,loc_code,wet,rice, qty,bag,pur_price,
                wet*qty total_wet,rice*qty total_rice, vou_total,grand_total,paid,balance,comp_code,vou_no,reference
                from v_purchase
                where date(vou_date) between ? and ?
                and deleted =false
                and comp_code = ?
                and (stock_type_code= ? or '-'=?)
                and (brand_code= ? or '-'=?)
                and (category_code=? or '-'=?)
                and (loc_code= ? or '-'=?)
                and (stock_code= ? or '-'=?)
                and (labour_group_code= ? or '-'=?)
                )a
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by date(vou_date),qty desc
                """;
        try {
            ResultSet rs = getResult(sql, fromDate, toDate, compCode, groupCode, groupCode, brandCode, brandCode,
                    catCode, catCode, locCode, locCode, stockCode, stockCode, labourGroupCode, labourGroupCode);
            while (rs.next()) {
                VPurchase p = VPurchase.builder().build();
                p.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                p.setVouNo(rs.getString("vou_no"));
                p.setTraderCode(rs.getString("trader_code"));
                p.setStockCode(rs.getString("stock_code"));
                p.setLocationCode(rs.getString("loc_code"));
                p.setPurPrice(rs.getDouble("pur_price"));
                p.setQty(rs.getDouble("qty"));
                p.setBag(rs.getDouble("bag"));
                p.setWet(rs.getDouble("wet"));
                p.setTotalWet(rs.getDouble("total_wet"));
                p.setRice(rs.getDouble("rice"));
                p.setTotalRice(rs.getDouble("total_rice"));
                p.setVouTotal(rs.getDouble("vou_total"));
                p.setGrandTotal(rs.getDouble("grand_total"));
                p.setPaid(rs.getDouble("paid"));
                p.setBalance(rs.getDouble("balance"));
                String reference = rs.getString("reference");
                String traderName = rs.getString("trader_name");
                p.setTraderName(Util1.isNull(reference, traderName));
                p.setLocationName(rs.getString("loc_name"));
                p.setStockName(rs.getString("stock_name"));
                p.setPurPrice(rs.getDouble("pur_price"));
                list.add(p);
                //vou_date, vou_no, trader_code, stock_code, loc_code,
                // pur_price, qty, bag, wet, rice, pur_amt, grand_total,
                // paid, balance, comp_code, trader_name, loc_name
            }
        } catch (Exception e) {
            log.error("getPurchaseList : " + e.getMessage());
        }
        return list;
    }

    @Override
    public Mono<ReturnObject> getTopPurchasePaddy(String fromDate, String toDate, String compCode, String stockCode,
                                                  String groupCode, String catCode, String brandCode, String locCode) {
        String sql = """
                select a.*,c.cat_name,round(sum(total_wet)/sum(qty),2) avg_wet,
                round(sum(total_rice)/sum(qty),2) avg_rice,
                round(sum(vou_total)/sum(qty),2) avg_price,sum(vou_total) ttl_vou_total,sum(qty) total_qty
                from (
                select category_code,stock_code,stock_name,wet,rice, qty,bag,pur_price,
                wet*qty total_wet,rice*qty total_rice, pur_amt,comp_code,vou_no,vou_total
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and deleted =false
                and comp_code = :compCode
                and (stock_type_code= :typeCode or '-'=:typeCode)
                and (brand_code= :brandCode or '-'=:brandCode)
                and (category_code=:catCode or '-'=:catCode)
                and (loc_code= :locCode or '-'=:locCode)
                and (stock_code= :stockCode or '-'=:stockCode)
                )a
                left join category c on a.category_code = c.cat_code
                and a.comp_code = c.comp_code
                group by stock_code
                order by total_qty desc
                """;
        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", groupCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("locCode", locCode)
                .bind("stockCode", stockCode)
                .map((row) -> VPurchase.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .avgWet(row.get("avg_wet", Double.class))
                        .avgPrice(row.get("avg_price", Double.class))
                        .avgRice(row.get("avg_rice",Double.class))
                        .groupName(row.get("cat_name", String.class))
                        .vouTotal(row.get("ttl_vou_total", Double.class))
                        .qty(row.get("total_qty", Double.class))
                        .build())
                .all()
                .collectList()
                .map(list -> {
                    double totalQty = list.stream()
                            .filter(v -> Objects.nonNull(v.getQty())) // Filter out null values
                            .mapToDouble(VPurchase::getQty) // Map to double
                            .sum(); // Perform the sum operation
                    if (!list.isEmpty()) {
                        list.forEach(t -> t.setQtyPercent((t.getQty() / totalQty) * 100));
                    }
                    return list;
                }).map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    @Override
    public List<VConsign> getStockIssueReceiveHistory(String fromDate, String toDate, String traderCode, String userCode, String stockCode,
                                                      String vouNo, String remark, String locCode, Integer deptId,
                                                      boolean deleted, String compCode, int transSource) {
        String filter = "";
        if (!vouNo.equals("-")) {
            filter += "and vou_no ='" + vouNo + "'\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like '" + remark + "%'\n";
        }
        if (!userCode.equals("-")) {
            filter += "and v.created_by ='" + userCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code ='" + stockCode + "'\n";
        }
        if (!traderCode.equals("-")) {
            filter += "and trader_code = '" + traderCode + "'\n";
        }
        if (!locCode.equals("-")) {
            filter += "and v.loc_code ='" + locCode + "'\n";
        }
        String sql = "select v.vou_date,v.vou_no,v.stock_code,s.stock_name ,v.remark,v.created_by," +
                "v.deleted,v.dept_id,l.loc_name loc_name,t.trader_name, v.labour_group_code,sum(bag)bag\n" +
                "from v_consign v join location l\n" +
                "on v.loc_code = l.loc_code\n" +
                "and v.comp_code = l.comp_code\n" +
                "join stock s on v.stock_code = s.stock_code\n" +
                " and v.comp_code =s.comp_code\n" +
                "left join trader t on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where v.comp_code = '" + compCode + "'\n" +
                "and v.deleted = " + deleted + "\n" +
                "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and (v.tran_source = " + transSource + " or 0 =" + transSource + ")\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + filter +
                "group by v.vou_no\n" +
                "order by v.vou_date desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VConsign> vStockIRList = new ArrayList<>();
        try {
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VConsign s = new VConsign();
                    s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    s.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                    s.setVouNo(rs.getString("vou_no"));
                    s.setStockCode(rs.getString("stock_code"));
                    s.setStockName(rs.getString("stock_name"));
                    s.setRemark(rs.getString("remark"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    s.setLocation(rs.getString("loc_name"));
                    s.setDeptId(rs.getInt("dept_id"));
                    s.setTraderName(rs.getString("trader_name"));
                    s.setBag(rs.getDouble("bag"));
                    vStockIRList.add(s);
                }
            }
        } catch (Exception e) {
            log.error("getStockIssueReceiveList : " + e.getMessage());
        }
        return vStockIRList;
    }

    @Override
    public List<VPurOrder> getPurOrderHistory(String fromDate, String toDate, String traderCode, String userCode, String stockCode,
                                              String vouNo, String remark, Integer deptId,
                                              boolean deleted, String compCode) {
        String filter = "";
        if (!vouNo.equals("-")) {
            filter += "and vou_no ='" + vouNo + "'\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like '" + remark + "%'\n";
        }
        if (!userCode.equals("-")) {
            filter += "and v.created_by ='" + userCode + "'\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code ='" + stockCode + "'\n";
        }
        if (!traderCode.equals("-")) {
            filter += "and trader_code = '" + traderCode + "'\n";
        }
        String sql = "select v.vou_date,v.vou_no,v.stock_code,s.stock_name ,v.remark,v.created_by," +
                "v.deleted,v.due_date,v.dept_id,t.trader_name \n" +
                "from v_pur_order v \n" +
                "join stock s on v.stock_code = s.stock_code\n" +
                " and v.comp_code =s.comp_code\n" +
                "left join trader t on v.trader_code = t.code\n" +
                "and v.comp_code = t.comp_code\n" +
                "where v.comp_code = '" + compCode + "'\n" +
                "and v.deleted = " + deleted + "\n" +
                "and (v.dept_id = " + deptId + " or 0 =" + deptId + ")\n" +
                "and date(v.vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + filter +
                "group by v.vou_no\n" +
                "order by v.vou_date desc\n";
        ResultSet rs = reportDao.executeSql(sql);
        List<VPurOrder> vPurOrderList = new ArrayList<>();
        try {
            if (!Objects.isNull(rs)) {
                while (rs.next()) {
                    VPurOrder s = new VPurOrder();
                    s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                    s.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                    s.setDueDateTime(Util1.toZonedDateTime(rs.getTimestamp("due_date").toLocalDateTime()));
                    s.setVouNo(rs.getString("vou_no"));
                    s.setStockCode(rs.getString("stock_code"));
                    s.setStockName(rs.getString("stock_name"));
                    s.setRemark(rs.getString("remark"));
                    s.setCreatedBy(rs.getString("created_by"));
                    s.setDeleted(rs.getBoolean("deleted"));
                    s.setDeptId(rs.getInt("dept_id"));
                    s.setTraderName(rs.getString("trader_name"));
                    vPurOrderList.add(s);
                }
            }
        } catch (Exception e) {
            log.error("getPurOrderList : " + e.getMessage());
        }
        return vPurOrderList;
    }

    private void calculateStockIOOpeningByTrader(String opDate, String fromDate, String traderCode, String compCode, int macId) {
        String delSql = "delete from tmp_stock_opening where mac_id = " + macId;
        String sql = "insert into tmp_stock_opening(tran_date,trader_code,stock_code,loc_code,ttl_qty,ttl_weight,comp_code,dept_id,mac_id)\n" +
                "select '" + opDate + "',trader_code,stock_code,loc_code,sum(qty) qty,sum(total_weight) total_weight,comp_code,1," + macId + "\n" +
                "from(\n" +
                "select trader_code,stock_code,loc_code,in_qty qty,total_weight,weight_unit,comp_code\n" +
                "from v_stock_io\n" +
                "where date(vou_date) >= '" + opDate + "' \n" +
                "and date(vou_date) <'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and coalesce(in_qty,0) <>0\n" +
                "and trader_code is not null\n" +
                "and (trader_code ='" + traderCode + "' or '-'='" + traderCode + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "group by trader_code,stock_code\n" +
                "\tunion all\n" +
                "select trader_code,stock_code,loc_code,out_qty*-1,total_weight*-1,weight_unit,comp_code\n" +
                "from v_stock_io\n" +
                "where date(vou_date) >= '" + opDate + "' \n" +
                "and date(vou_date) <'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and coalesce(out_qty,0) <>0\n" +
                "and trader_code is not null\n" +
                "and (trader_code ='" + traderCode + "' or '-'='" + traderCode + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "group by trader_code,stock_code\n" +
                ")a\n" +
                "group by trader_code,stock_code\n";
        executeSql(delSql, sql);
    }

    private void calculateStockIOClosingByTrader(String fromDate, String toDate, String traderCode, String compCode, int macId) {
        String delSql = "delete from tmp_stock_io_column where mac_id =" + macId;
        String opSql = "insert into tmp_stock_io_column(tran_option,tran_date,trader_code,vou_no,remark,stock_code,op_qty,op_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'A-Opening',tran_date,trader_code,'-','Opening',stock_code,sum(ttl_qty) ttl_qty,sum(ttl_weight) ttl_weight,loc_code,mac_id,'" + compCode + "', 1 \n" +
                "from tmp_stock_opening tmp \n" +
                "where mac_id =" + macId + "\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "group by tran_date,stock_code,trader_code,mac_id";
        String inSql = "insert into tmp_stock_io_column(tran_option,tran_date,trader_code,vou_no,remark,stock_code,in_qty,in_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'StockIn',date(vou_date) vou_date,trader_code,vou_no,remark,stock_code,sum(in_qty) qty,total_weight,loc_code," + macId + ",comp_code,1\n" +
                "from v_stock_io\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and coalesce(in_qty,0) <>0\n" +
                "and trader_code is not null\n" +
                "and (trader_code ='" + traderCode + "' or '-'='" + traderCode + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "group by trader_code,stock_code,in_unit,vou_no";
        String outSql = "insert into tmp_stock_io_column(tran_option,tran_date,trader_code,vou_no,remark,stock_code,out_qty,out_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'StockOut',date(vou_date) vou_date,trader_code,vou_no,remark,stock_code,sum(out_qty)*-1 qty,total_weight*-1,loc_code," + macId + ",comp_code,1\n" +
                "from v_stock_io\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and coalesce(out_qty,0) <>0\n" +
                "and trader_code is not null\n" +
                "and (trader_code ='" + traderCode + "' or '-'='" + traderCode + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "group by trader_code,stock_code,out_unit,vou_no";
        executeSql(delSql, opSql, inSql, outSql);
    }

    private void calculateOpeningByTrader(String opDate, String fromDate,
                                          String traderCode, String stockCode, String compCode, int macId) {
        String delSql = "delete from tmp_stock_opening where mac_id = " + macId;
        String sql = "insert into tmp_stock_opening(tran_date,trader_code,stock_code,ttl_qty,ttl_weight,loc_code,unit,comp_code,dept_id,mac_id)\n" +
                "select '" + opDate + "' op_date ,trader_code,stock_code,sum(qty) ttl_qty,sum(weight) ttl_weight,loc_code,ifnull(weight_unit,'-') weight_unit,comp_code,1," + macId + " \n" +
                "from (\n" +
                "select trader_code,stock_code,sum(total_weight) weight,sum(qty) qty,loc_code, weight_unit,comp_code\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + opDate + "' and '" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,trader_code\n" +
                "\tunion all\n" +
                "select trader_code,stock_code,sum(total_weight) weight,sum(qty) qty,loc_code, weight_unit,comp_code\n" +
                "from v_opening\n" +
                "where date(op_date) between '" + opDate + "' and '" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and tran_source = 2 \n" +
                "and (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,trader_code\n" +
                "\tunion all\n" +
                "select trader_code,stock_code,sum(total_weight)*-1 weight,sum(in_qty) qty,loc_code, weight_unit,comp_code\n" +
                "from v_stock_io\n" +
                "where date(vou_date) between '" + opDate + "' and '" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and in_qty>0\n" +
                "and trader_code is not null\n" +
                "and (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,trader_code\n" +
                ")a\n" +
                "group by stock_code,trader_code";
        executeSql(delSql, sql);
    }

    private void calculateClosingByTrader(String fromDate, String toDate,
                                          String traderCode, String stockCode, String compCode, int macId) {
        String delSql = "delete from tmp_stock_io_column where mac_id = " + macId;
        String saleSql = "insert into tmp_stock_io_column(tran_option,tran_date,trader_code,vou_no,remark,stock_code,sale_qty,sale_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'Sale',date(vou_date) vou_date,trader_code,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(total_weight) ttl_weight,loc_code," + macId + ",comp_code,1\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),vou_no,stock_code,trader_code";
        String opSql = "insert into tmp_stock_io_column(tran_option,tran_date,trader_code,vou_no,remark,stock_code,op_qty,op_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'A-Opening',tran_date,trader_code,'-','Opening',stock_code,sum(ttl_qty) ttl_qty,sum(ttl_weight) ttl_weight,loc_code,mac_id,'" + compCode + "', 1 \n" +
                "from tmp_stock_opening tmp \n" +
                "where mac_id =" + macId + "\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by tran_date,stock_code,trader_code,mac_id";
        String outSql = "insert into tmp_stock_io_column(tran_option,trader_code,tran_date,vou_no,remark,stock_code,out_qty,out_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'StockOut',trader_code,date(vou_date) vou_date,vou_no,remark,stock_code,sum(out_qty)*-1 ttl_qty,sum(total_weight)*-1 ttl_weight,loc_code," + macId + ",comp_code,1\n" +
                "from v_stock_io\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and out_qty>0\n" +
                "and trader_code is not null\n" +
                "and (trader_code ='" + traderCode + "' or '-' ='" + traderCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),vou_no,stock_code,trader_code";
        executeSql(delSql, saleSql, opSql, outSql);
    }


    @Override
    public List<VSale> getCustomerBalanceSummary(String fromDate, String toDate, String compCode,
                                                 String curCode, String traderCode, String batchNo,
                                                 String projectNo, String locCode, double creditAmt) {
        List<VSale> list = new ArrayList<>();
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code ='" + traderCode + "'\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no ='" + projectNo + "'\n";
        }

        String sql = "select *\n" +
                "from (\n" +
                "select b.*,t.user_code,t.trader_name,t.address,if(ifnull(t.credit_amt,0)=0," + creditAmt + ",t.credit_amt) credit_amt,b.vou_balance - if(ifnull(t.credit_amt,0)=0," + creditAmt + ",t.credit_amt) diff_amt\n" +
                "from (\n" +
                "select trader_code,cur_code,sum(vou_balance) vou_balance,comp_code\n" +
                "from(\n" +
                "select trader_code,cur_code,sum(vou_balance) vou_balance,comp_code\n" +
                "from sale_his \n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and cur_code ='" + curCode + "'\n" +
                "and deleted = false\n" +
                "and vou_balance>0\n" + filter +
                "group by trader_code\n" +
                "\tunion all\n" +
                "select pd.trader_code,phd.cur_code,sum(ifnull(phd.pay_amt,0))*-1 paid,pd.comp_code\n" +
                "from payment_his pd join payment_his_detail phd\n" +
                "on pd.vou_no = phd.vou_no\n" +
                "and pd.comp_code = phd.comp_code\n" +
                "where date(sale_vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and pd.tran_option ='C'\n" +
                "and pd.comp_code ='" + compCode + "'\n" +
                "and phd.cur_code ='" + curCode + "'\n" +
                "and pd.deleted = false\n" + filter +
                "group by pd.trader_code\n" +
                ")a\n" +
                "group by trader_code\n" +
                ")b\n" +
                "join trader t on b.trader_code = t.code\n" +
                "and b.comp_code = t.comp_code\n" +
                "where b.vou_balance<>0\n" +
                ")c\n" +
                "order by diff_amt desc";
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                // cur_code, vou_balance, user_code, trader_name,
                // address, credit_amt, diff_amt
                VSale s = VSale.builder().build();
                s.setUserCode(rs.getString("user_code"));
                s.setCurCode(rs.getString("cur_code"));
                s.setVouBalance(rs.getDouble("vou_balance"));
                s.setCreditAmt(rs.getDouble("credit_amt"));
                s.setDiffAmt(rs.getDouble("diff_amt"));
                s.setAddress(rs.getString("address"));
                s.setTraderName(rs.getString("trader_name"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error("getCustomerBalanceDetail : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<VSale> getSupplierBalanceSummary(String fromDate, String toDate, String compCode, String curCode, String traderCode, String batchNo, String projectNo, String locCode, double creditAmt) {
        List<VSale> list = new ArrayList<>();
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code ='" + traderCode + "'\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no ='" + projectNo + "'\n";
        }

        String sql = "select *\n" +
                "from (\n" +
                "select b.*,t.user_code,t.trader_name,t.address,if(ifnull(t.credit_amt,0)=0," + creditAmt + ",t.credit_amt) credit_amt,b.vou_balance - if(ifnull(t.credit_amt,0)=0," + creditAmt + ",t.credit_amt) diff_amt\n" +
                "from (\n" +
                "select trader_code,cur_code,sum(vou_balance) vou_balance,comp_code\n" +
                "from(\n" +
                "select trader_code,cur_code,sum(balance) vou_balance,comp_code\n" +
                "from pur_his \n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and cur_code ='" + curCode + "'\n" +
                "and deleted = false\n" +
                "and balance>0\n" + filter +
                "group by trader_code\n" +
                "\tunion all\n" +
                "select pd.trader_code,phd.cur_code,sum(ifnull(phd.pay_amt,0))*-1 paid,pd.comp_code\n" +
                "from payment_his pd join payment_his_detail phd\n" +
                "on pd.vou_no = phd.vou_no\n" +
                "and pd.comp_code = phd.comp_code\n" +
                "where date(sale_vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and pd.comp_code ='" + compCode + "'\n" +
                "and phd.cur_code ='" + curCode + "'\n" +
                "and pd.tran_option ='S'\n" +
                "and pd.deleted = false\n" + filter +
                "group by pd.trader_code\n" +
                ")a\n" +
                "group by trader_code\n" +
                ")b\n" +
                "join trader t on b.trader_code = t.code\n" +
                "and b.comp_code = t.comp_code\n" +
                "where b.vou_balance<>0\n" +
                ")c\n" +
                "order by diff_amt desc";
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                // cur_code, vou_balance, user_code, trader_name,
                // address, credit_amt, diff_amt
                VSale s = VSale.builder().build();
                s.setUserCode(rs.getString("user_code"));
                s.setCurCode(rs.getString("cur_code"));
                s.setVouBalance(rs.getDouble("vou_balance"));
                s.setCreditAmt(rs.getDouble("credit_amt"));
                s.setDiffAmt(rs.getDouble("diff_amt"));
                s.setAddress(rs.getString("address"));
                s.setTraderName(rs.getString("trader_name"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error("getSupplierBalanceSummary : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<VSale> getCustomerBalanceDetail(String fromDate, String toDate, String compCode,
                                                String curCode, String traderCode, String batchNo,
                                                String projectNo, String locCode) {
        List<VSale> list = new ArrayList<>();
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code ='" + traderCode + "'\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no ='" + projectNo + "'\n";
        }
        String sql = "select sh.vou_date, b.vou_no, b.cur_code, sh.vou_total, sh.vou_balance, sh.remark, \n" +
                "sh.reference, b.outstanding,sh.trader_code, t.user_code, t.address, t.trader_name\n" +
                "from (\n" +
                "select vou_no,cur_code,sum(vou_balance) outstanding,comp_code\n" +
                "from (\n" +
                "select vou_no,cur_code,vou_balance,comp_code\n" +
                "from sale_his \n" +
                "where comp_code ='" + compCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false\n" +
                "and cur_code='" + curCode + "'\n" + filter +
                "and vou_balance>0\n" +
                "\tunion all\n" +
                "select phd.sale_vou_no,phd.cur_code,phd.pay_amt*-1,pd.comp_code\n" +
                "from payment_his pd join payment_his_detail phd\n" +
                "on pd.vou_no = phd.vou_no\n" +
                "and pd.comp_code = phd.comp_code\n" +
                "where pd.comp_code ='" + compCode + "'\n" +
                "and date(phd.sale_vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and pd.deleted = false\n" +
                "and pd.tran_option = 'C'\n" +
                "and phd.cur_code='" + curCode + "'\n" + filter +
                ")a\n" +
                "group by vou_no\n" +
                ")b\n" +
                "join sale_his sh\n" +
                "on b.vou_no = sh.vou_no\n" +
                "and b.comp_code = sh.comp_code\n" +
                "join trader t on sh.trader_code = t.code\n" +
                "and sh.comp_code = t.comp_code\n" +
                "where outstanding<>0\n" +
                "order by vou_date;";
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                //vou_date, vou_no, cur_code, vou_total, vou_balance,
                //  remark, reference, outstanding, user_code, address, trader_name
                VSale s = VSale.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setUserCode(rs.getString("user_code"));
                s.setTraderCode(rs.getString("trader_code"));
                s.setVouNo(rs.getString("vou_no"));
                s.setCurCode(rs.getString("cur_code"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
                s.setVouBalance(rs.getDouble("vou_balance"));
                s.setAddress(rs.getString("address"));
                s.setTraderName(rs.getString("trader_name"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error("getCustomerBalanceDetail : " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<VSale> getSupplierBalanceDetail(String fromDate, String toDate, String compCode,
                                                String curCode, String traderCode, String batchNo,
                                                String projectNo, String locCode) {
        List<VSale> list = new ArrayList<>();
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code ='" + traderCode + "'\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no ='" + projectNo + "'\n";
        }
        String sql = "select sh.vou_date, b.vou_no, b.cur_code, sh.vou_total, sh.balance, sh.remark, \n" +
                "sh.reference, b.outstanding,sh.trader_code, t.user_code, t.address, t.trader_name\n" +
                "from (\n" +
                "select vou_no,cur_code,sum(balance) outstanding,comp_code\n" +
                "from (\n" +
                "select vou_no,cur_code,balance,comp_code\n" +
                "from pur_his \n" +
                "where comp_code ='" + compCode + "'\n" +
                "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false\n" +
                "and cur_code='" + curCode + "'\n" + filter +
                "and balance>0\n" +
                "\tunion all\n" +
                "select phd.sale_vou_no,phd.cur_code,phd.pay_amt*-1,pd.comp_code\n" +
                "from payment_his pd join payment_his_detail phd\n" +
                "on pd.vou_no = phd.vou_no\n" +
                "and pd.comp_code = phd.comp_code\n" +
                "where pd.comp_code ='" + compCode + "'\n" +
                "and date(phd.sale_vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and pd.deleted = false\n" +
                "and pd.tran_option = 'S'\n" +
                "and phd.cur_code='" + curCode + "'\n" + filter +
                ")a\n" +
                "group by vou_no\n" +
                ")b\n" +
                "join pur_his sh\n" +
                "on b.vou_no = sh.vou_no\n" +
                "and b.comp_code = sh.comp_code\n" +
                "join trader t on sh.trader_code = t.code\n" +
                "and sh.comp_code = t.comp_code\n" +
                "where outstanding<>0\n" +
                "order by vou_date;";
        try {
            ResultSet rs = getResult(sql);
            while (rs.next()) {
                //vou_date, vou_no, cur_code, vou_total, vou_balance,
                //  remark, reference, outstanding, user_code, address, trader_name
                VSale s = VSale.builder().build();
                s.setVouDate(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setUserCode(rs.getString("user_code"));
                s.setTraderCode(rs.getString("trader_code"));
                s.setVouNo(rs.getString("vou_no"));
                s.setCurCode(rs.getString("cur_code"));
                s.setVouTotal(rs.getDouble("vou_total"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
                s.setVouBalance(rs.getDouble("balance"));
                s.setAddress(rs.getString("address"));
                s.setTraderName(rs.getString("trader_name"));
                list.add(s);
            }
        } catch (Exception e) {
            log.error("getSupplierBalanceDetail : " + e.getMessage());
        }
        return list;
    }

    private void insertClosingIntoColumn(Integer macId) {
        //delete tmp
        String delSql = "delete from tmp_closing_column where mac_id = " + macId;
        executeSql(delSql);
        //opening
        String opSql = "insert into tmp_closing_column(tran_option,vou_no, tran_date,stock_code,loc_code,op_qty,op_price,op_amt,op_unit,mac_id,comp_code)\n" + "select tran_option,vou_no,tran_date, stock_code,loc_code,sum(qty) ttl_qty,sum(price) ttl_price,\n" + "sum(qty)*sum(price) ttl_amt,unit,mac_id,comp_code\n" + "from tmp_inv_closing\n" + "where tran_option ='A-Opening' and mac_id = " + macId + "\n" + "group by tran_option,vou_no,tran_date,stock_code,loc_code,mac_id\n";
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

    private void insertPriceDetail(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) {
        //delete tmp
        String delSql = "delete from tmp_inv_closing where mac_id = " + macId;
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

    private void calculateClosing(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) {
        String delSql = "delete from tmp_stock_io_column where mac_id = " + macId;
        String opSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,op_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'Opening',a.tran_date,'-','A-Opening',a.stock_code,sum(smallest_qty) smallest_qty,a.loc_code,a.mac_id,'" + compCode + "'," + deptId + "\n" + "from (\n" + "select tmp.tran_date,tmp.stock_code,tmp.ttl_qty * rel.smallest_qty smallest_qty,tmp.loc_code,tmp.mac_id\n" + "from tmp_stock_opening tmp \n" + "join stock s on tmp.stock_code = s.stock_code\n" + "and tmp.comp_code = s.comp_code\n" + "join v_relation rel on s.rel_code = rel.rel_code\n" + "and tmp.comp_code = rel.comp_code\n" + "and tmp.unit = rel.unit\n" + "where tmp.mac_id =" + macId + ")a\n" + "group by tran_date,stock_code,mac_id";
        String purSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,pur_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'Purchase',a.vou_date vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code, pur_unit,rel_code,comp_code,dept_id\n" + "from v_purchase\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and (calculate = 1 and " + calPur + " = 0)\n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),vou_no,stock_code,pur_unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.pur_unit = rel.unit\n" + "group by a.vou_date ,a.stock_code,a.vou_no";
        //ret in
        String retInSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'ReturnIn',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,rel_code, unit,comp_code,dept_id\n" + "from v_return_in\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and (calculate = 1 and " + calRI + " = 0)\n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,vou_no,unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code,vou_no";
        String stockInSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'StockIn',date(a.vou_date) vou_date,vou_no,a.description,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,description,stock_code,sum(in_qty) qty,loc_code,in_unit,rel_code,comp_code,dept_id\n" + "from v_stock_io\n" + "where ifnull(in_qty,0)<>0 and in_unit is not null\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and (vou_status ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,in_unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.in_unit = rel.unit\n" + "group by a.vou_date ,a.stock_code,a.vou_no";
        String saleSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,sale_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'Sale',a.vou_date ,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code, sale_unit,rel_code,comp_code,dept_id\n" + "from v_sale\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and (calculate = 1 and " + calSale + " = 0)\n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,sale_unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.sale_unit = rel.unit\n" + "group by a.vou_date,a.stock_code,a.vou_no";
        String returnOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'ReturnOut',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code, unit,rel_code,comp_code,dept_id\n" + "from v_return_out\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and (calculate = 1 and " + calRO + " = 0)\n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code,vou_no";
        String stockOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'StockOut',a.vou_date,a.vou_no,a.description,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,description,stock_code,sum(out_qty) qty,loc_code,out_unit,rel_code,comp_code,dept_id\n" + "from v_stock_io\n" + "where ifnull(out_qty,0)<>0 and out_unit is not null\n" + "and date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and (vou_status ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,out_unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.out_unit = rel.unit\n" + "group by vou_date,a.stock_code,vou_no";
        String fFSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'Transfer-F',a.vou_date,a.vou_no,if(ifnull(a.remark,'')='','Transfer',a.remark),a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,\n" + "loc_code_from," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code_from,rel_code, unit,comp_code,dept_id\n" + "from v_transfer\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code_from in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code,vou_no";
        String tFSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'Transfer-T',a.vou_date,a.vou_no,if(ifnull(a.remark,'')='','Transfer',a.remark),a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,\n" + "loc_code_to," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code_to,rel_code, unit\n" + "from v_transfer\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" + "and loc_code_to in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code,vou_no";
        String pIn = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'P-IN',a.end_date ,a.vou_no,v.description,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(end_date) end_date,vou_no,pt_code,stock_code,sum(qty) qty,loc_code, unit,rel_code,comp_code,dept_id\n" + "from v_process_his\n" + "where date(end_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1\n" + "and finished = 1\n" + "and comp_code ='" + compCode + "'\n" + "and (pt_code ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(end_date),stock_code,unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.unit = rel.unit\n" + "join vou_status v on a.pt_code = v.code\n" + "and a.comp_code = v.comp_code\n" + "group by a.end_date,a.stock_code,a.vou_no";
        String pOut = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'P-OUT',a.vou_date ,a.vou_no,v.description,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,pt_code,stock_code,sum(qty) qty,loc_code, unit,rel_code,comp_code,dept_id\n" + "from v_process_his_detail\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and calculate = 1\n" + "and comp_code ='" + compCode + "'\n" + "and (pt_code ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.unit = rel.unit\n" + "join vou_status v on a.pt_code = v.code\n" + "and a.comp_code = v.comp_code\n" + "group by a.vou_date,a.stock_code,a.vou_no";
        String mRawSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,loc_code,mac_id,comp_code,dept_id)\n" + "select 'UM-RAW',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty)*-1 smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code, unit,rel_code,comp_code,dept_id\n" + "from v_milling_raw\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and (calculate = 1 and " + calRO + " = 0)\n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,unit,vou_no)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code,vou_no";
        String mOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'UM-OUTPUT',a.vou_date,a.vou_no,a.remark,a.stock_code,sum(a.qty * rel.smallest_qty) smallest_qty,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" + "from (\n" + "select date(vou_date) vou_date,vou_no,remark,stock_code,sum(qty) qty,loc_code,rel_code, unit,comp_code,dept_id\n" + "from v_milling_output\n" + "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" + "and deleted = 0 \n" + "and (calculate = 1 and " + calRI + " = 0)\n" + "and comp_code ='" + compCode + "'\n" + "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" + "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" + "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" + "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" + "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" + "group by date(vou_date),stock_code,vou_no,unit)a\n" + "join v_relation rel on a.rel_code = rel.rel_code\n" + "and a.comp_code = rel.comp_code\n" + "and a.unit = rel.unit\n" + "group by vou_date,stock_code,vou_no";
        try {
            reportDao.executeSql(delSql, opSql, purSql, retInSql, stockInSql, stockOutSql, saleSql, returnOutSql, fFSql, tFSql, pIn, pOut, mRawSql, mOutSql);
        } catch (Exception e) {
            log.error(String.format("calculateClosing: %s", e.getMessage()));
        }
    }

    private void calculateOpening(String opDate, String fromDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) {
        //delete tmp
        String delSql = "delete from tmp_stock_opening where mac_id = " + macId;
        //opening
        String opSql = "insert into tmp_stock_opening(tran_date,stock_code,ttl_qty,loc_code,unit,comp_code,dept_id,mac_id)\n" +
                "select '" + fromDate + "' op_date ,stock_code,sum(qty) ttl_qty,loc_code,unit,'" + compCode + "'," + deptId + "," + macId + " \n" +
                "from (\n" + "select stock_code,sum(qty) qty,loc_code, unit\n" +
                "from v_opening\n" +
                "where date(op_date) = '" + opDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and tran_source =1\n" +
                "and deleted = 0 \n" + "and calculate = 1 \n" +
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
                "and deleted = 0 \n" +
                "and (calculate = 1 and " + calPur + "=0) \n" + "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,pur_unit\n"
                + "\tunion all\n" +
                "select stock_code,sum(qty) qty,loc_code, unit\n" +
                "from v_return_in\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = 0 \n" + "and (calculate = 1 and " + calRI + "=0) \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty) qty,loc_code, unit\n" +
                "from v_milling_output\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = 0 \n" + "and (calculate = 1 and " + calRI + "=0) \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(in_qty) qty,loc_code, in_unit\n" +
                "from v_stock_io\n" + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = 0\n" + "and calculate = 1 \n" +
                "and in_qty is not null and in_unit is not null\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (vou_status ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" +
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
                "and deleted = 0\n" + "and calculate = 1 \n" +
                "and out_qty is not null and out_unit is not null\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and (vou_status ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" +
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
                "and deleted = false \n" + "and (calculate = 1 and " + calRO + "=0) \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty)*-1 qty,loc_code, unit\n" +
                "from v_milling_raw\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = false \n" + "and (calculate = 1 and " + calRO + "=0) \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty)*-1 qty,loc_code, sale_unit\n" +
                "from v_sale\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = 0 \n" + "and (calculate = 1 and " + calSale + "=0) \n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,sale_unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty)*-1 qty,loc_code_from, unit\n" +
                "from v_transfer\n" + "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" +
                "and loc_code_from in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty) qty,loc_code_to, unit\n" +
                "from v_transfer\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = 0 \n" + "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" +
                "and loc_code_to in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty)*-1 qty,loc_code, unit\n" +
                "from v_process_his_detail\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" +
                "and (pt_code ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                "\tunion all\n" +
                "select stock_code,sum(qty) qty,loc_code, unit\n" +
                "from v_process_his\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and deleted = false\n" + "and (pt_code ='" + vouStatus + "' or '-'='" + vouStatus + "')\n" +
                "and calculate = 1 \n" + "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code,unit\n" +
                ")a\n" +
                "group by stock_code,unit";
        try {
            reportDao.executeSql(delSql, opSql);
        } catch (Exception e) {
            log.error(String.format("calculateOpening: %s", e.getMessage()));
        }
    }

    private void calculateClosingByWeight(String fromDate, String toDate, String typeCode, String catCode, String brandCode,
                                          String stockCode, boolean calSale, String compCode, Integer deptId, Integer macId) {
        String delSql = "delete from tmp_stock_io_column where mac_id = " + macId;
        String opSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,op_qty,op_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'A-Opening',tran_date,'-','Opening',stock_code,sum(ttl_qty) ttl_qty,sum(ttl_weight) ttl_weight,loc_code,mac_id,'" + compCode + "'," + deptId + "\n" +
                "from tmp_stock_opening tmp \n" +
                "where mac_id =" + macId + "\n" +
                "group by tran_date,stock_code,mac_id";
        String purSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,pur_qty,pur_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'Purchase',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(total_weight) ttl_weight,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from v_purchase\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),vou_no,stock_code";
        String retInSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,in_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'ReturnIn',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(total_weight) ttl_weight,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from v_return_in\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),vou_no,stock_code";
        String retOutSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,out_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'ReturnOut',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(total_weight) ttl_weight,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from v_return_out\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),vou_no,stock_code";
        String saleSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,sale_qty,sale_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'Sale',vou_date vou_date,vou_no,remark,stock_code,sum(qty)*-1 ttl_qty,sum(total_weight)*-1 ttl_weight,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from v_sale\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),vou_no,stock_code";
        String tfSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,out_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'Transfer-F',vou_date vou_date,vou_no,remark,stock_code,sum(qty)*-1 ttl_qty,sum(total_weight)*-1 ttl_weight,loc_code_from," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from v_transfer\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code_from in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),vou_no,stock_code";
        String ttSql = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,in_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'Transfer-T',vou_date vou_date,vou_no,remark,stock_code,sum(qty) ttl_qty,sum(total_weight) ttl_weight,loc_code_to," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from v_transfer\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code_to in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by date(vou_date),vou_no,stock_code";
        String stockIn = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,in_qty,in_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'StockIn',vou_date vou_date,vou_no,remark,stock_code,sum(in_qty) ttl_qty,sum(total_weight) ttl_weight,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from v_stock_io\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and in_qty>0\n" +
                "group by date(vou_date),vou_no,stock_code";
        String stockOut = "insert into tmp_stock_io_column(tran_option,tran_date,vou_no,remark,stock_code,out_qty,out_weight,loc_code,mac_id,comp_code,dept_id)\n" +
                "select 'StockOut',vou_date vou_date,vou_no,remark,stock_code,sum(out_qty)*-1 ttl_qty,sum(total_weight)*-1 ttl_weight,loc_code," + macId + ",'" + compCode + "'," + deptId + "\n" +
                "from v_stock_io\n" +
                "where date(vou_date) between '" + fromDate + "' and '" + toDate + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + ")\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and out_qty>0\n" +
                "group by date(vou_date),vou_no,stock_code";
        try {
            reportDao.executeSql(delSql, opSql, purSql, retInSql, saleSql,
                    retOutSql, tfSql, ttSql, stockIn, stockOut);
        } catch (Exception e) {
            log.error(String.format("calculateClosingByWeight: %s", e.getMessage()));
        }
    }


    private void calculateOpeningByWeight(String opDate, String fromDate, String typeCode,
                                          String catCode, String brandCode,
                                          String stockCode, boolean calSale,
                                          String compCode, Integer deptId, Integer macId) {
        //delete tmp
        String delSql = "delete from tmp_stock_opening where mac_id = " + macId;
        //opening
        String opSql = "insert into tmp_stock_opening(tran_date,stock_code,ttl_qty,ttl_weight,loc_code,unit,comp_code,dept_id,mac_id)\n" +
                "select '" + opDate + "' op_date ,stock_code,sum(qty) ttl_qty,sum(weight) ttl_weight,loc_code,ifnull(weight_unit,'-') weight_unit,'" + compCode + "'," + deptId + "," + macId + " \n" +
                "from (\n" +
                "select stock_code,sum(total_weight) weight,sum(qty) qty,loc_code, weight_unit\n" +
                "from v_opening\n" +
                "where date(op_date) = '" + opDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and calculate = true \n" +
                "and tran_source = 1 \n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,sum(total_weight) weight,sum(qty) qty,loc_code, weight_unit\n" +
                "from v_purchase\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and calculate = true \n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,sum(total_weight) weight,sum(qty) qty,loc_code, weight_unit\n" +
                "from v_return_in\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and calculate = true \n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,sum(total_weight)*-1 weight,sum(qty)*-1 qty,loc_code, weight_unit\n" +
                "from v_return_out\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,sum(total_weight)*-1 weight,sum(qty)*-1 qty,loc_code, weight_unit\n" +
                "from v_sale\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and (calculate = true and " + calSale + "=false) \n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (cat_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,sum(total_weight)*-1 weight,sum(qty)*-1 qty,loc_code_from, weight_unit\n" +
                "from v_transfer\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and loc_code_from in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,sum(total_weight) weight,sum(qty) qty,loc_code_to, weight_unit\n" +
                "from v_transfer\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and calculate = true\n" +
                "and loc_code_to in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,sum(total_weight) weight,sum(in_qty) qty,loc_code, weight_unit\n" +
                "from v_stock_io\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and in_qty>0\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,sum(total_weight)*-1 weight,sum(out_qty)*-1 qty,loc_code, weight_unit\n" +
                "from v_stock_io\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "and out_qty>0\n" +
                "group by stock_code\n" +
                "\tunion all\n" +
                "select stock_code,0 weight,sum(qty)*-1 qty,loc_code, '-' weight_unit\n" +
                "from v_milling_usage\n" +
                "where date(vou_date) >= '" + opDate + "' and date(vou_date)<'" + fromDate + "'\n" +
                "and comp_code ='" + compCode + "'\n" +
                "and deleted = false \n" +
                "and loc_code in (select f_code from f_location where mac_id =  " + macId + " )\n" +
                "and (stock_type_code = '" + typeCode + "' or '-' = '" + typeCode + "')\n" +
                "and (brand_code = '" + brandCode + "' or '-' = '" + brandCode + "')\n" +
                "and (category_code = '" + catCode + "' or '-' = '" + catCode + "')\n" +
                "and (stock_code = '" + stockCode + "' or '-' = '" + stockCode + "')\n" +
                "group by stock_code\n" +
                ")a\n" +
                "group by stock_code";
        reportDao.executeSql(delSql, opSql);
    }

    @Override
    public void insertTmp(List<String> listStr, Integer macId, String taleName, String warehouse) {
        try {
            deleteTmp(taleName, macId);
            if (listStr != null) {
                listStr.removeIf(s -> s.equals("-"));
            }
            if (listStr == null || listStr.isEmpty() || !warehouse.equals("-")) {
                String sql = "insert into " + taleName + "(f_code,mac_id)\n" + "select loc_code," + macId + " mac_id from location " +
                        " where warehouse_code = '" + warehouse + "' or '-' = '" + warehouse + "'";
                executeSql(sql);
            } else {
                for (String str : listStr) {
                    if (str != null && str.length() > 2) {
                        String sql = "insert into " + taleName + "(f_code,mac_id)\n" + "select '" + str + "'," + macId;
                        executeSql(sql);
                    }
                }
            }
        } catch (Exception e) {
            log.error(String.format("insertTmp: %s", e.getMessage()));
        }
    }

    private void deleteTmp(String tableName, Integer macId) {
        String delSql = "delete from " + tableName + " where mac_id =" + macId;
        executeSql(delSql);
    }

    private void calculatePrice(String toDate, String opDate, String stockCode,
                                String typeCode, String catCode, String brandCode,
                                String compCode, Integer macId) {
        try {
            String filter = "";
            if (!stockCode.equals("-")) {
                filter += "and stock_code ='" + stockCode + "'\n";
            }
            if (!typeCode.equals("-")) {
                filter += "and stock_type_code ='" + typeCode + "'\n";
            }
            if (!brandCode.equals("-")) {
                filter += "and brand_code ='" + brandCode + "'\n";
            }
            if (!catCode.equals("-")) {
                filter += "and category_code ='" + catCode + "'\n";
            }
            String delSql = "delete from tmp_stock_price where mac_id = " + macId;
            String purSql = "insert into tmp_stock_price(tran_option,stock_code,pur_avg_price,mac_id)\n" +
                    "select 'PUR-AVG',stock_code,avg(small_price)," + macId + "\n" +
                    "from (\n" +
                    "select 'PUR-AVG',pur.stock_code,(pur.pur_price/rel.smallest_qty) small_price\n" +
                    "from v_purchase pur\n" +
                    "join v_relation rel\n" +
                    "on pur.rel_code = rel.rel_code\n" +
                    "and pur.comp_code = rel.comp_code\n" +
                    "and pur.pur_unit = rel.unit\n" +
                    "where deleted = false\n" +
                    "and date(vou_date) <='" + toDate + "'\n" +
                    "and pur.comp_code ='" + compCode + "'\n" + filter +
                    "group by pur.stock_code,small_price\n" +
                    "\tunion all\n" +
                    "select 'OP',op.stock_code,(op.price/rel.smallest_qty) small_price\n" +
                    "from v_opening op\n" +
                    "join v_relation rel\n" +
                    "on op.rel_code = rel.rel_code\n" +
                    "and op.comp_code = rel.comp_code\n" +
                    "and op.unit = rel.unit\n" +
                    "where op.price > 0\n" +
                    "and deleted = false\n" +
                    "and date(op_date) <='" + toDate + "'\n" +
                    "and op.comp_code ='" + compCode + "'\n" + filter +
                    "group by op.stock_code,small_price)a\n" +
                    "group by stock_code";
            String sInSql = "insert into tmp_stock_price(tran_option,stock_code,in_avg_price,mac_id)\n" +
                    "select 'SIN-AVG',stock_code,avg(small_price)," + macId + "\n" +
                    "from(\n" +
                    "select 'SIN-AVG',a.stock_code,(a.cost_price/rel.smallest_qty) small_price\n" +
                    "from (\n" +
                    "select stock_code,cost_price,ifnull(in_unit,out_unit) unit,comp_code,rel_code\n" +
                    "from v_stock_io\n" +
                    "where cost_price >0\n" +
                    "and deleted = false\n" +
                    "and date(vou_date) <='" + toDate + "'\n" +
                    "and comp_code ='" + compCode + "'\n" + filter +
                    "group by stock_code,cost_price,ifnull(in_unit,out_unit)\n" +
                    ")a\n" +
                    "join v_relation rel on a.rel_code =rel.rel_code\n" +
                    "and a.unit =rel.unit\n" +
                    "and a.comp_code =rel.comp_code\n" +
                    "group by stock_code,small_price\n" +
                    "\tunion all\n" +
                    "select 'OP',op.stock_code,(op.price/rel.smallest_qty) small_price\n" +
                    "from v_opening op\n" +
                    "join v_relation rel\n" +
                    "on op.rel_code = rel.rel_code\n" +
                    "and op.comp_code = rel.comp_code\n" +
                    "and op.unit = rel.unit\n" +
                    "where op.price > 0\n" +
                    "and op.deleted = false\n" +
                    "and date(op_date) ='" + opDate + "'\n" +
                    "and op.comp_code ='" + compCode + "'\n" + filter +
                    "group by op.stock_code,small_price\n" +
                    ")a\n" +
                    "group by stock_code";
            String purRecentSql = "insert into tmp_stock_price(stock_code,tran_option,pur_recent_price,mac_id)\n" +
                    "select a.stock_code,'PUR_RECENT',a.pur_price/rel.smallest_qty pur_price," + macId + "\n" +
                    "from (\n" + "with rows_and_position as \n" +
                    "( \n" +
                    "select stock_code, pur_price,pur_unit,row_number() over (partition by stock_code order by vou_date desc) as position,rel_code,comp_code,dept_id\n" +
                    "from v_purchase\n" +
                    "where (stock_code ='" + stockCode + "' or '-' ='" + stockCode + "')\n" +
                    "and (stock_type_code ='" + typeCode + "' or '-' ='" + typeCode + "')\n" + "    and (brand_code ='" + brandCode + "' or '-' ='" + brandCode + "')\n" + "    and (category_code ='" + catCode + "' or '-' ='" + catCode + "')\n" + "    and date(vou_date) <='" + toDate + "'\n" +
                    "and comp_code ='" + compCode + "'\n" + "    and deleted = false\n" + "  )\n" +
                    "select stock_code, pur_price,pur_unit,rel_code,comp_code,dept_id\n" +
                    "from  rows_and_position\n" +
                    "where position =1\n" + ")a\n" +
                    "join v_relation rel\n" +
                    "on a.rel_code = rel.rel_code\n" +
                    "and a.pur_unit = rel.unit\n" +
                    "and a.comp_code = rel.comp_code\n";
            String ioRecent = "insert into tmp_stock_price(stock_code,tran_option,io_recent_price,mac_id)\n" +
                    "select a.stock_code,'IO_RECENT',a.cost_price/rel.smallest_qty price," + macId + "\n" +
                    "from (\n" +
                    "with rows_and_position as \n" +
                    "  ( \n" +
                    "    select stock_code, cost_price,ifnull(in_unit,out_unit) unit,row_number() over (partition by stock_code order by vou_date desc) as position,rel_code,comp_code,dept_id\n" +
                    "    from v_stock_io\n" +
                    "    where date(vou_date) <='" + toDate + "'\n" +
                    "    and comp_code ='" + compCode + "'\n" +
                    "    and deleted = false\n" +
                    "    and cost_price>0\n" + filter +
                    "  )\n" +
                    "select stock_code, cost_price,unit,rel_code,comp_code,dept_id\n" +
                    "from  rows_and_position\n" +
                    "where position =1\n" +
                    ")a\n" +
                    "join v_relation rel\n" +
                    "on a.rel_code = rel.rel_code\n" +
                    "and a.unit = rel.unit\n" +
                    "and a.comp_code = rel.comp_code";
            reportDao.executeSql(delSql, purSql, sInSql, purRecentSql, ioRecent);
        } catch (Exception e) {
            log.error(String.format("calculatePrice: %s", e.getMessage()));
        }
    }

    @Override
    public List<MillingHis> getMillingHistory(String fromDate, String toDate, String traderCode, String vouNo, String remark, String reference, String userCode, String stockCode, String locCode,
                                              String compCode, Integer deptId, boolean deleted,
                                              String projectNo, String curCode, String jobNo) throws Exception {
        String sql = """
                select a.*,t.trader_name, v.description
                from (
                select vou_date vou_date,vou_no,remark,created_by,reference,vou_status_id, trader_code,comp_code,dept_id
                from milling_his p\s
                where comp_code = ?
                and (dept_id = ? or 0 = ?)
                and deleted =?
                and date(vou_date) between ? and ?
                and cur_code = ?
                and (vou_no = ? or '-' = ?)
                and (remark LIKE CONCAT(?, '%') or '-'= ?)
                and (reference LIKE CONCAT(?, '%') or '-'= ?)
                and (trader_code = ? or '-'= ?)
                and (created_by = ? or '-'= ?)
                and (project_no =? or '-' =?)
                and (job_no =? or '-' =?)
                group by vou_no)a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                join vou_status v on a.vou_status_id = v.code
                and a.comp_code = v.comp_code
                order by vou_date""";
        ResultSet rs = getResult(sql, compCode, deptId, deptId, deleted, fromDate, toDate, curCode, vouNo, vouNo, remark, remark, reference,
                reference, traderCode, traderCode, userCode, userCode, projectNo, projectNo, jobNo, jobNo);
        List<MillingHis> purchaseList = new ArrayList<>();
        if (!Objects.isNull(rs)) {
            while (rs.next()) {
                MillingHis s = new MillingHis();
                MillingHisKey key = new MillingHisKey();
                key.setVouNo(rs.getString("vou_no"));
                s.setKey(key);
                s.setVouDateStr(Util1.toDateStr(rs.getDate("vou_date"), "dd/MM/yyyy"));
                s.setVouDateTime(Util1.toZonedDateTime(rs.getTimestamp("vou_date").toLocalDateTime()));
                s.setTraderName(rs.getString("trader_name"));
                s.setProcessType(rs.getString("description"));
                s.setRemark(rs.getString("remark"));
                s.setReference(rs.getString("reference"));
                s.setCreatedBy(rs.getString("created_by"));
                s.setDeptId(rs.getInt("dept_id"));
                purchaseList.add(s);
            }
        }
        return purchaseList;
    }
}




