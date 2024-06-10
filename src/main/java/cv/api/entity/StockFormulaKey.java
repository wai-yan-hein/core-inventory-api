package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class StockFormulaKey implements Serializable {
    private String formulaCode;
    private String compCode;
}
