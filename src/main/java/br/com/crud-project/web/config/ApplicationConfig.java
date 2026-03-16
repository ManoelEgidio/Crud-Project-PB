package br.com.crud_project.web.config;

import br.com.crud_project.app.ProductApplicationFactory;
import br.com.crud_project.app.ProductApplicationServices;
import br.com.crud_project.repository.ProductRepository;
import br.com.crud_project.service.ProductCatalog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ProductApplicationServices productApplicationServices() {
        return ProductApplicationFactory.createInMemoryApplication();
    }

    @Bean
    public ProductRepository productRepository(ProductApplicationServices applicationServices) {
        return applicationServices.repository();
    }

    @Bean
    public ProductCatalog productCatalog(ProductApplicationServices applicationServices) {
        return applicationServices.catalog();
    }
}
