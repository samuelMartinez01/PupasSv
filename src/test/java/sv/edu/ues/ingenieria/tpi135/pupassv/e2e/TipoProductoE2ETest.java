package sv.edu.ues.ingenieria.tpi135.pupassv.e2e;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TipoProductoE2ETest {

    private WebDriver driver;

    @BeforeAll
    public void setup() {
        // Ruta manual para identificar donnde se encuentra el chromedriver
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterAll
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    public void testCargarTiposDeProducto() {
        driver.get("http://localhost:3000/index.html");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Espera hasta que el select tenga al menos 2 opciones (1 por defecto + tipos) para que el servidor los detecte
        WebElement selectElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("select-categorias")));

        wait.until(driver -> {
            List<WebElement> options = selectElement.findElements(By.tagName("option"));
            return options.size() > 1;
        });

        List<WebElement> options = selectElement.findElements(By.tagName("option"));
        // Mostrar las opciones encontradas en la consola
        System.out.println("Opciones disponibles:");
        for (WebElement option : options) {
            System.out.println("Opción: " + option.getText());
        }
        // Asegurar que hay más de una opción (la primera suele ser 'Seleccionar...')
        Assertions.assertTrue(options.size() > 1, "No se cargaron tipos de producto desde el backend.");
    }
}
