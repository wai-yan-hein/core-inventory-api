package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class StockFormulaQtyKey implements Serializable {
    private String formulaCode;
    private String compCode;
    private int uniqueId;
}
