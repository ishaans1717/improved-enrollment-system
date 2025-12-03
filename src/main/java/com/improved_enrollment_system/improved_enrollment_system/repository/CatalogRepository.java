package com.improved_enrollment_system.improved_enrollment_system.repository;

import com.improved_enrollment_system.improved_enrollment_system.entity.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogRepository extends JpaRepository<Catalog, Long> {
}