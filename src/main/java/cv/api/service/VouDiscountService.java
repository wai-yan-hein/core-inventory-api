package cv.api.service;

import cv.api.entity.VouDiscount;
import cv.api.entity.VouDiscountKey;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VouDiscountService {
    private final DatabaseClient client;

    @Transactional
    public Mono<VouDiscount> insert(VouDiscount dto) {
        String sql = """
                INSERT INTO vou_discount (vou_no, comp_code, unique_id, description, unit, qty, price, amount)
                VALUES (:vouNo, :compCode, :uniqueId, :description, :unit, :qty, :price, :amount)
                """;
        return client.sql(sql)
                .bind("vouNo", dto.getKey().getVouNo())
                .bind("compCode", dto.getKey().getCompCode())
                .bind("uniqueId", dto.getKey().getUniqueId())
                .bind("description", Parameters.in(R2dbcType.VARCHAR, dto.getDescription()))
                .bind("unit", Parameters.in(R2dbcType.VARCHAR, dto.getUnit()))
                .bind("qty", dto.getQty())
                .bind("price", dto.getPrice())
                .bind("amount", dto.getAmount())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Flux<VouDiscount> getVoucherDiscount(String vouNo, String compCode) {
        String sql = """
                select v.*,u.unit_name
                from vou_discount v join stock_unit u
                on v.unit = u.unit_code
                and v.comp_code = u.comp_code
                where v.vou_no =:vouNo
                and v.comp_code =:compCode
                order by unique_id""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> {
                    return VouDiscount.builder()
                            .key(VouDiscountKey.builder()
                                    .compCode(row.get("comp_code", String.class))
                                    .uniqueId(row.get("unique_id", Integer.class))
                                    .vouNo(row.get("vou_no", String.class))
                                    .build())
                            .description(row.get("description", String.class))
                            .unit(row.get("unit", String.class))
                            .unitName(row.get("unit_name", String.class))
                            .qty(row.get("qty", Double.class))
                            .price(row.get("price", Double.class))
                            .amount(row.get("amount", Double.class))
                            .build();
                }).all();
    }


    public Flux<VouDiscount> getDescription(String str, String compCode) {
        str += "%";
        String sql = """
                select description
                from vou_discount
                where comp_code= :compCode
                and description like :str
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("str", str)
                .map((row, rowMetadata) -> {
                    return VouDiscount.builder()
                            .description(row.get("description", String.class))
                            .build();
                }).all();
    }
}
