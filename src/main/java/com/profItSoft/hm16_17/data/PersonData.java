package com.profItSoft.hm16_17.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@Document("persons")
@CompoundIndexes({
        @CompoundIndex(name = "fullNameIndex", def = "{'first_name': 1, 'last_name': 1, 'patronymic': 1}"),
        @CompoundIndex(name = "firstAndLastNameIndex", def = "{'first_name': 1, 'last_name': 1}"),
        @CompoundIndex(name = "firstNameAndPatronymicIndex", def = "{'first_name': 1, 'patronymic': 1}"),
        @CompoundIndex(name = "lastNameAndPatronymicIndex", def = "{'last_name': 1, 'patronymic': 1}")
})
public class PersonData {

    @MongoId
    private String id;

    @JsonProperty("first_name")
    @Field("first_name")
    @Indexed
    private String firstName;

    @JsonProperty("last_name")
    @Field("last_name")
    @Indexed
    private String lastName;

    @JsonProperty("patronymic")
    @Field("patronymic")
    @Indexed
    private String patronymic;

    @JsonProperty("full_name")
    @Field("full_name")
    private String fullName;

    @JsonProperty("date_of_birth")
    @Field("date_of_birth")
    private String dateOfBirth;

    @JsonProperty("city_of_birth")
    @Field("city_of_birth")
    private String cityOfBirthUk;

    @JsonProperty("type_of_official")
    @Field("type_of_official")
    private String typeOfOfficial;

    private boolean died;

    @JsonProperty("is_pep")
    @Field("is_pep")
    private boolean isPep;

}
