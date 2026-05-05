package com.task.gymmanagement.domain;

import com.task.gymmanagement.domain.dto.response.RevenueReportDto;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

class SimpleInMemoryMemberRepository implements MemberRepository {
    private final Map<Long, Member> db = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public long countActiveMembersByMembershipPlan(final MembershipPlan membershipPlan) {
        return db.values().stream()
                .filter(m -> m.getMembershipPlan().equals(membershipPlan) && m.getStatus().equals(MemberStatus.ACTIVE))
                .count();
    }

    @Override
    public List<RevenueReportDto> calculateRevenueReport() {
        return db.values().stream()
                .filter(m -> m.getStatus().equals(MemberStatus.ACTIVE))
                .collect(Collectors.groupingBy(
                        members -> new GroupKey(
                                members.getMembershipPlan().getGym().getName(),
                                members.getMembershipPlan().getCurrency()
                        ),
                        Collectors.mapping(
                                m -> m.getMembershipPlan().getAmount(),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ))
                .entrySet().stream()
                .map(entry -> RevenueReportDto.builder()
                        .gymName(entry.getKey().gymName())
                        .amount(entry.getValue())
                        .currency(entry.getKey().currencyCode())
                        .build())
                .toList();
    }

    private record GroupKey(String gymName, String currencyCode) {
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Member> S saveAndFlush(final S entity) {
        return null;
    }

    @Override
    public <S extends Member> List<S> saveAllAndFlush(final Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(final Iterable<Member> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(final Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Member getOne(final Long aLong) {
        return null;
    }

    @Override
    public Member getById(final Long aLong) {
        return null;
    }

    @Override
    public Member getReferenceById(final Long aLong) {
        return null;
    }

    @Override
    public <S extends Member> Optional<S> findOne(final Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Member> List<S> findAll(final Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Member> List<S> findAll(final Example<S> example, final Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Member> Page<S> findAll(final Example<S> example, final Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Member> long count(final Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Member> boolean exists(final Example<S> example) {
        return false;
    }

    @Override
    public <S extends Member, R> R findBy(final Example<S> example, final Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Member> S save(final S entity) {
        Long id = idGenerator.getAndIncrement();
        if (entity.getId() == null) {
            entity.setId(id);
            entity.setStatus(MemberStatus.ACTIVE);
            entity.setStartDate(Instant.now());
        }
        db.put(id, entity);
        return entity;
    }

    @Override
    public <S extends Member> List<S> saveAll(final Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Member> findById(final Long aLong) {
        return Optional.ofNullable(db.get(aLong));
    }

    @Override
    public boolean existsById(final Long aLong) {
        return false;
    }

    @Override
    public List<Member> findAll() {
        return db.values().stream().toList();
    }

    @Override
    public List<Member> findAllById(final Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(final Long aLong) {

    }

    @Override
    public void delete(final Member entity) {

    }

    @Override
    public void deleteAllById(final Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(final Iterable<? extends Member> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Member> findAll(final Sort sort) {
        return List.of();
    }

    @Override
    public Page<Member> findAll(final Pageable pageable) {
        return null;
    }
}
