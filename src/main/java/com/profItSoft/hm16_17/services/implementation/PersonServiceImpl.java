package com.profItSoft.hm16_17.services.implementation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.profItSoft.hm16_17.data.PersonData;
import com.profItSoft.hm16_17.dto.PepStatistic;
import com.profItSoft.hm16_17.dto.PersonDetailsDto;
import com.profItSoft.hm16_17.dto.PersonSearchDto;
import com.profItSoft.hm16_17.exceptions.NotZipArchiveException;
import com.profItSoft.hm16_17.repository.implementations.PersonRepositoryImpl;
import com.profItSoft.hm16_17.services.interfaces.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonServiceImpl implements PersonService {

    private final PersonRepositoryImpl repository;

    @Override
    public void upload(MultipartFile file) {
        if (!file.getOriginalFilename().endsWith("zip")) {
            throw new NotZipArchiveException("Uploaded file must be zip archive!");
        }

        repository.dropCollection();

        try (var zipInputStream = new ZipInputStream(file.getInputStream());
             var bufferedInputStream = new BufferedInputStream(zipInputStream)) {

            parseZipEntries(zipInputStream, bufferedInputStream);

        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

    }

    private void parseZipEntries(ZipInputStream zipInputStream, BufferedInputStream bufferedInputStream) throws IOException {
        ObjectReader objectReader = getConfiguredReader();
        ZipEntry entry = zipInputStream.getNextEntry();

        while (entry != null) {
            if (entry.isDirectory() || !entry.getName().endsWith(".json")) {
                continue;
            }

            List<Document> documents = objectReader.readValue(bufferedInputStream);
            repository.saveAll(documents);

            entry = zipInputStream.getNextEntry();
        }
    }

    private ObjectReader getConfiguredReader() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

        return objectMapper.readerForListOf(Document.class);
    }

    @Override
    public Page<PersonDetailsDto> search(PersonSearchDto searchQuery) {
        Page<PersonData> personsPage = repository.search(searchQuery);
        return personsPage.map(this::mapPersonDataToDto);
    }

    private PersonDetailsDto mapPersonDataToDto(PersonData person) {
        return PersonDetailsDto.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .patronymic(person.getPatronymic())
                .fullName(person.getFullName())
                .dateOfBirth(person.getFullName())
                .typeOfOfficial(person.getTypeOfOfficial())
                .died(person.isDied())
                .isPep(person.isPep())
                .build();
    }

    @Override
    public List<PepStatistic> getStatistic() {
        return repository.getStatistic();
    }
}
