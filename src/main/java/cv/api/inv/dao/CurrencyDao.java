/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.dao;

import cv.api.inv.entity.Currency;

import java.util.List;

/**
 * @author WSwe
 */
public interface CurrencyDao {

    Currency save(Currency cur);

    Currency findById(String id);

    List<Currency> search(String code, String name);

    int delete(String code, String compCode);
}
