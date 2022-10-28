package cv.api.inv.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "process_his")
public class ProcessHis {
    @EmbeddedId
    private ProcessHisKey key;
    @Column(name = "qty")
    private float qty;
    @Column(name = "price")
    private float price;
    @Column(name = "finish")
    private boolean finish;
    @Column(name = "loc_code")
    private String locCode;
}
