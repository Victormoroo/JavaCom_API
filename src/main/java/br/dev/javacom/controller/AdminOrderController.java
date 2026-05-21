package br.dev.javacom.controller;

import br.dev.javacom.dto.response.OrderResponse;
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
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Lista todos os pedidos (ADMIN)")
    public ResponseEntity<List<OrderResponse>> listAll() {
        return ResponseEntity.ok(orderService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalha um pedido qualquer (ADMIN)")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findById(id));
    }
}
