package br.dev.javacom.controller;

import br.dev.javacom.dto.request.AddCartItemRequest;
import br.dev.javacom.dto.request.UpdateCartItemRequest;
import br.dev.javacom.dto.response.CartResponse;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.security.AuthenticatedUserProvider;
import br.dev.javacom.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart")
@PreAuthorize("hasRole('USER')")
public class CartController {

    private final CartService cartService;
    private final AuthenticatedUserProvider userProvider;

    @GetMapping
    @Operation(summary = "Retorna o carrinho do usuário autenticado")
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(userProvider.getCurrentUser()));
    }

    @PostMapping("/items")
    @Operation(summary = "Adiciona um item ao carrinho")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(userProvider.getCurrentUser(), request));
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "Altera a quantidade de um item do carrinho")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Long productId,
                                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(userProvider.getCurrentUser(), productId, request));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove um item do carrinho")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userProvider.getCurrentUser(), productId));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Esvazia o carrinho")
    public void clear() {
        cartService.clear(userProvider.getCurrentUser());
    }

    @PostMapping("/checkout")
    @Operation(summary = "Finaliza a compra; baixa estoque e cria pedido COMPLETED")
    public ResponseEntity<OrderResponse> checkout() {
        return ResponseEntity.ok(cartService.checkout(userProvider.getCurrentUser()));
    }
}
