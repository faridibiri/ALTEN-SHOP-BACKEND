package com.alten.shop.service;

import com.alten.shop.dto.LoginRequest;
import com.alten.shop.dto.SignupRequest;
import com.alten.shop.model.User;
import com.alten.shop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    private AuthService authService;
    
    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    
    @BeforeEach
    void setUp() {
        signupRequest = new SignupRequest();
        signupRequest.setEmail("test@test.com");
        signupRequest.setPassword("password");
        signupRequest.setUsername("testuser");
        signupRequest.setFirstname("Test");
        
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@test.com");
        loginRequest.setPassword("password");
    }
    
    @Test
    void signup_WithNewEmail_ShouldCreateUser() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        
        authService.signup(signupRequest);
        
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void signup_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);
        
        assertThatThrownBy(() -> authService.signup(signupRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered");
    }
    
    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken(authentication)).thenReturn("token");
        
        String token = authService.login(loginRequest);
        
        assertThat(token).isEqualTo("token");
    }
    
    @Test
    void isAdmin_WithAdminEmail_ShouldReturnTrue() {
        boolean isAdmin = authService.isAdmin("admin@admin.com");
        assertThat(isAdmin).isTrue();
    }
    
    @Test
    void isAdmin_WithNonAdminEmail_ShouldReturnFalse() {
        boolean isAdmin = authService.isAdmin("user@test.com");
        assertThat(isAdmin).isFalse();
    }
}