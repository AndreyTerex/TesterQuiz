package mappers;

import dto.ResultDTO;
import entity.Result;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import org.mapstruct.Mapping;

@Mapper(uses = {QuestionMapper.class, AnswerMapper.class, AnswerInResultMapper.class}, componentModel = "default")
public interface ResultMapper {

    ResultMapper INSTANCE = Mappers.getMapper(ResultMapper.class);

    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "testId", target = "test.id")
    Result toEntity(ResultDTO resultDTO);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "test.id", target = "testId")
    ResultDTO toDTO(Result result);
}
