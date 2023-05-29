package cv.api.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "payment_his_detail")
public class PaymentHisDetail {
    @EmbeddedId
    private PaymentHisDetailKey key;
    @Column(name = "sale_vou_no")
    private String saleVouNo;
    @Column(name = "pay_amt")
    private Float payAmt;
    @Column(name = "dis_amt")
    private Float disAmt;
    @Column(name = "dis_percent")
    private Float disPercent;
    @Column(name = "full_paid")
    private boolean fullPaid;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Temporal(TemporalType.DATE)
    @Column(name = "sale_vou_date")
    private Date saleDate;
    @Column(name = "vou_total")
    private Float vouTotal;
    @Column(name = "vou_balance")
    private Float vouBalance;

}
