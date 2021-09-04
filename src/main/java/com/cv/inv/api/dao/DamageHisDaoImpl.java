/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.dao;


import com.cv.inv.api.entity.DamageHis;

import java.sql.ResultSet;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author lenovo
 */
@Repository
public class DamageHisDaoImpl extends AbstractDao<String, DamageHis> implements DamageHisDao {

    @Override
    public DamageHis save(DamageHis ph) {
        persist(ph);
        return ph;
    }

    @Override
    public DamageHis findById(String id) {
        return getByKey(id);
    }

    @Override
    public List<DamageHis> search(String from, String to, String location, String remark, String vouNo) {
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            strFilter = "v.dmgDate between '" + from
                    + "' and '" + to + "'";
        } else if (!from.equals("-")) {
            strFilter = "v.dmgDate >= '" + from + "'";
        } else if (!to.equals("-")) {
            strFilter = "v.dmgDate <= '" + to + "'";
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
                strFilter = "v.dmgVouId like '%" + vouNo + "%'";
            } else {
                strFilter = strFilter + " like v.dmgVouId '%" + vouNo + "%'";
            }
        }

        String strSql = "select distinct v from DamageHis v";

        List<DamageHis> listDH = null;
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter;
            listDH = findHSQL(strSql);
        }

        return listDH;
    }

    @Override
    public ResultSet searchM(String from, String to, String location,
                             String remark, String vouNo) throws Exception {
        String strFilter = "";

        if (!from.equals("-") && !to.equals("-")) {
            strFilter = "date(dmg.dmg_date) between '" + from
                    + "' and '" + to + "'";
        } else if (!from.equals("-")) {
            strFilter = "date(dmg.dmg_date) >= '" + from + "'";
        } else if (!to.equals("-")) {
            strFilter = "date(dmg.dmg_date) <= '" + to + "'";
        }

        if (!location.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "dmg.location = " + location;
            } else {
                strFilter = strFilter + " and dmg.location = " + location;
            }
        }

        if (!remark.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "dmg.remark like '%" + remark + "%'";
            } else {
                strFilter = strFilter + " like dmg.remark '%" + remark + "%'";
            }
        }

        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "dmg.dmg_id like '%" + vouNo + "%'";
            } else {
                strFilter = strFilter + " like dmg.dmg_id '%" + vouNo + "%'";
            }
        }

        ResultSet rs = null;
        if (!strFilter.isEmpty()) {
            strFilter = new StringBuilder().
                    append("select date(dmg.dmg_date)as dmg_date, dmg.dmg_id, dmg.remark,dmg.amount, ")
                    .append(" dmg.deleted, l.location_name, \n")
                    .append(" apu.user_short_name from dmg_his dmg\n")
                    .append(" join dmg_detail_his ddh on ddh.dmg_id=dmg.dmg_id\n")
                    .append(" left join location l on dmg.location = l.location_id\n")
                    .append(" left join appuser apu on dmg.created_by = apu.user_code\n")
                    .append(" where ").append(strFilter).append(" and dmg.deleted=false\n")
                    .append(" group by dmg.dmg_date, dmg.dmg_id\n").append(" order by dmg_date desc").toString();
            rs = getResultSet(strFilter);
        }
        return rs;
    }

    @Override
    public int delete(String vouNo) {
        String strSql1 = "delete from DamageDetailHis o where o.dmgVouId = '" + vouNo + "'";
        execUpdateOrDelete(strSql1);
        String strSql = "delete from DamageHis o where o.dmgVouId = '" + vouNo + "'";
        return execUpdateOrDelete(strSql);
    }

}
