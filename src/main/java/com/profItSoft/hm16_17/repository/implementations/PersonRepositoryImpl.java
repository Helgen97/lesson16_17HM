package com.profItSoft.hm16_17.repository.implementations;

import com.profItSoft.hm16_17.data.PersonData;
import com.profItSoft.hm16_17.dto.PepStatistic;
import com.profItSoft.hm16_17.dto.PersonSearchDto;
import com.profItSoft.hm16_17.repository.interfaces.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@RequiredArgsConstructor
public class PersonRepositoryImpl implements PersonRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public void dropCollection() {
        mongoTemplate.dropCollection(PersonData.class);
    }

    @Override
    public void saveAll(List<Document> documents) {
        mongoTemplate.insert(documents, PersonData.class);
    }

    @Override
    public Page<PersonData> search(PersonSearchDto searchQuery) {
        PageRequest pageRequest = getPageRequestByParameters(searchQuery.getPage(), searchQuery.getSize());
        Query query = createQueryFromDto(searchQuery).with(pageRequest);

        List<PersonData> personList = mongoTemplate.find(query, PersonData.class);

        return PageableExecutionUtils.getPage(
                personList,
                pageRequest,
                () -> mongoTemplate.count((Query.of(query).limit(-1).skip(-1)), PersonData.class)
        );
    }

    private PageRequest getPageRequestByParameters(int page, int size) {
        int DEFAULT_PAGE_SIZE = 5;

        return PageRequest.of(
                page,
                size == 0 ? DEFAULT_PAGE_SIZE : size,
                Sort.by(Sort.Direction.DESC, PersonData.Fields.firstName)
        );
    }

    private Query createQueryFromDto(PersonSearchDto searchQuery) {
        Query query = new Query();

        if (searchQuery.getFirstName() != null) {
            query.addCriteria(where(PersonData.Fields.firstName).is(searchQuery.getFirstName()));
        }
        if (searchQuery.getLastName() != null) {
            query.addCriteria(where(PersonData.Fields.lastName).is(searchQuery.getLastName()));
        }
        if (searchQuery.getPatronymic() != null) {
            query.addCriteria(where(PersonData.Fields.patronymic).is(searchQuery.getPatronymic()));
        }

        return query;
    }

    @Override
    public List<PepStatistic> getStatistic() {
        GroupOperation groupByFirstNameAndSumByPeopleWithThisName = group(PersonData.Fields.firstName).count().as(PepStatistic.Fields.peopleWithName);
        MatchOperation matchIsPep = match(new Criteria(PersonData.Fields.isPep).is(true));
        SortOperation sortPeopleCountDesc = sort(Sort.by(Sort.Direction.DESC, PepStatistic.Fields.peopleWithName));
        LimitOperation limitResult = limit(10);
        ProjectionOperation projectionOperation = project(PepStatistic.Fields.peopleWithName).and(PersonData.Fields.firstName).previousOperation();

        Aggregation aggregation = newAggregation(
                matchIsPep,
                groupByFirstNameAndSumByPeopleWithThisName,
                projectionOperation,
                sortPeopleCountDesc,
                limitResult);

        AggregationResults<PepStatistic> pepStatistics = mongoTemplate.aggregate(aggregation, PersonData.class, PepStatistic.class);

        return pepStatistics.getMappedResults();
    }
}
