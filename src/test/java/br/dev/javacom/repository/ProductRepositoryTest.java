package br.dev.javacom.repository;

import br.dev.javacom.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findAllByActiveTrueOrderByNameAsc_returnsActiveOnly() {
        productRepository.save(Product.builder()
                .name("Alpha").description("d").price(new BigDecimal("10")).stockQuantity(1).active(true).build());
        productRepository.save(Product.builder()
                .name("Beta").description("d").price(new BigDecimal("10")).stockQuantity(1).active(false).build());
        productRepository.save(Product.builder()
                .name("Charlie").description("d").price(new BigDecimal("10")).stockQuantity(1).active(true).build());

        List<Product> actives = productRepository.findAllByActiveTrueOrderByNameAsc();

        assertThat(actives).extracting(Product::getName).containsExactly("Alpha", "Charlie");
    }

    @Test
    void existsByNameIgnoreCase_returnsTrueForExisting() {
        productRepository.save(Product.builder()
                .name("Notebook XYZ").description("d").price(new BigDecimal("10"))
                .stockQuantity(1).active(true).build());

        assertThat(productRepository.existsByNameIgnoreCase("notebook xyz")).isTrue();
        assertThat(productRepository.existsByNameIgnoreCase("OUTRA COISA")).isFalse();
    }
}
