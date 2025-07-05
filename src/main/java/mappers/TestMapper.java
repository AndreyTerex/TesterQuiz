package mappers;

import dto.TestDTO;
import entity.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity Test and its DTO TestDTO.
 */
@Mapper(uses = {QuestionMapper.class}, componentModel = "default")
public interface TestMapper {

    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    Test toEntity(TestDTO testDTO);

    TestDTO toDTO(Test test);
}
