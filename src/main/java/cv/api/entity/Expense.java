package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Expense {
    private ExpenseKey key;
    private String userCode;
    private String expenseName;
    private String accountCode;
    private Boolean deleted;
    private Double percent;
}
