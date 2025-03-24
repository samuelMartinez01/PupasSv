package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.inject.Inject;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.OrdenBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Orden;

/**
 * Recurso REST para gestionar entidades de tipo Orden.
 * Proporciona operaciones para consultar, crear y manejar órdenes.
 *
 * @author samuel
 */
@Path("orden")
public class OrdenResource implements Serializable {

    @Inject
    private OrdenBean oBean;

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
                Long total = oBean.count();
                return Response.ok(encontrados)
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
     * Busca una orden por su identificador único.
     *
     * @param id Identificador de la orden.
     * @return Respuesta HTTP con la orden encontrada o un código de error si no existe.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") Long id) {
        if (id != null && id > 0) {
            try {
                Orden encontrado = oBean.findById(id);
                if (encontrado != null) {
                    return Response.ok(encontrado).type(MediaType.APPLICATION_JSON).build();
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
     * Crea una nueva orden en el sistema.
     *
     * @param Orden Objeto Orden a crear.
     * @param uriInfo Información sobre la URI de la petición.
     * @return Respuesta HTTP con la URI de la orden creada o un código de error.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Orden Orden, @Context UriInfo uriInfo) {
        if (Orden != null && Orden.getIdOrden() == null) {
            try {
                oBean.create(Orden);
                if (Orden.getIdOrden() != null) {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                    uriBuilder.path(String.valueOf(Orden.getIdOrden()));
                    return Response.created(uriBuilder.build()).build();
                }
                return Response.status(500).header("Process-Error", "Record couldn't be created").build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(422).header("Wrong-Parameter", "Orden:" + Orden).build();
    }

//    /**
//     * Método de actualización de órdenes. (Aún no implementado)
//     *
//     * @param id de la Orden a actualizar.
//     * @return Respuesta HTTP.
//     */
//    @PUT
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response update(@PathParam("id") Long id, Orden tipoOrden) {
//        if (id != null && id > 0) {
//            try {
//                // Verificar si la orden con el ID existe
//                Orden ordenExistente = oBean.findById(id);
//                if (ordenExistente != null) {
//                    // Actualizar los valores de la orden con los nuevos datos
//                    ordenExistente.setFecha(tipoOrden.getFecha());
//                    ordenExistente.setSucursal(tipoOrden.getSucursal());
//                    // Aquí agregar los demás campos que deseas actualizar
//                    oBean.update(ordenExistente);
//                    return Response.ok(ordenExistente).build();
//                }
//                return Response.status(404).header("Not-found", "id:" + id).build();
//            } catch (Exception e) {
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
//                return Response.status(500).entity(e.getMessage()).build();
//            }
//        }
//        return Response.status(422).header("Wrong-Parameter", "id:" + id).build();
//    }
//
//
//    @DELETE
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response delete(@PathParam("id") Long id) {
//        if (id != null && id > 0) {
//            try {
//                // Verificar si la orden con el ID existe
//                Orden ordenExistente = oBean.findById(id);
//                if (ordenExistente != null) {
//                    oBean.delete(ordenExistente);
//                    return Response.noContent().build(); // 204 No Content indica que se eliminó correctamente
//                }
//                return Response.status(404).header("Not-found", "id:" + id).build();
//            } catch (Exception e) {
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
//                return Response.status(500).entity(e.getMessage()).build();
//            }
//        }
//        return Response.status(422).header("Wrong-Parameter", "id:" + id).build();
//    }
}
