package services;

import dao.ResultDao;
import dto.ResultDTO;
import dto.TestDTO;
import dto.TestStatsDTO;
import entity.*;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ResultService {
    private final ResultDao resultDao;

    public ResultService(ResultDao resultDao) {
        this.resultDao = resultDao;
    }

    /**
     * Creates a test result object
     */
    public Result buildResultObject(Test test, UUID userid, LocalDateTime startTime) {
        return Result.builder()
                .id(UUID.randomUUID())
                .userId(userid)
                .testId(test.getId())
                .resultAnswers(new ArrayList<>())
                .date(startTime)
                .testTitle(test.getTitle())
                .build();
    }

    /**
     * Saves the result of passing the test
     */
    public void saveResult(ResultDTO resultDTO) {
        try {
            resultDao.save(resultDTO.toEntity());
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save result", e);
        }
    }


    /**
     * Calculates the final score for the test
     */
    public Result calculateScoreResult(Result result) {
        int score = 0;
        List<ResultAnswer> resultAnswers = result.getResultAnswers();

        for (ResultAnswer resultAnswer : resultAnswers) {
            List<Answer> correctAnswers = resultAnswer.getQuestion().getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .toList();

            List<Answer> selectedAnswers = resultAnswer.getSelectedAnswers();

            if (new HashSet<>(selectedAnswers).equals(new HashSet<>(correctAnswers))) {
                score++;
            }
        }

        result.setScore(score);
        return result;
    }

    public List<ResultDTO> getAllResultsByUserId(UUID id) {
        return resultDao.getAllResultsByUserId(id).stream()
                .map(Result::toDTO)
                .sorted(Comparator.comparing(ResultDTO::getDate).reversed())
                .toList();

    }

    public ResultDTO findById(String id) {
        if(id == null || id.isEmpty()){
            throw new ValidationException("Result id is null or empty");
        }
        return resultDao.findById(UUID.fromString(id))
                .map(Result::toDTO)
                .orElseThrow(() -> new ValidationException("Result with id=" + id + " not found"));
    }

    public List<TestStatsDTO> getStats(List<TestDTO> allTestsDTO) {
        List<TestStatsDTO> testStatsDTOList = new ArrayList<>();

        for (TestDTO testDTO : allTestsDTO) {

            List<ResultDTO> testSpecificResults = findAllResultsByTestId(testDTO.getId());

            String testTitle = testDTO.getTitle();
            Integer totalQuestions = testDTO.getQuestions().size();
            Integer totalPassed = testSpecificResults.size();

            Integer maxScore = testSpecificResults.stream()
                    .mapToInt(ResultDTO::getScore)
                    .max()
                    .orElse(0);

            LocalDateTime lastPassed = testSpecificResults.stream()
                    .map(ResultDTO::getDate)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            TestStatsDTO testStatsDTO = TestStatsDTO.builder()
                    .testTitle(testTitle)
                    .totalQuestions(totalQuestions)
                    .totalPassed(totalPassed)
                    .maxScore(maxScore)
                    .lastPassed(lastPassed)
                    .build();

            testStatsDTOList.add(testStatsDTO);
        }
        testStatsDTOList.sort(Comparator.comparing(TestStatsDTO::getTestTitle));
        return testStatsDTOList;
    }


    private List<ResultDTO> findAllResultsByTestId(UUID testId) {
        return resultDao.getAllResultsByTestId(testId).stream()
                .map(Result::toDTO)
                .toList();
    }

    public Integer countAttempts() {
        return resultDao.getCount();
    }
}
