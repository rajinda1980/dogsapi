package com.polaris.police.dogsapi.model.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polaris.police.dogsapi.model.enums.DogStatus;
import com.polaris.police.dogsapi.model.enums.Gender;
import com.polaris.police.dogsapi.model.enums.LeavingReason;
import com.polaris.police.dogsapi.model.request.validator.ValidEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DogDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Size(max = 200, message = "{invalid.field.length}")
    private String name;

    @Size(max = 200, message = "{invalid.field.length}")
    private String breed;

    @NotNull(message = "{field.required}")
    private Integer supplierId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String supplierName;

    @Size(max = 200, message = "{invalid.field.length}")
    private String badgeId;

    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateAcquired;

    private Gender gender;

    @ValidEnum(enumClass = DogStatus.class, fieldName = "currentStatus")
    private DogStatus currentStatus;

    @ValidEnum(enumClass = LeavingReason.class, fieldName = "leavingReason")
    private LeavingReason leavingReason;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate leavingDate;

    @Size(max = 500, message = "{invalid.field.length}")
    private String kennellingCharacteristic;
}
