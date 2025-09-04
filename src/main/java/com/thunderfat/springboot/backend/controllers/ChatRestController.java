package com.thunderfat.springboot.backend.controllers;

import com.thunderfat.springboot.backend.model.dto.ManualApiResponseDTO;
import com.thunderfat.springboot.backend.model.dto.ChatDTO;
import com.thunderfat.springboot.backend.model.service.IChatService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@Tag(name = "Chat", description = "Operations related to chat functionality between nutritionists and patients")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:8100"})
@RestController
@RequestMapping("/chat")
public class ChatRestController {
    
    private final IChatService chatService;

    public ChatRestController(IChatService chatService) {
        this.chatService = chatService;
    }
    
    @Operation(summary = "Get all chat messages", 
               description = "Retrieves all chat messages in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chat messages retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/todos")
    public ResponseEntity<ManualApiResponseDTO<List<ChatDTO>>> listarTodos() {
        try {
            List<ChatDTO> chats = chatService.listar();
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(
                    chats,
                    "Chat messages retrieved successfully"
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving chat messages: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Get chat message by ID", 
               description = "Retrieves a specific chat message by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Chat message retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Chat message not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ManualApiResponseDTO<ChatDTO>> buscarPorId(
            @Parameter(description = "Chat message ID") @PathVariable int id) {
        try {
            ChatDTO chat = chatService.buscarPorId(id);
            if (chat != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(
                        chat,
                        "Chat message retrieved successfully"
                    )
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(
                    "Chat message not found"
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving chat message: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Create chat message", 
               description = "Creates a new chat message between nutritionist and patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Chat message created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/save")
    public ResponseEntity<ManualApiResponseDTO<ChatDTO>> guardar(
            @Valid @RequestBody ChatDTO chatDTO) {
        try {
            chatService.insertar(chatDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ManualApiResponseDTO.success(
                    chatDTO,
                    "Chat message created successfully"
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ManualApiResponseDTO.error(
                    "Error creating chat message: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Delete chat message", 
               description = "Deletes a chat message by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Chat message deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Chat message not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ManualApiResponseDTO<Void>> eliminar(
            @Parameter(description = "Chat message ID") @PathVariable int id) {
        try {
            chatService.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ManualApiResponseDTO.success(
                    null,
                    "Chat message deleted successfully"
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error deleting chat message: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Get chat for patient", 
               description = "Retrieves the chat conversation for a specific patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient chat retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/paciente/{id_paciente}")
    public ResponseEntity<ManualApiResponseDTO<ChatDTO>> buscarPorPaciente(
            @Parameter(description = "Patient ID") @PathVariable int id_paciente) {
        try {
            ChatDTO chat = chatService.buscarPorPaciente(id_paciente);
            if (chat != null) {
                return ResponseEntity.ok(
                    ManualApiResponseDTO.success(
                        chat,
                        "Patient chat retrieved successfully"
                    )
                );
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ManualApiResponseDTO.error(
                    "Chat not found for patient"
                ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving patient chat: " + e.getMessage()
                ));
        }
    }
    
    @Operation(summary = "Get chats for nutritionist", 
               description = "Retrieves all chat conversations for a specific nutritionist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nutritionist chats retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Nutritionist not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/nutricionista/{id_nutricionista}")
    public ResponseEntity<ManualApiResponseDTO<List<ChatDTO>>> buscarPorNutricionista(
            @Parameter(description = "Nutritionist ID") @PathVariable int id_nutricionista) {
        try {
            List<ChatDTO> chats = chatService.buscarPorNutricionista(id_nutricionista);
            return ResponseEntity.ok(
                ManualApiResponseDTO.success(
                    chats,
                    "Nutritionist chats retrieved successfully"
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ManualApiResponseDTO.error(
                    "Error retrieving nutritionist chats: " + e.getMessage()
                ));
        }
    }
}
