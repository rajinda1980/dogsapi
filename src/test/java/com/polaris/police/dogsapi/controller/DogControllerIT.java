package com.polaris.police.dogsapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.polaris.police.dogsapi.TestUtils;
import com.polaris.police.dogsapi.dto.TestDogDTO;
import com.polaris.police.dogsapi.model.enums.DogStatus;
import com.polaris.police.dogsapi.model.enums.Gender;
import com.polaris.police.dogsapi.model.enums.LeavingReason;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the {@link DogController}.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class DogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Happy path - Save Dog record
     */
    @Test
    @DisplayName("Integration test - Dog object should be saved")
    @Sql(scripts = {"/db/clean_table.sql", "/db/add_suppliers.sql"})
    void shouldSaveDogRecord() throws Exception {
        String dogDto = TestUtils.getDogDTO1();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/dogs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dogDto))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Rex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breed").value("German Shepherd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supplierId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supplierName").value("breeder"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.badgeId").value("K9-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("2021-05-20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateAcquired").value("2022-02-15"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value("MALE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentStatus").value("IN_SERVICE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leavingReason").value("TRANSFERRED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leavingDate").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.kennellingCharacteristic")
                        .value("Strong, obedient, and alert. Excellent tracking ability."));
    }

    /**
     * Happy path - Update Dog record
     */
    @Test
    @DisplayName("Integration test - Dog object should be updated")
    @Sql(scripts = {"/db/clean_table.sql", "/db/add_suppliers.sql", "/db/add_dog.sql"})
    void shouldUpdateDogRecord() throws Exception {
        String dogDto = TestUtils.getDogDTO4();

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/dogs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dogDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Rex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breed").value("German Shepherd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supplierId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supplierName").value("kennels"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.badgeId").value("K9-100"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("2021-05-20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateAcquired").value("2022-02-15"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentStatus").value("IN_SERVICE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leavingReason").value("TRANSFERRED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leavingDate").value("2028-05-20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.kennellingCharacteristic")
                        .value("Strong, obedient, and alert. Excellent tracking ability."));
    }

    /**
     * Happy path - Delete Dog record
     */
    @Test
    @DisplayName("Integration test - Dog object should be deleted (soft delete)")
    @Sql(scripts = {"/db/clean_table.sql", "/db/add_suppliers.sql", "/db/add_dog.sql"})
    void shouldDeleteDogRecord() throws Exception {

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/api/dogs/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/dogs/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        String sql = "SELECT deleted FROM dogs WHERE id = ?";
        Boolean deleted = jdbcTemplate.queryForObject(sql, Boolean.class, 1);
        assertEquals(Boolean.TRUE, deleted);
    }

    /**
     * Happy path - Parameter Search Dog record (with default parameter values)
     */
    @Test
    @DisplayName("Integration test - List of Dog object should be returned for default parameters")
    @Sql(scripts = {"/db/clean_table.sql", "/db/add_suppliers.sql", "/db/add_dog_for_search.sql"})
    void shouldSearchDogRecord_withDefaultParameters() throws Exception {

        MvcResult result = mockMvc.perform(
                                MockMvcRequestBuilders.get("/api/dogs")
                                        .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

        String jsonResponse =  result.getResponse().getContentAsString();
        List<TestDogDTO> list = objectMapper.readValue(jsonResponse, new TypeReference<List<TestDogDTO>>() {});
        assertNotNull(list);
        assertEquals(10, list.size());

        long count = list.stream().filter(d -> d.getBreed().equals("German Shepherd")).count();
        assertEquals(3, count);

        long genderCount =  list.stream().filter(d -> d.getGender().equals(Gender.MALE)).count();
        assertEquals(5, genderCount);
    }

    static Stream<Arguments> searchArguments() {
        return Stream.of(
                Arguments.of(TestUtils.searchDogRecordsWithPagination(), 20, 5, 9, 10, 8, 8),
                Arguments.of(TestUtils.searchDogRecordsWithName(), 1, 1, 0, 1, 0, 0),
                Arguments.of(TestUtils.searchDogRecordsWithBreed(), 2, 0, 1, 1, 1, 1),
                Arguments.of(TestUtils.searchDogRecordsWithSupplier(), 4, 0, 0, 4, 1, 1),
                Arguments.of(TestUtils.searchActiveDogRecords(), 37, 7, 17, 18, 12, 12),
                Arguments.of(TestUtils.searchDogRecordsWithMultipleParams(), 5, 0, 2, 3, 1, 1)
        );

    }
    /**
     * Happy path - Parameter Search Dog record (with custom parameter values)
     */
    @ParameterizedTest
    @MethodSource("searchArguments")
    @DisplayName("Integration test - List of Dog object should be returned for custom parameters")
    @Sql(scripts = {"/db/clean_table.sql", "/db/add_suppliers.sql", "/db/add_dog_for_search.sql"})
    void shouldSearchDogRecord(MultiValueMap<String, String> map,
                               long expectedCount,
                               long breedCount,
                               long maleCount,
                               long femaleCount,
                               long statusCount,
                               long reasonCount) throws Exception {

        MvcResult result = mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/dogs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .params(map))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(expectedCount))
                .andReturn();

        String jsonResponse =  result.getResponse().getContentAsString();
        List<TestDogDTO> list = objectMapper.readValue(jsonResponse, new TypeReference<List<TestDogDTO>>() {});
        assertNotNull(list);

        Map<String, Long> categories =
                list.stream().collect(Collectors.collectingAndThen(
                        Collectors.toList(), dogs -> Map.of(
                                "breed", dogs.stream().filter(d -> StringUtils.isNotBlank(d.getBreed()) && d.getBreed().equals("Golden Retriever")).count(),
                                "male", dogs.stream().filter(d -> null != d.getGender() && d.getGender().equals(Gender.MALE)).count(),
                                "female", dogs.stream().filter(d -> null != d.getGender() && d.getGender().equals(Gender.FEMALE)).count(),
                                "status", dogs.stream().filter(d -> null != d.getCurrentStatus() && d.getCurrentStatus().equals(DogStatus.IN_SERVICE)).count(),
                                "reason", dogs.stream().filter(d -> null != d.getLeavingReason() && d.getLeavingReason().equals(LeavingReason.TRANSFERRED)).count()
                        )
                ));
        assertEquals(breedCount, categories.get("breed"));
        assertEquals(maleCount, categories.get("male"));
        assertEquals(femaleCount, categories.get("female"));
        assertEquals(statusCount, categories.get("status"));
        assertEquals(reasonCount, categories.get("reason"));
    }

    /**
     * Negative scenario - Not save dog dto.
     */
    @Test
    @DisplayName("Integration test - Dog object should not be saved")
    @Sql(scripts = {"/db/clean_table.sql", "/db/add_suppliers.sql"})
    void shouldNotSaveDogRecord() throws Exception {
        String dogDto = TestUtils.getDogDTO2();
        mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/dogs")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(dogDto))
            .andExpect(MockMvcResultMatchers.status().isNotFound());

        String sql = "SELECT COUNT(*) FROM dogs";
        long count = jdbcTemplate.queryForObject(sql, Long.class);
        assertEquals(0, count);
    }

    /**
     * Negative scenario - Not update Dog record.
     */
    @Test
    @DisplayName("Integration test - Dog object should be updated")
    @Sql(scripts = {"/db/clean_table.sql", "/db/add_suppliers.sql", "/db/add_dog.sql"})
    void shouldNoUpdateDogRecord() throws Exception {
        String dogDto = TestUtils.getDogDTO5();

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/api/dogs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(dogDto))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/dogs/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Rex"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.breed").value("German Shepherd"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supplierId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.supplierName").value("breeder"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.badgeId").value("K9-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthDate").value("2021-05-20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dateAcquired").value("2022-02-15"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gender").value("MALE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentStatus").value("IN_SERVICE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leavingReason").value("TRANSFERRED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.leavingDate").isEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.kennellingCharacteristic")
                        .value("Strong, obedient, and alert. Excellent tracking ability."));
    }

}
