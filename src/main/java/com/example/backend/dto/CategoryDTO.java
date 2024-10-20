package com.example.backend.dto;

import java.io.Serial;
import java.io.Serializable;

import com.example.backend.annotations.UniqueCategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "O nome da categoria não pode ser vazio")
    @Size(min = 3, max = 100, message = "O nome da categoria deve ter entre 3 e 100 caracteres")
    @UniqueCategoryName(message = "Uma categoria com este nome já existe")
    private String name;

    // Construtor padrão
    public CategoryDTO() {
    }

    // Construtor com todos os campos
    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Equals e HashCode baseados no id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDTO that = (CategoryDTO) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    // ToString para facilitar o debug
    @Override
    public String toString() {
        return "CategoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}