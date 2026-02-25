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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lists/{listId}/cards")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Tarjetas", description = "Tareas dentro de una lista")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final AuthService authService;

    @Operation(summary = "Crear tarjeta", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
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

    @Operation(summary = "Listar tarjetas", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<CardDTO>> list(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long listId) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(cardService.findByListId(listId, user));
    }

    @Operation(summary = "Actualizar tarjeta", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{cardId}")
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

    @Operation(summary = "Mover tarjeta", description = "Mueve la tarjeta a otra lista del mismo proyecto",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{cardId}/move")
    public ResponseEntity<CardDTO> move(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long listId,
            @PathVariable Long cardId,
            @Valid @RequestBody MoveCardRequest request) {
        User user = authService.getCurrentUser(principal.getEmail());
        return ResponseEntity.ok(cardService.move(cardId, request.getTargetListId(), request.getNewPosition(), user));
    }

    @Operation(summary = "Eliminar tarjeta", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long listId,
            @PathVariable Long cardId) {
        User user = authService.getCurrentUser(principal.getEmail());
        cardService.delete(cardId, user);
        return ResponseEntity.noContent().build();
    }
}
