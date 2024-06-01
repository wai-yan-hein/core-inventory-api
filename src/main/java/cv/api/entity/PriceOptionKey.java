package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Builder
public class PriceOptionKey implements Serializable {
    private String priceType;
    private String compCode;
    private Integer deptId;
}
