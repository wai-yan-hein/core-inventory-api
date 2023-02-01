package cv.api.dao;

import cv.api.entity.ReorderLevel;
import org.springframework.stereotype.Repository;

@Repository
public class ReorderDaoImpl extends AbstractDao<String, ReorderLevel> implements ReorderDao {
    @Override
    public ReorderLevel save(ReorderLevel rl) {
        persist(rl);
        return rl;
    }
}
