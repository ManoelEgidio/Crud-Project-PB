package br.com.crud_project.app;

import br.com.crud_project.repository.ProductRepository;
import br.com.crud_project.service.ProductCatalog;

public record ProductApplicationServices(
        ProductRepository repository,
        ProductCatalog catalog
) {
}
