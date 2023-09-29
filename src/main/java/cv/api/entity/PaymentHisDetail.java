package cv.api.entity;

import lombok.Data;

import jakarta.persistence.*;
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
    private double payAmt;
    @Column(name = "dis_amt")
    private double disAmt;
    @Column(name = "dis_percent")
    private double disPercent;
    @Column(name = "full_paid")
    private boolean fullPaid;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "reference")
    private String reference;
    @Temporal(TemporalType.DATE)
    @Column(name = "sale_vou_date")
    private Date saleDate;
    @Column(name = "vou_total")
    private double vouTotal;
    @Column(name = "vou_balance")
    private double vouBalance;

}
