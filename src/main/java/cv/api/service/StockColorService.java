package cv.api.service;

import cv.api.common.Util1;
import cv.api.r2dbc.StockColor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StockColorService {
    private final R2dbcEntityTemplate template;
    private final SeqService seqService;
    private final DatabaseClient client;

    public Mono<StockColor> saveOrUpdate(StockColor color) {
        String colorId = color.getColorId();
        if (Util1.isNullOrEmpty(colorId)) {
            return seqService.getNextCode("color_code_seq", color.getCompCode(), 5)
                    .flatMap(seqNo -> {
                        color.setColorId(seqNo);
                        color.setUpdatedDate(LocalDateTime.now());
                        return template.insert(color);
                    });
        } else {
            return update(color);
        }
    }

    public Mono<StockColor> update(StockColor color) {
        String sql = """
                update stock_color
                set color_name=:colorName,updated_date=:updatedDate
                where color_id=:colorId
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("colorId", color.getColorId())
                .bind("compCode", color.getCompCode())
                .bind("colorName", color.getColorName())
                .bind("updatedDate", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .thenReturn(color);
    }

    public Flux<StockColor> getStockColor(String compCode) {
        String query = """
                select *
                from stock_color
                where comp_code = :compCode""";
        return client.sql(query)
                .bind("compCode", compCode)
                .map((row) -> StockColor.builder()
                        .colorId(row.get("color_id", String.class))
                        .colorName(row.get("color_name", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build())
                .all();
    }
}
