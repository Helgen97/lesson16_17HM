package com.profItSoft.hm16_17.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.profItSoft.hm16_17.Hm1617Application;
import com.profItSoft.hm16_17.data.PersonData;
import com.profItSoft.hm16_17.dto.PersonSearchDto;
import com.profItSoft.hm16_17.exceptions.NotZipArchiveException;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Hm1617Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void beforeEach() {
        mongoTemplate.dropCollection(PersonData.class);
        mongoTemplate.createCollection(PersonData.class);
        mongoTemplate.insert(generateDocuments(), PersonData.class);
    }

    private List<Document> generateDocuments() {
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 100; i++) {

            documents.add(createDocument(
                    "first" + i / 10,
                    "last" + i / 10,
                    "patronymic" + 1 / 10,
                    i % 2 == 0
            ));
        }
        return documents;
    }

    private Document createDocument(String firstName, String lastName, String patronymic, boolean isPep) {
        Document document = new Document();

        document.put("first_name", firstName);
        document.put("last_name", lastName);
        document.put("patronymic", patronymic);
        document.put("full_name", "%s %s %s".formatted(firstName, lastName, patronymic));
        document.put("city_of_birth_uk", "Kyiv");
        document.put("date_of_birth", "Today");
        document.put("type_of_official", "Person");
        document.put("died", false);
        document.put("is_pep", isPep);

        return document;
    }

    @Test
    void upload__whenUploadZipFile_thenStatusIsOkAndDocumentsCountIsEquals() throws Exception {
        long DOCUMENTS_IN_DB = 45875L;
        MockMultipartFile file = new MockMultipartFile("file", "pep.zip", "application/zip", new ClassPathResource("/testFiles/pep.zip").getInputStream());

        mvc.perform(multipart("/api/pep/upload").file(file))
                .andExpect(status().isOk());

        String personCollectionName = mongoTemplate.getCollectionName(PersonData.class);
        long documentsCount = mongoTemplate.getCollection(personCollectionName).countDocuments();
        assertThat(documentsCount).isEqualTo(DOCUMENTS_IN_DB);
    }

    @Test
    void upload__whenUploadNotZipFile_thenStatusIsBadRequest() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "NeverGonnaGiveYouUp.png", "image/png", new ClassPathResource("/testFiles/NeverGonnaGiveYouUp.png").getInputStream());

        mvc.perform(multipart("/api/pep/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> mvcResult.getResolvedException().getClass().equals(NotZipArchiveException.class));

    }

    @Test
    void search_whenPostQuery_thenStatusIsOkAndReceivePageOfPersons() throws Exception {
        PersonSearchDto searchDto = PersonSearchDto.builder()
                .firstName("first1")
                .page(0)
                .size(5)
                .build();

        mvc.perform(post("/api/pep/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(searchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isNotEmpty())
                .andExpect(jsonPath("$.content[0].firstName").value("first1"))
                .andExpect(jsonPath("$.content[1].firstName").value("first1"))
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(5));
    }

    @Test
    void statistic_whenGet_statusIsOkAndGetStatisticArray() throws Exception {
        mvc.perform(get("/api/pep/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].firstName").isString())
                .andExpect(jsonPath("$[0].peopleWithName").isNumber());
    }
}