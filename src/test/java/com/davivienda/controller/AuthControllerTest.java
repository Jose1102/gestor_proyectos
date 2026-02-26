package com.davivienda.controller;

import com.davivienda.dto.AuthResponse;
import com.davivienda.dto.LoginRequest;
import com.davivienda.dto.RegisterRequest;
import com.davivienda.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del AuthController.
 * Se mockea AuthService y se llama directamente a los métodos del controlador.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("register: devuelve 201 y el AuthResponse del servicio")
    void register_ok() {
        RegisterRequest request = RegisterRequest.builder()
                .email("nuevo@test.com")
                .password("password123")
                .nombre("Nuevo Usuario")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .type("Bearer")
                .userId(1L)
                .email("nuevo@test.com")
                .role("USER")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.register(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getToken()).isEqualTo("jwt-token");
        assertThat(result.getBody().getEmail()).isEqualTo("nuevo@test.com");
    }

    @Test
    @DisplayName("login: devuelve 200 y el AuthResponse del servicio")
    void login_ok() {
        LoginRequest request = LoginRequest.builder()
                .email("user@test.com")
                .password("password123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .type("Bearer")
                .userId(1L)
                .email("user@test.com")
                .role("USER")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        ResponseEntity<AuthResponse> result = authController.login(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getToken()).isEqualTo("jwt-token");
    }
}
