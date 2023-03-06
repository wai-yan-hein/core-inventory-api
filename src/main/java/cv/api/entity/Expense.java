package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "expense")
public class Expense{
    @EmbeddedId
    private ExpenseKey key;
    @Column(name = "expense_name")
    private String expenseName;
    @Column(name = "account_code")
    private String accountCode;
    @Column(name = "deleted")
    private boolean deleted;
}
