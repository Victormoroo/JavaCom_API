package br.dev.javacom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Produto")
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        boolean active,
        @Schema(description = "Disponível para compra (ativo e com estoque > 0)")
        boolean purchasable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
