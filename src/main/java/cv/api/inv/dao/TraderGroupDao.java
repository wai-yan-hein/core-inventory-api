package cv.api.inv.dao;

import cv.api.inv.entity.TraderGroup;
import cv.api.inv.entity.TraderGroupKey;

import java.util.List;

public interface TraderGroupDao {
    TraderGroup save(TraderGroup group);

    List<TraderGroup> getTraderGroup(String compCode,Integer deptId);
    List<TraderGroup> unUpload();
    TraderGroup findById(TraderGroupKey key);


}
