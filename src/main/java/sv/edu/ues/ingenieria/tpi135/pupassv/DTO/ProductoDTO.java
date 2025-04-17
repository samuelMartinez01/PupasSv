package sv.edu.ues.ingenieria.tpi135.pupassv.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetalle;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoPrecio;
import java.math.BigDecimal;

/**
 * Clase DTO (Data Transfer Object) para representar un producto con información resumida (solo lo necesario)
 * Se usa para enviar datos de productos sin incluir todas sus relaciones y detalles.
 *
 * ¡ALEEEEEERTA!!!!!!!!!! Si algo es nulo no lo va a serializar asi que hay que manejar los posibles casos
 */
@JsonInclude(JsonInclude.Include.ALWAYS) // Asegura que se incluyan campos nulos
public class ProductoDTO {
    private Long idProducto;  //Estas variables se deben llamar exactamente igual que las de las Entitys
    private String tipo;
    private String nombre;
    private BigDecimal precioActual;
    private String observaciones;
    private Boolean activo;

    public ProductoDTO() {
    }
    /**
     * Constructor que convierte una entidad {@link Producto} en un {@code ProductoListDTO}.
     * @param producto La entidad {@link Producto} de la cual se extraerán los datos.
     */
    public ProductoDTO(Producto producto) {
        this.idProducto = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.observaciones = producto.getObservaciones();
        this.activo = producto.getActivo();
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long id) {
        this.idProducto = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPrecioActual() {
        return precioActual;
    }

    public void setPrecioActual(BigDecimal precioActual) {
        this.precioActual = precioActual;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}