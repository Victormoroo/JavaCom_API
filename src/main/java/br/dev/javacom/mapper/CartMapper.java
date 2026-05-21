package br.dev.javacom.mapper;

import br.dev.javacom.dto.response.CartItemResponse;
import br.dev.javacom.dto.response.CartResponse;
import br.dev.javacom.entity.Cart;
import br.dev.javacom.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int itemCount = cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return new CartResponse(
                cart.getId(),
                cart.getUser().getUsername(),
                itemResponses,
                total,
                itemCount
        );
    }

    public CartItemResponse toItemResponse(CartItem item) {
        return new CartItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.subtotal()
        );
    }
}
