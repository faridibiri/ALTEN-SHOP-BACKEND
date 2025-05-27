package com.alten.shop.service;

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
class WishlistServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ProductService productService;
    
    @InjectMocks
    private WishlistService wishlistService;
    
    @Mock
    private SecurityContext securityContext;
    
    @Mock
    private Authentication authentication;
    
    private User user;
    private Product product;
    
    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setWishlist(new ArrayList<>());
        
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@test.com");
    }
    
    @Test
    void getCurrentUserWishlist_ShouldReturnUserWishlist() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        
        assertThat(wishlistService.getCurrentUserWishlist()).isEqualTo(user.getWishlist());
    }
    
    @Test
    void addToWishlist_ShouldAddProductToWishlist() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(productService.getProduct(1L)).thenReturn(product);
        
        wishlistService.addToWishlist(1L);
        
        assertThat(user.getWishlist()).contains(product);
        verify(userRepository).save(user);
    }
    
    @Test
    void removeFromWishlist_ShouldRemoveProductFromWishlist() {
        user.getWishlist().add(product);
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        
        wishlistService.removeFromWishlist(1L);
        
        assertThat(user.getWishlist()).isEmpty();
        verify(userRepository).save(user);
    }
}