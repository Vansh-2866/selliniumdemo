package sellinium;

import java.nio.file.Paths;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class logintest {
	WebDriver driver;
    WebDriverWait wait;

    // Opens the HTML file straight from the project folder
    String loginUrl = Paths.get("src/main/resources/web/login.html")
                           .toAbsolutePath().toUri().toString();

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.getBoolean("headless")) {   // set to true inside Docker
            options.addArguments("--headless=new", "--no-sandbox",
                                 "--disable-dev-shm-usage", "--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get(loginUrl);
    }

    private void login(String user, String pass) {
        driver.findElement(By.id("username")).sendKeys(user);
        driver.findElement(By.id("password")).sendKeys(pass);
        driver.findElement(By.id("loginBtn")).click();
    }

    @Test(priority = 1)
    public void loginPageTitleTest() {
        Assert.assertEquals(driver.getTitle(), "Login Page");
    }

    @Test(priority = 2)
    public void validLoginTest() {
        login("admin", "admin123");
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        Assert.assertEquals(driver.getTitle(), "Dashboard");
        Assert.assertEquals(driver.findElement(By.id("welcome")).getText(), "Welcome, admin!");
    }

    @Test(priority = 3)
    public void invalidLoginTest() {
        login("admin", "wrongpass");
        String msg = driver.findElement(By.id("error")).getText();
        Assert.assertEquals(msg, "Invalid username or password");
        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"));
    }

    @Test(priority = 4)
    public void emptyFieldsTest() {
        driver.findElement(By.id("loginBtn")).click();
        String msg = driver.findElement(By.id("error")).getText();
        Assert.assertEquals(msg, "Username and password are required");
    }

    @Test(priority = 5)
    public void logoutTest() {
        login("admin", "admin123");
        wait.until(ExpectedConditions.urlContains("dashboard.html"));
        driver.findElement(By.id("logout")).click();
        wait.until(ExpectedConditions.urlContains("login.html"));
        Assert.assertEquals(driver.getTitle(), "Login Page");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
