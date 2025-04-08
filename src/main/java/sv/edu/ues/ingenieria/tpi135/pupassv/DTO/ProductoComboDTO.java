package sv.edu.ues.ingenieria.tpi135.pupassv.DTO;

import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;

import java.math.BigDecimal;

public class ProductoComboDTO {
    private Long idProducto;
    private String nombre;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal precioTotal;

    public ProductoComboDTO() {}

    public ProductoComboDTO(Producto producto, Integer cantidad) {
        this.idProducto = producto.getIdProducto();
        this.nombre = producto.getNombre();
        this.cantidad = cantidad != null ? cantidad : 1;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }
}
