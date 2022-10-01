package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "process_type")
public class ProcessType {
    @EmbeddedId
    private ProcessTypeKey key;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "pt_name")
    private String proName;
    @Column(name = "calculate")
    private boolean calculate;
    @Column(name = "active")
    private boolean active;
}
