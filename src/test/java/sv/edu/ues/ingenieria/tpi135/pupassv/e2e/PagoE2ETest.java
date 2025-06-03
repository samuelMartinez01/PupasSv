package sv.edu.ues.ingenieria.tpi135.pupassv.e2e;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
public class PagoE2ETest {

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
    public void testFlujoCompletoDePago() {
        // 1. Crear Orden
        WebElement btnCrearOrden = wait.until(ExpectedConditions.elementToBeClickable(By.id("crear-orden")));
        btnCrearOrden.click();

        // 2. Seleccionar categoría válida
        Select selectCategorias = new Select(driver.findElement(By.id("select-categorias")));
        List<WebElement> categorias = selectCategorias.getOptions();
        String primeraCategoria = null;
        for (WebElement option : categorias) {
            String text = option.getText().toLowerCase();
            if (!text.contains("seleccionar") && !text.contains("combo")) {
                selectCategorias.selectByVisibleText(option.getText());
                primeraCategoria = option.getText();
                break;
            }
        }
        assertNotNull(primeraCategoria, "No se encontró una categoría válida");

        // 3. Seleccionar producto de esa categoría
        WebElement selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        Select productos = new Select(selectProducto);
        Select finalProductos = productos;
        wait.until(driver -> finalProductos.getOptions().size() > 1);
        productos.selectByIndex(1);

        // 4. Verificar render de detalles
        WebElement detalles = driver.findElement(By.id("producto-detalle"));
        assertTrue(detalles.getText().toLowerCase().contains("precio"));

        // 5. Agregar a la orden
        WebElement btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();

        // 6. Seleccionar nueva categoría
        String segundaCategoria = null;
        for (WebElement option : categorias) {
            if (!option.getText().equals(primeraCategoria) &&
                    !option.getText().toLowerCase().contains("seleccionar") &&
                    !option.getText().toLowerCase().contains("combo")) {
                selectCategorias.selectByVisibleText(option.getText());
                segundaCategoria = option.getText();
                break;
            }
        }
        assertNotNull(segundaCategoria, "No se encontró una segunda categoría válida");

        // 7. Seleccionar producto de esa nueva categoría
        selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        productos = new Select(selectProducto);
        Select finalProductos1 = productos;
        wait.until(driver -> finalProductos1.getOptions().size() > 1);
        productos.selectByIndex(1);

        // 8. Agregar a la orden
        btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();

        // 9. Click en "Ir a pagar"
        WebElement btnPagar = wait.until(ExpectedConditions.elementToBeClickable(By.id("pagar-orden")));
        btnPagar.click();

        // 10. Verificar sección de pagos
        WebElement divPagos = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagos")));
        assertTrue(divPagos.isDisplayed());

        // 11. Seleccionar un método de pago
        WebElement radio = driver.findElement(By.cssSelector("input[name='metodo-pago'][value='Efectivo']"));
        radio.click();
        assertTrue(radio.isSelected(), "Método de pago no fue seleccionado");

        // 12. Confirmar pago
        WebElement btnConfirmar = driver.findElement(By.id("btn-confirmar-pago"));
        btnConfirmar.click();

        // 13. Verificar alerta de confirmación
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().toLowerCase().contains("pago exitoso"));
        alert.accept();
    }
}
