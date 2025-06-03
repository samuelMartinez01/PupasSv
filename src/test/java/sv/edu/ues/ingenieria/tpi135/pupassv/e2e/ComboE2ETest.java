package sv.edu.ues.ingenieria.tpi135.pupassv.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class ComboE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://localhost:3000/index.html");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testVisualizarDetallesCombo() {
        // Paso 1: Crear orden
        WebElement btnCrearOrden = wait.until(ExpectedConditions.elementToBeClickable(By.id("crear-orden")));
        btnCrearOrden.click();

        // Paso 2: Seleccionar categoría "Combos"
        WebElement selectCategoria = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-categorias")));
        Select categorias = new Select(selectCategoria);
        boolean comboSeleccionado = false;

        for (WebElement option : categorias.getOptions()) {
            if (option.getText().toLowerCase().contains("combos")) {
                categorias.selectByVisibleText(option.getText());
                comboSeleccionado = true;
                break;
            }
        }

        assertTrue(comboSeleccionado, "No se encontró la categoría 'Combos'.");

        // Paso 3: Seleccionar un combo (esperar que cargue primero)
        WebElement selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        Select productos = new Select(selectProducto);

        wait.until(driver -> productos.getOptions().size() > 1);
        assertTrue(productos.getOptions().size() > 1, "No se cargaron combos.");

        productos.selectByIndex(1);

        // Paso 4: Verificar que se renderiza la información del combo
        WebElement detalle = driver.findElement(By.id("producto-detalle"));
        assertTrue(detalle.getText().toLowerCase().contains("contenido"), "No se muestran los detalles del combo.");
    }
}
