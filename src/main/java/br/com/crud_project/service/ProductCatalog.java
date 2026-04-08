package br.com.crud_project.service;

import br.com.crud_project.domain.model.Product;

import java.util.List;

public interface ProductCatalog {
    void create(Product product);
    Product findById(String id);
    List<Product> findAll();
    void update(Product product);
    void deleteById(String id);
    int count();
}
