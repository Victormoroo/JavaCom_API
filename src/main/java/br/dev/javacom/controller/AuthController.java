package br.dev.javacom.controller;

import br.dev.javacom.dto.request.LoginRequest;
import br.dev.javacom.dto.response.LoginResponse;
import br.dev.javacom.exception.ApiError;
import br.dev.javacom.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Autentica e emite um token JWT",
            description = "Recebe credenciais (`username` + `password`) e devolve um JWT (HS256) válido pelo tempo configurado em `javacom.security.jwt.expiration-minutes`."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-05-28T11:42:10",
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "Usuário ou senha inválidos",
                                      "path": "/api/auth/login",
                                      "fieldErrors": null
                                    }
                                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
