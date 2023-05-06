package cv.api.dao;

import cv.api.entity.*;

import java.util.Date;
import java.util.List;

public interface UnitRelationDetailDao {
    UnitRelationDetail save(UnitRelationDetail unit);

    List<UnitRelationDetail> getRelationDetail(String code, String compCode, Integer deptId);

    UnitRelationDetail findByKey(UnitRelationDetailKey key);
}

