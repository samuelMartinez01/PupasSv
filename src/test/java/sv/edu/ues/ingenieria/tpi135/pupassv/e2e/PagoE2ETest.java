package sv.edu.ues.ingenieria.tpi135.pupassv.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Tag("e2e")
public class PagoE2ETest {

    private WebDriver driver;
    private WebDriverWait wait;
    private PrintWriter logWriter;

    @BeforeEach
    public void setup() throws IOException {
        File logFile = new File("target/logs/pago-e2e.log");
        logFile.getParentFile().mkdirs();
        logWriter = new PrintWriter(logFile);

        // Configurar WebDriverManager para manejar automáticamente el driver
        // Forzar descarga de la versión más reciente compatible
        WebDriverManager.chromedriver()
                .clearDriverCache() // Limpiar cache de drivers antiguos
                .forceDownload() // Forzar descarga nueva
                .setup();

        ChromeOptions options = new ChromeOptions();

        // Detectar si estamos en un entorno CI/CD o si se especifica headless
        boolean isHeadless = isRunningInCI() || isHeadlessRequested();

        if (isHeadless) {
            log("Ejecutando en modo headless");
            // Configuraciones para modo headless
            options.addArguments("--headless=new"); // Usar nuevo modo headless
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-web-security");
            options.addArguments("--allow-running-insecure-content");
            options.addArguments("--remote-allow-origins=*");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-background-timer-throttling");
            options.addArguments("--disable-backgrounding-occluded-windows");
            options.addArguments("--disable-renderer-backgrounding");
        } else {
            log("Ejecutando en modo con interfaz gráfica");
            options.addArguments("--force-device-scale-factor=0.8");
            options.addArguments("--window-size=1280,800");
        }

        // Configuraciones comunes
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });

        try {
            driver = new ChromeDriver(options);
        } catch (SessionNotCreatedException e) {
            log("Error al crear sesión ChromeDriver: " + e.getMessage());
            // Intentar una vez más con configuración alternativa
            WebDriverManager.chromedriver()
                    .browserVersion("137.0.7151.68") // Especificar versión exacta
                    .setup();
            driver = new ChromeDriver(options);
        }

        if (!isHeadless) {
            driver.manage().window().maximize();
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        driver.get("http://localhost:3000/index.html");

        log("Inicio de prueba E2E");
    }

    /**
     * Detecta si estamos ejecutando en un entorno CI/CD
     */
    private boolean isRunningInCI() {
        return System.getenv("CI") != null ||
                System.getenv("JENKINS_URL") != null ||
                System.getenv("GITHUB_ACTIONS") != null ||
                System.getenv("GITLAB_CI") != null ||
                System.getenv("BAMBOO_BUILD_NUMBER") != null;
    }

    /**
     * Detecta si se ha solicitado específicamente el modo headless
     */
    private boolean isHeadlessRequested() {
        return "true".equals(System.getProperty("headless")) ||
                "true".equals(System.getenv("HEADLESS")) ||
                "true".equals(System.getProperty("chrome.headless"));
    }

    @AfterEach
    public void tearDown() {
        log("Fin de prueba E2E");
        if (logWriter != null)
            logWriter.close();
        if (driver != null)
            driver.quit();
    }

    private void log(String msg) {
        String message = "[" + java.time.LocalDateTime.now() + "] " + msg;
        logWriter.println(message);
        logWriter.flush();
        // También imprimir en consola para debugging en CI
        System.out.println(message);
    }

    @Test
    public void testFlujoCompletoDePago() {
        log("Haciendo clic en Crear Orden");
        WebElement btnCrearOrden = wait.until(ExpectedConditions.elementToBeClickable(By.id("crear-orden")));
        btnCrearOrden.click();

        log("Esperando componente carrito-orden");
        WebElement carritoOrden = wait
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("carrito-orden")));

        log("Seleccionando primera categoría válida");
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
        log("Categoría seleccionada: " + primeraCategoria);
        assertNotNull(primeraCategoria);

        WebElement selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        Select productos = new Select(selectProducto);
        Select finalProductos = productos;
        wait.until(driver -> finalProductos.getOptions().size() > 1);
        productos.selectByIndex(1);

        log("Producto seleccionado y renderizado");
        WebElement detalles = driver.findElement(By.id("producto-detalle"));
        assertTrue(detalles.getText().toLowerCase().contains("precio"));

        WebElement btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();
        log("Producto agregado al pedido");

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
        log("Segunda categoría seleccionada: " + segundaCategoria);
        assertNotNull(segundaCategoria);

        selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        productos = new Select(selectProducto);
        Select finalProductos1 = productos;
        wait.until(driver -> finalProductos1.getOptions().size() > 1);
        productos.selectByIndex(1);
        btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();
        log("Segundo producto agregado");

        WebElement btnPagar = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelector('#pagar-orden')", carritoOrden);
        wait.until(ExpectedConditions.elementToBeClickable(btnPagar)).click();
        log("Clic en botón Pagar");

        WebElement divPagos = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pagos")));
        assertTrue(divPagos.isDisplayed());
        log("Sección de pagos visible");

        WebElement radio = driver.findElement(By.cssSelector("input[name='metodo-pago'][value='Efectivo']"));
        radio.click();
        log("Método de pago seleccionado: Efectivo");
        assertTrue(radio.isSelected());

        WebElement btnConfirmar = driver.findElement(By.id("btn-confirmar-pago"));
        btnConfirmar.click();
        log("Clic en Confirmar Pago");

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        log("Alerta recibida: " + alert.getText());
        assertTrue(alert.getText().toLowerCase().contains("pago exitoso"));
        alert.accept();

        // === NUEVA ORDEN ===

        log("Iniciando segunda orden");
        btnCrearOrden = wait.until(ExpectedConditions.elementToBeClickable(By.id("crear-orden")));
        btnCrearOrden.click();
        carritoOrden = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("carrito-orden")));

        selectCategorias = new Select(driver.findElement(By.id("select-categorias")));
        categorias = selectCategorias.getOptions();
        String cat1 = null;
        for (WebElement option : categorias) {
            if (!option.getText().toLowerCase().contains("seleccionar")
                    && !option.getText().toLowerCase().contains("combo")) {
                selectCategorias.selectByVisibleText(option.getText());
                cat1 = option.getText();
                break;
            }
        }
        log("Categoría seleccionada: " + cat1);

        selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        productos = new Select(selectProducto);
        Select finalProductos2 = productos;
        wait.until(driver -> finalProductos2.getOptions().size() > 1);
        productos.selectByIndex(1);

        detalles = driver.findElement(By.id("producto-detalle"));
        assertTrue(detalles.isDisplayed());

        btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();
        log("Producto agregado al nuevo pedido");

        WebElement btnLimpiar = driver.findElement(By.id("btn-limpiar-seleccion"));
        btnLimpiar.click();
        log("Selección limpiada");

        String cat2 = null;
        for (WebElement option : categorias) {
            if (!option.getText().equals(cat1) && !option.getText().toLowerCase().contains("seleccionar") &&
                    !option.getText().toLowerCase().contains("combo")) {
                selectCategorias.selectByVisibleText(option.getText());
                cat2 = option.getText();
                break;
            }
        }
        log("Segunda categoría: " + cat2);

        selectProducto = wait.until(ExpectedConditions.elementToBeClickable(By.id("select-productos")));
        productos = new Select(selectProducto);
        Select finalProductos3 = productos;
        wait.until(driver -> finalProductos3.getOptions().size() > 1);
        productos.selectByIndex(1);

        btnAgregar = driver.findElement(By.id("btn-agregar-producto"));
        btnAgregar.click();
        log("Segundo producto agregado");

        WebElement btnMas1 = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelectorAll('.btn-mas')[0]", carritoOrden);
        btnMas1.click();
        btnMas1.click();
        log("Aumentado cantidad primer producto (+2)");

        WebElement btnMas2 = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelectorAll('.btn-mas')[1]", carritoOrden);
        for (int i = 0; i < 4; i++)
            btnMas2.click();
        log("Aumentado cantidad segundo producto (+4)");

        WebElement btnMenos2 = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelectorAll('.btn-menos')[1]", carritoOrden);
        btnMenos2.click();
        btnMenos2.click();
        log("Reducida cantidad segundo producto (-2)");

        WebElement btnCancelar = (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelector('#cancelar-orden')", carritoOrden);
        btnCancelar.click();
        log("Orden cancelada");

        wait.until(ExpectedConditions.alertIsPresent());
        alert = driver.switchTo().alert();
        log("Alerta de cancelación: " + alert.getText());
        assertTrue(alert.getText().toLowerCase().contains("cancelar"));
        alert.accept();

        btnLimpiar = driver.findElement(By.id("btn-limpiar-seleccion"));
        btnLimpiar.click();
        log("Selección final limpiada");
    }
}