package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoComboDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ComboBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ComboDetalleBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ProductoBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ComboDTO;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("combo")
public class ComboResource implements Serializable {
    @Inject
    ComboBean cBean;
    @Inject
    ComboDetalleBean cdBean;
    @Inject
    ProductoBean pBean;
    @Resource
    UserTransaction utx; //Manejador de transacciones


    /**
     * Obtiene los combos DTO
     * @param first
     * @param max
     * @return
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
                List<Combo> lista = cBean.findRange(first, max);
                // Convierte Combo (Entity) a ComboDTO para la vista
                List<ComboDTO> listaDTO = lista.stream()
                        .map(cBean::convertirAComboDTO)
                        .collect(Collectors.toList());
                long total = cBean.count();
                return Response.ok(listaDTO)
                        .header(Headers.TOTAL_RECORD, total)
                        .build();
            } else {
                return Response.status(400)
                        .header(Headers.WRONG_PARAMETER, first + "-" + max)
                        .build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", e.getMessage());
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    /**
     * Metodo para buscar un combo por su id
     * @param id
     * @return  comboDTO
     */
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        if (id != null) {
            try {
                Combo combo = cBean.findById(id);
                if (combo != null) {
                    ComboDTO comboDTO = cBean.convertirAComboDTO(combo);
                    return Response.ok(comboDTO).build();
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
     * Metodo para obtener todos los combos
     * @param combo
     * @param uriInfo
     * @return
     */
    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Combo combo, @Context UriInfo uriInfo) {
        if (combo == null || combo.getIdCombo() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
        try {
            if (cBean.findById(combo.getIdCombo()) != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Ya existe un combo con este ID")
                        .build();
            }
            if (combo.getActivo() == null) {
                combo.setActivo(true);
            }
            try {
                utx.begin();
                cBean.create(combo);
                utx.commit();
                return Response.created(uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(combo.getIdCombo()))
                        .build()).build();
            } catch (Exception e) {
                try {
                    if (utx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en rollback", ex);
                }
                throw e;
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al crear combo", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al crear combo: " + e.getMessage())
                    .build();
        }
    }


    @PUT
    @Path("/{idCombo}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("idCombo") Long idCombo,
                           Combo combo,
                           @Context UriInfo uriInfo) {
        if (idCombo != null && combo != null) {
            try {
                utx.begin();
                Combo comboEncontrado = cBean.findById(idCombo);
                if (comboEncontrado == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Combo no encontrado con ID: " + idCombo)
                            .build();
                }
                //Si el campo es null o no está en el combo, el campo no se actualizará en la base de datos.
                if (combo.getNombre() != null) {
                    comboEncontrado.setNombre(combo.getNombre());
                }
                if (combo.getDescripcionPublica() != null) {
                    comboEncontrado.setDescripcionPublica(combo.getDescripcionPublica());
                }
                if (combo.getActivo() != null) {
                    comboEncontrado.setActivo(combo.getActivo());
                }
                cBean.update(comboEncontrado);
                utx.commit();
                return Response.ok()
                        .location(uriInfo.getAbsolutePathBuilder().path(idCombo.toString()).build())
                        .build();

            } catch (Exception e) {
                try {
                    if (utx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en rollback", ex);
                }

                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al actualizar combo", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error al actualizar: " + e.getMessage())
                        .build();
            }

        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID y datos del combo son requeridos")
                    .build();
        }
    }

    @DELETE
    @Path("/{idCombo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("idCombo") Long idCombo) {
        if (idCombo != null && idCombo > 0) {
            try {
                //Procede a eliminar
                utx.begin();
                cdBean.deleteRelacionCombo(idCombo);
                Combo combo = cBean.findById(idCombo);
                if (combo != null){
                    cBean.delete(idCombo);
                    utx.commit();
                    return Response.noContent().build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("Combo no encontrado con ID: " + idCombo)
                            .build();
                }
            } catch (Exception e) {
                try {
                    if (utx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en rollback", ex);
                }

                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al eliminar combo", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error al eliminar combo: " + e.getMessage())
                        .build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .build();
        }
    }

    /**
     * Metodo para obtener los productos de un combo
     * @param idCombo
     * @return ProductoComboDTO
     */
    @GET
    @Path("/{idCombo}/productos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductosDelCombo(@PathParam("idCombo") Long idCombo) {
        try {
            Combo combo = cBean.findById(idCombo);
            if (combo != null) {
                List<ProductoComboDTO> productos = cBean.findProductosByComboId(idCombo);
                return Response.ok(productos).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Combo no encontrado con ID: " + idCombo)
                        .build();
            }
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al obtener productos del combo", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al obtener productos: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Metodo para asignar productos a un combo
     * @param idCombo
     * @param productosDTO
     * @return
     */
    @POST
    @Path("/{idCombo}/productos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response asignarProductosACombo(
            @PathParam("idCombo") Long idCombo,
            List<ProductoComboDTO> productosDTO) {
        if (idCombo != null && productosDTO != null) {
            try {
                utx.begin();
                Combo combo = cBean.findById(idCombo);
                if (combo != null) {
                    for (ProductoComboDTO dto : productosDTO) {
                        if (dto.getIdProducto() != null && dto.getCantidad() != null && dto.getCantidad() > 0) {
                            Producto producto = pBean.findById(dto.getIdProducto());
                            if (producto != null) {
                                //Se crea una clave compuesta (pk)
                                ComboDetallePK pk = new ComboDetallePK(idCombo, dto.getIdProducto());
                                ComboDetalle detalle = cdBean.findByPk(pk);
                                if (detalle == null) {
                                    //Se crea un detalle (una relacion)
                                    detalle = new ComboDetalle(pk);
                                    detalle.setCombo(combo);
                                    detalle.setProducto(producto);
                                    detalle.setCantidad(dto.getCantidad());
                                    detalle.setActivo(true);
                                    cdBean.create(detalle);
                                } else {
                                    detalle.setCantidad(dto.getCantidad());
                                    detalle.setActivo(true);
                                    cdBean.update(detalle);
                                }
                            } else {
                                utx.rollback();
                                return Response.status(Response.Status.NOT_FOUND).build();
                            }
                        } else {
                            utx.rollback();
                            return Response.status(Response.Status.BAD_REQUEST).build();
                        }
                    }
                    utx.commit();
                    return Response.ok().build();
                }else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } catch (Exception e) {
                try {
                    if (utx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error en rollback", ex);
                }
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al asignar productos", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }


    /**
     * MEtodo para eliminar un producto de un combo
     * @param idCombo
     * @param idProducto
     * @return
     */
    @DELETE
    @Path("/{idCombo}/productos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response eliminarProductosDeCombo(
            @PathParam("idCombo") Long idCombo,
            @QueryParam("idProducto") Long idProducto) {
        if (idCombo != null && idProducto != null) {
            try {
                utx.begin();
                Combo combo = cBean.findById(idCombo);
                if (combo != null) {
                        cdBean.deleteProductoCombo(idCombo, idProducto);
                    utx.commit();
                    return Response.ok().build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND)
                            .build();
                }
            } catch (Exception e) {
                try {
                    if (utx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        utx.rollback();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", ex);
                }
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al eliminar productos del combo", e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        }else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}