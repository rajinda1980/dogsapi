package com.polaris.police.dogsapi.model.db.repository;

import com.polaris.police.dogsapi.model.db.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface DogRepository extends JpaRepository<Dog, Long>, JpaSpecificationExecutor<Dog> {
    Optional<Dog> findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(Long id1, Long id2);
}
