/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.inv.api.controller;

import com.cv.inv.api.common.ReturnObject;
import com.cv.inv.api.entity.Category;
import com.cv.inv.api.entity.Currency;
import com.cv.inv.api.entity.CurrencyKey;
import com.cv.inv.api.entity.Location;
import com.cv.inv.api.entity.Region;
import com.cv.inv.api.entity.SaleMan;
import com.cv.inv.api.entity.Stock;
import com.cv.inv.api.entity.StockBrand;
import com.cv.inv.api.entity.StockType;
import com.cv.inv.api.entity.StockUnit;
import com.cv.inv.api.entity.Trader;
import com.cv.inv.api.service.CategoryService;
import com.cv.inv.api.service.CurrencyService;
import com.cv.inv.api.service.LocationService;
import com.cv.inv.api.service.RegionService;
import com.cv.inv.api.service.SaleManService;
import com.cv.inv.api.service.StockBrandService;
import com.cv.inv.api.service.StockService;
import com.cv.inv.api.service.StockTypeService;
import com.cv.inv.api.service.StockUnitService;
import com.cv.inv.api.service.TraderService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Lenovo
 */
@RestController
@RequestMapping("/setup")
@Slf4j
public class SetupController {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private SaleManService saleManService;
    @Autowired
    private StockBrandService brandService;
    @Autowired
    private StockTypeService typeService;
    @Autowired
    private StockUnitService unitService;
    @Autowired
    private TraderService traderService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private StockService stockService;
    private final ReturnObject ro = new ReturnObject();

    @PostMapping(path = "/save-currency")
    public ResponseEntity<Currency> saveCurrency(@RequestBody Currency machine, HttpServletRequest request) throws Exception {
        Currency currency = currencyService.save(machine);
        return ResponseEntity.ok(currency);
    }

    @PostMapping(path = "/find-by-id")
    public ResponseEntity<Currency> findById(@RequestBody CurrencyKey key) {
        Currency cur = currencyService.findById(key);
        return ResponseEntity.ok(cur);
    }

    @GetMapping(path = "/get-currency")
    public ResponseEntity<List<Currency>> getCurrency(@RequestParam String compCode) {
        List<Currency> currency = currencyService.search("-", "-", compCode);
        return ResponseEntity.ok(currency);
    }

    @PostMapping(path = "/save-category")
    public ResponseEntity<Category> saveCategory(@RequestBody Category cat, HttpServletRequest request) throws Exception {
        log.info("/save-category");
        Category category = categoryService.save(cat);
        return ResponseEntity.ok(category);
    }

    @GetMapping(path = "/get-category")
    public ResponseEntity<List<Category>> getCategory(@RequestParam String compCode) {
        log.info("/get-category");
        List<Category> listCat = categoryService.findAll(compCode);
        return ResponseEntity.ok(listCat);
    }

    @DeleteMapping(path = "/delete-category")
    public ResponseEntity<ReturnObject> deleteCategory(@RequestParam String code) {
        log.info("/delete-category");
        categoryService.delete(code);
        ro.setMeesage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-category")
    public ResponseEntity<Category> findCategory(@RequestParam String code) {
        log.info("/find-category");
        Category cat = categoryService.findByCode(code);
        return ResponseEntity.ok(cat);
    }

    @PostMapping(path = "/save-location")
    public ResponseEntity<Location> saveLocation(@RequestBody Location location, HttpServletRequest request) throws Exception {
        log.info("/save-locaiton");
        Location loc = locationService.save(location);
        return ResponseEntity.ok(loc);
    }

    @GetMapping(path = "/get-location")
    public ResponseEntity<List<Location>> getLocation(@RequestParam String compCode) {
        log.info("/get-location");
        List<Location> listLoc = locationService.findAll(compCode);
        return ResponseEntity.ok(listLoc);
    }

    @DeleteMapping(path = "/delete-location")
    public ResponseEntity<ReturnObject> deleteLocation(@RequestParam String code) {
        log.info("/delete-location");
        locationService.delete(code);
        ro.setMeesage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-location")
    public ResponseEntity<Location> findLocation(@RequestParam String locCode) {
        log.info("/find-location");
        Location loc = locationService.findByCode(locCode);
        return ResponseEntity.ok(loc);
    }

    @PostMapping(path = "/save-saleman")
    public ResponseEntity<SaleMan> saveSaleMan(@RequestBody SaleMan saleMan, HttpServletRequest request) throws Exception {
        log.info("/save-saleman");
        SaleMan sm = saleManService.save(saleMan);
        return ResponseEntity.ok(sm);
    }

    @GetMapping(path = "/get-saleman")
    public ResponseEntity<List<SaleMan>> getSaleMan(@RequestParam String compCode) {
        log.info("/get-saleman");
        List<SaleMan> listSM = saleManService.findAll(compCode);
        return ResponseEntity.ok(listSM);
    }

    @DeleteMapping(path = "/delete-saleman")
    public ResponseEntity<ReturnObject> deleteSaleMan(@RequestParam String code) {
        log.info("/delete-saleman");
        saleManService.delete(code);
        ro.setMeesage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-saleman")
    public ResponseEntity<SaleMan> findSaleMan(@RequestParam String code) {
        log.info("/find-saleman");
        SaleMan sm = saleManService.findByCode(code);
        return ResponseEntity.ok(sm);
    }

    @PostMapping(path = "/save-brand")
    public ResponseEntity<StockBrand> saveBrand(@RequestBody StockBrand brand, HttpServletRequest request) throws Exception {
        log.info("/save-brand");
        StockBrand b = brandService.save(brand);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-brand")
    public ResponseEntity<List<StockBrand>> getBrand(@RequestParam String compCode) {
        log.info("/get-brand");
        List<StockBrand> listB = brandService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-brand")
    public ResponseEntity<ReturnObject> deleteBrand(@RequestParam String code) {
        log.info("delete-brand");
        brandService.delete(code);
        ro.setMeesage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-brand")
    public ResponseEntity<StockBrand> findBrand(@RequestParam String code) {
        log.info("/find-brand");
        StockBrand b = brandService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-type")
    public ResponseEntity<StockType> saveType(@RequestBody StockType type, HttpServletRequest request) throws Exception {
        log.info("/save-type");
        StockType b = typeService.save(type);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-type")
    public ResponseEntity<List<StockType>> getType(@RequestParam String compCode) {
        log.info("/get-type");
        List<StockType> listB = typeService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-type")
    public ResponseEntity<ReturnObject> deleteType(@RequestParam String code) {
        log.info("/delete-type");
        typeService.delete(code);
        ro.setMeesage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-type")
    public ResponseEntity<StockType> findType(@RequestParam String code) {
        log.info("/find-type");
        StockType b = typeService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-unit")
    public ResponseEntity<StockUnit> saveUnit(@RequestBody StockUnit unit, HttpServletRequest request) {
        log.info("/save-unit");
        StockUnit b = unitService.save(unit);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-unit")
    public ResponseEntity<List<StockUnit>> getUnit(@RequestParam String compCode) {
        log.info("/get-unit");
        List<StockUnit> listB = unitService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-unit")
    public ResponseEntity<ReturnObject> deleteUnit(@RequestParam String code) {
        log.info("/delete-unit");
        unitService.delete(code);
        ro.setMeesage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-unit")
    public ResponseEntity<StockUnit> findUnit(@RequestParam String code) {
        log.info("/find-type");
        StockUnit b = unitService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-region")
    public ResponseEntity<Region> saveRegion(@RequestBody Region region, HttpServletRequest request) throws Exception {
        log.info("/save-region");
        Region b = regionService.save(region);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-region")
    public ResponseEntity<List<Region>> getRegion(@RequestParam String compCode) {
        log.info("/get-region");
        List<Region> listB = regionService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-region")
    public ResponseEntity<ReturnObject> deleteRegion(@RequestParam String code) {
        log.info("/delete-unit");
        List<Trader> search = traderService.search(code, "-");
        if (search.isEmpty()) {
            regionService.delete(code);
            ro.setMeesage("Deleted.");
        } else {
            ro.setMeesage("Can't delete.");
        }
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-region")
    public ResponseEntity<Region> findRegion(@RequestParam String code) {
        log.info("/find-region");
        Region b = regionService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-trader")
    public ResponseEntity<Trader> saveTrader(@RequestBody Trader trader, HttpServletRequest request) throws Exception {
        log.info("/save-trader");
        Trader b = traderService.saveTrader(trader);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-trader")
    public ResponseEntity<List<Trader>> getTrader(@RequestParam String compCode) {
        log.info("/get-trader");
        List<Trader> listB = traderService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/get-customer")
    public ResponseEntity<List<Trader>> getCustomer(@RequestParam String compCode) {
        log.info("/get-customer");
        List<Trader> listB = traderService.findCustomer(compCode);
        return ResponseEntity.ok(listB);
    }

    @GetMapping(path = "/get-supplier")
    public ResponseEntity<List<Trader>> getSupplier(@RequestParam String compCode) {
        log.info("/get-supplier");
        List<Trader> listB = traderService.findSupplier(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-trader")
    public ResponseEntity<ReturnObject> deleteTrader(@RequestParam String code) {
        log.info("/delete-trader");
        traderService.delete(code);
        ro.setMeesage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-trader")
    public ResponseEntity<Trader> findTrader(@RequestParam String code) {
        log.info("/find-trader");
        Trader b = traderService.findByCode(code);
        return ResponseEntity.ok(b);
    }

    @PostMapping(path = "/save-stock")
    public ResponseEntity<Stock> saveStock(@RequestBody Stock region, HttpServletRequest request) throws Exception {
        log.info("/save-stock");
        Stock b = stockService.save(region);
        return ResponseEntity.ok(b);
    }

    @GetMapping(path = "/get-stock")
    public ResponseEntity<List<Stock>> getStock(@RequestParam String compCode) {
        log.info("/get-stock");
        List<Stock> listB = stockService.findAll(compCode);
        return ResponseEntity.ok(listB);
    }

    @DeleteMapping(path = "/delete-stock")
    public ResponseEntity<ReturnObject> deleteStock(@RequestParam String code) {
        log.info("/delete-stock");
        stockService.delete(code);
        ro.setMeesage("Deleted.");
        return ResponseEntity.ok(ro);
    }

    @GetMapping(path = "/find-stock")
    public ResponseEntity<Stock> findStock(@RequestParam String code) {
        log.info("/find-region");
        Stock b = stockService.findById(code);
        return ResponseEntity.ok(b);
    }

}
