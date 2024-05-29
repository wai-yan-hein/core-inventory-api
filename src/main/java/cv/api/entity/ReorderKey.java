package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReorderKey {
    private String stockCode;
    private String compCode;
    private String locCode;
}
