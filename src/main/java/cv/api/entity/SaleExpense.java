package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.*;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "sale_expense")
public class SaleExpense {
    @EmbeddedId
    private SaleExpenseKey key;
    @Column(name = "amount")
    private Float amount;
    private transient String expenseName;
}