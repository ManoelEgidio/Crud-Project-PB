package br.com.crud_project.domain.model;

import br.com.crud_project.domain.exception.ValidationException;

import java.util.Objects;

public final class Product {
    private static final int MAX_ID_LENGTH = 12;
    private static final int MAX_NAME_LENGTH = 50;

    private final String id;
    private final String name;
    private final double price;
    private final int quantity;
    private final Category category;

    public Product(String id, String name, double price, int quantity, Category category) {
        validateId(id);
        validateName(name);
        validatePrice(price);
        validateQuantity(quantity);
        validateCategory(category);

        this.id = id.trim();
        this.name = name.trim();
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public double price() {
        return price;
    }

    public int quantity() {
        return quantity;
    }

    public Category category() {
        return category;
    }

    public Product withUpdatedData(String name, double price, int quantity, Category category) {
        return new Product(this.id, name, price, quantity, category);
    }

    public String categoryDescription() {
        return switch (category) {
            case FOOD -> "Alimentos";
            case ELECTRONICS -> "Eletronicos";
            case OFFICE -> "Escritorio";
        };
    }

    private static void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("ID do produto e obrigatorio.");
        }
        if (id.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("ID do produto deve ter no maximo 12 caracteres.");
        }
        if (containsUnsafeCharacters(id)) {
            throw new ValidationException("ID do produto contem caracteres invalidos.");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Nome do produto e obrigatorio.");
        }
        if (name.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Nome do produto deve ter no maximo 50 caracteres.");
        }
        if (containsUnsafeCharacters(name)) {
            throw new ValidationException("Nome do produto contem caracteres invalidos.");
        }
    }

    private static void validatePrice(double price) {
        if (price <= 0) {
            throw new ValidationException("Preco deve ser maior que zero.");
        }
    }

    private static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new ValidationException("Quantidade nao pode ser negativa.");
        }
    }

    private static void validateCategory(Category category) {
        if (category == null) {
            throw new ValidationException("Categoria e obrigatoria.");
        }
    }

    private static boolean containsUnsafeCharacters(String value) {
        return value.chars().anyMatch(character ->
                Character.isISOControl(character)
                        || character == '<'
                        || character == '>'
                        || character == '{'
                        || character == '}'
                        || character == ';'
        );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Product other)) {
            return false;
        }
        return Double.compare(price, other.price) == 0
                && quantity == other.quantity
                && id.equals(other.id)
                && name.equals(other.name)
                && category == other.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, quantity, category);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", category=" + category +
                '}';
    }
}
