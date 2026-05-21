package br.dev.javacom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais de login")
public record LoginRequest(

        @Schema(example = "admin")
        @NotBlank(message = "username é obrigatório")
        String username,

        @Schema(example = "admin123")
        @NotBlank(message = "password é obrigatório")
        String password
) {}
