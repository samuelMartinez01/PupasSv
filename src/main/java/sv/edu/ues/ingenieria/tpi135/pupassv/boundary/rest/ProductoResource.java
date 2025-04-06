package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.*;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Recurso REST que maneja las gestion de productos dentro de un tipo de producto especifico
 */

@Path("tipoproducto/{idTipoProducto}/producto")
public class ProductoResource implements Serializable {

    @Inject
    ProductoBean pBean;
    @Inject
    ProductoDetalleBean pdBean;
    @Inject
    ProductoPrecioBean ppBean;
    @Inject
    TipoProductoBean tpBean;
    @Inject
    ComboDetalleBean cdBean;
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
                List<Producto> productos = pBean.findRange(first, max);
                List<ProductoDTO> listaProductos = productos.stream()
                        .map( p -> {
                            ProductoDTO dto = new ProductoDTO(p);
                            return dto;
                        }).collect(Collectors.toList());
                long total = pBean.count();
                return Response.ok(listaProductos)
                        .header(Headers.TOTAL_RECORD, total)
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
     * @param idTipo String y luego se convierte a Integer
     * @return lista de productos pertenecientes a un solo tipo
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findPorTipo(
            @PathParam("idTipoProducto") String idTipo,
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max) {


        try {

            if (first >= 0 && max >= 0 && max <= 50) {
                if (idTipo.equals("all")) {
                    return findAll(first, max);
                }
               Integer idTipoProducto = Integer.valueOf(idTipo);
                TipoProducto tipoProducto = tpBean.findById(idTipoProducto);
                if (tipoProducto == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                           // .entity("Tipo producto no encontrado")
                            .build();
                }


                List<Producto> productos = pBean.findByIdTipoProducto(idTipoProducto, first, max);
                if (productos.isEmpty()) {
                    return Response.status(Response.Status.NOT_FOUND)
                           // .entity("No se encontraron productosdddd")
                            .build();
                }
                List<ProductoDTO> response = productos.stream()
                        .map(p -> {
                            ProductoDTO dto = new ProductoDTO(p);
                            return dto;
                        })
                        .collect(Collectors.toList());
                long total = pBean.count();
                return Response.ok(response)
                        .header(Headers.TOTAL_RECORD, total).build();
            } else {
                return Response.status(400).header(Headers.WRONG_PARAMETER, "first" + first + "max" + max).build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Busca el producto por su identificador unico
     * @param idProducto
     * @return el productoDTO encontrado
     */
    @Path("/{idProducto}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("idProducto") Long idProducto) {
        if (idProducto != null) {
            try {
                Producto producto = pBean.findById(idProducto);
                if (producto != null) {
                    ProductoDTO dto = new ProductoDTO(producto);
                    return Response.ok(dto).build();
                }
                return Response.status(404).header(Headers.NOT_FOUND_ID, String.valueOf(idProducto)).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(400).header(Headers.WRONG_PARAMETER, "id:" + idProducto).build();
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            Producto producto,
            @PathParam("idTipoProducto") Integer idTipoProducto,
            @Context UriInfo uriInfo) {
        if (producto == null || producto.getIdProducto() != null || idTipoProducto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Datos de producto inválidos")
                    .build();
        }
        try {
            utx.begin();
            producto.setActivo(true); // Valor por defecto
            pBean.create(producto);
            pBean.getEntityManager().flush();
            // crea una relación con tipo de producto
            ProductoDetalle pDetalle = new ProductoDetalle(idTipoProducto, producto.getIdProducto());
            pDetalle.setActivo(true);
            pdBean.create(pDetalle);
            utx.commit();
            UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder()
                    .path(producto.getIdProducto().toString());
            return Response.created(uriBuilder.build()).build();

        } catch (Exception e) {
            try {
                if (utx.getStatus() == Status.STATUS_ACTIVE) {
                    utx.rollback();
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en rollback", ex);
            }
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al crear producto", e);
            return Response.serverError().entity("Error al crear producto").build();
        }
    }

    /**
     *  Atualiza un productoDTO existente
     * @param productoDTO
     * @param uriInfo
     * @return
     */


    @PUT
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(
            ProductoDTO productoDTO,
            @Context UriInfo uriInfo) {

        if (productoDTO == null || productoDTO.getIdProducto() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .header(Headers.WRONG_PARAMETER, "El DTO o ID del producto son nulos")
                    .entity("Debe proporcionar un producto válido con ID")
                    .build();
        }

        try {
            Producto productoExistente = pBean.findById(productoDTO.getIdProducto());
            if (productoExistente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .header(Headers.NOT_FOUND_ID, productoDTO.getIdProducto().toString())
                        .entity("Producto no encontrado")
                        .build();
            }
            // setters condicionales para evitar sobrescribir con nulls
            if (productoDTO.getNombre() != null) {
                productoExistente.setNombre(productoDTO.getNombre());
            }
            if (productoDTO.getObservaciones() != null) {
                productoExistente.setObservaciones(productoDTO.getObservaciones());
            }
            if (productoDTO.getActivo() != null) {
                productoExistente.setActivo(productoDTO.getActivo());
            }
            pBean.update(productoExistente);
            ProductoDTO dtoActualizado = new ProductoDTO(productoExistente);
            return Response.ok(dtoActualizado)
                    .header(Headers.LOCATION,
                            uriInfo.getAbsolutePathBuilder()
                                    .path(productoExistente.getIdProducto().toString())
                                    .build()
                                    .toString())
                    .build();

        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al actualizar producto", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .header(Headers.PROCESS_ERROR, e.getMessage())
                    .entity("Error interno al actualizar el producto")
                    .build();
        }
    }

    @DELETE
    @Path("/{idProducto}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(
            @PathParam("idProducto") Long idProducto,
            @QueryParam("idTipoProducto") Integer idTipoProducto) {
        try {
            if (idProducto == null || idProducto <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("ID de producto inválido")
                        .build();
            }
            if (idTipoProducto == null || idTipoProducto <= 0) {
                pBean.delete(idProducto);
            } else {
                pBean.deleteRelacion(idProducto, idTipoProducto);
            }
            return Response.noContent().build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }
}
