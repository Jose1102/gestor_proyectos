package com.davivienda.controller;

import com.davivienda.dto.CardDTO;
import com.davivienda.dto.CreateCardRequest;
import com.davivienda.dto.MoveCardRequest;
import com.davivienda.model.User;
import com.davivienda.security.UserPrincipal;
import com.davivienda.service.AuthService;
import com.davivienda.service.CardService;
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
@RequestMapping("/api/v1/lists/{listId}/cards")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Tarjetas", description = "Tareas dentro de una lista")
public class CardController {

    @Autowired
    private CardService cardService;
    @Autowired
    private AuthService authService;

    @PostMapping
    @Operation(summary = "Crear tarjeta", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardDTO> create(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long listId,
            @Valid @RequestBody CreateCardRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        CardDTO created = cardService.create(
                listId, user,
                request.getTitle(), request.getDescription(), request.getPosition(),
                request.getAssigneeId(), request.getDueDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "Listar tarjetas", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CardDTO>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long listId) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(cardService.findByListId(listId, user));
    }

    @PutMapping("/{cardId}")
    @Operation(summary = "Actualizar tarjeta", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardDTO> update(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long listId,
            @PathVariable Long cardId,
            @Valid @RequestBody CreateCardRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(cardService.update(
                cardId, user,
                request.getTitle(), request.getDescription(), request.getPosition(),
                request.getAssigneeId(), request.getDueDate()));
    }

    @PostMapping("/{cardId}/move")
    @Operation(summary = "Mover tarjeta", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CardDTO> move(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long listId,
            @PathVariable Long cardId,
            @Valid @RequestBody MoveCardRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(cardService.move(cardId, request.getTargetListId(), request.getNewPosition(), user));
    }

    @DeleteMapping("/{cardId}")
    @Operation(summary = "Eliminar tarjeta", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long listId,
            @PathVariable Long cardId) {
        User user = authService.getCurrentUser(principal.getEmail());
        cardService.delete(cardId, user);
        return ResponseEntity.noContent().build();
    }
}
