package br.com.crud_project.app;

import br.com.crud_project.repository.InMemoryProductRepository;
import br.com.crud_project.repository.ProductRepository;
import br.com.crud_project.service.ProductCatalog;
import br.com.crud_project.service.ProductService;

public final class ProductApplicationFactory {
    private ProductApplicationFactory() {
    }

    public static ProductApplicationServices createInMemoryApplication() {
        ProductRepository repository = new InMemoryProductRepository();
        ProductCatalog catalog = new ProductService(repository);
        return new ProductApplicationServices(repository, catalog);
    }
}
