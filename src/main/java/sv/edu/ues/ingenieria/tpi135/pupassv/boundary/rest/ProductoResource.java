package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ProductoBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ProductoDetalleBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetalle;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Recurso REST que maneja las gestion de productos dentro de un tipo de producto especifico
 */

@Path("tipoproducto/{idTipoProducto}/producto")
public class ProductoResource implements Serializable {

   @Inject
    ProductoBean pBean;
    ProductoDetalleBean pdBean;
    @Resource
    UserTransaction utx; //Manejador de transacciones

    /**
     * Obtiene una lista de todos los productos si se es llamado de findRange
     * sera llamado desde findRange si en lugar de un idTipoProducto es "any"
     * @param first
     * @param max
     * @return lista de productos
     */
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll(int first, int max) {
        try {
            if (first >= 0 && max > 0 && max<= 50) {
                List<Producto> encontrados = pBean.findRange(first, max);
                Long total = pBean.count();
                return Response.ok(encontrados)
                        .header(Headers.TOTAL_RECORD, total)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            } else {
                return Response.status(422)
                        .header(Headers.WRONG_PARAMETER, "first:" + first + " max:" + max)
                        .build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Obtiene una lista de productos filtrados por su id
     * segun el tipo de producto al que pertenecen
     * @param first
     * @param max
     * @param idTipoProducto
     * @return lista de productos pertenecientes a un solo tipo
     */

    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max,
            @PathParam("idTipoProducto") String idTipoProducto
    ) {
        try {
            if (first >= 0 && max >= 0 && max <= 50) {
                if (idTipoProducto.equals("any")) {
                    return findAll(first, max);
                }
                List<Producto> lista = pBean.findByIdTipoProducto(Integer.valueOf(idTipoProducto), first, max);
                long total = pBean.count();
                Response.ResponseBuilder responseHttp =
                        Response.ok(lista)
                                .header(Headers.TOTAL_RECORD, total)
                                .type(MediaType.APPLICATION_JSON);
                return responseHttp.build();
            }else {
                return Response.status(400).header(Headers.WRONG_PARAMETER, "first:" + first + " max:" + max).build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Busca el producto por su identificador unico
     * @param id
     * @return el producto encontrado
     */
    @Path("/{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        if (id != null) {
            try {
                Producto producto = pBean.findById(id);
                if (producto != null) {
                    Response.ResponseBuilder respuestaHttp = Response.ok(producto);
                    return respuestaHttp.build();
                }
                return Response.status(404).header(Headers.NOT_FOUND_ID, String.valueOf(id)).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER, "id:" + id).build();
    }

    /**
     * Crea un nuevo producto y lo asocia a un tipo de producto
     * @param producto Objeto a ser creado
     * @param idTipoProducto identiicador del tipo de producto asociado
     * @param uriInfo info de la solicitud http
     * @return respuesta con el estado de la operacion y la hubicacion del nuevo registro
     */
    @Path("")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response create(Producto producto, @PathParam("idTipoProducto") Integer idTipoProducto, @Context UriInfo uriInfo) {
        if (producto != null && producto.getIdProducto() == null) {
            try {
                utx.begin();
                pBean.create(producto);
                pBean.getEntityManager().flush();
                pBean.getEntityManager().refresh(producto);

                ProductoDetalle pDetalle = new ProductoDetalle(idTipoProducto, producto.getIdProducto());
                pDetalle.setActivo(true);
                pdBean.create(pDetalle);
                utx.commit();

                if (producto.getIdProducto() != null) {
                    UriBuilder uri = uriInfo.getAbsolutePathBuilder();
                    uri.path(String.valueOf(producto.getIdProducto()));
                    return Response.created(uri.build())
                            .header(Headers.LOCATION, uri.build().toString()).build();
                }
                return Response.status(422).header(Headers.UNPROCESSABLE_ENTITY, "producto").build();
            }catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                try {
                    utx.rollback();
                } catch (Exception rollbackException) {
                    rollbackException.printStackTrace();
                }
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER, producto).build();
    }

    /**
     * Elimina un porodcuto existente
     * @param id identificador del producto a elinminar
     * @return
     */

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response delete(@PathParam("id") Long id) {
        if (id != null) {
            try {
                pBean.delete(id);
                return Response.status(200).header(Headers.X_DELETED_ID, id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).header(Headers.PROCESS_ERROR, "no se pudo eliminar el producto").build();
            }
        }
        return Response.status(404).header(Headers.NOT_FOUND_ID, id).build();
    }

    /**
     *  Atualiza un producto existente
     * @param producto
     * @param uriInfo
     * @return
     */

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(Producto producto, @Context UriInfo uriInfo) {
        if (producto != null && producto.getIdProducto() != null) {
            try {
                pBean.update(producto);
                if (producto.getIdProducto() != null) {
                    return Response.status(200).header(Headers.LOCATION, uriInfo.getAbsolutePath().toString()).build();
                }
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER, "parametros incorrectos").build();
    }
}
