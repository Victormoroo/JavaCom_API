package br.dev.javacom.service;

import br.dev.javacom.dto.request.AddCartItemRequest;
import br.dev.javacom.dto.request.UpdateCartItemRequest;
import br.dev.javacom.dto.response.CartResponse;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.entity.User;

public interface CartService {

    CartResponse getCart(User user);

    CartResponse addItem(User user, AddCartItemRequest request);

    CartResponse updateItem(User user, Long productId, UpdateCartItemRequest request);

    CartResponse removeItem(User user, Long productId);

    void clear(User user);

    OrderResponse checkout(User user);
}
