package com.polaris.police.dogsapi.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.polaris.police.dogsapi.TestUtils;
import com.polaris.police.dogsapi.model.db.entity.Dog;
import com.polaris.police.dogsapi.model.db.mapper.DogMapper;
import com.polaris.police.dogsapi.model.request.DogDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.List;

class DogMapperTest {

    private DogMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(DogMapper.class);
    }

    @Test
    @DisplayName("Mapper - Return entity")
    void testToEntity_positive() {
        DogDTO dto = TestUtils.getDogDTOForMapper1();

        Dog entity = mapper.toEntity(dto);

        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "supplier", "deleted")
                .isEqualTo(dto);
    }

    @Test
    @DisplayName("Mapper - Return dto")
    void testToDto_positive() {
        Dog entity = TestUtils.getDogForMapper1();

        DogDTO dto = mapper.toDto(entity);

        assertThat(dto)
                .usingRecursiveComparison()
                .ignoringFields("supplierId", "supplierName")
                .isEqualTo(entity);
        assertThat(dto.getSupplierId()).isEqualTo(entity.getSupplier().getId());
        assertThat(dto.getSupplierName()).isEqualTo(entity.getSupplier().getSupplierName());
    }

    @Test
    @DisplayName("Mapper - Return DTO list")
    void testToDtoList_positive() {
        Dog dog1 = TestUtils.getDogForMapper1();
        Dog dog2 = TestUtils.getDogForMapper2();
        Dog dog3 = TestUtils.getDogForMapper3();

        List<DogDTO> dtoList = mapper.toDtoList(List.of(dog1, dog2, dog3));

        assertEquals(3, dtoList.size());
        assertThat(dtoList.get(0)).usingRecursiveComparison().ignoringFields("supplierId", "supplierName").isEqualTo(dog1);
        assertThat(dtoList.get(1)).usingRecursiveComparison().ignoringFields("supplierId", "supplierName").isEqualTo(dog2);
        assertThat(dtoList.get(2)).usingRecursiveComparison().ignoringFields("supplierId", "supplierName").isEqualTo(dog3);

        assertThat(dtoList.get(0).getSupplierId()).isEqualTo(dog1.getSupplier().getId());
        assertThat(dtoList.get(0).getSupplierName()).isEqualTo(dog1.getSupplier().getSupplierName());
        assertThat(dtoList.get(1).getSupplierId()).isEqualTo(dog2.getSupplier().getId());
        assertThat(dtoList.get(1).getSupplierName()).isEqualTo(dog2.getSupplier().getSupplierName());
        assertThat(dtoList.get(2).getSupplierId()).isEqualTo(dog3.getSupplier().getId());
        assertThat(dtoList.get(2).getSupplierName()).isEqualTo(dog3.getSupplier().getSupplierName());
    }

    @Test
    @DisplayName("Mapper - Return updated entity")
    void testUpdateEntity_positive() {
        DogDTO dto = TestUtils.getDogDTOForMapper1();
        Dog entity = TestUtils.getDogForMapper1();

        dto.setName("Rocky");
        dto.setBadgeId("ZM-010");
        dto.setKennellingCharacteristic("Change department");

        Dog updated = mapper.updateEntity(dto, entity);

        assertThat(updated).usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "supplier", "deleted")
                .isEqualTo(entity);
        assertThat(entity.getSupplier().getId()).isEqualTo(updated.getSupplier().getId());
        assertThat(entity.getSupplier().getSupplierName()).isEqualTo(updated.getSupplier().getSupplierName());
    }

    @Test
    @DisplayName("Mapper - Return null for null dto")
    void testToEntity_withNullDto() {
        Dog entity = mapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    @DisplayName("Mapper - Return null for null entity")
    void testToDto_withNullEntity() {
        DogDTO dto = mapper.toDto(null);
        assertNull(dto);
    }

    @Test
    @DisplayName("Mapper - Return dto without supplier for null supplier")
    void testToDto_withNullSupplier() {
        Dog dog = TestUtils.getDogForMapper1();
        dog.setSupplier(null);

        DogDTO dto = mapper.toDto(dog);

        assertNotNull(dto);
        assertNull(dto.getSupplierId());
        assertNull(dto.getSupplierName());
        assertEquals(1L, dto.getId());
    }

    @Test
    @DisplayName("Mapper - Return null for null list")
    void testToDtoList_withNullList() {
        List<DogDTO> result = mapper.toDtoList(null);
        assertNull(result);
    }

    @Test
    @DisplayName("Mapper - Return empty list for empty entity list")
    void testToDtoList_withEmptyList() {
        List<DogDTO> result = mapper.toDtoList(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Mapper - Return the existing instance if dto is null")
    void testUpdateEntity_withNullDto() {
        Dog existing = TestUtils.getDogForMapper1();
        Dog result = mapper.updateEntity(null, existing);
        assertEquals(existing, result);
    }
}
