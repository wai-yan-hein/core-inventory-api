package cv.api.inv.dao;

import cv.api.inv.entity.TraderGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TraderGroupDaoImpl extends AbstractDao<String, TraderGroup> implements TraderGroupDao {
    @Override
    public TraderGroup save(TraderGroup group) {
        persist(group);
        return group;
    }

    @Override
    public List<TraderGroup> getTraderGroup(String compCode,Integer deptId) {
        String hsql = "select o from TraderGroup o where o.key.compCode ='" + compCode + "' and o.key.deptId = "+deptId+"";
        return findHSQL(hsql);
    }
}
