package com.davivienda.service;

import com.davivienda.dto.CardDTO;
import com.davivienda.model.Card;
import com.davivienda.model.User;

import java.time.LocalDate;
import java.util.List;

public interface CardService {

    CardDTO create(Long listId, User user, String title, String description, Integer position, Long assigneeId, LocalDate dueDate);

    List<CardDTO> findByListId(Long listId, User user);

    CardDTO update(Long cardId, User user, String title, String description, Integer position, Long assigneeId, LocalDate dueDate);

    CardDTO move(Long cardId, Long targetListId, Integer newPosition, User user);

    void delete(Long cardId, User user);
}
