package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.LabourGroup;
import cv.api.entity.LabourGroupKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class LabourGroupDaoImpl extends AbstractDao<LabourGroupKey, LabourGroup> implements LabourGroupDao{
    @Override
    public LabourGroup save(LabourGroup g) {
        g.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(g, g.getKey());
        return g;
    }

    @Override
    public List<LabourGroup> findAll(String compCode) {
        String hsql = "select o from LabourGroup o where o.key.compCode = '" + compCode + "' and o.deleted =false";
        return findHSQL(hsql);
    }

    @Override
    public int delete(LabourGroupKey key) {
        remove(key);
        return 1;
    }

    @Override
    public LabourGroup findById(LabourGroupKey id) {
        return getByKey(id);
    }


    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from labour_group";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    @Override
    public List<LabourGroup> getLabourGroup(LocalDateTime updatedDate) {
        String hsql = "select o from LabourGroup o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
