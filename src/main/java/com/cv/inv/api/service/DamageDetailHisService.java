/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.entity.DamageDetailHis;
import java.util.List;

/**
 *
 * @author lenovo
 */
 public interface DamageDetailHisService {
      List<DamageDetailHis> search(String dmgVouId);
}
