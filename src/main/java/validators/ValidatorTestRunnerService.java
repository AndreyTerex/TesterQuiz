package validators;

import dto.TestProgressDTO;
import dto.UserDTO;
import entity.Test;
import exceptions.ValidationException;

public class ValidatorTestRunnerService {
    public void validateTestSessionStart(Test currentTest, UserDTO userDTO) {
        if (currentTest == null) {
            throw new ValidationException("Test not found");
        }
        if (userDTO == null) {
            throw new ValidationException("User not found");
        }
    }

    public void validateTestProgressDTO(TestProgressDTO testProgressDTO) {
        if(testProgressDTO == null){
            throw new ValidationException("Data not found");
        }
        if (testProgressDTO.getAnswers() == null) {
            throw new ValidationException("Answers are not selected");
        }
        if (testProgressDTO.getResult() == null) {
            throw new ValidationException("Result not found");
        }
        if (testProgressDTO.getQuestion() == null) {
            throw new ValidationException("Question not found");
        }
    }

    public void validateTest(Test test) {
        if (test == null) {
            throw new ValidationException("Test not found");
        }
        if (test.getQuestions().isEmpty()){
            throw new ValidationException("Test is empty");
        }
    }
}
