package cv.api.service;

import cv.api.entity.TraderGroup;
import cv.api.entity.TraderGroupKey;

import java.util.List;

public interface TraderGroupService {
    TraderGroup save(TraderGroup group);

    List<TraderGroup> getTraderGroup(String compCode, Integer deptId);

    List<TraderGroup> unUpload();

    TraderGroup findById(TraderGroupKey key);
}
