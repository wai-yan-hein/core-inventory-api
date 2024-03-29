/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.common;

import cv.api.entity.Currency;
import cv.api.entity.Location;
import cv.api.entity.SaleMan;
import cv.api.entity.Trader;
import lombok.Data;

/**
 * @author wai yan
 */
@Data
public class RoleDefault {
    private Currency defaultCurrency;
    private Location defaultLocation;
    private SaleMan defaultSaleMan;
    private Trader defaultCustomer;
    private Trader defaultSupplier;
}
