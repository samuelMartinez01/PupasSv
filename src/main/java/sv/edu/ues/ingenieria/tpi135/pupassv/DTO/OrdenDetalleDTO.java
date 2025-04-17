package sv.edu.ues.ingenieria.tpi135.pupassv.DTO;

import java.math.BigDecimal;

public class OrdenDetalleDTO {
    private Long idProductoPrecio;
    private Long idProducto;
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private String observaciones;

    public OrdenDetalleDTO() {
    }

    public Long getIdProductoPrecio() {
        return idProductoPrecio;
    }

    public void setIdProductoPrecio(Long idProductoPrecio) {
        this.idProductoPrecio = idProductoPrecio;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}