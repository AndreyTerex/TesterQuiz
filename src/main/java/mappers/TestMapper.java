package mappers;

import dto.TestDTO;
import entity.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;

@Mapper(uses = {QuestionMapper.class}, componentModel = "default")
public interface TestMapper {

    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    @Mapping(source = "creatorId", target = "creator.id")
    Test toEntity(TestDTO testDTO);

    @Mapping(source = "creator.id", target = "creatorId")
    TestDTO toDTO(Test test);
}
