package br.dev.kajosama.dropship.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.dev.kajosama.dropship.domain.model.entities.User;
import br.dev.kajosama.dropship.domain.repositories.UserRepository;

/**
 * @author Sam_Umbra
 * @Description Custom implementation of Spring Security's
 *              {@link UserDetailsService}.
 *              This service is responsible for loading user-specific data
 *              during the authentication process.
 *              It retrieves user details from the database using
 *              {@link UserRepository}.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * Repository for {@link User} entities, used to fetch user details from the
     * database.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Locates the user based on the username (email in this case).
     * In a real application, the username can be a unique identifier for the user,
     * such as email address, a username, or a user ID.
     *
     * @param email The email address of the user to retrieve.
     * @return A fully populated {@link UserDetails} object (which is the
     *         {@link User} entity itself).
     * @throws UsernameNotFoundException If the user with the specified email is not
     *                                   found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));
    }
}