package br.dev.javacom.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(description = "Dados para cadastro ou atualização de produto")
public record ProductRequest(

        @Schema(example = "Notebook Dell Inspiron 15")
        @NotBlank(message = "name é obrigatório")
        @Size(max = 120)
        String name,

        @Schema(example = "Notebook Intel i7, 16GB RAM, 512GB SSD")
        @NotBlank(message = "description é obrigatório")
        @Size(max = 500)
        String description,

        @Schema(example = "5499.90")
        @NotNull(message = "price é obrigatório")
        @DecimalMin(value = "0.01", message = "price deve ser positivo")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @Schema(example = "12")
        @NotNull(message = "stockQuantity é obrigatório")
        @Min(value = 0, message = "stockQuantity deve ser maior ou igual a zero")
        Integer stockQuantity,

        @Schema(example = "true", description = "Se ausente no PUT, mantém o valor atual")
        Boolean active
) {}
