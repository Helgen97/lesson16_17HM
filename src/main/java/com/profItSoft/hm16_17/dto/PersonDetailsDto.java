package com.profItSoft.hm16_17.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PersonDetailsDto {

    private String id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String fullName;
    private String dateOfBirth;
    private String typeOfOfficial;
    private boolean died;
    private boolean isPep;
}
