package cv.api.inv.dao;

import cv.api.inv.entity.SysProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class SysPropertyDaoImpl extends AbstractDao<String, SysProperty> implements SysPropertyDao {
    @Override
    public SysProperty save(SysProperty property) throws Exception {
        persist(property);
        return property;
    }

    @Override
    public List<SysProperty> search(String compCode) {
        String hsql = "select o from SysProperty o where o.compCode ='" + compCode + "'";
        return findHSQL(hsql);
    }
}
