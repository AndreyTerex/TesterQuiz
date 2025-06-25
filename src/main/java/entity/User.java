package entity;

import dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String username;
    private String password;
    private String role;
    private UUID id;

    public UserDTO toDTO() {
        return UserDTO.builder()
                .username(username)
                .role(role)
                .id(id)
                .build();
    }
}
