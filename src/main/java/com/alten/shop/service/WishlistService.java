package com.alten.shop.service;

import com.alten.shop.model.Product;
import com.alten.shop.model.User;
import com.alten.shop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {
    
    private final UserRepository userRepository;
    private final ProductService productService;
    
    public List<Product> getCurrentUserWishlist() {
        return getCurrentUser().getWishlist();
    }
    
    @Transactional
    public void addToWishlist(Long productId) {
        User user = getCurrentUser();
        Product product = productService.getProduct(productId);
        if (!user.getWishlist().contains(product)) {
            user.getWishlist().add(product);
            userRepository.save(user);
        }
    }
    
    @Transactional
    public void removeFromWishlist(Long productId) {
        User user = getCurrentUser();
        user.getWishlist().removeIf(product -> product.getId().equals(productId));
        userRepository.save(user);
    }
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}