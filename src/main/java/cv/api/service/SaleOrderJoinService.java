package cv.api.service;

import cv.api.entity.SaleOrderJoin;
import cv.api.entity.SaleOrderJoinKey;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SaleOrderJoinService {
    private final DatabaseClient client;

    public Flux<SaleOrderJoin> getSaleOrder(String saleVouNo, String compCode) {
        String sql = """
                select sale_vou_no, order_vou_no, comp_code
                from sale_order_join
                where sale_vou_no=:saleVouNo
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("saleVouNo", saleVouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> SaleOrderJoin.builder()
                        .key(SaleOrderJoinKey.builder()
                                .saleVouNo(row.get("sale_vou_no", String.class))
                                .orderVouNo(row.get("order_vou_no", String.class))
                                .compCode(row.get("comp_code", String.class))
                                .build())
                        .build()).all();
    }

    public Mono<Boolean> delete(String saleVouNo, String compCode) {
        String sql = """
                delete from sale_order_join where sale_vou_no=:saleVouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("saleVouNo", saleVouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<SaleOrderJoin> insert(SaleOrderJoin s) {
        String sql = """
                INSERT INTO sale_order_join (sale_vou_no, order_vou_no, comp_code)
                VALUES (:saleVouNo, :orderVouNo, :compCode)
                """;
        return client.sql(sql)
                .bind("saleVouNo", s.getKey().getSaleVouNo())
                .bind("orderVouNo", s.getKey().getOrderVouNo())
                .bind("compCode", s.getKey().getCompCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(s);
    }


}
