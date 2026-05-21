package br.dev.javacom.mapper;

import br.dev.javacom.dto.response.OrderItemResponse;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.entity.Order;
import br.dev.javacom.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUser().getUsername(),
                order.getStatus(),
                order.getTotalAmount(),
                items,
                order.getCreatedAt()
        );
    }

    public OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }

    public List<OrderResponse> toResponseList(List<Order> orders) {
        return orders.stream().map(this::toResponse).toList();
    }
}
