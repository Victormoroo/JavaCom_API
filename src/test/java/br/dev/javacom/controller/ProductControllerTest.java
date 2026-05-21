package br.dev.javacom.controller;

import br.dev.javacom.dto.request.ProductRequest;
import br.dev.javacom.dto.response.ProductResponse;
import br.dev.javacom.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean ProductService productService;

    @Test
    void list_isPublic() throws Exception {
        when(productService.listAll(true)).thenReturn(List.of(stub()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Mouse"));
    }

    @Test
    void create_unauthorizedWithoutToken() throws Exception {
        String body = objectMapper.writeValueAsString(
                new ProductRequest("Mouse", "desc", new BigDecimal("99.90"), 5, true));

        mockMvc.perform(post("/api/products").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_forbiddenForUserRole() throws Exception {
        String body = objectMapper.writeValueAsString(
                new ProductRequest("Mouse", "desc", new BigDecimal("99.90"), 5, true));

        mockMvc.perform(post("/api/products")
                        .with(user("alice").roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_okForAdminRole() throws Exception {
        when(productService.create(any(ProductRequest.class))).thenReturn(stub());

        String body = objectMapper.writeValueAsString(
                new ProductRequest("Mouse", "desc", new BigDecimal("99.90"), 5, true));

        mockMvc.perform(post("/api/products")
                        .with(user("root").roles("ADMIN"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Mouse"));
    }

    private ProductResponse stub() {
        return new ProductResponse(1L, "Mouse", "desc", new BigDecimal("99.90"),
                5, true, true, LocalDateTime.now(), LocalDateTime.now());
    }
}
