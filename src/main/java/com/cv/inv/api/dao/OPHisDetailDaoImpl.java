package com.cv.inv.api.dao;

import com.cv.inv.api.entity.OPHisDetail;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OPHisDetailDaoImpl extends AbstractDao<String, OPHisDetail> implements OPHisDetailDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public OPHisDetail save(OPHisDetail op) {
        persist(op);
        return op;
    }

    @Override
    public List<OPHisDetail> search(String vouNo) {
        String hsql = "select o from OPHisDetail o where o.vouNo ='" + vouNo + "'";
        Query<OPHisDetail> query = sessionFactory.getCurrentSession().createQuery(hsql, OPHisDetail.class);
        return query.list();
    }

    @Override
    public int delete(String opCode) {
        String delSql = "delete from OPHisDetail o where o.opCode = '" + opCode + "'";
        return execUpdateOrDelete(delSql);
    }
}
