package cv.api.service;


import cv.api.entity.LabourGroup;
import cv.api.entity.LabourGroupKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface LabourGroupService {
    LabourGroup save(LabourGroup status);
    List<LabourGroup> findAll(String compCode);
    int delete(LabourGroupKey key);
    LabourGroup findById(LabourGroupKey key);

    Date getMaxDate();

    List<LabourGroup> getLabourGroup(LocalDateTime updatedDate);
}
