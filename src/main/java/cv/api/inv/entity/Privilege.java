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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * @author winswe
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "privilege")
public class Privilege implements java.io.Serializable {

    @EmbeddedId
    private PrivilegeKey key;
    @Column(name = "allow")
    private boolean allow;

    public Privilege(PrivilegeKey key, boolean allow) {
        this.key = key;
        this.allow = allow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Privilege privilege = (Privilege) o;
        return key != null && Objects.equals(key, privilege.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
