/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "stock_in_out")
public class StockInOut implements Serializable {

    @Id
    @Column(name = "vou_no", unique = true, nullable = false)
    private String vouNo;
    @Column(name = "remark")
    private String remark;
    @Column(name = "description")
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "comp_code")
    private String compCode;
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "vou_status",insertable = false,updatable = false),
            @JoinColumn(name = "comp_code",insertable = false,updatable = false),
            @JoinColumn(name = "dept_id",insertable = false,updatable = false)
    })
    private VouStatus vouStatus;
    @Column(name = "mac_id")
    private Integer macId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "vou_date")
    private Date vouDate;
    @Column(name = "deleted")
    private Boolean deleted;
    @Transient
    private String status = "STATUS";
    @Transient
    private List<StockInOutDetail> listSH;
    @Transient
    private List<String> listDel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockInOut that = (StockInOut) o;
        return Objects.equals(vouNo, that.vouNo);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
