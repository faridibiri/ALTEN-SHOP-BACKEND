package com.alten.shop.integration;

import com.alten.shop.dto.LoginRequest;
import com.alten.shop.dto.TokenResponse;
import com.alten.shop.model.CartItem;
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
public class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private Product testProduct;

    @BeforeEach
    void setUp() throws Exception {
        // Get user token
        LoginRequest userLogin = new LoginRequest();
        userLogin.setEmail("user@test.com");
        userLogin.setPassword("userpass");

        MvcResult result = mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLogin)))
                .andReturn();

        TokenResponse userResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TokenResponse.class
        );
        userToken = userResponse.getToken();

        // Create a test product
        Product product = new Product();
        product.setName("Cart Test Product");
        product.setPrice(99.99);
        product.setQuantity(10);

        result = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + getAdminToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andReturn();

        testProduct = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Product.class
        );
    }

    @Test
    void addToCart_WithValidProduct_ShouldAddToCart() throws Exception {
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(1);

        mockMvc.perform(post("/api/cart/items")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id").value(testProduct.getId()));
    }

    @Test
    void getCart_ShouldReturnUserCart() throws Exception {
        mockMvc.perform(get("/api/cart")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateCartItemQuantity_WithValidQuantity_ShouldUpdateQuantity() throws Exception {
        // First add item to cart
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(1);

        mockMvc.perform(post("/api/cart/items")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem)));

        // Then update quantity
        mockMvc.perform(patch("/api/cart/items/" + testProduct.getId())
                .header("Authorization", "Bearer " + userToken)
                .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    void removeFromCart_ShouldRemoveItem() throws Exception {
        // First add item to cart
        CartItem cartItem = new CartItem();
        cartItem.setProduct(testProduct);
        cartItem.setQuantity(1);

        mockMvc.perform(post("/api/cart/items")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartItem)));

        // Then remove it
        mockMvc.perform(delete("/api/cart/items/" + testProduct.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    private String getAdminToken() throws Exception {
        LoginRequest adminLogin = new LoginRequest();
        adminLogin.setEmail("admin@admin.com");
        adminLogin.setPassword("adminpass");

        MvcResult result = mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andReturn();

        TokenResponse adminResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TokenResponse.class
        );
        return adminResponse.getToken();
    }
}