package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.LandingHisGrade;
import cv.api.entity.LandingHisGradeKey;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LandingGradeService {
    private final DatabaseClient client;

    public Mono<LandingHisGrade> insert(LandingHisGrade dto) {
        String sql = """
                INSERT INTO landing_his_grade
                (vou_no, comp_code, unique_id, stock_code, match_count, match_percent, choose)
                VALUES
                (:vouNo, :compCode, :uniqueId, :stockCode, :matchCount, :matchPercent, :choose)
                """;

        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("stockCode", dto.getStockCode())
                .bind("matchCount", dto.getMatchCount())
                .bind("matchPercent", dto.getMatchPercent())
                .bind("choose", Util1.getBoolean(dto.getChoose()))
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }
    public Flux<LandingHisGrade> getLandingGrade(String vouNo, String compCode) {
        String sql = """
                SELECT g.*, s.stock_name, s.user_code
                FROM landing_his_grade g
                JOIN stock s ON g.stock_code = s.stock_code AND g.comp_code = s.comp_code
                WHERE g.vou_no = :vouNo AND g.comp_code = :compCode
                ORDER BY g.unique_id
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, metadata) -> LandingHisGrade.builder()
                        .key(LandingHisGradeKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .stockCode(row.get("stock_code", String.class))
                        .matchCount(row.get("match_count", Double.class))
                        .matchPercent(row.get("match_percent", Double.class))
                        .choose(row.get("choose", Boolean.class))
                        .stockName(row.get("stock_name", String.class))
                        .userCode(row.get("user_code", String.class))
                        .build())
                .all();
    }
    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from labour_his_grade
                where vou_no=:vouNo and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch()
                .rowsUpdated()
                .thenReturn(true)
                .defaultIfEmpty(false);
    }
}
