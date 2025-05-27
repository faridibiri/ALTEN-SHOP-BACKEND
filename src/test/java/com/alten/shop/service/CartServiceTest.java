package com.alten.shop.service;

import com.alten.shop.model.CartItem;
import com.alten.shop.model.Product;
import com.alten.shop.model.User;
import com.alten.shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ProductService productService;
    
    @InjectMocks
    private CartService cartService;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    private User user;
    private Product product;
    private CartItem cartItem;
    
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setCart(new ArrayList<>());
        
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        
        cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(1);
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
    }
    
    @Test
    void getCurrentUserCart_ShouldReturnUserCart() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        
        assertThat(cartService.getCurrentUserCart()).isEqualTo(user.getCart());
    }
    
    @Test
    void addToCart_ShouldAddItemToCart() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(productService.getProduct(1L)).thenReturn(product);
        
        CartItem result = cartService.addToCart(cartItem);
        
        assertThat(result).isEqualTo(cartItem);
        assertThat(user.getCart()).contains(cartItem);
        verify(userRepository).save(user);
    }
    
    @Test
    void removeFromCart_ShouldRemoveItemFromCart() {
        user.getCart().add(cartItem);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        
        cartService.removeFromCart(1L);
        
        assertThat(user.getCart()).isEmpty();
        verify(userRepository).save(user);
    }
    
    @Test
    void updateQuantity_ShouldUpdateItemQuantity() {
        user.getCart().add(cartItem);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        
        CartItem result = cartService.updateQuantity(1L, 2);
        
        assertThat(result.getQuantity()).isEqualTo(2);
        verify(userRepository).save(user);
    }
}