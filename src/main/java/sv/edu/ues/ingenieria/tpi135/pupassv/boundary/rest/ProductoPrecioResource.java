package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.ingenieria.tpi135.pupassv.DTO.ProductoPrecioDTO;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ProductoPrecioBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ProductoBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoPrecio;
import java.util.Date;
@Path("producto")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoPrecioResource {

    @Inject
    ProductoPrecioBean ppBean;

    @Inject
    ProductoBean productoBean;

    @GET
    @Path("{idProducto}/precio")
    public Response findById(@PathParam("idProducto") Long idProducto) {
        ProductoPrecio precioActual = ppBean.findProductoPrecioByProducto(idProducto);
        if (precioActual == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ProductoPrecioDTO dto = ppBean.convertirADTO(precioActual);
        return Response.ok(dto).build();
    }

    @POST
    @Path("{idProducto}/precio")
    public Response create(
            @PathParam("idProducto") Long idProducto,
            ProductoPrecio nuevoPrecio) {
        try {
            if (nuevoPrecio == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            Producto producto = productoBean.findById(idProducto);
            if (producto == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            if (nuevoPrecio.getPrecioSugerido() == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            if (nuevoPrecio.getFechaDesde() == null) {
                nuevoPrecio.setFechaDesde(new Date());
            }
            nuevoPrecio.setIdProducto(producto);
            producto.getProductoPrecioList().add(nuevoPrecio);
            ppBean.create(nuevoPrecio);
            return Response.status(Response.Status.CREATED)
                    .entity(nuevoPrecio)
                    .build();

        } catch (Exception e) {
            return Response.serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("{idProducto}/precio/{idPrecio}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(
            @PathParam("idProducto") Long idProducto,
            @PathParam("idPrecio") Long idPrecio,
            ProductoPrecio precioActualizado) {
        try {
            if (precioActualizado != null && precioActualizado.getPrecioSugerido() != null) {
                Producto producto = productoBean.findById(idProducto);
                if (producto != null) {
                    ProductoPrecio precioExistente = ppBean.findById(idPrecio);
                    if (precioExistente != null && precioExistente.getIdProducto().getIdProducto().equals(idProducto)) {
                        precioExistente.setPrecioSugerido(precioActualizado.getPrecioSugerido());
                        ppBean.update(precioExistente);
                        ProductoPrecioDTO responseDTO = ppBean.convertirADTO(precioExistente); //Para mantener la relacion
                        return Response.ok(responseDTO).build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } else {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Response.Status.BAD_REQUEST) .build();
            }
        } catch (Exception e) {
            return Response.serverError()
                    .build();
        }
    }

    @DELETE
    @Path("{idProducto}/precio/{idPrecio}")
    public Response delete(
            @PathParam("idProducto") Long idProducto,
            @PathParam("idPrecio") Long idPrecio) {
        try {
            Producto producto = productoBean.findById(idProducto);
            if (producto != null) {
                ProductoPrecio precioExistente = ppBean.findById(idPrecio);
                if (precioExistente != null ) {
                    ppBean.delete(precioExistente.getIdProductoPrecio());
                    return Response.noContent().build();
                } else {
                    return Response.status(Response.Status.NOT_FOUND)  .build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.serverError()
                    .build();
        }
    }
}