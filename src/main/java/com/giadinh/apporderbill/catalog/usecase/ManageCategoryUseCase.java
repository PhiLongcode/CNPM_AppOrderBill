package com.giadinh.apporderbill.catalog.usecase;

import com.giadinh.apporderbill.catalog.model.Category;
import com.giadinh.apporderbill.catalog.repository.CategoryRepository;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageCategoryInput;
import com.giadinh.apporderbill.catalog.usecase.dto.ManageCategoryOutput;

import java.util.Optional;

public class ManageCategoryUseCase {

    private final CategoryRepository categoryRepository;

    public ManageCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public ManageCategoryOutput create(ManageCategoryInput input) {
        if (categoryRepository.findByName(input.getName()).isPresent()) {
            return new ManageCategoryOutput(false, "Tên danh mục đã tồn tại.", 0);
        }

        Category newCategory = new Category(0, input.getName(), input.getDescription()); // ID sẽ được DB tự động tạo
        categoryRepository.save(newCategory);
        return new ManageCategoryOutput(true, "Tạo danh mục thành công.", newCategory.getId());
    }

    public ManageCategoryOutput update(int categoryId, ManageCategoryInput input) {
        Optional<Category> existingCategoryOptional = categoryRepository.findById(categoryId);
        if (existingCategoryOptional.isEmpty()) {
            return new ManageCategoryOutput(false, "Danh mục không tồn tại.", categoryId);
        }
        Category existingCategory = existingCategoryOptional.get();

        // Kiểm tra trùng tên danh mục (trừ chính nó)
        Optional<Category> categoryWithSameName = categoryRepository.findByName(input.getName());
        if (categoryWithSameName.isPresent() && categoryWithSameName.get().getId() != categoryId) {
            return new ManageCategoryOutput(false, "Tên danh mục đã tồn tại bởi danh mục khác.", categoryId);
        }

        existingCategory.setName(input.getName());
        existingCategory.setDescription(input.getDescription());

        categoryRepository.save(existingCategory);
        return new ManageCategoryOutput(true, "Cập nhật danh mục thành công.", categoryId);
    }

    public void delete(int categoryId) {
        categoryRepository.delete(categoryId);
    }
}
