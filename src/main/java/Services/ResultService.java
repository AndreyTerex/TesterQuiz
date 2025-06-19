package Services;

import dao.ResultDao;
import dto.ResultDTO;
import dto.TestDTO;
import dto.TestStatsDTO;
import entity.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ResultService {
    private final ResultDao resultDao;

    public ResultService(ResultDao resultDao) {
        this.resultDao = resultDao;
    }

    /**
     * Создает объект результата теста
     */
    public Result buildResultObject(Test test, UUID userid, LocalDateTime startTime) {
        return Result.builder()
                .id(UUID.randomUUID())
                .user_id(userid)
                .test_id(test.getId())
                .resultAnswers(new ArrayList<ResultAnswer>())
                .date(startTime)
                .testTitle(test.getTitle())
                .build();
    }

    /**
     * Сохраняет результат прохождения теста
     */
    public void saveResult(ResultDTO resultDTO, String realPath) throws IOException {
        resultDao.save(resultDTO.toEntity(), realPath);
    }


    /**
     * Вычисляет итоговый балл за тест
     */
    public Result calculateScoreResult(Result result) {
        int score = 0;
        List<ResultAnswer> resultAnswers = result.getResultAnswers();

        for (ResultAnswer resultAnswer : resultAnswers) {
            List<Answer> correctAnswers = resultAnswer.getQuestion().getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .toList();

            List<Answer> selectedAnswers = resultAnswer.getSelectedAnswers();

            if (new HashSet<>(selectedAnswers).containsAll(correctAnswers) && new HashSet<>(correctAnswers).containsAll(selectedAnswers)) {
                score++;
            }
        }

        result.setScore(score);
        return result;
    }

    public List<ResultDTO> getAllResultsByUserId(UUID id) throws IOException {
       return resultDao.getAllResultsByUserId(id).stream()
                .map(Result::toDTO)
                .collect(Collectors.toList());

    }

    public ResultDTO findById(String id) throws IOException {
        Result result = resultDao.findById(UUID.fromString(id));
        return result != null ? result.toDTO() : null;
    }

    public List<TestStatsDTO> getStats(List<TestDTO> allTestsDTO) throws IOException {
        List<Result> results = resultDao.findAll();
        List<TestStatsDTO> testStatsDTOList = new ArrayList<>();
        for (TestDTO testDTO : allTestsDTO) {
            String testTitle = testDTO.getTitle();
            Integer totalQuestions = testDTO.getQuestions().size();
            Integer totalPassed = results.stream().filter(r -> r.getTest_id().equals(testDTO.getId())).toList().size();
            Optional<Result> maxOptional = results.stream().filter(r -> r.getTest_id().equals(testDTO.getId())).max(Comparator.comparingInt(Result::getScore));
            Integer maxScore = maxOptional.isPresent() ? maxOptional.get().getScore() :0;
            Optional<Result> min = results.stream().min((r1, r2) -> r2.getDate().compareTo(r1.getDate()));
            LocalDateTime lastPassed = min.map(Result::getDate).orElse(null);
            TestStatsDTO testStatsDTO = TestStatsDTO.builder()
                    .testTitle(testTitle)
                    .totalQuestions(totalQuestions)
                    .totalPassed(totalPassed)
                    .maxScore(maxScore)
                    .lastPassed(lastPassed)
                    .build();

            testStatsDTOList.add(testStatsDTO);
        }
        return testStatsDTOList;

    }


    private List<Result> findAllResultsByTestId(UUID testId, List<Result> results) {
        return results.stream()
                .filter(result -> result.getTest_id().equals(testId))
                .toList();

    }

    public Integer countAttempts() throws IOException {
        return resultDao.findAll().size();
    }
}
