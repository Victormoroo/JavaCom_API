package br.dev.javacom.cli;

import br.dev.javacom.entity.User;
import br.dev.javacom.repository.UserRepository;
import br.dev.javacom.security.SecurityUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthTerminalService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public Optional<User> login(String username, String password) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(auth);

            if (auth.getPrincipal() instanceof SecurityUserPrincipal sup) {
                return Optional.of(sup.user());
            }
            return userRepository.findByUsername(username);
        } catch (BadCredentialsException ex) {
            return Optional.empty();
        }
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
