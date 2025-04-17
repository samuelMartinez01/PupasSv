package sv.edu.ues.ingenieria.tpi135.pupassv.DTO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * PAra manejar la relacion con producto
 */
public class ProductoPrecioDTO {
    private Long idProductoPrecio;
    private Long idProducto;
    private String nombreProducto;
    private Date fechaDesde;
    private Date fechaHasta;
    private BigDecimal precioSugerido;

    public ProductoPrecioDTO() {
    }

    public ProductoPrecioDTO(Long idProductoPrecio, Long idProducto, String nombreProducto,
                             Date fechaDesde, Date fechaHasta, BigDecimal precioSugerido) {
        this.idProductoPrecio = idProductoPrecio;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.precioSugerido = precioSugerido;
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

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public BigDecimal getPrecioSugerido() {
        return precioSugerido;
    }

    public void setPrecioSugerido(BigDecimal precioSugerido) {
        this.precioSugerido = precioSugerido;
    }
}