package com.davivienda.controller;

import com.davivienda.dto.BoardListDTO;
import com.davivienda.dto.CreateBoardListRequest;
import com.davivienda.model.User;
import com.davivienda.security.UserPrincipal;
import com.davivienda.service.AuthService;
import com.davivienda.service.BoardListService;
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
@RequestMapping("/api/v1/projects/{projectId}/lists")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Listas", description = "Columnas del tablero (Por hacer, En progreso, Hecho)")
@RequiredArgsConstructor
public class BoardListController {

    private final BoardListService boardListService;
    private final AuthService authService;

    @Operation(summary = "Crear lista", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<BoardListDTO> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @Valid @RequestBody CreateBoardListRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        BoardListDTO created = boardListService.create(projectId, user, request.getTitle(), request.getPosition());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Listar columnas", description = "Obtiene las listas del proyecto con sus tarjetas",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<BoardListDTO>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(boardListService.findByProjectId(projectId, user));
    }

    @Operation(summary = "Actualizar lista", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{listId}")
    public ResponseEntity<BoardListDTO> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @PathVariable Long listId,
            @Valid @RequestBody CreateBoardListRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(boardListService.update(listId, user, request.getTitle(), request.getPosition()));
    }

    @Operation(summary = "Eliminar lista", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @PathVariable Long listId) {
        User user = authService.getCurrentUser(principal.getEmail());
        boardListService.delete(listId, user);
        return ResponseEntity.noContent().build();
    }
}
