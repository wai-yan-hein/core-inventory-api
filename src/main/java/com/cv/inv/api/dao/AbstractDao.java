package com.cv.inv.api.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractDao<PK extends Serializable, T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);
    private final Class<T> persistentClass;
    private ResultSet rs = null;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Autowired
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public T getByKey(PK key) {
        return (T) getSession().get(persistentClass, key);
    }

    public void persist(T entity) {
        try {
            getSession().saveOrUpdate(entity);
        } catch (Exception e) {
            logger.error("persiste  :" + e.getMessage());
        }
    }

    public void delete(T entity) {
        getSession().delete(entity);
    }

    protected Criteria createEntityCriteria() {
        return getSession().createCriteria(persistentClass);
    }

    public List<T> findHSQL(String hsql) {
        List<T> list = null;
        try {
            Query query = getSession().createQuery(hsql);
            list = query.list();
        } catch (Exception e) {
            logger.error("findHSQL  :" + e.getMessage());
        }
        return list;

    }

    public List findHSQLPC(String hsql, String filterName, String paramName, String paramValue) {
        Session session = getSession();
        Filter filter = session.enableFilter(filterName);
        filter.setParameter(paramName, paramValue);
        Query query = session.createQuery(hsql);
        List list = query.list();
        session.disableFilter(filterName);
        return list;
    }

    public List findHSQLList(String hsql) {
        Query query = getSession().createQuery(hsql);
        List list = query.list();
        return list;
    }

    public int execUpdateOrDelete(String hsql) {
        Query query = getSession().createQuery(hsql);
        int cnt = query.executeUpdate();
        return cnt;
    }

    public Object exeSQL(String hsql) {
        Query query = getSession().createQuery(hsql);
        Object obj = query.uniqueResult();
        return obj;

    }

    public Object findByKey(Class type, Serializable id) {
        Object obj = null;

        try {
            if (!id.equals("")) {

                obj = getSession().get(type, id);
                //tran.commit();
            }
        } catch (Exception ex) {
            logger.error("find1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        return obj;

    }

    public List<T> saveBatch(List<T> list) {
        list.forEach(obj -> {
            persist(obj);
        });
        return list;
    }

    public void execProc(String procName, String... parameters) {
        String strSQL = "{call " + procName + "(";
        String tmpStr = "";

        for (String parameter : parameters) {
            if (tmpStr.isEmpty()) {
                tmpStr = "?";
            } else {
                tmpStr = tmpStr + ",?";
            }
        }

        strSQL = strSQL + tmpStr + ")}";

        NativeQuery query = getSession().createSQLQuery(strSQL);
        int i = 0;
        for (String prm : parameters) {
            query.setParameter(i, prm);
            i++;
        }

        query.executeUpdate();
    }

    public void execSQL(String... strSql) throws Exception {
        for (String sql : strSql) {
            NativeQuery query = getSession().createSQLQuery(sql);
            query.executeUpdate();
        }
    }

    /*public void execSQL(List<String> listSql) throws Exception {
        listSql.stream().map(sql -> getSession().createSQLQuery(sql)).forEachOrdered(query -> {
            query.executeUpdate();
        });
    }*/
    public String getGlLogSql(String glCode, String actionType, String userCode, Integer macId) {
        String strSql = "insert \n"
                + "into gl_log(gl_code, gl_date, created_date, modify_date, modify_by, description,\n"
                + "            source_ac_id, account_id, to_cur_id,from_cur_id, ex_rate, dr_amt,\n"
                + "            cr_amt, reference, dept_code, voucher_no, user_code, trader_code, \n"
                + "            cheque_no, comp_code, gst, tran_source, bank_code, gl_vou_no, split_id, \n"
                + "            intg_upd_status, remark, from_desp, to_desp, naration, project_id, location_id,\n"
                + "            ref_no, cerdit_term, mac_id,log_user_code,log_mac_id)\n"
                + "     select gl_code, gl_date, created_date, modify_date, modify_by, description,\n"
                + "            source_ac_id, account_id, to_cur_id,from_cur_id, ex_rate, dr_amt,\n"
                + "            cr_amt, reference, dept_code, voucher_no, user_code, trader_code, \n"
                + "            cheque_no, comp_code, gst, '" + actionType + "', bank_code, gl_vou_no, split_id, \n"
                + "            intg_upd_status, remark, from_desp, to_desp, naration, project_id, location_id,\n"
                + "            ref_no, cerdit_term, mac_id,'" + userCode + "'," + macId + "\n"
                + "	from gl where gl_code = '" + glCode + "'";
        return strSql;
    }

    public Object getAggregate(String sql) {
        NativeQuery query = getSession().createSQLQuery(sql);
        Object obj = query.uniqueResult();
        return obj;
    }

    public void doWork(Work work) {
        Session sess = getSession();
        sess.doWork(work);
    }

    public ResultSet getResultSet(final String strSql) throws Exception {
        rs = null;

        Work work = (Connection con) -> {
            try {
                PreparedStatement pstmt = con.prepareStatement(strSql);
                rs = pstmt.executeQuery();
            } catch (SQLException ex) {
                logger.error("getResultSet : " + strSql + " : " + ex.getMessage());
            }
        };
        doWork(work);

        return rs;
    }
}
