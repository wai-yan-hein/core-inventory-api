/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.common;

import com.cv.inv.api.entity.Currency;
import com.cv.inv.api.entity.Location;
import com.cv.inv.api.entity.SaleMan;
import com.cv.inv.api.entity.Trader;
import lombok.Data;

/**
 * @author Lenovo
 */
@Data
public class RoleDefault {
    private Currency defaultCurrency;
    private Location defaultLocation;
    private SaleMan defaultSaleMan;
    private Trader defaultCustomer;
    private Trader defaultSupplier;
}
