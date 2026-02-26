package com.davivienda.controller;

import com.davivienda.dto.CardDTO;
import com.davivienda.dto.CreateCardRequest;
import com.davivienda.model.User;
import com.davivienda.security.UserPrincipal;
import com.davivienda.service.AuthService;
import com.davivienda.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del CardController.
 */
@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private CardController cardController;

    private UserPrincipal principal;
    private User user;
    private CreateCardRequest createRequest;

    @BeforeEach
    void setUp() {
        principal = new UserPrincipal(1L, "user@test.com");
        user = User.builder().id(1L).email("user@test.com").nombre("Usuario").build();
        createRequest = new CreateCardRequest();
        createRequest.setTitle("Nueva tarjeta");
        createRequest.setDescription("Descripción");
        createRequest.setPosition(0);
    }

    @Test
    @DisplayName("create: devuelve 201 y la tarjeta creada")
    void create_ok() {
        CardDTO dto = CardDTO.builder().id(1L).title("Nueva tarjeta").listId(20L).position(0).build();
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);
        when(cardService.create(eq(20L), eq(user), eq("Nueva tarjeta"), eq("Descripción"), eq(0), eq(null), eq(null)))
                .thenReturn(dto);

        ResponseEntity<CardDTO> result = cardController.create(principal, 20L, createRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("Nueva tarjeta");
    }

    @Test
    @DisplayName("list: devuelve 200 y la lista de tarjetas")
    void list_ok() {
        List<CardDTO> tarjetas = List.of(
                CardDTO.builder().id(1L).title("Tarea 1").listId(20L).build()
        );
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);
        when(cardService.findByListId(20L, user)).thenReturn(tarjetas);

        ResponseEntity<List<CardDTO>> result = cardController.list(principal, 20L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("delete: devuelve 204")
    void delete_ok() {
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);

        ResponseEntity<Void> result = cardController.delete(principal, 20L, 5L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(cardService).delete(5L, user);
    }
}
