package com.davivienda.service.impl;

import com.davivienda.dto.AuthResponse;
import com.davivienda.dto.LoginRequest;
import com.davivienda.dto.RegisterRequest;
import com.davivienda.exception.BadResourceRequestException;
import com.davivienda.model.User;
import com.davivienda.repository.UserRepository;
import com.davivienda.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de autenticación.
 * Se mockean UserRepository, PasswordEncoder y JwtUtil.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .password("encodedPassword")
                .nombre("Usuario Test")
                .role(User.Role.USER)
                .build();

        registerRequest = RegisterRequest.builder()
                .email("test@test.com")
                .password("password123")
                .nombre("Usuario Test")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("register: cuando el email no existe, crea usuario y devuelve AuthResponse con token")
    void register_ok() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyString(), any(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@test.com");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getRole()).isEqualTo("USER");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(user.getEmail(), user.getId(), "USER");
    }

    @Test
    @DisplayName("register: cuando el email ya existe, lanza BadResourceRequestException")
    void register_emailYaExiste_lanzaExcepcion() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(BadResourceRequestException.class)
                .hasMessageContaining("Ya existe un usuario");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("login: cuando email y contraseña son correctos, devuelve AuthResponse con token")
    void login_ok() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), any(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("login: cuando la contraseña es incorrecta, lanza BadResourceRequestException")
    void login_contraseñaIncorrecta_lanzaExcepcion() {
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadResourceRequestException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    @DisplayName("getCurrentUser: cuando el email existe, devuelve el usuario")
    void getCurrentUser_ok() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        User result = authService.getCurrentUser("test@test.com");

        assertThat(result).isEqualTo(user);
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("getCurrentUser: cuando el email no existe, lanza BadResourceRequestException")
    void getCurrentUser_noEncontrado_lanzaExcepcion() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getCurrentUser("noexiste@test.com"))
                .isInstanceOf(BadResourceRequestException.class)
                .hasMessageContaining("Usuario no encontrado");
    }
}
