package cv.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderFileJoin {
    String vouNo;
    String compCode;
    String fileId;
}
