/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * Patient class is patient information. Sharing "trader" table with Patient,
 * Customer and Trader class. Database table name is trader.
 */
@Entity
@Table(name = "trader")
@DiscriminatorValue("S")
public class Supplier extends Trader implements java.io.Serializable {

    public Supplier() {
    }

    public Supplier(String code, String traderName) {
        super(code, traderName);
    }

}
