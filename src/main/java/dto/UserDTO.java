package dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    @NotBlank(message = "Username cannot be blank or contain only whitespace characters.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long.")
    @Pattern(regexp = "^(?!\\d+$)[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers, and cannot consist only of digits.")
    private String username;
    private String role;
    private UUID id;
}
