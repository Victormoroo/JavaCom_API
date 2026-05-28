package br.dev.javacom.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "ApiError", description = "Estrutura padrão de erro retornada pela API")
public record ApiError(

        @Schema(description = "Data/hora em que o erro ocorreu", example = "2026-05-28T11:42:10")
        LocalDateTime timestamp,

        @Schema(description = "Código HTTP", example = "404")
        int status,

        @Schema(description = "Reason phrase do HTTP", example = "Not Found")
        String error,

        @Schema(description = "Mensagem legível para humanos", example = "Produto não encontrado(a) com id=99")
        String message,

        @Schema(description = "Path da requisição que originou o erro", example = "/api/products/99")
        String path,

        @Schema(description = "Lista de erros de validação por campo (presente apenas em 400)")
        List<FieldError> fieldErrors
) {
    @Schema(name = "ApiFieldError", description = "Erro de validação em um campo específico do request")
    public record FieldError(
            @Schema(description = "Nome do campo com erro", example = "price")
            String field,
            @Schema(description = "Mensagem de validação", example = "price deve ser positivo")
            String message
    ) {}
}
