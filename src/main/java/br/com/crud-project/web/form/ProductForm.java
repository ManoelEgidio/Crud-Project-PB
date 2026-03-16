package br.com.crud_project.web.form;

import br.com.crud_project.domain.model.Product;

public record ProductForm(
        String id,
        String name,
        String price,
        String quantity,
        String category
) {
    public static ProductForm empty() {
        return new ProductForm("", "", "", "", "");
    }

    public static ProductForm fromProduct(Product product) {
        return new ProductForm(
                product.id(),
                product.name(),
                String.valueOf(product.price()),
                String.valueOf(product.quantity()),
                product.category().name()
        );
    }
}
