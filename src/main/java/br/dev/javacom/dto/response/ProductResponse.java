package br.dev.javacom.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "ProductResponse", description = "Representação de um produto do catálogo")
public record ProductResponse(

        @Schema(description = "Identificador único do produto", example = "1")
        Long id,

        @Schema(description = "Nome do produto", example = "Notebook Dell Inspiron 15")
        String name,

        @Schema(description = "Descrição do produto",
                example = "Intel Core i7, 16GB RAM, SSD 512GB, tela 15.6\" Full HD")
        String description,

        @Schema(description = "Preço em BRL", example = "5499.90")
        BigDecimal price,

        @Schema(description = "Quantidade disponível em estoque", example = "12")
        Integer stockQuantity,

        @Schema(description = "Indica se o produto está ativo no catálogo", example = "true")
        boolean active,

        @Schema(description = "Disponível para compra — derivado: ativo e com estoque maior que zero",
                example = "true")
        boolean purchasable,

        @Schema(description = "Data/hora de criação", example = "2026-05-21T08:42:00")
        LocalDateTime createdAt,

        @Schema(description = "Data/hora da última atualização", example = "2026-05-28T11:15:30")
        LocalDateTime updatedAt
) {}
