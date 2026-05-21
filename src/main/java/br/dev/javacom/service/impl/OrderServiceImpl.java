package br.dev.javacom.service.impl;

import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.entity.Order;
import br.dev.javacom.entity.User;
import br.dev.javacom.exception.ResourceNotFoundException;
import br.dev.javacom.exception.UnauthorizedOperationException;
import br.dev.javacom.mapper.OrderMapper;
import br.dev.javacom.repository.OrderRepository;
import br.dev.javacom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listMine(User user) {
        return orderMapper.toResponseList(orderRepository.findAllByUserOrderByCreatedAtDesc(user));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findMine(User user, Long id) {
        Order order = loadOrder(id);
        if (!order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedOperationException("Você não tem permissão para visualizar este pedido");
        }
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listAll() {
        List<Order> orders = orderRepository.findAll().stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .toList();
        return orderMapper.toResponseList(orders);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        return orderMapper.toResponse(loadOrder(id));
    }

    private Order loadOrder(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Pedido", id));
    }
}
