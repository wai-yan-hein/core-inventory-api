package cv.api.service;

import cv.api.entity.ProcessHisDetail;
import cv.api.entity.ProcessHisDetailKey;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProcessHisDetailService {
    private final DatabaseClient client;


    public Mono<ProcessHisDetail> insert(ProcessHisDetail dto) {
        String sql = """
                    INSERT INTO process_his_detail (
                        vou_no, comp_code, unique_id, stock_code, dept_id, vou_date, qty, unit, price, loc_code
                    ) VALUES (
                        :vouNo, :compCode, :uniqueId, :stockCode, :deptId, :vouDate, :qty, :unit, :price, :locCode
                    )
                """;
        return executeUpdate(sql, dto);
    }

    public Mono<ProcessHisDetail> update(ProcessHisDetail dto) {
        String sql = """
                UPDATE process_his_detail SET
                    stock_code = :stockCode,
                    dept_id = :deptId,
                    vou_date = :vouDate,
                    qty = :qty,
                    unit = :unit,
                    price = :price,
                    loc_code = :locCode
                WHERE vou_no = :vouNo
                AND comp_code = :compCode
                AND unique_id = :uniqueId
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<ProcessHisDetail> executeUpdate(String sql, ProcessHisDetail dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("stockCode", dto.getStockCode())
                .bind("deptId", dto.getDeptId())
                .bind("vouDate", dto.getVouDate())
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnit())
                .bind("price", dto.getPrice())
                .bind("locCode", dto.getLocCode())
                .fetch().rowsUpdated().thenReturn(dto);
    }

    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from process_his_detail
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

    public Flux<ProcessHisDetail> search(String vouNo, String compCode) {
        String sql = """
                SELECT a.*, s.user_code, s.stock_name, l.loc_name
                FROM (
                    SELECT *
                    FROM process_his_detail
                    WHERE vou_no = :vouNo
                    AND comp_code = :compCode
                ) a
                JOIN stock s ON s.stock_code = a.stock_code
                AND s.comp_code = a.comp_code
                JOIN location l ON a.loc_code = l.loc_code
                AND l.comp_code = a.comp_code
                ORDER BY a.unique_id
                """;

        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> ProcessHisDetail.builder()
                        .key(ProcessHisDetailKey.builder()
                                .vouNo(vouNo)
                                .compCode(compCode)
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .stockCode(row.get("stock_code", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .deptId(row.get("dept_id", Integer.class))
                        .locName(row.get("loc_name", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .stockUsrCode(row.get("user_code", String.class))
                        .qty(row.get("qty", Double.class))
                        .price(row.get("price", Double.class))
                        .unit(row.get("unit", String.class))
                        .vouDate(row.get("vou_date", LocalDateTime.class))
                        .build())
                .all();
    }

}
