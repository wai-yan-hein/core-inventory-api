package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "trader_group")
public class TraderGroup {
    @EmbeddedId
    private TraderGroupKey key;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "group_name")
    private String groupName;
}
