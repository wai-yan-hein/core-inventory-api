package cv.api.dao;

import cv.api.common.FilterObject;
import cv.api.entity.Job;
import cv.api.entity.JobKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        Boolean finished = filterObject.getFinished();
        List<Job> jList = new ArrayList<>();
        String whereClause = "";
        if (finished != null) {
            whereClause += " and finished = " + finished;
        }
        if (!fromDate.isEmpty() && !toDate.isEmpty()) {
            whereClause += " and date(start_date) between '" + fromDate + "' and '" + toDate + "'" +
                    "and date(end_date) between '" + fromDate + "' and '" + toDate + "'";
        }
        String sql = """ 
                select * from job
                where deleted = false
                and dept_id = ?
                and comp_code = ?
                """ + whereClause;
        ResultSet rs = getResult(sql, deptId, compCode);

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
                    job.setDeptId(rs.getInt("dept_id"));
                    job.setCreatedBy(rs.getString("created_by"));
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
    public List<Job> getJob(LocalDateTime updatedDate) {
        String hsql = "select o from Job o where o.updatedDate > :updatedDate";
        return createQuery(hsql).setParameter("updatedDate", updatedDate).getResultList();
    }
}
