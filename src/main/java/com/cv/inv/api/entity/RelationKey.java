/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Lenovo
 */
@Embeddable
public class RelationKey implements Serializable {

    @Column(name = "from_unit")
    private String fromUnit;
    @Column(name = "to_unit")
    private String toUnit;
    @Column(name = "pattern_code")
    private String patternId;

    public RelationKey() {
    }

    public RelationKey(String fromUnit, String toUnit, String patternId) {
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
        this.patternId = patternId;
    }

    public String getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(String fromUnit) {
        this.fromUnit = fromUnit;
    }

    public String getToUnit() {
        return toUnit;
    }

    public void setToUnit(String toUnit) {
        this.toUnit = toUnit;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.fromUnit);
        hash = 43 * hash + Objects.hashCode(this.patternId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RelationKey other = (RelationKey) obj;
        if (!Objects.equals(this.fromUnit, other.fromUnit)) {
            return false;
        }
        if (!Objects.equals(this.toUnit, other.toUnit)) {
            return false;
        }
        return Objects.equals(this.patternId, other.patternId);
    }

    public String getPatternId() {
        return patternId;
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

}
