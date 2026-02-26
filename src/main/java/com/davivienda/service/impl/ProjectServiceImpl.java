package com.davivienda.service.impl;

import com.davivienda.dto.BoardListDTO;
import com.davivienda.dto.ProjectDTO;
import com.davivienda.exception.BadResourceRequestException;
import com.davivienda.exception.NoSuchResourceFoundException;
import com.davivienda.model.Project;
import com.davivienda.model.ProjectMember;
import com.davivienda.model.User;
import com.davivienda.repository.ProjectMemberRepository;
import com.davivienda.repository.ProjectRepository;
import com.davivienda.repository.UserRepository;
import com.davivienda.service.BoardListService;
import com.davivienda.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final BoardListService boardListService;

    @Override
    @Transactional
    public ProjectDTO create(User user, String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadResourceRequestException("El nombre del proyecto es requerido");
        }
        Project project = Project.builder()
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .createdBy(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        project = projectRepository.save(project);
        ProjectMember owner = ProjectMember.builder()
                .project(project)
                .user(user)
                .role(ProjectMember.Role.OWNER)
                .build();
        projectMemberRepository.save(owner);
        return toDTO(project, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> findByUser(User user) {
        List<ProjectMember> memberships = projectMemberRepository.findByUserWithProject(user);
        return memberships.stream()
                .map(pm -> toDTO(pm.getProject(), false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getById(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró el proyecto con ID: " + id));
        ensureMember(project, user);
        ProjectDTO dto = toDTO(project, false);
        dto.setLists(boardListService.findByProjectId(id, user));
        return dto;
    }

    @Override
    @Transactional
    public ProjectDTO update(Long id, User user, String name, String description) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró el proyecto con ID: " + id));
        ensureMember(project, user);
        if (name != null && !name.trim().isEmpty()) {
            project.setName(name.trim());
        }
        if (description != null) {
            project.setDescription(description.trim());
        }
        project.setUpdatedAt(LocalDateTime.now());
        project = projectRepository.save(project);
        return toDTO(project, false);
    }

    @Override
    @Transactional
    public void delete(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró el proyecto con ID: " + id));
        ensureOwner(project, user);
        projectMemberRepository.findByProjectOrderByRole(project).forEach(projectMemberRepository::delete);
        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public void addMember(Long projectId, User currentUser, String memberEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró el proyecto con ID: " + projectId));
        ensureOwner(project, currentUser);
        User newMember = userRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró un usuario con email: " + memberEmail));
        if (projectMemberRepository.existsByProjectAndUser(project, newMember)) {
            throw new BadResourceRequestException("El usuario ya es miembro del proyecto");
        }
        ProjectMember pm = ProjectMember.builder()
                .project(project)
                .user(newMember)
                .role(ProjectMember.Role.MEMBER)
                .build();
        projectMemberRepository.save(pm);
    }

    @Override
    @Transactional
    public void removeMember(Long projectId, Long userId, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró el proyecto con ID: " + projectId));
        ensureOwner(project, currentUser);
        User toRemove = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchResourceFoundException("No se encontró el usuario con ID: " + userId));
        ProjectMember pm = projectMemberRepository.findByProjectAndUser(project, toRemove)
                .orElseThrow(() -> new BadResourceRequestException("El usuario no es miembro del proyecto"));
        if (pm.getRole() == ProjectMember.Role.OWNER) {
            throw new BadResourceRequestException("No se puede eliminar al propietario del proyecto");
        }
        projectMemberRepository.delete(pm);
    }

    private void ensureMember(Project project, User user) {
        if (!projectMemberRepository.existsByProjectAndUser(project, user)) {
            throw new BadResourceRequestException("No tienes acceso a este proyecto");
        }
    }

    private void ensureOwner(Project project, User user) {
        ProjectMember pm = projectMemberRepository.findByProjectAndUser(project, user)
                .orElseThrow(() -> new BadResourceRequestException("No tienes acceso a este proyecto"));
        if (pm.getRole() != ProjectMember.Role.OWNER) {
            throw new BadResourceRequestException("Solo el propietario puede realizar esta acción");
        }
    }

    private ProjectDTO toDTO(Project p, boolean includeLists) {
        return ProjectDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .createdById(p.getCreatedBy() != null ? p.getCreatedBy().getId() : null)
                .createdByName(p.getCreatedBy() != null ? p.getCreatedBy().getNombre() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .lists(includeLists ? null : null)
                .build();
    }
}
