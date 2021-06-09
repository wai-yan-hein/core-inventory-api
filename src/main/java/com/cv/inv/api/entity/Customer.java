/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import javax.persistence.*;

/**
 *
 * Customer class. Sharing "trader" table with Supplier, Patient and Trader
 * class. Database table name is trader.
 */
@Entity
//@Table(name="trader")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("C")
public class Customer extends Trader implements java.io.Serializable {

    @Column(name = "credit_days", nullable = true)
    private Integer creditLimit;
    @Column(name = "credit_limit", nullable = true)
    private Integer creditDays;
    @Column(name = "contact_person")
    private String contactPerson;

    public Customer() {
        super();
    }

    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    public Integer getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Integer creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

}
