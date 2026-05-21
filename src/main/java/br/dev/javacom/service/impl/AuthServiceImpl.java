package br.dev.javacom.service.impl;

import br.dev.javacom.dto.request.LoginRequest;
import br.dev.javacom.dto.response.LoginResponse;
import br.dev.javacom.entity.User;
import br.dev.javacom.repository.UserRepository;
import br.dev.javacom.security.JwtService;
import br.dev.javacom.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Usuário ou senha inválidos");
        }

        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado"));

        JwtService.TokenPair pair = jwtService.generate(user.getUsername(), user.getRole().name());

        return new LoginResponse(
                pair.token(),
                "Bearer",
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                pair.expiresAt()
        );
    }
}
