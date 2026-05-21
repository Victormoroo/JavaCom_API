package br.dev.javacom.dto.response;

import br.dev.javacom.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Resposta de login bem-sucedido")
public record LoginResponse(

        @Schema(example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(example = "Bearer")
        String tokenType,

        @Schema(example = "admin")
        String username,

        @Schema(example = "Administrador")
        String fullName,

        Role role,

        LocalDateTime expiresAt
) {}
