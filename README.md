<div align="center">

# JavaCom API

**API REST de e-commerce — carrinho de compras, gestão de produtos, autenticação JWT e controle de estoque.**

[![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![OpenAPI](https://img.shields.io/badge/OpenAPI-3.1-6BA539?logo=openapiinitiative&logoColor=white)](https://www.openapis.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

</div>

---

## Sumário

- [Sobre](#sobre)
- [Stack](#stack)
- [Arquitetura](#arquitetura)
- [Como executar](#como-executar)
- [Credenciais seed](#credenciais-seed)
- [Endpoints](#endpoints)
- [Documentação interativa (Swagger)](#documentação-interativa-swagger)
- [H2 Console](#h2-console)
- [Fluxo de uso (`curl`)](#fluxo-de-uso-curl)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Decisões técnicas](#decisões-técnicas)
- [Branches do repositório](#branches-do-repositório)
- [Próximos passos para produção](#próximos-passos-para-produção)
- [Licença](#licença)

---

## Sobre

API REST inspirada em e-commerce real, focada em produtos de tecnologia. Implementa o ciclo completo:
**cadastro de produto → catálogo público → carrinho → checkout → pedido** — com autenticação JWT, perfis de
usuário e controle de estoque transacional.

Construída com **Spring Boot 4**, **Java 21**, persistência **JPA/Hibernate** em **H2** (substituível por PostgreSQL)
e documentada com **OpenAPI 3.1 / Swagger UI**.

---

## Stack

| Camada           | Tecnologia                                 |
|------------------|--------------------------------------------|
| Linguagem        | Java 21                                    |
| Framework        | Spring Boot 4.0.6 · Spring Framework 7     |
| Build            | Maven                                      |
| Persistência     | Spring Data JPA · Hibernate 6              |
| Banco            | H2 in-memory (modo PostgreSQL)             |
| Segurança        | Spring Security · JWT (HS256, `jjwt` 0.12) |
| Documentação     | springdoc-openapi 2.8 · Swagger UI         |
| Mapeamento       | MapStruct 1.6                              |
| Reduz boilerplate| Lombok 1.18                                |
| Testes           | JUnit 5 · Mockito · AssertJ · Spring Test  |

---

## Arquitetura

Organização em camadas clássica, com separação clara de responsabilidades:

```
Controller  →  Service (regras de negócio)  →  Repository  →  Entity
     ↑              ↑
   DTOs          Mapper (MapStruct)
     ↑
  Swagger / OpenAPI
```

- **Controllers** recebem DTOs validados (`@Valid`), nunca expõem entidades JPA
- **Services** centralizam toda regra de negócio (validação de estoque, finalização de compra, etc.)
- **Repositories** apenas acessam dados (sem regra)
- **Security** com `JwtAuthenticationFilter` stateless + `@PreAuthorize` por método
- **GlobalExceptionHandler** com `@RestControllerAdvice` e estrutura de erro consistente (`ApiError`)

---

## Como executar

**Pré-requisitos:** Java 21+ e Maven 3.9+.

```bash
# rodar em modo desenvolvimento
mvn spring-boot:run

# empacotar e rodar JAR
mvn clean package
java -jar target/javacom-api.jar

# executar os testes
mvn test
```

A aplicação sobe em `http://localhost:8080`.

### Variáveis de ambiente

| Variável                          | Default                    | Descrição                                      |
|-----------------------------------|----------------------------|------------------------------------------------|
| `JAVACOM_JWT_SECRET`              | (chave embutida, dev only) | Chave Base64 ≥ 32 bytes para assinar o JWT     |
| `JAVACOM_JWT_EXPIRATION_MINUTES`  | `120`                      | Tempo de vida do token JWT                     |

> ⚠️ Em produção, **sempre** defina `JAVACOM_JWT_SECRET` via variável de ambiente.

---

## Credenciais seed

`DataSeeder` cria automaticamente no primeiro boot:

| Usuário | Senha       | Papel  |
|---------|-------------|--------|
| `admin` | `admin123`  | ADMIN  |
| `user`  | `user123`   | USER   |

Senhas armazenadas com **BCrypt**.

Também cria 10 produtos iniciais de tecnologia (Notebook Dell, MacBook Air M3, RTX 4060, etc.).

---

## Endpoints

| Método   | Path                              | Acesso     | Descrição                              |
|----------|-----------------------------------|------------|----------------------------------------|
| `POST`   | `/api/auth/login`                 | público    | Autentica e devolve um JWT             |
| `GET`    | `/api/products`                   | público    | Lista produtos (`activeOnly=true` por padrão) |
| `GET`    | `/api/products/{id}`              | público    | Detalha um produto                     |
| `GET`    | `/api/products/stock`             | público    | Lista pública de estoque               |
| `POST`   | `/api/products`                   | **ADMIN**  | Cadastra produto                       |
| `PUT`    | `/api/products/{id}`              | **ADMIN**  | Atualiza produto                       |
| `DELETE` | `/api/products/{id}`              | **ADMIN**  | Desativa produto (soft-delete)         |
| `GET`    | `/api/cart`                       | **USER**   | Carrinho do usuário autenticado        |
| `POST`   | `/api/cart/items`                 | **USER**   | Adiciona item ao carrinho              |
| `PUT`    | `/api/cart/items/{productId}`     | **USER**   | Atualiza quantidade de um item         |
| `DELETE` | `/api/cart/items/{productId}`     | **USER**   | Remove item do carrinho                |
| `DELETE` | `/api/cart`                       | **USER**   | Esvazia o carrinho                     |
| `POST`   | `/api/cart/checkout`              | **USER**   | Finaliza compra (baixa estoque, cria pedido) |
| `GET`    | `/api/orders`                     | **USER**   | Lista pedidos do usuário               |
| `GET`    | `/api/orders/{id}`                | **USER**   | Detalha pedido próprio                 |
| `GET`    | `/api/admin/orders`               | **ADMIN**  | Lista todos os pedidos                 |
| `GET`    | `/api/admin/orders/{id}`          | **ADMIN**  | Detalha qualquer pedido                |

### Códigos HTTP retornados

| Status | Significado                                                    |
|--------|----------------------------------------------------------------|
| `200`  | Sucesso                                                        |
| `201`  | Recurso criado (com header `Location`)                         |
| `204`  | Sucesso sem corpo (`DELETE`)                                   |
| `400`  | Erro de validação no payload                                   |
| `401`  | Não autenticado                                                |
| `403`  | Autenticado mas sem permissão                                  |
| `404`  | Recurso não encontrado                                         |
| `409`  | Conflito (ex: estoque insuficiente, integridade)               |
| `422`  | Regra de negócio violada (ex: carrinho vazio, nome duplicado)  |
| `500`  | Erro interno                                                   |

Estrutura de erro padronizada (`ApiError`):

```json
{
  "timestamp": "2026-05-28T11:42:10",
  "status": 404,
  "error": "Not Found",
  "message": "Produto não encontrado(a) com id=99",
  "path": "/api/products/99",
  "fieldErrors": null
}
```

---

## Documentação interativa (Swagger)

- **Swagger UI:** <http://localhost:8080/swagger-ui.html>
- **OpenAPI JSON:** <http://localhost:8080/v3/api-docs>

### Para testar endpoints protegidos na UI

1. Faça `POST /api/auth/login` na seção **Auth**
2. Copie o `token` da resposta
3. Clique em **Authorize** 🔒 no topo da página
4. Cole o token (sem o prefixo `Bearer`) e confirme
5. O Swagger UI **persiste a autorização** entre recargas, então você só faz isso uma vez por sessão

---

## H2 Console

- **URL:** <http://localhost:8080/h2-console>
- **JDBC URL:** `jdbc:h2:mem:javacomdb`
- **User:** `admin`
- **Password:** `123`

> O banco é in-memory: ao parar a aplicação, todos os dados são perdidos.
> O `DataSeeder` recria os usuários e produtos no próximo boot.

---

## Fluxo de uso (`curl`)

### 1. Autenticar como USER

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}' \
  | jq -r .token)
```

### 2. Listar produtos (público)

```bash
curl -s http://localhost:8080/api/products | jq
```

### 3. Adicionar item ao carrinho

```bash
curl -s -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}' | jq
```

### 4. Ver carrinho

```bash
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/cart | jq
```

### 5. Finalizar compra

```bash
curl -s -X POST -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/cart/checkout | jq
```

### 6. Ver meus pedidos

```bash
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/orders | jq
```

### Cadastrar produto (ADMIN)

```bash
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r .token)

curl -s -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
        "name":"Câmera GoPro Hero 12",
        "description":"4K 60fps, à prova d`água, estabilização HyperSmooth 6.0",
        "price":3299.00,
        "stockQuantity":7,
        "active":true
      }' | jq
```

---

## Estrutura do projeto

```
src/main/java/br/dev/javacom
├── JavaComApiApplication.java        # entrypoint
├── config/                           # SecurityConfig, OpenApiConfig
├── controller/                       # AuthController, ProductController, CartController,
│                                     # OrderController, AdminOrderController
├── dto/
│   ├── request/                      # LoginRequest, ProductRequest, AddCartItemRequest, UpdateCartItemRequest
│   └── response/                     # LoginResponse, ProductResponse, CartResponse, CartItemResponse,
│                                     # OrderResponse, OrderItemResponse
├── entity/                           # User, Product, Cart, CartItem, Order, OrderItem, BaseAuditEntity
├── enums/                            # Role, OrderStatus
├── exception/                        # ResourceNotFoundException, BusinessException,
│                                     # UnauthorizedOperationException, InsufficientStockException,
│                                     # ApiError, GlobalExceptionHandler
├── mapper/                           # ProductMapper (MapStruct), CartMapper, OrderMapper
├── repository/                       # JpaRepositories para User, Product, Cart, Order
├── security/                         # JwtService, JwtAuthenticationFilter, JwtProperties,
│                                     # SecurityUserDetailsService, SecurityUserPrincipal,
│                                     # AuthenticatedUserProvider
├── seed/                             # DataSeeder
└── service/                          # interfaces + impl/*ServiceImpl
```

---

## Decisões técnicas

| Decisão | Motivação |
|---|---|
| **JWT stateless** | API REST sem sessão; mais fácil escalar horizontalmente |
| **Carrinho 1-para-1 com `User`** | Cada usuário tem um único carrinho persistente entre sessões |
| **Total calculado dinamicamente** | Evita inconsistência entre preço do produto e total cacheado |
| **`OrderItem` com snapshot** | Pedido preserva nome e preço do produto **no momento da compra**, mesmo que o produto seja alterado/desativado depois |
| **Soft delete em produtos** | `DELETE /api/products/{id}` apenas desativa — preserva integridade referencial com pedidos antigos |
| **`@PreAuthorize` por método** | Segurança declarativa e expressiva; complementa o filtro JWT |
| **H2 com `MODE=PostgreSQL`** | SQL próximo do PostgreSQL para facilitar migração futura |
| **MapStruct + Lombok** | Reduz boilerplate; compila em código limpo (sem reflection em runtime) |
| **BCrypt para senhas** | Padrão da indústria; safe-by-default no Spring Security |
| **`ApiError` consistente** | Estrutura uniforme de erros, com lista de `fieldErrors` em 400 |

---

## Branches do repositório

| Branch        | Conteúdo                                                                 |
|---------------|--------------------------------------------------------------------------|
| `main`        | Projeto principal — somente a **API REST**                               |
| `interaction` | Versão experimental com **CLI interativa via terminal** (menus ADMIN/USER) |

Para conferir a CLI:

```bash
git checkout interaction
mvn spring-boot:run
```

---

## Próximos passos para produção

1. **Banco**: trocar H2 por PostgreSQL e adicionar **Flyway/Liquibase** para versionamento de schema
2. **Configuração**: mover todos os secrets para variáveis de ambiente; `ddl-auto: validate` em vez de `update`
3. **Observabilidade**: Spring Actuator + Micrometer + Prometheus/Grafana
4. **Containerização**: `Dockerfile` + `docker-compose.yml` com Postgres
5. **CI/CD**: GitHub Actions com build, testes e Sonar
6. **Testes de integração**: Testcontainers com PostgreSQL real
7. **Rate limiting**: bucket4j ou similar nos endpoints públicos
8. **Refresh tokens**: ciclo completo de renovação de JWT
9. **Pagamento**: integração com gateway real (Stripe/Mercado Pago)
10. **Auditoria**: log estruturado de operações sensíveis (ADMIN actions)

---

## Licença

[MIT](LICENSE)
