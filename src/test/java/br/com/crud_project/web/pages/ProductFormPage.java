package br.com.crud_project.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class ProductFormPage {
    private final WebDriver driver;
    private final String baseUrl;

    public ProductFormPage(WebDriver driver, String baseUrl) {
        this.driver = driver;
        this.baseUrl = baseUrl;
    }

    public ProductFormPage openCreateForm() {
        driver.get(baseUrl + "/products/new");
        return this;
    }

    public ProductFormPage typeId(String id) {
        WebElement field = driver.findElement(By.name("id"));
        field.clear();
        field.sendKeys(id);
        return this;
    }

    public ProductFormPage typeName(String name) {
        WebElement field = driver.findElement(By.name("name"));
        field.clear();
        field.sendKeys(name);
        return this;
    }

    public ProductFormPage typePrice(String price) {
        WebElement field = driver.findElement(By.name("price"));
        field.clear();
        field.sendKeys(price);
        return this;
    }

    public ProductFormPage typeQuantity(String quantity) {
        WebElement field = driver.findElement(By.name("quantity"));
        field.clear();
        field.sendKeys(quantity);
        return this;
    }

    public ProductFormPage selectCategory(String category) {
        Select select = new Select(driver.findElement(By.name("category")));
        select.selectByValue(category);
        return this;
    }

    public ProductFormPage submitExpectingForm() {
        driver.findElement(By.cssSelector("[data-testid='submit-button']")).click();
        return this;
    }

    public ProductListPage submitExpectingList() {
        driver.findElement(By.cssSelector("[data-testid='submit-button']")).click();
        return new ProductListPage(driver, baseUrl);
    }

    public String errorMessage() {
        List<WebElement> messages = driver.findElements(By.cssSelector("[data-testid='error-message']"));
        return messages.isEmpty() ? "" : messages.get(0).getText();
    }

    public String currentPath() {
        return driver.getCurrentUrl();
    }
}
