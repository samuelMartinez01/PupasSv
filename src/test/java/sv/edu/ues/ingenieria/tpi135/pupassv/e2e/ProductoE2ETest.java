package sv.edu.ues.ingenieria.tpi135.pupassv.e2e;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

public class ProductoE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setup() {
        System.setProperty("webdriver.chrome.driver", "/usr/bin/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get("http://localhost:3000/index.html");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testVisualizarDetallesProducto() {
        // Paso 1: Dar click en "Crear Orden"
        WebElement btnCrearOrden = wait.until(ExpectedConditions.elementToBeClickable(By.id("crear-orden")));
        btnCrearOrden.click();

        // Paso 2: Seleccionar una categoría válida
        Select categorias = new Select(driver.findElement(By.id("select-categorias")));
        boolean categoriaSeleccionada = false;
        for (WebElement option : categorias.getOptions()) {
            String texto = option.getText().toLowerCase();
            if (!texto.contains("seleccionar") && !texto.contains("combos")) {
                categorias.selectByVisibleText(option.getText());
                categoriaSeleccionada = true;
                break;
            }
        }

        assertTrue(categoriaSeleccionada, "No se pudo seleccionar una categoría válida.");

        // Paso 3: Esperar y seleccionar un producto
        Select productos = new Select(wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos"))));
        wait.until(driver -> productos.getOptions().size() > 1);
        productos.selectByIndex(1); // Seleccionamos el primer producto real

        // Paso 4: Verificar que se renderiza el detalle del producto
        WebElement detalle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("producto-detalle")));

        // Esperamos a que se muestre contenido significativo (ID, Precio)
        String textoDetalle = detalle.getText().toLowerCase();
        assertAll("Validar contenido del detalle",
                () -> assertTrue(textoDetalle.contains("id"), "No se muestra el ID del producto."),
                () -> assertTrue(textoDetalle.contains("precio"), "No se muestra el precio del producto."),
                () -> assertTrue(textoDetalle.length() > 20, "El detalle es muy corto o no se ha renderizado completamente.")
        );
    }

}
