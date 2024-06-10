package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class SaleExpense {
    private SaleExpenseKey key;
    private Double amount;
    private String expenseName;
    private String account;
}
