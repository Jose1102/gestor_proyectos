package com.davivienda.controller;

import com.davivienda.dto.CreateProjectRequest;
import com.davivienda.dto.ProjectDTO;
import com.davivienda.model.User;
import com.davivienda.security.UserPrincipal;
import com.davivienda.service.AuthService;
import com.davivienda.service.ProjectService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del ProjectController.
 * Se mockean AuthService y ProjectService; el principal (usuario logueado) se simula con UserPrincipal.
 */
@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ProjectController projectController;

    private UserPrincipal principal;
    private User user;
    private CreateProjectRequest createRequest;

    @BeforeEach
    void setUp() {
        principal = new UserPrincipal(1L, "user@test.com");
        user = User.builder().id(1L).email("user@test.com").nombre("Usuario").role(User.Role.USER).build();
        createRequest = new CreateProjectRequest();
        createRequest.setName("Nuevo Proyecto");
        createRequest.setDescription("Descripción");
    }

    @Test
    @DisplayName("create: devuelve 201 y el proyecto creado")
    void create_ok() {
        ProjectDTO dto = ProjectDTO.builder().id(1L).name("Nuevo Proyecto").description("Descripción").build();
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);
        when(projectService.create(eq(user), eq("Nuevo Proyecto"), eq("Descripción"))).thenReturn(dto);

        ResponseEntity<ProjectDTO> result = projectController.create(principal, createRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getName()).isEqualTo("Nuevo Proyecto");
    }

    @Test
    @DisplayName("list: devuelve 200 y la lista de proyectos del usuario")
    void list_ok() {
        List<ProjectDTO> proyectos = List.of(
                ProjectDTO.builder().id(1L).name("Proyecto 1").build(),
                ProjectDTO.builder().id(2L).name("Proyecto 2").build()
        );
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);
        when(projectService.findByUser(user)).thenReturn(proyectos);

        ResponseEntity<List<ProjectDTO>> result = projectController.list(principal);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).hasSize(2);
    }

    @Test
    @DisplayName("getById: devuelve 200 y el proyecto")
    void getById_ok() {
        ProjectDTO dto = ProjectDTO.builder().id(10L).name("Mi Proyecto").build();
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);
        when(projectService.getById(10L, user)).thenReturn(dto);

        ResponseEntity<ProjectDTO> result = projectController.getById(principal, 10L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("delete: devuelve 204 sin cuerpo")
    void delete_ok() {
        when(authService.getCurrentUser("user@test.com")).thenReturn(user);

        ResponseEntity<Void> result = projectController.delete(principal, 10L);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(projectService).delete(10L, user);
    }
}
