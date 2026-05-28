package br.dev.javacom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UpdateCartItemRequest", description = "Define a nova quantidade absoluta de um item do carrinho")
public record UpdateCartItemRequest(

        @Schema(description = "Nova quantidade total do item (substitui o valor atual)",
                example = "3", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "quantity é obrigatório")
        @Min(value = 1, message = "quantity deve ser maior que zero")
        Integer quantity
) {}
