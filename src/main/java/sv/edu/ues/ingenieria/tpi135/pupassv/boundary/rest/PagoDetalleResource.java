package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.PagoBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.PagoDetalleBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Pago;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.PagoDetalle;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("pago/{idPago}/detalle")
public class PagoDetalleResource {

    @Inject
    PagoDetalleBean pdBean;
    @Inject
    PagoBean pgBean;
    @Inject
    UserTransaction utx;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetalles(@PathParam("idPago") Long idPago) {
        if (idPago != null) {
            if (pgBean.findById(idPago) != null) {
                try {
                    List<PagoDetalle> detalles = pdBean.findDetallesByPagoId(idPago);
                    return Response.ok(detalles).build();
                } catch (Exception e) {
                    return Response.serverError().entity(e.getMessage()).build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Agrega un detalle a un pago existente
     * @param idPago
     * @param detalle
     * @param uriInfo
     * @return
     */
    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDetalle(
            @PathParam("idPago") Long idPago,
            PagoDetalle detalle,
            @Context UriInfo uriInfo) {
        if (idPago != null && detalle != null ) {
            Pago pago = pgBean.findById(idPago);
            if (pago != null) {
                try {
                    utx.begin();
                    detalle.setIdPago(pago);
                    pdBean.create(detalle);
                    utx.commit();
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder()
                            .path(detalle.getIdPagoDetalle().toString());
                    return Response.created(uriBuilder.build()).build();
                } catch (Exception e) {
                    try {
                        if (utx.getStatus() == jakarta.transaction.Status.STATUS_ACTIVE) {
                            utx.rollback();
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(PagoResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return Response.serverError().entity(e.getMessage()).build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Elimina un detalle de pago específico
     * @param idPago ID del pago
     * @param idDetalle ID del detalle a eliminar
     * @return Respuesta HTTP
     */
    @DELETE
    @Path("/{idDetalle}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteDetalle(
            @PathParam("idPago") Long idPago,
            @PathParam("idDetalle") Long idDetalle) {

        if (idPago != null && idDetalle != null) {
            try {
                Pago pago = pgBean.findById(idPago);
                PagoDetalle detalle = pdBean.findById(idDetalle);
                if (detalle != null && pago != null) {
                    utx.begin();
                    // Elimina el detalle
                    pdBean.delete(idDetalle);
                    utx.commit();
                    return Response.noContent().build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .build();
                }
            } catch (Exception e) {
                try {
                    if (utx.getStatus() == jakarta.transaction.Status.STATUS_ACTIVE) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PagoResource.class.getName()).log(Level.SEVERE, null, ex);
                }
                return Response.serverError()
                        .entity("Error al eliminar detalle: " + e.getMessage())
                        .build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Actualiza un detalle de pago existente
     * @param idPago ID del pago al que pertenece el detalle
     * @param idDetalle ID del detalle a actualizar
     * @param detalle Datos actualizados del detalle
     * @return Respuesta HTTP
     */
    @PUT
    @Path("/{idDetalle}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateDetalle(
            @PathParam("idPago") Long idPago,
            @PathParam("idDetalle") Long idDetalle,
            PagoDetalle detalle) {
        if (idPago != null && idDetalle != null && detalle != null ) {
            try {
                Pago pago = pgBean.findById(idPago);
                if (pago != null) {
                    // Verifica que el detalle existe y pertenece al pago
                    PagoDetalle existingDetalle = pdBean.findById(idDetalle);
                    if (existingDetalle == null || !existingDetalle.getIdPago().getIdPago().equals(idPago)) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                    utx.begin();
                    // Actualiza solo los campos permitidos
                    existingDetalle.setMonto(detalle.getMonto());
                    existingDetalle.setObservaciones(detalle.getObservaciones());
                    pdBean.update(existingDetalle);
                    utx.commit();
                    return Response.ok(existingDetalle).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } catch (Exception e) {
                try {
                    if (utx.getStatus() == jakarta.transaction.Status.STATUS_ACTIVE) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PagoDetalleResource.class.getName()).log(Level.SEVERE, null, ex);
                }
                return Response.serverError()
                        .entity("Error al actualizar detalle: " + e.getMessage())
                        .build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos inválidos: ID de pago, detalle o datos inconsistentes")
                    .build();
        }
    }
}