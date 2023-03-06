package cv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class ExpenseKey implements Serializable {
    @Column(name = "expense_code")
    private String expenseCode;
    @Column(name = "comp_code")
    private String compCode;
}
