package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Combo;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class ComboBeanTest {

    @Mock
    private EntityManager em;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private ComboBean bean;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Inyección manual del EntityManager usando reflexión
        Field field = ComboBean.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(bean, em);
    }

    @Test
    void getEntityManager() {
        assertNotNull(bean.getEntityManager());
    }

    @Test
    void findProductosByComboId() {
        System.out.println("ComboBeanTest.findProductosByComboId");
        Long idCombo = 1L;

        Producto producto = new Producto();
        Integer cantidad = 2;
        BigDecimal precioSugerido = new BigDecimal("5.00");

        Object[] tuple = new Object[]{producto, cantidad, precioSugerido};
        List<Object[]> resultados = Arrays.asList(new Object[][]{tuple}); // ← CORREGIDO

        when(em.createNamedQuery("Combo.findProductosByComboId")).thenReturn(mockQuery);
        when(mockQuery.setParameter("idCombo", idCombo)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(resultados);

        List<ProductoComboDTO> productos = bean.findProductosByComboId(idCombo);

        assertNotNull(productos);
        assertEquals(1, productos.size());
        ProductoComboDTO dto = productos.get(0);
        assertEquals(cantidad, dto.getCantidad());
        assertEquals(precioSugerido, dto.getPrecioUnitario());
        assertEquals(precioSugerido.multiply(BigDecimal.valueOf(cantidad)), dto.getPrecioTotal());
    }

    @Test
    void convertirAComboDTO_conComboValido() {
        System.out.println("ComboBeanTest.convertirAComboDTO_conComboValido");
        Long idCombo = 1L;
        Combo combo = new Combo();
        combo.setIdCombo(idCombo);
        combo.setNombre("Combo Especial");
        combo.setActivo(Boolean.TRUE);
        combo.setDescripcionPublica("Descripción del combo");

        Producto producto = new Producto();
        Integer cantidad = 3;
        BigDecimal precioUnitario = new BigDecimal("10.00");

        Object[] tuple = new Object[]{producto, cantidad, precioUnitario};
        List<Object[]> resultadoQuery = Arrays.asList(new Object[][]{tuple}); // ← CORREGIDO

        when(em.createNamedQuery("Combo.findProductosByComboId")).thenReturn(mockQuery);
        when(mockQuery.setParameter("idCombo", idCombo)).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(resultadoQuery);

        ComboDTO dto = bean.convertirAComboDTO(combo);

        assertNotNull(dto);
        assertEquals(combo.getIdCombo(), dto.getIdCombo());
        assertEquals(combo.getNombre(), dto.getNombre());
        assertEquals(combo.getActivo(), dto.getActivo());
        assertEquals(combo.getDescripcionPublica(), dto.getDescripcionPublica());
        assertEquals(1, dto.getProductos().size());

        BigDecimal expectedTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        assertEquals(expectedTotal, dto.getPrecioTotal());
    }

    @Test
    void convertirAComboDTO_conComboNull() {
        System.out.println("ComboBeanTest.convertirAComboDTO_conComboNull");
        ComboDTO dto = bean.convertirAComboDTO(null);
        assertNull(dto);
    }
}
