package mappers;

import dto.AnswerDTO;
import entity.Answer;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity Answer and its DTO AnswerDTO.
 */
@Mapper(componentModel = "default")
public interface AnswerMapper {

    Answer toEntity(AnswerDTO answerDTO);

    AnswerDTO toDTO(Answer answer);
}
