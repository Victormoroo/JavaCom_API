package br.dev.javacom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "CartItemResponse", description = "Item do carrinho com preço unitário e subtotal calculado")
public record CartItemResponse(

        @Schema(description = "ID do produto", example = "1")
        Long productId,

        @Schema(description = "Nome do produto no momento da consulta", example = "Notebook Dell Inspiron 15")
        String productName,

        @Schema(description = "Preço unitário atual do produto", example = "5499.90")
        BigDecimal unitPrice,

        @Schema(description = "Quantidade no carrinho", example = "2")
        Integer quantity,

        @Schema(description = "Subtotal = unitPrice * quantity", example = "10999.80")
        BigDecimal subtotal
) {}
