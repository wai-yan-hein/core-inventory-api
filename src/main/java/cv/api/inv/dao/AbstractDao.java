package cv.api.inv.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
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

public abstract class AbstractDao<PK extends Serializable, T> {

    private static final Logger log = LoggerFactory.getLogger(AbstractDao.class);
    private Class<T> persistentClass;
    private ResultSet rs = null;
    @Autowired
    private SessionFactory sessionFactory;

    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    public AbstractDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() {
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

    public void deleteEntity(T entity) {
        getSession().delete(entity);
    }

    public List<T> findHSQL(String hsql) {
        List<T> list = null;
        try {
            Query<T> query = getSession().createQuery(hsql, persistentClass);
            list = query.list();
        } catch (Exception e) {
            log.error("findHSQL  :" + e.getMessage());
        }
        return list;
    }

    public List<T> findHSQL(String hsql, int limit) {
        List<T> list = null;
        try {
            Query<T> query = getSession().createQuery(hsql, persistentClass);
            query.setMaxResults(limit);
            list = query.list();
        } catch (Exception e) {
            log.error("findHSQL  :" + e.getMessage());
        }
        return list;

    }

    public int execUpdateOrDelete(String hsql) {
        Query<T> query = getSession().createQuery(hsql, persistentClass);
        return query.executeUpdate();
    }

    public void execSQL(String... strSql) {
        for (String sql : strSql) {
            getSession().createNativeQuery(sql).executeUpdate();
        }
    }

    public void doWork(Work work) {
        Session sees = getSession();
        sees.doWork(work);
    }

    public ResultSet getResultSet(final String sql) {
        Work work = (Connection con) -> {
            try {
                PreparedStatement stmt = con.prepareStatement(sql);
                rs = null;
                rs = stmt.executeQuery();
            } catch (SQLException ex) {
                throw new IllegalStateException(ex.getMessage());
            }
        };
        doWork(work);
        return rs;
    }
}
