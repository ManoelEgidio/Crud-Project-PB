package br.com.crud_project.repository;

import br.com.crud_project.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    void save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    void update(Product product);
    void deleteById(String id);
    void deleteAll();
    boolean existsById(String id);
    int count();
}
