/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Lenovo
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "role_prop")
public class RoleProperty implements Serializable {

    @EmbeddedId
    private RolePropertyKey key;
    @Column(name = "value")
    private String propValue;
    private String compCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoleProperty that = (RoleProperty) o;
        return key != null && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
