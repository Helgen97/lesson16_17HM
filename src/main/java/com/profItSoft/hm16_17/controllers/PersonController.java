package com.profItSoft.hm16_17.controllers;

import com.profItSoft.hm16_17.dto.PepStatistic;
import com.profItSoft.hm16_17.dto.PersonDetailsDto;
import com.profItSoft.hm16_17.dto.PersonSearchDto;
import com.profItSoft.hm16_17.services.implementation.PersonServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/pep")
@RequiredArgsConstructor
public class PersonController {

    private final PersonServiceImpl personService;

    @PostMapping("/upload")
    public void upload(@RequestParam("file") MultipartFile uploadedFile) {
        personService.upload(uploadedFile);
    }

    @PostMapping("/_search")
    public Page<PersonDetailsDto> search(@RequestBody PersonSearchDto searchQuery) {
        return personService.search(searchQuery);
    }

    @GetMapping("/stats")
    public List<PepStatistic> statistic() {
        return personService.getStatistic();
    }
}

