package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.TipoProductoBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.TipoProducto;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author samuel
 */
@Path("tipoproducto")
public class TipoProductoResource implements Serializable {
    @Inject
    TipoProductoBean tpBean;

    /**
     * Obtiene una lista paginada de registros de TipoProducto.
     *
     * @param first Índice inicial de los registros a recuperar (por defecto 0).
     * @param max Número máximo de registros a devolver (máximo 50, por defecto 50).
     * @return Una respuesta HTTP con la lista de registros y el total de registros.
     */
    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max
    ) {
        try {
            if (first >= 0 && max <= 50) {
                List<TipoProducto> lista = tpBean.findRange(first, max);
                long total = tpBean.count();
                Response.ResponseBuilder responseHttp =
                        Response.ok(lista).
                                header(Headers.TOTAL_RECORD, total).
                                type(MediaType.APPLICATION_JSON);
                return responseHttp.build();
            } else {
                return Response.status(400).header(Headers.WRONG_PARAMETER, first + "-" + max).type(MediaType.APPLICATION_JSON).build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Obtiene un TipoProducto por su ID.
     *
     * @param id Identificador del TipoProducto.
     * @return Una respuesta HTTP con el registro encontrado o un código 404 si no existe.
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Integer id) {
        if (id != null) {
            try {
                TipoProducto tipoProducto = tpBean.findById(id);
                if (tipoProducto != null) {
                    return Response.ok(tipoProducto).build();
                }
                return Response.status(404).header(Headers.NOT_FOUND_ID, id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", e.getMessage());
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(404).header(Headers.WRONG_PARAMETER, id).build();
    }

    /**
     * Crea un nuevo registro de TipoProducto.
     *
     * @param registro Datos del TipoProducto a crear.
     * @param uriInfo Información sobre la URI de la solicitud.
     * @return Una respuesta HTTP con la ubicación del nuevo recurso o un código de error.
     */
    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(TipoProducto registro, @Context UriInfo uriInfo) {
        if (registro != null && registro.getIdTipoProducto() == null) {
            try {
                tpBean.create(registro);
                if (registro.getIdTipoProducto() != null) {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(String.valueOf(registro.getIdTipoProducto()));
                    return Response.created(uriBuilder.build()).build();
                }
                return Response.status(422).header(Headers.UNPROCESSABLE_ENTITY, registro).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", e.getMessage());
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
    }

    /**
     * Elimina un TipoProducto por su ID.
     *
     * @param id Identificador del TipoProducto a eliminar.
     * @return Una respuesta HTTP indicando el resultado de la operación.
     */
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        if (id != null) {
            try {
                tpBean.delete(id);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", e.getMessage());
                return Response.status(422).header(Headers.PROCESS_ERROR, e.getMessage()).build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, id).build();
    }

    /**
     * Actualiza un registro existente de TipoProducto.
     *
     * @param registro Datos actualizados del TipoProducto.
     * @return Una respuesta HTTP indicando el resultado de la operación.
     */
    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(TipoProducto registro) {
        if (registro != null && registro.getIdTipoProducto() != null) {
            try {
                tpBean.update(registro);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", e.getMessage());
                return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
    }
}
