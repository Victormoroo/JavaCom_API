package br.dev.javacom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Atualiza a quantidade de um item do carrinho")
public record UpdateCartItemRequest(

        @Schema(example = "3")
        @NotNull(message = "quantity é obrigatório")
        @Min(value = 1, message = "quantity deve ser maior que zero")
        Integer quantity
) {}
