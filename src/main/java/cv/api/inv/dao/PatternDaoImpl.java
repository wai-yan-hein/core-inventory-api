package cv.api.inv.dao;

import cv.api.inv.entity.Pattern;
import cv.api.inv.entity.PatternDetail;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
public class PatternDaoImpl implements PatternDao {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Pattern findByCode(String code) {
        return sessionFactory.getCurrentSession().get(Pattern.class, code);
    }

    @Override
    public Pattern save(Pattern pattern) {
        sessionFactory.getCurrentSession().saveOrUpdate(pattern);
        return pattern;
    }

    @Override
    public PatternDetail save(PatternDetail pd) {
        sessionFactory.getCurrentSession().saveOrUpdate(pd);
        return pd;
    }

    @Override
    public List<Pattern> search(String compCode, Boolean active) {
        String hsql = "select o from Pattern o where o.compCode ='" + compCode + "'";
        if (!Objects.isNull(active)) {
            hsql = hsql.concat(" and o.active = " + active + "");
        }
        Query<Pattern> query = sessionFactory.getCurrentSession().createQuery(hsql, Pattern.class);
        return query.list();
    }

    @Override
    public List<PatternDetail> searchDetail(String code) {
        String hsql = "select o from PatternDetail o where o.patternCode = '" + code + "'";
        Query<PatternDetail> query = sessionFactory.getCurrentSession().createQuery(hsql, PatternDetail.class);
        return query.list();
    }
}
