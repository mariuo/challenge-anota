package com.mcamelo.challengeanota.services;


import com.mcamelo.challengeanota.domain.category.Category;
import com.mcamelo.challengeanota.domain.category.exceptions.CategoryNotFoundException;
import com.mcamelo.challengeanota.domain.products.Product;
import com.mcamelo.challengeanota.domain.products.ProductDTO;
import com.mcamelo.challengeanota.domain.products.exceptions.ProductNotFoundException;
import com.mcamelo.challengeanota.repositories.ProductRepository;
import com.mcamelo.challengeanota.services.aws.AwsSnsService;
import com.mcamelo.challengeanota.services.aws.MessageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final CategoryService categoryService;
    private final AwsSnsService snsService;

    public ProductService(ProductRepository repository, CategoryService categoryService, AwsSnsService snsService) {
        this.repository = repository;
        this.categoryService = categoryService;
        this.snsService = snsService;
    }

    public Product create(ProductDTO productDTO){
        this.categoryService.getById(productDTO.categoryId())
                .orElseThrow(CategoryNotFoundException::new);
        Product newProduct = new Product(productDTO);

        this.repository.save(newProduct);
        this.snsService.publish(new MessageDTO(newProduct.getOwnerId()));
        return newProduct;
    }

    public List<Product> getAll() {
        return this.repository.findAll();
    }
    public Product update(String id, ProductDTO productDTO) {
        Product product = this.repository.findById(id).orElseThrow(ProductNotFoundException::new);
        if(productDTO.categoryId() != null) {
            this.categoryService.getById(productDTO.categoryId())
                .orElseThrow(CategoryNotFoundException::new);
            product.setCategory(productDTO.categoryId());

        }
        if(!productDTO.title().isEmpty()) product.setTitle(productDTO.title());
        if(!productDTO.description().isEmpty()) product.setDescription(productDTO.description());
        if(!(productDTO.price() == null)) product.setPrice(productDTO.price());

        this.repository.save(product);
        this.snsService.publish(new MessageDTO(product.getOwnerId()));

        return product;
    }

    public void delete(String id) {
        Product product = this.repository.findById(id).orElseThrow(ProductNotFoundException::new);
        this.repository.delete(product);
    }
}
