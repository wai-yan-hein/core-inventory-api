package cv.api.inv.dao;

import cv.api.inv.entity.RelationKey;
import cv.api.inv.entity.UnitRelation;
import cv.api.inv.entity.UnitRelationDetail;
import cv.api.inv.entity.UnitRelationDetailKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UnitRelationDaoImpl extends AbstractDao<RelationKey, UnitRelation> implements UnitRelationDao {
    @Override
    public UnitRelation save(UnitRelation ur) {
        persist(ur);
        return ur;
    }

    @Override
    public UnitRelation findByKey(RelationKey key) {
        return getByKey(key);
    }

    @Override
    public List<UnitRelation> findRelation(String compCode, Integer deptId) {
        String hsql = "select o from UnitRelation o and o.compCode ='" + compCode + "' and o.deptId =" + deptId + "";
        return findHSQL(hsql);
    }

    @Override
    public UnitRelationDetail save(UnitRelationDetail unit) {
        getSession().saveOrUpdate(unit);
        return unit;
    }

    @Override
    public List<UnitRelationDetail> getRelationDetail(String code, String compCode, Integer deptId) {
        String hsql;
        if (code.equals("-")) {
            hsql = "select o from UnitRelationDetail o where o.key.compCode ='" + compCode + "' and o.key.deptId =" + deptId + "";
        } else {
            hsql = "select o from UnitRelationDetail o where o.key.relCode = '" + code + "' and o.key.compCode ='" + compCode + "' and o.key.deptId =" + deptId + "";
        }
        return getSession().createQuery(hsql, UnitRelationDetail.class).list();
    }

    @Override
    public UnitRelationDetail findByKey(UnitRelationDetailKey key) {
        return getSession().get(UnitRelationDetail.class, key);
    }

    @Override
    public List<UnitRelation> unUpload() {
        String hsql = "select o from UnitRelation o where o.intgUpdStatus is null";
        List<UnitRelation> list = findHSQL(hsql);
        list.forEach(o -> {
            String code = o.getKey().getRelCode();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setDetailList(getRelationDetail(code, compCode, deptId));
        });
        return list;
    }
}
