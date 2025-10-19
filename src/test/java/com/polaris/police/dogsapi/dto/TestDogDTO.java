package com.polaris.police.dogsapi.dto;

import com.polaris.police.dogsapi.model.enums.DogStatus;
import com.polaris.police.dogsapi.model.enums.Gender;
import com.polaris.police.dogsapi.model.enums.LeavingReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDogDTO {

    private Long id;
    private String name;
    private String breed;
    private Integer supplierId;
    private String supplierName;
    private String badgeId;
    private LocalDate birthDate;
    private LocalDate dateAcquired;
    private Gender gender;
    private DogStatus currentStatus;
    private LeavingReason leavingReason;
    private LocalDate leavingDate;
    private String kennellingCharacteristic;
}
