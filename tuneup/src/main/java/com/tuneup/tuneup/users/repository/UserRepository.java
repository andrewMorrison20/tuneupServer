package com.tuneup.tuneup.users.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
public class UserRepository implements JpaRepository<AppUser, Long> {
    @Override
    public void flush() {

    }

    @Override
    public <S extends AppUser> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends AppUser> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<AppUser> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public AppUser getOne(Long aLong) {
        return null;
    }

    @Override
    public AppUser getById(Long aLong) {
        return null;
    }

    @Override
    public AppUser getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends AppUser> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends AppUser> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends AppUser> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends AppUser> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends AppUser> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends AppUser> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends AppUser, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends AppUser> S save(S entity) {
        return null;
    }

    @Override
    public <S extends AppUser> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<AppUser> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<AppUser> findAll() {
        return List.of();
    }

    @Override
    public List<AppUser> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(AppUser entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends AppUser> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<AppUser> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<AppUser> findAll(Pageable pageable) {
        return null;
    }
}
