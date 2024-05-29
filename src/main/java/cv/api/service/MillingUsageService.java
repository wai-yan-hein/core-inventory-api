package cv.api.service;

import cv.api.entity.MillingUsage;
import cv.api.entity.MillingUsageKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class MillingUsageService {
    private final DatabaseClient client;

    public Mono<MillingUsage> insert(MillingUsage dto) {
        String sql = """
                INSERT INTO milling_usage (vou_no, comp_code, unique_id, stock_code, qty, unit, loc_code)
                VALUES (:vouNo, :compCode, :uniqueId, :stockCode, :qty, :unit, :locCode)
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<MillingUsage> update(MillingUsage dto) {
        String sql = """
                UPDATE milling_usage
                SET stock_code = :stockCode, qty = :qty, unit = :unit, loc_code = :locCode
                WHERE vou_no = :vouNo AND comp_code = :compCode AND unique_id = :uniqueId
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<MillingUsage> executeUpdate(String sql, MillingUsage dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("stockCode", dto.getStockCode())
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnit())
                .bind("locCode", dto.getLocCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }
    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from milling_usage where vou_no = :vouNo and comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }
    public Flux<MillingUsage> getMillingUsage(String vouNo, String compCode) {
        String sql = """
                SELECT u.*, s.user_code, s.stock_name, l.loc_name
                FROM milling_usage u
                JOIN stock s ON u.stock_code = s.stock_code AND u.comp_code = s.comp_code
                JOIN location l ON u.loc_code = l.loc_code AND u.comp_code = l.comp_code
                WHERE u.vou_no = :vouNo AND u.comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> MillingUsage.builder()
                        .key(MillingUsageKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .qty(row.get("qty", Double.class))
                        .unit(row.get("unit", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .build())
                .all();
    }
}
