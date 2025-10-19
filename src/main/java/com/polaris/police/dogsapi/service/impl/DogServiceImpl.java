package com.polaris.police.dogsapi.service.impl;

import com.polaris.police.dogsapi.exception.ResourceNotFoundException;
import com.polaris.police.dogsapi.model.db.entity.Dog;
import com.polaris.police.dogsapi.model.db.entity.Supplier;
import com.polaris.police.dogsapi.model.db.mapper.DogMapper;
import com.polaris.police.dogsapi.model.db.repository.DogRepository;
import com.polaris.police.dogsapi.model.db.repository.SupplierRepository;
import com.polaris.police.dogsapi.model.request.DogDTO;
import com.polaris.police.dogsapi.model.request.SearchParam;
import com.polaris.police.dogsapi.service.DogService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DogServiceImpl implements DogService {

    private final SupplierRepository supplierRepository;
    private final DogRepository dogRepository;
    private final MessageSource messageSource;
    private final DogMapper dogMapper;

    public DogServiceImpl(SupplierRepository supplierRepository, DogRepository dogRepository, MessageSource messageSource, DogMapper dogMapper) {
        this.supplierRepository = supplierRepository;
        this.dogRepository = dogRepository;
        this.messageSource = messageSource;
        this.dogMapper = dogMapper;
    }

    /**
     * Save dog record
     *
     * @param dto - Dog dto object
     * @return save dog instance
     */
    @Override
    public DogDTO createDog(DogDTO dto) {
        log.debug("Creating dog with name={} and supplierId={}", dto.getName(), dto.getSupplierId());
        Supplier supplier = findSupplier(dto.getSupplierId());
        Dog dog = dogMapper.toEntity(dto);
        dog.setCreatedAt(LocalDateTime.now());
        dog.setSupplier(supplier);
        dogRepository.save(dog);

        return dogMapper.toDto(dog);
    }

    /**
     * Delete dog instance (Soft delete)
     *
     * @param id - Primary key value
     */
    @Override
    public void deleteDog(Long id) {
        log.debug("Deleting dog id={}", id);
        Dog dog = findDog(id);
        dog.setDeleted(true);
        dogRepository.save(dog);
    }

    /**
     * Update dog instance
     *
     * @param id - Primary key value
     * @param dto - Dog dto object
     * @return Update dog record
     */
    @Override
    public DogDTO updateDog(Long id, DogDTO dto) {
        log.debug("Updating dog id={} with new data={}", id, dto);
        Dog dog = findDog(id);
        Supplier supplier = findSupplier(dto.getSupplierId());
        Dog updatedDog = dogMapper.updateEntity(dto, dog);
        updatedDog.setSupplier(supplier);
        dogRepository.save(updatedDog);
        return dogMapper.toDto(updatedDog);
    }

    /**
     * Get dog record
     *
     * @param id - Primary key value
     * @return dog record
     */
    @Override
    public DogDTO getDog(Long id) {
        Dog dog = findDog(id);
        return dogMapper.toDto(dog);
    }

    private Supplier findSupplier(Integer supplierId) {
        if (supplierId == null) {
            log.error("Supplier id is null");
            throw new ResourceNotFoundException(
                    messageSource.getMessage("invalid.supplier.reference", null, LocaleContextHolder.getLocale())
            );
        }

        Optional<Supplier> optSupplier = optSupplier = supplierRepository.findById(supplierId);

        if (optSupplier.isEmpty()) {
            log.debug("No result found. Supplier id: {}", supplierId);

            String message = messageSource.getMessage("invalid.supplier.reference", null, LocaleContextHolder.getLocale());
            throw new ResourceNotFoundException(message);
        }
        return optSupplier.get();
    }

    private Dog findDog(Long id) {
        Optional<Dog> optDog = dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(id, id);
        if (optDog.isEmpty()) {
            throw new ResourceNotFoundException(messageSource.getMessage("record.not.exist",
                    new Object[]{id}, LocaleContextHolder.getLocale()));
        }
        return optDog.get();
    }

    /**
     * Search dog records
     *
     * @param searchParam - Search parameters
     * @return dog records
     */
    @Override
    public List<DogDTO> getDogList(SearchParam searchParam) {
        Specification<Dog> spec =
                (root, cq, cb) ->
                        cb.or(cb.isFalse(root.get("deleted")), cb.isNull(root.get("deleted")));

        if (StringUtils.isNotBlank(searchParam.getName())) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(cb.lower(root.get("name")), searchParam.getName().toLowerCase()));
        }
        if (StringUtils.isNotBlank(searchParam.getBreed())) {
            spec = spec.and((root, cq, cb) ->
                    cb.equal(cb.lower(root.get("breed")), searchParam.getBreed().toLowerCase()));
        }
        if (StringUtils.isNotBlank(searchParam.getSupplier())) {
            spec = spec.and((root, cq, cb) -> {
                Join<Object, Object> supplierJoin = root.join("supplier", JoinType.LEFT);
                return cb.equal(cb.lower(supplierJoin.get("supplierName")), searchParam.getSupplier().toLowerCase());
            });
        }

        Pageable pageable = PageRequest.of(searchParam.getPageNum(), searchParam.getPageSize(), Sort.by(Sort.Direction.ASC, "id"));

        Page<Dog> dbList = dogRepository.findAll(spec, pageable);
        List<DogDTO> dtoList = dogMapper.toDtoList(dbList.getContent());
        if (dtoList.isEmpty()) {
            throw new ResourceNotFoundException(messageSource.getMessage("records.not.found", null, LocaleContextHolder.getLocale()));
        }
        return dtoList;
    }
}
