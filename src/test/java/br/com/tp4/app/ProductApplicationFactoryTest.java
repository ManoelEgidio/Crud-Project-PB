package br.com.tp4.app;

import br.com.crud_project.app.ProductApplicationFactory;
import br.com.crud_project.app.ProductApplicationServices;
import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductApplicationFactoryTest {

    @Test
    void shouldCreateIntegratedApplicationServicesUsingSharedRepositoryAndCatalog() {
        ProductApplicationServices application = ProductApplicationFactory.createInMemoryApplication();

        assertNotNull(application.repository());
        assertNotNull(application.catalog());

        application.catalog().create(new Product("10", "Monitor", 999.90, 3, Category.ELECTRONICS));

        assertEquals(1, application.repository().count());
        assertEquals(1, application.catalog().count());
        assertEquals("Monitor", application.catalog().findById("10").name());
    }
}
