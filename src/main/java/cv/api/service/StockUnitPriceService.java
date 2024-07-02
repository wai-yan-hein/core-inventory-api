package cv.api.service;

import cv.api.common.Util1;
import cv.api.entity.StockUnitPrice;
import cv.api.entity.StockUnitPriceKey;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockUnitPriceService {

    private final DatabaseClient client;


    public Mono<StockUnitPrice> insert(StockUnitPrice dto) {
        String sql = """
                    INSERT INTO stock_unit_price (stock_code, comp_code, unit,rel_code, sale_price_n, sale_price_a, sale_price_b, sale_price_c, sale_price_d, sale_price_e, unique_id, updated_date)
                    VALUES (:stockCode, :compCode, :unit, :relCode, :salePriceN, :salePriceA, :salePriceB, :salePriceC, :salePriceD, :salePriceE, :uniqueId, :updatedDate)
                """;
        return client.sql(sql)
                .bind("stockCode", dto.getKey().getStockCode())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("unit", dto.getKey().getUnit())
                .bind("relCode", dto.getKey().getRelCode())
                .bind("salePriceN", Util1.getDouble(dto.getSalePriceN()))
                .bind("salePriceA", Util1.getDouble(dto.getSalePriceA()))
                .bind("salePriceB", Util1.getDouble(dto.getSalePriceB()))
                .bind("salePriceC", Util1.getDouble(dto.getSalePriceC()))
                .bind("salePriceD", Util1.getDouble(dto.getSalePriceD()))
                .bind("salePriceE", Util1.getDouble(dto.getSalePriceE()))
                .bind("uniqueId", dto.getUniqueId())
                .bind("updatedDate", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<Boolean> saveList(List<StockUnitPrice> list) {
        if (list != null && !list.isEmpty()) {
            String stockCode = list.getFirst().getKey().getStockCode();
            String compCode = list.getFirst().getKey().getCompCode();
            return deleteDetail(stockCode, compCode)
                    .thenMany(Flux.fromIterable(list)
                            .flatMap(this::insert))
                    .then(Mono.just(true));
        }
        return Mono.just(false);
    }

    private Mono<Boolean> deleteDetail(String stockCode, String compCode) {
        String sql = """
                delete from stock_unit_price where stock_code = :stockCode and comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("compCode", compCode)
                .fetch().rowsUpdated()
                .thenReturn(true);
    }

    public Flux<StockUnitPrice> generateStockUnitPrice(String stockCode, String relCode, String compCode) {
        return checkExist(stockCode, relCode, compCode)
                .flatMapMany(exist -> {
                    if (exist) {
                        return getStockUnitPrice(stockCode, relCode, compCode);
                    } else {
                        return genStockPriceByRelation(relCode, stockCode, compCode)
                                .flatMapMany(aBoolean -> getStockUnitPrice(stockCode, relCode, compCode));
                    }
                });
    }

    private Mono<Boolean> genStockPriceByRelation(String relCode, String stockCode, String compCode) {
        String sql = """
                insert into stock_unit_price(stock_code,comp_code,unit,unique_id,rel_code,updated_date)
                select :stockCode stock_code,comp_code,unit,unique_id,rel_code,:updatedDate
                from unit_relation_detail
                where rel_code = :relCode
                and comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("relCode", relCode)
                .bind("compCode", compCode)
                .bind("stockCode", stockCode)
                .bind("updatedDate", LocalDateTime.now())
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Flux<StockUnitPrice> getStockUnitPrice(String stockCode, String relCode, String compCode) {
        String sql = """
                select *
                from stock_unit_price
                where comp_code = :compCode
                and stock_code = :stockCode
                and rel_code = :relCode
                order by unique_id
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("stockCode", stockCode)
                .bind("relCode", relCode)
                .map((row, rowMetadata) -> mapRow(row))
                .all();
    }

    private Mono<Boolean> checkExist(String stockCode, String relCode, String compCode) {
        String sql = """
                select count(*) count
                from stock_unit_price
                where comp_code = :compCode
                and stock_code = :stockCode
                and rel_code = :relCode
                """;
        return client.sql(sql)
                .bind("stockCode", stockCode)
                .bind("compCode", compCode)
                .bind("relCode", relCode)
                .map((row) -> row.get("count", Integer.class))
                .one()
                .map(count -> count > 0);
    }

    private StockUnitPrice mapRow(Row row) {
        return StockUnitPrice.builder()
                .key(StockUnitPriceKey.builder()
                        .stockCode(row.get("stock_code", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .unit(row.get("unit", String.class))
                        .relCode(row.get("rel_code", String.class))
                        .build())
                .salePriceN(row.get("sale_price_n", Double.class))
                .salePriceA(row.get("sale_price_a", Double.class))
                .salePriceB(row.get("sale_price_b", Double.class))
                .salePriceC(row.get("sale_price_c", Double.class))
                .salePriceD(row.get("sale_price_d", Double.class))
                .salePriceE(row.get("sale_price_e", Double.class))
                .uniqueId(row.get("unique_id", Integer.class))
                .updatedDate(row.get("updated_date", LocalDateTime.class))
                .build();
    }

    public Flux<StockUnitPrice> getStockUnitPrice(LocalDateTime updatedDate) {
        String sql = """
                select *
                from stock_unit_price
                where updated_date > :updatedDate
                """;
        return client.sql(sql)
                .bind("updatedDate", updatedDate)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }
}
