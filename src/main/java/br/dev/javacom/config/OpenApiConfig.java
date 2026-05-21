package br.dev.javacom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JavaCom API - E-commerce Cart")
                        .description("""
                                API de estudo para simulação de carrinho de e-commerce com Spring Boot,
                                H2, autenticação, perfis de usuário, estoque, pedidos e interação via terminal.
                                """)
                        .version("v1")
                        .contact(new Contact().name("JavaCom").email("contato@javacom.dev.br"))
                        .license(new License().name("MIT")))
                .tags(List.of(
                        new Tag().name("Auth").description("Autenticação e emissão de JWT"),
                        new Tag().name("Products").description("Catálogo de produtos e consulta de estoque"),
                        new Tag().name("Cart").description("Carrinho de compras do usuário"),
                        new Tag().name("Orders").description("Pedidos do usuário autenticado"),
                        new Tag().name("Admin").description("Operações administrativas")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtido em POST /api/auth/login")));
    }
}
