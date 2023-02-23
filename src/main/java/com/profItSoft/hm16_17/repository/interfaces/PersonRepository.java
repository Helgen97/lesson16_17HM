package com.profItSoft.hm16_17.repository.interfaces;

import com.profItSoft.hm16_17.data.PersonData;
import com.profItSoft.hm16_17.dto.PepStatistic;
import com.profItSoft.hm16_17.dto.PersonSearchDto;
import org.bson.Document;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PersonRepository {

    void dropCollection();

    void saveAll(List<Document> documents);

    Page<PersonData> search(PersonSearchDto searchQuery);

    List<PepStatistic> getStatistic();
}
