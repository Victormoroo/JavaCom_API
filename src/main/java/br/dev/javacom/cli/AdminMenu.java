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
            try {
                switch (choice) {
                    case 1 -> listProducts();
                    case 2 -> findProduct();
                    case 3 -> createProduct();
                    case 4 -> updateProduct();
                    case 5 -> deactivateProduct();
                    case 6 -> listStock();
                    case 7 -> listOrders();
                    case 0 -> stay = false;
                    default -> console.println("Opção inválida");
                }
            } catch (RuntimeException ex) {
                console.println("Erro: " + ex.getMessage());
            }
        }
    }

    private void listProducts() {
        List<ProductResponse> products = productService.listAll(false);
        printProducts(products);
    }

    private void findProduct() {
        Long id = console.readLong("ID do produto: ");
        ProductResponse p = productService.findById(id);
        printProduct(p);
    }

    private void createProduct() {
        console.printHeader("CADASTRO DE PRODUTO");
        String name = console.readLine("Nome: ");
        String description = console.readLine("Descrição: ");
        BigDecimal price = console.readBigDecimal("Preço (ex: 1499.90): ");
        int stock = console.readInt("Estoque inicial: ", 0, Integer.MAX_VALUE);

        ProductResponse created = productService.create(
                new ProductRequest(name, description, price, stock, true));
        console.println("Produto criado com ID: " + created.id());
        printProduct(created);
    }

    private void updateProduct() {
        Long id = console.readLong("ID do produto a atualizar: ");
        ProductResponse current = productService.findById(id);
        console.println("Atual: " + current.name() + " - R$ " + current.price() + " - estoque " + current.stockQuantity());

        String name = console.readLine("Novo nome: ");
        String description = console.readLine("Nova descrição: ");
        BigDecimal price = console.readBigDecimal("Novo preço: ");
        int stock = console.readInt("Novo estoque: ", 0, Integer.MAX_VALUE);
        boolean active = console.confirm("Manter ativo?");

        ProductResponse updated = productService.update(id,
                new ProductRequest(name, description, price, stock, active));
        printProduct(updated);
    }

    private void deactivateProduct() {
        Long id = console.readLong("ID do produto a desativar: ");
        if (console.confirm("Confirmar desativação?")) {
            productService.deactivate(id);
            console.println("Produto desativado.");
        }
    }

    private void listStock() {
        console.printHeader("ESTOQUE");
        productService.listStock().forEach(p ->
                console.println("[%d] %s | preço R$ %s | estoque %d | %s".formatted(
                        p.id(), p.name(), p.price(), p.stockQuantity(),
                        p.active() ? "ATIVO" : "INATIVO")));
    }

    private void listOrders() {
        console.printHeader("TODOS OS PEDIDOS");
        List<OrderResponse> orders = orderService.listAll();
        if (orders.isEmpty()) {
            console.println("Nenhum pedido encontrado.");
            return;
        }
        orders.forEach(o ->
                console.println("Pedido #%d | usuário=%s | total R$ %s | %s | criado em %s".formatted(
                        o.id(), o.username(), o.totalAmount(), o.status(), o.createdAt())));
    }

    private void printProducts(List<ProductResponse> products) {
        if (products.isEmpty()) {
            console.println("Nenhum produto cadastrado.");
            return;
        }
        products.forEach(this::printProduct);
    }

    private void printProduct(ProductResponse p) {
        console.println("[%d] %s | R$ %s | estoque %d | %s%s".formatted(
                p.id(), p.name(), p.price(), p.stockQuantity(),
                p.active() ? "ATIVO" : "INATIVO",
                p.purchasable() ? "" : " (não comprável)"));
        console.println("    " + p.description());
    }
}
