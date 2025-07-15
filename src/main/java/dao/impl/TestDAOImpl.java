package dao.impl;

import dao.TestDAO;
import entity.Question;
import entity.Test;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class TestDAOImpl extends GenericBaseDAOImpl<Test, UUID> implements TestDAO {
    public TestDAOImpl() {
        super(Test.class);
    }


    @Override
    public boolean existByTitle(String title) {
        log.debug("Checking for existence of Test with title: '{}'", title);
        String hql = "SELECT count(t) FROM Test t WHERE t.title = :title";
        Long count = executeWithResult(session ->
                        session.createQuery(hql, Long.class)
                                .setParameter("title", title)
                                .getSingleResult(),
                "Failed to check for existence of test with title: " + title
        );
        return count > 0;
    }

    public Optional<Test> findByIdWithDetails(UUID testId) {
        return executeWithResult(session -> {
            String hql = "SELECT t FROM Test t " +
                    "LEFT JOIN FETCH t.creator " +
                    "LEFT JOIN FETCH t.questions " +
                    "WHERE t.id = :testId";

            Test test = session.createQuery(hql, Test.class)
                    .setParameter("testId", testId)
                    .uniqueResult();

            if (test != null && test.getQuestions() != null) {
                for (Question q : test.getQuestions()) {
                    Hibernate.initialize(q.getAnswers());
                }
                test.getQuestions().sort(Comparator.comparing(Question::getQuestionNumber));
            }

            return Optional.ofNullable(test);
        }, "Failed to find test with details for id: " + testId);
    }

}
