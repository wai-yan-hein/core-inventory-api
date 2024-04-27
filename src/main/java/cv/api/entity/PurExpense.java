package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class PurExpense {
    private PurExpenseKey key;
    private Double amount;
    private Double percent;
    private String expenseName;
    private String account;
}
