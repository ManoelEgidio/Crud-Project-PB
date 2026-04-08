package br.com.crud_project.property;

import br.com.crud_project.domain.exception.ValidationException;
import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Provide;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.DoubleRange;
import net.jqwik.api.constraints.IntRange;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductPropertiesTest {

    @Property
    void shouldCreateProductForAnyValidData(
            @ForAll("validIds") String id,
            @ForAll("validNames") String name,
            @ForAll @DoubleRange(min = 0.01, max = 100000.0) double price,
            @ForAll @IntRange(min = 0, max = 100000) int quantity,
            @ForAll Category category
    ) {
        Product product = new Product(id, name, price, quantity, category);

        assertEquals(id.trim(), product.id());
        assertEquals(name.trim(), product.name());
        assertEquals(price, product.price());
        assertEquals(quantity, product.quantity());
        assertEquals(category, product.category());
    }

    @Property
    void shouldRejectInvalidPrices(
            @ForAll("validIds") String id,
            @ForAll("validNames") String name,
            @ForAll @DoubleRange(min = -100000.0, max = 0.0) double invalidPrice,
            @ForAll @IntRange(min = 0, max = 1000) int quantity,
            @ForAll Category category
    ) {
        assertThrows(ValidationException.class,
                () -> new Product(id, name, invalidPrice, quantity, category));
    }

    @Property
    void shouldRejectNegativeQuantities(
            @ForAll("validIds") String id,
            @ForAll("validNames") String name,
            @ForAll @DoubleRange(min = 0.01, max = 1000.0) double price,
            @ForAll @IntRange(min = -10000, max = -1) int invalidQuantity,
            @ForAll Category category
    ) {
        assertThrows(ValidationException.class,
                () -> new Product(id, name, price, invalidQuantity, category));
    }

    @Property
    void shouldRejectBlankNames(
            @ForAll("validIds") String id,
            @ForAll("blankNames") String blankName,
            @ForAll @DoubleRange(min = 0.01, max = 1000.0) double price,
            @ForAll @IntRange(min = 0, max = 1000) int quantity,
            @ForAll Category category
    ) {
        assertThrows(ValidationException.class,
                () -> new Product(id, blankName, price, quantity, category));
    }

    @Provide
    Arbitrary<String> validIds() {
        return Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
                .ofMinLength(1)
                .ofMaxLength(12);
    }

    @Provide
    Arbitrary<String> validNames() {
        return Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ")
                .ofMinLength(1)
                .ofMaxLength(50)
                .filter(value -> !value.isBlank());
    }

    @Provide
    Arbitrary<String> blankNames() {
        return Arbitraries.of("", " ", "   ", "\t", "\n");
    }
}
