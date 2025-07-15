package mappers;

import dto.AnswersInResultDTO;
import entity.AnswersInResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import org.mapstruct.Mapping;

@Mapper(uses = {QuestionMapper.class, AnswerMapper.class}, componentModel = "default")
public interface AnswerInResultMapper {

    AnswerInResultMapper INSTANCE = Mappers.getMapper(AnswerInResultMapper.class);

    @Mapping(source = "resultId", target = "result.id")
    AnswersInResult toEntity(AnswersInResultDTO answerInResultDTO);

    @Mapping(source = "result.id", target = "resultId")
    AnswersInResultDTO toDTO(AnswersInResult answersInResult);
}
