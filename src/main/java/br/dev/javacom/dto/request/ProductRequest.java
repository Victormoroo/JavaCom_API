package br.dev.javacom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "ProductRequest", description = "Dados para criação ou atualização de um produto")
public record ProductRequest(

        @Schema(description = "Nome do produto (único)", example = "Notebook Dell Inspiron 15", maxLength = 120,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "name é obrigatório")
        @Size(max = 120)
        String name,

        @Schema(description = "Descrição do produto", example = "Notebook Intel i7, 16GB RAM, SSD 512GB",
                maxLength = 500, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "description é obrigatório")
        @Size(max = 500)
        String description,

        @Schema(description = "Preço em reais (BRL). Deve ser maior que zero",
                example = "5499.90", minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "price é obrigatório")
        @DecimalMin(value = "0.01", message = "price deve ser positivo")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @Schema(description = "Quantidade disponível em estoque (0 ou mais)",
                example = "12", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "stockQuantity é obrigatório")
        @Min(value = 0, message = "stockQuantity deve ser maior ou igual a zero")
        Integer stockQuantity,

        @Schema(description = "Flag de produto ativo. Se omitido em `PUT`, mantém o valor atual",
                example = "true", nullable = true)
        Boolean active
) {}
