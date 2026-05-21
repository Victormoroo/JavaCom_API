package br.dev.javacom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Carrinho do usuário")
public record CartResponse(
        Long id,
        String username,
        List<CartItemResponse> items,
        BigDecimal total,
        int itemCount
) {}
