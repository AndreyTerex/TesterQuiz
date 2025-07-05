package mappers;

import dto.UserDTO;
import entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity User and its DTO UserDTO.
 */
@Mapper(componentModel = "default")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    User toEntity(UserDTO userDTO);

    UserDTO toDTO(User user);
}
