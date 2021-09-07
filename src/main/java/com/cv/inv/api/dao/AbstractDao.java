package com.cv.inv.api.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;

public abstract class AbstractDao<PK extends Serializable, T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractDao.class);
    private Class<T> persistentClass;
    private ResultSet rs = null;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Autowired
    private SessionFactory sessionFactory;

    public AbstractDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public T getByKey(PK key) {
        return getSession().get(persistentClass, key);
    }

    public void persist(T entity) {
        try {
            getSession().saveOrUpdate(entity);
        } catch (Exception e) {
            throw new IllegalStateException("persist" + e.getMessage());
        }
    }

    public void delete(T entity) {
        getSession().delete(entity);
    }

    public List findHSQL(String hsql) {
        List list = null;
        try {
            Query query = getSession().createQuery(hsql);
            list = query.list();
        } catch (Exception e) {
            log.error("findHSQL  :" + e.getMessage());
        }
        return list;

    }

    public List findHSQLList(String hsql) {
        Query query = getSession().createQuery(hsql);
        return query.list();
    }

    public int execUpdateOrDelete(String hsql) {
        Query query = getSession().createQuery(hsql);
        return query.executeUpdate();
    }

    public Object exeSQL(String hsql) {
        Query query = getSession().createQuery(hsql);
        return query.uniqueResult();

    }

    @SuppressWarnings("unchecked")
    public Object findByKey(Class type, Serializable id) {
        Object obj = null;

        try {
            //tran.commit();
            if (!id.equals("")) {
                obj = getSession().get(type, id);
            }
        } catch (Exception ex) {
            log.error("find1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        }
        return obj;

    }

    public void execProc(String procName, String... parameters) {
        String strSQL = "{call " + procName + "(";
        StringBuilder tmpStr = new StringBuilder();

        for (String parameter : parameters) {
            if (tmpStr.length() == 0) {
                tmpStr = new StringBuilder("?");
            } else {
                tmpStr.append(",?");
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

    public Object getAggregate(String sql) {
        NativeQuery query = getSession().createSQLQuery(sql);
        return query.uniqueResult();
    }

    public void doWork(Work work) {
        Session sees = getSession();
        sees.doWork(work);
    }

    public ResultSet getResultSet(final String strSql) {
        rs = null;
        Work work = (Connection con) -> {
            try {
                PreparedStatement stmt = con.prepareStatement(strSql);
                rs = stmt.executeQuery();
            } catch (SQLException ex) {
                throw new IllegalStateException(ex.getMessage());
            }
        };
        doWork(work);

        return rs;
    }

    public void exportPDF(final String reportPath, final String exportPath,
            final String fontPath, final Map<String, Object> parameters) throws Exception {
        Work work = (Connection con) -> {
            try {
                parameters.put("REPORT_CONNECTION", con);
                JasperPrint jp = getReport(reportPath, parameters, con, fontPath);
                JasperExportManager.exportReportToPdfFile(jp, exportPath.concat(".pdf"));
                log.info("exportPDF.");
            } catch (Exception ex) {
                throw new IllegalStateException(ex.getMessage());
            }
        };
        doWork(work);
    }

    private JasperPrint getReport(String reportPath, Map<String, Object> parameters,
            Connection con, String fontPath) throws Exception {
        JasperPrint jp;
        reportPath = reportPath + ".jasper";
        JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", fontPath);
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
        jasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
        jp = JasperFillManager.fillReport(reportPath, parameters, con);
        return jp;
    }

}
