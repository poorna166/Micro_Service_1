package com.techcommerce.admin_service.controller;

import com.techcommerce.admin_service.dto.CatalogDto;
import com.techcommerce.admin_service.service.AdminCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/catalogs")
@Tag(name = "Admin Catalogs", description = "Admin operations for managing catalogs")
public class AdminCatalogController {

    private final AdminCatalogService service;

    public AdminCatalogController(AdminCatalogService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new catalog")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Catalog created",
                    content = @Content(schema = @Schema(implementation = CatalogDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CatalogDto dto) {
        return ResponseEntity.status(201).body(service.createCatalog(dto));
    }

    @Operation(summary = "Get catalog by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catalog found",
                    content = @Content(schema = @Schema(implementation = CatalogDto.class))),
            @ApiResponse(responseCode = "404", description = "Catalog not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCatalog(id));
    }

    @Operation(summary = "List all catalogs")
    @ApiResponse(responseCode = "200", description = "Catalog list retrieved")
    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(service.listCatalogs());
    }
}
