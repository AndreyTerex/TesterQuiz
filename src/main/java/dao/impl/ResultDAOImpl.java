package dao.impl;

import dao.ResultDAO;
import entity.AnswersInResult;
import entity.Result;
import org.hibernate.Hibernate;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ResultDAOImpl extends GenericBaseDAOImpl<Result, UUID> implements ResultDAO {
    public ResultDAOImpl() {
        super(Result.class);
    }

    public List<Result> getAllResultsByUserId(UUID id) {
        String hql = "FROM Result r WHERE r.user.id = :userId";
        return executeWithResult(session ->
                        session.createQuery(hql, Result.class)
                                .setParameter("userId", id)
                                .list(),
                "Failed to get results for user id: " + id
        );
    }

    public Optional<Result> findByIdWithDetails(UUID resultId) {
        return executeWithResult(session -> {
            String hql = "SELECT r FROM Result r " +
                    "LEFT JOIN FETCH r.user " +
                    "LEFT JOIN FETCH r.test " +
                    "LEFT JOIN FETCH r.answersInResults air " +
                    "WHERE r.id = :resultId";

            Result result = session.createQuery(hql, Result.class)
                    .setParameter("resultId", resultId)
                    .uniqueResult();

            if (result != null && result.getAnswersInResults() != null) {
                for (AnswersInResult air : result.getAnswersInResults()) {
                    Hibernate.initialize(air.getQuestion());
                    Hibernate.initialize(air.getSelectedAnswers());
                }
            }

            return Optional.ofNullable(result);
        }, "Failed to find result with details for id: " + resultId);
    }

    public Long getCount() {
        String hql = "SELECT COUNT(r) FROM Result r";
        return executeWithResult(session ->
                        session.createQuery(hql, Long.class).getSingleResult(),
                "Failed to get result count"
        );
    }
}
