package br.com.crud_project.service;

import br.com.crud_project.domain.exception.DuplicateProductException;
import br.com.crud_project.domain.exception.ProductNotFoundException;
import br.com.crud_project.domain.exception.ValidationException;
import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import br.com.crud_project.repository.InMemoryProductRepository;
import br.com.crud_project.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductServiceTest {
    private ProductService service;

    @BeforeEach
    void setUp() {
        ProductRepository repository = new InMemoryProductRepository();
        service = new ProductService(repository);
    }

    @Test
    void shouldCreateValidProduct() {
        Product product = new Product("1", "Notebook", 3500.00, 10, Category.ELECTRONICS);

        service.create(product);

        Product savedProduct = service.findById("1");
        assertEquals(product, savedProduct);
    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateProduct() {
        Product product = new Product("1", "Notebook", 3500.00, 10, Category.ELECTRONICS);
        service.create(product);

        assertThrows(DuplicateProductException.class, () -> service.create(product));
    }

    @Test
    void shouldListProductsSortedById() {
        service.create(new Product("2", "Caderno", 20.00, 30, Category.OFFICE));
        service.create(new Product("1", "Arroz", 8.50, 40, Category.FOOD));

        List<Product> products = service.findAll();

        assertEquals(2, products.size());
        assertEquals("1", products.get(0).id());
        assertEquals("2", products.get(products.size() - 1).id());
    }

    @Test
    void shouldUpdateExistingProduct() {
        service.create(new Product("1", "Notebook", 3500.00, 10, Category.ELECTRONICS));
        Product updated = new Product("1", "Notebook Gamer", 5500.00, 5, Category.ELECTRONICS);

        service.update(updated);

        Product found = service.findById("1");
        assertEquals("Notebook Gamer", found.name());
        assertEquals(5500.00, found.price());
        assertEquals(5, found.quantity());
    }

    @Test
    void shouldDeleteExistingProduct() {
        service.create(new Product("1", "Notebook", 3500.00, 10, Category.ELECTRONICS));

        service.deleteById("1");

        assertEquals(0, service.count());
        assertThrows(ProductNotFoundException.class, () -> service.findById("1"));
    }

    @Test
    void shouldThrowExceptionWhenFindingNonexistentProduct() {
        assertThrows(ProductNotFoundException.class, () -> service.findById("999"));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentProduct() {
        Product product = new Product("999", "Teclado", 150.00, 3, Category.ELECTRONICS);

        assertThrows(ProductNotFoundException.class, () -> service.update(product));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentProduct() {
        assertThrows(ProductNotFoundException.class, () -> service.deleteById("999"));
    }

    @Test
    void shouldThrowValidationExceptionForBlankName() {
        assertThrows(ValidationException.class,
                () -> new Product("1", "   ", 100.00, 2, Category.OFFICE));
    }

    @Test
    void shouldThrowValidationExceptionForInvalidPrice() {
        assertThrows(ValidationException.class,
                () -> new Product("1", "Caneta", 0.0, 2, Category.OFFICE));
    }

    @Test
    void shouldThrowValidationExceptionForNegativeQuantity() {
        assertThrows(ValidationException.class,
                () -> new Product("1", "Caneta", 2.5, -1, Category.OFFICE));
    }

    @Test
    void shouldDescribeCategoryWithoutDefaultCase() {
        Product product = new Product("1", "Caneta", 2.5, 10, Category.OFFICE);

        assertEquals("Escritório", product.categoryDescription());
    }

    @Test
    void shouldRejectNullRepository() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new ProductService(null));

        assertTrue(exception.getMessage().contains("Repositório"));
    }

    @Test
    void shouldRejectNullProductOnCreate() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.create(null));

        assertTrue(exception.getMessage().contains("Produto"));
    }

    @Test
    void shouldRejectNullProductOnUpdate() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.update(null));

        assertTrue(exception.getMessage().contains("Produto"));
    }
}
