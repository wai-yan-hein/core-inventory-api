package com.cv.inv.api.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Getter
@Setter
@ToString
@Entity
@Table(name = "sys_prop")
public class SysProperty implements java.io.Serializable {
    @Id
    @Column(name = "prop_key")
    private String propKey;
    @Column(name = "prop_value")
    private String propValue;
    @Column(name = "remark")
    private String remark;
    @Column(name = "comp_code")
    private String compCode;

    public SysProperty() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SysProperty that = (SysProperty) o;
        return propKey != null && Objects.equals(propKey, that.propKey);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
