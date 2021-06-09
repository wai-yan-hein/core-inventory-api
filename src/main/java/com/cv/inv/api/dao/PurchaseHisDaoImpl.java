/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.PurHis;
import java.sql.ResultSet;


import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mg Kyaw Thura Aung
 */
@Repository
public class PurchaseHisDaoImpl extends AbstractDao<String, PurHis> implements PurchaseHisDao {

    @Override
    public PurHis save(PurHis sh) {
        persist(sh);
        return sh;
    }

    @Override
    public List<PurHis> search(String fromDate, String toDate, String cusId, String vouStatusId, String remark) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.purDate) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.purDate) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.purDate) >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and date(o.purDate) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.purDate) <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.purDate) <= '" + toDate + "'";
            }
        }

        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.customerId = '" + cusId + "'";
            } else {
                strFilter = strFilter + " and o.customerId = '" + cusId + "'";
            }
        }

        if (!vouStatusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouStatus = '" + vouStatusId + "'";
            } else {
                strFilter = strFilter + " and o.vouStatus = '" + vouStatusId + "'";
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark = '" + remark + "'";
            } else {
                strFilter = strFilter + " and o.remark = '" + remark + "'";
            }
        }

        String strSql = "select o from PurHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " and o.deleted is not true";
        }

        List<PurHis> listPurHis = findHSQL(strSql);
        return listPurHis;
    }

    @Override
    public PurHis findById(String id) {
        PurHis ph = getByKey(id);
        return ph;
    }

    @Override
    public ResultSet searchM(String fromDate, String toDate,
            String cusId, String vouStatusId, String remark) throws Exception {
        String strFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(ph.pur_date) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(ph.pur_date) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(ph.pur_date) >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and date(ph.pur_date) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(ph.pur_date) <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(ph.pur_date) <= '" + toDate + "'";
            }
        }

        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "ph.trader_code = '" + cusId + "'";
            } else {
                strFilter = strFilter + " and ph.trader_code = '" + cusId + "'";
            }
        }

        if (!vouStatusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "ph.vou_status = '" + vouStatusId + "'";
            } else {
                strFilter = strFilter + " and ph.vou_status = '" + vouStatusId + "'";
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "ph.remark = '" + remark + "'";
            } else {
                strFilter = strFilter + " and ph.remark = '" + remark + "'";
            }
        }
        ResultSet rs = null;
        if (!strFilter.isEmpty()) {
            strFilter = "select distinct ph.pur_date, ph.vou_no, ph.remark,td.trader_name,\n"
                    + " ph.vou_total,apu.user_short_name,ph.deleted  from pur_his ph\n"
                    + "                     join  pur_his_detail phd on phd.vou_id=ph.vou_no\n"
                    + "                    join location l on ph.loc_code = l.location_code\n"
                    + "                     join appuser apu on ph.created_by = apu.app_user_code\n"
                    + "                     left join trader td on ph.trader_code=td.code\n"
                    + "                     where \n"
                    + strFilter
                    + "                     and ph.deleted=false\n"
                    + "                    order by ph.pur_date  desc ,ph.vou_no desc";
            rs = getResultSet(strFilter);
        }

        return rs;

    }

    @Override
    public int delete(String vouNo) throws Exception {
        String strSql = "update pur_his set pur_his = true where vou_no = '" + vouNo + "'";
        execSQL(strSql);
        return 1;
    }

}
