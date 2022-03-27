/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;


import cv.api.inv.entity.DamageHis;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wai yan
 */
@Repository
public class DamageHisDaoImpl extends AbstractDao<String, DamageHis> implements DamageHisDao {
    @Autowired
    private SessionFactory sessionFactory;

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
            Query<DamageHis> query = sessionFactory.getCurrentSession().createQuery(strSql, DamageHis.class);
            listDH = query.list();
        }

        return listDH;
    }

    @Override
    public int delete(String vouNo) {
        String strSql1 = "delete from DamageDetailHis o where o.dmgVouId = '" + vouNo + "'";
        execUpdateOrDelete(strSql1);
        String strSql = "delete from DamageHis o where o.dmgVouId = '" + vouNo + "'";
        return execUpdateOrDelete(strSql);
    }

}
