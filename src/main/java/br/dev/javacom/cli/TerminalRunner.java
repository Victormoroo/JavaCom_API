package br.dev.javacom.cli;

import br.dev.javacom.entity.User;
import br.dev.javacom.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@Order(100)
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "javacom.cli", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TerminalRunner implements ApplicationRunner {

    private final AuthTerminalService authTerminalService;
    private final AdminMenu adminMenu;
    private final UserMenu userMenu;
    private final ConsoleIO console;

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        Thread cliThread = new Thread(this::interactiveLoop, "javacom-cli");
        cliThread.setDaemon(true);
        cliThread.start();
    }

    private void interactiveLoop() {
        try {
            Thread.sleep(700);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }

        console.printHeader("BEM-VINDO À JAVACOM CLI");
        console.println("Aplicação rodando em http://localhost:8080");
        console.println("Swagger:    http://localhost:8080/swagger-ui.html");
        console.println("H2 Console: http://localhost:8080/h2-console");
        console.println("Credenciais seed: admin/admin123 ou user/user123");

        while (true) {
            try {
                Optional<User> loggedUser = promptLogin();
                if (loggedUser.isEmpty()) {
                    continue;
                }
                User user = loggedUser.get();
                console.println("Login bem-sucedido. Papel: " + user.getRole());

                if (user.getRole() == Role.ADMIN) {
                    adminMenu.run(user);
                } else {
                    userMenu.run(user);
                }
                authTerminalService.logout();
                console.println("Sessão encerrada.");
            } catch (RuntimeException ex) {
                log.error("Erro no loop do CLI", ex);
                console.println("Erro inesperado: " + ex.getMessage());
            }
        }
    }

    private Optional<User> promptLogin() {
        console.printHeader("LOGIN");
        console.println("Digite 'exit' como usuário para encerrar o CLI.");
        String username = console.readLine("Usuário: ");
        if ("exit".equalsIgnoreCase(username)) {
            console.println("Encerrando CLI. A API REST continua disponível.");
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            return Optional.empty();
        }
        String password = console.readLine("Senha: ");

        Optional<User> user = authTerminalService.login(username, password);
        if (user.isEmpty()) {
            console.println("Credenciais inválidas.");
        }
        return user;
    }
}
