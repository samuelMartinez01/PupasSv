/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.ingenieria.tpi135.pupassv.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author samuel
 */
@Entity
@Table(name = "producto")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Producto.findAll", query = "SELECT p FROM Producto p"),
    @NamedQuery(name = "Producto.findByIdProducto", query = "SELECT p FROM Producto p WHERE p.idProducto = :idProducto"),
    @NamedQuery(name = "Producto.findByNombre", query = "SELECT p FROM Producto p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Producto.findByActivo", query = "SELECT p FROM Producto p WHERE p.activo = :activo"),
    @NamedQuery(name = "Producto.findByObservaciones", query = "SELECT p FROM Producto p WHERE p.observaciones = :observaciones"),
    @NamedQuery(name = "Producto.findByIdTipoProducto",
            query = "SELECT DISTINCT p FROM ProductoDetalle pd JOIN  pd.producto p WHERE pd.tipoProducto.idTipoProducto = :idTipoProducto\n" ),
    @NamedQuery(name = "Producto.countByIdTipoProducto",
            query = "SELECT DISTINCT count (p) FROM ProductoDetalle pd JOIN pd.producto p WHERE pd.tipoProducto.idTipoProducto = :idTipoProducto\n")})

public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_producto")
    private Long idProducto;
    @Size(max = 155)
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "activo")
    private Boolean activo;
    @Size(max = 2147483647)
    @Column(name = "observaciones")
    private String observaciones;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto")
    private List<ComboDetalle> comboDetalleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "producto")
    @JsonManagedReference
    private List<ProductoDetalle> productoDetalleList;
    @OneToMany(mappedBy = "idProducto")
    @JsonManagedReference
    private List<ProductoPrecio> productoPrecioList;

    public Producto() {
    }

    public Producto(Long idProducto) {
        this.idProducto = idProducto;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @XmlTransient
    @JsonIgnore
    public List<ComboDetalle> getComboDetalleList() {
        return comboDetalleList;
    }

    public void setComboDetalleList(List<ComboDetalle> comboDetalleList) {
        this.comboDetalleList = comboDetalleList;
    }

    @XmlTransient
    //@JsonIgnore
    public List<ProductoDetalle> getProductoDetalleList() {
        return productoDetalleList;
    }

    public void setProductoDetalleList(List<ProductoDetalle> productoDetalleList) {
        this.productoDetalleList = productoDetalleList;
    }

    @XmlTransient
    //@JsonIgnore
    public List<ProductoPrecio> getProductoPrecioList() {
        return productoPrecioList;
    }

    public void setProductoPrecioList(List<ProductoPrecio> productoPrecioList) {
        this.productoPrecioList = productoPrecioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProducto != null ? idProducto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Producto)) {
            return false;
        }
        Producto other = (Producto) object;
        if ((this.idProducto == null && other.idProducto != null) || (this.idProducto != null && !this.idProducto.equals(other.idProducto))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto[ idProducto=" + idProducto + " ]";
    }
    
}
