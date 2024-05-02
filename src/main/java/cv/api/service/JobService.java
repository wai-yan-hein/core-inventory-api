package cv.api.service;

import cv.api.common.ReportFilter;
import cv.api.common.Util1;
import cv.api.entity.Job;
import cv.api.entity.JobKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JobService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<Job> save(Job dto) {
        String code = dto.getKey().getJobNo();
        String compCode = dto.getKey().getCompCode();
        Integer deptId = dto.getDeptId();
        if (Util1.isNullOrEmpty(code)) {
            return seqService.getNextCode("Job", compCode, 5)
                    .flatMap(seqNo -> {
                        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
                        String deptCode = String.format("%02d", deptId);
                        String jobNo = deptCode + period + "-" + seqNo;
                        dto.getKey().setJobNo(jobNo);
                        return insert(dto);
                    });
        }
        return update(dto);
    }

    public Mono<Job> insert(Job dto) {
        String sql = """
                INSERT INTO job (job_no, comp_code, job_name, start_date, end_date,
                updated_date, created_date, created_by, updated_by, deleted, finished, dept_id,
                output_qty, output_cost)
                VALUES (:jobNo, :compCode, :jobName, :startDate, :endDate, :updatedDate,
                :createdDate, :createdBy, :updatedBy, :deleted, :finished, :deptId,
                :outputQty, :outputCost)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<Job> update(Job dto) {
        String sql = """
                UPDATE job
                SET job_name = :jobName, start_date = :startDate, end_date = :endDate,
                updated_date = :updatedDate, created_date = :createdDate, created_by = :createdBy,
                updated_by = :updatedBy, deleted = :deleted, finished = :finished, dept_id = :deptId,
                output_qty = :outputQty, output_cost= :outputCost
                WHERE job_no = :jobNo AND comp_code = :compCode
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<Job> executeUpdate(String sql, Job dto) {
        return client.sql(sql)
                .bind("jobNo", dto.getKey().getJobNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("jobName", dto.getJobName())
                .bind("startDate", dto.getStartDate())
                .bind("endDate", dto.getEndDate())
                .bind("updatedDate", LocalDateTime.now())
                .bind("createdDate", dto.getCreatedDate())
                .bind("createdBy", dto.getCreatedBy())
                .bind("updatedBy", Parameters.in(R2dbcType.VARCHAR, dto.getUpdatedBy()))
                .bind("deleted", Util1.getBoolean(dto.getDeleted()))
                .bind("finished", Util1.getBoolean(dto.getFinished()))
                .bind("deptId", dto.getDeptId())
                .bind("outputQty",dto.getOutputQty())
                .bind("outputCost",dto.getOutputCost())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    private Job mapRow(Row row) {
        return Job.builder()
                .key(JobKey.builder()
                        .jobNo(row.get("job_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .jobName(row.get("job_name", String.class))
                .startDate(row.get("start_date", LocalDate.class))
                .endDate(row.get("end_date", LocalDate.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .createdDate(row.get("created_date", LocalDateTime.class))
                .createdBy(row.get("created_by", String.class))
                .updatedBy(row.get("updated_by", String.class))
                .deleted(row.get("deleted", Boolean.class))
                .finished(row.get("finished", Boolean.class))
                .deptId(row.get("dept_id", Integer.class))
                .outputCost(row.get("output_cost",Double.class))
                .outputQty(row.get("output_qty",Double.class))
                .build();
    }


    public Flux<Job> findAll(ReportFilter filter) {
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String fromDate = Util1.isNull(filter.getFromDate(),Util1.getOldDate());
        String toDate = Util1.isNull(filter.getToDate(),Util1.toDateStr(Util1.getTodayDate(),"yyyy-MM-dd"));
        boolean finished = filter.isFinished();
        String sql = """
            select *
            from job
            where deleted = false
            and dept_id = :deptId
            and comp_code = :compCode
            and finished = :finished
            and date(start_date) between :fromDate and :toDate
            and date(end_date) between :fromDate and :toDate
            """;
        return client.sql(sql)
                .bind("deptId", deptId)
                .bind("compCode", compCode)
                .bind("finished", finished)
                .bind("fromDate", fromDate)
                .bind("toDate", toDate)
                .map((row, rowMetadata) -> mapRow(row))
                .all();
    }
    public Flux<Job> getActiveJob(String compCode){
        String sql= """
                select *
                from job
                where comp_code =:compCode
                and deleted = false
                """;
        return client.sql(sql)
                .bind("compCode",compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }



    public Mono<Boolean> delete(JobKey key) {
        String sql = """
                update job
                set deleted = true,updated_date = :updatedDate
                where comp_code =:compCode
                and job_no =:jobNo
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("jobNo", key.getJobNo())
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }


    public Mono<Job> findById(JobKey key) {
        String sql = """
                select *
                from job
                where comp_code =:compCode
                and code =:code
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("code", key.getJobNo())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<Job> getJob(LocalDateTime updatedDate) {
        String sql = """
                select *
                from job
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

}
