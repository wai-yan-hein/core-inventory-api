package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;

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
    @Column(name = "account")
    private String account;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
}
