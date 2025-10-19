package com.polaris.police.dogsapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polaris.police.dogsapi.TestUtils;
import com.polaris.police.dogsapi.model.request.DogDTO;
import com.polaris.police.dogsapi.service.DogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.stream.Stream;

@WebMvcTest(DogController.class)
public class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DogService dogService;

    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> validationData() {
        String message = "must be less than or equal to 200 characters";
        String message500 = "must be less than or equal to 500 characters";
        String messageBirthDay = "Invalid date format for field birthDate. Expected format: yyyy-MM-dd";
        String messageAcquiredDay = "Invalid date format for field dateAcquired. Expected format: yyyy-MM-dd";
        String messageLeavingDate = "Invalid date format for field leavingDate. Expected format: yyyy-MM-dd";
        String messageGender = "Invalid value MAL for field gender. Allowed values are: MALE, FEMALE";
        String messageCurrentStatus = "Invalid value IN_SERV for field currentStatus. Allowed values are: IN_TRAINING, IN_SERVICE, RETIRED, LEFT";
        String messageLeavingReason = "Invalid value TRANSFERR for field leavingReason. Allowed values are: TRANSFERRED, RETIRED_PUT_DOWN, KIA, REJECTED, RETIRED_RE_HOUSED, DIED";
        String messageBDFutureDate = "must be a date in the past or in the present";

        String inputName = TestUtils.getDogDTO3().replace("\"Rex\"", "\"" + "a".repeat(201) + "\"");
        String inputBreed = TestUtils.getDogDTO3().replace("\"German Shepherd\"", "\"" + "a".repeat(201) + "\"");
        String inputBadgeId = TestUtils.getDogDTO3().replace("\"K9-001\"", "\"" + "a".repeat(201) + "\"");
        String inputCharacteristics = TestUtils.getDogDTO3().replace("\"Strong, obedient, and alert. Excellent tracking ability.\"", "\"" + "a".repeat(501) + "\"");
        String inputBirthDay1 = TestUtils.getDogDTO3().replace("\"2021-05-20\"", "\"05-20-2021\"");
        String inputBirthDay2 = TestUtils.getDogDTO3().replace("\"2021-05-20\"", "\"05-2021-05\"");
        String inputBirthDay3 = TestUtils.getDogDTO3().replace("\"2021-05-20\"", "\"2021/05/200\"");
        String inputAcquired1 = TestUtils.getDogDTO3().replace("\"2022-02-15\"", "\"02-20-2022\"");
        String inputAcquired2 = TestUtils.getDogDTO3().replace("\"2022-02-15\"", "\"02-2022-05\"");
        String inputAcquired3 = TestUtils.getDogDTO3().replace("\"2022-02-15\"", "\"2022/02/15\"");
        String inputLeavingDate1 = TestUtils.getDogDTO3().replace("\"2028-10-15\"", "\"10-15-2022\"");
        String inputLeavingDate2 = TestUtils.getDogDTO3().replace("\"2028-10-15\"", "\"10-2022-15\"");
        String inputLeavingDate3 = TestUtils.getDogDTO3().replace("\"2028-10-15\"", "\"2028/10/15\"");
        String inputGender = TestUtils.getDogDTO3().replace("\"MALE\"", "\"MAL\"");
        String inputCurrentStatus = TestUtils.getDogDTO3().replace("\"IN_SERVICE\"", "\"IN_SERV\"");
        String inputLeavingReason = TestUtils.getDogDTO3().replace("\"TRANSFERRED\"", "\"TRANSFERR\"");
        String inputFutureBD = TestUtils.getDogDTO3().replace("\"2021-05-20\"", "\"2099-05-20\"");

        return Stream.of(
                Arguments.of(inputName, "name", message),
                Arguments.of(inputBreed, "breed", message),
                Arguments.of(inputBadgeId, "badgeId", message),
                Arguments.of(inputCharacteristics, "kennellingCharacteristic", message500),
                Arguments.of(inputBirthDay1, "birthDate", messageBirthDay),
                Arguments.of(inputBirthDay2, "birthDate", messageBirthDay),
                Arguments.of(inputBirthDay3, "birthDate", messageBirthDay),
                Arguments.of(inputAcquired1, "dateAcquired", messageAcquiredDay),
                Arguments.of(inputAcquired2, "dateAcquired", messageAcquiredDay),
                Arguments.of(inputAcquired3, "dateAcquired", messageAcquiredDay),
                Arguments.of(inputLeavingDate1, "leavingDate", messageLeavingDate),
                Arguments.of(inputLeavingDate2, "leavingDate", messageLeavingDate),
                Arguments.of(inputLeavingDate3, "leavingDate", messageLeavingDate),
                Arguments.of(inputGender, "gender", messageGender),
                Arguments.of(inputCurrentStatus, "currentStatus", messageCurrentStatus),
                Arguments.of(inputLeavingReason, "leavingReason", messageLeavingReason),
                Arguments.of(inputFutureBD, "birthDate", messageBDFutureDate)
        );
    }

    @ParameterizedTest
    @MethodSource("validationData")
    @DisplayName("DTO fields should be validated")
    void shouldFail_WhenNameTooLong(String input, String field, String message) throws Exception {

        mockMvc.perform(post("/api/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(input))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value(field))
                .andExpect(jsonPath("$.fieldErrors[0].message").value(message));
    }

    @Test
    @DisplayName("Shuld return success message for valid input")
    void shouldSuccess_WhenValidInput() throws Exception {
        String validInput = TestUtils.getDogDTO3();
        DogDTO requestDto = objectMapper.readValue(validInput, DogDTO.class);

        DogDTO responseDto =
                DogDTO.builder()
                        .id(1L)
                        .name(requestDto.getName())
                        .breed(requestDto.getBreed())
                        .supplierId(requestDto.getSupplierId())
                        .supplierName("breeder")
                        .badgeId(requestDto.getBadgeId())
                        .birthDate(requestDto.getBirthDate())
                        .dateAcquired(requestDto.getDateAcquired())
                        .gender(requestDto.getGender())
                        .currentStatus(requestDto.getCurrentStatus())
                        .leavingReason(requestDto.getLeavingReason())
                        .leavingDate(requestDto.getLeavingDate())
                        .kennellingCharacteristic(requestDto.getKennellingCharacteristic())
                        .build();

        when(dogService.createDog(any(DogDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validInput))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Rex"))
                .andExpect(jsonPath("$.breed").value("German Shepherd"))
                .andExpect(jsonPath("$.supplierId").value(1))
                .andExpect(jsonPath("$.supplierName").value("breeder"))
                .andExpect(jsonPath("$.badgeId").value("K9-001"))
                .andExpect(jsonPath("$.birthDate").value("2021-05-20"))
                .andExpect(jsonPath("$.dateAcquired").value("2022-02-15"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.currentStatus").value("IN_SERVICE"))
                .andExpect(jsonPath("$.leavingReason").value("TRANSFERRED"))
                .andExpect(jsonPath("$.leavingDate").value("2028-10-15"))
                .andExpect(jsonPath("$.kennellingCharacteristic").value("Strong, obedient, and alert. Excellent tracking ability."));
    }
}
