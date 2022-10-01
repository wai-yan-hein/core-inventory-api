package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "trader_group")
public class TraderGroup {
    @Id
    @Column(name = "group_code")
    private String groupCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "group_name")
    private String groupName;
}
