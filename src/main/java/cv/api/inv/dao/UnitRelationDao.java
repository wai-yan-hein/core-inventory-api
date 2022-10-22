package cv.api.inv.dao;

import cv.api.inv.entity.RelationKey;
import cv.api.inv.entity.UnitRelation;
import cv.api.inv.entity.UnitRelationDetail;
import cv.api.inv.entity.UnitRelationDetailKey;

import java.util.List;

public interface UnitRelationDao {
    UnitRelation save(UnitRelation ur);
    UnitRelation findByKey(RelationKey key);

    List<UnitRelation> findRelation( String compCode, Integer deptId);

    UnitRelationDetail save(UnitRelationDetail unit);

    List<UnitRelationDetail> getRelationDetail(String code, String compCode, Integer deptId);

    UnitRelationDetail findByKey(UnitRelationDetailKey key);
    List<UnitRelation> unUpload();

}

