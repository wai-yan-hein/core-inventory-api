package cv.api.inv.dao;

import cv.api.inv.entity.TraderGroup;
import cv.api.inv.entity.TraderGroupKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraderGroupDaoImpl extends AbstractDao<TraderGroupKey, TraderGroup> implements TraderGroupDao {
    @Override
    public TraderGroup save(TraderGroup group) {
        persist(group);
        return group;
    }

    @Override
    public List<TraderGroup> getTraderGroup(String compCode, Integer deptId) {
        String hsql = "select o from TraderGroup o where o.key.compCode ='" + compCode + "' and o.key.deptId = " + deptId + "";
        return findHSQL(hsql);
    }

    @Override
    public List<TraderGroup> unUpload() {
        String hsql = "select o from TraderGroup o where intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public TraderGroup findById(TraderGroupKey key) {
        return getByKey(key);
    }
}
