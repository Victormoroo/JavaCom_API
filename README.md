# JavaCom_API

API de e-commerce simulando um carrinho de compras, **sem front-end**, com duas interfaces:

1. **REST API** documentada com Swagger/OpenAPI
2. **CLI interativa** via terminal (login + menus ADMIN/USER)

Projeto de estudo construído com **Java 21** e **Spring Boot 4.0.6**.

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Build | Maven |
| Persistência | Spring Data JPA + Hibernate 6 |
| Banco | H2 (in-memory, modo PostgreSQL) |
| Segurança | Spring Security + JWT (HS256 via jjwt) |
| Documentação | springdoc-openapi 2.8 |
| Mapeamento | MapStruct 1.6 |
| Boilerplate | Lombok 1.18 |
| Testes | JUnit 5, Mockito, AssertJ, Spring Security Test |

---

## Como rodar

Pré-requisitos: **Java 21+** e **Maven 3.9+** instalados.

```bash
mvn spring-boot:run
```

Para empacotar e rodar o JAR:
```bash
mvn clean package
java -jar target/javacom-api.jar
```

Rodar testes:
```bash
mvn test
```

A aplicação sobe em `http://localhost:8080`.

> **Importante:** a CLI inicia automaticamente em uma thread daemon paralela à API REST. Se quiser desabilitar (por exemplo em CI), defina `javacom.cli.enabled=false`.

---

## Credenciais seed

Criadas automaticamente no `DataSeeder` no primeiro boot:

| Usuário | Senha | Papel |
|---|---|---|
| `admin` | `admin123` | ADMIN |
| `user`  | `user123`  | USER  |

Senhas são gravadas no H2 com **BCrypt**.

---

## Endpoints principais

| Método | Path | Quem acessa |
|---|---|---|
| `POST` | `/api/auth/login` | público |
| `GET` | `/api/products` | público |
| `GET` | `/api/products/{id}` | público |
| `GET` | `/api/products/stock` | público |
| `POST` | `/api/products` | ADMIN |
| `PUT` | `/api/products/{id}` | ADMIN |
| `DELETE` | `/api/products/{id}` | ADMIN (desativa) |
| `GET` | `/api/cart` | USER |
| `POST` | `/api/cart/items` | USER |
| `PUT` | `/api/cart/items/{productId}` | USER |
| `DELETE` | `/api/cart/items/{productId}` | USER |
| `DELETE` | `/api/cart` | USER |
| `POST` | `/api/cart/checkout` | USER |
| `GET` | `/api/orders` | USER (próprios) |
| `GET` | `/api/orders/{id}` | USER (próprio) |
| `GET` | `/api/admin/orders` | ADMIN |
| `GET` | `/api/admin/orders/{id}` | ADMIN |

---

## Swagger / OpenAPI

- UI: <http://localhost:8080/swagger-ui.html>
- JSON: <http://localhost:8080/v3/api-docs>

Para testar endpoints protegidos no Swagger:
1. Faça `POST /api/auth/login`
2. Copie o `token` da resposta
3. Clique em **Authorize** no topo da UI
4. Cole o token (sem o prefixo `Bearer`)

---

## H2 Console

- URL: <http://localhost:8080/h2-console>
- JDBC URL: `jdbc:h2:mem:javacomdb`
- User: `admin`
- Password: `123`

> **Nota Spring Boot 4:** o autoconfigure do H2 Console foi movido para o módulo dedicado `spring-boot-h2console`. Se ele for removido do `pom.xml`, o console responde 500 com `NoResourceFoundException`.
>
> **Sobre as credenciais:** o H2 in-memory autentica contra o usuário que criou a base. Como o HikariCP abre a conexão usando `spring.datasource.username/password`, o usuário/senha do `application.yml` é o que vale no H2 Console. Trocar o yml e reiniciar a app altera essas credenciais.

---

## Fluxo básico com `curl`

### 1. Login (USER)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'
```
A resposta traz o `token`. Exporte para reuso:
```bash
TOKEN="eyJhbGciOi..."
```

### 2. Listar produtos
```bash
curl http://localhost:8080/api/products
```

### 3. Adicionar produto ao carrinho
```bash
curl -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

### 4. Visualizar carrinho
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/cart
```

### 5. Finalizar compra
```bash
curl -X POST -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/cart/checkout
```

### 6. Ver pedido
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/orders
```

### Cadastro de produto (ADMIN)
```bash
# Login como admin
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r .token)

curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Câmera GoPro","description":"GoPro Hero 12","price":3299.00,"stockQuantity":7,"active":true}'
```

---

## CLI no terminal

Ao iniciar a aplicação, depois dos logs do Spring, o terminal exibe:

```
========================================
  BEM-VINDO À JAVACOM CLI
========================================
Aplicação rodando em http://localhost:8080
Swagger:    http://localhost:8080/swagger-ui.html
H2 Console: http://localhost:8080/h2-console
Credenciais seed: admin/admin123 ou user/user123
...
========================================
  LOGIN
========================================
Digite 'exit' como usuário para encerrar o CLI.
Usuário: _
```

### Menu ADMIN
```
1 - Listar produtos
2 - Buscar produto por ID
3 - Cadastrar produto
4 - Atualizar produto
5 - Desativar produto
6 - Ver estoque
7 - Listar pedidos
0 - Sair
```

### Menu USER
```
 1 - Listar produtos
 2 - Buscar produto por ID
 3 - Ver estoque
 4 - Adicionar produto ao carrinho
 5 - Remover produto do carrinho
 6 - Alterar quantidade no carrinho
 7 - Ver carrinho
 8 - Limpar carrinho
 9 - Finalizar compra
10 - Meus pedidos
 0 - Sair
```

Digitar `0` no menu retorna à tela de login. Digitar `exit` na tela de login encerra o CLI (a API REST continua rodando).

---

## Estrutura de pastas

```
src/main/java/br/dev/javacom
├── JavaComApiApplication.java
├── cli/                  # ConsoleIO, AuthTerminalService, AdminMenu, UserMenu, TerminalRunner
├── config/               # SecurityConfig, OpenApiConfig
├── controller/           # AuthController, ProductController, CartController, OrderController, AdminOrderController
├── dto/
│   ├── request/          # LoginRequest, ProductRequest, AddCartItemRequest, UpdateCartItemRequest
│   └── response/         # LoginResponse, ProductResponse, CartResponse, CartItemResponse, OrderResponse, OrderItemResponse
├── entity/               # User, Product, Cart, CartItem, Order, OrderItem, BaseAuditEntity
├── enums/                # Role, OrderStatus
├── exception/            # ResourceNotFoundException, BusinessException, UnauthorizedOperationException,
│                         # InsufficientStockException, ApiError, GlobalExceptionHandler
├── mapper/               # ProductMapper (MapStruct), CartMapper, OrderMapper
├── repository/           # UserRepository, ProductRepository, CartRepository, OrderRepository
├── security/             # JwtService, JwtAuthenticationFilter, JwtProperties,
│                         # SecurityUserDetailsService, SecurityUserPrincipal, AuthenticatedUserProvider
├── seed/                 # DataSeeder
└── service/              # interfaces + impl/*ServiceImpl
```

---

## Decisões técnicas

| Decisão | Motivação |
|---|---|
| **JWT em vez de Basic Auth** | Stateless, mais moderno, e didático para projetos de estudo |
| **Carrinho persistido (1:1 com User)** | Permite recuperar carrinho entre sessões; aderente ao mundo real |
| **Total do carrinho calculado dinamicamente** | Evita inconsistência entre preço atual do produto e total cacheado |
| **`OrderItem` armazena `productName`, `unitPrice` e `subtotal`** | Snapshot do produto no momento da compra; alterar produto não muda pedido |
| **Desativação lógica em vez de exclusão (`DELETE /api/products/{id}`)** | Preserva integridade referencial com pedidos antigos |
| **`@PreAuthorize` nos controllers** | Reaproveita papéis do Spring Security; mais limpo que `if/else` |
| **CLI em thread daemon** | API REST + CLI coexistem; CLI não bloqueia o servidor |
| **H2 com `MODE=PostgreSQL`** | Antecipa migração futura: SQL mais próximo do PostgreSQL |
| **MapStruct em vez de mapper manual** | Reduz boilerplate; melhor performance que reflection |
| **BCrypt para senhas** | Padrão da indústria; safe-by-default no Spring Security |
| **`ProblemDetail`-like `ApiError`** | Estrutura uniforme de erros, com lista de `fieldErrors` para validação |

---

## Próximos passos — migrar para PostgreSQL

1. Adicionar dependência no `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```
2. Trocar o `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/javacomdb
       username: javacom
       password: javacom
     jpa:
       hibernate:
         ddl-auto: validate   # passar para validate em produção
       database-platform: org.hibernate.dialect.PostgreSQLDialect
   ```
3. Adicionar **Flyway** ou **Liquibase** para versionar o schema e desligar `ddl-auto: update`.
4. Mover o secret JWT para variável de ambiente (`JAVACOM_SECURITY_JWT_SECRET`).
5. Criar `docker-compose.yml` com Postgres + a aplicação.
6. Adicionar **Testcontainers** nos testes de integração para validar contra o Postgres real.

---

## Como testar a segurança

| Cenário | Comando |
|---|---|
| Listar produtos sem token | `curl http://localhost:8080/api/products` → 200 |
| Acessar carrinho sem token | `curl http://localhost:8080/api/cart` → 401 |
| USER tentando criar produto | `curl -X POST -H "Authorization: Bearer $USER_TOKEN" ...` → 403 |
| ADMIN listando pedidos | `curl -H "Authorization: Bearer $ADMIN_TOKEN" http://localhost:8080/api/admin/orders` → 200 |
| Token inválido | qualquer rota com `Bearer invalid` → 401 (filtro ignora; spring rejeita) |

---

## Licença

Projeto educacional. Sem licença comercial.
