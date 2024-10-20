package com.example.backend;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.entities.Category;
import com.example.backend.repositories.CategoryRepository;
import com.example.backend.services.CategoryService;
import com.example.backend.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void createCategoryShouldReturnDTOWhenSuccessful() {
        // Arrange
        CategoryDTO dto = new CategoryDTO(null, "Test Category");
        Category savedCategory = new Category("Test Category");
        setPrivateField(savedCategory, "id", 1L);

        when(repository.save(any(Category.class))).thenReturn(savedCategory);

        // Act
        CategoryDTO result = service.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Category", result.getName());
        verify(repository, times(1)).save(any(Category.class));
    }

    @Test
    void findByIdShouldReturnCategoryDTOWhenIdExists() {
        // Arrange
        Long id = 1L;
        Category category = new Category("Test Category");
        setPrivateField(category, "id", id);
        when(repository.findById(id)).thenReturn(Optional.of(category));

        // Act
        CategoryDTO result = service.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Test Category", result.getName());
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findById(id));
    }

    @Test
    void deleteShouldCallRepositoryDeleteWhenIdExists() {
        // Arrange
        Long id = 1L;
        doNothing().when(repository).deleteById(id);

        // Act
        assertDoesNotThrow(() -> service.delete(id));

        // Assert
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        // Arrange
        Long id = 1L;
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(id);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.delete(id));
    }

    // Helper method to set private fields using reflection
    private void setPrivateField(Object object, String fieldName, Object fieldValue) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error setting private field", e);
        }
    }
}