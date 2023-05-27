package cv.api.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Table;

@Data
@Table(name = "receive_his_detail")
public class ReceiveHisDetail {
    @EmbeddedId
    private ReceiveHisDetailKey key;
    @Column(name = "rec_vou_no")
    private String recVouNo;
    @Column(name = "amount")
    private Float amount;
}
