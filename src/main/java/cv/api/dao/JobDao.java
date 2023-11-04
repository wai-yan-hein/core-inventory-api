package cv.api.dao;

import cv.api.entity.Job;
import cv.api.entity.JobKey;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface JobDao {
    Job save(Job Job);

    List<Job> findAll(String compCode, Boolean isFinished,int deptId);

    int delete(JobKey key);

    Job findById(JobKey id);

    List<Job> search(String des);

    List<Job> unUpload();

    Date getMaxDate();

    List<Job> getJob(LocalDateTime updatedDate);
}
