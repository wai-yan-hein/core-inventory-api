package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
}
