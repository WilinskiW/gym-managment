package com.task.gymmanagement.domain;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

class SimpleInMemoryGymRepository implements GymRepository {
    private final Map<Long, Gym> db = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void flush() {

    }

    @Override
    public <S extends Gym> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Gym> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Gym> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Gym getOne(Long aLong) {
        return null;
    }

    @Override
    public Gym getById(Long aLong) {
        return null;
    }

    @Override
    public Gym getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Gym> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Gym> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Gym> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<Gym> findAll() {
        return db.values().stream().toList();
    }

    @Override
    public List<Gym> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public <S extends Gym> S save(S entity) {
        Long id = idGenerator.getAndIncrement();
        if (entity.getId() == null) {
            entity.setId(id);
        }
        db.put(id, entity);
        return entity;
    }

    @Override
    public boolean existsByName(String name) {
        return db.values().stream()
                .anyMatch(g -> g.getName().equals(name));
    }

    @Override
    public Optional<Gym> findByName(String name) {
        return  db.values().stream()
                .filter(g -> g.getName().equals(name))
                .findFirst();
    }

    @Override
    public Optional<Gym> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Gym entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Gym> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Gym> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Gym> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Gym> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Gym> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Gym> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Gym> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Gym, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }


}
