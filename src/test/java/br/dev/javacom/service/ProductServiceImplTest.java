package br.dev.javacom.service;

import br.dev.javacom.dto.request.ProductRequest;
import br.dev.javacom.dto.response.ProductResponse;
import br.dev.javacom.entity.Product;
import br.dev.javacom.exception.BusinessException;
import br.dev.javacom.exception.ResourceNotFoundException;
import br.dev.javacom.mapper.ProductMapper;
import br.dev.javacom.repository.ProductRepository;
import br.dev.javacom.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl service;

    private Product sample;

    @BeforeEach
    void setup() {
        sample = Product.builder()
                .id(1L)
                .name("Mouse")
                .description("Mouse gamer")
                .price(new BigDecimal("100.00"))
                .stockQuantity(10)
                .active(true)
                .build();
    }

    @Test
    void create_persistsAndReturnsResponse() {
        when(productRepository.existsByNameIgnoreCase(anyString())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(sample);
        when(productMapper.toResponse(any(Product.class))).thenReturn(stubResponse(sample));

        ProductRequest req = new ProductRequest("Mouse", "Mouse gamer",
                new BigDecimal("100.00"), 10, true);

        ProductResponse response = service.create(req);

        assertThat(response.name()).isEqualTo("Mouse");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void create_failsWhenNameAlreadyExists() {
        when(productRepository.existsByNameIgnoreCase("Mouse")).thenReturn(true);

        ProductRequest req = new ProductRequest("Mouse", "Mouse gamer",
                new BigDecimal("100.00"), 10, true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Já existe");
    }

    @Test
    void findById_throwsWhenMissing() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deactivate_setsActiveFalse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sample));

        service.deactivate(1L);

        assertThat(sample.isActive()).isFalse();
    }

    @Test
    void update_changesFields() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sample));
        when(productMapper.toResponse(any(Product.class))).thenReturn(stubResponse(sample));

        ProductRequest req = new ProductRequest("Mouse Pro", "Mouse melhor",
                new BigDecimal("150.00"), 8, true);

        service.update(1L, req);

        assertThat(sample.getName()).isEqualTo("Mouse Pro");
        assertThat(sample.getPrice()).isEqualByComparingTo("150.00");
        assertThat(sample.getStockQuantity()).isEqualTo(8);
    }

    @Test
    void listAll_returnsActiveOnlyWhenFlagTrue() {
        when(productRepository.findAllByActiveTrueOrderByIdAsc()).thenReturn(List.of(sample));
        when(productMapper.toResponseList(any())).thenReturn(List.of(stubResponse(sample)));

        List<ProductResponse> result = service.listAll(true);

        assertThat(result).hasSize(1);
        verify(productRepository).findAllByActiveTrueOrderByIdAsc();
    }

    private ProductResponse stubResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getStockQuantity(), p.isActive(), p.isActive() && p.getStockQuantity() > 0,
                LocalDateTime.now(), LocalDateTime.now());
    }
}
