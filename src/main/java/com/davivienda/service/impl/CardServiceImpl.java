package com.davivienda.service.impl;

import com.davivienda.dto.CardDTO;
import com.davivienda.exception.BadResourceRequestException;
import com.davivienda.exception.NoSuchResourceFoundException;
import com.davivienda.model.BoardList;
import com.davivienda.model.Card;
import com.davivienda.model.User;
import com.davivienda.repository.BoardListRepository;
import com.davivienda.repository.CardRepository;
import com.davivienda.repository.ProjectMemberRepository;
import com.davivienda.repository.UserRepository;
import com.davivienda.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final BoardListRepository boardListRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CardDTO create(Long listId, User user, String title, String description, Integer position, Long assigneeId, LocalDate dueDate) {
        BoardList list = boardListRepository.findById(listId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la lista con ID: " + listId));
        ensureMember(list.getProject(), user);
        if (title == null || title.trim().isEmpty()) {
            throw new BadResourceRequestException("El título de la tarjeta es requerido");
        }
        if (position == null) {
            long count = cardRepository.findByListOrderByPositionAsc(list).size();
            position = (int) count;
        }
        User assignee = null;
        if (assigneeId != null) {
            assignee = userRepository.findById(assigneeId).orElse(null);
        }
        Card card = Card.builder()
                .title(title.trim())
                .description(description != null ? description.trim() : null)
                .position(position)
                .list(list)
                .assignee(assignee)
                .dueDate(dueDate)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        card = cardRepository.save(card);
        return toDTO(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDTO> findByListId(Long listId, User user) {
        BoardList list = boardListRepository.findById(listId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la lista con ID: " + listId));
        if (user != null) {
            ensureMember(list.getProject(), user);
        }
        List<Card> cards = cardRepository.findByListOrderByPositionAscWithDetails(list);
        return cards.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CardDTO update(Long cardId, User user, String title, String description, Integer position, Long assigneeId, LocalDate dueDate) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la tarjeta con ID: " + cardId));
        ensureMember(card.getList().getProject(), user);
        if (title != null && !title.trim().isEmpty()) {
            card.setTitle(title.trim());
        }
        if (description != null) {
            card.setDescription(description.trim());
        }
        if (position != null) {
            card.setPosition(position);
        }
        if (assigneeId != null) {
            if (assigneeId == 0) {
                card.setAssignee(null);
            } else {
                card.setAssignee(userRepository.findById(assigneeId).orElse(null));
            }
        }
        if (dueDate != null) {
            card.setDueDate(dueDate);
        }
        card.setUpdatedAt(LocalDateTime.now());
        card = cardRepository.save(card);
        return toDTO(card);
    }

    @Override
    @Transactional
    public CardDTO move(Long cardId, Long targetListId, Integer newPosition, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la tarjeta con ID: " + cardId));
        BoardList targetList = boardListRepository.findById(targetListId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la lista con ID: " + targetListId));
        ensureMember(card.getList().getProject(), user);
        ensureMember(targetList.getProject(), user);
        if (!card.getList().getProject().getId().equals(targetList.getProject().getId())) {
            throw new BadResourceRequestException("Solo se puede mover la tarjeta entre listas del mismo proyecto");
        }
        card.setList(targetList);
        if (newPosition != null) {
            card.setPosition(newPosition);
        }
        card.setUpdatedAt(LocalDateTime.now());
        card = cardRepository.save(card);
        return toDTO(card);
    }

    @Override
    @Transactional
    public void delete(Long cardId, User user) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la tarjeta con ID: " + cardId));
        ensureMember(card.getList().getProject(), user);
        cardRepository.delete(card);
    }

    private void ensureMember(com.davivienda.model.Project project, User user) {
        if (user == null) return;
        if (!projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new BadResourceRequestException("No tienes acceso a este proyecto");
        }
    }

    private CardDTO toDTO(Card c) {
        return CardDTO.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .position(c.getPosition())
                .listId(c.getList() != null ? c.getList().getId() : null)
                .assigneeId(c.getAssignee() != null ? c.getAssignee().getId() : null)
                .assigneeName(c.getAssignee() != null ? c.getAssignee().getNombre() : null)
                .dueDate(c.getDueDate())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
