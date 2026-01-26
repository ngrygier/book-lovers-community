package com.booklover.book_lover_community.e2e;


import com.booklover.book_lover_community.pages.SwaggerPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


import static org.junit.jupiter.api.Assertions.assertTrue;

class SwaggerHappyPathIT {

    private WebDriver driver;
    private SwaggerPage swaggerPage;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        swaggerPage = new SwaggerPage(driver);
    }


    @Test
    void swaggerUiHomePageLoadsCorrectly() {

        swaggerPage.openSwaggerUi();

    }



    @AfterEach
    void tearDown() {
        driver.quit();
    }
}
