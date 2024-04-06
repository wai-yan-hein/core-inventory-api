package cv.api.entity;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class WareHouseKey {
    private String code;
    private String compCode;
}
