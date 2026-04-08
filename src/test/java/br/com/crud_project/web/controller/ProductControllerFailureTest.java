package br.com.crud_project.web.controller;

import br.com.crud_project.domain.exception.ProductNotFoundException;
import br.com.crud_project.domain.exception.ServiceTimeoutException;
import br.com.crud_project.domain.exception.ServiceUnavailableException;
import br.com.crud_project.domain.exception.SystemOverloadException;
import br.com.crud_project.domain.model.Category;
import br.com.crud_project.domain.model.Product;
import br.com.crud_project.repository.InMemoryProductRepository;
import br.com.crud_project.service.ProductService;
import br.com.crud_project.web.WebApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = WebApplication.class)
@AutoConfigureMockMvc
@Import(ProductControllerFailureTest.TestConfig.class)
class ProductControllerFailureTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StubProductService service;

    @BeforeEach
    void resetStub() {
        service.reset();
    }

    @Test
    void shouldRedirectHomeToProducts() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void shouldShowFriendlyMessageWhenListTimesOut() throws Exception {
        service.failOnListWithTimeout();

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A operacao demorou mais do que o esperado. Tente novamente.")))
                .andExpect(content().string(not(containsString("socket timeout"))));
    }

    @Test
    void shouldRenderEditFormForExistingProduct() throws Exception {
        service.seedStoredProduct(new Product("1", "Notebook", 10.00, 1, Category.ELECTRONICS));

        mockMvc.perform(get("/products/1/edit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar produto")))
                .andExpect(content().string(containsString("Notebook")));
    }

    @Test
    void shouldRedirectWithFriendlyMessageWhenEditTargetIsMissing() throws Exception {
        mockMvc.perform(get("/products/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/products?*messageType=error*"));
    }

    @Test
    void shouldShowFriendlyMessageWhenCreateFacesNetworkFailure() throws Exception {
        service.failOnCreateWithUnavailable();

        mockMvc.perform(post("/products")
                        .param("id", "1")
                        .param("name", "Notebook")
                        .param("price", "10.00")
                        .param("quantity", "1")
                        .param("category", "FOOD"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Servico temporariamente indisponivel. Tente novamente.")))
                .andExpect(content().string(not(containsString("Connection refused"))));
    }

    @Test
    void shouldFailEarlyForInvalidInputWithoutCallingService() throws Exception {
        mockMvc.perform(post("/products")
                        .param("id", "1")
                        .param("name", "<script>")
                        .param("price", "10.00")
                        .param("quantity", "1")
                        .param("category", "FOOD"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nome do produto contem caracteres invalidos.")));

        assertEquals(0, service.createCalls());
    }

    @Test
    void shouldHideUnexpectedErrorsOnUpdate() throws Exception {
        service.failOnUpdateWithUnexpectedError();

        mockMvc.perform(post("/products/1")
                        .param("name", "Notebook")
                        .param("price", "10.00")
                        .param("quantity", "1")
                        .param("category", "FOOD"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nao foi possivel processar a solicitacao no momento.")))
                .andExpect(content().string(not(containsString("NullPointerException"))));
    }

    @Test
    void shouldRedirectWithFriendlyMessageWhenSystemIsOverloaded() throws Exception {
        service.failOnDeleteWithOverload();

        mockMvc.perform(post("/products/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/products?*messageType=error*"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        StubProductService stubProductService() {
            return new StubProductService();
        }
    }

    static class StubProductService extends ProductService {
        private FailureMode failureMode = FailureMode.NONE;
        private Product storedProduct;
        private int createCalls;

        StubProductService() {
            super(new InMemoryProductRepository());
        }

        void reset() {
            failureMode = FailureMode.NONE;
            storedProduct = null;
            createCalls = 0;
        }

        void seedStoredProduct(Product product) {
            storedProduct = product;
        }

        void failOnListWithTimeout() {
            failureMode = FailureMode.LIST_TIMEOUT;
        }

        void failOnCreateWithUnavailable() {
            failureMode = FailureMode.CREATE_UNAVAILABLE;
        }

        void failOnUpdateWithUnexpectedError() {
            failureMode = FailureMode.UPDATE_UNEXPECTED;
        }

        void failOnDeleteWithOverload() {
            failureMode = FailureMode.DELETE_OVERLOAD;
        }

        int createCalls() {
            return createCalls;
        }

        @Override
        public List<Product> findAll() {
            if (failureMode == FailureMode.LIST_TIMEOUT) {
                throw new ServiceTimeoutException("socket timeout");
            }
            return storedProduct == null ? List.of() : List.of(storedProduct);
        }

        @Override
        public Product findById(String id) {
            if (storedProduct != null && storedProduct.id().equals(id)) {
                return storedProduct;
            }
            throw new ProductNotFoundException("Produto nao encontrado.");
        }

        @Override
        public void create(Product product) {
            createCalls++;
            if (failureMode == FailureMode.CREATE_UNAVAILABLE) {
                throw new ServiceUnavailableException("Connection refused by upstream");
            }
            storedProduct = product;
        }

        @Override
        public void update(Product product) {
            if (failureMode == FailureMode.UPDATE_UNEXPECTED) {
                throw new IllegalStateException("NullPointerException at dao");
            }
            if (storedProduct == null || !storedProduct.id().equals(product.id())) {
                throw new ProductNotFoundException("Produto nao encontrado para atualizacao.");
            }
            storedProduct = product;
        }

        @Override
        public void deleteById(String id) {
            if (failureMode == FailureMode.DELETE_OVERLOAD) {
                throw new SystemOverloadException("too many requests");
            }
            if (storedProduct == null || !storedProduct.id().equals(id)) {
                throw new ProductNotFoundException("Produto nao encontrado para remocao.");
            }
            storedProduct = null;
        }
    }

    enum FailureMode {
        NONE,
        LIST_TIMEOUT,
        CREATE_UNAVAILABLE,
        UPDATE_UNEXPECTED,
        DELETE_OVERLOAD
    }
}
