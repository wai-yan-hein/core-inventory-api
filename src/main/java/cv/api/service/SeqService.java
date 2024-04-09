package cv.api.service;

import cv.api.r2dbc.SequenceTable;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeqService {

    private final DatabaseClient client;
    private final int MAC_ID=0;

    public Mono<String> getNextCode(String seqName, String compCode, int format) {
        return findById(MAC_ID, seqName, "-", compCode)
                .flatMap(seqTable -> {
                    int nextValue = seqTable.getSeqNo() + 1;
                    seqTable.setSeqNo(nextValue);
                    return update(seqTable)
                            .map(updatedSeqTable -> String.format("%0" + format + "d", updatedSeqTable.getSeqNo()));

                }).switchIfEmpty(createNewSequence(seqName, compCode, format));
    }

    @Transactional
    public Mono<SequenceTable> update(SequenceTable dto) {
        String sql = """
                update seq_table
                set seq_no = :seqNo
                where comp_code = :compCode
                and seq_option = :seqOption
                and mac_id = :macId
                and period = :period
                """;
        return executeUpdate(sql, dto);

    }

    @Transactional
    public Mono<SequenceTable> insert(SequenceTable dto) {
        String sql = """
                insert into seq_table (seq_no, comp_code, seq_option, mac_id, period)
                values (:seqNo, :compCode, :seqOption, :macId, :period)
                """;
        return executeUpdate(sql, dto);
    }


    private Mono<SequenceTable> executeUpdate(String sql, SequenceTable dto) {
        return client.sql(sql)
                .bind("compCode", dto.getCompCode())
                .bind("seqOption", dto.getSeqOption())
                .bind("macId", dto.getMacId())
                .bind("period", dto.getPeriod())
                .bind("seqNo", dto.getSeqNo())
                .fetch()
                .rowsUpdated()
                .thenReturn(dto);
    }

    public Mono<SequenceTable> findById(Integer macId, String option, String period, String compCode) {
        String sql = """
                select mac_id, seq_option, period, comp_code, seq_no,
                updated_date, created_date, created_by, updated_by, user_code
                from seq_table
                where mac_id=:macId
                and seq_option=:option
                and period=:period
                and comp_code=:compCode
                """;
        return client.sql(sql)
                .bind("macId", macId)
                .bind("option", option)
                .bind("period", period)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).one();
    }

    private SequenceTable mapRow(Row row) {
        return SequenceTable.builder()
                .seqNo(row.get("seq_no", Integer.class))
                .compCode(row.get("comp_code", String.class))
                .seqOption(row.get("seq_option", String.class))
                .macId(row.get("mac_id", Integer.class))
                .period(row.get("period", String.class))
                .build();
    }

    private Mono<String> createNewSequence(String seqName, String compCode, int format) {
        var seq = SequenceTable.builder()
                .compCode(compCode)
                .period("-")
                .macId(MAC_ID)
                .seqOption(seqName)
                .seqNo(1).build();
        return insert(seq)
                .map(savedSeqTable -> String.format("%0" + format + "d", savedSeqTable.getSeqNo()));
    }

    public Mono<Boolean> isExist(String compCode) {
        String sql = """
                SELECT count(*) count
                FROM seq_table
                WHERE comp_code = :compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row) -> row.get("count",Integer.class))
                .one()
                .map(count -> count > 0);
    }
    public Flux<SequenceTable> findAll(String compCode) {
        String sql = """
                select *
                from seq_table
                where comp_code =:compCode
                """;
        return client.sql(sql)
                .bind("compCode", compCode)
                .map((row, rowMetadata) -> mapRow(row)).all();
    }
}
