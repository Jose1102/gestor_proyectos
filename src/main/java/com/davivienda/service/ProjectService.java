package com.davivienda.service;

import com.davivienda.dto.ProjectDTO;
import com.davivienda.model.Project;
import com.davivienda.model.User;

import java.util.List;

public interface ProjectService {

    ProjectDTO create(User user, String name, String description);

    List<ProjectDTO> findByUser(User user);

    ProjectDTO getById(Long id, User user);

    ProjectDTO update(Long id, User user, String name, String description);

    void delete(Long id, User user);

    void addMember(Long projectId, User currentUser, String memberEmail);

    void removeMember(Long projectId, Long userId, User currentUser);
}
