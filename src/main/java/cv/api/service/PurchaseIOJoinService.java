package cv.api.service;

import cv.api.entity.PurchaseIOJoin;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PurchaseIOJoinService {
    private final DatabaseClient client;

    public Flux<PurchaseIOJoin> getPurIO(String purVouNo, String compCode) {
        String sql = """
                select pur_vou_no, io_vou_no, comp_code
                from pur_io_join
                where pur_vou_no=:purVouNo
                and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("purVouNo", purVouNo)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> PurchaseIOJoin.builder()
                        .purVouNo(row.get("pur_vou_no", String.class))
                        .ioVouNo(row.get("io_vou_no", String.class))
                        .compCode(row.get("comp_code", String.class))
                        .build()).all();
    }

    public Mono<Boolean> delete(String purVouNo, String compCode) {
        String sql = """
                delete from pur_io_join where pur_vou_no=:purVouNo and comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("purVouNo", purVouNo)
                .bind("compCode", compCode)
                .fetch().rowsUpdated().thenReturn(true);
    }

    public Mono<PurchaseIOJoin> insert(PurchaseIOJoin s) {
        String sql = """
                INSERT INTO pur_io_join (pur_vou_no, io_vou_no, comp_code)
                VALUES (:purVouNo, :ioVouNo, :compCode)
                """;
        return client.sql(sql)
                .bind("purVouNo", s.getPurVouNo())
                .bind("ioVouNo", s.getIoVouNo())
                .bind("compCode", s.getCompCode())
                .fetch()
                .rowsUpdated()
                .thenReturn(s);
    }


}
