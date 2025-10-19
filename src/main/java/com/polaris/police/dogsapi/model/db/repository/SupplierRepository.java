package com.polaris.police.dogsapi.model.db.repository;

import com.polaris.police.dogsapi.model.db.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier,Integer> {
}
