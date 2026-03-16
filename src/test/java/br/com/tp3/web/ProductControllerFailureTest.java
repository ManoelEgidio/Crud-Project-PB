package br.com.tp3.web;

import br.com.crud_project.domain.exception.ServiceTimeoutException;
import br.com.crud_project.domain.exception.ServiceUnavailableException;
import br.com.crud_project.domain.exception.SystemOverloadException;
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
    void shouldShowFriendlyMessageWhenListTimesOut() throws Exception {
        service.failOnListWithTimeout();

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A operacao demorou mais do que o esperado. Tente novamente.")))
                .andExpect(content().string(not(containsString("socket timeout"))));
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
        private int createCalls;

        StubProductService() {
            super(new InMemoryProductRepository());
        }

        void reset() {
            failureMode = FailureMode.NONE;
            createCalls = 0;
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
            return List.of();
        }

        @Override
        public void create(Product product) {
            createCalls++;
            if (failureMode == FailureMode.CREATE_UNAVAILABLE) {
                throw new ServiceUnavailableException("Connection refused by upstream");
            }
        }

        @Override
        public void update(Product product) {
            if (failureMode == FailureMode.UPDATE_UNEXPECTED) {
                throw new IllegalStateException("NullPointerException at dao");
            }
        }

        @Override
        public void deleteById(String id) {
            if (failureMode == FailureMode.DELETE_OVERLOAD) {
                throw new SystemOverloadException("too many requests");
            }
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
