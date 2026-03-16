package br.com.tp2.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class ProductListPage {
    private final WebDriver driver;
    private final String baseUrl;

    public ProductListPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    public ProductListPage open() {
        driver.get(baseUrl + "/products");
        return this;
    }

    public ProductFormPage goToNewProductPage() {
        driver.findElement(By.xpath("//*[@data-testid='new-product-link']")).click();
        return new ProductFormPage(driver, baseUrl);
    }

    public ProductFormPage goToEditPage(String id) {
        driver.findElement(By.xpath("//*[@data-product-id='" + id + "']//*[@data-testid='edit-link']")).click();
        return new ProductFormPage(driver, baseUrl);
    }

    public ProductListPage deleteProduct(String id) {
        driver.findElement(By.xpath("//*[@data-product-id='" + id + "']//*[@data-testid='delete-button']")).click();
        return this;
    }

    public boolean hasProduct(String id, String name) {
        List<WebElement> rows = driver.findElements(By.xpath("//*[@data-product-id='" + id + "']"));
        return rows.stream().anyMatch(row -> row.getText().contains(name));
    }

    public boolean isEmptyStateVisible() {
        return !driver.findElements(By.xpath("//*[@data-testid='empty-state']")).isEmpty();
    }

    public String successMessage() {
        List<WebElement> messages = driver.findElements(By.xpath("//*[@data-testid='success-message']"));
        return messages.isEmpty() ? "" : messages.get(0).getText();
    }

    public int productCount() {
        return driver.findElements(By.xpath("//*[@data-testid='product-row']")).size();
    }
}
