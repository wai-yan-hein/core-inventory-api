package cv.api.dao;

import cv.api.entity.LabourGroup;
import cv.api.entity.LabourGroupKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface LabourGroupDao {
    LabourGroup save(LabourGroup LabourGroup);

    List<LabourGroup> findAll(String compCode);

    int delete(LabourGroupKey key);

    LabourGroup findById(LabourGroupKey id);


    Date getMaxDate();

    List<LabourGroup> getLabourGroup(LocalDateTime updatedDate);
}