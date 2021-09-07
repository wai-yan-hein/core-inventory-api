/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.VStockBalance;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Lenovo
 */
@Repository
public class CalculationDaoImpl extends AbstractDao<String, VStockBalance> implements CalculationDao {

    @Override
    public List<VStockBalance> calStockBalance(String stockCode, Integer macId) throws Exception {
        String delSql = "delete from tmp_stock_balance where mac_id = " + macId + "";
        String insertSql = "insert into tmp_stock_balance(stock_code,qty,wt,unit,loc_code,mac_id)\n"
                + "select stock_code,sum(qty),wt,unit,loc_code," + macId + "\n"
                + "from(\n"
                + "select stock_code,sum(qty)*-1 qty,sale_wt wt,sale_unit unit,loc_code\n"
                + "from sale_his_detail\n"
                + "where (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "group by stock_code,sale_wt,sale_unit,loc_code\n"
                + "	union all\n"
                + "select stock_code,sum(qty) qyt,avg_wt,pur_unit,loc_code\n"
                + "from pur_his_detail \n"
                + "where (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "group by stock_code,avg_wt,pur_unit,loc_code\n"
                + "	union all\n"
                + "select stock_code,sum(qty) qty,wt,unit,loc_code\n"
                + "from ret_in_his_detail\n"
                + "where (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "group by stock_code,wt,unit,loc_code\n"
                + "	union all\n"
                + "select stock_code,sum(qty)*-1 qty,wt,unit,loc_code\n"
                + "from ret_out_his_detail\n"
                + "where (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "group by stock_code,wt,unit,loc_code\n"
                + "	union all\n"
                + "select stock_code,sum(in_qty) qty,in_wt,in_unit,loc_code\n"
                + "from stock_in_out_detail\n"
                + "where (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "and in_qty is not null\n"
                + "group by stock_code,in_wt,in_unit,loc_code\n"
                + "	union all\n"
                + "select stock_code,sum(out_qty)*-1 qty,out_wt,out_unit,loc_code\n"
                + "from stock_in_out_detail\n"
                + "where (stock_code = '" + stockCode + "' or '-' ='" + stockCode + "')\n"
                + "and out_qty is not null\n"
                + "group by stock_code,in_wt,in_unit,loc_code\n"
                + ")a\n"
                + "group by stock_code,wt,unit,loc_code";
        execSQL(delSql, insertSql);
        String hsql = "select o from VStockBalance o where o.key.macId =" + macId + "";
        return findHSQL(hsql);
    }

}
