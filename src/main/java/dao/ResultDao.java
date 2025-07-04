package dao;

import entity.Result;
import exceptions.DataAccessException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResultDao {
    void save(Result result) throws DataAccessException;

    List<Result> getAllResultsByUserId(UUID id);

    Optional<Result> findById(UUID resultId);

    List<Result> getAllResultsByTestId(UUID testId);

    Integer getCount();
}