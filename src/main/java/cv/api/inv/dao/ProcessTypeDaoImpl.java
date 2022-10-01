package cv.api.inv.dao;

import cv.api.inv.entity.ProcessType;
import cv.api.inv.entity.ProcessTypeKey;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProcessTypeDaoImpl extends AbstractDao<ProcessTypeKey, ProcessType> implements ProcessTypeDao {
    @Override
    public ProcessType save(ProcessType type) {
        persist(type);
        return type;
    }

    @Override
    public List<ProcessType> getProcessType(String compCode) {
        String hsql = "select o from ProcessType o where o.key.compCode = '" + compCode + "'";
        return findHSQL(hsql);
    }
}
