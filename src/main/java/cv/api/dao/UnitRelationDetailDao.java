package cv.api.dao;

import cv.api.entity.UnitRelationDetail;
import cv.api.entity.UnitRelationDetailKey;

import java.util.List;

public interface UnitRelationDetailDao {
    UnitRelationDetail save(UnitRelationDetail unit);

    List<UnitRelationDetail> getRelationDetail(String code, String compCode);

    UnitRelationDetail findByKey(UnitRelationDetailKey key);
}

