/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.StockReceiveHis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class StockReceiveHisDaoImpl extends AbstractDao<String, StockReceiveHis> implements StockReceiveHisDao {

    @Autowired
    private StockReceiveDetailHisDao detaildao;

    @Override
    public StockReceiveHis save(StockReceiveHis ph) {
        persist(ph);
        return ph;
    }

    @Override
    public StockReceiveHis findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<StockReceiveHis> search(String from, String to, String location, String remark, String vouNo) {
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            strFilter = "v.receiveDate between '" + from
                    + "' and '" + to + "'";
        } else if (!from.equals("-")) {
            strFilter = "v.receiveDate >= '" + from + "'";
        } else if (!to.equals("-")) {
            strFilter = "v.receiveDate <= '" + to + "'";
        }

        if (!location.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.location = " + location;
            } else {
                strFilter = strFilter + " and v.location = " + location;
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.remark like '%" + remark + "%'";
            } else {
                strFilter = strFilter + " like v.remark '%" + remark + "%'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.receivedId like '%" + vouNo + "%'";
            } else {
                strFilter = strFilter + " like v.receivedId '%" + vouNo + "%'";
            }
        }

        String strSql = "select distinct v from StockReceiveHis v";

        List<StockReceiveHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String vouNo) {
        String strSql1 = "delete from StockReceiveDetailHis o where o.refVou = '" + vouNo + "'";
        execUpdateOrDelete(strSql1);
        String strSql = "delete from StockReceiveHis o where o.receivedId = '" + vouNo + "'";
        return execUpdateOrDelete(strSql);
    }

}
