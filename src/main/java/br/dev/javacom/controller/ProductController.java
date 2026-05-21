package br.dev.javacom.controller;

import br.dev.javacom.dto.request.ProductRequest;
import br.dev.javacom.dto.response.ProductResponse;
import br.dev.javacom.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Lista produtos (por padrão, apenas ativos)")
    public ResponseEntity<List<ProductResponse>> list(
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(productService.listAll(activeOnly));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto pelo ID")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/stock")
    @Operation(summary = "Consulta o estoque público de todos os produtos")
    public ResponseEntity<List<ProductResponse>> stock() {
        return ResponseEntity.ok(productService.listStock());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastra um novo produto (ADMIN)")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualiza um produto existente (ADMIN)")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativa um produto (ADMIN). Operação lógica, não remove fisicamente.")
    public void deactivate(@PathVariable Long id) {
        productService.deactivate(id);
    }
}
