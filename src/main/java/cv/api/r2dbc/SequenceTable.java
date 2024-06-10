package cv.api.r2dbc;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SequenceTable {
    private Integer macId;
    private String seqOption;
    private String period;
    private String compCode;
    private Integer seqNo;

}
