package cv.api.dao;

import cv.api.entity.RelationKey;
import cv.api.entity.StockUnit;
import cv.api.entity.UnitRelation;

import java.time.LocalDateTime;
import java.util.List;

public interface UnitRelationDao {
    UnitRelation save(UnitRelation ur);

    UnitRelation findByKey(RelationKey key);

    List<UnitRelation> findRelation(String compCode, Integer deptId);

    List<StockUnit> getRelation(String relCode, String compCode, Integer deptId);

    List<UnitRelation> unUpload();

    List<UnitRelation> getRelation(LocalDateTime updatedDate);


}

