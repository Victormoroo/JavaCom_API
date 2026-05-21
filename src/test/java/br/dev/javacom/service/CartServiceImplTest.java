package br.dev.javacom.service;

import br.dev.javacom.dto.request.AddCartItemRequest;
import br.dev.javacom.dto.request.UpdateCartItemRequest;
import br.dev.javacom.dto.response.CartItemResponse;
import br.dev.javacom.dto.response.CartResponse;
import br.dev.javacom.dto.response.OrderItemResponse;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.entity.Cart;
import br.dev.javacom.entity.Order;
import br.dev.javacom.entity.Product;
import br.dev.javacom.entity.User;
import br.dev.javacom.enums.OrderStatus;
import br.dev.javacom.enums.Role;
import br.dev.javacom.exception.BusinessException;
import br.dev.javacom.exception.InsufficientStockException;
import br.dev.javacom.mapper.CartMapper;
import br.dev.javacom.mapper.OrderMapper;
import br.dev.javacom.repository.CartRepository;
import br.dev.javacom.repository.OrderRepository;
import br.dev.javacom.repository.ProductRepository;
import br.dev.javacom.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock CartRepository cartRepository;
    @Mock ProductRepository productRepository;
    @Mock OrderRepository orderRepository;
    @Mock CartMapper cartMapper;
    @Mock OrderMapper orderMapper;

    @InjectMocks CartServiceImpl service;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setup() {
        user = User.builder().id(10L).username("user").role(Role.USER).enabled(true).build();
        product = Product.builder().id(1L).name("Mouse").price(new BigDecimal("100"))
                .stockQuantity(5).active(true).description("d").build();
        cart = Cart.builder().id(1L).user(user).items(new ArrayList<>()).build();
    }

    @Test
    void addItem_addsNewItem() {
        when(cartRepository.findByUserWithItems(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartMapper.toResponse(cart)).thenReturn(stubCart(2));

        service.addItem(user, new AddCartItemRequest(1L, 2));

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void addItem_incrementsExistingItem() {
        when(cartRepository.findByUserWithItems(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartMapper.toResponse(cart)).thenReturn(stubCart(3));

        service.addItem(user, new AddCartItemRequest(1L, 2));
        service.addItem(user, new AddCartItemRequest(1L, 1));

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void addItem_failsWhenStockInsufficient() {
        product.setStockQuantity(1);
        when(cartRepository.findByUserWithItems(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.addItem(user, new AddCartItemRequest(1L, 5)))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void addItem_failsWhenProductInactive() {
        product.setActive(false);
        when(cartRepository.findByUserWithItems(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> service.addItem(user, new AddCartItemRequest(1L, 1)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("inativo");
    }

    @Test
    void updateItem_changesQuantity() {
        when(cartRepository.findByUserWithItems(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartMapper.toResponse(any(Cart.class))).thenReturn(stubCart(1));

        service.addItem(user, new AddCartItemRequest(1L, 1));
        service.updateItem(user, 1L, new UpdateCartItemRequest(3));

        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void checkout_emptyCartFails() {
        when(cartRepository.findByUserWithItems(user)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> service.checkout(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("vazio");
    }

    @Test
    void checkout_reducesStockAndClearsCart() {
        when(cartRepository.findByUserWithItems(user)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartMapper.toResponse(any(Cart.class))).thenReturn(stubCart(2));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(99L);
            return o;
        });
        when(orderMapper.toResponse(any(Order.class))).thenReturn(stubOrder());

        service.addItem(user, new AddCartItemRequest(1L, 2));
        OrderResponse response = service.checkout(user);

        assertThat(response.id()).isEqualTo(99L);
        assertThat(product.getStockQuantity()).isEqualTo(3);
        assertThat(cart.getItems()).isEmpty();
    }

    private CartResponse stubCart(int totalItems) {
        BigDecimal total = product.getPrice().multiply(BigDecimal.valueOf(totalItems));
        return new CartResponse(1L, "user",
                List.of(new CartItemResponse(1L, "Mouse", product.getPrice(), totalItems, total)),
                total, totalItems);
    }

    private OrderResponse stubOrder() {
        return new OrderResponse(99L, "user", OrderStatus.COMPLETED, new BigDecimal("200"),
                List.of(new OrderItemResponse(1L, "Mouse", new BigDecimal("100"), 2, new BigDecimal("200"))),
                LocalDateTime.now());
    }
}
