package cv.api.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class Expense{
    private ExpenseKey key;
    private String userCode;
    private String expenseName;
    private String accountCode;
    private Boolean deleted;
    private Double percent;
}
