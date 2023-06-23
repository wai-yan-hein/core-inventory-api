package cv.api.auto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LocationSetting {
    private String cashAcc;
    private String deptCode;
}
