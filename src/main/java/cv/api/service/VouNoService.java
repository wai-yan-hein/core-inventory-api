package cv.api.service;

import cv.api.common.Util1;
import cv.api.r2dbc.SequenceTable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VouNoService {
    private final R2dbcEntityTemplate template;
    private final DatabaseClient databaseClient;

    public Mono<String> getVouNo(int deptId, String option, String compCode, int macId) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        return findById(macId, option, period, compCode)
                .flatMap(table -> {
                    int seqNo = table.getSeqNo() + 1;
                    table.setSeqNo(seqNo);
                    return update(table);
                })
                .switchIfEmpty(createNewSequence(option, period, macId, compCode))
                .flatMap(table -> {
                    String deptIdFormatted = String.format("%02d", deptId);
                    String macIdFormatted = String.format("%02d", macId);
                    String seqNoFormatted = String.format("%05d", table.getSeqNo());
                    return Mono.just(deptIdFormatted + "-" + macIdFormatted + period + "-" + seqNoFormatted);
                });
    }


    private Mono<SequenceTable> findById(Integer macId, String option, String period, String compCode) {
        String sql = """
                select mac_id, seq_option, period, comp_code, seq_no,
                updated_date, created_date, created_by, updated_by, user_code
                from seq_table
                where mac_id=:macId
                and seq_option=:option
                and period=:period
                and comp_code=:compCode
                """;
        return databaseClient.sql(sql)
                .bind("macId", macId)
                .bind("option", option)
                .bind("period", period)
                .bind("compCode", compCode)
                .map((row) -> SequenceTable.builder()
                        .macId(row.get("mac_id", Integer.class))
                        .compCode(row.get("comp_code", String.class))
                        .seqOption(row.get("seq_option", String.class))
                        .period(row.get("period", String.class))
                        .seqNo(row.get("seq_no", Integer.class))
                        .build()).one();
    }

    private Mono<SequenceTable> createNewSequence(String option, String period, Integer macId, String compCode) {
        return template.insert(SequenceTable.builder()
                .seqOption(option)
                .period(period)
                .macId(macId)
                .seqNo(1)
                .compCode(compCode)
                .build());
    }

    private Mono<SequenceTable> update(SequenceTable table) {
        String sql = """
                UPDATE seq_table
                SET
                seq_no = :seqNo,
                updated_date = :updatedDate
                WHERE mac_id =:macId AND seq_option =:seqOption AND period =:period AND comp_code =:compCode
                """;
        return databaseClient.sql(sql)
                .bind("macId", table.getMacId())
                .bind("seqOption", table.getSeqOption())
                .bind("period", table.getPeriod())
                .bind("compCode", table.getCompCode())
                .bind("seqNo", table.getSeqNo())
                .bind("updatedDate", LocalDateTime.now())
                .fetch()
                .rowsUpdated()
                .thenReturn(table);
    }
}
