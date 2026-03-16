package br.com.tp2.web;

import br.com.crud_project.repository.ProductRepository;
import br.com.crud_project.web.WebApplication;
import br.com.tp2.pages.ProductFormPage;
import br.com.tp2.pages.ProductListPage;
import org.htmlunit.BrowserVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = WebApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
class ProductWebSeleniumTest {
    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository repository;

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.CHROME, true);
        htmlUnitDriver.getWebClient().getOptions().setCssEnabled(false);
        htmlUnitDriver.getWebClient().getOptions().setThrowExceptionOnScriptError(false);
        driver = htmlUnitDriver;
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldCreateUpdateAndDeleteProductThroughWebFlow() {
        ProductListPage listPage = new ProductListPage(driver, baseUrl).open();
        assertTrue(listPage.isEmptyStateVisible());

        ProductListPage createdList = listPage.goToNewProductPage()
                .typeId("101")
                .typeName("Notebook Pro")
                .typePrice("4999.90")
                .typeQuantity("7")
                .selectCategory("ELECTRONICS")
                .submitExpectingList();

        assertEquals("Produto cadastrado com sucesso.", createdList.successMessage());
        assertTrue(createdList.hasProduct("101", "Notebook Pro"));

        ProductListPage updatedList = createdList.goToEditPage("101")
                .typeName("Notebook Pro Max")
                .typePrice("5799.90")
                .typeQuantity("4")
                .selectCategory("ELECTRONICS")
                .submitExpectingList();

        assertEquals("Produto atualizado com sucesso.", updatedList.successMessage());
        assertTrue(updatedList.hasProduct("101", "Notebook Pro Max"));

        updatedList.deleteProduct("101");
        assertEquals("Produto removido com sucesso.", updatedList.successMessage());
        assertEquals(0, updatedList.productCount());
        assertTrue(updatedList.isEmptyStateVisible());
    }

    @ParameterizedTest
    @MethodSource("invalidProducts")
    void shouldDisplayValidationErrorsForInvalidInputs(
            String id,
            String name,
            String price,
            String quantity,
            String category,
            String expectedMessage
    ) {
        ProductFormPage formPage = new ProductFormPage(driver, baseUrl)
                .openCreateForm()
                .typeId(id)
                .typeName(name)
                .typePrice(price)
                .typeQuantity(quantity);

        if (!category.isBlank()) {
            formPage.selectCategory(category);
        }

        formPage.submitExpectingForm();

        assertTrue(formPage.currentPath().contains("/products"));
        assertEquals(expectedMessage, formPage.errorMessage());
    }

    @Test
    void shouldPreventDuplicateIdsAndKeepUserOnForm() {
        ProductListPage listPage = new ProductListPage(driver, baseUrl).open();
        listPage.goToNewProductPage()
                .typeId("500")
                .typeName("Teclado")
                .typePrice("120.00")
                .typeQuantity("3")
                .selectCategory("OFFICE")
                .submitExpectingList();

        ProductFormPage formPage = new ProductListPage(driver, baseUrl).open()
                .goToNewProductPage()
                .typeId("500")
                .typeName("Outro Teclado")
                .typePrice("150.00")
                .typeQuantity("1")
                .selectCategory("OFFICE")
                .submitExpectingForm();

        assertEquals("Ja existe produto com esse ID.", formPage.errorMessage());
    }

    @Test
    void shouldRejectUnsafeCharactersWithoutBreakingInterface() {
        ProductFormPage formPage = new ProductFormPage(driver, baseUrl)
                .openCreateForm()
                .typeId("x1")
                .typeName("<script>")
                .typePrice("10.00")
                .typeQuantity("1")
                .selectCategory("FOOD")
                .submitExpectingForm();

        assertEquals("Nome do produto contem caracteres invalidos.", formPage.errorMessage());
    }

    private static Stream<Arguments> invalidProducts() {
        return Stream.of(
                Arguments.of("1", "", "10.00", "1", "FOOD", "Nome do produto e obrigatorio."),
                Arguments.of("2", "Arroz", "abc", "1", "FOOD", "Preco e quantidade devem ser numericos."),
                Arguments.of("3", "Arroz", "10.00", "-1", "FOOD", "Quantidade nao pode ser negativa."),
                Arguments.of("4", "Arroz", "10.00", "2", "", "Categoria invalida.")
        );
    }
}
