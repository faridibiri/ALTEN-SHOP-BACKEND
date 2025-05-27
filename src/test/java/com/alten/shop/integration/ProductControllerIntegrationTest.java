package com.alten.shop.integration;

import com.alten.shop.dto.LoginRequest;
import com.alten.shop.dto.TokenResponse;
import com.alten.shop.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // Get admin token
        LoginRequest adminLogin = new LoginRequest("admin@admin.com", "adminpass");

        MvcResult result = mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andReturn();

        TokenResponse adminResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TokenResponse.class
        );
        adminToken = adminResponse.token();

        // Get regular user token
        LoginRequest userLogin = new LoginRequest("user@test.com", "userpass");

        result = mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)))
                .andReturn();

        TokenResponse userResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TokenResponse.class
        );
        userToken = userResponse.token();
    }

    @Test
    void getAllProducts_WithValidToken_ShouldReturnProducts() throws Exception {
        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createProduct_AsAdmin_ShouldCreateProduct() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(99.99);
        product.setQuantity(10);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void createProduct_AsUser_ShouldReturnForbidden() throws Exception {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(99.99);
        product.setQuantity(10);

        mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProduct_AsAdmin_ShouldUpdateProduct() throws Exception {
        // First create a product
        Product product = new Product();
        product.setName("Original Product");
        product.setPrice(99.99);
        product.setQuantity(10);

        MvcResult result = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andReturn();

        Product createdProduct = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Product.class
        );

        // Then update it
        createdProduct.setName("Updated Product");

        mockMvc.perform(patch("/api/products/" + createdProduct.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    void deleteProduct_AsAdmin_ShouldDeleteProduct() throws Exception {
        // First create a product
        Product product = new Product();
        product.setName("Product to Delete");
        product.setPrice(99.99);
        product.setQuantity(10);

        MvcResult result = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andReturn();

        Product createdProduct = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Product.class
        );

        // Then delete it
        mockMvc.perform(delete("/api/products/" + createdProduct.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // Verify it's deleted
        mockMvc.perform(get("/api/products/" + createdProduct.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}