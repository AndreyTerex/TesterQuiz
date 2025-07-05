package mappers;

import dto.ResultDTO;
import entity.Result;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper for the entity Result and its DTO ResultDTO.
 */
@Mapper(uses = {QuestionMapper.class, AnswerMapper.class, ResultAnswerMapper.class}, componentModel = "default")
public interface ResultMapper {

    ResultMapper INSTANCE = Mappers.getMapper(ResultMapper.class);

    Result toEntity(ResultDTO resultDTO);

    ResultDTO toDTO(Result result);
}
