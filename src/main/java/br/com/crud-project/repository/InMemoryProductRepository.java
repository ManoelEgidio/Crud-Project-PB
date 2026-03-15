package br.com.crud_project.repository;

import br.com.crud_project.domain.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<String, Product> products = new HashMap<>();

    @Override
    public void save(Product product) {
        products.put(product.id(), product);
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public void update(Product product) {
        products.put(product.id(), product);
    }

    @Override
    public void deleteById(String id) {
        products.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return products.containsKey(id);
    }

    @Override
    public int count() {
        return products.size();
    }
}
