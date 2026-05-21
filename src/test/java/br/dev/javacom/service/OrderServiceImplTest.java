package br.dev.javacom.service;

import br.dev.javacom.dto.response.OrderItemResponse;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.entity.Order;
import br.dev.javacom.entity.User;
import br.dev.javacom.enums.OrderStatus;
import br.dev.javacom.enums.Role;
import br.dev.javacom.exception.ResourceNotFoundException;
import br.dev.javacom.exception.UnauthorizedOperationException;
import br.dev.javacom.mapper.OrderMapper;
import br.dev.javacom.repository.OrderRepository;
import br.dev.javacom.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock OrderMapper orderMapper;
    @InjectMocks OrderServiceImpl service;

    @Test
    void findMine_throwsWhenOrderBelongsToAnotherUser() {
        User owner = User.builder().id(1L).username("alice").role(Role.USER).build();
        User attacker = User.builder().id(2L).username("bob").role(Role.USER).build();

        Order order = Order.builder().id(10L).user(owner).status(OrderStatus.COMPLETED)
                .totalAmount(BigDecimal.TEN).build();
        when(orderRepository.findByIdWithItems(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> service.findMine(attacker, 10L))
                .isInstanceOf(UnauthorizedOperationException.class);
    }

    @Test
    void findMine_returnsOrderWhenOwner() {
        User owner = User.builder().id(1L).username("alice").role(Role.USER).build();
        Order order = Order.builder().id(10L).user(owner).status(OrderStatus.COMPLETED)
                .totalAmount(BigDecimal.TEN).items(List.of()).build();
        when(orderRepository.findByIdWithItems(10L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(new OrderResponse(
                10L, "alice", OrderStatus.COMPLETED, BigDecimal.TEN,
                List.<OrderItemResponse>of(), LocalDateTime.now()));

        OrderResponse response = service.findMine(owner, 10L);

        org.assertj.core.api.Assertions.assertThat(response.id()).isEqualTo(10L);
    }

    @Test
    void findById_throwsWhenMissing() {
        when(orderRepository.findByIdWithItems(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
