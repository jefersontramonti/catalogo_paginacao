package com.example.backend.services;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.entities.Category;
import com.example.backend.repositories.CategoryRepository;
import com.example.backend.services.exceptions.DatabaseException;
import com.example.backend.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(String name, Pageable pageable) {
        Specification<Category> spec = Specification.where(null);
        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        Page<Category> page = repository.findAll(spec, pageable);
        return page.map(this::convertToDTO);
    }

    @Cacheable(value = "categories", key = "#id")
    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        return repository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found. Id: " + id));
    }

    @CacheEvict(value = "categories", allEntries = true)
    @Transactional
    public CategoryDTO create(CategoryDTO dto) {
        log.info("Attempting to create a new category: {}", dto.getName());
        try {
            Category entity = new Category(dto.getName());
            entity = repository.save(entity);
            log.info("Category created successfully with ID: {}", entity.getId());
            return convertToDTO(entity);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create category due to integrity violation", e);
            throw new DatabaseException("Integrity violation");
        }
    }

    @CacheEvict(value = "categories", key = "#id")
    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto) {
        try {
            Category entity = repository.getReferenceById(id);
            entity.setName(dto.getName());
            entity = repository.save(entity);
            return convertToDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Category not found. Id: " + id);
        }
    }

    @CacheEvict(value = "categories", key = "#id")
    @Transactional
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Category not found. Id: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private CategoryDTO convertToDTO(Category entity) {
        return new CategoryDTO(entity.getId(), entity.getName());
    }
}