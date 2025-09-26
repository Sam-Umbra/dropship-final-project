package br.dev.kajosama.dropship.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.dev.kajosama.dropship.domain.model.entities.Category;
import br.dev.kajosama.dropship.domain.repositories.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepo;

    public boolean existsById(Long id) {
        return categoryRepo.existsById(id);
    }

    public void deleteCategoryById(Long id) {
        if(!existsById(id)) {
            throw new EntityNotFoundException("Category with ID: {" + id + "} NOT FOUND");
        }
        categoryRepo.deleteById(id);
    }

    public Category getCategoryById(Long id) {
        return categoryRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Category with ID: {" + id + "} NOT FOUND"));
    }

    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }

    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    public List<Category> getParentCategories() {
        return categoryRepo.findAll().stream()
            .filter(cat -> cat.getParentCategory() == null)
            .toList();
    }

}
