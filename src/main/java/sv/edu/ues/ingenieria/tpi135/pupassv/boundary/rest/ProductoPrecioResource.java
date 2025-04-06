package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ProductoPrecioBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoPrecio;


@Path("producto/{idProducto}/precio")
public class ProductoPrecioResource {

    @Inject
    ProductoPrecioBean ppBean;
    @Path("")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrecioActual(@PathParam("idProducto") Integer idProducto) {
        ProductoPrecio precio = ppBean.findPrice(idProducto);
        if(precio != null) {
            return Response.ok(precio).build();
        }
        return Response.status(404).build();
    }
}