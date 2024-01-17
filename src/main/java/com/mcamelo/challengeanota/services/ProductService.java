package com.mcamelo.challengeanota.services;


import com.mcamelo.challengeanota.domain.category.Category;
import com.mcamelo.challengeanota.domain.category.exceptions.CategoryNotFoundException;
import com.mcamelo.challengeanota.domain.products.Product;
import com.mcamelo.challengeanota.domain.products.ProductDTO;
import com.mcamelo.challengeanota.domain.products.exceptions.ProductNotFoundException;
import com.mcamelo.challengeanota.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private ProductRepository repository;
    private CategoryService categoryService;

    public ProductService(ProductRepository repository, CategoryService categoryService) {
        this.repository = repository;
        this.categoryService = categoryService;
    }

    public Product create(ProductDTO productDTO){
        Category category = this.categoryService.getById(productDTO.categoryId())
                .orElseThrow(CategoryNotFoundException::new);
        Product newProduct = new Product(productDTO);
        newProduct.setCategory(category);
        this.repository.save(newProduct);
        return newProduct;
    }

    public List<Product> getAll() {
        return this.repository.findAll();
    }
    public Product update(String id, ProductDTO productDTO) {
        Product product = this.repository.findById(id).orElseThrow(ProductNotFoundException::new);
        if(productDTO.categoryId() != null) {
            this.categoryService.getById(productDTO.categoryId())
                .ifPresent(product::setCategory);

        }
        if(!productDTO.title().isEmpty()) product.setTitle(productDTO.title());
        if(!productDTO.description().isEmpty()) product.setDescription(productDTO.description());
        if(!(productDTO.price() == null)) product.setPrice(productDTO.price());

        this.repository.save(product);

        return product;
    }

    public void delete(String id) {
        Product product = this.repository.findById(id).orElseThrow(ProductNotFoundException::new);
        this.repository.delete(product);
    }
}
