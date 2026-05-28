package br.dev.javacom.controller;

import br.dev.javacom.config.OpenApiConfig;
import br.dev.javacom.dto.request.ProductRequest;
import br.dev.javacom.dto.response.ProductResponse;
import br.dev.javacom.exception.ApiError;
import br.dev.javacom.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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

    @Operation(
            summary = "Lista produtos do catálogo",
            description = "Endpoint **público**. Por padrão retorna apenas produtos ativos. Use `activeOnly=false` para incluir desativados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))
    })
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<List<ProductResponse>> list(
            @Parameter(description = "Se `true`, retorna somente produtos ativos. Se `false`, retorna todos.",
                    example = "true")
            @RequestParam(defaultValue = "true") boolean activeOnly) {
        return ResponseEntity.ok(productService.listAll(activeOnly));
    }

    @Operation(
            summary = "Busca um produto por ID",
            description = "Endpoint **público**. Retorna o produto independente de estar ativo ou não."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(
            @Parameter(description = "ID do produto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(
            summary = "Visão pública de estoque",
            description = "Endpoint **público**. Lista todos os produtos com a quantidade em estoque."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de produtos com estoque",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class))))
    })
    @SecurityRequirements
    @GetMapping("/stock")
    public ResponseEntity<List<ProductResponse>> stock() {
        return ResponseEntity.ok(productService.listStock());
    }

    @Operation(
            summary = "Cadastra um novo produto",
            description = "Apenas usuários com perfil **ADMIN**.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Produto cadastrado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão (usuário não é ADMIN)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "422", description = "Nome de produto já existente",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @Operation(
            summary = "Atualiza um produto existente",
            description = "Apenas usuários com perfil **ADMIN**. Todos os campos do request são aplicados; o campo `active` pode ser omitido para manter o estado atual.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Produto atualizado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @Parameter(description = "ID do produto a atualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.update(id, request));
    }

    @Operation(
            summary = "Desativa um produto",
            description = "Apenas usuários com perfil **ADMIN**. Operação **lógica**: o produto continua existindo no banco, apenas deixa de aparecer no catálogo público e fica indisponível para compra.",
            security = @SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Produto desativado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(
            @Parameter(description = "ID do produto a desativar", example = "1") @PathVariable Long id) {
        productService.deactivate(id);
    }
}
