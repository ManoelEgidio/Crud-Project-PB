package br.com.crud_project.web.form;

import br.com.crud_project.domain.exception.ValidationException;
import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ProductInputMapper {

    public Product toProduct(ProductForm form, String id) {
        try {
            double parsedPrice = Double.parseDouble(normalize(form.price()).replace(',', '.'));
            int parsedQuantity = Integer.parseInt(normalize(form.quantity()));
            Category parsedCategory = Category.valueOf(normalize(form.category()).toUpperCase(Locale.ROOT));
            return new Product(id, form.name(), parsedPrice, parsedQuantity, parsedCategory);
        } catch (NumberFormatException exception) {
            throw new ValidationException("Preco e quantidade devem ser numericos.");
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Categoria invalida.");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
