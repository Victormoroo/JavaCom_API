package br.dev.javacom.service.impl;

import br.dev.javacom.dto.request.ProductRequest;
import br.dev.javacom.dto.response.ProductResponse;
import br.dev.javacom.entity.Product;
import br.dev.javacom.exception.BusinessException;
import br.dev.javacom.exception.ResourceNotFoundException;
import br.dev.javacom.mapper.ProductMapper;
import br.dev.javacom.repository.ProductRepository;
import br.dev.javacom.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> listAll(boolean activeOnly) {
        List<Product> products = activeOnly
                ? productRepository.findAllByActiveTrueOrderByNameAsc()
                : productRepository.findAll().stream()
                        .sorted(Comparator.comparing(Product::getName))
                        .toList();
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productMapper.toResponse(loadProduct(id));
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException("Já existe um produto com o nome: " + request.name());
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .active(request.active() == null || request.active())
                .build();

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = loadProduct(id);

        if (!product.getName().equalsIgnoreCase(request.name())
                && productRepository.existsByNameIgnoreCase(request.name())) {
            throw new BusinessException("Já existe outro produto com o nome: " + request.name());
        }

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        if (request.active() != null) {
            product.setActive(request.active());
        }

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Product product = loadProduct(id);
        product.setActive(false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> listStock() {
        return productMapper.toResponseList(
                productRepository.findAll().stream()
                        .sorted(Comparator.comparing(Product::getName))
                        .toList()
        );
    }

    private Product loadProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Produto", id));
    }
}
