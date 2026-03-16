package br.com.crud_project.web.config;

import br.com.crud_project.repository.InMemoryProductRepository;
import br.com.crud_project.repository.ProductRepository;
import br.com.crud_project.service.ProductService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public ProductRepository productRepository() {
        return new InMemoryProductRepository();
    }

    @Bean
    public ProductService productService(ProductRepository repository) {
        return new ProductService(repository);
    }
}
