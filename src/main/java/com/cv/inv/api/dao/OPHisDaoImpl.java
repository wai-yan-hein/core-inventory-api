package com.cv.inv.api.dao;

import com.cv.inv.api.entity.OPHis;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OPHisDaoImpl extends AbstractDao<String, OPHis> implements OPHisDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public OPHis save(OPHis op) {
        persist(op);
        return op;
    }

    @Override
    public List<OPHis> search(String compCode) {
        String hsql = "select o from OPHis o where o.compCode ='" + compCode + "'";
        Query<OPHis> query = sessionFactory.getCurrentSession().createQuery(hsql, OPHis.class);
        return query.list();
    }

    @Override
    public OPHis findByCode(String vouNo) {
        return getByKey(vouNo);
    }

    @Override
    public List<OPHis> search(String fromDate, String toDate, String vouNo, String userCode, String compCode) {
        String strFilter = "";
        if (!fromDate.equals("-") && !toDate.equals("-")) {
            strFilter = "date(o.vouDate) between '" + fromDate
                    + "' and '" + toDate + "'";
        } else if (!fromDate.equals("-")) {
            strFilter = "date(o.vouDate) >= '" + fromDate + "'";
        } else if (!toDate.equals("-")) {
            strFilter = "date(o.vouDate) <= '" + toDate + "'";
        }
        if (!vouNo.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.vouNo = '" + vouNo + "'";
            } else {
                strFilter = strFilter + " and o.vouNo = '" + vouNo + "'";
            }
        }
        if (!userCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.createdBy = '" + userCode + "'";
            } else {
                strFilter = strFilter + " and o.createdBy = '" + userCode + "'";
            }
        }
        if (!compCode.equals("-")) {
            if (strFilter.isEmpty()) {
                strFilter = "o.compCode = '" + compCode + "'";
            } else {
                strFilter = strFilter + " and o.compCode = '" + compCode + "'";
            }
        }
        String strSql = "select o from OPHis o";
        if (!strFilter.isEmpty()) {
            strSql = strSql + " where " + strFilter + " order by o.vouDate desc";
        }
        Query<OPHis> query = sessionFactory.getCurrentSession().createQuery(strSql, OPHis.class);
        return query.list();
    }
}
