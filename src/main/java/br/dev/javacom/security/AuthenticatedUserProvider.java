package br.dev.javacom.security;

import br.dev.javacom.entity.User;
import br.dev.javacom.exception.UnauthorizedOperationException;
import br.dev.javacom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedOperationException("Operação exige autenticação");
        }

        String username;
        Object principal = auth.getPrincipal();
        if (principal instanceof SecurityUserPrincipal sup) {
            return sup.user();
        }
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
            username = ud.getUsername();
        } else {
            username = principal.toString();
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedOperationException("Usuário autenticado não foi encontrado"));
    }
}
