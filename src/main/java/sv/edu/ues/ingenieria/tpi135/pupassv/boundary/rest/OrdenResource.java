package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.OrdenDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.OrdenDetalleDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.OrdenBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.OrdenDetalleBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.PagoBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ProductoPrecioBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.*;

/**
 * Recurso REST para gestionar entidades de tipo Orden.
 * Proporciona operaciones para consultar, crear y manejar órdenes.
 *
 * @author samuel
 */
@Path("orden")
public class OrdenResource implements Serializable {

    @Inject
    OrdenBean oBean;
    @Inject
    PagoBean pgBean;
    @Inject
    OrdenDetalleBean odBean;
    @Inject
    ProductoPrecioBean ppBean;
    @Resource
    UserTransaction utx; //Manejador de transacciones

    /**
     * Obtiene un rango de órdenes basado en parámetros de paginación.
     *
     * @param firstResult Índice del primer resultado (por defecto 0).
     * @param maxResult Cantidad máxima de resultados a devolver (máximo 50, por defecto 50).
     * @return Respuesta HTTP con la lista de órdenes y la cantidad total de registros.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int firstResult,
            @QueryParam("max") @DefaultValue("50") @Max(50) int maxResult) {
        try {
            if (firstResult >= 0 && maxResult > 0 && maxResult <= 50) {
                List<Orden> encontrados = oBean.findRange(firstResult, maxResult);
                // Convertir las entidades a DTOs
                List<OrdenDTO> dtos = encontrados.stream()
                        .map(oBean::convertirAOrdenDTO)
                        .collect(Collectors.toList());

                Long total = oBean.count();
                return Response.ok(dtos)
                        .header("Total-Records", total)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                return Response.status(422)
                        .header("Wrong-Parameter", "first:" + firstResult + " max:" + maxResult)
                        .build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Busca una orden por su id
     * @param id Identificador de la orden.
     * @return Respuesta HTTP con la orden encontrada o un código de error si no existe.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long id) {
        if (id != null && id > 0) {
            try {
                Orden orden = oBean.findById(id);
                if (orden != null) {
                    OrdenDTO dto = oBean.convertirAOrdenDTO(orden);
                    List<Pago> pago = pgBean.findPagosByOrdenId(orden.getIdOrden());
                    dto.setPagos(pago);
                    return Response.ok(dto).type(MediaType.APPLICATION_JSON).build();
                }
                return Response.status(404).header("Not-found", "id:" + id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(422).header("Wrong-Parameter", "id:" + id).build();
    }

    /**
     * Obtiene un pago específico de una orden
     * @param idOrden ID de la orden
     * @return Respuesta con el pago solicitado
     */
    @GET
    @Path("/{idOrden}/pago")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPagoDeOrden(
            @PathParam("idOrden") Long idOrden) {
        try {
            Orden orden = oBean.findById(idOrden);
            if (orden == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
           List<Pago> pagos = pgBean.findPagosByOrdenId(idOrden);
            if (pagos == null ) {
                return Response.status(Response.Status.NOT_FOUND)  .build();
            }
            return Response.ok(pagos).build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.serverError()
                    .entity("Error al obtener el pago: " + e.getMessage())
                    .build();
        }
    }


    /**
     * Crea una nueva orden en el sistema.
     *
     * @param orden Objeto Orden a crear.
     * @param uriInfo Información sobre la URI de la petición.
     * @return Respuesta HTTP con la URI de la orden creada o un código de error.
     */

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Orden orden, @Context UriInfo uriInfo) {
        if (orden == null || orden.getSucursal() == null || orden.getSucursal().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos de orden inválidos: sucursal es requerida")
                    .build();
        }
        try {
            utx.begin();
            if (orden.getAnulada() == null) {
                orden.setAnulada(false);
            }
            if (orden.getFecha() == null) {
                orden.setFecha(new Date());
            }
            oBean.create(orden);
            utx.commit();
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder()
                    .path(orden.getIdOrden().toString());
            return Response.created(uriBuilder.build()).build();
        } catch (Exception e) {
            try {
                if (utx.getStatus() == Status.STATUS_ACTIVE) {
                    utx.rollback();
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en rollback", ex);
            }
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al crear orden", e);
            return Response.serverError()
                    .entity("Error al crear la orden: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Metodo para actualizar una orden
     * @param id
     * @param ordenDTO
     * @return
     */
    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, OrdenDTO ordenDTO) {
        if (id != null && id > 0 && ordenDTO != null) {
            try {
                Orden ordenExistente = oBean.findById(id);
                if (ordenExistente != null) {
                    ordenExistente.setFecha(ordenDTO.getFecha());
                    ordenExistente.setSucursal(ordenDTO.getSucursal());
                    ordenExistente.setAnulada(ordenDTO.getAnulada());
                    oBean.update(ordenExistente);
                    return Response.ok(oBean.convertirAOrdenDTO(ordenExistente)).build();
                }
                return Response.status(404).header("Not-found", "id:" + id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(422).header("Wrong-Parameter", "id:" + id + " OrdenDTO:" + ordenDTO).build();
    }

    /**
     * Metodo para eliminar una orden
     * @param id
     * @return
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Long id) {
        if (id != null && id > 0) {
            try {
                Orden ordenExistente = oBean.findById(id);
                if (ordenExistente != null) {
                    pgBean.eliminarRelacionOrdenPago(id);
                    odBean.eliminarRelacionOrdenDetalle(id);
                    oBean.delete(id);
                    return Response.noContent().build();
                }
                return Response.status(404).header("Not-found", "id:" + id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(422).header("Wrong-Parameter", "id:" + id).build();
    }


    /**
     * Metodo para agregar productos a una orden
     * @param ordenDTO
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addProducto(OrdenDTO ordenDTO) {
        if (ordenDTO == null || ordenDTO.getProductos() == null || ordenDTO.getProductos().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Debe proporcionar productos.").build();
        }

        try {
            utx.begin();

            Orden nuevaOrden = new Orden();
            nuevaOrden.setSucursal(ordenDTO.getSucursal());
            nuevaOrden.setFecha(new Date());
            nuevaOrden.setAnulada(false);
            oBean.create(nuevaOrden);

            for (OrdenDetalleDTO p : ordenDTO.getProductos()) {
                ProductoPrecio precio = ppBean.findProductoPrecioByProducto(p.getIdProducto());

                if (precio == null) {
                    throw new RuntimeException("No hay precio activo para el producto ID: " + p.getIdProducto());
                }

                OrdenDetallePK pk = new OrdenDetallePK(nuevaOrden.getIdOrden(), precio.getIdProductoPrecio());
                OrdenDetalle detalle = new OrdenDetalle(pk);
                detalle.setOrden(nuevaOrden);
                detalle.setProductoPrecio(precio);
                detalle.setCantidad(p.getCantidad());
                detalle.setPrecio(p.getPrecioUnitario() != null ? p.getPrecioUnitario() : precio.getPrecioSugerido());
                detalle.setObservaciones(p.getObservaciones());
                odBean.create(detalle);
            }

            utx.commit();
            return Response.ok().entity(nuevaOrden.getIdOrden()).build();

        } catch (Exception e) {
            try {
                if (utx.getStatus() == Status.STATUS_ACTIVE) {
                    utx.rollback();
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en rollback", ex);
            }
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al crear orden", e);
            return Response.serverError().entity("Error interno al procesar la orden.").build();
        }
    }













    /**
     * Metodo para eliminar un producto de una orden
     * @param idOrden
     * @param idProducto
     * @return
     */
    @DELETE
    @Path("/{idOrden}/productos/{idProducto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProducto(
            @PathParam("idOrden") Long idOrden,
            @PathParam("idProducto") Long idProducto) {

        if (idOrden != null && idOrden > 0 && idProducto != null && idProducto > 0) {
            try {
                utx.begin();
                Orden orden = oBean.findById(idOrden);
                if (orden != null) {
                    if (!Boolean.TRUE.equals(orden.getAnulada())) {
                        ProductoPrecio productoPrecio = ppBean.findProductoPrecioByProducto(idProducto);
                        if (productoPrecio != null) {
                            OrdenDetallePK pk = new OrdenDetallePK(idOrden, productoPrecio.getIdProductoPrecio());
                            odBean.eliminarProductoDeOrden(pk);
                            utx.commit();
                            return Response.noContent().build();
                        } else {
                            return Response.status(Response.Status.NOT_FOUND) .build();
                        }
                    } else {
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } catch (Exception e) {
                try {
                    if (utx.getStatus() == Status.STATUS_ACTIVE) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en rollback", ex);
                }
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al eliminar producto de orden", e);
                return Response.serverError().entity("Error al eliminar producto de la orden: " + e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

}
