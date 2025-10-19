package com.polaris.police.dogsapi.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class SearchParam {
    private String name;
    private String breed;
    private String supplier;
    private int pageNum;
    private int pageSize;
}
