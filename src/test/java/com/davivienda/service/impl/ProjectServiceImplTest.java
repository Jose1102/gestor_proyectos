package com.davivienda.service.impl;

import com.davivienda.dto.ProjectDTO;
import com.davivienda.exception.BadResourceRequestException;
import com.davivienda.exception.NoSuchResourceFoundException;
import com.davivienda.model.Project;
import com.davivienda.model.ProjectMember;
import com.davivienda.model.User;
import com.davivienda.repository.ProjectMemberRepository;
import com.davivienda.repository.ProjectRepository;
import com.davivienda.service.BoardListService;
import com.davivienda.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de proyectos.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private BoardListService boardListService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("owner@test.com")
                .password("encoded")
                .nombre("Owner")
                .role(User.Role.USER)
                .build();

        project = Project.builder()
                .id(10L)
                .name("Mi Proyecto")
                .description("Descripción")
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("create: con nombre válido crea proyecto y devuelve DTO")
    void create_ok() {
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        ProjectDTO result = projectService.create(user, "Mi Proyecto", "Descripción");

        assertThat(result.getName()).isEqualTo("Mi Proyecto");
        assertThat(result.getDescription()).isEqualTo("Descripción");
        verify(projectRepository).save(any(Project.class));
        verify(projectMemberRepository).save(any(ProjectMember.class));
    }

    @Test
    @DisplayName("create: con nombre vacío lanza BadResourceRequestException")
    void create_nombreVacio_lanzaExcepcion() {
        assertThatThrownBy(() -> projectService.create(user, "   ", "Desc"))
                .isInstanceOf(BadResourceRequestException.class)
                .hasMessageContaining("nombre del proyecto");

        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("findByUser: devuelve lista de proyectos del usuario")
    void findByUser_ok() {
        ProjectMember pm = ProjectMember.builder().project(project).user(user).role(ProjectMember.Role.OWNER).build();
        when(projectMemberRepository.findByUserWithProject(user)).thenReturn(List.of(pm));

        List<ProjectDTO> result = projectService.findByUser(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mi Proyecto");
        assertThat(result.get(0).getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("getById: cuando el proyecto no existe lanza NoSuchResourceFoundException")
    void getById_noExiste_lanzaExcepcion() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getById(999L, user))
                .isInstanceOf(NoSuchResourceFoundException.class)
                .hasMessageContaining("No se encontró el proyecto");
    }

    @Test
    @DisplayName("getById: cuando el usuario no es miembro lanza BadResourceRequestException")
    void getById_noEsMiembro_lanzaExcepcion() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(false);

        assertThatThrownBy(() -> projectService.getById(10L, user))
                .isInstanceOf(BadResourceRequestException.class)
                .hasMessageContaining("No tienes acceso");
    }
}
