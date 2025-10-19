package com.polaris.police.dogsapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.polaris.police.dogsapi.TestUtils;
import com.polaris.police.dogsapi.exception.ResourceNotFoundException;
import com.polaris.police.dogsapi.model.db.entity.Dog;
import com.polaris.police.dogsapi.model.db.entity.Supplier;
import com.polaris.police.dogsapi.model.db.mapper.DogMapper;
import com.polaris.police.dogsapi.model.db.repository.DogRepository;
import com.polaris.police.dogsapi.model.db.repository.SupplierRepository;
import com.polaris.police.dogsapi.model.request.DogDTO;
import com.polaris.police.dogsapi.model.request.SearchParam;
import com.polaris.police.dogsapi.service.impl.DogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DogServiceImplTest {

    @Mock
    private DogRepository dogRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private DogMapper dogMapper;

    @InjectMocks
    private DogServiceImpl dogServiceImpl;

    private Supplier supplier;
    private Dog dog;
    private DogDTO  dogDTO;

    @BeforeEach
    void setUp() {
        supplier = TestUtils.getSupplierObject();
        dog = TestUtils.getDogObject();
        dogDTO = TestUtils.getDogDTOObject();

        dogDTO.setSupplierId(supplier.getId());
    }

    @Nested
    class SaveDog {

        @Test
        @DisplayName("Save Dog - Save dog successfully")
        void createRecord_saveDog() {
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
            when(dogMapper.toEntity(dogDTO)).thenReturn(dog);
            when(dogRepository.save(any(Dog.class))).thenReturn(dog);
            when(dogMapper.toDto(dog)).thenReturn(dogDTO);

            DogDTO result = dogServiceImpl.createDog(dogDTO);

            assertNotNull(result);
            assertEquals("Rex", result.getName());
            verify(supplierRepository).findById(1);
            verify(dogRepository).save(any(Dog.class));
            verify(dogMapper).toEntity(dogDTO);
            verify(dogMapper).toDto(dog);
        }

        @Test
        @DisplayName("Save Dog - Supplier not found")
        void createRecord_fail() {
            when(supplierRepository.findById(1)).thenReturn(Optional.empty());
            when(messageSource.getMessage(eq("invalid.supplier.reference"), any(), any(Locale.class)))
                    .thenReturn("Supplier not found");

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                dogServiceImpl.createDog(dogDTO);
            });

            assertEquals("Supplier not found", exception.getMessage());
            verify(supplierRepository).findById(1);
            verify(messageSource).getMessage(eq("invalid.supplier.reference"), any(), any(Locale.class));
            verifyNoInteractions(dogMapper);
            verifyNoInteractions(dogRepository);
        }

        @Test
        @DisplayName("Save Dog - Null Supplier ID")
        void testCreateDog_NullSupplierId() {
            dogDTO.setSupplierId(null);
            when(messageSource.getMessage(eq("invalid.supplier.reference"), any(), any(Locale.class)))
                    .thenReturn("Supplier not found");

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                dogServiceImpl.createDog(dogDTO);
            });

            assertEquals("Supplier not found", exception.getMessage());
            verify(messageSource).getMessage(eq("invalid.supplier.reference"), any(), any(Locale.class));
            verifyNoInteractions(supplierRepository);
            verifyNoInteractions(dogMapper);
            verifyNoInteractions(dogRepository);
        }

        @Test
        @DisplayName("Save Dog - Should set createAt when creating dog record")
        void createRecord_createAt() {
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
            when(dogMapper.toEntity(dogDTO)).thenReturn(dog);
            when(dogMapper.toDto(dog)).thenReturn(dogDTO);
            when(dogRepository.save(any(Dog.class))).thenAnswer(invocation -> {
                Dog savedDog = invocation.getArgument(0);
                assertNotNull(savedDog.getCreatedAt());
                return savedDog;
            });

            DogDTO result = dogServiceImpl.createDog(dogDTO);

            assertNotNull(result);
            verify(dogRepository).save(any(Dog.class));
        }
    }

    @Nested
    class UpdateDog {

        @Test
        @DisplayName("Update Dog - Update dog successfully")
        void testUpdateDog_Success() {
            dog.setId(1L);
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.of(dog));
            when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
            when(dogMapper.updateEntity(dogDTO, dog)).thenReturn(dog);
            when(dogMapper.toDto(dog)).thenReturn(dogDTO);

            DogDTO result = dogServiceImpl.updateDog(1L, dogDTO);

            assertNotNull(result);
            assertEquals(dogDTO, result);
            verify(dogRepository).save(dog);
            verify(dogMapper).updateEntity(dogDTO, dog);
            verify(dogMapper).toDto(dog);
        }

        @Test
        @DisplayName("Update Dog - Dog not found")
        void testUpdateDog_DogNotFound() {
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.empty());
            when(messageSource.getMessage(eq("record.not.exist"), any(), any())).thenReturn("Dog not found");

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> dogServiceImpl.updateDog(1L, dogDTO));

            assertEquals("Dog not found", ex.getMessage());
            verify(dogRepository).findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L);
            verify(messageSource).getMessage(eq("record.not.exist"), any(), any(Locale.class));
            verifyNoInteractions(supplierRepository);
            verifyNoInteractions(dogMapper);
        }

        @Test
        @DisplayName("Update Dog - Supplier not found to update dog")
        void testUpdateDog_SupplierNotFound() {
            dog.setId(1L);
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.of(dog));
            when(supplierRepository.findById(1)).thenReturn(Optional.empty());
            when(messageSource.getMessage(eq("invalid.supplier.reference"), any(), any())).thenReturn("Supplier not found");

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> dogServiceImpl.updateDog(1L, dogDTO));

            verify(dogRepository, never()).save(any());
            assertEquals("Supplier not found", ex.getMessage());
            verify(dogRepository).findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L);
            verify(messageSource).getMessage(eq("invalid.supplier.reference"), any(), any(Locale.class));
            verifyNoInteractions(dogMapper);
        }

        @Test
        @DisplayName("Update Dog - Supplier is null to update dog")
        void testUpdateDog_SupplierIdIsNull() {
            dog.setId(1L);
            dogDTO.setSupplierId(null);
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.of(dog));
            when(messageSource.getMessage(eq("invalid.supplier.reference"), any(), any())).thenReturn("Supplier not found");

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> dogServiceImpl.updateDog(1L, dogDTO));

            verify(dogRepository, never()).save(any());
            assertEquals("Supplier not found", ex.getMessage());
            verify(messageSource).getMessage(eq("invalid.supplier.reference"), any(), any(Locale.class));
            verifyNoInteractions(supplierRepository);
            verifyNoInteractions(dogMapper);
        }
    }

    @Nested
    class DeleteDog {

        @Test
        @DisplayName("Delete Dog - Delete dog successfully")
        void testDeleteDog_Success() {
            dog.setId(1L);
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.of(dog));

            dogServiceImpl.deleteDog(1L);

            assertTrue(dog.getDeleted());
            verify(dogRepository).save(dog);
            verify(dogRepository).findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L);
        }

        @Test
        @DisplayName("Delete Dog - Dog not found to delete")
        void testDeleteDog_NotFound() {
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.empty());
            when(messageSource.getMessage(eq("record.not.exist"), any(), any(Locale.class))).thenReturn("Record not exist");

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> dogServiceImpl.deleteDog(1L));

            assertEquals("Record not exist", ex.getMessage());
            verify(dogRepository, never()).save(any(Dog.class));
        }

        @Test
        @DisplayName("Delete Dog - Dog is already deleted")
        void testDeleteDog_AlreadyDeleted() {
            dog.setId(1L);
            dog.setDeleted(true);
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.of(dog));

            dogServiceImpl.deleteDog(1L);

            assertTrue(dog.getDeleted());
            verify(dogRepository).save(dog);
        }

        @Test
        @DisplayName("Delete Dog - For any db failure")
        void testDeleteDog_RepositoryError() {
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.of(dog));
            doThrow(new RuntimeException("DB error")).when(dogRepository).save(any(Dog.class));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> dogServiceImpl.deleteDog(1L));
            assertEquals("DB error", ex.getMessage());
        }
    }

    @Nested
    class GetDog {

        @Test
        @DisplayName("Get Dog - Get dog successfully")
        void testGetDog_Success() {
            dog.setId(1L);
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.of(dog));
            when(dogMapper.toDto(dog)).thenReturn(dogDTO);

            DogDTO result = dogServiceImpl.getDog(1L);

            assertNotNull(result);
            assertEquals("Rex", result.getName());
            verify(dogRepository, times(1)).findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L);
            verify(dogMapper, times(1)).toDto(dog);
        }

        @Test
        @DisplayName("Get Dog - Dog not found")
        void testGetDog_NotFound() {
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.empty());
            when(messageSource.getMessage(eq("record.not.exist"), any(), any(Locale.class)))
                    .thenReturn("Dog not found");

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> dogServiceImpl.getDog(1L));

            assertEquals("Dog not found", ex.getMessage());
            verify(dogRepository, times(1)).findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L);
            verifyNoInteractions(dogMapper);
        }

        @Test
        @DisplayName("Get Dog - Deleted flag is null")
        void testGetDog_DeletedFlagNull() {
            dog.setId(1L);
            dog.setDeleted(null);
            when(dogRepository.findByIdAndDeletedIsFalseOrIdAndDeletedIsNull(1L, 1L)).thenReturn(Optional.of(dog));
            when(dogMapper.toDto(dog)).thenReturn(dogDTO);

            DogDTO result = dogServiceImpl.getDog(1L);

            assertNotNull(result);
            assertEquals("Rex", result.getName());
        }
    }

    @Nested
    class GetDogList {

        @Test
        @DisplayName("Get Dog List - All parameters are present ")
        void testGetDogList_AllParams_Success() {
            SearchParam param = new SearchParam();
            param.setName("Rex");
            param.setBreed("German Shepherd");
            param.setSupplier("Breeder");
            param.setPageNum(0);
            param.setPageSize(5);

            dog.setId(1L);
            dogDTO.setId(1L);

            Page<Dog> dogPage = new PageImpl<>(List.of(dog));
            when(dogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(dogPage);
            when(dogMapper.toDtoList(List.of(dog))).thenReturn(List.of(dogDTO));

            List<DogDTO> result = dogServiceImpl.getDogList(param);

            assertEquals(1, result.size());
            assertEquals("Rex", result.get(0).getName());
            verify(dogRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
            verify(dogMapper, times(1)).toDtoList(anyList());
        }

        @Test
        @DisplayName("Get Dog List - With single filter")
        void testGetDogList_NameOnly_Success() {
            SearchParam param = new SearchParam();
            param.setName("Rex");
            param.setPageNum(0);
            param.setPageSize(10);

            dog.setId(1L);
            dogDTO.setId(1L);

            when(dogRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(dog)));
            when(dogMapper.toDtoList(List.of(dog))).thenReturn(List.of(dogDTO));

            List<DogDTO> result = dogServiceImpl.getDogList(param);

            assertEquals(1, result.size());
            assertEquals("Rex", result.get(0).getName());
        }

        @Test
        @DisplayName("Get Dog List - No records found")
        void testGetDogList_NoResults_ThrowsException() {
            SearchParam param = new SearchParam();
            param.setPageNum(0);
            param.setPageSize(5);

            when(dogRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));
            when(messageSource.getMessage(eq("records.not.found"), any(), any()))
                    .thenReturn("No records found");

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                dogServiceImpl.getDogList(param);
            });

            assertEquals("No records found", exception.getMessage());
        }

        @Test
        @DisplayName("Get Dog List - Null and blank filters")
        void testGetDogList_BlankFilters_Success() {
            SearchParam param = new SearchParam();
            param.setName("");
            param.setBreed(null);
            param.setSupplier("   ");
            param.setPageNum(0);
            param.setPageSize(2);

            dog.setId(1L);
            dogDTO.setId(1L);

            when(dogRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(dog)));
            when(dogMapper.toDtoList(List.of(dog))).thenReturn(List.of(dogDTO));

            List<DogDTO> result = dogServiceImpl.getDogList(param);

            assertEquals(1, result.size());
            verify(dogRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        }

        @Test
        @DisplayName("Get Dog List - Page size or page number at limits")
        void testGetDogList_PageBounds() {
            SearchParam param = new SearchParam();
            param.setPageNum(0);
            param.setPageSize(1);

            dog.setId(1L);
            dogDTO.setId(1L);

            when(dogRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(dog)));
            when(dogMapper.toDtoList(List.of(dog))).thenReturn(List.of(dogDTO));

            List<DogDTO> result = dogServiceImpl.getDogList(param);

            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Get Dog List - Mapper return empty list")
        void testGetDogList_MapperReturnsEmpty_ThrowsException() {
            SearchParam param = new SearchParam();
            param.setPageNum(0);
            param.setPageSize(5);

            dog.setId(1L);

            when(dogRepository.findAll(any(Specification.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(dog)));
            when(dogMapper.toDtoList(anyList())).thenReturn(Collections.emptyList());
            when(messageSource.getMessage(eq("records.not.found"), any(), any()))
                    .thenReturn("No records found");

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                dogServiceImpl.getDogList(param);
            });

            assertEquals("No records found", exception.getMessage());
        }

    }
}
