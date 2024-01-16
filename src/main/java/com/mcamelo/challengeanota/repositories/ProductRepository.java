package com.mcamelo.challengeanota.repositories;

import com.mcamelo.challengeanota.domain.products.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
