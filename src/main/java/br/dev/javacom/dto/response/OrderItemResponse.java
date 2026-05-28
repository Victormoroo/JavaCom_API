package br.dev.javacom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "OrderItemResponse", description = "Item de um pedido (snapshot do produto no momento da compra)")
public record OrderItemResponse(

        @Schema(description = "ID do produto", example = "1")
        Long productId,

        @Schema(description = "Nome do produto no momento da compra", example = "Notebook Dell Inspiron 15")
        String productName,

        @Schema(description = "Preço unitário no momento da compra", example = "5499.90")
        BigDecimal unitPrice,

        @Schema(description = "Quantidade comprada", example = "2")
        Integer quantity,

        @Schema(description = "Subtotal = unitPrice * quantity", example = "10999.80")
        BigDecimal subtotal
) {}
