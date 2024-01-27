package cv.api.dao;

import cv.api.common.FilterObject;
import cv.api.entity.Job;
import cv.api.entity.JobKey;

import java.time.LocalDateTime;
import java.util.List;

public interface JobDao {
    Job save(Job Job);

    List<Job> findAll(FilterObject filterObject);

    int delete(JobKey key);

    Job findById(JobKey id);

    List<Job> search(String des);

    List<Job> unUpload();

    List<Job> getJob(LocalDateTime updatedDate);
}
