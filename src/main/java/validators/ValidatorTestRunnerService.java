package validators;

import dto.TestProgressDTO;
import dto.UserDTO;
import entity.Test;
import exceptions.ValidationException;
import util.ValidatorUtil;

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
        ValidatorUtil.validate(testProgressDTO);
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
