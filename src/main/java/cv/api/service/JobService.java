package cv.api.service;


import cv.api.common.FilterObject;
import cv.api.entity.Job;
import cv.api.entity.Job;
import cv.api.entity.JobKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface JobService {
    Job save(Job status);
    List<Job> findAll(FilterObject filterObject);
    int delete(JobKey key);
    Job findById(JobKey key);
    List<Job> search(String description);
    List<Job> unUpload();

    Date getMaxDate();

    List<Job> getJob(LocalDateTime updatedDate);
}
