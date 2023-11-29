package cv.api.dao;

import cv.api.common.FilterObject;
import cv.api.common.Util1;
import cv.api.entity.Job;
import cv.api.entity.JobKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Repository
public class JobDaoImpl extends AbstractDao<JobKey, Job> implements JobDao {
    @Override
    public Job save(Job job) {
        job.setUpdatedDate(LocalDateTime.now());
        saveOrUpdate(job, job.getKey());
        return job;
    }

    @Override
    public List<Job> findAll(FilterObject filterObject) {
        String compCode = filterObject.getCompCode();
        Integer deptId = filterObject.getDeptId();
        String fromDate = filterObject.getFromDate();
        String toDate = filterObject.getToDate();
        boolean finished = filterObject.isFinished();
        List<Job> jList = new ArrayList<>();
        String sql = """ 
                select * from Job
                where comp_code = ?
                and finished = ?
                and dept_id = ?
                and date(start_date) between ? and ?
                and date(end_date) between ? and ?
                and deleted = false
                """;
        ResultSet rs = getResult(sql, compCode, finished, deptId, fromDate, toDate, fromDate, toDate);
        if (rs != null) {
            try {
                while (rs.next()) {
                    Job job = new Job();
                    JobKey jKey = new JobKey();
                    jKey.setJobNo(rs.getString("job_no"));
                    jKey.setCompCode(rs.getString("comp_code"));
                    job.setKey(jKey);
                    job.setJobName(rs.getString("job_name"));
                    job.setStartDate(rs.getDate("start_date"));
                    job.setEndDate(rs.getDate("end_date"));
                    jList.add(job);
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        return jList;
    }

    @Override
    public int delete(JobKey key) {
        remove(key);
        return 1;
    }

    @Override
    public Job findById(JobKey id) {
        return getByKey(id);
    }

    @Override
    public List<Job> search(String des) {
        String strSql = "";

        if (!des.equals("-")) {
            strSql = "o.name like '%" + des + "%'";
        }

        if (strSql.isEmpty()) {
            strSql = "select o from Job o";
        } else {
            strSql = "select o from Job o where " + strSql;
        }

        return findHSQL(strSql);
    }

    @Override
    public List<Job> unUpload() {
        String hsql = "select o from Job o where o.intgUpdStatus is null";
        return findHSQL(hsql);
    }

    @Override
    public Date getMaxDate() {
        String sql = "select max(updated_date) date from labour_group";
        ResultSet rs = getResult(sql);
        try {
            if (rs.next()) {
                Date date = rs.getTimestamp("date");
                if (date != null) {
                    return date;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Util1.getOldDate();
    }

    @Override
    public List<Job> getJob(LocalDateTime updatedDate) {
        String hsql = "select o from Job o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
