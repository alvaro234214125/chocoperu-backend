package com.ecommerce.chocoperu.controller;

import com.ecommerce.chocoperu.dto.CategoryDto;
import com.ecommerce.chocoperu.entity.Category;
import com.ecommerce.chocoperu.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> listAll() {
        return categoryService.findAll().stream()
                .map(categoryService::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getOne(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(categoryService::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@Valid @RequestBody CategoryDto dto) {
        Category category = categoryService.fromDto(dto);
        Category saved = categoryService.save(category);
        return ResponseEntity.ok(categoryService.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @Valid @RequestBody CategoryDto dto) {
        return categoryService.findById(id).map(existing -> {
            Category updated = categoryService.fromDto(dto);
            updated.setId(id);
            return ResponseEntity.ok(categoryService.toDto(categoryService.save(updated)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (categoryService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
