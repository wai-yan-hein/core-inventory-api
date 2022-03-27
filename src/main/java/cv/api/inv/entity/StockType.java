/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

/**
 * @author wai yan
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "stock_type")
public class StockType implements java.io.Serializable {

    @Id
    @Column(name = "stock_type_code", unique = true, nullable = false, length = 5)
    private String stockTypeCode;
    @Column(name = "stock_type_name", nullable = false, length = 50, unique = true)
    private String stockTypeName;
    @Column(name = "account_id")
    private String accountId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "comp_code")
    private String compCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockType stockType = (StockType) o;
        return stockTypeCode != null && Objects.equals(stockTypeCode, stockType.stockTypeCode);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
