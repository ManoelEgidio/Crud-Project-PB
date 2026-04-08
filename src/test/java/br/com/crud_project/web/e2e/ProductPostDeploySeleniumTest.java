package br.com.crud_project.web.e2e;

import br.com.crud_project.web.pages.ProductFormPage;
import br.com.crud_project.web.pages.ProductListPage;
import org.htmlunit.BrowserVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("post-deploy")
class ProductPostDeploySeleniumTest {
    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldValidateCrudFlowOnPublishedEnvironment() {
        String baseUrl = resolveBaseUrl();
        String productId = "dp" + (Instant.now().toEpochMilli() % 1_000_000_000L);

        ProductListPage listPage = new ProductListPage(driver, baseUrl).open();

        ProductListPage createdList = listPage.goToNewProductPage()
                .typeId(productId)
                .typeName("Produto Publicado")
                .typePrice("99.90")
                .typeQuantity("2")
                .selectCategory("OFFICE")
                .submitExpectingList();

        assertEquals("Produto cadastrado com sucesso.", createdList.successMessage());
        assertTrue(createdList.hasProduct(productId, "Produto Publicado"));

        ProductListPage updatedList = createdList.goToEditPage(productId)
                .typeName("Produto Publicado Atualizado")
                .typePrice("119.90")
                .typeQuantity("4")
                .selectCategory("OFFICE")
                .submitExpectingList();

        assertEquals("Produto atualizado com sucesso.", updatedList.successMessage());
        assertTrue(updatedList.hasProduct(productId, "Produto Publicado Atualizado"));

        updatedList.deleteProduct(productId);
        assertEquals("Produto removido com sucesso.", updatedList.successMessage());
    }

    @Test
    void shouldKeepFriendlyValidationMessagesAfterDeploy() {
        String baseUrl = resolveBaseUrl();
        String validId = "iv" + (Instant.now().toEpochMilli() % 1_000_000_000L);

        ProductFormPage formPage = new ProductFormPage(driver, baseUrl)
                .openCreateForm()
            .typeId(validId)
                .typeName("<script>")
                .typePrice("10.00")
                .typeQuantity("1")
                .selectCategory("FOOD")
                .submitExpectingForm();

        assertEquals("Nome do produto contem caracteres invalidos.", formPage.errorMessage());
    }

    private String resolveBaseUrl() {
        String systemValue = System.getProperty("app.base-url", "").trim();
        String envValue = System.getenv("APP_BASE_URL") == null ? "" : System.getenv("APP_BASE_URL").trim();
        String baseUrl = systemValue.isBlank() ? envValue : systemValue;

        Assumptions.assumeFalse(baseUrl.isBlank(), "Defina app.base-url ou APP_BASE_URL para executar os testes pos-deploy.");

        HtmlUnitDriver htmlUnitDriver = new HtmlUnitDriver(BrowserVersion.CHROME, true);
        htmlUnitDriver.getWebClient().getOptions().setCssEnabled(false);
        htmlUnitDriver.getWebClient().getOptions().setThrowExceptionOnScriptError(false);
        driver = htmlUnitDriver;

        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
