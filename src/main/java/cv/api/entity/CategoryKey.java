package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryKey {
    private String catCode;
    private String compCode;
}
