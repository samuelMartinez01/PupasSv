package sv.edu.ues.ingenieria.tpi135.pupassv.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Pago;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class OrdenDTO {
    private Long idOrden;
    private Date fecha;
    private String sucursal;
    private Boolean anulada;
    private List<OrdenDetalleDTO> detalles;
    private BigDecimal total;
    private List<Pago> pagos;
    private List<OrdenDetalleDTO> productos;

    public OrdenDTO() {
    }

    public Long getIdOrden() {
        return idOrden;
    }

    public void setIdOrden(Long idOrden) {
        this.idOrden = idOrden;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public Boolean getAnulada() {
        return anulada;
    }

    public void setAnulada(Boolean anulada) {
        this.anulada = anulada;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrdenDetalleDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OrdenDetalleDTO> detalles) {
        this.detalles = detalles;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public List<OrdenDetalleDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<OrdenDetalleDTO> productos) {
        this.productos = productos;
    }
}