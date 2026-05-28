package br.dev.javacom.dto.response;

import br.dev.javacom.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "LoginResponse", description = "Resposta de login bem-sucedido com token JWT")
public record LoginResponse(

        @Schema(description = "Token JWT (HS256) — use no header `Authorization: Bearer <token>`",
                example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTcwMH0.fakeSignature")
        String token,

        @Schema(description = "Tipo do token", example = "Bearer")
        String tokenType,

        @Schema(description = "Nome de usuário autenticado", example = "admin")
        String username,

        @Schema(description = "Nome completo do usuário", example = "Administrador")
        String fullName,

        @Schema(description = "Papel do usuário autenticado", implementation = Role.class)
        Role role,

        @Schema(description = "Momento (timezone do servidor) em que o token expira",
                example = "2026-05-28T17:00:00")
        LocalDateTime expiresAt
) {}
