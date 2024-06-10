package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccKey {
    private String type;
    private String compCode;

    public AccKey(String type, String compCode) {
        this.type = type;
        this.compCode = compCode;
    }
}
