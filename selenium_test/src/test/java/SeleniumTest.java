import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumTest {
    WebDriver driver;
    final String vyatsuURL = "https://old.vyatsu.ru";
    final String otzovikURL = "https://otzovik.com";
    final String cdsvyatkaURL = "https://m.cdsvyatka.com";

    @BeforeEach 
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        driver = new ChromeDriver(options);
    }

    @Test
    public void titleTest() {
        driver.get(vyatsuURL);
        Assertions.assertEquals(driver.getTitle(), "Официальный сайт ВятГУ");
    }

    @Test
    public void task1Scene1() {
        driver.get(vyatsuURL);
        WebElement menuElement = driver.findElement(By.linkText("Сведения об образовательной организации"));
        Actions actions = new Actions(driver);
        actions.moveToElement(menuElement).perform();

        WebElement button = driver.findElement(By.linkText("Образовательные стандарты"));
        button.click();
        Assertions.assertEquals(driver.getTitle(), "Сведения об ОО Образовательные стандарты и требования");
    }

    @Test
    public void task1Scene2() {
        driver.get(vyatsuURL);
        WebElement searchInput = driver.findElement(By.name("query"));
        searchInput.sendKeys("отпуск", Keys.ENTER);

        List<WebElement> searchResults = driver.findElements(By.xpath("//div[@class='column-center']//h4//a"));
        assertFalse(searchResults.isEmpty(), "Поиск не дал результатов, список пуст!");
    }

    @Test
    public void task1Scene3correct() {
        driver.get(vyatsuURL);
        WebElement studentLink = driver.findElement(By.xpath("//a[.//h3[text()='Студенту']]"));
        studentLink.click();
        WebElement emailInput = driver.findElement(By.id("email-input"));
        emailInput.sendKeys("test_user@vyatsu.ru", Keys.ENTER);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        assertTrue(text.contains("успешно добавлен") || text.contains("находится в базе данных"), "Ошибка: валидный email не прошел!");
    }

    @Test
    public void task1Scene3incorrect() {
        driver.get(vyatsuURL);
        WebElement studentLink = driver.findElement(By.xpath("//a[.//h3[text()='Студенту']]"));
        studentLink.click();
        WebElement emailInput = driver.findElement(By.id("email-input"));
        emailInput.sendKeys("aboba", Keys.ENTER);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String text = alert.getText();
        assertTrue(text.contains("Неправильно введен"), "Ошибка: невалидный email прошел!");
    }

    @Test
    public void task2Scene1() {
        driver.get(otzovikURL);
        WebElement searchInput = driver.findElement(By.xpath("//div[contains(@class, 'header-mobile-row')]//input[@id='header-search-input']"));
        searchInput.sendKeys("яндекс");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement resultLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Yandex Metro - программа для Android")));
        resultLink.click();

        Assertions.assertEquals(driver.getTitle(), "Отзывы о Yandex Metro - программа для Android");
        WebElement counterSpan = driver.findElement(By.xpath("//a[contains(@class, 'reviews') and contains(., 'Отзывы')]//span[@class='counter']"));
        String countStr = counterSpan.getText().trim();
        int reviewsCount = Integer.parseInt(countStr);
        assertTrue(reviewsCount > 0, "Ошибка: счетчик отзывов равен нулю или пустой!");
    }

    @Test
    public void task2Scene2() {
        driver.get(otzovikURL);
        WebElement menuBtn = driver.findElement(By.id("header-menu-btn"));
        menuBtn.click();
        WebElement reviewBtn = driver.findElement(By.xpath("//div[@id='header-nav-mobile-popup']//a[contains(@class, 'header-postreview-btn')]"));
        reviewBtn.click();

        WebElement searchInput = driver.findElement(By.id("tproduct"));
        searchInput.sendKeys("Ботанический сад Киров");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement resultLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Ботанический сад ВятГУ (Россия, Киров)")));
        resultLink.click();

        WebElement titleInput = driver.findElement(By.id("content_title"));
        titleInput.sendKeys("Прекрасный сад!");
        WebElement previewBtn = driver.findElement(By.id("previewbtn"));
        previewBtn.click();
        WebElement dialog = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("dia-body")));
        String text = dialog.getText();
        assertTrue(text.contains("Обнаружены ошибки, либо не все поля заполнены."), "Ошибка: невалидный ввод прошел!");
    }

    @Test
    public void task2Scene3() {
        driver.get(otzovikURL);
        WebElement menuBtn = driver.findElement(By.id("header-menu-btn"));
        menuBtn.click();
        WebElement reviewBtn = driver.findElement(By.xpath("//div[@id='header-nav-mobile-popup']//a[contains(@class, 'header-postreview-btn')]"));
        reviewBtn.click();

        WebElement searchInput = driver.findElement(By.id("tproduct"));
        searchInput.sendKeys("Ботанический сад Киров");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement resultLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Ботанический сад ВятГУ (Россия, Киров)")));
        resultLink.click();

        WebElement starBtn = driver.findElement(By.xpath("//*[local-name()='svg' and @data-value='5']"));
        starBtn.click();
        WebElement titleInput = driver.findElement(By.id("content_title"));
        titleInput.sendKeys("Прекрасный сад!");
        WebElement contentInput = driver.findElement(By.className("text_area_auto_height_ta"));
        contentInput.sendKeys("Этот сад настолько прекрасен, что я не мог не посетить его. Купил конечно экскурсию, но чето дороговато, но эмоций много");
        WebElement prosInput = driver.findElement(By.name("content_pros"));
        prosInput.sendKeys("Простор");
        WebElement consInput = driver.findElement(By.name("content_cons"));
        consInput.sendKeys("Дорогие экскурсии");
        WebElement recommendBtn = driver.findElement(By.xpath("//span[@class='button2023']//span[text()='Да']"));
        recommendBtn.click();
        WebElement previewBtn = driver.findElement(By.id("previewbtn"));
        previewBtn.click();
        WebElement preview = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("review-wrap")));
        String text = preview.getText();
        assertTrue(text.contains("Отзыв: Ботанический сад ВятГУ (Россия, Киров) - Прекрасный сад!"), "Ошибка: валидный ввод не прошел!");
    }

    @Test
    public void task3Scene1() {
        driver.get(cdsvyatkaURL);
        WebElement dropdownElement = driver.findElement(By.id("id_MarshSelect"));
        Select select = new Select(dropdownElement);
        select.selectByVisibleText("Авт 21: Ипподром-с.Порошино");
        WebElement btnMap = driver.findElement(By.id("id_buttonMap"));
        btnMap.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement anyBus = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("leaflet-canvas-icon")));
        anyBus.click();
        WebElement popup = driver.findElement(By.className("leaflet-popup-content"));
        String text = popup.getText();
        assertTrue(text.contains("Модель"), "Ошибка: характеристики автобуса не показываются!");
    }

    @Test
    public void task3Scene2() {
        driver.get(cdsvyatkaURL);
        WebElement dropdownElement1 = driver.findElement(By.id("id_MarshSelect"));
        Select select1 = new Select(dropdownElement1);
        select1.selectByVisibleText("Авт 21: Ипподром-с.Порошино");
        WebElement dropdownElement2 = driver.findElement(By.id("id_StopSelect"));
        Select select2 = new Select(dropdownElement2);
        select2.selectByVisibleText("Село Порошино (код 492)");
        WebElement btnPred = driver.findElement(By.id("id_buttonPrediction"));
        btnPred.click();
        WebElement table = driver.findElement(By.xpath("//table"));
        String text = table.getText();
        assertTrue(text.contains("Гос. номер") && text.contains("Через"), "Ошибка: прогноз не показывается!");
    }

    @Test
    public void task3Scene3() {
        driver.get(cdsvyatkaURL);
        WebElement dropdownElement1 = driver.findElement(By.id("id_MarshSelect"));
        Select select1 = new Select(dropdownElement1);
        select1.selectByVisibleText("Авт 21: Ипподром-с.Порошино");
        WebElement dropdownElement2 = driver.findElement(By.id("id_StopSelect"));
        Select select2 = new Select(dropdownElement2);
        select2.selectByVisibleText("Село Порошино (код 492)");
        WebElement btnRasp = driver.findElement(By.id("id_addRaspisToBookMarks_Btn"));
        btnRasp.click();
        select1.selectByVisibleText("Авт 117: Киров-Сидоровка");
        select2.selectByVisibleText("Село Порошино (код 492)");
        btnRasp.click();
        select1.selectByVisibleText("Авт 121: Киров-Баташи");
        select2.selectByVisibleText("Село Порошино (код 492)");
        btnRasp.click();
        WebElement favorits = driver.findElement(By.id("div_favorits"));
        String text = favorits.getText();
        assertTrue(text.contains("Авт 21: Ипподром-с.Порошино через Село Порошино (код 492)") && text.contains("Авт 117: Киров-Сидоровка через Село Порошино (код 492)") && text.contains("Авт 121: Киров-Баташи через Село Порошино (код 492)"), "Ошибка: избранное не показывается!");
    }

    @AfterEach 
    public void quit() {

    }
}