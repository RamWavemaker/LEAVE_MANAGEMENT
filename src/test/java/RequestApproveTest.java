import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RequestApproveTest {
    public static ChromeOptions options;
    public static WebDriver webDriver;
    public static WebDriverWait wait;


    @BeforeTest(description = "opening the loginpage")
    void setUp() {
        options = new ChromeOptions();
        webDriver = new ChromeDriver(options);
        wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        webDriver.manage().timeouts().implicitlyWait(10, java.util.concurrent.TimeUnit.SECONDS);
        webDriver.manage().window().maximize();
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/src/test/resources/chromedriver.exe");
        webDriver.get("http://localhost:8080/LeaveManagement/");
    }

    @Test(description = "Applying leave Testing", priority = 1)
    void applyingLeave() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        String formattedDate = today.format(formatter);
        findingElement(By.id("email"), "sujitha@gmail.com");  //By("//*[@id=\"email\"]")
        findingElement(By.id("password"), "1234");  //By.xpath("//*[@id=\"password\"]")
        clickElement(By.xpath("//button[@type='submit']"));

        clickElement(By.xpath("//button[@class = 'btn btn-primary applyleavesbutton']"));


        findingElement(By.id("fromdateinput"), "09-05-2024");

        findingElement(By.id("todateinput"), "09-07-2024");

        findingElement(By.id("applieddateinput"), formattedDate);


        WebElement dropdownElement = webDriver.findElement(By.id("leavetypeinput"));
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByVisibleText("Casual");
//        dropdown.selectByValue("SICK"); can also use this
//        dropdown.selectByIndex(1); //selects the second element casual
        findingElement(By.id("leavecommentinput"), "TESTING-44444");

        clickElement(By.xpath("//button[@type='submit']"));

        clickElement(By.xpath("//button[@class='btn btn-danger logoutbutton']"));
    }

    @Test(description = "Manager Approve Leave Testing", priority = 2)
    void ManagerApprove() {
        findingElement(By.id("email"), "bhaskar@gmail.com");

        findingElement(By.id("password"), "1234");

        clickElement(By.xpath("//button[@type='submit']"));

        clickElement(By.xpath("//button[@class='btn btn-primary leaverequestbutton']"));

        clickElement(By.xpath("//*[@id='dashboard']/div[2]/div/div/div/button[1]"));

//        *[@id="dashboard"]/div[2]/div[2]/div/div/button[1]
//        *[@id="dashboard"]/div[2]/div[1]/div/div/button[1]
    }


    @AfterTest(description = "closing the browser")
    public void Quiting() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    void findingElement(By locator, String input) {
        webDriver.findElement(locator).sendKeys(input);
    }

    void clickElement(By locator) {
        webDriver.findElement(locator).click();
    }

    WebElement waitimplicity(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

}
