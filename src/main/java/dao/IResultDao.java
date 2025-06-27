package dao;

import entity.Result;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IResultDao {
    void save(Result result);

    List<Result> getAllResultsByUserId(UUID id);

    Optional<Result> findById(UUID resultId);

    List<Result> getAllResultsByTestId(UUID testId);

    Integer getCount();
}