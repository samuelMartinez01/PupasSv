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
        // === FLUJO DE PAGO ===

        WebElement btnCrearOrden = wait.until(ExpectedConditions.elementToBeClickable(By.id("crear-orden")));
        btnCrearOrden.click();

        WebElement carritoOrden = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("carrito-orden")));

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
        assertNotNull(primeraCategoria);

        WebElement selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        Select productos = new Select(selectProducto);
        Select finalProductos = productos;
        wait.until(driver -> finalProductos.getOptions().size() > 1);
        productos.selectByIndex(1);

        WebElement detalles = driver.findElement(By.id("producto-detalle"));
        assertTrue(detalles.getText().toLowerCase().contains("precio"));

        WebElement btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();

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
        assertNotNull(segundaCategoria);

        selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        productos = new Select(selectProducto);
        Select finalProductos1 = productos;
        wait.until(driver -> finalProductos1.getOptions().size() > 1);
        productos.selectByIndex(1);
        btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();

        WebElement btnPagar = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelector('#pagar-orden')", carritoOrden);
        wait.until(ExpectedConditions.elementToBeClickable(btnPagar)).click();

        WebElement divPagos = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagos")));
        assertTrue(divPagos.isDisplayed());

        WebElement radio = driver.findElement(By.cssSelector("input[name='metodo-pago'][value='Efectivo']"));
        radio.click();
        assertTrue(radio.isSelected());

        WebElement btnConfirmar = driver.findElement(By.id("btn-confirmar-pago"));
        btnConfirmar.click();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertTrue(alert.getText().toLowerCase().contains("pago exitoso"));
        alert.accept();

        // === NUEVA SECUENCIA DE INTERACCIONES ===

        // 1. Crear nueva orden
        btnCrearOrden = wait.until(ExpectedConditions.elementToBeClickable(By.id("crear-orden")));
        btnCrearOrden.click();

        // 2. Esperar <carrito-orden>
        carritoOrden = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("carrito-orden")));

        // 3. Seleccionar categoría válida
        selectCategorias = new Select(driver.findElement(By.id("select-categorias")));
        categorias = selectCategorias.getOptions();
        String cat1 = null;
        for (WebElement option : categorias) {
            if (!option.getText().toLowerCase().contains("seleccionar") && !option.getText().toLowerCase().contains("combo")) {
                selectCategorias.selectByVisibleText(option.getText());
                cat1 = option.getText();
                break;
            }
        }

        // 4. Seleccionar producto y esperar detalles
        selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        productos = new Select(selectProducto);
        Select finalProductos2 = productos;
        wait.until(driver -> finalProductos2.getOptions().size() > 1);
        productos.selectByIndex(1);

        detalles = driver.findElement(By.id("producto-detalle"));
        assertTrue(detalles.isDisplayed());

        // 4.5 Agregar al pedido
        btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();

        // 5. Limpiar selección
        WebElement btnLimpiar = driver.findElement(By.id("btn-limpiar-seleccion"));
        btnLimpiar.click();

        // 6. Seleccionar nueva categoría
        String cat2 = null;
        for (WebElement option : categorias) {
            if (!option.getText().equals(cat1) && !option.getText().toLowerCase().contains("seleccionar") &&
                    !option.getText().toLowerCase().contains("combo")) {
                selectCategorias.selectByVisibleText(option.getText());
                cat2 = option.getText();
                break;
            }
        }

        // 7. Seleccionar producto
        selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        productos = new Select(selectProducto);
        Select finalProductos3 = productos;
        wait.until(driver -> finalProductos3.getOptions().size() > 1);
        productos.selectByIndex(1);

        // 7.5 Agregar segundo producto
        btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();

        // 8. "+" dos veces al primer producto
        WebElement btnMas1 = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelectorAll('.btn-mas')[0]", carritoOrden);
        btnMas1.click(); btnMas1.click();

        // 9. "+" cuatro veces al segundo producto
        WebElement btnMas2 = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelectorAll('.btn-mas')[1]", carritoOrden);
        for (int i = 0; i < 4; i++) btnMas2.click();

        // 10. "-" dos veces al segundo producto
        WebElement btnMenos2 = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelectorAll('.btn-menos')[1]", carritoOrden);
        btnMenos2.click(); btnMenos2.click();

        // 11. Cancelar orden
        WebElement btnCancelar = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelector('#cancelar-orden')", carritoOrden);
        btnCancelar.click();

        // 12. Confirmar alerta de cancelación
        wait.until(ExpectedConditions.alertIsPresent());
        alert = driver.switchTo().alert();
        String alertaTexto = alert.getText().toLowerCase();
        System.out.println("Alerta al cancelar: " + alertaTexto);
        assertTrue(alertaTexto.contains("cancelar")); // usa una palabra clave más amplia
        alert.accept();


        // 13. Limpiar selección (aunque orden ya no exista)
        btnLimpiar = driver.findElement(By.id("btn-limpiar-seleccion"));
        btnLimpiar.click();
    }
}
