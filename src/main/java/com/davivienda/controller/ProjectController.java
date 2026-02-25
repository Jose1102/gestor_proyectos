package com.davivienda.controller;

import com.davivienda.dto.AddMemberRequest;
import com.davivienda.dto.CreateProjectRequest;
import com.davivienda.dto.ProjectDTO;
import com.davivienda.model.User;
import com.davivienda.security.UserPrincipal;
import com.davivienda.service.AuthService;
import com.davivienda.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Proyectos", description = "Tableros tipo Trello - colaboración en proyectos")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final AuthService authService;

    @Operation(summary = "Crear proyecto", description = "Crea un tablero y te añade como propietario",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<ProjectDTO> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateProjectRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        ProjectDTO created = projectService.create(user, request.getName(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Mis proyectos", description = "Lista los proyectos donde participas",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> list(@AuthenticationPrincipal UserPrincipal principal) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(projectService.findByUser(user));
    }

    @Operation(summary = "Ver proyecto", description = "Obtiene un proyecto con sus listas y tarjetas",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(projectService.getById(id, user));
    }

    @Operation(summary = "Actualizar proyecto", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody CreateProjectRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(projectService.update(id, user, request.getName(), request.getDescription()));
    }

    @Operation(summary = "Eliminar proyecto", description = "Solo el propietario puede eliminar",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        User user = authService.getCurrentUser(principal.getEmail());
        projectService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Añadir miembro", description = "Añade un usuario al proyecto por email (solo propietario)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addMember(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        projectService.addMember(id, user, request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Eliminar miembro", description = "Quita un usuario del proyecto (solo propietario)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        User user = authService.getCurrentUser(principal.getEmail());
        projectService.removeMember(projectId, userId, user);
        return ResponseEntity.noContent().build();
    }
}
