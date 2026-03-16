package br.com.tp3.property;

import br.com.crud_project.domain.exception.ValidationException;
import br.com.crud_project.domain.model.Product;
import br.com.crud_project.web.form.ProductForm;
import br.com.crud_project.web.form.ProductInputMapper;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Provide;
import net.jqwik.api.Property;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductInputFuzzTest {
    private static final Set<String> SAFE_MESSAGES = Set.of(
            "Preco e quantidade devem ser numericos.",
            "Categoria invalida.",
            "ID do produto e obrigatorio.",
            "ID do produto deve ter no maximo 12 caracteres.",
            "ID do produto contem caracteres invalidos.",
            "Nome do produto e obrigatorio.",
            "Nome do produto deve ter no maximo 50 caracteres.",
            "Nome do produto contem caracteres invalidos.",
            "Preco deve ser maior que zero.",
            "Quantidade nao pode ser negativa.",
            "Categoria e obrigatoria."
    );

    private final ProductInputMapper mapper = new ProductInputMapper();

    @Property(tries = 400)
    void shouldHandleRandomInputWithoutLeakingInternalDetails(
            @ForAll("randomText") String id,
            @ForAll("randomText") String name,
            @ForAll("randomText") String price,
            @ForAll("randomText") String quantity,
            @ForAll("randomText") String category
    ) {
        ProductForm form = new ProductForm(id, name, price, quantity, category);

        try {
            Product product = mapper.toProduct(form, id);
            assertNotNull(product);
            assertFalse(product.id().isBlank());
        } catch (RuntimeException exception) {
            assertTrue(exception instanceof ValidationException);
            assertTrue(SAFE_MESSAGES.contains(exception.getMessage()));
            assertFalse(exception.getMessage().toLowerCase().contains("exception"));
            assertFalse(exception.getMessage().toLowerCase().contains("java"));
        }
    }

    @Provide
    Arbitrary<String> randomText() {
        return Arbitraries.strings()
                .withChars("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 <>;{}-_,./")
                .ofMinLength(0)
                .ofMaxLength(80);
    }
}
