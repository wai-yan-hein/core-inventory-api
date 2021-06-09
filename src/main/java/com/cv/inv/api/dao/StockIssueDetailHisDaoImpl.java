/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.StockIssueDetailHis;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class StockIssueDetailHisDaoImpl extends AbstractDao<Long, StockIssueDetailHis> implements StockIssueDetailHisDao {

    @Override
    public StockIssueDetailHis save(StockIssueDetailHis sdh) {
        persist(sdh);
        return sdh;
    }

    @Override
    public StockIssueDetailHis findById(Long id) {
        StockIssueDetailHis sdh = getByKey(id);
        return sdh;
    }

    @Override
    public List<StockIssueDetailHis> search(String saleInvId) {
        String strFilter = "";
          if (!saleInvId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "v.issueId = '" + saleInvId+"'";
            } else {
                strFilter = strFilter + " and v.issueId = '" + saleInvId+"'";
            }
        }
            String strSql = "select v from StockIssueDetailHis v";

        List<StockIssueDetailHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public int delete(String id) {
        String strSql = "delete from StockIssueDetailHis o where o.tranId = " + id;
        int cnt = execUpdateOrDelete(strSql);
        return cnt;
    }

}
