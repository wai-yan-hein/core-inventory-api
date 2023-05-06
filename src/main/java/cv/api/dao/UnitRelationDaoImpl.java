package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.RelationKey;
import cv.api.entity.StockUnit;
import cv.api.entity.StockUnitKey;
import cv.api.entity.UnitRelation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
@Slf4j
public class UnitRelationDaoImpl extends AbstractDao<RelationKey, UnitRelation> implements UnitRelationDao {
   @Autowired
   private UnitRelationDetailDao dao;
    @Override
    public UnitRelation save(UnitRelation ur) {
        saveOrUpdate(ur,ur.getKey());
        return ur;
    }

    @Override
    public UnitRelation findByKey(RelationKey key) {
        return getByKey(key);
    }

    @Override
    public List<UnitRelation> findRelation(String compCode, Integer deptId) {
        String hsql = "select o from UnitRelation o where o.key.compCode ='" + compCode + "' and (o.key.deptId =" + deptId + " or 0 =" + deptId + ")";
        return findHSQL(hsql);
    }

    @Override
    public List<StockUnit> getRelation(String relCode, String compCode, Integer deptId) {
        List<StockUnit> list = new ArrayList<>();
        String sql = "select unit from unit_relation_detail where rel_code ='" + relCode + "' and comp_code ='" + compCode + "' and dept_id =" + deptId + "";
        ResultSet rs = getResult(sql);
        try {
            while (rs.next()) {
                StockUnitKey key = new StockUnitKey();
                key.setUnitCode(rs.getString("unit"));
                key.setCompCode(compCode);
                key.setDeptId(deptId);
                StockUnit u = new StockUnit();
                u.setKey(key);
                list.add(u);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return list;
    }


    @Override
    public List<UnitRelation> unUpload() {
        String hsql = "select o from UnitRelation o where o.intgUpdStatus is null";
        List<UnitRelation> list = findHSQL(hsql);
        list.forEach(o -> {
            String code = o.getKey().getRelCode();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setDetailList(dao.getRelationDetail(code, compCode, deptId));
        });
        return list;
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from unit_relation";
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
    public List<UnitRelation> getRelation(String updatedDate) {
        String hsql = "select o from UnitRelation o where o.updatedDate >'" + updatedDate + "'";
        List<UnitRelation> list = findHSQL(hsql);
        list.forEach(o -> {
            String code = o.getKey().getRelCode();
            String compCode = o.getKey().getCompCode();
            Integer deptId = o.getKey().getDeptId();
            o.setDetailList(dao.getRelationDetail(code, compCode, deptId));
        });
        return list;
    }
}
