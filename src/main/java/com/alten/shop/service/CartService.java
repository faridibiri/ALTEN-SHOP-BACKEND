package com.alten.shop.service;

import com.alten.shop.model.CartItem;
import com.alten.shop.model.User;
import com.alten.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    
    private final UserRepository userRepository;
    private final ProductService productService;
    
    public List<CartItem> getCurrentUserCart() {
        return getCurrentUser().getCart();
    }
    
    @Transactional
    public CartItem addToCart(CartItem item) {
        User user = getCurrentUser();
        item.setProduct(productService.getProduct(item.getProduct().getId()));
        user.getCart().add(item);
        userRepository.save(user);
        return item;
    }
    
    @Transactional
    public void removeFromCart(Long productId) {
        User user = getCurrentUser();
        user.getCart().removeIf(item -> item.getProduct().getId().equals(productId));
        userRepository.save(user);
    }
    
    @Transactional
    public CartItem updateQuantity(Long productId, Integer quantity) {
        User user = getCurrentUser();
        CartItem item = user.getCart().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));
        
        item.setQuantity(quantity);
        userRepository.save(user);
        return item;
    }
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}