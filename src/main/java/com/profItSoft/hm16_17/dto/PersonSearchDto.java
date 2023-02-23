package com.profItSoft.hm16_17.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PersonSearchDto {

    private String firstName;
    private String lastName;
    private String patronymic;
    private int page;
    private int size;

}
