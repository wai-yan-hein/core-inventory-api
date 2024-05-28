package cv.api.service;

import cv.api.entity.LandingHisQty;
import cv.api.entity.LandingHisQtyKey;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LandingQtyService {
    private final DatabaseClient client;

    public Mono<LandingHisQty> insert(LandingHisQty dto) {
        String sql = """
                    INSERT INTO landing_his_qty
                    (vou_no, comp_code, unique_id, criteria_code, percent, percent_allow, qty, total_qty, unit)
                    VALUES
                    (:vouNo, :compCode, :uniqueId, :criteriaCode, :percent, :percentAllow, :qty, :totalQty, :unit)
                """;

        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("criteriaCode", dto.getCriteriaCode())
                .bind("percent", dto.getPercent())
                .bind("percentAllow", dto.getPercentAllow())
                .bind("qty", dto.getQty())
                .bind("totalQty", dto.getTotalQty())
                .bind("unit", dto.getUnit())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Flux<LandingHisQty> getLandingQty(String vouNo, String compCode) {
        String sql = """
                SELECT l.*, sc.criteria_name, sc.user_code
                FROM landing_his_qty l
                JOIN stock_criteria sc ON l.criteria_code = sc.criteria_code AND l.comp_code = sc.comp_code
                WHERE l.vou_no = :vouNo AND l.comp_code = :compCode
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, metadata) -> LandingHisQty.builder()
                        .key(LandingHisQtyKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .vouNo(row.get("vou_no", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .criteriaCode(row.get("criteria_code", String.class))
                        .criteriaUserCode(row.get("user_code", String.class))
                        .criteriaName(row.get("criteria_name", String.class))
                        .percent(row.get("percent", Double.class))
                        .percentAllow(row.get("percent_allow", Double.class))
                        .qty(row.get("qty", Double.class))
                        .totalQty(row.get("total_qty", Double.class))
                        .unit(row.get("unit", String.class))
                        .build())
                .all();
    }
    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from labour_his_qty
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
