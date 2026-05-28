package br.dev.javacom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Credenciais para autenticação")
public record LoginRequest(

        @Schema(description = "Nome de usuário cadastrado", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "username é obrigatório")
        String username,

        @Schema(description = "Senha do usuário", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
        @NotBlank(message = "password é obrigatório")
        String password
) {}
