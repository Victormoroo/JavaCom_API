package br.dev.javacom.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Ambiente local de desenvolvimento")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositório no GitHub")
                        .url("https://github.com/Victormoroo/JavaCom_API"))
                .tags(List.of(
                        new Tag().name("Auth").description("Autenticação por JWT — emissão e renovação de tokens"),
                        new Tag().name("Products").description("Catálogo de produtos — listagem pública e gestão administrativa"),
                        new Tag().name("Cart").description("Carrinho de compras do usuário autenticado e checkout"),
                        new Tag().name("Orders").description("Histórico de pedidos do usuário autenticado"),
                        new Tag().name("Admin").description("Operações administrativas restritas ao perfil ADMIN")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME, bearerScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("JavaCom API — E-commerce Cart")
                .version("v1")
                .description("""
                        API REST de e-commerce que simula um carrinho de compras para produtos de tecnologia.

                        ## Recursos
                        - Cadastro e gestão de produtos com controle de estoque (ADMIN)
                        - Catálogo público (sem autenticação) com listagem e busca de produtos
                        - Carrinho de compras por usuário, com validação de estoque
                        - Finalização de compra com baixa de estoque e geração de pedido
                        - Histórico de pedidos por usuário e visão consolidada para administradores

                        ## Autenticação
                        Endpoints protegidos exigem **JWT** no header `Authorization: Bearer <token>`.
                        Obtenha o token em `POST /api/auth/login`.

                        ## Papéis
                        - **USER** — consulta produtos, gerencia carrinho, finaliza compras e consulta os próprios pedidos
                        - **ADMIN** — gerencia produtos e visualiza pedidos de qualquer usuário
                        """)
                .contact(new Contact()
                        .name("JavaCom")
                        .url("https://github.com/Victormoroo/JavaCom_API"))
                .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private SecurityScheme bearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Token JWT obtido em `POST /api/auth/login`. Informe **apenas o token**, sem o prefixo `Bearer `.");
    }
}
