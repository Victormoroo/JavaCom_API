package br.dev.javacom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Adiciona um produto ao carrinho")
public record AddCartItemRequest(

        @Schema(example = "1")
        @NotNull(message = "productId é obrigatório")
        Long productId,

        @Schema(example = "2")
        @NotNull(message = "quantity é obrigatório")
        @Min(value = 1, message = "quantity deve ser maior que zero")
        Integer quantity
) {}
