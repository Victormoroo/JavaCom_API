package br.dev.javacom.controller;

import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.security.AuthenticatedUserProvider;
import br.dev.javacom.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders")
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;
    private final AuthenticatedUserProvider userProvider;

    @GetMapping
    @Operation(summary = "Lista os pedidos do usuário autenticado")
    public ResponseEntity<List<OrderResponse>> listMine() {
        return ResponseEntity.ok(orderService.listMine(userProvider.getCurrentUser()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha um pedido próprio")
    public ResponseEntity<OrderResponse> getMine(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findMine(userProvider.getCurrentUser(), id));
    }
}
