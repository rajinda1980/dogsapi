package com.polaris.police.dogsapi.service;

import com.polaris.police.dogsapi.model.request.DogDTO;
import com.polaris.police.dogsapi.model.request.SearchParam;
import java.util.List;

public interface DogService {
    DogDTO createDog(DogDTO dto);
    void deleteDog(Long id);
    DogDTO updateDog(Long id, DogDTO dto);
    DogDTO getDog(Long id);
    List<DogDTO> getDogList(SearchParam searchParam);
}
