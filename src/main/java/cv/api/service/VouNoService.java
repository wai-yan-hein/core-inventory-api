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
    private final SeqService seqService;


    public Mono<String> getVouNo(int deptId, String option, String compCode, int macId) {
        String period = Util1.toDateStr(Util1.getTodayDate(), "MMyy");
        return seqService.findById(macId, option, period, compCode)
                .flatMap(table -> {
                    int seqNo = table.getSeqNo() + 1;
                    table.setSeqNo(seqNo);
                    return seqService.update(table);
                })
                .switchIfEmpty(createNewSequence(option, period, macId, compCode))
                .flatMap(table -> {
                    String deptIdFormatted = String.format("%02d", deptId);
                    String macIdFormatted = String.format("%02d", macId);
                    String seqNoFormatted = String.format("%05d", table.getSeqNo());
                    return Mono.just(deptIdFormatted + "-" + macIdFormatted + period + "-" + seqNoFormatted);
                });
    }

    private Mono<SequenceTable> createNewSequence(String option, String period, Integer macId, String compCode) {
        var seq =SequenceTable.builder()
                .seqOption(option)
                .period(period)
                .macId(macId)
                .seqNo(1)
                .compCode(compCode)
                .build();
        return seqService.insert(seq);
    }

}
