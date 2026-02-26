package com.davivienda.service.impl;

import com.davivienda.dto.BoardListDTO;
import com.davivienda.dto.CardDTO;
import com.davivienda.exception.BadResourceRequestException;
import com.davivienda.exception.NoSuchResourceFoundException;
import com.davivienda.model.BoardList;
import com.davivienda.model.Project;
import com.davivienda.model.User;
import com.davivienda.repository.BoardListRepository;
import com.davivienda.repository.ProjectMemberRepository;
import com.davivienda.repository.ProjectRepository;
import com.davivienda.service.BoardListService;
import com.davivienda.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardListServiceImpl implements BoardListService {

    @Autowired
    private BoardListRepository boardListRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private CardService cardService;

    @Override
    @Transactional
    public BoardListDTO create(Long projectId, User user, String title, Integer position) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró el proyecto con ID: " + projectId));
        ensureMember(project, user);
        if (title == null || title.trim().isEmpty()) {
            throw new BadResourceRequestException("El título de la lista es requerido");
        }
        if (position == null) {
            position = (int) boardListRepository.findByProjectOrderByPositionAsc(project).stream().count();
        }
        BoardList list = BoardList.builder()
                .title(title.trim())
                .position(position)
                .project(project)
                .build();
        list = boardListRepository.save(list);
        return toDTO(list, false, user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardListDTO> findByProjectId(Long projectId, User user) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró el proyecto con ID: " + projectId));
        ensureMember(project, user);
        List<BoardList> lists = boardListRepository.findByProjectOrderByPositionAsc(project);
        return lists.stream().map(l -> toDTO(l, true, user)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BoardListDTO update(Long listId, User user, String title, Integer position) {
        BoardList list = boardListRepository.findById(listId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la lista con ID: " + listId));
        ensureMember(list.getProject(), user);
        if (title != null && !title.trim().isEmpty()) {
            list.setTitle(title.trim());
        }
        if (position != null) {
            list.setPosition(position);
        }
        list = boardListRepository.save(list);
        return toDTO(list, false, user);
    }

    @Override
    @Transactional
    public void delete(Long listId, User user) {
        BoardList list = boardListRepository.findById(listId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la lista con ID: " + listId));
        ensureMember(list.getProject(), user);
        boardListRepository.delete(list);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardList getEntityById(Long id) {
        return boardListRepository.findById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró la lista con ID: " + id));
    }

    private void ensureMember(Project project, User user) {
        if (!projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new BadResourceRequestException("No tienes acceso a este proyecto");
        }
    }

    private BoardListDTO toDTO(BoardList l, boolean includeCards, User user) {
        List<CardDTO> cards = null;
        if (includeCards && user != null) {
            cards = cardService.findByListId(l.getId(), user);
        }
        return BoardListDTO.builder()
                .id(l.getId())
                .title(l.getTitle())
                .position(l.getPosition())
                .projectId(l.getProject() != null ? l.getProject().getId() : null)
                .cards(cards)
                .build();
    }
}
