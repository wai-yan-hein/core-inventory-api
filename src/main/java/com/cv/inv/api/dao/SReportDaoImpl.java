/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import java.io.Serializable;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */


@Repository
public class SReportDaoImpl extends AbstractDao<Serializable, Object> implements SReportDao {

    private static final Logger log = LoggerFactory.getLogger(SReportDaoImpl.class);

    @Override
    public void generateStockBalance(String stockCode, String locId, String compCode, String macId) {
        try {
            deleteTmpFilter(macId);
            insertTmpStockFilter(stockCode, locId, compCode, macId);
            calculateBalance(macId);
        } catch (Exception ex) {
            log.error("calclate stock balance :" + ex.getMessage());
        }
    }

    private void insertTmpStockFilter(String stockCode, String locId, String compCode, String macId) throws Exception {
        String filterSql = "insert tmp_stock_filter(stock_code, comp_code, loc_code, mac_id)\n"
                + "select stock_code," + compCode + ",location_id," + macId + "\n"
                + "from v_stock_loc";
        String andSql = "";
        if (!stockCode.equals("-")) {
            if (andSql.isEmpty()) {
                andSql = "stock_code in  (" + stockCode + ")";
            } else {
                andSql = andSql + " and stock_code '" + stockCode + "'";
            }
        }
        if (!locId.equals("-")) {
            if (andSql.isEmpty()) {
                andSql = "location_id  = '" + locId + "'";
            } else {
                andSql = andSql + " and location_id = '" + locId + "'";
            }
        }
        if (!andSql.isEmpty()) {
            filterSql = filterSql + " where " + andSql;
        }
        execSQL(filterSql);
        log.info("insert tmp table success.");
    }

    private void calculateBalance(String macId) throws Exception {
        String insertSql = "insert into tmp_stock_balance(stock_code,qty,wt,small_wt_ttl,small_unit,loc_code,mac_id)\n"
                + "select b.stock_code,sum(b.qty) qty,b.wt,sum(b.small_wt) as small_wt_ttl,b.small_unit,b.loc," + macId + "\n"
                + "	from(\n"
                + "		select p.stock_code,sum(p.qty) as qty,p.avg_wt as wt,sum(p.small_wt) as small_wt,p.small_unit,p.loc_code as loc \n"
                + "		from v_purchase p,tmp_stock_filter tmp\n"
                + "             where p.stock_code = tmp.stock_code and p.loc_code = tmp.loc_code and p.comp_code = tmp.comp_code\n"
                + "             and tmp.mac_id = " + macId + " and p.deleted = false\n"
                + "		group by stock_code,loc,std_wt,small_unit\n"
                + "			union all\n"
                + "		select s.stock_code,sum(s.qty)*-1 as qty,s.std_weight as wt,sum(s.small_wt)*-1 as small_wt,s.small_unit,s.loc_code as loc \n"
                + "		from v_sale s, tmp_stock_filter tmp\n"
                + "		where s.stock_code = tmp.stock_code and s.loc_code = tmp.loc_code and s.comp_code = tmp.comp_code\n"
                + "             and tmp.mac_id = " + macId + " and s.deleted = false\n"
                + "		group by stock_code,loc,std_weight,small_unit\n"
                + "			union all \n"
                + "		select  r.stock_code,sum(r.qty) as qty,r.std_wt as wt,sum(r.small_wt) as small_wt,r.small_unit,r.loc_code as loc\n"
                + "		from v_return_in r, tmp_stock_filter tmp\n"
                + "		where r.stock_code = tmp.stock_code and r.loc_code = tmp.loc_code and r.comp_code = tmp.comp_code \n"
                + "             and tmp.mac_id = " + macId + " and r.deleted = false\n"
                + "		group by stock_code,loc,std_wt,small_unit\n"
                + "			union all \n"
                + "             select  r.stock_code,sum(r.qty)*-1 as qty,r.std_wt as wt,sum(r.small_wt*-1) as  small_wt,r.small_unit,r.loc_code as loc\n"
                + "		from v_return_out r, tmp_stock_filter tmp\n"
                + "		where r.stock_code = tmp.stock_code and r.loc_code = tmp.loc_code and r.comp_code = tmp.comp_code \n"
                + "             and tmp.mac_id = " + macId + " and r.deleted = false\n"
                + "		group by stock_code,loc,std_wt,small_unit\n"
                + "                      union all \n"
                + "             select od.stock_code,sum(ifnull(od.in_qty,0)) qty,sum(ifnull(od.in_weight,0)) wt,sum(ifnull(od.small_in_weight,0)) small_wt,od.small_in_unit unit,od.loc_code loc\n"
                + "             from v_stock_in_out od,tmp_stock_filter tmp\n"
                + "             where od.stock_code = tmp.stock_code and tmp.mac_id = " + macId + " and od.deleted = false\n"
                + "             group by od.stock_code,od.small_in_unit,od.loc_code\n"
                + "                     union all \n"
                + "             select od.stock_code,sum(ifnull(od.out_qty,0))*-1 qty,sum(ifnull(od.out_weight,0))*-1 wt,sum(ifnull(od.small_out_weight,0))*-1 small_wt,od.small_out_unit unit,od.loc_code loc\n"
                + "             from v_stock_in_out od,tmp_stock_filter tmp\n"
                + "             where od.stock_code = tmp.stock_code and tmp.mac_id = " + macId + " and od.deleted = false\n"
                + "             group by od.stock_code,od.small_in_unit,od.loc_code"
                + "        ) b\n"
                + "group by b.stock_code,b.loc";
        execSQL(insertSql);
        log.info("insert stock balance tmp success.");
    }

    private void deleteTmpFilter(String macId) throws Exception {
        String delSql = "delete from tmp_stock_filter where mac_id = " + macId + "";
        String delSql1 = "delete from tmp_stock_balance where mac_id = " + macId + "";
        execSQL(delSql, delSql1);
        log.info("delete tmp table success.");
    }

    private void insertTmpStockCode(String stockCode, String macId) throws Exception {
        String delSql = "delete from tmp_stock_code where mac_id = " + macId + "";
        execSQL(delSql);
        if (!stockCode.equals("-")) {
            String insertSql = "insert into tmp_stock_code(stock_code,mac_id)\n"
                    + "select stock_code," + macId + " from stock where stock_code in (" + stockCode + ")";
            execSQL(insertSql);
        } else {
            String inserSql = "insert into tmp_stock_code(stock_code,mac_id)\n"
                    + "select stock_code, " + macId + "\n"
                    + "from stock where active = 1";
            execSQL(inserSql);
        }
    }

    private void insertTmpRegionCode(String regionCode, String macId) throws Exception {
        String delSql = "delete from tmp_region_code where mac_id = " + macId + "";
        execSQL(delSql);
        if (!regionCode.equals("-")) {
            String insertSql = "insert tmp_region_code(reg_code,mac_id)\n"
                    + "select reg_id," + macId + " from region where reg_id in (" + regionCode + ")";
            execSQL(insertSql);
        }
    }

    @Override
    public void reportViewer(String reportPath, String filePath, String fontPath, Map<String, Object> parameters) {
        try {
            //doReportPDF(reportPath, filePath, parameters, fontPath);
        } catch (Exception ex) {
            log.error("Report Viewer Error :" + ex.getMessage());
        }
    }

    @Override
    public void reportJsonViewer(String path, String reportPath, String filePath, String fontPath, Map<String, Object> parameters) {
        try {
            //genJsonReport(path, reportPath, filePath, parameters, fontPath);
        } catch (Exception ex) {
            log.error("Report Viewer Error :" + ex.getMessage());
        }
    }

    @Override
    public void generateSaleByStock(String stockCode, String regionCode, String macId) {
        try {
            insertTmpStockCode(stockCode, macId);
            insertTmpRegionCode(regionCode, macId);
        } catch (Exception e) {
            log.error("generateSaleByStock :" + e.getMessage());
        }
    }

    @Override
    public String genJsonFile(final String strSql) throws Exception {
        return "test";
    }
}
