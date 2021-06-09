/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;

import com.cv.inv.api.entity.RetOutHis;
import java.sql.ResultSet;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lenovo
 */
@Repository
public class RetOutDaoImpl extends AbstractDao<String, RetOutHis> implements RetOutDao {

    @Override
    public RetOutHis save(RetOutHis retOutDetailHis) {
        persist(retOutDetailHis);
        return retOutDetailHis;
    }

    @Override
    public List<RetOutHis> search(String fromDate, String toDate, String cusId, String locId, String vouNo, String filterCode) {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.retOutDate) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.retOutDate) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.retOutDate) >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " and date(o.retOutDate) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "date(o.retOutDate) <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(o.retOutDate) <= '" + toDate + "'";
            }
        }

        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.customer = '" + cusId + "'";
            } else {
                strFilter = strFilter + " and o.customer = '" + cusId + "'";
            }
        }

        if (!locId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.location = '" + locId + "'";
            } else {
                strFilter = strFilter + " and o.location = '" + locId + "'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.retOutId = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.retOutId = '" + vouNo + "'";
            }
        }
        if (!filterCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.remark = '" + filterCode + "'";
            } else {
                strFilter = strFilter + " and o.remark = '" + filterCode + "'";
            }
        }
        String strSql = "select o from RetOutHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " and o.deleted is not true";
        }

        List<RetOutHis> listPurHis = findHSQL(strSql);
        return listPurHis;
    }

    @Override
    public ResultSet searchM(String fromDate, String toDate,
            String cusId, String locId, String vouNo, String filterCode) throws Exception {
        String strFilter = "";

        if (!fromDate.equals("-") && !toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = " date(retout.ret_out_date) between '" + fromDate
                        + "' and '" + toDate + "'";
            } else {
                strFilter = strFilter + " and date(retout.ret_out_date) between '"
                        + fromDate + "' and '" + toDate + "'";
            }
        } else if (!fromDate.endsWith("-")) {
            if (strFilter.isEmpty()) {
                strFilter = " date(retout.ret_out_date) >= '" + fromDate + "'";
            } else {
                strFilter = strFilter + " date(retout.ret_out_date) >= '" + fromDate + "'";
            }
        } else if (!toDate.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = " date(retout.ret_out_date) <= '" + toDate + "'";
            } else {
                strFilter = strFilter + " and  date(retout.ret_out_date) <= '" + toDate + "'";
            }
        }

        if (!cusId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "retout.cus_id = '" + cusId + "'";
            } else {
                strFilter = strFilter + " and retout.cus_id = '" + cusId + "'";
            }
        }

        if (!locId.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "retout.location = '" + locId + "'";
            } else {
                strFilter = strFilter + " and retout.location = '" + locId + "'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = " retout.ret_out_id = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and retout.ret_out_id = '" + vouNo + "'";
            }
        }
        if (!filterCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "retout.remark = '" + filterCode + "'";
            } else {
                strFilter = strFilter + " and retout.remark = '" + filterCode + "'";
            }
        }
        ResultSet rs = null;
        if (!strFilter.isEmpty()) {
            strFilter = "select distinct retout.ret_out_date, retout.ret_out_id, retout.remark, td.trader_name,\n"
                    + " retout.vou_total, retout.deleted,l.location_name,\n"
                    + " apu.user_short_name\n"
                    + " from ret_out_his retout\n"
                    + " join ret_out_his_detail rohd on  rohd.voucher_no=retout.ret_out_id\n"
                    + " join location l on retout.location = l.location_id\n"
                    + " join appuser apu on retout.created_by = apu.user_code\n"
                    + " left join trader td on retout.cus_id=td.id\n"
                    + " where " + strFilter
                    + " and retout.deleted=false\n"
                    + " order by retout.ret_out_date desc , retout.ret_out_id desc";
            rs = getResultSet(strFilter);
        }

        return rs;
    }

    @Override
    public RetOutHis findById(String id) {
        RetOutHis ph = getByKey(id);
        return ph;
    }

    @Override
    public int delete(String id) throws Exception {
        String strSql = "update ret_out_his set deleted = true where ret_out_id = '" + id + "'";
        execSQL(strSql);
        return 1;
    }

}
