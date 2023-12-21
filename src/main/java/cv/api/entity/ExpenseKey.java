package cv.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ExpenseKey implements Serializable {
    @Column(name = "expense_code")
    private String expenseCode;
    @Column(name = "comp_code")
    private String compCode;
}
