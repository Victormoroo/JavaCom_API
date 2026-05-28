package br.dev.javacom.dto.response;

import br.dev.javacom.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(name = "OrderResponse", description = "Pedido finalizado, com snapshot dos itens e valor total")
public record OrderResponse(

        @Schema(description = "ID do pedido", example = "1")
        Long id,

        @Schema(description = "Username do comprador", example = "user")
        String username,

        @Schema(description = "Status do pedido", implementation = OrderStatus.class)
        OrderStatus status,

        @Schema(description = "Valor total do pedido", example = "10999.80")
        BigDecimal totalAmount,

        @Schema(description = "Itens do pedido")
        List<OrderItemResponse> items,

        @Schema(description = "Data/hora em que o pedido foi finalizado",
                example = "2026-05-28T11:30:00")
        LocalDateTime createdAt
) {}
