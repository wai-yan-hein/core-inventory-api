package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import jakarta.persistence.*;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "expense")
public class Expense{
    @EmbeddedId
    private ExpenseKey key;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "expense_name")
    private String expenseName;
    @Column(name = "account_code")
    private String accountCode;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "percent")
    private Float percent;
}
