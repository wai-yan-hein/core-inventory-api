package cv.api.service;

import cv.api.entity.GradeDetail;
import cv.api.entity.GradeDetailKey;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GradeDetailService {
    private final DatabaseClient client;
    private final SeqService seqService;

    public Mono<GradeDetail> insert(GradeDetail dto) {
        String sql = """
                INSERT INTO grade_detail (comp_code, formula_code, criteria_code, unique_id, min_percent, max_percent,
                grade_stock_code, updated_date)
                VALUES (:compCode, :formulaCode, :criteriaCode, :uniqueId, :minPercent, :maxPercent, :gradeStockCode, :updatedDate)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<GradeDetail> update(GradeDetail dto) {
        String sql = """
                UPDATE grade_detail
                SET min_percent = :minPercent, max_percent = :maxPercent, grade_stock_code = :gradeStockCode, updated_date = :updatedDate
                WHERE comp_code = :compCode AND formula_code = :formulaCode AND criteria_code = :criteriaCode AND unique_id = :uniqueId
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<GradeDetail> executeUpdate(String sql, GradeDetail dto) {
        return client.sql(sql)
                .bind("compCode", dto.getKey().getCompCode())
                .bind("formulaCode", dto.getKey().getFormulaCode())
                .bind("criteriaCode", dto.getKey().getCriteriaCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("minPercent", dto.getMinPercent())
                .bind("maxPercent", dto.getMaxPercent())
                .bind("gradeStockCode", dto.getGradeStockCode())
                .bind("updatedDate", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<GradeDetail> save(GradeDetail dto) {
        Integer uniqueId = dto.getKey().getUniqueId();
        if (uniqueId == null) {
            return insert(dto);
        }
        return update(dto);
    }

    private GradeDetail mapRow(Row row) {
        return GradeDetail.builder()
                .key(GradeDetailKey.builder()
                        .compCode(row.get("comp_code", String.class))
                        .formulaCode(row.get("formula_code", String.class))
                        .criteriaCode(row.get("criteria_code", String.class))
                        .uniqueId(row.get("unique_id", Integer.class))
                        .build())
                .minPercent(row.get("min_percent", Double.class))
                .maxPercent(row.get("max_percent", Double.class))
                .gradeStockCode(row.get("grade_stock_code", String.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .build();
    }

    public Flux<GradeDetail> findAll(String compCode) {
        String sql = """
                select *
                from grade_detail
                where comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> delete(GradeDetailKey key) {
        String sql = """
                delete from grade_detail
                where comp_code =:compCode
                and formula_code =:formulaCode
                and criteria_code =:criteriaCode
                and unique_id =:uniqueId
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("formulaCode", key.getFormulaCode())
                .bind("criteriaCode", key.getCriteriaCode())
                .bind("uniqueId", key.getUniqueId())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<GradeDetail> findById(GradeDetailKey key) {
        String sql = """
                select *
                from grade_detail
                where comp_code =:compCode
                and formula_code =:formulaCode
                and criteria_code =:criteriaCode
                and unique_id =:uniqueId
                """;
        return client.sql(sql)
                .bind("compCode", key.getCompCode())
                .bind("formulaCode", key.getFormulaCode())
                .bind("criteriaCode", key.getCriteriaCode())
                .bind("uniqueId", key.getUniqueId())
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    public Flux<GradeDetail> getGradeDetail(LocalDateTime updatedDate) {
        String sql = """
                select *
                from grade_detail
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<GradeDetail> getGradeDetail(String formulaCode, String criteriaCode, String compCode) {
        String sql = """
                 select g.*,s.stock_name
                 from grade_detail g left join stock s on g.grade_stock_code = s.stock_code
                 and g.comp_code = s.comp_code
                 where g.comp_code = :compCode
                 and g.formula_code = :formulaCode
                 and g.criteria_code = :criteriaCode
                 order by g.unique_id
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("formulaCode", formulaCode)
                .bind("criteriaCode", criteriaCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Flux<GradeDetail> getStockFormulaGrade(String formulaCode, String compCode) {
        String sql = """
                select g.*,s.stock_name
                from grade_detail g join stock s
                on g.grade_stock_code = s.stock_code
                and g.comp_code = s.comp_code
                where g.formula_code =?
                and g.comp_code =?
                order by g.unique_id""";
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("formulaCode", formulaCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }

    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM grade_detail
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count", Integer.class))
                .one()
                .map(count -> count > 0);
    }
}
