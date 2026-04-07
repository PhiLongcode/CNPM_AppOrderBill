package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.Category;
import com.giadinh.apporderbill.catalog.repository.CategoryRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageCategoryInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageCategoryOutput;
import com.giadinh.apporderbill.shared.error.DomainException;
import com.giadinh.apporderbill.shared.error.ErrorCode;

import java.util.Optional;

public class ManageCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public ManageCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ManageCategoryOutput create(ManageCategoryInput input) {
        if (categoryRepository.findByName(input.getName()).isPresent()) {
            throw new DomainException(ErrorCode.CATEGORY_NAME_DUPLICATE);
        }

        Category newCategory = new Category(0, input.getName(), input.getDescription());
        categoryRepository.save(newCategory);
        return new ManageCategoryOutput(newCategory.getId());
    }

    public ManageCategoryOutput update(int categoryId, ManageCategoryInput input) {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(categoryId);
        if (existingCategoryOptional.isEmpty()) {
            throw new DomainException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        Category existingCategory = existingCategoryOptional.get();

        Optional<Category> categoryWithSameName = categoryRepository.findByName(input.getName());
        if (categoryWithSameName.isPresent() && categoryWithSameName.get().getId() != categoryId) {
            throw new DomainException(ErrorCode.CATEGORY_NAME_DUPLICATE);
        }

        existingCategory.setName(input.getName());
        existingCategory.setDescription(input.getDescription());

        categoryRepository.save(existingCategory);
        return new ManageCategoryOutput(categoryId);
    }

    public void delete(int categoryId) {
        categoryRepository.delete(categoryId);
    }
}
