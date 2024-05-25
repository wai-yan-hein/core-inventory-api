/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.service;

import cv.api.common.*;
import cv.api.dao.ReportDao;
import cv.api.entity.*;
import cv.api.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author wai yan
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final DecimalFormat formatter = new DecimalFormat("###.##");
    private final ReportDao reportDao;
    private final DatabaseClient client;
    private final OPHisService opHisService;


    public void executeSql(String... sql) {
        reportDao.executeSql(sql);
    }


    public ResultSet getResult(String sql, Object... params) {
        return reportDao.getResultSql(sql, params);
    }


    public ResultSet getResult(String sql) {
        return reportDao.executeSql(sql);
    }


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


    public Flux<VSale> getSaleVoucher(String vouNo, String compCode) {
        String sql = """
                select t.trader_name,t.rfid,t.phone,t.address,v.remark,v.reference,v.vou_no,v.vou_date,v.stock_name,
                v.qty,v.bag,v.weight,v.weight_unit,v.sale_price,v.sale_unit,v.sale_amt,v.vou_total,v.discount,
                v.paid,v.vou_balance,t.user_code t_user_code,t.phone,t.address,l.loc_name,v.created_by,
                v.comp_code,c.cat_name,r.reg_name,u1.unit_name sale_unit_name,u2.unit_name weight_unit_name,design,size
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
                where v.vou_no = :vouNo
                and v.comp_code = :compCode""";

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    String remark = row.get("remark", String.class);
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
                    sale.setTraderCode(row.get("t_user_code", String.class));
                    sale.setTraderName(row.get("trader_name", String.class));
                    sale.setRemark(remark);
                    sale.setRefNo(refNo);
                    sale.setReference(row.get("reference", String.class));
                    sale.setPhoneNo(row.get("phone", String.class));
                    sale.setAddress(row.get("address", String.class));
                    sale.setRfId(row.get("rfid", String.class));
                    sale.setVouNo(row.get("vou_no", String.class));
                    sale.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    sale.setStockName(row.get("stock_name", String.class));
                    sale.setQty(row.get("qty", Double.class));
                    sale.setBag(row.get("bag", Double.class));
                    sale.setSalePrice(row.get("sale_price", Double.class));
                    sale.setSaleAmount(row.get("sale_amt", Double.class));
                    sale.setVouTotal(row.get("vou_total", Double.class));
                    sale.setDiscount(row.get("discount", Double.class));
                    sale.setPaid(row.get("paid", Double.class));
                    sale.setVouBalance(row.get("vou_balance", Double.class));
                    sale.setSaleUnit(row.get("sale_unit", String.class));
                    sale.setCusAddress(Util1.isNull(row.get("phone", String.class), "") + "/" + Util1.isNull(row.get("address", String.class), ""));
                    sale.setLocationName(row.get("loc_name", String.class));
                    sale.setCreatedBy(row.get("created_by", String.class));
                    sale.setCompCode(row.get("comp_code", String.class));
                    sale.setCategoryName(row.get("cat_name", String.class));
                    sale.setRegionName(row.get("reg_name", String.class));
                    sale.setSaleUnitName(row.get("sale_unit_name", String.class));
                    sale.setWeightUnitName(row.get("weight_unit_name", String.class));
                    sale.setDesign(row.get("design", String.class));
                    sale.setSize(row.get("size", String.class));
                    double weight = Util1.getDouble(row.get("weight", Double.class));
                    if (weight > 0) {
                        sale.setWeight(weight);
                        sale.setWeightUnit(row.get("weight_unit", String.class));
                    }
                    return sale;
                })
                .all();
    }

    public Flux<VPurchase> getPurchaseVoucher(String vouNo, String compCode) {
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
                where p.vou_no = :vouNo
                and p.comp_code = :compCode""";

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> {
                    VPurchase p = VPurchase.builder().build();
                    p.setTraderName(row.get("trader_name", String.class));
                    p.setRemark(row.get("remark", String.class));
                    p.setVouNo(row.get("vou_no", String.class));
                    p.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    p.setStockName(row.get("stock_name", String.class));
                    p.setQty(row.get("qty", Double.class));
                    p.setPurUnit(row.get("pur_unit", String.class));
                    p.setPurPrice(row.get("pur_price", Double.class));
                    p.setPurAmount(row.get("pur_amt", Double.class));
                    p.setVouTotal(row.get("vou_total", Double.class));
                    p.setDiscount(row.get("discount", Double.class));
                    p.setPaid(row.get("paid", Double.class));
                    p.setBalance(row.get("balance", Double.class));
                    p.setBatchNo(row.get("batch_no", String.class));
                    p.setWeight(row.get("weight", Double.class));
                    p.setWeightUnit(row.get("weight_unit", String.class));
                    p.setLabourGroupName(row.get("labour_name", String.class));
                    p.setLandVouNo(row.get("land_vou_no", String.class));
                    p.setWeightUnitName(row.get("weight_unit_name", String.class));
                    p.setPurUnitName(row.get("pur_unit_name", String.class));
                    p.setLocationName(row.get("loc_name", String.class));
                    p.setPhoneNo(row.get("phone", String.class));
                    p.setRegionName(row.get("reg_name", String.class));
                    return p;
                })
                .all();
    }


    public Flux<VPurchase> getGRNVoucher(String vouNo, String compCode) {
        String sql = """
                select a.*, t.trader_name, t.address, l.loc_name, s.stock_name
                from (
                select p.stock_code, g.vou_no, g.vou_date, p.weight, p.weight_unit, p.qty, p.unit, g.loc_code, g.trader_code, g.comp_code, g.remark, g.batch_no
                from grn g, grn_detail p
                where g.vou_no = p.vou_no
                and g.comp_code = p.comp_code
                and g.vou_no = :vouNo
                and g.comp_code = :compCode ) a
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

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> {
                    VPurchase p = VPurchase.builder().build();
                    p.setVouNo(row.get("vou_no", String.class));
                    p.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    p.setLocationName(row.get("loc_name", String.class));
                    p.setTraderName(row.get("trader_name", String.class));
                    p.setCompAddress(row.get("address", String.class));
                    p.setRemark(row.get("remark", String.class));
                    p.setBatchNo(row.get("batch_no", String.class));
                    p.setStockCode(row.get("stock_code", String.class));
                    p.setStockName(row.get("stock_name", String.class));
                    p.setQty(row.get("qty", Double.class));
                    p.setPurUnit(row.get("unit", String.class));
                    p.setWeight(row.get("weight", Double.class));
                    p.setWeightUnit(row.get("weight_unit", String.class));
                    p.setTotal(Util1.getDouble(row.get("weight", Double.class)) * Util1.getDouble(row.get("qty", Double.class)));
                    return p;
                })
                .all();
    }


    public Mono<ReturnObject> getSaleBySaleManDetail(String fromDate, String toDate, String curCode, String smCode, String stockCode, String compCode) {
        String sql = """
                SELECT v.vou_date, v.vou_no, v.saleman_code, sm.saleman_name, v.stock_name, v.qty, v.sale_unit, v.sale_price, v.sale_amt
                FROM v_sale v
                LEFT JOIN sale_man sm ON v.saleman_code = sm.saleman_code
                WHERE (v.saleman_code = :smCode OR '-' = :smCode)
                AND v.deleted = false
                AND v.comp_code = :compCode
                AND (v.stock_code = :stockCode OR '-' = :stockCode)
                AND v.cur_code = :curCode
                AND DATE(v.vou_date) BETWEEN :fromDate AND :toDate
                ORDER BY sm.saleman_name, v.vou_date, v.vou_no
                """;
        return client.sql(sql)
                .bind("smCode", smCode)
                .bind("compCode", compCode)
                .bind("stockCode", stockCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    sale.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    sale.setVouNo(row.get("vou_no", String.class));
                    sale.setSaleManCode(row.get("saleman_code", String.class));
                    sale.setSaleManName(Util1.isNull(row.get("saleman_name", String.class), "Other"));
                    sale.setStockName(row.get("stock_name", String.class));
                    sale.setQty(row.get("qty", Double.class));
                    sale.setSaleUnit(row.get("sale_unit", String.class));
                    sale.setSalePrice(row.get("sale_price", Double.class));
                    sale.setSaleAmount(row.get("sale_amt", Double.class));
                    return sale;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSaleByCustomerSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode) {
        String sql = """
                SELECT a.*, a.ttl_qty * ifnull(rel.smallest_qty,1) smallest_qty, t.user_code, t.trader_name, rel.rel_name, t.address, rel.unit
                FROM (
                    SELECT stock_code, s_user_code, stock_name, SUM(qty) ttl_qty, sale_unit, SUM(sale_amt) ttl_amt, rel_code, trader_code, comp_code, dept_id
                    FROM v_sale
                    WHERE DATE(vou_date) BETWEEN :fromDate AND :toDate
                    AND comp_code = :compCode
                    AND deleted = false
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (cat_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    AND (trader_code = :traderCode OR '-' = :traderCode)
                    GROUP BY stock_code, sale_unit, trader_code
                ) a
                LEFT JOIN v_relation rel ON a.rel_code = rel.rel_code AND a.sale_unit = rel.unit AND a.comp_code = rel.comp_code
                JOIN trader t ON a.trader_code = t.code AND a.comp_code = t.comp_code
                ORDER BY t.user_code, t.trader_name
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    String userCode = row.get("s_user_code", String.class);
                    String sCode = row.get("stock_code", String.class);
                    String traderUsr = row.get("user_code", String.class);
                    String tCode = row.get("trader_code", String.class);
                    Double smallQty = row.get("smallest_qty", Double.class);
                    s.setTraderCode(Util1.isNull(traderUsr, tCode));
                    s.setStockCode(Util1.isNull(userCode, sCode));
                    s.setStockName(row.get("stock_name", String.class));
                    s.setSaleAmount(row.get("ttl_amt", Double.class));
                    s.setRelName(row.get("rel_name", String.class));
                    s.setTraderName(row.get("trader_name", String.class));
                    s.setAddress(row.get("address", String.class));
                    s.setTotalQty(smallQty);
                    s.setSaleUnit(row.get("unit", String.class));
                    return s;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSaleByProjectSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String traderCode, String compCode, Integer deptId, String projectNo) {
        String sql = """
                SELECT a.*, a.ttl_qty * rel.smallest_qty smallest_qty, t.user_code, t.trader_name, rel.rel_name
                FROM (
                    SELECT stock_code, s_user_code, stock_name, SUM(qty) ttl_qty, sale_unit, SUM(sale_amt) ttl_amt, rel_code, trader_code, comp_code, dept_id, project_no
                    FROM v_sale
                    WHERE DATE(vou_date) BETWEEN :fromDate AND :toDate
                    AND comp_code = :compCode
                    AND (dept_id = :deptId OR 0 = :deptId)
                    AND deleted = 0
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (cat_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    AND (trader_code = :traderCode OR '-' = :traderCode)
                    AND (project_no = :projectNo OR '-' = :projectNo)
                    AND project_no IS NOT NULL
                    GROUP BY stock_code, sale_unit, project_no
                ) a
                JOIN v_relation rel ON a.rel_code = rel.rel_code
                AND a.sale_unit = rel.unit
                AND a.comp_code = rel.comp_code
                JOIN trader t ON a.trader_code = t.code
                AND a.comp_code = t.comp_code
                ORDER BY a.project_no
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .bind("projectNo", projectNo)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    String userCode = row.get("s_user_code", String.class);
                    String sCode = row.get("stock_code", String.class);
                    String traderUsr = row.get("user_code", String.class);
                    String tCode = row.get("trader_code", String.class);
                    String relCode = row.get("rel_code", String.class);
                    Double smallQty = row.get("smallest_qty", Double.class);
                    s.setTraderCode(Util1.isNull(traderUsr, tCode));
                    s.setStockCode(Util1.isNull(userCode, sCode));
                    s.setStockName(row.get("stock_name", String.class));
                    s.setSaleAmount(row.get("ttl_amt", Double.class));
                    s.setRelName(row.get("rel_name", String.class));
                    s.setQtyStr(getRelStr(relCode, smallQty));
                    s.setTraderName(row.get("trader_name", String.class));
                    s.setProjectNo(row.get("project_no", String.class));
                    return s;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    public Mono<ReturnObject> getSaleBySaleManSummary(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String smCode, String compCode, Integer deptId) {
        String sql = """
                SELECT a.*, a.ttl_qty * rel.smallest_qty smallest_qty, t.user_code, t.saleman_name, rel.rel_name, rel.unit
                FROM (
                    SELECT stock_code, s_user_code, stock_name, SUM(qty) ttl_qty, sale_unit, SUM(sale_amt) ttl_amt, rel_code, saleman_code, comp_code, dept_id
                    FROM v_sale
                    WHERE DATE(vou_date) BETWEEN :fromDate AND :toDate
                    AND comp_code = :compCode
                    AND (dept_id = :deptId OR 0 = :deptId)
                    AND deleted = 0
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (cat_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    AND (saleman_code = :smCode OR '-' = :smCode)
                    GROUP BY stock_code, sale_unit, saleman_code
                ) a
                JOIN v_relation rel ON a.rel_code = rel.rel_code
                AND a.sale_unit = rel.unit
                AND a.comp_code = rel.comp_code
                LEFT JOIN sale_man t ON a.saleman_code = t.saleman_code
                AND a.comp_code = t.comp_code
                ORDER BY t.user_code, t.saleman_name
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("smCode", smCode)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    String userCode = row.get("s_user_code", String.class);
                    String sCode = row.get("stock_code", String.class);
                    String smUsr = row.get("user_code", String.class);
                    String tCode = row.get("saleman_code", String.class);
                    String relCode = row.get("rel_code", String.class);
                    Double smallQty = row.get("smallest_qty", Double.class);
                    s.setSaleManCode(Util1.isNull(smUsr, tCode));
                    s.setStockCode(Util1.isNull(userCode, sCode));
                    s.setStockName(row.get("stock_name", String.class));
                    s.setSaleAmount(row.get("ttl_amt", Double.class));
                    s.setRelName(row.get("rel_name", String.class));
                    s.setQtyStr(getRelStr(relCode, smallQty));
                    s.setSaleManName(Util1.isNull(row.get("saleman_name", String.class), "Other"));
                    s.setTotalQty(smallQty);
                    s.setSaleUnit(row.get("unit", String.class));
                    return s;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSaleByCustomerDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode) {
        String sql = """
                SELECT v.vou_date, v.vou_no, v.trader_code, t.trader_name, t.address, v.stock_name, v.qty, v.sale_unit, v.sale_price, v.sale_amt
                FROM v_sale v JOIN trader t
                ON v.trader_code = t.code
                AND v.comp_code = t.comp_code
                WHERE (v.trader_code = :traderCode OR '-' = :traderCode)
                AND v.deleted = false
                AND v.comp_code = :compCode
                AND (v.stock_code = :stockCode OR '-' = :stockCode)
                AND (v.cur_code = :curCode OR '-' = :curCode)
                AND DATE(v.vou_date) BETWEEN :fromDate AND :toDate
                ORDER BY t.trader_name, v.vou_date, v.vou_no
                """;

        return client.sql(sql)
                .bind("traderCode", traderCode)
                .bind("compCode", compCode)
                .bind("stockCode", stockCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> VSale.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .saleUnit(row.get("sale_unit", String.class))
                        .salePrice(row.get("sale_price", Double.class))
                        .saleAmount(row.get("sale_amt", Double.class))
                        .address(row.get("address", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getPurchaseBySupplierSummary(String fromDate, String toDate, String typCode, String brandCode, String catCode, String stockCode, String traderCode, String compCode, Integer deptId) {
        String sql = """
                select a.*,a.ttl_qty*ifnull(rel.smallest_qty,1) smallest_qty, t.user_code,t.trader_name,rel.rel_name,rel.unit, t.address
                from (
                select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,pur_unit,sum(pur_amt) ttl_amt,rel_code,trader_code,comp_code,dept_id
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and comp_code = :compCode
                and (dept_id = :deptId or 0 = :deptId)
                and deleted = false
                and (stock_type_code = :typCode or '-' = :typCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                group by stock_code,pur_unit,trader_code
                )a
                left join v_relation rel
                on a.rel_code = rel.rel_code
                and a.pur_unit = rel.unit
                and a.comp_code =rel.comp_code
                join trader t
                on a.trader_code = t.code
                and a.comp_code =t.comp_code
                order by t.user_code,t.trader_name
                """;
        return client
                .sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("typCode", typCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .map(row -> VPurchase.builder()
                        .traderCode(row.get("user_code", String.class))
                        .stockCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .purAmount(row.get("ttl_amt", Double.class))
                        .relName(row.get("rel_name", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .totalQty(row.get("smallest_qty", Double.class))
                        .purUnit(row.get("unit", String.class))
                        .address(row.get("address", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getPurchaseByProjectSummary(String fromDate, String toDate, String typCode, String brandCode, String catCode, String stockCode, String traderCode, String compCode, Integer deptId) {
        String sql = """
                select a.*,a.ttl_qty*rel.smallest_qty smallest_qty, t.user_code,t.trader_name,rel.rel_name
                from (
                select stock_code,s_user_code,stock_name,sum(qty) ttl_qty,pur_unit,sum(pur_amt) ttl_amt,rel_code,trader_code,comp_code,dept_id,project_no
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and comp_code = :compCode
                and (dept_id = :deptId or 0 = :deptId)
                and deleted = 0
                and (stock_type_code = :typCode or '-' = :typCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (trader_code = :traderCode or '-' = :traderCode)
                group by stock_code,pur_unit,project_no
                )a
                join v_relation rel
                on a.rel_code = rel.rel_code
                and a.pur_unit = rel.unit
                and a.comp_code =rel.comp_code
                join trader t
                on a.trader_code = t.code
                and a.comp_code =t.comp_code
                order by t.user_code,t.trader_name
                """;

        return client
                .sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("typCode", typCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .map(row -> VPurchase.builder()
                        .traderCode(Util1.isNull(row.get("s_user_code", String.class), row.get("stock_code", String.class)))
                        .stockCode(Util1.isNull(row.get("user_code", String.class), row.get("trader_code", String.class)))
                        .stockName(row.get("stock_name", String.class))
                        .purAmount(row.get("ttl_amt", Double.class))
                        .relName(row.get("rel_name", String.class))
                        .qtyStr(getRelStr(row.get("rel_code", String.class), row.get("smallest_qty", Double.class)))
                        .traderName(row.get("trader_name", String.class))
                        .projectNo(row.get("project_no", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getPurchaseBySupplierDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode) {
        String sql = """
                SELECT v.vou_date, v.vou_no, v.trader_code, t.trader_name, t.address,
                       v.stock_name, v.qty, v.pur_unit, v.pur_price, v.pur_amt
                FROM v_purchase v JOIN trader t
                     ON v.trader_code = t.code
                     AND v.comp_code = t.comp_code
                WHERE (v.trader_code = :traderCode OR '-' = :traderCode)
                    AND v.deleted = false
                    AND v.comp_code = :compCode
                    AND (v.stock_code = :stockCode OR '-' = :stockCode)
                    AND (v.cur_code = :curCode OR '-' = :curCode)
                    AND DATE(v.vou_date) BETWEEN :fromDate AND :toDate
                ORDER BY t.trader_name, v.vou_no
                """;

        return client.sql(sql)
                .bind("traderCode", traderCode)
                .bind("compCode", compCode)
                .bind("stockCode", stockCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> VPurchase.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .purUnit(row.get("pur_unit", String.class))
                        .purPrice(row.get("pur_price", Double.class))
                        .purAmount(row.get("pur_amt", Double.class))
                        .address(row.get("address", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getPurchaseByProjectDetail(String fromDate, String toDate, String curCode, String traderCode, String stockCode, String compCode, Integer macId, String projectNo) {
        String sql = """
                select v.vou_date,v.vou_no,v.trader_code,t.trader_name,
                v.stock_name,v.qty,v.pur_unit,v.pur_price,v.pur_amt,v.project_no
                from v_purchase v join trader t
                on v.trader_code = t.code
                and v.comp_code = t.comp_code
                where (v.trader_code = :traderCode or '-' = :traderCode)
                and v.deleted = false
                and v.comp_code = :compCode
                and (v.stock_code = :stockCode or '-' = :stockCode)
                and (v.cur_code = :curCode or '-' = :curCode)
                and (v.project_no = :projectNo or '-' = :projectNo)
                and date(v.vou_date) between :fromDate and :toDate
                and v.project_no is not null order by t.trader_name,v.vou_no;
                """;

        return client
                .sql(sql)
                .bind("traderCode", traderCode)
                .bind("compCode", compCode)
                .bind("stockCode", stockCode)
                .bind("curCode", curCode)
                .bind("projectNo", projectNo)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> VPurchase.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .purUnit(row.get("pur_unit", String.class))
                        .purPrice(row.get("pur_price", Double.class))
                        .purAmount(row.get("pur_amt", Double.class))
                        .projectNo(row.get("project_no", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


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
                .map(this::calPercentSale)
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private List<VSale> calPercentSale(List<VSale> list) {
        double totalQty = list.stream()
                .filter(v -> Objects.nonNull(v.getQty())) // Filter out null values
                .mapToDouble(VSale::getQty) // Map to double
                .sum(); // Perform the sum operation
        if (!list.isEmpty()) {
            list.forEach(t -> t.setQtyPercent((t.getQty() / totalQty) * 100));
        }
        return list;
    }


    public Mono<ReturnObject> getSaleByVoucherDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) {
        StringBuilder filter = new StringBuilder();
        if (!typeCode.equals("-")) {
            filter.append("and stock_type_code=:typeCode\n");
        }
        if (!brandCode.equals("-")) {
            filter.append("and brand_code=:brandCode\n");
        }
        if (!catCode.equals("-")) {
            filter.append("and cat_code=:catCode\n");
        }
        if (!stockCode.equals("-")) {
            filter.append("and stock_code=:stockCode\n");
        }
        if (!batchNo.equals("-")) {
            filter.append("and batch_no=:batchNo\n");
        }
        if (!locCode.equals("-")) {
            filter.append("and v.loc_code=:locCode\n");
        }

        String sql = """
                SELECT v.vou_date, v.vou_no, v.vou_total, v.paid, v.remark, v.reference, v.batch_no, sup.trader_name sup_name,
                t.user_code, t.trader_name, t.address, v.s_user_code, v.stock_name, v.qty, v.sale_unit, v.sale_price, v.sale_amt
                FROM v_sale v
                JOIN trader t ON v.trader_code = t.code
                AND v.comp_code = t.comp_code
                LEFT JOIN grn g ON v.batch_no = g.batch_no
                AND v.comp_code = g.comp_code
                LEFT JOIN trader sup ON g.trader_code = sup.code
                AND g.comp_code = sup.comp_code
                WHERE v.deleted = false
                AND v.comp_code = :compCode
                AND v.cur_code = :curCode
                AND DATE(v.vou_date) BETWEEN :fromDate AND :toDate
                """ + filter + """
                ORDER BY v.vou_date, v.vou_no, v.unique_id
                """;

        return client.sql(sql)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("batchNo", batchNo)
                .bind("locCode", locCode)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    s.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    s.setVouNo(row.get("vou_no", String.class));
                    s.setRemark(row.get("remark", String.class));
                    s.setReference(row.get("reference", String.class));
                    s.setBatchNo(row.get("batch_no", String.class));
                    s.setSupplierName(row.get("sup_name", String.class));
                    s.setTraderCode(row.get("user_code", String.class));
                    s.setTraderName(row.get("trader_name", String.class));
                    s.setCusAddress(row.get("address", String.class));
                    s.setStockUserCode(row.get("s_user_code", String.class));
                    s.setStockName(row.get("stock_name", String.class));
                    s.setQty(row.get("qty", Double.class));
                    s.setSaleUnit(row.get("sale_unit", String.class));
                    s.setSalePrice(row.get("sale_price", Double.class));
                    s.setSaleAmount(row.get("sale_amt", Double.class));
                    s.setVouTotal(row.get("vou_total", Double.class));
                    s.setPaid(row.get("paid", Double.class));
                    return s;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSaleByVoucherSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) {
        StringBuilder filter = new StringBuilder();
        if (!typeCode.equals("-")) {
            filter.append("AND stock_type_code=:typeCode\n");
        }
        if (!brandCode.equals("-")) {
            filter.append("AND brand_code=:brandCode\n");
        }
        if (!catCode.equals("-")) {
            filter.append("AND cat_code=:catCode\n");
        }
        if (!stockCode.equals("-")) {
            filter.append("AND stock_code=:stockCode\n");
        }
        if (!batchNo.equals("-")) {
            filter.append("AND batch_no=:batchNo\n");
        }
        if (!locCode.equals("-")) {
            filter.append("AND loc_code=:locCode\n");
        }

        String sql = """
                SELECT a.*, t.trader_name
                FROM (
                    SELECT vou_no, vou_date, trader_code, vou_total, comp_code
                    FROM sale_his
                    WHERE DATE(vou_date) BETWEEN :fromDate AND :toDate
                    AND deleted = false
                    AND comp_code = :compCode
                    AND cur_code = :curCode
                    """ + filter + """
                ) a
                JOIN trader t ON a.trader_code = t.code
                AND a.comp_code = t.comp_code
                ORDER BY vou_date, vou_no
                """;

        return client.sql(sql)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("batchNo", batchNo)
                .bind("locCode", locCode)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    s.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    s.setVouNo(row.get("vou_no", String.class));
                    s.setTraderName(row.get("trader_name", String.class));
                    s.setVouTotal(row.get("vou_total", Double.class));
                    return s;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSaleByBatchDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId) {
        StringBuilder filter = new StringBuilder();
        if (!typeCode.equals("-")) {
            filter.append("AND stock_type_code=:typeCode\n");
        }
        if (!brandCode.equals("-")) {
            filter.append("AND brand_code=:brandCode\n");
        }
        if (!catCode.equals("-")) {
            filter.append("AND cat_code=:catCode\n");
        }
        if (!stockCode.equals("-")) {
            filter.append("AND stock_code=:stockCode\n");
        }
        if (!batchNo.equals("-")) {
            filter.append("AND v.batch_no=:batchNo\n");
        }

        String sql = """
                SELECT v.vou_date, v.vou_no, v.vou_total, v.paid, v.remark, v.reference, v.batch_no, sup.trader_name sup_name,
                    t.user_code, t.trader_name, t.address, v.s_user_code, v.stock_name, v.qty, v.sale_unit, v.sale_price, v.sale_amt
                FROM v_sale v
                JOIN trader t ON v.trader_code = t.code AND v.comp_code = t.comp_code
                LEFT JOIN grn g ON v.batch_no = g.batch_no AND v.comp_code = g.comp_code
                LEFT JOIN trader sup ON g.trader_code = sup.code AND g.comp_code = sup.comp_code
                WHERE v.deleted = false
                AND v.comp_code = :compCode
                AND v.cur_code = :curCode
                AND DATE(v.vou_date) BETWEEN :fromDate AND :toDate
                AND v.batch_no IS NOT NULL
                """ + filter.toString() + """
                ORDER BY v.vou_date, v.batch_no, v.unique_id
                """;

        return client.sql(sql)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("batchNo", batchNo)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    s.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    s.setVouNo(row.get("vou_no", String.class));
                    s.setRemark(row.get("remark", String.class));
                    s.setReference(row.get("reference", String.class));
                    s.setBatchNo(row.get("batch_no", String.class));
                    s.setSupplierName(row.get("sup_name", String.class));
                    s.setTraderCode(row.get("user_code", String.class));
                    s.setTraderName(row.get("trader_name", String.class));
                    s.setCusAddress(row.get("address", String.class));
                    s.setStockUserCode(row.get("s_user_code", String.class));
                    s.setStockName(row.get("stock_name", String.class));
                    s.setQty(row.get("qty", Double.class));
                    s.setSaleUnit(row.get("sale_unit", String.class));
                    s.setSalePrice(row.get("sale_price", Double.class));
                    s.setSaleAmount(row.get("sale_amt", Double.class));
                    s.setVouTotal(row.get("vou_total", Double.class));
                    s.setPaid(row.get("paid", Double.class));
                    return s;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSaleByProjectDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String batchNo, String compCode, Integer deptId, Integer macId, String projectNo) {
        StringBuilder filter = new StringBuilder();
        if (!typeCode.equals("-")) {
            filter.append("AND stock_type_code=:typeCode\n");
        }
        if (!brandCode.equals("-")) {
            filter.append("AND brand_code=:brandCode\n");
        }
        if (!catCode.equals("-")) {
            filter.append("AND cat_code=:catCode\n");
        }
        if (!stockCode.equals("-")) {
            filter.append("AND stock_code=:stockCode\n");
        }
        if (!batchNo.equals("-")) {
            filter.append("AND v.batch_no=:batchNo\n");
        }
        if (!projectNo.equals("-")) {
            filter.append("AND v.project_no=:projectNo\n");
        }

        String sql = """
                SELECT v.vou_date, v.vou_no, v.vou_total, v.paid, v.remark, v.reference, v.batch_no, sup.trader_name sup_name,
                    t.user_code, t.trader_name, t.address, v.s_user_code, v.stock_name, v.qty, v.sale_unit, v.sale_price, v.sale_amt, v.project_no
                FROM v_sale v
                JOIN trader t ON v.trader_code = t.code AND v.comp_code = t.comp_code
                LEFT JOIN grn g ON v.batch_no = g.batch_no AND v.comp_code = g.comp_code
                LEFT JOIN trader sup ON g.trader_code = sup.code AND g.comp_code = sup.comp_code
                WHERE v.deleted = false
                AND v.comp_code = :compCode
                AND v.cur_code = :curCode
                AND DATE(v.vou_date) BETWEEN :fromDate AND :toDate
                AND v.project_no IS NOT NULL
                """ + filter + """
                ORDER BY v.vou_date, v.project_no, v.unique_id
                """;

        return client.sql(sql)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("batchNo", batchNo)
                .bind("projectNo", projectNo)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    s.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    s.setVouNo(row.get("vou_no", String.class));
                    s.setRemark(row.get("remark", String.class));
                    s.setReference(row.get("reference", String.class));
                    s.setBatchNo(row.get("batch_no", String.class));
                    s.setProjectNo(row.get("project_no", String.class));
                    s.setSupplierName(row.get("sup_name", String.class));
                    s.setTraderCode(row.get("user_code", String.class));
                    s.setTraderName(row.get("trader_name", String.class));
                    s.setCusAddress(row.get("address", String.class));
                    s.setStockUserCode(row.get("s_user_code", String.class));
                    s.setStockName(row.get("stock_name", String.class));
                    s.setQty(row.get("qty", Double.class));
                    s.setSaleUnit(row.get("sale_unit", String.class));
                    s.setSalePrice(row.get("sale_price", Double.class));
                    s.setSaleAmount(row.get("sale_amt", Double.class));
                    s.setVouTotal(row.get("vou_total", Double.class));
                    s.setPaid(row.get("paid", Double.class));
                    return s;
                }).all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSaleByStockDetail(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer macId) {
        String sql = """
                SELECT v.vou_date, v.vou_no, v.trader_code, t.trader_name, v.s_user_code, v.stock_name, v.qty, v.sale_unit, v.sale_price, v.sale_amt
                FROM v_sale v
                JOIN trader t ON v.trader_code = t.code AND v.comp_code = t.comp_code
                WHERE (v.stock_code = :stockCode OR '-' = :stockCode)
                AND (stock_type_code = :typeCode OR '-' = :typeCode)
                AND (brand_code = :brandCode OR '-' = :brandCode)
                AND (cat_code = :catCode OR '-' = :catCode)
                AND (loc_code = :locCode OR '-' = :locCode)
                AND v.deleted = false
                AND v.comp_code = :compCode
                AND v.cur_code = :curCode
                AND DATE(v.vou_date) BETWEEN :fromDate AND :toDate
                ORDER BY v.s_user_code, v.vou_no
                """;

        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("locCode", locCode)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    sale.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    sale.setVouNo(row.get("vou_no", String.class));
                    sale.setTraderCode(row.get("trader_code", String.class));
                    sale.setTraderName(row.get("trader_name", String.class));
                    sale.setStockUserCode(row.get("s_user_code", String.class));
                    sale.setStockName(row.get("stock_name", String.class));
                    sale.setQty(row.get("qty", Double.class));
                    sale.setSaleUnit(row.get("sale_unit", String.class));
                    sale.setSalePrice(row.get("sale_price", Double.class));
                    sale.setSaleAmount(row.get("sale_amt", Double.class));
                    return sale;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    public Mono<ReturnObject> getPurchaseByStockDetail(String fromDate, String toDate, String curCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId, String locCode) {
        String sql = """
                select v.vou_date,v.vou_no,v.trader_code,t.trader_name,
                v.s_user_code,v.stock_name,v.qty,v.pur_unit,v.pur_price,v.pur_amt
                from v_purchase v join trader t
                on v.trader_code = t.code
                and v.comp_code = t.comp_code
                where (v.stock_code = :stockCode or '-'=:stockCode)
                and (v.stock_type_code = :typeCode or '-'=:typeCode)
                and (v.brand_code = :brandCode or '-'=:brandCode)
                and v.loc_code in (select f_code from f_location where mac_id = :macId)
                and (v.category_code = :catCode or '-'=:catCode)
                and v.deleted = false
                and v.comp_code = :compCode
                and v.cur_code = :curCode
                and date(v.vou_date) between :fromDate and :toDate
                order by v.s_user_code,v.vou_date,v.vou_no;
                """;
        return client
                .sql(sql)
                .bind("stockCode", stockCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("macId", macId)
                .bind("catCode", catCode)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> VPurchase.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .traderCode(row.get("trader_code", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .stockUserCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .purUnit(row.get("pur_unit", String.class))
                        .purPrice(row.get("pur_price", Double.class))
                        .purAmount(row.get("pur_amt", Double.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getPurchaseByStockSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) {
        String sql = """
                select stock_code,s_user_code,stock_name,sum(qty) qty,sum(bag) bag,sum(pur_amt) amount,comp_code,dept_id
                from v_purchase
                where deleted = false
                and date(vou_date) between :fromDate and :toDate
                and comp_code = :compCode
                and cur_code = :curCode
                and (dept_id =:deptId or 0 =:deptId)
                and (loc_code = :locCode or '-' = :locCode)
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
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
                .map(this::calPercentSale)
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }

    private List<VPurchase> calPercent(List<VPurchase> list) {
        double totalQty = list.stream()
                .filter(v -> Objects.nonNull(v.getQty())) // Filter out null values
                .mapToDouble(VPurchase::getQty) // Map to double
                .sum(); // Perform the sum operation
        if (!list.isEmpty()) {
            list.forEach(t -> t.setQtyPercent((t.getQty() / totalQty) * 100));
        }
        return list;
    }


    public Mono<ReturnObject> getPurchaseByStockWeightSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) {
        String sql = """
                select a.*,u1.unit_name,u2.unit_name weight_unit_name
                from (
                select stock_code,s_user_code,stock_name,sum(qty) qty,sum(ifnull(total_weight,0)) total_weight,
                sum(pur_amt) pur_amt,pur_unit,weight_unit,comp_code
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and comp_code = :compCode
                and deleted = false
                and (stock_type_code = :typeCode or '-' = :typeCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                group by stock_code,weight_unit,pur_unit
                )a
                join stock_unit u1 on a.pur_unit = u1.unit_code
                and a.comp_code = u1.comp_code
                join stock_unit u2 on a.weight_unit = u2.unit_code
                and a.comp_code = u2.comp_code
                order by s_user_code;
                """;
        return client
                .sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .map(row -> VPurchase.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .stockUserCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .purAmount(row.get("pur_amt", Double.class))
                        .totalQty(row.get("qty", Double.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .purUnitName(row.get("unit_name", String.class))
                        .weightUnitName(row.get("weight_unit_name", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<General> getPurchaseRecentPrice(String stockCode, String purDate, String unit, String compCode) {
        String sql = """
                SELECT rel.smallest_qty * smallest_price price, rel.unit
                FROM (
                    SELECT pur_unit, pur_price / ifnull(rel.smallest_qty,1) smallest_price, pd.rel_code, pd.comp_code, pd.dept_id
                    FROM v_purchase pd
                    JOIN v_relation rel ON pd.rel_code = rel.rel_code
                    AND pd.pur_unit = rel.unit
                    AND pd.comp_code = rel.comp_code
                    WHERE pd.stock_code = :stockCode AND vou_no = (
                        SELECT ph.vou_no
                        FROM pur_his ph, pur_his_detail pd
                        WHERE DATE(ph.vou_date) <= :purDate
                        AND deleted = 0
                        AND ph.comp_code = :compCode AND ph.vou_no = pd.vou_no
                        AND pd.stock_code = :stockCode
                        GROUP BY ph.vou_no
                        ORDER BY ph.vou_date DESC
                        LIMIT 1
                    )
                ) a
                LEFT JOIN v_relation rel
                ON a.rel_code = rel.rel_code
                AND a.comp_code = rel.comp_code
                AND rel.unit = :unit
                """;

        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("purDate", purDate)
                .bind("compCode", compCode)
                .bind("unit", unit)
                .map(row -> General.builder().amount(row.get("price", Double.class)).build()).one();
    }


    public Mono<General> getWeightLossRecentPrice(String stockCode, String vouDate, String unit, String compCode) {
        String sql = """
                SELECT rel.smallest_qty * smallest_price price, rel.unit
                FROM (
                    SELECT v.loss_price / rel.smallest_qty smallest_price, v.rel_code, v.comp_code, v.dept_id
                    FROM v_weight_loss v
                    JOIN v_relation rel ON v.rel_code = rel.rel_code
                    AND v.loss_unit = rel.unit
                    AND v.comp_code = rel.comp_code
                    WHERE v.stock_code = :stockCode AND vou_no = (
                        SELECT ph.vou_no
                        FROM weight_loss_his ph, weight_loss_his_detail pd
                        WHERE DATE(ph.vou_date) <= :vouDate
                        AND deleted = 0
                        AND ph.comp_code = :compCode AND ph.vou_no = pd.vou_no
                        AND pd.stock_code = :stockCode
                        GROUP BY ph.vou_no
                        ORDER BY ph.vou_date DESC
                        LIMIT 1
                    )
                ) a
                JOIN v_relation rel
                ON a.rel_code = rel.rel_code
                AND a.comp_code = rel.comp_code
                AND rel.unit = :unit
                """;

        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("vouDate", vouDate)
                .bind("compCode", compCode)
                .bind("unit", unit)
                .map(row -> General.builder().amount(row.get("price", Double.class)).build())
                .one();
    }


    public Mono<General> getProductionRecentPrice(String stockCode, String purDate, String unit, String compCode) {
        String sql = """
                SELECT rel.smallest_qty * smallest_price price, rel.unit
                FROM (
                    SELECT pd.unit, price / rel.smallest_qty smallest_price, pd.rel_code, pd.comp_code, pd.dept_id
                    FROM v_process_his pd
                    JOIN v_relation rel ON pd.rel_code = rel.rel_code
                    AND pd.unit = rel.unit
                    AND pd.comp_code = rel.comp_code
                    WHERE pd.stock_code = :stockCode
                    AND pd.comp_code = :compCode
                    AND vou_no = (
                        SELECT ph.vou_no
                        FROM process_his ph
                        WHERE DATE(ph.vou_date) <= :purDate
                        AND deleted = false
                        AND ph.comp_code = :compCode
                        AND ph.stock_code = :stockCode
                        GROUP BY ph.vou_no
                        ORDER BY ph.vou_date DESC
                        LIMIT 1
                    )
                ) a
                JOIN v_relation rel
                ON a.rel_code = rel.rel_code
                AND a.comp_code = rel.comp_code
                AND rel.unit = :unit
                """;

        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("purDate", purDate)
                .bind("compCode", compCode)
                .bind("unit", unit)
                .map(row -> General.builder().amount(row.get("price", Double.class)).build())
                .one();
    }


    public Mono<General> getPurchaseAvgPrice(String stockCode, String purDate, String unit, String compCode) {
        String sql = """
                SELECT stock_code, ROUND(AVG(avg_price) * rel.smallest_qty, 2) price
                FROM (
                    SELECT 'PUR-AVG' AS source, pur.stock_code, AVG(pur.pur_price / rel.smallest_qty) avg_price, pur.rel_code, pur.comp_code, pur.dept_id
                    FROM v_purchase pur
                    JOIN v_relation rel ON pur.rel_code = rel.rel_code
                    AND pur.pur_unit = rel.unit
                    AND pur.comp_code = rel.comp_code
                    WHERE deleted = false
                    AND pur.comp_code = :compCode
                    AND pur.stock_code = :stockCode
                    AND DATE(pur.vou_date) <= :purDate
                    GROUP BY pur.stock_code
                    
                    UNION ALL
                    
                    SELECT 'OP' AS source, op.stock_code, AVG(op.price / rel.smallest_qty) avg_price, op.rel_code, op.comp_code, op.dept_id
                    FROM v_opening op
                    JOIN v_relation rel ON op.rel_code = rel.rel_code
                    AND op.unit = rel.unit
                    AND op.comp_code = rel.comp_code
                    WHERE op.price > 0
                    AND op.deleted = false
                    AND op.comp_code = :compCode
                    AND DATE(op.op_date) = :purDate
                    AND op.stock_code = :stockCode
                    GROUP BY op.stock_code
                ) a
                JOIN v_relation rel ON a.rel_code = rel.rel_code
                AND a.comp_code = rel.comp_code
                AND rel.unit = :unit
                GROUP BY stock_code
                """;

        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("purDate", purDate)
                .bind("compCode", compCode)
                .bind("unit", unit)
                .map(row -> General.builder().amount(row.get("price", Double.class)).build())
                .one();
    }


    public Mono<General> getSaleRecentPrice(String stockCode, String saleDate, String unit, String compCode) {
        String sql = """
                SELECT ifnull(rel.smallest_qty,1) * smallest_price price, rel.unit
                FROM (
                    SELECT sale_unit, sale_price / ifnull(rel.smallest_qty,1) smallest_price, pd.rel_code, pd.comp_code, pd.dept_id
                    FROM v_sale pd
                    LEFT JOIN v_relation rel ON pd.rel_code = rel.rel_code
                    AND pd.sale_unit = rel.unit
                    AND pd.comp_code = rel.comp_code
                    WHERE pd.stock_code = :stockCode
                    AND vou_no = (
                        SELECT ph.vou_no
                        FROM sale_his ph, sale_his_detail pd
                        WHERE DATE(ph.vou_date) <= :saleDate
                        AND deleted = false
                        AND ph.comp_code = :compCode
                        AND ph.vou_no = pd.vou_no
                        AND pd.stock_code = :stockCode
                        ORDER BY ph.vou_date DESC
                        LIMIT 1
                    )
                ) a
                LEFT JOIN v_relation rel ON a.rel_code = rel.rel_code
                AND a.comp_code = rel.comp_code
                AND rel.unit = :unit
                """;

        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("saleDate", saleDate)
                .bind("compCode", compCode)
                .bind("unit", unit)
                .map(row -> General.builder().amount(row.get("price", Double.class)).build())
                .one();
    }


    public Mono<General> getStockIORecentPrice(String stockCode, String vouDate, String unit) {
        String sql = """
                SELECT cost_price, stock_code, MAX(unique_id)
                FROM stock_in_out_detail
                WHERE stock_code = :stockCode
                AND (in_unit = :unit OR out_unit = :unit)
                AND vou_no = (
                    SELECT sio.vou_no
                    FROM stock_in_out sio
                    JOIN stock_in_out_detail siod ON sio.vou_no = siod.vou_no
                    WHERE DATE(vou_date) <= :vouDate
                    AND deleted = false
                    AND siod.stock_code = :stockCode
                    AND (in_unit = :unit OR out_unit = :unit)
                    AND cost_price <> 0
                    ORDER BY sio.vou_date DESC
                    LIMIT 1
                )
                """;

        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("vouDate", vouDate)
                .bind("unit", unit)
                .map(row -> General.builder().amount(row.get("cost_price", Double.class)).build())
                .one();
    }


    public Mono<General> getWeightAvgPrice(String stockCode, String locCode, String compCode) {
        String sql = """
                SELECT stock_code, comp_code, SUM(amount) / SUM(qty) AS price
                FROM (
                    SELECT stock_code, comp_code, SUM(qty) AS qty, SUM(amount) AS amount
                    FROM v_opening
                    WHERE deleted = false
                    AND DATE(op_date) = :opDate
                    AND comp_code = :compCode
                    AND stock_code = :stockCode
                    GROUP BY stock_code, comp_code
                    UNION ALL
                    SELECT stock_code, comp_code, SUM(qty) AS qty, SUM(pur_amt) AS amount
                    FROM v_purchase
                    WHERE deleted = false
                    AND comp_code = :compCode
                    AND stock_code = :stockCode
                    GROUP BY stock_code, comp_code
                ) a
                GROUP BY stock_code, comp_code
                """;
        return opHisService.getOpeningDateByLocation(compCode, locCode)
                .flatMap(opDate -> client.sql(sql)
                        .bind("opDate", opDate)
                        .bind("compCode", compCode)
                        .bind("stockCode", stockCode)
                        .map(row -> General.builder()
                                .amount(Util1.getDouble(row.get("price", Double.class)))
                                .build())
                        .one())
                .onErrorResume(e -> {
                    log.error("Error in getWeightAvgPrice: {}", e.getMessage());
                    return Mono.empty();
                });
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
                                .unitName(getRelStr(rs.getString("rel_code"), rs.getDouble("qty")))
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
                                .unitName(getRelStr(rs.getString("rel_code"), rs.getDouble("smallest_qty")))
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
                log.error("getStockBalanceByWeight : {}", e.getMessage());
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

    private String getRelStr(String relCode, Double smallestQty) {
        return null;
    }

    public List<ClosingBalance> getClosingStock(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) throws SQLException {
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


    public Flux<ReorderLevel> getReorderLevel(String opDate, String clDate, String typeCode, String catCode, String brandCode,
                                              String stockCode, boolean calSale, boolean calPur, boolean calRI,
                                              boolean calRo, String locCode, String compCode,
                                              Integer deptId, Integer macId) {

        calStockBalanceByLocation(opDate, clDate, typeCode, catCode, brandCode,
                stockCode, calSale, calPur,
                calRI, calRo, locCode, compCode, deptId, macId);

        String sql = """
                select *,if(small_bal_qty<small_min_qty,1,if(small_bal_qty>small_min_qty,2,if(small_bal_qty<small_max_qty,3,if(small_bal_qty> small_max_qty,4,5)))) position
                from (
                select a.*,rel.rel_name,bal_qty*rel.smallest_qty small_bal_qty,min_qty*ifnull(rel1.smallest_qty,0) small_min_qty,max_qty*ifnull(rel2.smallest_qty,0) small_max_qty
                from (
                select tmp.stock_code,tmp.loc_code,tmp.smallest_qty bal_qty, tmp.unit bal_unit,ifnull(min_qty,0) min_qty,min_unit,
                ifnull(max_qty,0) max_qty,max_unit,tmp.comp_code,tmp.dept_id,s.rel_code,s.user_code,s.stock_name,l.loc_name
                from tmp_stock_balance tmp
                left join reorder_level r
                on tmp.stock_code= r.stock_code
                and tmp.comp_code = r.comp_code
                and tmp.loc_code = r.loc_code
                and tmp.mac_id = :macId
                and tmp.comp_code = :compCode
                join stock s on tmp.stock_code = s.stock_code
                and tmp.comp_code = s.comp_code
                join location l on tmp.loc_code = l.loc_code
                and tmp.comp_code = l.comp_code ) a
                join v_relation rel
                on a.rel_code = rel.rel_code
                and a.bal_unit = rel.unit
                and a.comp_code = rel.comp_code
                left join v_relation rel1
                on a.rel_code = rel1.rel_code
                and a.min_unit = rel1.unit
                and a.comp_code = rel1.comp_code
                left join v_relation rel2
                on a.rel_code = rel2.rel_code
                and a.max_unit = rel2.unit
                and a.comp_code = rel2.comp_code )b
                order by position,small_bal_qty
                """;

        return client.sql(sql)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .map(row -> {
                    ReorderLevel r = new ReorderLevel();
                    ReorderKey key = new ReorderKey();
                    key.setDeptId(deptId);
                    key.setCompCode(compCode);
                    key.setStockCode(row.get("stock_code", String.class));
                    key.setLocCode(row.get("loc_code", String.class));
                    r.setKey(key);
                    String relCode = row.get("rel_code", String.class);
                    r.setStockName(row.get("stock_name", String.class));
                    r.setUserCode(row.get("user_code", String.class));
                    r.setRelName(row.get("rel_name", String.class));
                    r.setLocName(row.get("loc_name", String.class));
                    r.setMinQty(row.get("min_qty", Double.class));
                    r.setMinUnitCode(row.get("min_unit", String.class));
                    r.setMaxQty(row.get("max_qty", Double.class));
                    r.setPosition(row.get("position", Integer.class));
                    r.setMaxUnitCode(row.get("max_unit", String.class));
                    //max qty
                    r.setMaxSmallQty(row.get("small_max_qty", Double.class));
                    //min qty
                    r.setMinSmallQty(row.get("small_min_qty", Double.class));
                    //bal qty
                    Double balSmallQty = row.get("small_bal_qty", Double.class);
                    r.setBalUnit(getRelStr(relCode, balSmallQty));
                    r.setBalSmallQty(balSmallQty);
                    return r;
                })
                .all();
    }


    public Mono<ReturnObject> getStockListByGroup(String typeCode, String compCode, Integer macId) {
        String sql = """
                select s.stock_code,s.user_code,s.stock_name,s.stock_type_code,
                st.stock_type_name,b.brand_name,c.cat_name,rel.rel_name
                from stock s
                join stock_type st on s.stock_type_code = st.stock_type_code
                and s.comp_code = st.comp_code
                left join stock_brand b on s.brand_code = b.brand_code
                and s.comp_code = b.comp_code
                left join category c on s.category_code = c.cat_code
                and s.comp_code = c.comp_code
                left join unit_relation rel on s.rel_code = rel.rel_code
                and s.comp_code = rel.comp_code
                where s.active = true and s.comp_code = :compCode
                and (s.stock_type_code = :typeCode or '-' = :typeCode)
                order by st.stock_type_code
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .map(row -> General.builder()
                        .stockCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .sysCode(row.get("stock_code", String.class))
                        .stockTypeName(row.get("stock_type_name", String.class))
                        .brandName(row.get("brand_name", String.class))
                        .categoryName(row.get("cat_name", String.class))
                        .qtyRel(row.get("rel_name", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


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


    public Mono<ReturnObject> getTopSaleBySaleMan(String fromDate, String toDate, String compCode) {
        String sql = """
                select s.user_code,s.saleman_name,count(*) vou_qty,sum(sh.vou_total) vou_total
                from sale_his sh left join sale_man s
                on sh.saleman_code = s.saleman_code
                and sh.comp_code = s.comp_code
                where date(vou_date) between :fromDate and :toDate
                and sh.comp_code = :compCode and sh.deleted = false
                group by sh.saleman_code
                order by vou_total desc
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .map(row -> General.builder()
                        .saleManName(row.get("saleman_name", String.class))
                        .saleManCode(row.get("user_code", String.class))
                        .totalQty(row.get("vou_qty", Double.class))
                        .amount(row.get("vou_total", Double.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


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
                .map(this::calPercentSale)
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


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
                    b.setOpenRel(getRelStr(relCode, opQty));
                    b.setPurQty(purQty);
                    b.setPurRel(getRelStr(relCode, purQty));
                    b.setInQty(inQty);
                    b.setInRel(getRelStr(relCode, inQty));
                    b.setSaleQty(saleQty);
                    b.setSaleRel(getRelStr(relCode, saleQty));
                    b.setOutQty(outQty);
                    b.setOutRel(getRelStr(relCode, outQty));
                    b.setBalQty(balQty);
                    b.setBalRel(getRelStr(relCode, balQty));
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


    public void calculateStockInOutDetail(String opDate, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String vouStatus, boolean calSale, boolean calPur, boolean calRI, boolean calRO, String compCode, Integer deptId, Integer macId) {
        calculateOpening(opDate, fromDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
        calculateClosing(fromDate, toDate, typeCode, catCode, brandCode, stockCode, vouStatus, calSale, calPur, calRI, calRO, compCode, deptId, macId);
    }


    public void calculateStockInOutDetailByWeight(String opDate, String fromDate, String toDate, String typeCode,
                                                  String catCode, String brandCode, String stockCode, String vouStatus,
                                                  boolean calSale, boolean calPur, boolean calRI, boolean calRO, boolean calMill,
                                                  String compCode, Integer deptId, Integer macId) {
        calculateOpeningByWeight(opDate, fromDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, deptId, macId);
        calculateClosingByWeight(fromDate, toDate, typeCode, catCode, brandCode, stockCode, calSale, compCode, deptId, macId);
    }


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
                    c.setOpenRel(getRelStr(relCode, opQty));
                    c.setPurRel(getRelStr(relCode, purQty));
                    c.setInRel(getRelStr(relCode, inQty));
                    c.setSaleRel(getRelStr(relCode, saleQty));
                    c.setOutRel(getRelStr(relCode, outQty));
                    c.setBalRel(getRelStr(relCode, clQty));
                } else {
                    ClosingBalance c = balances.get(i);
                    String relCode = c.getRelCode();
                    double opQty = c.getOpenQty();
                    double purQty = c.getPurQty();
                    double inQty = c.getInQty();
                    double outQty = c.getOutQty();
                    double saleQty = c.getSaleQty();
                    double clQty = opQty + purQty + inQty + outQty + saleQty;
                    c.setOpenRel(getRelStr(relCode, opQty));
                    c.setPurRel(getRelStr(relCode, purQty));
                    c.setInRel(getRelStr(relCode, inQty));
                    c.setSaleRel(getRelStr(relCode, saleQty));
                    c.setOutRel(getRelStr(relCode, outQty));
                    c.setBalRel(getRelStr(relCode, clQty));
                }
            }
        } catch (Exception e) {
            log.error(String.format("getStockInOutDetail: %s", e.getMessage()));
        }
        return balances;
    }


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
                    value.setBalRel(getRelStr(rs.getString("rel_code"), rs.getDouble("bal_qty")));
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


    public Mono<ReturnObject> getOpeningByLocation(String typeCode, String brandCode, String catCode, String stockCode, Integer macId, String compCode, Integer deptId) {
        String sql = """
                select v.op_date,v.vou_no,v.remark,v.stock_code,v.stock_user_code,v.stock_name,l.loc_name,
                v.unit,v.qty,v.price,v.amount,v.comp_code,v.dept_id
                from v_opening v join location l
                on v.loc_code = l.loc_code
                and v.comp_code = l.comp_code
                where v.deleted = false
                and v.tran_source = 1
                and (v.stock_code = :stockCode or '-' = :stockCode)
                and (v.stock_type_code = :typeCode or '-' = :typeCode)
                and (v.category_code = :catCode or '-' = :catCode)
                and (v.brand_code = :brandCode or '-' = :brandCode)
                and v.loc_code in (select f_code from f_location where mac_id = :macId)
                and v.comp_code = :compCode
                and (v.dept_id = :deptId or 0 = :deptId)
                order by l.loc_name,v.stock_user_code
                """;

        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("macId", macId)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .map(row -> {
                    VOpening op = new VOpening();
                    op.setVouDate(Util1.toDateStr(row.get("op_date", LocalDate.class), "dd/MM/yyyy"));
                    op.setRemark(row.get("remark", String.class));
                    op.setStockCode(row.get("stock_code", String.class));
                    op.setUnit(row.get("unit", String.class));
                    op.setStockUserCode(row.get("stock_user_code", String.class));
                    op.setStockName(row.get("stock_name", String.class));
                    op.setLocationName(row.get("loc_name", String.class));
                    op.setQty(row.get("qty", Double.class));
                    op.setPrice(row.get("price", Double.class));
                    op.setAmount(row.get("amount", Double.class));
                    return op;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getOpeningByGroup(String typeCode, String stockCode, String catCode, String brandCode, Integer macId, String compCode, Integer deptId) {
        String sql = """
                select a.*,t.stock_type_name
                from (select v.op_date,v.remark,v.stock_type_code,v.stock_code,v.stock_user_code,v.stock_name,l.loc_name,
                unit,qty,price,amount,v.comp_code
                from v_opening v join location l
                on v.loc_code = l.loc_code
                and v.comp_code = l.comp_code
                where v.deleted = false
                and v.tran_source = 1
                and v.comp_code = :compCode
                and (v.dept_id = :deptId or 0 = :deptId)
                and (v.stock_code = :stockCode or '-' = :stockCode)
                and (v.brand_code = :brandCode or '-' = :brandCode)
                and (v.category_code = :catCode or '-' = :catCode)
                and (v.stock_type_code = :typeCode or '-' = :typeCode)) a
                join stock_type t on a.stock_type_code = t.stock_type_code
                and a.comp_code = t.comp_code
                order by t.stock_type_name,a.stock_user_code
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("stockCode", stockCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("typeCode", typeCode)
                .map(row -> {
                    VOpening opening = new VOpening();
                    opening.setVouDate(Util1.toDateStr(row.get("op_date", LocalDate.class), "dd/MM/yyyy"));
                    opening.setRemark(row.get("remark", String.class));
                    opening.setStockTypeName(row.get("stock_type_name", String.class));
                    opening.setStockCode(row.get("stock_code", String.class));
                    opening.setUnit(row.get("unit", String.class));
                    opening.setStockUserCode(row.get("stock_user_code", String.class));
                    opening.setStockName(row.get("stock_name", String.class));
                    opening.setLocationName(row.get("loc_name", String.class));
                    opening.setQty(row.get("qty", Double.class));
                    opening.setPrice(row.get("price", Double.class));
                    opening.setAmount(row.get("amount", Double.class));
                    return opening;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());

    }


    public Mono<ReturnObject> getStockIODetailByVoucherType(String vouStatus, String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) {
        String sql = """
                select v.vou_date,v.vou_no,v.remark,v.description,s.user_code vs_user_code,s.description vou_status_name,v.s_user_code,v.stock_name,l.loc_name,
                v.out_qty,v.out_unit,v.cur_code,v.cost_price,v.cost_price* v.out_qty out_amt
                from v_stock_io v join vou_status s
                on v.vou_status = s.code
                and v.comp_code = s.comp_code
                join location l on v.loc_code = l.loc_code
                and v.comp_code = l.comp_code
                where v.comp_code = :compCode
                and v.deleted = 0
                and date(v.vou_date) between :fromDate and :toDate
                and (v.stock_type_code = :typeCode or '-' = :typeCode)
                and (v.category_code = :catCode or '-' = :catCode)
                and (v.brand_code = :brandCode or '-' = :brandCode)
                and (v.vou_status = :vouStatus or '-' = :vouStatus)
                and v.out_qty is not null and v.out_unit is not null
                group by date(v.vou_date),v.vou_no,v.stock_code,v.in_unit,v.out_unit,v.cur_code
                order by s.user_code,v.cur_code,v.vou_date,v.vou_no,v.s_user_code;
                """;
        return client
                .sql(sql)
                .bind("compCode", compCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("vouStatus", vouStatus)
                .map(row -> VStockIO.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .stockUsrCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .remark(row.get("remark", String.class))
                        .description(row.get("description", String.class))
                        .vouTypeUserCode(row.get("vs_user_code", String.class))
                        .vouTypeName(row.get("vou_status_name", String.class))
                        .locName(row.get("loc_name", String.class))
                        .outQty(row.get("out_qty", Double.class))
                        .outUnit(row.get("out_unit", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .costPrice(row.get("cost_price", Double.class))
                        .outAmt(row.get("out_amt", Double.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getStockIOPriceCalender(String vouType, String fromDate, String toDate, String typeCode,
                                                      String catCode, String brandCode, String stockCode, String compCode,
                                                      Integer deptId) {
        String filter = "";
        if (!vouType.equals("-")) {
            filter += " and vou_status = :vouType\n";
        }
        if (!typeCode.equals("-")) {
            filter += " and stock_type_code = :typeCode\n";
        }
        if (!catCode.equals("-")) {
            filter += " and category_code = :catCode\n";
        }
        if (!brandCode.equals("-")) {
            filter += " and brand_code = :brandCode\n";
        }
        if (!stockCode.equals("-")) {
            filter += " and stock_code = :stockCode\n";
        }
        String sql = """
                select vou_date, vou_no, remark, stock_code, stock_user_code, stock_name, price, unit, rel_name, small_price, description
                from (
                    select a.*,rel.rel_name,a.price/rel.smallest_qty small_price,null description
                    from (
                        select date(op_date) vou_date,vou_no,null vou_status,'Opening' remark,stock_code,stock_user_code,stock_name,rel_code,
                        price, unit,comp_code
                        from v_opening
                        where price > 0
                        and deleted = false
                        and date(op_date) between :fromDate and :toDate
                        and comp_code = :compCode
                        and (stock_type_code = :typeCode or '-' = :typeCode)
                        and (category_code = :catCode or '-' = :catCode)
                        and (brand_code = :brandCode or '-' = :brandCode)
                        and (stock_code = :stockCode or '-' = :stockCode)
                        group by stock_code,unit
                        )a
                        join v_relation rel on a.rel_code = rel.rel_code
                        and a.unit = rel.unit
                        and a.comp_code = rel.comp_code
                        union
                        select a.*,rel.rel_name,a.cost_price/rel.smallest_qty small_price,vs.description
                        from (
                            select date(vou_date) vou_date,vou_no,vou_status,remark,stock_code,s_user_code,stock_name,rel_code,
                            cost_price,ifnull(in_unit,out_unit) unit,comp_code
                            from v_stock_io
                            where deleted = 0
                            and date(vou_date) between :fromDate and :toDate
                            and comp_code = :compCode
                            and (vou_status = :vouType or '-' = :vouType)
                            and (stock_type_code = :typeCode or '-' = :typeCode)
                            and (category_code = :catCode or '-' = :catCode)
                            and (brand_code = :brandCode or '-' = :brandCode)
                            and (stock_code = :stockCode or '-' = :stockCode)
                            group by stock_code,cost_price,ifnull(in_unit,out_unit)
                            )a
                            join v_relation rel on a.rel_code = rel.rel_code
                            and a.unit = rel.unit
                            and a.comp_code = rel.comp_code
                            join vou_status vs on a.vou_status = vs.code
                            and a.comp_code = vs.comp_code
                        )a
                        group by small_price
                        order by stock_user_code,vou_date,vou_no;
                        """;

        return client
                .sql(sql)
                .bind("vouType", vouType)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .map(row -> VStockIO.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .stockUsrCode(row.get("stock_user_code", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .vouTypeName(row.get("description", String.class))
                        .relName(row.get("rel_name", String.class))
                        .inUnit(row.get("unit", String.class))
                        .costPrice(row.get("price", Double.class))
                        .smallPrice(row.get("small_price", Double.class))
                        .remark(row.get("remark", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Flux<VTransfer> getTransferHistory(String fromDate, String toDate, String refNo, String vouNo, String remark, String userCode, String stockCode,
                                              String locCode, String compCode, Integer deptId, String deleted, String traderCode) {

        String filter = "";
        if (!vouNo.equals("-")) {
            filter += "and vou_no = :vouNo\n";
        }
        if (!refNo.equals("-")) {
            filter += "and ref_no like :refNo\n";
        }
        if (!remark.equals("-")) {
            filter += "and remark like :remark\n";
        }
        if (!userCode.equals("-")) {
            filter += "and v.created_by = :userCode\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code = :stockCode\n";
        }
        if (!traderCode.equals("-")) {
            filter += "and trader_code = :traderCode\n";
        }
        if (!locCode.equals("-")) {
            filter += "and (loc_code_from = :locCode or loc_code_to = :locCode)\n";
        }

        String sql = """
                select v.vou_date,v.vou_no,v.remark,v.ref_no,v.created_by,
                       v.deleted,v.dept_id,l.loc_name from_loc_name,ll.loc_name to_loc_name,t.trader_name, v.labour_group_code
                from v_transfer v join location l
                on v.loc_code_from = l.loc_code
                and v.comp_code = l.comp_code
                join location ll on v.loc_code_to = ll.loc_code
                and v.comp_code = ll.comp_code
                left join trader t on v.trader_code = t.code
                and v.comp_code = t.comp_code
                where v.comp_code = :compCode
                and v.deleted = :deleted
                and (v.dept_id = :deptId or 0 = :deptId)
                and date(v.vou_date) between :fromDate and :toDate
                """ + filter + """
                group by v.vou_no
                order by v.vou_date desc
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("refNo", refNo + "%")
                .bind("remark", remark + "%")
                .bind("userCode", userCode)
                .bind("stockCode", stockCode)
                .bind("traderCode", traderCode)
                .bind("locCode", locCode)
                .bind("compCode", compCode)
                .bind("deleted", deleted)
                .bind("deptId", deptId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map(row -> VTransfer.builder()
                        .vouDateTime(Util1.toZonedDateTime(row.get("vou_date", LocalDateTime.class)))
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .remark(row.get("remark", String.class))
                        .refNo(row.get("ref_no", String.class))
                        .createdBy(row.get("created_by", String.class))
                        .deleted(row.get("deleted", Boolean.class))
                        .fromLocationName(row.get("from_loc_name", String.class))
                        .toLocationName(row.get("to_loc_name", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .traderName(row.get("trader_name", String.class))
                        .build())
                .all();
    }


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


    public Mono<ReturnObject> getSalePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) {
        String sql = """
                select s.s_user_code,s.vou_date,s.vou_no,s.stock_code,
                s.stock_name,s.sale_unit,s.sale_price,s.remark,t.trader_name,s.cur_code 
                from v_sale s join trader t
                on s.trader_code = t.code
                and s.comp_code = t.comp_code
                where s.comp_code = :compCode
                and s.deleted = false
                and date(s.vou_date) between :fromDate and :toDate
                and (s.stock_code = :stockCode or '-' = :stockCode)
                and (s.stock_type_code = :typeCode or '-' = :typeCode)
                and (s.cat_code = :catCode or '-' = :catCode)
                and (s.brand_code = :brandCode or '-' = :brandCode)
                group by s.stock_code,s.sale_price,s.sale_unit
                order by s.s_user_code,s.vou_date,s.sale_unit
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("stockCode", stockCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .map(row -> VSale.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .stockUserCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .saleUnit(row.get("sale_unit", String.class))
                        .salePrice(row.get("sale_price", Double.class))
                        .remark(row.get("remark", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getPurchasePriceCalender(String fromDate, String toDate, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer macId) {
        String sql = """
                select s.s_user_code,s.vou_date,s.vou_no,s.stock_code,
                s.stock_name,s.pur_unit,s.pur_price,s.remark,t.trader_name,s.cur_code 
                from v_purchase s join trader t
                on s.trader_code = t.code
                and s.comp_code = t.comp_code
                where s.comp_code = :compCode
                and s.deleted = false
                and date(s.vou_date) between :fromDate and :toDate
                and (s.stock_code = :stockCode or '-' = :stockCode)
                and (s.stock_type_code = :typeCode or '-' = :typeCode)
                and (s.category_code = :catCode or '-' = :catCode)
                and (s.brand_code = :brandCode or '-' = :brandCode)
                group by s.stock_code,s.pur_price,s.pur_unit
                order by s.s_user_code,s.vou_date,s.pur_unit
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("stockCode", stockCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .map(row -> VPurchase.builder()
                        .vouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyy"))
                        .vouNo(row.get("vou_no", String.class))
                        .stockUserCode(row.get("s_user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .purUnit(row.get("pur_unit", String.class))
                        .purPrice(row.get("pur_price", Double.class))
                        .remark(row.get("remark", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


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


    public Flux<General> isStockExist(String stockCode, String compCode) {
        return searchDetail(stockCode, compCode);
    }

    private Flux<General> searchDetail(String code, String compCode) {
        HashMap<String, String> map = new HashMap<>();
        map.put("sale_his_detail", "Sale");
        map.put("pur_his_detail", "Purchase");
        map.put("ret_in_his_detail", "Return In");
        map.put("ret_out_his_detail", "Return Out");
        map.put("stock_in_out_detail", "Stock In/Out");
        map.put("op_his_detail", "Opening");
        return Flux.fromIterable(map.entrySet())
                .flatMap(entry -> {
                    String table = entry.getKey();
                    String type = entry.getValue();
                    String sql = "SELECT count(*) count FROM " + table + " WHERE stock_code = :code AND comp_code = :compCode";
                    return client.sql(sql)
                            .bind("code", code)
                            .bind("compCode", compCode)
                            .map((row) -> row.get("count", Integer.class))
                            .one()
                            .map(count -> count > 0)
                            .flatMapMany(exist -> {
                                if (exist) {
                                    return Flux.just(General.builder().message("Transaction exists in " + type).build());
                                }
                                return Flux.empty();
                            });
                });
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


    public Flux<VStockIO> getStockInOutVoucher(String vouNo, String compCode) {

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
                where v.vou_no = :vouNo
                and v.comp_code = :compCode
                order by unique_id
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> {
                    VStockIO in = VStockIO.builder().build();                     //vou_no, vou_date, remark, description, s_user_code, stock_name, in_qty, in_unit, out_qty, out_unit, loc_name
                    in.setStockName(row.get("stock_name", String.class));
                    in.setInUnit(row.get("in_unit", String.class));
                    in.setInQty(Util1.toNull(row.get("in_qty", Double.class)));
                    in.setOutUnit(row.get("out_unit", String.class));
                    in.setOutQty(Util1.toNull(row.get("out_qty", Double.class)));
                    in.setVouNo(row.get("vou_no", String.class));
                    in.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    in.setLocName(row.get("loc_name", String.class));
                    in.setStockCode(row.get("s_user_code", String.class));
                    in.setRemark(row.get("remark", String.class));
                    in.setDescription(row.get("description", String.class));
                    in.setJobName(row.get("job_name", String.class));
                    in.setLabourGroupName(row.get("labour_name", String.class));
                    in.setTraderName(row.get("trader_name", String.class));
                    in.setPhoneNo(row.get("phone", String.class));
                    in.setReceivedName(row.get("received_name", String.class));
                    in.setReceivedPhone(row.get("received_phone", String.class));
                    in.setCarNo(row.get("car_no", String.class));
                    in.setRegionName(row.get("reg_name", String.class));
                    in.setUnit(Util1.isNull(in.getInUnit(), in.getOutUnit()));
                    in.setInUnitName(row.get("in_unit_name", String.class));
                    in.setOutUnitName(row.get("out_unit_name", String.class));
                    in.setWeightUnitName(row.get("weight_unit_name", String.class));
                    in.setWeight(Util1.toNull(row.get("weight", Double.class)));
                    in.setInBag(Util1.toNull(row.get("in_bag", Double.class)));
                    in.setOutBag(Util1.toNull(row.get("out_bag", Double.class)));
                    return in;
                })
                .all();
    }


    public Mono<ReturnObject> getProcessOutputDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        String sql = """
                select v.user_code stock_code,v.stock_name,date(end_date) end_date, qty,unit,price,remark,process_no,vs.description,l.loc_name
                from v_process_his v
                join vou_status vs
                on v.pt_code =vs.code
                and v.comp_code = vs.comp_code
                join location l on v.loc_code = l.loc_code
                and v.comp_code = l.comp_code
                where v.comp_code = :compCode
                and (dept_id = :deptId or 0 = :deptId)
                and v.calculate=true
                and v.finished =true
                and v.loc_code in (select f_code from f_location where mac_id = :macId )
                and date(v.end_date) between :fromDate and :toDate
                and (v.pt_code = :ptCode or '-'= :ptCode)
                and (v.stock_type_code = :typeCode or '-'= :typeCode)
                and (v.category_code = :catCode or '-'= :catCode)
                and (v.brand_code = :brandCode or '-'= :brandCode)
                and (v.stock_code = :stockCode or '-'= :stockCode)
                order by vs.description,v.end_date
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("macId", macId)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("ptCode", ptCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .map(row -> VStockIO.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .vouDate(Util1.toDateStr(row.get("end_date", LocalDate.class), "dd/MM/yyyy"))
                        .qty(row.get("qty", Double.class))
                        .unit(row.get("unit", String.class))
                        .price(row.get("price", Double.class))
                        .remark(row.get("remark", String.class))
                        .processNo(row.get("process_no", String.class))
                        .description(row.get("description", String.class))
                        .locName(row.get("loc_name", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getProcessOutputSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        String sql = """
                select a.*,l.loc_name,vs.description 
                from ( 
                select ifnull(user_code,stock_code) stock_code,stock_name,sum(qty) qty,unit,avg(price) avg_price,loc_code,pt_code,comp_code,dept_id 
                from v_process_his  
                where comp_code = :compCode 
                and (dept_id = :deptId or 0 = :deptId) 
                and calculate=true 
                and finished =true 
                and deleted = false 
                and date(end_date) between :fromDate and :toDate 
                and loc_code in (select f_code from f_location where mac_id =  :macId ) 
                and (pt_code = :ptCode or '-'= :ptCode) 
                and (stock_type_code = :typeCode or '-'= :typeCode) 
                and (category_code = :catCode or '-'= :catCode) 
                and (brand_code = :brandCode or '-'= :brandCode) 
                and (stock_code = :stockCode or '-'= :stockCode) 
                group by stock_code,loc_code,pt_code,unit)a 
                join location l on a.loc_code =l.loc_code 
                and a.comp_code = l.comp_code 
                join vou_status vs 
                on a.pt_code =vs.code 
                and a.comp_code = vs.comp_code 
                order by vs.description,l.loc_name
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("macId", macId)
                .bind("ptCode", ptCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .map(row -> {
                    VStockIO io = VStockIO.builder().build();
                    io.setStockCode(row.get("stock_code", String.class));
                    io.setStockName(row.get("stock_name", String.class));
                    io.setQty(row.get("qty", Double.class));
                    io.setUnit(row.get("unit", String.class));
                    io.setPrice(row.get("avg_price", Double.class));
                    io.setLocName(row.get("loc_name", String.class));
                    io.setDescription(row.get("description", String.class));
                    return io;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getProcessUsageSummary(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        String sql = """
                select ifnull(v.user_code,v.stock_code) stock_code,v.stock_name,date(vou_date) vou_date, qty,unit,price,vs.description,l.loc_name
                from v_process_his_detail v
                join vou_status vs
                on v.pt_code =vs.code
                and v.comp_code = vs.comp_code
                join location l on v.loc_code = l.loc_code
                and v.comp_code = l.comp_code
                where v.comp_code = :compCode
                and (v.dept_id = :deptId or 0 = :deptId)
                and v.calculate=true
                and v.loc_code in (select f_code from f_location where mac_id =  :macId )
                and date(v.vou_date) between :fromDate and :toDate
                and (v.pt_code = :ptCode or '-'= :ptCode)
                and (v.stock_type_code = :typeCode or '-'= :typeCode)
                and (v.category_code = :catCode or '-'= :catCode)
                and (v.brand_code = :brandCode or '-'= :brandCode)
                and (v.stock_code = :stockCode or '-'= :stockCode)
                order by vs.description,v.vou_date
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("macId", macId)
                .bind("ptCode", ptCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .map(row -> {
                    VStockIO io = VStockIO.builder().build();
                    io.setStockCode(row.get("stock_code", String.class));
                    io.setStockName(row.get("stock_name", String.class));
                    io.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    io.setQty(row.get("qty", Double.class));
                    io.setUnit(row.get("unit", String.class));
                    io.setPrice(row.get("price", Double.class));
                    io.setLocName(row.get("loc_name", String.class));
                    io.setDescription(row.get("description", String.class));
                    return io;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getProcessUsageDetail(String fromDate, String toDate, String ptCode, String typeCode, String catCode, String brandCode, String stockCode, String compCode, Integer deptId, Integer macId) {
        String sql = """
                select v.*,l.loc_name,vs.description
                from v_process_his_detail v
                join location l
                on v.loc_code = l.loc_code
                and v.comp_code = l.comp_code
                join vou_status vs
                on v.pt_code = vs.code
                and v.comp_code = vs.comp_code
                where v.comp_code = :compCode
                and (v.dept_id = :deptId or 0 = :deptId)
                and v.deleted = false
                and date(v.vou_date) between :fromDate and :toDate
                and v.loc_code in (select f_code from f_location where mac_id = :macId )
                and (v.pt_code = :ptCode or '-'= :ptCode)
                and (v.stock_type_code = :typeCode or '-'= :typeCode)
                and (v.category_code = :catCode or '-'= :catCode)
                and (v.brand_code = :brandCode or '-'= :brandCode)
                and (v.stock_code = :stockCode or '-'= :stockCode)
                order by v.vou_date,vs.description,v.unique_id
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .bind("macId", macId)
                .bind("ptCode", ptCode)
                .bind("typeCode", typeCode)
                .bind("catCode", catCode)
                .bind("brandCode", brandCode)
                .bind("stockCode", stockCode)
                .map(row -> {
                    VStockIO io = VStockIO.builder().build();
                    io.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    io.setStockCode(row.get("user_code", String.class));
                    io.setStockName(row.get("stock_name", String.class));
                    io.setQty(row.get("qty", Double.class));
                    io.setUnit(row.get("unit", String.class));
                    io.setPrice(row.get("price", Double.class));
                    io.setLocName(row.get("loc_name", String.class));
                    io.setDescription(row.get("description", String.class));
                    return io;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());

    }



    public Flux<VPurchase> getPurchaseByWeightVoucher(String vouNo, String batchNo, String compCode) {

        String sql = """
                select 'I' group_name,user_code,stock_name,qty,unit,weight,weight_unit,0 price,0 amount,qty*weight ttl_qty,qty*weight ttl
                from v_grn
                where batch_no = :batchNo
                and stock_code in (select stock_code from pur_his_detail where vou_no = :vouNo and comp_code = :compCode)
                and comp_code = :compCode
                union all
                select 'R',s_user_code,stock_name,qty,pur_unit,weight,weight_unit,pur_price,pur_amt,qty*weight ttl_qty,(qty*weight)*-1 ttl
                from v_purchase
                where vou_no = :vouNo
                and comp_code = :compCode
                """;

        return client.sql(sql)
                .bind("batchNo", batchNo)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> {
                    VPurchase p = VPurchase.builder().build();
                    p.setGroupName(row.get("group_name", String.class));
                    p.setStockUserCode(row.get("user_code", String.class));
                    p.setStockName(row.get("stock_name", String.class));
                    p.setQty(Util1.toNull(row.get("qty", Double.class)));
                    p.setPurUnit(row.get("unit", String.class));
                    p.setWeight(Util1.toNull(row.get("weight", Double.class)));
                    p.setWeightUnit(row.get("weight_unit", String.class));
                    p.setPurAmount(Util1.toNull(row.get("amount", Double.class)));
                    p.setTotalQty(Util1.toNull(row.get("ttl_qty", Double.class)));
                    p.setPurPrice(Util1.toNull(row.get("price", Double.class)));
                    p.setTotal(Util1.toNull(row.get("ttl", Double.class)));
                    return p;
                })
                .all();
    }


    public Mono<ReturnObject> getProfitMarginByStock(String fromDate, String toDate, String curCode, String stockCode, String compCode, Integer deptId) {
        String sql = """
                select
                s_user_code stock_code, stock_name,
                rel.unit unit,
                sum(avg_sale_price) sale_price,
                sum(avg_pur_price) purchase_price,
                round((sum(avg_sale_price)- sum(avg_pur_price)),2) diff_amount,
                concat(cast(round(((sum(avg_sale_price)-sum(avg_pur_price))/(sum(avg_pur_price)))*100,2) as char),'%') diff_percent_amount
                from (
                select s.stock_code,s.s_user_code,s.stock_name, avg(s.sale_price/rel.smallest_qty) avg_sale_price,rel.unit sale_unit,0 avg_pur_price,null pur_unit,s.rel_code, s.comp_code, s.dept_id
                from v_sale s
                join v_relation rel
                on s.rel_code = rel.rel_code
                and s.comp_code = rel.comp_code
                and s.sale_unit = rel.unit
                where s.deleted = false
                and (s.comp_code = :compCode or '-' = :compCode)
                and (s.dept_id = :deptId or '-' = :deptId)
                and (s.cur_code = :curCode or '-' = :curCode)
                and date(s.vou_date) between :fromDate and :toDate
                and (s.stock_code = :stockCode or '-' = :stockCode)
                group by s.stock_code
                union all
                select pur.stock_code,pur.s_user_code,pur.stock_name,0,null,avg(pur.pur_price/rel.smallest_qty) avg_pur_price,rel.unit pur_unit, pur.rel_code, pur.comp_code, pur.dept_id
                from v_purchase pur
                join v_relation rel on pur.rel_code = rel.rel_code
                and pur.pur_unit = rel.unit
                where pur.deleted = false
                and (pur.comp_code = :compCode or '-' = :compCode)
                and (pur.dept_id = :deptId or '-' = :deptId)
                and (pur.cur_code = :curCode or '-' = :curCode)
                and date(pur.vou_date) between :fromDate and :toDate
                and (pur.stock_code = :stockCode or '-' = :stockCode)
                group by pur.stock_code
                )a
                join v_relation rel
                on a.rel_code = rel.rel_code
                where rel.smallest_qty =1
                group by stock_code
                order by stock_code desc
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("stockCode", stockCode)
                .bind("compCode", compCode)
                .bind("deptId", deptId)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    sale.setStockName(row.get("stock_name", String.class));
                    sale.setStockCode(row.get("stock_code", String.class));
                    sale.setSaleUnit(row.get("unit", String.class));
                    sale.setSalePrice(row.get("sale_price", Double.class));
                    sale.setSaleAmount(row.get("purchase_price", Double.class));
                    sale.setPaid(row.get("diff_amount", Double.class));
                    sale.setVouNo(row.get("diff_percent_amount", String.class));
                    return sale;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSaleByDueDate(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
                                               String brandCode, String catCode, String locCode, String batchNo, String compCode,
                                               Integer deptId, Integer macId) {
        String filter = "";
        if (!fromDueDate.equals("-") && !toDueDate.equals("-")) {
            filter += "and date(credit_term) between :fromDueDate and :toDueDate\n";
        }
        if (!typeCode.equals("-")) {
            filter += "and stock_type_code= :typeCode\n";
        }
        if (!brandCode.equals("-")) {
            filter += "and brand_code= :brandCode\n";
        }
        if (!catCode.equals("-")) {
            filter += "and cat_code= :catCode\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code= :stockCode\n";
        }
        if (!batchNo.equals("-")) {
            filter += "and batch_no= :batchNo\n";
        }
        if (!locCode.equals("-")) {
            filter += "and loc_code= :locCode\n";
        }

        String sql = """
                select a.*,t.trader_name
                from (select vou_date,credit_term,vou_no,trader_code,vou_total,comp_code
                from sale_his
                where deleted = false
                """ + filter + """
                and comp_code = :compCode
                and cur_code = :curCode
                and dept_id = :deptId
                )a
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by credit_term,vou_date,vou_no
                """;

        return client.sql(sql)
                .bind("fromDueDate", fromDueDate)
                .bind("toDueDate", toDueDate)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("batchNo", batchNo)
                .bind("locCode", locCode)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("deptId", deptId)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    sale.setCreditTerm(Util1.toDateStr(row.get("credit_term", LocalDate.class), "dd/MM/yyyy"));
                    sale.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    sale.setVouNo(row.get("vou_no", String.class));
                    sale.setTraderName(row.get("trader_name", String.class));
                    sale.setVouTotal(row.get("vou_total", Double.class));
                    return sale;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());

    }


    public Mono<ReturnObject> getSaleByDueDateDetail(String fromDueDate, String toDueDate, String curCode, String stockCode, String typeCode,
                                                     String brandCode, String catCode, String locCode, String batchNo, String compCode,
                                                     Integer deptId, Integer macId) {
        String filter = "";
        if (!typeCode.equals("-")) {
            filter += "and stock_type_code= :typeCode\n";
        }
        if (!brandCode.equals("-")) {
            filter += "and brand_code= :brandCode\n";
        }
        if (!catCode.equals("-")) {
            filter += "and cat_code= :catCode\n";
        }
        if (!stockCode.equals("-")) {
            filter += "and stock_code= :stockCode\n";
        }
        if (!batchNo.equals("-")) {
            filter += "and v.batch_no= :batchNo\n";
        }

        String sql = """
                select v.credit_term,v.vou_date,v.vou_no,v.vou_total,v.paid,v.remark,v.reference,v.batch_no,sup.trader_name sup_name,
                t.user_code,t.trader_name,t.address,v.s_user_code,v.stock_name,v.qty,v.sale_unit,v.sale_price,v.sale_amt
                from v_sale v join trader t
                on v.trader_code = t.code
                left join grn g
                on v.batch_no = g.batch_no
                and v.comp_code = g.comp_code
                left join trader sup
                on g.trader_code = sup.code
                and g.comp_code = sup.comp_code
                where v.deleted = false
                and v.comp_code = :compCode
                and v.cur_code = :curCode
                and date(v.credit_term) between :fromDueDate and :toDueDate
                """ + filter + """
                order by v.credit_term,v.vou_date,v.unique_id
                """;

        return client.sql(sql)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .bind("batchNo", batchNo)
                .bind("fromDueDate", fromDueDate)
                .bind("toDueDate", toDueDate)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    sale.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    sale.setCreditTerm(Util1.toDateStr(row.get("credit_term", LocalDate.class), "dd/MM/yyyy"));
                    sale.setVouNo(row.get("vou_no", String.class));
                    sale.setRemark(row.get("remark", String.class));
                    sale.setReference(row.get("reference", String.class));
                    sale.setBatchNo(row.get("batch_no", String.class));
                    sale.setSupplierName(row.get("sup_name", String.class));
                    sale.setTraderCode((row.get("user_code", String.class)));
                    sale.setTraderName(row.get("trader_name", String.class));
                    sale.setCusAddress(row.get("address", String.class));
                    sale.setStockUserCode(row.get("s_user_code", String.class));
                    sale.setStockName(row.get("stock_name", String.class));
                    sale.setQty(row.get("qty", Double.class));
                    sale.setSaleUnit(row.get("sale_unit", String.class));
                    sale.setSalePrice(row.get("sale_price", Double.class));
                    sale.setSaleAmount(row.get("sale_amt", Double.class));
                    sale.setVouTotal(row.get("vou_total", Double.class));
                    sale.setPaid(row.get("paid", Double.class));
                    return sale;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Flux<VSale> getSaleSummaryByDepartment(String fromDate, String toDate, String compCode) {
        String sql = """
                select sum(vou_total) vou_total,sum(vou_balance) vou_balance,sum(paid) paid,cur_code,dept_id,count(*) vou_count
                from sale_his
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and comp_code = :compCode
                group by dept_id,cur_code""";

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    s.setVouTotal(row.get("vou_total", Double.class));
                    s.setVouBalance(row.get("vou_balance", Double.class));
                    s.setPaid(row.get("paid", Double.class));
                    s.setDeptId(row.get("dept_id", Integer.class));
                    s.setVouCount(row.get("vou_count", Integer.class));
                    return s;
                })
                .all();
    }


    public Flux<VSale> getSaleByBatchReport(String vouNo, String grnVouNo, String compCode) {
        String sql = """
                select 'I' group_name,user_code,stock_name,qty,unit,weight,weight_unit,0 price,0 amount,qty*weight ttl_qty
                from v_grn
                where vou_no = :grnVouNo
                and comp_code = :compCode
                union all
                select 'R',s_user_code,stock_name,qty,sale_unit,weight,weight_unit,sale_price,sale_amt,qty*weight ttl_qty
                from v_sale
                where vou_no = :vouNo
                and comp_code = :compCode
                """;

        return client.sql(sql)
                .bind("grnVouNo", grnVouNo)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map(row -> {
                    VSale s = VSale.builder().build();
                    s.setGroupName(row.get("group_name", String.class));
                    s.setStockUserCode(row.get("user_code", String.class));
                    s.setStockName(row.get("stock_name", String.class));
                    s.setQty(Util1.toNull(row.get("qty", Double.class)));
                    s.setSaleUnit(row.get("unit", String.class));
                    s.setWeight(Util1.toNull(row.get("weight", Double.class)));
                    s.setWeightUnit(row.get("weight_unit", String.class));
                    s.setSalePrice(Util1.toNull(row.get("price", Double.class)));
                    s.setSaleAmount(Util1.toNull(row.get("amount", Double.class)));
                    s.setTotalQty(Util1.toNull(row.get("ttl_qty", Double.class)));
                    return s;
                })
                .all();
    }


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


    public Mono<ReturnObject> getStockPayableByTrader(String opDate, String fromDate, String toDate,
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
        return null;
    }


    public Mono<ReturnObject> getSaleByStockWeightSummary(String fromDate, String toDate, String curCode, String stockCode, String typeCode, String brandCode, String catCode, String locCode, String compCode, Integer deptId, Integer macId) {
        String sql = """
                SELECT a.*, u1.unit_name, u2.unit_name weight_unit_name
                FROM (
                    SELECT stock_code, s_user_code, stock_name, SUM(qty) qty, SUM(IFNULL(total_weight, 0)) total_weight,
                    SUM(sale_amt) sale_amt, sale_unit, weight_unit, comp_code
                    FROM v_sale
                    WHERE DATE(vou_date) BETWEEN :fromDate AND :toDate
                    AND comp_code = :compCode
                    AND deleted = false
                    AND (stock_type_code = :typeCode OR '-' = :typeCode)
                    AND (brand_code = :brandCode OR '-' = :brandCode)
                    AND (cat_code = :catCode OR '-' = :catCode)
                    AND (stock_code = :stockCode OR '-' = :stockCode)
                    GROUP BY stock_code, weight_unit, sale_unit
                ) a
                JOIN stock_unit u1 ON a.sale_unit = u1.unit_code
                AND a.comp_code = u1.comp_code
                JOIN stock_unit u2 ON a.weight_unit = u2.unit_code
                AND a.comp_code = u2.comp_code
                ORDER BY s_user_code
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("typeCode", typeCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("stockCode", stockCode)
                .map(row -> {
                    VSale p = VSale.builder().build();
                    p.setStockCode(row.get("stock_code", String.class));
                    p.setStockUserCode(row.get("s_user_code", String.class));
                    p.setStockName(row.get("stock_name", String.class));
                    p.setSaleAmount(row.get("sale_amt", Double.class));
                    p.setTotalQty(row.get("qty", Double.class));
                    p.setTotalWeight(row.get("total_weight", Double.class));
                    p.setSaleUnitName(row.get("unit_name", String.class));
                    p.setWeightUnitName(row.get("weight_unit_name", String.class));
                    return p;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getStockPayableConsignor(String opDate, String fromDate, String toDate,
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
        return null;

    }


    public Mono<ReturnObject> getPurchaseList(String fromDate, String toDate, String compCode, String stockCode,
                                              String groupCode, String catCode, String brandCode, String locCode,
                                              String labourGroupCode) {
        String sql = """
                select a.*,t.trader_name,l.loc_name
                from (
                select date(vou_date) vou_date,trader_code,stock_code,stock_name,loc_code,wet,rice, qty,bag,pur_price,
                wet*qty total_wet,rice*qty total_rice, vou_total,grand_total,paid,balance,comp_code,vou_no,reference
                from v_purchase
                where date(vou_date) between :fromDate and :toDate
                and deleted = false
                and comp_code = :compCode
                and (stock_type_code = :groupCode or '-' = :groupCode)
                and (brand_code = :brandCode or '-' = :brandCode)
                and (category_code = :catCode or '-' = :catCode)
                and (loc_code = :locCode or '-' = :locCode)
                and (stock_code = :stockCode or '-' = :stockCode)
                and (labour_group_code = :labourGroupCode or '-' = :labourGroupCode)
                ) a
                join location l on a.loc_code = l.loc_code
                and a.comp_code = l.comp_code
                join trader t on a.trader_code = t.code
                and a.comp_code = t.comp_code
                order by date(vou_date), qty desc
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("groupCode", groupCode)
                .bind("brandCode", brandCode)
                .bind("catCode", catCode)
                .bind("locCode", locCode)
                .bind("stockCode", stockCode)
                .bind("labourGroupCode", labourGroupCode)
                .map(row -> {
                    VPurchase purchase = VPurchase.builder().build();
                    purchase.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    purchase.setVouNo(row.get("vou_no", String.class));
                    purchase.setTraderCode(row.get("trader_code", String.class));
                    purchase.setStockCode(row.get("stock_code", String.class));
                    purchase.setLocationCode(row.get("loc_code", String.class));
                    purchase.setPurPrice(row.get("pur_price", Double.class));
                    purchase.setQty(row.get("qty", Double.class));
                    purchase.setBag(row.get("bag", Double.class));
                    purchase.setWet(row.get("wet", Double.class));
                    purchase.setTotalWet(row.get("total_wet", Double.class));
                    purchase.setRice(row.get("rice", Double.class));
                    purchase.setTotalRice(row.get("total_rice", Double.class));
                    purchase.setVouTotal(row.get("vou_total", Double.class));
                    purchase.setGrandTotal(row.get("grand_total", Double.class));
                    purchase.setPaid(row.get("paid", Double.class));
                    purchase.setBalance(row.get("balance", Double.class));
                    String reference = row.get("reference", String.class);
                    String traderName = row.get("trader_name", String.class);
                    purchase.setTraderName(Util1.isNull(reference, traderName));
                    purchase.setLocationName(row.get("loc_name", String.class));
                    purchase.setStockName(row.get("stock_name", String.class));
                    purchase.setPurPrice(row.get("pur_price", Double.class));
                    return purchase;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


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


    public Mono<ReturnObject> getCustomerBalanceSummary(String fromDate, String toDate, String compCode,
                                                        String curCode, String traderCode, String batchNo,
                                                        String projectNo, String locCode, double creditAmt) {
        String sql = """
                select *
                from (
                    select b.*,t.user_code,t.trader_name,t.address,if(ifnull(t.credit_amt,0)=0,:creditAmt,t.credit_amt) credit_amt,b.vou_balance - if(ifnull(t.credit_amt,0)=0,:creditAmt,t.credit_amt) diff_amt
                    from (
                        select trader_code,cur_code,sum(vou_balance) vou_balance,comp_code
                        from(
                            select trader_code,cur_code,sum(vou_balance) vou_balance,comp_code
                            from sale_his 
                            where date(vou_date) between :fromDate and :toDate
                            and comp_code =:compCode
                            and cur_code =:curCode
                            and deleted = false
                            and vou_balance>0
                            and (trader_code = :traderCode or '-' = :traderCode)
                            and (project_no = :projectNo or '-' = :projectNo)
                            group by trader_code
                            union all
                            select pd.trader_code,phd.cur_code,sum(ifnull(phd.pay_amt,0))*-1 paid,pd.comp_code
                            from payment_his pd join payment_his_detail phd
                            on pd.vou_no = phd.vou_no
                            and pd.comp_code = phd.comp_code
                            where date(sale_vou_date) between :fromDate and :toDate
                            and pd.tran_option ='C'
                            and pd.comp_code =:compCode
                            and phd.cur_code =:curCode
                            and pd.deleted = false
                            and (pd.trader_code = :traderCode or '-' = :traderCode)
                            and (pd.project_no = :projectNo or '-' = :projectNo)
                            group by pd.trader_code
                        )a
                        group by trader_code
                    )b
                    join trader t on b.trader_code = t.code
                    and b.comp_code = t.comp_code
                    where b.vou_balance<>0
                )c
                order by diff_amt desc
                """;


        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("compCode", compCode)
                .bind("curCode", curCode)
                .bind("traderCode", traderCode)
                .bind("projectNo", projectNo)
                .bind("creditAmt", creditAmt)
                .map((row, metadata) -> VSale.builder()
                        .userCode(row.get("user_code", String.class))
                        .curCode(row.get("cur_code", String.class))
                        .vouBalance(row.get("vou_balance", Double.class))
                        .creditAmt(row.get("credit_amt", Double.class))
                        .diffAmt(row.get("diff_amt", Double.class))
                        .address(row.get("address", String.class))
                        .traderName(row.get("trader_name", String.class))
                        .build())
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());

    }


    public Mono<ReturnObject> getSupplierBalanceSummary(String fromDate, String toDate, String compCode, String curCode, String traderCode, String batchNo, String projectNo, String locCode, double creditAmt) {
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code = :traderCode\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no = :projectNo\n";
        }

        String sql = """
                select *
                from (
                select b.*,t.user_code,t.trader_name,t.address,if(ifnull(t.credit_amt,0)=0,:creditAmt,t.credit_amt) credit_amt,b.vou_balance - if(ifnull(t.credit_amt,0)=0,:creditAmt,t.credit_amt) diff_amt
                from (
                select trader_code,cur_code,sum(vou_balance) vou_balance,comp_code
                from(
                select trader_code,cur_code,sum(balance) vou_balance,comp_code
                from pur_his 
                where date(vou_date) between :fromDate and :toDate
                and comp_code = :compCode
                and cur_code = :curCode
                and deleted = false
                and balance>0
                """ + filter + """
                group by trader_code
                union all
                select pd.trader_code,phd.cur_code,sum(ifnull(phd.pay_amt,0))*-1 paid,pd.comp_code
                from payment_his pd join payment_his_detail phd
                on pd.vou_no = phd.vou_no
                and pd.comp_code = phd.comp_code
                where date(sale_vou_date) between :fromDate and :toDate
                and pd.comp_code = :compCode
                and phd.cur_code = :curCode
                and pd.tran_option ='S'
                and pd.deleted = false
                """ + filter + """
                group by pd.trader_code
                )a
                group by trader_code
                )b
                join trader t on b.trader_code = t.code
                and b.comp_code = t.comp_code
                where b.vou_balance<>0
                )c
                order by diff_amt desc;
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("compCode", compCode)
                .bind("traderCode", traderCode)
                .bind("projectNo", projectNo)
                .bind("creditAmt", creditAmt)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    sale.setUserCode(row.get("user_code", String.class));
                    sale.setCurCode(row.get("cur_code", String.class));
                    sale.setVouBalance(row.get("vou_balance", Double.class));
                    sale.setCreditAmt(row.get("credit_amt", Double.class));
                    sale.setDiffAmt(row.get("diff_amt", Double.class));
                    sale.setAddress(row.get("address", String.class));
                    sale.setTraderName(row.get("trader_name", String.class));
                    return sale;
                }).all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getCustomerBalanceDetail(String fromDate, String toDate, String compCode,
                                                       String curCode, String traderCode, String batchNo,
                                                       String projectNo, String locCode) {
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code = :traderCode\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no = :projectNo\n";
        }

        String sql = """
                select sh.vou_date, b.vou_no, b.cur_code, sh.vou_total, sh.vou_balance, sh.remark,
                sh.reference, b.outstanding,sh.trader_code, t.user_code, t.address, t.trader_name
                from (
                select vou_no,cur_code,sum(vou_balance) outstanding,comp_code
                from (
                select vou_no,cur_code,vou_balance,comp_code
                from sale_his 
                where comp_code = :compCode
                and date(vou_date) between :fromDate and :toDate
                and deleted = false
                and cur_code=:curCode
                """ + filter + """
                and vou_balance>0
                union all
                select phd.sale_vou_no,phd.cur_code,phd.pay_amt*-1,pd.comp_code
                from payment_his pd join payment_his_detail phd
                on pd.vou_no = phd.vou_no
                and pd.comp_code = phd.comp_code
                where pd.comp_code = :compCode
                and date(phd.sale_vou_date) between :fromDate and :toDate
                and pd.deleted = false
                and pd.tran_option = 'C'
                and phd.cur_code=:curCode
                """ + filter + """
                )a
                group by vou_no
                )b
                join sale_his sh
                on b.vou_no = sh.vou_no
                and b.comp_code = sh.comp_code
                join trader t on sh.trader_code = t.code
                and sh.comp_code = t.comp_code
                where outstanding<>0
                order by vou_date;
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("compCode", compCode)
                .bind("traderCode", traderCode)
                .bind("projectNo", projectNo)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    sale.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    sale.setUserCode(row.get("user_code", String.class));
                    sale.setTraderCode(row.get("trader_code", String.class));
                    sale.setVouNo(row.get("vou_no", String.class));
                    sale.setCurCode(row.get("cur_code", String.class));
                    sale.setVouTotal(row.get("vou_total", Double.class));
                    sale.setRemark(row.get("remark", String.class));
                    sale.setReference(row.get("reference", String.class));
                    sale.setVouBalance(row.get("vou_balance", Double.class));
                    sale.setAddress(row.get("address", String.class));
                    sale.setTraderName(row.get("trader_name", String.class));
                    return sale;
                }).all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
    }


    public Mono<ReturnObject> getSupplierBalanceDetail(String fromDate, String toDate, String compCode,
                                                       String curCode, String traderCode, String batchNo,
                                                       String projectNo, String locCode) {
        String filter = "";
        if (!traderCode.equals("-")) {
            filter += "and trader_code = :traderCode\n";
        }
        if (!projectNo.equals("-")) {
            filter += "and project_no = :projectNo\n";
        }

        String sql = """
                select sh.vou_date, b.vou_no, b.cur_code, sh.vou_total, sh.balance, sh.remark,
                sh.reference, b.outstanding,sh.trader_code, t.user_code, t.address, t.trader_name
                from (
                select vou_no,cur_code,sum(balance) outstanding,comp_code
                from (
                select vou_no,cur_code,balance,comp_code
                from pur_his 
                where comp_code = :compCode
                and date(vou_date) between :fromDate and :toDate
                and deleted = false
                and cur_code = :curCode
                """ + filter + """
                and balance>0
                union all
                select phd.sale_vou_no,phd.cur_code,phd.pay_amt*-1,pd.comp_code
                from payment_his pd join payment_his_detail phd
                on pd.vou_no = phd.vou_no
                and pd.comp_code = phd.comp_code
                where pd.comp_code = :compCode
                and date(phd.sale_vou_date) between :fromDate and :toDate
                and pd.deleted = false
                and pd.tran_option = 'S'
                and phd.cur_code = :curCode
                """ + filter + """
                )a
                group by vou_no
                )b
                join pur_his sh
                on b.vou_no = sh.vou_no
                and b.comp_code = sh.comp_code
                join trader t on sh.trader_code = t.code
                and sh.comp_code = t.comp_code
                where outstanding<>0
                order by vou_date;
                """;

        return client.sql(sql)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .bind("curCode", curCode)
                .bind("compCode", compCode)
                .bind("traderCode", traderCode)
                .bind("projectNo", projectNo)
                .map(row -> {
                    VSale sale = VSale.builder().build();
                    sale.setVouDate(Util1.toDateStr(row.get("vou_date", LocalDate.class), "dd/MM/yyyy"));
                    sale.setUserCode(row.get("user_code", String.class));
                    sale.setTraderCode(row.get("trader_code", String.class));
                    sale.setVouNo(row.get("vou_no", String.class));
                    sale.setCurCode(row.get("cur_code", String.class));
                    sale.setVouTotal(row.get("vou_total", Double.class));
                    sale.setRemark(row.get("remark", String.class));
                    sale.setReference(row.get("reference", String.class));
                    sale.setVouBalance(row.get("balance", Double.class));
                    sale.setAddress(row.get("address", String.class));
                    sale.setTraderName(row.get("trader_name", String.class));
                    return sale;
                })
                .all()
                .collectList()
                .map(Util1::convertToJsonBytes)
                .map(fileBytes -> ReturnObject.builder()
                        .status("success")
                        .message("Data fetched successfully")
                        .file(fileBytes)
                        .build());
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

}




