package services.interfaces;

import dto.UserDTO;
import entity.User;
import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.RegistrationException;

import java.util.UUID;

/**
 * Service for user-related operations like registration and authentication.
 */
public interface UserService {

    /** Registers a new user in the system. */
    UserDTO registerUser(UserDTO userDTO, String password) throws RegistrationException, DataAccessException;

    /** Authenticates a user by username and password. */
    UserDTO login(String username, String password) throws AuthenticationException, DataAccessException;

    /** Finds a user entity by its ID. */
    User findUserById(UUID userId) throws DataAccessException;

}
