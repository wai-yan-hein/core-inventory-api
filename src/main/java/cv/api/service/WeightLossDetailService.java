package cv.api.service;

import cv.api.entity.WeightLossHisDetail;
import cv.api.entity.WeightLossHisDetailKey;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WeightLossDetailService {
    private final DatabaseClient client;

    public Mono<WeightLossHisDetail> insert(WeightLossHisDetail dto) {
        String sql = """
                    INSERT INTO weight_loss_his_detail (
                        vou_no, comp_code, dept_id, unique_id, stock_code, qty, unit, price, loss_qty, loss_unit, loss_price, loc_code
                    ) VALUES (
                        :vouNo, :compCode, :deptId, :uniqueId, :stockCode, :qty, :unit, :price, :lossQty, :lossUnit, :lossPrice, :locCode
                    )
                """;
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("stockCode", dto.getStockCode())
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnit())
                .bind("price", dto.getPrice())
                .bind("lossQty", dto.getLossQty())
                .bind("lossUnit", dto.getLossUnit())
                .bind("lossPrice", dto.getLossPrice())
                .bind("locCode", dto.getLocCode())
                .fetch().rowsUpdated().thenReturn(dto);
    }

    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from weight_loss_his_detail where comp_code=:compCode and vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<WeightLossHisDetail> search(String vouNo, String compCode, Integer deptId) {
        String sql = """
                SELECT w.*, s.user_code, s.stock_name, rel.rel_name, l.loc_name
                FROM weight_loss_his_detail w
                JOIN stock s ON w.stock_code = s.stock_code AND w.comp_code = s.comp_code
                LEFT JOIN unit_relation rel ON s.rel_code = rel.rel_code AND s.comp_code = s.comp_code
                JOIN location l ON w.loc_code = l.loc_code AND w.comp_code = l.comp_code
                WHERE w.comp_code = :compCode AND w.vou_no = :vouNo
                ORDER BY unique_id
                """;

        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .map((row, rowMetadata) -> WeightLossHisDetail.builder()
                        .key(WeightLossHisDetailKey.builder()
                                .vouNo(vouNo)
                                .compCode(compCode)
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(deptId)
                        .stockUserCode(row.get("user_code", String.class))
                        .stockCode(row.get("stock_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .unit(row.get("unit", String.class))
                        .price(row.get("price", Double.class))
                        .lossQty(row.get("loss_qty", Double.class))
                        .lossUnit(row.get("loss_unit", String.class))
                        .lossPrice(row.get("loss_price", Double.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .relName(row.get("rel_name", String.class))
                        .build())
                .all();
    }
}
