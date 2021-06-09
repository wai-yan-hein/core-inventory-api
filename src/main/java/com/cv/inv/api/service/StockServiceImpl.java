/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.service;

import com.cv.inv.api.dao.CharacterNoDao;
import com.cv.inv.api.dao.StockDao;
import com.cv.inv.api.entity.Stock;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Lenovo
 */
@Transactional
@Service
public class StockServiceImpl implements StockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);
    @Autowired
    private StockDao dao;
    @Autowired
    private CharacterNoDao chDao;
    @Autowired
    private SeqTableService seqService;

    @Override
    public Stock save(Stock stock) {

        if (stock.getStockCode() == null || stock.getStockCode().isEmpty()) {
            Integer macId = stock.getMacId();
            String compCode = stock.getCompCode();
            stock.setStockCode(getStockCode(macId, "Stock", "-", compCode));
        }
        return dao.save(stock);
    }

    @Override
    public Stock findById(String id) {
        return dao.findById(id);
    }

    @Override
    public List<Stock> findAll() {
        return dao.findAll();
    }

    @Override
    public List<Stock> findActiveStock() {
        return dao.findActiveStock();
    }

    @Override
    public int delete(String id) {
        return dao.delete(id);
    }

    /*private String getMedCode(String stockName, StockType itemType) {
        String medCode = "";
        CharacterNo characterNo = null;

        if (!stockName.equals("")) {
            try {
                for (int i = 0; i < stockName.length(); i++) {
                    String strTmp = stockName.substring(i, i + 1).toUpperCase();
                    if (!strTmp.trim().equals("")) { //Space character detection
                        if (Util1.isNumber(strTmp)) {
                            i = stockName.length();
                            characterNo = new CharacterNo(" ", "00");
                        } else {
                            characterNo = chDao.findById(strTmp);
                            if (characterNo != null) {
                                i = stockName.length();
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            String strType = "";
            if (characterNo != null) {
                strType = characterNo.getStrNumber();
            }
            medCode = itemType.getItemTypeCode() + strType;
            int typeLength = itemType.getItemTypeCode().length();

            try {
                Object maxMedId = dao.getMax("select max(o.stock_code) from stock o where o.stock_code like '" + medCode + "%'");
                int ttlLength = 5;// get from system property
                int leftLength = ttlLength - medCode.length();

                if (maxMedId == null) {
                    for (int i = 0; i < leftLength - 1; i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + "1";
                } else {
                    int begin = typeLength + 2;
                    String tmpMedSerial = maxMedId.toString().substring(begin);

                    Integer tmpSerial = Integer.parseInt(tmpMedSerial) + 1;
                    tmpMedSerial = tmpSerial.toString();

                    for (int i = 0; i < (leftLength - tmpMedSerial.length()); i++) {
                        medCode = medCode + "0";
                    }

                    medCode = medCode + tmpMedSerial;
                }
            } catch (NumberFormatException ex) {
                LOGGER.error("getMedCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }

        return medCode;
    }
     */
    private String getStockCode(Integer macId, String option, String period, String compCode) {

        int seqNo = seqService.getSequence(macId, option, period, compCode);
        String tmpCatCode = String.format("%0" + 3 + "d", macId) + "-" + String.format("%0" + 5 + "d", seqNo);
        return tmpCatCode;
    }

    @Override
    public List<Stock> search(String stockType) {
        return dao.search(stockType);
    }

    @Override
    public List<Stock> searchC(String stockCat) {
        return dao.searchC(stockCat);
    }

    @Override
    public List<Stock> searchB(String stockBrand) {
        return dao.searchB(stockBrand);
    }

    @Override
    public List<Stock> searchM(String updatedDate) {
        return dao.searchM(updatedDate);
    }
}
