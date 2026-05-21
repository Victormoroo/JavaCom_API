package br.dev.javacom.cli;

import br.dev.javacom.dto.request.AddCartItemRequest;
import br.dev.javacom.dto.request.UpdateCartItemRequest;
import br.dev.javacom.dto.response.CartResponse;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.entity.User;
import br.dev.javacom.service.CartService;
import br.dev.javacom.service.OrderService;
import br.dev.javacom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMenu {

    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;
    private final ConsoleIO console;
    private final ProductPresenter productPresenter;

    public void run(User user) {
        boolean stay = true;
        while (stay) {
            console.printHeader("MENU USER — " + user.getFullName());
            console.println(" 1 - Listar produtos");
            console.println(" 2 - Buscar produto por ID");
            console.println(" 3 - Ver estoque");
            console.println(" 4 - Adicionar produto ao carrinho");
            console.println(" 5 - Remover produto do carrinho");
            console.println(" 6 - Alterar quantidade no carrinho");
            console.println(" 7 - Ver carrinho");
            console.println(" 8 - Limpar carrinho");
            console.println(" 9 - Finalizar compra");
            console.println("10 - Meus pedidos");
            console.println(" 0 - Sair");

            int choice = console.readInt("Escolha uma opção: ", 0, 10);
            switch (choice) {
                case 1 -> console.runScreen("LISTAR PRODUTOS", this::listProducts);
                case 2 -> console.runScreenLoop("BUSCAR PRODUTO POR ID", this::findProductLoop);
                case 3 -> console.runScreen("VER ESTOQUE", this::listStock);
                case 4 -> console.runScreen("ADICIONAR PRODUTO AO CARRINHO", () -> addToCart(user));
                case 5 -> console.runScreen("REMOVER PRODUTO DO CARRINHO", () -> removeFromCart(user));
                case 6 -> console.runScreen("ALTERAR QUANTIDADE NO CARRINHO", () -> updateCartItem(user));
                case 7 -> console.runScreen("VER CARRINHO", () -> showCart(user));
                case 8 -> console.runScreen("LIMPAR CARRINHO", () -> clearCart(user));
                case 9 -> console.runScreen("FINALIZAR COMPRA", () -> checkout(user));
                case 10 -> console.runScreen("MEUS PEDIDOS", () -> listMyOrders(user));
                case 0 -> stay = false;
            }
        }
    }

    private void listProducts() {
        productPresenter.printList(productService.listAll(true));
    }

    private void findProductLoop() {
        console.println("Digite o ID de um produto · 0 para voltar ao menu");
        console.println();
        while (true) {
            Long id = console.readLong("ID: ");
            if (id == 0L) return;
            try {
                productPresenter.printDetail(productService.findActiveById(id));
            } catch (RuntimeException ex) {
                console.println("  ✗ " + ex.getMessage());
            }
            console.println();
        }
    }

    private void listStock() {
        productPresenter.printStock(productService.listStock());
    }

    private void addToCart(User user) {
        Long productId = console.readLong("ID do produto: ");
        int quantity = console.readPositiveInt("Quantidade: ");
        CartResponse cart = cartService.addItem(user, new AddCartItemRequest(productId, quantity));
        printCart(cart);
    }

    private void removeFromCart(User user) {
        Long productId = console.readLong("ID do produto a remover: ");
        CartResponse cart = cartService.removeItem(user, productId);
        printCart(cart);
    }

    private void updateCartItem(User user) {
        Long productId = console.readLong("ID do produto: ");
        int quantity = console.readPositiveInt("Nova quantidade: ");
        CartResponse cart = cartService.updateItem(user, productId, new UpdateCartItemRequest(quantity));
        printCart(cart);
    }

    private void showCart(User user) {
        printCart(cartService.getCart(user));
    }

    private void clearCart(User user) {
        if (console.confirm("Confirmar limpeza do carrinho?")) {
            cartService.clear(user);
            console.println("Carrinho esvaziado.");
        }
    }

    private void checkout(User user) {
        if (!console.confirm("Finalizar a compra?")) {
            return;
        }
        OrderResponse order = cartService.checkout(user);
        console.printHeader("PEDIDO #" + order.id() + " — COMPRA FINALIZADA");
        order.items().forEach(i -> console.println("  • %s | qtd %d | R$ %s/un | subtotal R$ %s".formatted(
                i.productName(), i.quantity(), i.unitPrice(), i.subtotal())));
        console.println("TOTAL: R$ " + order.totalAmount());
    }

    private void listMyOrders(User user) {
        List<OrderResponse> orders = orderService.listMine(user);
        if (orders.isEmpty()) {
            console.println("Você ainda não fez pedidos.");
            return;
        }
        orders.forEach(o -> console.println("Pedido #%d | total R$ %s | %s | %s".formatted(
                o.id(), o.totalAmount(), o.status(), o.createdAt())));
    }

    private void printCart(CartResponse cart) {
        console.printHeader("CARRINHO de " + cart.username());
        if (cart.items().isEmpty()) {
            console.println("(vazio)");
            return;
        }
        cart.items().forEach(i -> console.println("  • [%d] %s | qtd %d | R$ %s/un | subtotal R$ %s".formatted(
                i.productId(), i.productName(), i.quantity(), i.unitPrice(), i.subtotal())));
        console.println("TOTAL: R$ " + cart.total() + " | " + cart.itemCount() + " item(ns)");
    }

}
