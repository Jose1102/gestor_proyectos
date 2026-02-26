package com.davivienda.service.impl;

import com.davivienda.dto.BoardListDTO;
import com.davivienda.exception.BadResourceRequestException;
import com.davivienda.exception.NoSuchResourceFoundException;
import com.davivienda.model.BoardList;
import com.davivienda.model.Project;
import com.davivienda.model.User;
import com.davivienda.repository.BoardListRepository;
import com.davivienda.repository.ProjectMemberRepository;
import com.davivienda.repository.ProjectRepository;
import com.davivienda.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

/**
 * Pruebas unitarias del servicio de listas (columnas del tablero).
 */
@ExtendWith(MockitoExtension.class)
class BoardListServiceImplTest {

    @Mock
    private BoardListRepository boardListRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private BoardListServiceImpl boardListService;

    private User user;
    private Project project;
    private BoardList boardList;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@test.com").nombre("Usuario").role(User.Role.USER).build();
        project = Project.builder().id(10L).name("Proyecto").createdBy(user).build();
        boardList = BoardList.builder()
                .id(20L)
                .title("Por hacer")
                .position(0)
                .project(project)
                .build();
    }

    @Test
    @DisplayName("create: con proyecto existente y título válido crea lista y devuelve DTO")
    void create_ok() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(true);
        when(boardListRepository.save(any(BoardList.class))).thenAnswer(inv -> {
            BoardList bl = inv.getArgument(0);
            bl.setId(20L);
            return bl;
        });

        BoardListDTO result = boardListService.create(10L, user, "Por hacer", 0);

        assertThat(result.getTitle()).isEqualTo("Por hacer");
        assertThat(result.getPosition()).isEqualTo(0);
        assertThat(result.getProjectId()).isEqualTo(10L);
        verify(boardListRepository).save(any(BoardList.class));
    }

    @Test
    @DisplayName("create: cuando el proyecto no existe lanza NoSuchResourceFoundException")
    void create_proyectoNoExiste_lanzaExcepcion() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> boardListService.create(999L, user, "Lista", 0))
                .isInstanceOf(NoSuchResourceFoundException.class)
                .hasMessageContaining("No se encontró el proyecto");
        verify(boardListRepository, never()).save(any(BoardList.class));
    }

    @Test
    @DisplayName("create: cuando el título está vacío lanza BadResourceRequestException")
    void create_tituloVacio_lanzaExcepcion() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(true);

        assertThatThrownBy(() -> boardListService.create(10L, user, "   ", 0))
                .isInstanceOf(BadResourceRequestException.class)
                .hasMessageContaining("título de la lista");
    }

    @Test
    @DisplayName("findByProjectId: devuelve listas del proyecto")
    void findByProjectId_ok() {
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(true);
        when(boardListRepository.findByProjectOrderByPositionAsc(project)).thenReturn(List.of(boardList));
        when(cardService.findByListId(20L, user)).thenReturn(List.of());

        List<BoardListDTO> result = boardListService.findByProjectId(10L, user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Por hacer");
    }
}
