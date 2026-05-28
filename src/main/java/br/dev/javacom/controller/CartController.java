package br.dev.javacom.controller;

import br.dev.javacom.config.OpenApiConfig;
import br.dev.javacom.dto.request.AddCartItemRequest;
import br.dev.javacom.dto.request.UpdateCartItemRequest;
import br.dev.javacom.dto.response.CartResponse;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.exception.ApiError;
import br.dev.javacom.security.AuthenticatedUserProvider;
import br.dev.javacom.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
public class CartController {

    private final CartService cartService;
    private final AuthenticatedUserProvider userProvider;

    @Operation(
            summary = "Retorna o carrinho do usuário autenticado",
            description = "Cria automaticamente um carrinho vazio caso ainda não exista."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho do usuário",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão (USER apenas)",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(userProvider.getCurrentUser()));
    }

    @Operation(
            summary = "Adiciona um item ao carrinho",
            description = "Se o produto já estiver no carrinho, **soma** a quantidade existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Carrinho atualizado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Produto inativo / sem estoque",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(userProvider.getCurrentUser(), request));
    }

    @Operation(summary = "Atualiza a quantidade de um item do carrinho",
            description = "Define a quantidade absoluta do item (substitui o valor atual).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Item não está no carrinho",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(userProvider.getCurrentUser(), productId, request));
    }

    @Operation(summary = "Remove um item do carrinho")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item removido; retorna o carrinho atualizado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Item não está no carrinho",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(userProvider.getCurrentUser(), productId));
    }

    @Operation(summary = "Esvazia o carrinho", description = "Remove todos os itens do carrinho do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrinho esvaziado")
    })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear() {
        cartService.clear(userProvider.getCurrentUser());
    }

    @Operation(
            summary = "Finaliza a compra",
            description = """
                    Valida novamente o estoque, **abate as quantidades** dos produtos comprados, cria um pedido com status `COMPLETED` e **limpa o carrinho**.
                    Esta operação é transacional — se qualquer item falhar a verificação, a operação é cancelada e o estoque permanece intacto.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido criado",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "409", description = "Estoque insuficiente para algum item",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Carrinho vazio",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() {
        return ResponseEntity.ok(cartService.checkout(userProvider.getCurrentUser()));
    }
}
