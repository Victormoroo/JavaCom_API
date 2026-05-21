package br.dev.javacom.service;

import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.entity.User;

import java.util.List;

public interface OrderService {

    List<OrderResponse> listMine(User user);

    OrderResponse findMine(User user, Long id);

    List<OrderResponse> listAll();

    OrderResponse findById(Long id);
}
