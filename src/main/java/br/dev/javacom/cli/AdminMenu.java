package br.dev.javacom.cli;

import br.dev.javacom.dto.request.ProductRequest;
import br.dev.javacom.dto.response.OrderResponse;
import br.dev.javacom.dto.response.ProductResponse;
import br.dev.javacom.entity.User;
import br.dev.javacom.service.OrderService;
import br.dev.javacom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminMenu {

    private final ProductService productService;
    private final OrderService orderService;
    private final ConsoleIO console;
    private final ProductPresenter productPresenter;

    public void run(User admin) {
        boolean stay = true;
        while (stay) {
            console.printHeader("MENU ADMIN — " + admin.getFullName());
            console.println("1 - Listar produtos");
            console.println("2 - Buscar produto por ID");
            console.println("3 - Cadastrar produto");
            console.println("4 - Atualizar produto");
            console.println("5 - Desativar produto");
            console.println("6 - Ver estoque");
            console.println("7 - Listar pedidos");
            console.println("0 - Sair");

            int choice = console.readInt("Escolha uma opção: ", 0, 7);
            switch (choice) {
                case 1 -> console.runScreen("LISTAR PRODUTOS", this::listProducts);
                case 2 -> console.runScreen("BUSCAR PRODUTO POR ID", this::findProduct);
                case 3 -> console.runScreen("CADASTRAR PRODUTO", this::createProduct);
                case 4 -> console.runScreen("ATUALIZAR PRODUTO", this::updateProduct);
                case 5 -> console.runScreen("DESATIVAR PRODUTO", this::deactivateProduct);
                case 6 -> console.runScreen("VER ESTOQUE", this::listStock);
                case 7 -> console.runScreen("LISTAR PEDIDOS", this::listOrders);
                case 0 -> stay = false;
            }
        }
    }

    private void listProducts() {
        productPresenter.printList(productService.listAll(false));
    }

    private void findProduct() {
        Long id = console.readLong("ID do produto: ");
        productPresenter.printDetail(productService.findById(id));
    }

    private void createProduct() {
        String name = console.readLine("Nome: ");
        String description = console.readLine("Descrição: ");
        BigDecimal price = console.readBigDecimal("Preço (ex: 1499.90): ");
        int stock = console.readInt("Estoque inicial: ", 0, Integer.MAX_VALUE);

        ProductResponse created = productService.create(
                new ProductRequest(name, description, price, stock, true));
        console.println("Produto criado com ID: " + created.id());
        console.println();
        productPresenter.printDetail(created);
    }

    private void updateProduct() {
        Long id = console.readLong("ID do produto a atualizar: ");
        ProductResponse current = productService.findById(id);
        console.println("Estado atual:");
        productPresenter.printDetail(current);
        console.println();

        String name = console.readLine("Novo nome: ");
        String description = console.readLine("Nova descrição: ");
        BigDecimal price = console.readBigDecimal("Novo preço: ");
        int stock = console.readInt("Novo estoque: ", 0, Integer.MAX_VALUE);
        boolean active = console.confirm("Manter ativo?");

        ProductResponse updated = productService.update(id,
                new ProductRequest(name, description, price, stock, active));
        console.println();
        console.println("Atualizado:");
        productPresenter.printDetail(updated);
    }

    private void deactivateProduct() {
        Long id = console.readLong("ID do produto a desativar: ");
        if (console.confirm("Confirmar desativação?")) {
            productService.deactivate(id);
            console.println("Produto desativado.");
        }
    }

    private void listStock() {
        productPresenter.printStock(productService.listStock());
    }

    private void listOrders() {
        List<OrderResponse> orders = orderService.listAll();
        if (orders.isEmpty()) {
            console.println("Nenhum pedido encontrado.");
            return;
        }
        orders.forEach(o ->
                console.println("Pedido #%d | usuário=%s | total R$ %s | %s | criado em %s".formatted(
                        o.id(), o.username(), o.totalAmount(), o.status(), o.createdAt())));
    }

}
