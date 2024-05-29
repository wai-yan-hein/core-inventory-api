package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Data
public class StockFormula {
    private StockFormulaKey key;
    private String formulaName;
    private String userCode;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private boolean deleted;
    private boolean active;
    private Double qty;
    private transient List<StockFormulaQty> listQty;
    private transient List<StockFormulaPrice> listPrice;

}
