package cv.api.inv.dao;

import cv.api.inv.entity.*;

import java.util.Date;
import java.util.List;

public interface UnitRelationDao {
    UnitRelation save(UnitRelation ur);
    UnitRelation findByKey(RelationKey key);

    List<UnitRelation> findRelation( String compCode, Integer deptId);
    List<StockUnit> getRelation(String relCode, String compCode, Integer deptId);


    UnitRelationDetail save(UnitRelationDetail unit);

    List<UnitRelationDetail> getRelationDetail(String code, String compCode, Integer deptId);

    UnitRelationDetail findByKey(UnitRelationDetailKey key);
    List<UnitRelation> unUpload();
    Date getMaxDate();
    List<UnitRelation> getRelation(String updatedDate);


}

