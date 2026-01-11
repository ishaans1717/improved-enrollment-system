package com.improved_enrollment_system.improved_enrollment_system.controller;

import com.improved_enrollment_system.improved_enrollment_system.entity.Catalog;
import com.improved_enrollment_system.improved_enrollment_system.service.CatalogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping
    public List<Catalog> getAllCatalogs() {
        return catalogService.getAllCatalogs();
    }

    @GetMapping("/{id}")
    public Catalog getCatalog(@PathVariable Long id) {
        return catalogService.getCatalogById(id);
    }
    @PostMapping
    public Catalog createCatalog(@RequestBody Catalog catalog) {
        return catalogService.saveCatalog(catalog);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCatalog(@PathVariable Long id) {
        catalogService.deleteCatalog(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Catalog deleted successfully");
    }
}