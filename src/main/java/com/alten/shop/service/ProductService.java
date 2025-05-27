package com.alten.shop.service;

import com.alten.shop.model.Product;
import com.alten.shop.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }
    
    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existingProduct = getProduct(id);
        
        if (product.getCode() != null) existingProduct.setCode(product.getCode());
        if (product.getName() != null) existingProduct.setName(product.getName());
        if (product.getDescription() != null) existingProduct.setDescription(product.getDescription());
        if (product.getImage() != null) existingProduct.setImage(product.getImage());
        if (product.getCategory() != null) existingProduct.setCategory(product.getCategory());
        if (product.getPrice() != null) existingProduct.setPrice(product.getPrice());
        if (product.getQuantity() != null) existingProduct.setQuantity(product.getQuantity());
        if (product.getInternalReference() != null) existingProduct.setInternalReference(product.getInternalReference());
        if (product.getShellId() != null) existingProduct.setShellId(product.getShellId());
        if (product.getInventoryStatus() != null) existingProduct.setInventoryStatus(product.getInventoryStatus());
        if (product.getRating() != null) existingProduct.setRating(product.getRating());
        
        return productRepository.save(existingProduct);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }
}