package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.WareHouse;
import cv.api.entity.LabourGroupKey;
import cv.api.entity.WareHouseKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class WareHouseDaoImpl extends AbstractDao<WareHouseKey, WareHouse> implements WareHouseDao{
    @Override
    public WareHouse save(WareHouse g) {
        g.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(g, g.getKey());
        return g;
    }

    @Override
    public List<WareHouse> findAll(String compCode) {
        String hsql = "select o from WareHouse o where o.key.compCode = '" + compCode + "' and o.deleted =false";
        return findHSQL(hsql);
    }

    @Override
    public int delete(WareHouseKey key) {
        remove(key);
        return 1;
    }

    @Override
    public WareHouse findById(WareHouseKey id) {
        return getByKey(id);
    }


    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from WareHouse";
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
    public List<WareHouse> getWareHouse(LocalDateTime updatedDate) {
        String hsql = "select o from WareHouse o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
