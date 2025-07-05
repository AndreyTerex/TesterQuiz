package mappers;

import dto.ResultAnswerDTO;
import entity.ResultAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {QuestionMapper.class, AnswerMapper.class}, componentModel = "default")
public interface ResultAnswerMapper {

    ResultAnswerMapper INSTANCE = Mappers.getMapper(ResultAnswerMapper.class);

    ResultAnswer toEntity(ResultAnswerDTO resultAnswerDTO);

    ResultAnswerDTO toDTO(ResultAnswer resultAnswer);
}
