/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockIssueHis;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class StockIssueHisDaoImpl extends AbstractDao<String, StockIssueHis> implements StockIssueHisDao {

    @Autowired
    private StockIssueDetailHisDao detaildao;

    @Override
    public StockIssueHis save(StockIssueHis ph) {
        persist(ph);
        return ph;
    }

    @Override
    public StockIssueHis findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<StockIssueHis> search(String from, String to, String location, String remark, String vouNo) {
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            strFilter = "v.issueDate between '" + from
                    + "' and '" + to + "'";
        } else if (!from.equals("-")) {
            strFilter = "v.issueDate >= '" + from + "'";
        } else if (!to.equals("-")) {
            strFilter = "v.issueDate <= '" + to + "'";
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
                strFilter = "v.issueId like '%" + vouNo + "%'";
            } else {
                strFilter = strFilter + " like v.issueId '%" + vouNo + "%'";
            }
        }

        String strSql = "select distinct v from StockIssueHis v";

        List<StockIssueHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }
    
 

    @Override
    public int delete(String vouNo) {
        String strSql1 = "delete from StockIssueDetailHis o where o.issueId = '" + vouNo + "'";
        execUpdateOrDelete(strSql1);
        String strSql = "delete from StockIssueHis o where o.issueId = '" + vouNo + "'";
        return execUpdateOrDelete(strSql);
    }

}
