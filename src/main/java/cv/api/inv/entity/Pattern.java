package cv.api.inv.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pattern")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Pattern implements java.io.Serializable {
    @Id
    @Column(name = "code")
    private String patternCode;
    @Column(name = "name")
    private String patternName;
    @ManyToOne
    @JoinColumn(name = "vou_status")
    private VouStatus vouStatus;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "active")
    private boolean active;
    @Column(name = "mac_id")
    private Integer macId;
    @Transient
    private List<PatternDetail> detailList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Pattern pattern = (Pattern) o;
        return patternCode != null && Objects.equals(patternCode, pattern.patternCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
