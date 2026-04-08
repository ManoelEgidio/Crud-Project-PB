package br.com.crud_project.repository;

import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryProductRepositoryTest {
    private InMemoryProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
    }

    @Test
    void shouldSaveAndFindProductById() {
        Product product = new Product("1", "Mouse", 90.0, 12, Category.ELECTRONICS);

        repository.save(product);

        Optional<Product> found = repository.findById("1");
        assertTrue(found.isPresent());
        assertEquals(product, found.get());
    }

    @Test
    void shouldUpdateProduct() {
        Product original = new Product("1", "Mouse", 90.0, 12, Category.ELECTRONICS);
        Product updated = new Product("1", "Mouse Gamer", 150.0, 5, Category.ELECTRONICS);

        repository.save(original);
        repository.update(updated);

        Product found = repository.findById("1").orElseThrow();
        assertEquals("Mouse Gamer", found.name());
    }

    @Test
    void shouldDeleteProduct() {
        Product product = new Product("1", "Mouse", 90.0, 12, Category.ELECTRONICS);
        repository.save(product);

        repository.deleteById("1");

        assertFalse(repository.existsById("1"));
        assertEquals(0, repository.count());
    }
}
