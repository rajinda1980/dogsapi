package com.polaris.police.dogsapi;

import com.polaris.police.dogsapi.model.db.entity.Dog;
import com.polaris.police.dogsapi.model.db.entity.Supplier;
import com.polaris.police.dogsapi.model.enums.DogStatus;
import com.polaris.police.dogsapi.model.enums.Gender;
import com.polaris.police.dogsapi.model.enums.LeavingReason;
import com.polaris.police.dogsapi.model.request.DogDTO;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.time.LocalDate;

/**
 * Utility class for creating test data.
 */
public class TestUtils {

    public static String getDogDTO1() {
        return """
                {
                  "name": "Rex",
                  "breed": "German Shepherd",
                  "supplierId": 1,
                  "badgeId": "K9-001",
                  "birthDate": "2021-05-20",
                  "dateAcquired": "2022-02-15",
                  "gender": "MALE",
                  "currentStatus": "IN_SERVICE",
                  "leavingReason": "TRANSFERRED",
                  "leavingDate": null,
                  "kennellingCharacteristic": "Strong, obedient, and alert. Excellent tracking ability."
                }
                """;
    }

    public static String getDogDTO2() {
        return """
                {
                  "name": "Rex",
                  "breed": "German Shepherd",
                  "supplierId": 5,
                  "badgeId": "K9-001",
                  "birthDate": "2021-05-20",
                  "dateAcquired": "2022-02-15",
                  "gender": "MALE",
                  "currentStatus": "IN_SERVICE",
                  "leavingReason": "TRANSFERRED",
                  "leavingDate": null,
                  "kennellingCharacteristic": "Strong, obedient, and alert. Excellent tracking ability."
                }
                """;
    }

    public static String getDogDTO3() {
        return """
                {
                  "name": "Rex",
                  "breed": "German Shepherd",
                  "supplierId": 1,
                  "badgeId": "K9-001",
                  "birthDate": "2021-05-20",
                  "dateAcquired": "2022-02-15",
                  "gender": "MALE",
                  "currentStatus": "IN_SERVICE",
                  "leavingReason": "TRANSFERRED",
                  "leavingDate": "2028-10-15",
                  "kennellingCharacteristic": "Strong, obedient, and alert. Excellent tracking ability."
                }
                """;
    }

    public static String getDogDTO4() {
        return """
                {
                  "name": "Rex",
                  "breed": "German Shepherd",
                  "supplierId": 2,
                  "badgeId": "K9-100",
                  "birthDate": "2021-05-20",
                  "dateAcquired": "2022-02-15",
                  "gender": null,
                  "currentStatus": "IN_SERVICE",
                  "leavingReason": "TRANSFERRED",
                  "leavingDate": "2028-05-20",
                  "kennellingCharacteristic": "Strong, obedient, and alert. Excellent tracking ability."
                }
                """;
    }

    public static String getDogDTO5() {
        return """
                {
                  "name": "Rex",
                  "breed": "German Shepherd",
                  "supplierId":7,
                  "badgeId": "K9-100",
                  "birthDate": "2021-05-20",
                  "dateAcquired": "2022-02-15",
                  "gender": null,
                  "currentStatus": "IN_SERVICE",
                  "leavingReason": "TRANSFERRED",
                  "leavingDate": "2028-05-20",
                  "kennellingCharacteristic": "Strong, obedient, and alert. Excellent tracking ability."
                }
                """;
    }

    public static MultiValueMap<String, String> searchDogRecordsWithPagination() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("pageNum", "0");
        map.add("pageSize", "20");
        return map;
    }

    public static MultiValueMap<String, String> searchDogRecordsWithName() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("pageNum", "0");
        map.add("pageSize", "10");
        map.add("name", "Bailey");
        return map;
    }

    public static MultiValueMap<String, String> searchDogRecordsWithBreed() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("pageNum", "2");
        map.add("pageSize", "5");
        map.add("breed", "German Shepherd");
        return map;
    }

    public static MultiValueMap<String, String> searchDogRecordsWithSupplier() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("pageNum", "2");
        map.add("pageSize", "8");
        map.add("supplier", "kennels");
        return map;
    }

    public static MultiValueMap<String, String> searchActiveDogRecords() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("pageNum", "0");
        map.add("pageSize", "50");
        return map;
    }

    public static MultiValueMap<String, String> searchDogRecordsWithMultipleParams() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("pageNum", "0");
        map.add("pageSize", "30");
        map.add("breed", "German Shepherd");
        map.add("supplier", "kennels");
        return map;
    }

    public static DogDTO getDogDTOObject() {
        DogDTO dogDTO = new DogDTO();
        dogDTO.setName("Rex");
        dogDTO.setBreed("German Shepherd");
        return dogDTO;
    }

    public static Dog getDogObject() {
        Dog dog = new Dog();
        dog.setName("Rex");
        dog.setBreed("German Shepherd");
        return dog;
    }

    public static Supplier getSupplierObject() {
        Supplier supplier = new Supplier();
        supplier.setId(1);
        supplier.setSupplierName("Breeder");
        return supplier;
    }

    public static DogDTO getDogDTOForMapper1() {
        return DogDTO.builder()
                .name("Rex")
                .breed("German Shepherd")
                .badgeId("K9-001")
                .birthDate(LocalDate.of(2021, 05, 20))
                .dateAcquired(LocalDate.of(2022, 02, 15))
                .gender(Gender.MALE)
                .currentStatus(DogStatus.IN_TRAINING)
                .leavingReason(LeavingReason.RETIRED_RE_HOUSED)
                .leavingDate(LocalDate.of(2030, 10, 15))
                .kennellingCharacteristic("Strong, obedient, and alert. Excellent tracking ability.")
                .build();
    }

    public static Dog getDogForMapper1() {
        return Dog.builder()
                .id(1L)
                .name("Rex")
                .breed("German Shepherd")
                .badgeId("K9-001")
                .birthDate(LocalDate.of(2021, 5, 20))
                .dateAcquired(LocalDate.of(2022, 2, 15))
                .gender(Gender.MALE)
                .currentStatus(DogStatus.IN_TRAINING)
                .leavingReason(LeavingReason.RETIRED_RE_HOUSED)
                .leavingDate(LocalDate.of(2030, 10, 15))
                .kennellingCharacteristic("Strong, obedient, and alert. Excellent tracking ability.")
                .deleted(true)
                .supplier(getSupplierObject())
                .build();
    }

    public static Dog getDogForMapper2() {
        return Dog.builder()
                .id(2L)
                .name("Bella")
                .breed("Labrador Retriever")
                .badgeId("K9-002")
                .birthDate(LocalDate.of(2012, 8, 13))
                .dateAcquired(LocalDate.of(2012, 10, 15))
                .gender(Gender.FEMALE)
                .currentStatus(DogStatus.RETIRED)
                .leavingReason(LeavingReason.RETIRED_RE_HOUSED)
                .leavingDate(LocalDate.of(2025, 2, 17))
                .kennellingCharacteristic("Strong, obedient, and alert. Excellent tracking ability.")
                .deleted(false)
                .supplier(getSupplierObject())
                .build();
    }

    public static Dog getDogForMapper3() {
        return Dog.builder()
                .id(3L)
                .name("Max")
                .breed("Belgian Malinois")
                .badgeId("K9-003")
                .birthDate(LocalDate.of(2016, 4, 21))
                .dateAcquired(LocalDate.of(2016, 4, 21))
                .gender(Gender.FEMALE)
                .currentStatus(DogStatus.LEFT)
                .leavingReason(LeavingReason.DIED)
                .leavingDate(LocalDate.of(2020, 10, 8))
                .kennellingCharacteristic("Strong, obedient, and alert. Excellent tracking ability.")
                .deleted(true)
                .supplier(getSupplierObject())
                .build();
    }
}

