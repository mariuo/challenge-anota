package com.mcamelo.challengeanota.services;

import com.mcamelo.challengeanota.domain.category.Category;
import com.mcamelo.challengeanota.domain.category.CategoryDTO;
import com.mcamelo.challengeanota.domain.category.exceptions.CategoryNotFoundException;
import com.mcamelo.challengeanota.repositories.CategoryRepository;
import com.mcamelo.challengeanota.services.aws.AwsSnsService;
import com.mcamelo.challengeanota.services.aws.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private CategoryRepository repository;
    private final AwsSnsService snsService;

    public CategoryService(CategoryRepository repository, AwsSnsService snsService){
        this.repository = repository;
        this.snsService = snsService;
    }

    public Category create(CategoryDTO categoryDTO){
        Category newCategory = new Category(categoryDTO);
        this.repository.save(newCategory);
        this.snsService.publish(new MessageDTO(newCategory.toString()));
        return newCategory;
    }

    public List<Category> getAll() {
        return this.repository.findAll();
    }
    public Category update(String id, CategoryDTO categoryDTO) {
        Category category = this.repository.findById(id).orElseThrow(CategoryNotFoundException::new);

        if(!categoryDTO.title().isEmpty()) category.setTitle(categoryDTO.title());
        if(!categoryDTO.description().isEmpty()) category.setDescription(categoryDTO.description());

        this.repository.save(category);
        this.snsService.publish(new MessageDTO(category.toString()));

        return category;
    }

    public void delete(String id) {
        Category category = this.repository.findById(id).orElseThrow(CategoryNotFoundException::new);
        this.repository.delete(category);
    }
    public Optional<Category> getById(String id){
        return this.repository.findById(id);
    }
}
