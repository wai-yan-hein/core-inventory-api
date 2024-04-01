package cv.api.service;

import cv.api.entity.ConsignHisDetail;
import cv.api.entity.ConsignHisDetailKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsignDetailService {
    private final DatabaseClient client;

    public Mono<ConsignHisDetail> insert(ConsignHisDetail dto) {
        String sql = """
                INSERT INTO consign_his_detail (vou_no, unique_id, comp_code, dept_id, stock_code, loc_code, wet, bag, qty, weight, rice, price, amount, total_weight)
                VALUES (:vouNo, :uniqueId, :compCode, :deptId, :stockCode, :locCode, :wet, :bag, :qty, :weight, :rice, :price, :amount, :totalWeight)
                """;
        return client
                .sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("stockCode", dto.getStockCode())
                .bind("locCode", dto.getLocCode())
                .bind("wet", Parameters.in(R2dbcType.DOUBLE, dto.getWet()))
                .bind("bag", Parameters.in(R2dbcType.DOUBLE, dto.getBag()))
                .bind("qty", Parameters.in(R2dbcType.DOUBLE, dto.getQty()))
                .bind("weight", Parameters.in(R2dbcType.DOUBLE, dto.getWeight()))
                .bind("rice", Parameters.in(R2dbcType.DOUBLE, dto.getRice()))
                .bind("price", Parameters.in(R2dbcType.DOUBLE, dto.getPrice()))
                .bind("amount", Parameters.in(R2dbcType.DOUBLE, dto.getAmount()))
                .bind("totalWeight", Parameters.in(R2dbcType.DOUBLE, dto.getTotalWeight()))
                .fetch().rowsUpdated().thenReturn(dto);
    }


    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from consign_his_detail
                where vou_no =:vouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<ConsignHisDetail> getConsignDetail(String vouNo, String compCode) {
        String sql = """
                SELECT op.*, s.stock_name, s.user_code
                FROM consign_his_detail op
                JOIN location l ON op.loc_code = l.loc_code
                AND op.comp_code = l.comp_code
                JOIN stock s ON op.stock_code = s.stock_code
                AND op.comp_code = s.comp_code
                WHERE op.vou_no = :vouNo
                AND op.comp_code = :compCode
                """;

        return client
                .sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, metadata) -> ConsignHisDetail.builder()
                        .key(ConsignHisDetailKey.builder()
                                .compCode(row.get("comp_code", String.class))
                                .vouNo(row.get("vou_no", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .wet(row.get("wet", Double.class))
                        .bag(row.get("bag", Double.class))
                        .qty(row.get("qty", Double.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .weight(row.get("weight", Double.class))
                        .rice(row.get("rice", Double.class))
                        .price(row.get("price", Double.class))
                        .amount(row.get("amount", Double.class))
                        .build())
                .all();
    }


}
