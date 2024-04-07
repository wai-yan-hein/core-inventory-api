package cv.api.r2dbc;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Data
public class SequenceTable {
    private Integer macId;
    private String seqOption;
    private String period;
    private String compCode;
    private Integer seqNo;

}
