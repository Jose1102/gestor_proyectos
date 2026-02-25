package com.davivienda.service;

import com.davivienda.dto.BoardListDTO;
import com.davivienda.model.BoardList;
import com.davivienda.model.User;

import java.util.List;

public interface BoardListService {

    BoardListDTO create(Long projectId, User user, String title, Integer position);

    List<BoardListDTO> findByProjectId(Long projectId, User user);

    BoardListDTO update(Long listId, User user, String title, Integer position);

    void delete(Long listId, User user);

    BoardList getEntityById(Long id);
}
