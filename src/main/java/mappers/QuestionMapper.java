package mappers;

import dto.QuestionDTO;
import entity.Question;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity Question and its DTO QuestionDTO.
 */
@Mapper(componentModel = "default", uses = {AnswerMapper.class})
public interface QuestionMapper {

    Question toEntity(QuestionDTO questionDTO);

    QuestionDTO toDTO(Question question);
}
