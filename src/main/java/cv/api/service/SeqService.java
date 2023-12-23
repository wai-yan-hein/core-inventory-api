package cv.api.service;

import cv.api.r2dbc.SequenceTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeqService {

    private final R2dbcEntityTemplate template;

    public Mono<String> getNextCode(String seqName, String compCode, int format) {
        return template.select(SequenceTable.class)
                .matching(Query.query(where("seq_option").is(seqName)
                        .and("comp_code").is(compCode)))
                .one()
                .flatMap(seqTable -> {
                    int nextValue = seqTable.getSeqNo() + 1;
                    seqTable.setSeqNo(nextValue);
                    return template.update(seqTable)
                            .map(updatedSeqTable -> String.format("%0" + format + "d", updatedSeqTable.getSeqNo()));

                }).switchIfEmpty(createNewSequence(seqName, compCode, format));
    }

    private Mono<String> createNewSequence(String seqName, String compCode, int format) {
        var seq = SequenceTable.builder()
                .compCode(compCode)
                .period("-")
                .macId(1)
                .seqOption(seqName)
                .seqNo(1).build();
        return template.insert(seq)
                .map(savedSeqTable -> String.format("%0" + format + "d", savedSeqTable.getSeqNo()));
    }


}
