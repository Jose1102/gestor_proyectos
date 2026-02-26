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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/lists")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Listas", description = "Columnas del tablero")
public class BoardListController {

    @Autowired
    private BoardListService boardListService;
    @Autowired
    private AuthService authService;

    @PostMapping
    @Operation(summary = "Crear lista", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BoardListDTO> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @Valid @RequestBody CreateBoardListRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        BoardListDTO created = boardListService.create(projectId, user, request.getTitle(), request.getPosition());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar listas", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<BoardListDTO>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(boardListService.findByProjectId(projectId, user));
    }

    @PutMapping("/{listId}")
    @Operation(summary = "Actualizar lista", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<BoardListDTO> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @PathVariable Long listId,
            @Valid @RequestBody CreateBoardListRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(boardListService.update(listId, user, request.getTitle(), request.getPosition()));
    }

    @DeleteMapping("/{listId}")
    @Operation(summary = "Eliminar lista", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @PathVariable Long listId) {
        User user = authService.getCurrentUser(principal.getEmail());
        boardListService.delete(listId, user);
        return ResponseEntity.noContent().build();
    }
}
