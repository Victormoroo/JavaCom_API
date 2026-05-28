package br.dev.javacom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(name = "AddCartItemRequest", description = "Adiciona ou incrementa um item no carrinho")
public record AddCartItemRequest(

        @Schema(description = "ID do produto a adicionar", example = "1",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "productId é obrigatório")
        Long productId,

        @Schema(description = "Quantidade a adicionar (somada à existente, se já estiver no carrinho)",
                example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "quantity é obrigatório")
        @Min(value = 1, message = "quantity deve ser maior que zero")
        Integer quantity
) {}
