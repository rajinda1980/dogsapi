package com.polaris.police.dogsapi.model.db.mapper;

import com.polaris.police.dogsapi.model.db.entity.Dog;
import com.polaris.police.dogsapi.model.request.DogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DogMapper {

    Dog toEntity(DogDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.supplierName")
    DogDTO toDto(Dog d);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Dog updateEntity(DogDTO dto, @MappingTarget Dog entity);

    List<DogDTO> toDtoList(List<Dog> dogList);
}
