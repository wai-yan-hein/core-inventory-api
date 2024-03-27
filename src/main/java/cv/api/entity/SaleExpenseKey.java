package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleExpenseKey {
    private String expenseCode;
    private String vouNo;
    private String compCode;
    private Integer uniqueId;
}