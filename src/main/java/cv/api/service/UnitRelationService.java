package cv.api.service;

import cv.api.entity.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface UnitRelationService {
    UnitRelation save(UnitRelation ur);

    UnitRelation findByKey(RelationKey key);

    List<UnitRelation> findRelation(String compCode, Integer deptId);

    List<StockUnit> getRelation(String relCode, String compCode, Integer deptId);

    UnitRelationDetail save(UnitRelationDetail unit);

    List<UnitRelationDetail> getRelationDetail(String code, String compCode);

    UnitRelationDetail findByKey(UnitRelationDetailKey key);

    List<UnitRelation> unUpload();

    List<UnitRelation> getRelation(LocalDateTime updatedDate);


}

