package br.com.crud_project.service;

import br.com.crud_project.domain.exception.DuplicateProductException;
import br.com.crud_project.domain.exception.ProductNotFoundException;
import br.com.crud_project.domain.model.Product;
import br.com.crud_project.repository.ProductRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = Objects.requireNonNull(repository, "Repositório é obrigatório.");
    }

    public void create(Product product) {
        Objects.requireNonNull(product, "Produto é obrigatório.");

        if (repository.existsById(product.id())) {
            throw new DuplicateProductException("Já existe produto com esse ID.");
        }

        repository.save(product);
    }

    public Product findById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Produto não encontrado."));
    }

    public List<Product> findAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Product::id))
                .toList();
    }

    public void update(Product product) {
        Objects.requireNonNull(product, "Produto é obrigatório.");

        if (!repository.existsById(product.id())) {
            throw new ProductNotFoundException("Produto não encontrado para atualização.");
        }

        repository.update(product);
    }

    public void deleteById(String id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException("Produto não encontrado para remoção.");
        }

        repository.deleteById(id);
    }

    public int count() {
        return repository.count();
    }
}
