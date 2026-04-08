package br.com.crud_project.service;

import br.com.crud_project.domain.exception.DuplicateProductException;
import br.com.crud_project.domain.exception.ProductNotFoundException;
import br.com.crud_project.domain.exception.ServiceUnavailableException;
import br.com.crud_project.domain.exception.SystemOverloadException;
import br.com.crud_project.domain.model.Product;
import br.com.crud_project.repository.ProductRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ProductService implements ProductCatalog {
    private static final int DEFAULT_MAX_PRODUCTS = 1_000;

    private final ProductRepository repository;
    private final int maxProducts;

    public ProductService(ProductRepository repository) {
        this(repository, DEFAULT_MAX_PRODUCTS);
    }

    ProductService(ProductRepository repository, int maxProducts) {
        this.repository = Objects.requireNonNull(repository, "Repositorio e obrigatorio.");
        this.maxProducts = maxProducts;
    }

    @Override
    public void create(Product product) {
        Objects.requireNonNull(product, "Produto e obrigatorio.");

        executeWrite(() -> {
            if (repository.existsById(product.id())) {
                throw new DuplicateProductException("Ja existe produto com esse ID.");
            }
            if (repository.count() >= maxProducts) {
                throw new SystemOverloadException("Capacidade maxima do catalogo atingida.");
            }
            repository.save(product);
        });
    }

    @Override
    public Product findById(String id) {
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Produto nao encontrado."));
        } catch (ProductNotFoundException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ServiceUnavailableException("Falha ao consultar o catalogo.", exception);
        }
    }

    @Override
    public List<Product> findAll() {
        try {
            return repository.findAll().stream()
                    .sorted(Comparator.comparing(Product::id))
                    .toList();
        } catch (RuntimeException exception) {
            throw new ServiceUnavailableException("Falha ao listar o catalogo.", exception);
        }
    }

    @Override
    public void update(Product product) {
        Objects.requireNonNull(product, "Produto e obrigatorio.");

        executeWrite(() -> {
            if (!repository.existsById(product.id())) {
                throw new ProductNotFoundException("Produto nao encontrado para atualizacao.");
            }
            repository.update(product);
        });
    }

    @Override
    public void deleteById(String id) {
        executeWrite(() -> {
            if (!repository.existsById(id)) {
                throw new ProductNotFoundException("Produto nao encontrado para remocao.");
            }
            repository.deleteById(id);
        });
    }

    @Override
    public int count() {
        return repository.count();
    }

    private void executeWrite(Runnable operation) {
        try {
            operation.run();
        } catch (DuplicateProductException | ProductNotFoundException | SystemOverloadException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ServiceUnavailableException("Falha ao acessar o catalogo.", exception);
        }
    }
}
