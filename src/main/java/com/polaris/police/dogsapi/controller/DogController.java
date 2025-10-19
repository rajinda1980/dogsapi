package com.polaris.police.dogsapi.controller;

import com.polaris.police.dogsapi.model.response.MessageDTO;
import com.polaris.police.dogsapi.model.request.DogDTO;
import com.polaris.police.dogsapi.model.request.SearchParam;
import com.polaris.police.dogsapi.service.DogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/dogs")
@Tag(name = "Dogs API", description = "Endpoints for managing police dog records")
public class DogController {

    private final DogService dogService;

    public DogController(DogService dogService) {
        this.dogService = dogService;
    }

    @Operation(summary = "Register a new dog")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Dog successfully registered",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DogDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))
            )
    })
    @PostMapping
    public ResponseEntity<DogDTO> create(@Valid @RequestBody DogDTO dogDTO) {
        DogDTO saved = dogService.createDog(dogDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @Operation(summary = "Delete dog record by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Dog deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Dog not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dogService.deleteDog(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(summary = "Update existing dog record")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dog record updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DogDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dog not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<DogDTO> updateDog(@PathVariable Long id, @Valid @RequestBody DogDTO dogDTO) {
        DogDTO updatedDog = dogService.updateDog(id, dogDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDog);
    }


    @Operation(summary = "Get dog record by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Dog record retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DogDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Dog not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<DogDTO> getDog(@PathVariable Long id) {
        DogDTO updatedDog = dogService.getDog(id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedDog);
    }


    @Operation(summary = "Get list of dogs matching search criteria")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of dogs retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DogDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageDTO.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<DogDTO>> getDogList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "breed", required = false) String breed,
            @RequestParam(value = "supplier", required = false) String supplier,
            @RequestParam(value = "pageNum", defaultValue = "0") @Min(0) int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") @Min(1) @Max(100) int pageSize
    ) {
        SearchParam searchParam = new SearchParam(name, breed, supplier, pageNum, pageSize);
        List<DogDTO> dogDTOList = dogService.getDogList(searchParam);
        return ResponseEntity.status(HttpStatus.OK).body(dogDTOList);
    }
}
