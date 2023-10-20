package cv.api.dao;

import cv.api.common.Util1;
import cv.api.entity.Job;
import cv.api.entity.JobKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.time.LocalDateTime;
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
    public List<Job> findAll(String compCode, Boolean isFinished) {
        String hsql = "select o from Job o where o.key.compCode = '" + compCode + "' and o.deleted =false";
        if (!isFinished) {
            hsql += " and o.finished = false";
        }
        return findHSQL(hsql);
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
