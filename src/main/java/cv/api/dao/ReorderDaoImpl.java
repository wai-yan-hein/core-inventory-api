package cv.api.dao;

import cv.api.entity.ReorderKey;
import cv.api.entity.ReorderLevel;
import org.springframework.stereotype.Repository;

@Repository
public class ReorderDaoImpl extends AbstractDao<ReorderKey, ReorderLevel> implements ReorderDao {
    @Override
    public ReorderLevel save(ReorderLevel rl) {
        saveOrUpdate(rl,rl.getKey());
        return rl;
    }
}
