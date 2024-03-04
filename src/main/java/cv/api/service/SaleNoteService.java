package cv.api.service;

import cv.api.entity.SaleNote;
import io.r2dbc.spi.Parameters;
import io.r2dbc.spi.R2dbcType;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SaleNoteService {
    private final DatabaseClient client;


    public Mono<Boolean> delete(String vouNo, String compCode) {
        String sql = """
                delete from sale_note where comp_code =:compCode and vou_no=:vouNo
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .bind("vouNo", vouNo)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<SaleNote> insert(SaleNote dto) {
        String sql = """
                INSERT INTO sale_note (
                    vou_no, comp_code, unique_id, description, sale_qty, qty,unit_name
                ) VALUES (
                    :vouNo, :compCode, :uniqueId, :description, :saleQty, :qty,:unitName
                )
                """;

        return executeUpdate(sql, dto);
    }


    private Mono<SaleNote> executeUpdate(String sql, SaleNote dto) {
        // Assuming client is a R2dbcClient or a similar type
        return client.sql(sql)
                .bind("vouNo", dto.getVouNo())
                .bind("compCode", dto.getCompCode())
                .bind("uniqueId", dto.getUniqueId())
                .bind("description", dto.getDescription())
                .bind("saleQty", Parameters.in(R2dbcType.DOUBLE, dto.getSaleQty()))
                .bind("qty", Parameters.in(R2dbcType.DOUBLE, dto.getQty()))
                .bind("unitName", Parameters.in(R2dbcType.VARCHAR, dto.getUnitName()))
                .fetch()
                .rowsUpdated().thenReturn(dto);
    }

    public Flux<SaleNote> getSaleNote(String vouNo, String compCode) {
        String sql = """
                select *
                from sale_note
                where comp_code =:compCode
                and vou_no = :vouNo
                order by unique_id""";
        return client.sql(sql)
                .bind("vouNo", vouNo)
                .bind("compCode", compCode)
                .map((row) -> SaleNote.builder()
                        .compCode(row.get("comp_code", String.class))
                        .uniqueId(row.get("unique_id", Integer.class))
                        .vouNo(row.get("vou_no", String.class))
                        .description(row.get("description", String.class))
                        .unitName(row.get("unit_name", String.class))
                        .saleQty(row.get("sale_qty", Double.class))
                        .qty(row.get("qty", Double.class))
                        .build()).all();

    }


}
