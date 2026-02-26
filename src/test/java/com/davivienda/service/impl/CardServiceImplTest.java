package com.davivienda.service.impl;

import com.davivienda.dto.CardDTO;
import com.davivienda.exception.BadResourceRequestException;
import com.davivienda.exception.NoSuchResourceFoundException;
import com.davivienda.model.BoardList;
import com.davivienda.model.Card;
import com.davivienda.model.Project;
import com.davivienda.model.User;
import com.davivienda.repository.BoardListRepository;
import com.davivienda.repository.CardRepository;
import com.davivienda.repository.ProjectMemberRepository;
import com.davivienda.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de tarjetas.
 */
@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private BoardListRepository boardListRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Project project;
    private BoardList boardList;
    private Card card;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("user@test.com").nombre("Usuario").build();
        project = Project.builder().id(10L).name("Proyecto").createdBy(user).build();
        boardList = BoardList.builder().id(20L).title("Lista").project(project).build();
        card = Card.builder()
                .id(30L)
                .title("Tarea 1")
                .description("Desc")
                .position(0)
                .list(boardList)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("create: con lista existente y título válido crea tarjeta y devuelve DTO")
    void create_ok() {
        when(boardListRepository.findById(20L)).thenReturn(Optional.of(boardList));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(true);
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card c = inv.getArgument(0);
            c.setId(30L);
            return c;
        });

        CardDTO result = cardService.create(20L, user, "Tarea 1", "Descripción", 0, null, null);

        assertThat(result.getTitle()).isEqualTo("Tarea 1");
        assertThat(result.getListId()).isEqualTo(20L);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    @DisplayName("create: cuando la lista no existe lanza NoSuchResourceFoundException")
    void create_listaNoExiste_lanzaExcepcion() {
        when(boardListRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.create(999L, user, "Tarea", null, 0, null, null))
                .isInstanceOf(NoSuchResourceFoundException.class)
                .hasMessageContaining("No se encontró la lista");
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    @DisplayName("create: cuando el título está vacío lanza BadResourceRequestException")
    void create_tituloVacio_lanzaExcepcion() {
        when(boardListRepository.findById(20L)).thenReturn(Optional.of(boardList));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(true);

        assertThatThrownBy(() -> cardService.create(20L, user, "   ", null, 0, null, null))
                .isInstanceOf(BadResourceRequestException.class)
                .hasMessageContaining("título de la tarjeta");
    }

    @Test
    @DisplayName("findByListId: devuelve tarjetas de la lista")
    void findByListId_ok() {
        when(boardListRepository.findById(20L)).thenReturn(Optional.of(boardList));
        when(projectMemberRepository.existsByProjectAndUser(project, user)).thenReturn(true);
        when(cardRepository.findByListOrderByPositionAscWithDetails(boardList)).thenReturn(List.of(card));

        List<CardDTO> result = cardService.findByListId(20L, user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Tarea 1");
    }
}
