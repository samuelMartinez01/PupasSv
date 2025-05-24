package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.PagoBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.PagoDetalleBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Pago;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("pago")
public class PagoResource {

    @Inject
    PagoBean pgBean;

    @Inject
    PagoDetalleBean pdBean;

    @Inject
    UserTransaction utx;

    /**
     * Metodo para obtener todos los pagos realizados
     * @param firstResult
     * @param maxResult
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int firstResult,
            @QueryParam("max") @DefaultValue("50") int maxResult) {
        try {
            List<Pago> pagos = pgBean.findRange(firstResult, maxResult);
            Long total = pgBean.count();
            return Response.ok(pagos)
                    .header("Total-Records", total)
                    .build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    /**
     * Metodo para obtener un pago por ID
     * @param id
     * @return
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long id) {
        if (id != null) {
            Pago pago = pgBean.findById(id);
            if (pago != null) {
                return Response.ok(pago).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Metodo para crear un pago a una orden
     * @param pago
     * @param uriInfo
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Pago pago, @Context UriInfo uriInfo) {
        if (pago != null && pago.getIdOrden() != null) {
            try {
                utx.begin();
                if (pago.getFecha() == null) {
                    pago.setFecha(new java.util.Date());
                }
                pgBean.create(pago);
                utx.commit();

                UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder()
                        .path(pago.getIdPago().toString());
                return Response.created(uriBuilder.build()).entity(pago).build();
            } catch (Exception e) {
                try {
                    if (utx.getStatus() == jakarta.transaction.Status.STATUS_ACTIVE) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PagoResource.class.getName()).log(Level.SEVERE, null, ex);
                }
                return Response.serverError().build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * Metodo para actualizar un pago que existe
     * @param id
     * @param pago
     * @return
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") Long id, Pago pago) {
        if (id != null && pago != null && id.equals(pago.getIdPago())) {
            try {
                Pago existing = pgBean.findById(id);
                if (existing != null) {
                    pgBean.update(pago);
                    return Response.ok(pago).build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } catch (Exception e) {
                return Response.serverError().entity(e.getMessage()).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    /**
     * MEtodo para eliminar un pago de una orden
     * @param id
     * @return
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            Pago pago = pgBean.findById(id);
            if (pago != null) {
                pgBean.delete(id);
                return Response.noContent().build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}