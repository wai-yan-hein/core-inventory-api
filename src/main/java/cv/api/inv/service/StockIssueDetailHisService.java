/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cv.api.inv.service;

import cv.api.inv.entity.StockIssueDetailHis;

import java.util.List;

/**
 * @author wai yan
 */
 public interface StockIssueDetailHisService {
      List<StockIssueDetailHis> search(String dmgVouId);
}
