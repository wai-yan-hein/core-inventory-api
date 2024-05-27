package cv.api.service;

import cv.api.entity.GRNDetail;
import cv.api.entity.GRNDetailKey;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GRNDetailService {
    private final DatabaseClient client;

    public Mono<GRNDetail> insert(GRNDetail dto) {
        String sql = """
                INSERT INTO grn_detail (vou_no, unique_id, comp_code, dept_id, stock_code, loc_code, qty, unit, weight, weight_unit, total_weight)
                VALUES (:vouNo, :uniqueId, :compCode, :deptId, :stockCode, :locCode, :qty, :unit, :weight, :weightUnit, :totalWeight)
                """;
        return executeUpdate(sql, dto);
    }

    private Mono<GRNDetail> executeUpdate(String sql, GRNDetail dto) {
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("deptId", dto.getDeptId())
                .bind("stockCode", dto.getStockCode())
                .bind("locCode", dto.getLocCode())
                .bind("qty", dto.getQty())
                .bind("unit", dto.getUnit())
                .bind("weight", dto.getWeight())
                .bind("weightUnit", dto.getWeightUnit())
                .bind("totalWeight", dto.getTotalWeight())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<Boolean> deleteDetail(String vouNo, String compCode) {
        String sql = """
                delete from grn_detail where vou_no = :vouNo and comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<GRNDetail> getDetail(String vouNo, String compCode) {
        String sql = """
                select g.*,s.user_code,s.stock_name,s.weight std_weight,rel.rel_name,l.loc_name
                from grn_detail g join stock s
                on g.stock_code = s.stock_code
                and g.comp_code =s.comp_code
                left join unit_relation rel
                on s.rel_code = rel.rel_code
                and s.comp_code =rel.comp_code
                join location l
                on g.loc_code = l.loc_code
                and g.comp_code =l.comp_code
                where g.vou_no=:vouNo
                and g.comp_code =:compCode
                order by unique_id
                """;
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map(row -> GRNDetail.builder()
                        .key(GRNDetailKey.builder()
                                .vouNo(row.get("vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .uniqueId(row.get("unique_id", Integer.class))
                                .build())
                        .deptId(row.get("dept_id", Integer.class))
                        .stockCode(row.get("stock_code", String.class))
                        .userCode(row.get("user_code", String.class))
                        .stockName(row.get("stock_name", String.class))
                        .stdWeight(row.get("std_weight", Double.class))
                        .totalWeight(row.get("total_weight", Double.class))
                        .relName(row.get("rel_name", String.class))
                        .qty(row.get("qty", Double.class))
                        .unit(row.get("unit", String.class))
                        .locCode(row.get("loc_code", String.class))
                        .locName(row.get("loc_name", String.class))
                        .weight(row.get("weight", Double.class))
                        .weightUnit(row.get("weight_unit", String.class))
                        .build())
                .all();
    }

}
