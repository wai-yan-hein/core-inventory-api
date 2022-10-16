package cv.api.inv.service;

import cv.api.inv.entity.TraderGroup;

import java.util.List;

public interface TraderGroupService {
    TraderGroup save(TraderGroup group);

    List<TraderGroup> getTraderGroup(String compCode, Integer deptId);
    List<TraderGroup> unUpload();
}
