package com.profItSoft.hm16_17.services.interfaces;

import com.profItSoft.hm16_17.dto.PepStatistic;
import com.profItSoft.hm16_17.dto.PersonDetailsDto;
import com.profItSoft.hm16_17.dto.PersonSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PersonService {

    void upload(MultipartFile file);

    Page<PersonDetailsDto> search(PersonSearchDto searchQuery);

    List<PepStatistic> getStatistic();
}
