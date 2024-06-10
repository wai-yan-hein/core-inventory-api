package cv.api.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseKey {
    private String expenseCode;
    private String compCode;
}
