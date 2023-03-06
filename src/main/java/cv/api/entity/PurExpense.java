package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "pur_expense")
public class PurExpense {
    @EmbeddedId
    private PurExpenseKey key;
    @Column(name = "amount")
    private Float amount;
    @Transient
    private String expenseName;
}
