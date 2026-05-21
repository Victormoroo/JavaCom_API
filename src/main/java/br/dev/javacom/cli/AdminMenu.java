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
                case 2 -> console.runScreenLoop("BUSCAR PRODUTO POR ID", this::findProductLoop);
                case 3 -> console.runScreen("CADASTRAR PRODUTO", this::createProduct);
                case 4 -> console.runScreenLoop("ATUALIZAR PRODUTO", this::updateProduct);
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

    private void findProductLoop() {
        console.println("Digite o ID de um produto · 0 para voltar ao menu");
        console.println();
        while (true) {
            Long id = console.readLong("ID: ");
            if (id == 0L) return;
            try {
                productPresenter.printDetail(productService.findById(id));
            } catch (RuntimeException ex) {
                console.println("  ✗ " + ex.getMessage());
            }
            console.println();
        }
    }

    private void createProduct() {
        console.println("Pressione ENTER para iniciar o cadastro · 0 para cancelar e voltar ao menu");
        String start = console.readLine("> ");
        if ("0".equals(start.trim())) {
            console.println();
            console.println("Cadastro cancelado.");
            return;
        }
        console.println();

        String name = console.readNonBlank("Nome: ");
        String description = console.readNonBlank("Descrição: ");
        BigDecimal price = console.readPositiveBigDecimal("Preço (ex: 1499.90): ");
        int stock = console.readInt("Estoque inicial: ", 0, Integer.MAX_VALUE);

        ProductResponse created = productService.create(
                new ProductRequest(name, description, price, stock, true));
        console.println();
        console.println("✓ Produto criado com ID " + created.id());
        console.println();
        productPresenter.printDetail(created);
    }

    private void updateProduct() {
        ProductResponse current = promptExistingProduct();
        if (current == null) return;

        String name = current.name();
        String description = current.description();
        BigDecimal price = current.price();
        int stock = current.stockQuantity();
        boolean active = current.active();
        boolean changed = false;

        while (true) {
            console.clear();
            console.printHeader("ATUALIZAR PRODUTO #" + current.id());
            productPresenter.printDetail(snapshot(current, name, description, price, stock, active));

            console.println();
            console.println(changed ? "(alterações pendentes — selecione 0 para salvar)" : "");
            console.println("Qual campo deseja alterar?");
            console.println(" 1 - Nome");
            console.println(" 2 - Descrição");
            console.println(" 3 - Preço");
            console.println(" 4 - Estoque");
            console.println(" 5 - Status (ativo / inativo)");
            console.println(" 0 - " + (changed ? "Salvar alterações" : "Voltar sem alterar"));

            int choice = console.readInt("Escolha: ", 0, 5);
            if (choice == 0) break;
            switch (choice) {
                case 1 -> { name = console.readNonBlank("Novo nome: "); changed = true; }
                case 2 -> { description = console.readNonBlank("Nova descrição: "); changed = true; }
                case 3 -> { price = console.readPositiveBigDecimal("Novo preço: "); changed = true; }
                case 4 -> { stock = console.readInt("Novo estoque (≥ 0): ", 0, Integer.MAX_VALUE); changed = true; }
                case 5 -> { active = console.confirm("Produto deve ficar ativo?"); changed = true; }
            }
        }

        if (!changed) return;

        ProductResponse updated = productService.update(current.id(),
                new ProductRequest(name, description, price, stock, active));
        console.clear();
        console.printHeader("PRODUTO ATUALIZADO");
        productPresenter.printDetail(updated);
        console.println();
        console.readLine("Pressione ENTER para voltar ao menu... ");
    }

    private ProductResponse promptExistingProduct() {
        while (true) {
            Long id = console.readLong("ID do produto a atualizar · 0 para voltar: ");
            if (id == 0L) return null;
            try {
                return productService.findById(id);
            } catch (RuntimeException ex) {
                console.println("  ✗ " + ex.getMessage());
                console.println();
            }
        }
    }

    private ProductResponse snapshot(ProductResponse base, String name, String description,
                                     BigDecimal price, int stock, boolean active) {
        boolean purchasable = active && stock > 0;
        return new ProductResponse(base.id(), name, description, price, stock,
                active, purchasable, base.createdAt(), base.updatedAt());
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
