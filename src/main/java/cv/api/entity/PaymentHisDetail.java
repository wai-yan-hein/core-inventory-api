package cv.api.entity;

import lombok.Builder;
import lombok.Data;

import jakarta.persistence.*;

import java.time.LocalDate;
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
    private Double payAmt;
    @Column(name = "dis_amt")
    private Double disAmt;
    @Column(name = "dis_percent")
    private Double disPercent;
    @Column(name = "full_paid")
    private Boolean fullPaid;
    @Column(name = "cur_code")
    private String curCode;
    @Column(name = "remark")
    private String remark;
    @Column(name = "reference")
    private String reference;
    @Temporal(TemporalType.DATE)
    @Column(name = "sale_vou_date")
    private LocalDate saleDate;
    @Column(name = "vou_total")
    private Double vouTotal;
    @Column(name = "vou_balance")
    private Double vouBalance;

}
