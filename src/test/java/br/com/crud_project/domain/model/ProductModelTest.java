package br.com.crud_project.domain.model;

import br.com.crud_project.domain.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductModelTest {

    @Test
    void shouldCreateUpdatedCopyWithoutMutatingOriginal() {
        Product original = new Product("1", "Mouse", 50.0, 2, Category.ELECTRONICS);

        Product updated = original.withUpdatedData("Mouse Pro", 80.0, 4, Category.ELECTRONICS);

        assertEquals("Mouse", original.name());
        assertEquals("Mouse Pro", updated.name());
        assertEquals("1", updated.id());
    }

    @Test
    void shouldRejectTooLongId() {
        assertThrows(ValidationException.class,
                () -> new Product("1234567890123", "Mouse", 10.0, 1, Category.ELECTRONICS));
    }

    @Test
    void shouldRejectUnsafeName() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> new Product("1", "<script>", 10.0, 1, Category.ELECTRONICS));

        assertEquals("Nome do produto contem caracteres invalidos.", exception.getMessage());
    }

    @Test
    void shouldRejectNullCategory() {
        assertThrows(ValidationException.class,
                () -> new Product("1", "Mouse", 10.0, 1, null));
    }

    @Test
    void shouldImplementEqualityHashCodeAndToString() {
        Product first = new Product("1", "Mouse", 10.0, 1, Category.ELECTRONICS);
        Product second = new Product("1", "Mouse", 10.0, 1, Category.ELECTRONICS);
        Product third = new Product("2", "Teclado", 20.0, 1, Category.OFFICE);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
        assertTrue(first.toString().contains("Mouse"));
    }
}
