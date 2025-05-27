package com.alten.shop.service;

import com.alten.shop.model.Product;
import com.alten.shop.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product product;
    
    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(99.99);
    }
    
    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        
        List<Product> result = productService.getAllProducts();
        
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test Product");
        verify(productRepository).findAll();
    }
    
    @Test
    void getProduct_WithValidId_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        Product result = productService.getProduct(1L);
        
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).findById(1L);
    }
    
    @Test
    void getProduct_WithInvalidId_ShouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        
        assertThatThrownBy(() -> productService.getProduct(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Product not found");
    }
    
    @Test
    void createProduct_ShouldReturnSavedProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        Product result = productService.createProduct(product);
        
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        verify(productRepository).save(product);
    }
    
    @Test
    void updateProduct_WithValidId_ShouldReturnUpdatedProduct() {
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        
        Product result = productService.updateProduct(1L, updatedProduct);
        
        assertThat(result).isNotNull();
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }
    
    @Test
    void deleteProduct_WithValidId_ShouldDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        
        productService.deleteProduct(1L);
        
        verify(productRepository).deleteById(1L);
    }
    
    @Test
    void deleteProduct_WithInvalidId_ShouldThrowException() {
        when(productRepository.existsById(1L)).thenReturn(false);
        
        assertThatThrownBy(() -> productService.deleteProduct(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Product not found");
    }
}