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

    @PostMapping
    @Operation(summary = "Crear proyecto", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProjectDTO> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateProjectRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        ProjectDTO created = projectService.create(user, request.getName(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Mis proyectos", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ProjectDTO>> list(@AuthenticationPrincipal UserPrincipal principal) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(projectService.findByUser(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver proyecto", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProjectDTO> getById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(projectService.getById(id, user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proyecto", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProjectDTO> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody CreateProjectRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(projectService.update(id, user, request.getName(), request.getDescription()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proyecto", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        User user = authService.getCurrentUser(principal.getEmail());
        projectService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    @Operation(summary = "Añadir miembro", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> addMember(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody AddMemberRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        projectService.addMember(id, user, request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Eliminar miembro", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        User user = authService.getCurrentUser(principal.getEmail());
        projectService.removeMember(projectId, userId, user);
        return ResponseEntity.noContent().build();
    }
}
