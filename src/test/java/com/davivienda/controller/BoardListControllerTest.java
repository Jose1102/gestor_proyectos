package com.davivienda.controller;

import com.davivienda.dto.BoardListDTO;
import com.davivienda.dto.CreateBoardListRequest;
import com.davivienda.model.User;
import com.davivienda.security.UserPrincipal;
import com.davivienda.service.AuthService;
import com.davivienda.service.BoardListService;
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
 * Pruebas unitarias del BoardListController.
 */
@ExtendWith(MockitoExtension.class)
class BoardListControllerTest {

    @Mock
    private BoardListService boardListService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private BoardListController boardListController;

    private UserPrincipal principal;
    private User user;
    private CreateBoardListRequest createRequest;

    @BeforeEach
    void setUp() {
        principal = new UserPrincipal(1L, "user@test.com");
        user = User.builder().id(1L).email("user@test.com").nombre("Usuario").build();
        createRequest = new CreateBoardListRequest();
        createRequest.setTitle("Por hacer");
        createRequest.setPosition(0);
    }

    @Test
    @DisplayName("create: devuelve 201 y la lista creada")
    void create_ok() {
        BoardListDTO dto = BoardListDTO.builder().id(1L).title("Por hacer").position(0).projectId(10L).build();
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);
        when(boardListService.create(eq(10L), eq(user), eq("Por hacer"), eq(0))).thenReturn(dto);

        ResponseEntity<BoardListDTO> result = boardListController.create(principal, 10L, createRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getTitle()).isEqualTo("Por hacer");
    }

    @Test
    @DisplayName("list: devuelve 200 y la lista de listas del proyecto")
    void list_ok() {
        List<BoardListDTO> listas = List.of(
                BoardListDTO.builder().id(1L).title("Por hacer").projectId(10L).build()
        );
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);
        when(boardListService.findByProjectId(10L, user)).thenReturn(listas);

        ResponseEntity<List<BoardListDTO>> result = boardListController.list(principal, 10L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("delete: devuelve 204")
    void delete_ok() {
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);

        ResponseEntity<Void> result = boardListController.delete(principal, 10L, 5L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(boardListService).delete(5L, user);
    }
}
