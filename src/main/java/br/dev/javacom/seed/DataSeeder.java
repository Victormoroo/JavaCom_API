package br.dev.javacom.seed;

import br.dev.javacom.entity.Product;
import br.dev.javacom.entity.User;
import br.dev.javacom.enums.Role;
import br.dev.javacom.repository.ProductRepository;
import br.dev.javacom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            log.info("Usuários já existem — pulando seed de usuários.");
            return;
        }

        userRepository.save(User.builder()
                .username("admin")
                .fullName("Administrador")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .fullName("Usuário Padrão")
                .password(passwordEncoder.encode("user123"))
                .role(Role.USER)
                .enabled(true)
                .build());

        log.info("Usuários seed criados: admin/admin123 e user/user123");
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            log.info("Produtos já existem — pulando seed de produtos.");
            return;
        }

        List<Product> products = List.of(
                product("Notebook Dell Inspiron 15",
                        "Intel Core i7, 16GB RAM, SSD 512GB, tela 15.6\" Full HD",
                        new BigDecimal("5499.90"), 12),
                product("MacBook Air M3",
                        "Apple M3, 8 GPU cores, 8GB RAM, SSD 256GB, tela 13.6\" Liquid Retina",
                        new BigDecimal("11999.00"), 5),
                product("Teclado Mecânico Keychron K2",
                        "Sem fio Bluetooth, switches gateron brown, RGB, layout 75%",
                        new BigDecimal("899.00"), 30),
                product("Mouse Logitech MX Master 3S",
                        "Sensor 8000 DPI, clique silencioso, multi-dispositivo",
                        new BigDecimal("749.00"), 25),
                product("Monitor LG Ultrawide 34\"",
                        "Resolução 3440x1440, 144Hz, painel IPS, HDR10",
                        new BigDecimal("3299.00"), 8),
                product("Headset HyperX Cloud II",
                        "Som surround 7.1, microfone removível, drivers de 53mm",
                        new BigDecimal("629.00"), 40),
                product("SSD NVMe Kingston KC3000 1TB",
                        "PCIe Gen 4, leitura até 7000MB/s, fator M.2 2280",
                        new BigDecimal("549.90"), 50),
                product("Placa de Vídeo RTX 4060",
                        "8GB GDDR6, DLSS 3, Ray Tracing, suporte 4K",
                        new BigDecimal("2399.00"), 6),
                product("Roteador Wi-Fi 6 TP-Link Archer AX55",
                        "Dual-band, até 3000Mbps, OFDMA, 4 antenas",
                        new BigDecimal("699.00"), 18),
                product("Webcam Logitech Brio 4K",
                        "Resolução 4K Ultra HD, HDR, foco automático",
                        new BigDecimal("1199.00"), 15)
        );

        productRepository.saveAll(products);
        log.info("Seed de {} produtos criado.", products.size());
    }

    private Product product(String name, String description, BigDecimal price, int stock) {
        return Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stockQuantity(stock)
                .active(true)
                .build();
    }
}
