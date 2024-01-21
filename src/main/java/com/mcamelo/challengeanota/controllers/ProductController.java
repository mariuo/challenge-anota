package com.mcamelo.challengeanota.controllers;

import com.amazonaws.services.sqs.model.Message;
import com.mcamelo.challengeanota.config.aws.AwsSqsConfig;
import com.mcamelo.challengeanota.domain.products.Product;
import com.mcamelo.challengeanota.domain.products.ProductDTO;
import com.mcamelo.challengeanota.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private ProductService service;
    @Autowired
    private AwsSqsConfig awsSqsConfig;

    public ProductController(ProductService service){
        this.service = service;
    }
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductDTO dto){
        Product newProduct = this.service.create(dto);
        return ResponseEntity.ok().body(newProduct);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll(){
        List<Product> products = this.service.getAll();
        return ResponseEntity.ok().body(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable("id") String id, @RequestBody ProductDTO dto){
        Product updatedProduct = this.service.update(id, dto);
        return ResponseEntity.ok().body(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> delete(@PathVariable("id") String id){
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/consumer")
    public List<Message> consumer2(){
//        awsSqsConfig.printUrls();
        return awsSqsConfig.consumeMessageFromSQS();
    }
}
