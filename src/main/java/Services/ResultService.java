package Services;

import dao.ResultDao;
import dto.ResultDTO;
import entity.*;
import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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
}
