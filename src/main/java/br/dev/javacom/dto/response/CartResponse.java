package br.dev.javacom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "CartResponse", description = "Carrinho do usuário autenticado com total recalculado")
public record CartResponse(

        @Schema(description = "ID do carrinho", example = "42")
        Long id,

        @Schema(description = "Username do dono do carrinho", example = "user")
        String username,

        @Schema(description = "Itens do carrinho")
        List<CartItemResponse> items,

        @Schema(description = "Total do carrinho (soma dos subtotais)", example = "10999.80")
        BigDecimal total,

        @Schema(description = "Quantidade total de unidades no carrinho", example = "2")
        int itemCount
) {}
