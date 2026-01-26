
package com.booklover.book_lover_community.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SwaggerPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    public SwaggerPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void login(String username, String password) {
        // czekamy na formularz logowania
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    public void openSwaggerUi() {
        driver.get("http://localhost:8081/swagger-ui/index.html");

        WebElement swaggerUi = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("swagger-ui"))
        );

        if (!swaggerUi.isDisplayed()) {
            throw new AssertionError("Swagger UI container nie jest widoczny");
        }
    }



    public boolean isSwaggerUiVisible() {
        return driver.findElement(By.id("swagger-ui")).isDisplayed();
    }


}
