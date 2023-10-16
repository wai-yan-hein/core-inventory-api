package cv.api.service;

import cv.api.common.Util1;
import cv.api.dao.JobDao;
import cv.api.entity.Job;
import cv.api.entity.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class JobServiceImpl implements JobService{

    @Autowired
    JobDao dao;
    @Autowired
    private SeqTableService seqService;
    @Override
    public Job save(Job status) {
        if (Objects.isNull(status.getKey().getJobNo())) {
            String compCode = status.getKey().getCompCode();
            status.getKey().setJobNo(getCode(compCode));
        }
        return dao.save(status);
    }

    @Override
    public List<Job> findAll(String compCode) {
        return dao.findAll(compCode);
    }

    @Override
    public int delete(JobKey key) {
        return dao.delete(key);
    }

    @Override
    public Job findById(JobKey key) {
        return dao.findById(key);
    }

    @Override
    public List<Job> search(String description) {
        return dao.search(description);
    }

    @Override
    public List<Job> unUpload() {
        return dao.unUpload();
    }

    @Override
    public Date getMaxDate() {
        return dao.getMaxDate();
    }

    @Override
    public List<Job> getJob(LocalDateTime updatedDate) {
        return dao.getJob(updatedDate);
    }

    private String getCode(String compCode) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        int seqNo = seqService.getSequence(0, "Job", period, compCode);
        return period + "-" + String.format("%0" + 5 + "d", seqNo);
    }
}