package com.improved_enrollment_system.improved_enrollment_system.service;

import com.improved_enrollment_system.improved_enrollment_system.entity.Catalog;
import com.improved_enrollment_system.improved_enrollment_system.repository.CatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public List<Catalog> getAllCatalogs() {
        return catalogRepository.findAll();
    }

    public Catalog getCatalogById(Long id) {
        return catalogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catalog not found"));
    }

    public Catalog saveCatalog(Catalog catalog) {
        return catalogRepository.save(catalog);
    }

    public void deleteCatalog(Long id) {
        catalogRepository.deleteById(id);
    }
}