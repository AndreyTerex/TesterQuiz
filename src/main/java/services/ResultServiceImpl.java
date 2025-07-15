package services;

import dao.ResultDAO;
import dto.*;
import entity.*;
import exceptions.DataAccessException;
import exceptions.SaveException;
import exceptions.ValidationException;
import mappers.ResultMapper;
import services.interfaces.ResultService;
import services.interfaces.TestService;
import services.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResultServiceImpl implements ResultService {
    private final ResultDAO resultDao;
    private final ResultMapper resultMapper;
    private final UserService userService;
    private final TestService testService;

    public ResultServiceImpl(ResultDAO resultDao, ResultMapper resultMapper, TestService testService, UserService userService) {
        this.resultDao = resultDao;
        this.resultMapper = resultMapper;
        this.userService = userService;
        this.testService = testService;
    }

    /**
     * Creates a test result object
     */
    public Result buildStartResultObject(Test test, User user, LocalDateTime startTime) {
        return Result.builder()
                .user(user)
                .test(test)
                .answersInResults(new ArrayList<>())
                .date(startTime)
                .testTitle(test.getTitle())
                .build();
    }

    /**
     * Saves the result of passing the test
     */
    public void buildAndSaveFinalResult(ResultDTO resultDTO) {
        try {
            Result result = buildFinalResult(resultDTO);
            resultDao.save(result);
        } catch (DataAccessException e) {
            throw new SaveException("Failed to save result", e);
        }
    }


    /**
     * Calculates the final score for the test
     */
    public Result calculateScoreResult(Result result) {
        int score = 0;
        List<AnswersInResult> answersInResults = result.getAnswersInResults();

        for (AnswersInResult answersInResult : answersInResults) {
            List<Answer> correctAnswers = answersInResult.getQuestion().getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .toList();

            List<Answer> selectedAnswers = answersInResult.getSelectedAnswers();

            if (new HashSet<>(selectedAnswers).equals(new HashSet<>(correctAnswers))) {
                score++;
            }
        }

        result.setScore(score);
        return result;
    }

    public List<ResultDTO> getAllResultsByUserId(UUID id) {
        return resultDao.getAllResultsByUserId(id).stream()
                .map(resultMapper::toDTO)
                .sorted(Comparator.comparing(ResultDTO::getDate).reversed())
                .toList();
    }

    public List<TestStatsDTO> getStats() {
        List<Result> allResults = resultDao.findAll();

        Map<String, List<Result>> resultsByTitle = allResults.stream()
                .collect(Collectors.groupingBy(Result::getTestTitle));

        return resultsByTitle.entrySet().stream()
                .map(entry -> buildStats(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(TestStatsDTO::getTestTitle))
                .collect(Collectors.toList());
    }

    public Long countAttempts() {
        return resultDao.getCount();
    }

    @Override
    public ResultDTO findByIdWithDetails(UUID resultId) {
       return resultMapper.toDTO(resultDao.findByIdWithDetails(resultId).orElseThrow(()-> new ValidationException("Result not found")));
    }

    private TestStatsDTO buildStats(String testTitle, List<Result> results) {
        int totalQuestions = results.stream()
                .findFirst()
                .flatMap(result -> result.getAnswersInResults().stream()
                        .map(a -> a.getQuestion().getQuestionNumber())
                        .max(Integer::compare))
                .orElse(0);

        long totalPassed = results.size();

        int maxScore = results.stream()
                .mapToInt(Result::getScore)
                .max()
                .orElse(0);

        LocalDateTime lastPassed = results.stream()
                .map(Result::getDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return TestStatsDTO.builder()
                .testTitle(testTitle)
                .totalQuestions(totalQuestions)
                .totalPassed(totalPassed)
                .maxScore(maxScore)
                .lastPassed(lastPassed)
                .build();
    }

    private Result buildFinalResult(ResultDTO resultDTO) {
        User user = userService.findUserById(resultDTO.getUserId());
        Test test = testService.findTestById(resultDTO.getTestId());
        Result result = resultMapper.toEntity(resultDTO);
        result.setUser(user);
        result.setTest(test);
        result.setTestTitle(test.getTitle());
        result.setAnswersInResults(new ArrayList<>());

        Map<UUID, Question> questionMap = test.getQuestions().stream()
                .collect(Collectors.toMap(Question::getId, Function.identity()));

        for (AnswersInResultDTO answersInResultDTO : resultDTO.getAnswersInResults()) {
            UUID questionId = answersInResultDTO.getQuestion().getId();
            Question question = Optional.ofNullable(questionMap.get(questionId))
                    .orElseThrow(() -> new SaveException("Question not found for id: " + questionId));

            List<UUID> selectedAnswerIds = answersInResultDTO.getSelectedAnswers().stream()
                    .map(AnswerDTO::getId)
                    .toList();

            List<Answer> selectedAnswers = findAnswersInQuestion(question, selectedAnswerIds);

            AnswersInResult answersInResult = new AnswersInResult();
            answersInResult.setResult(result);
            answersInResult.setQuestion(question);
            answersInResult.setSelectedAnswers(selectedAnswers);

            result.getAnswersInResults().add(answersInResult);
        }
        return result;
    }


    private List<Answer> findAnswersInQuestion(Question question, List<UUID> selectedAnswerIds) {
        Map<UUID, Answer> answerMap = question.getAnswers().stream()
                .collect(Collectors.toMap(Answer::getId, Function.identity()));

        return selectedAnswerIds.stream()
                .map(answerId -> Optional.ofNullable(answerMap.get(answerId))
                .orElseThrow(() -> new SaveException("Answer not found for Id: " + answerId)))
                .toList();
    }
}
