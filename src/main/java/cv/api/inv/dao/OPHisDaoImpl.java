package cv.api.inv.dao;

import cv.api.inv.entity.OPHis;
import cv.api.inv.entity.OPHisKey;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OPHisDaoImpl extends AbstractDao<OPHisKey, OPHis> implements OPHisDao {
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
    public OPHis findByCode(OPHisKey key) {
        return getByKey(key);
    }

}
