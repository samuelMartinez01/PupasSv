package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import sv.edu.ues.ingenieria.tpi135.pupassv.control.ComboBean;
import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Combo;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("combo")
public class ComboResource implements Serializable {
    @Inject
    ComboBean comBean;

    @GET
    @Path("")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findRange(
            @QueryParam("first") @DefaultValue("0") int first,
            @QueryParam("max") @DefaultValue("50") int max
    ) {
        try {
            if (first >= 0 && max <= 50) {
                List<Combo> lista = comBean.findRange(first, max);
                long total = comBean.count();
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

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response findById(@PathParam("id") Long id) {
        if (id != null) {
            try {
                Combo lista = comBean.findById(id);
                if (lista != null) {
                    return Response.ok(lista).build();
                }
                return Response.status(404).header(Headers.NOT_FOUND_ID, id).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", e.getMessage());
                return Response.status(500).entity(e.getMessage()).build();
            }
        }
        return Response.status(404).header(Headers.WRONG_PARAMETER, id).build();
    }

    @POST
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(Combo registro, @Context UriInfo uriInfo) {
        if (registro != null && registro.getIdCombo() == null) {
            try {
                comBean.create(registro);
                if (registro.getIdCombo() != null) {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder().path(String.valueOf(registro.getIdCombo()));
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

    @PUT
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response update(Combo registro) {
        if (registro != null && registro.getIdCombo() != null) {
            try {
                comBean.update(registro);
                return Response.status(200).build();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error", e.getMessage());
                return Response.status(500).header(Headers.PROCESS_ERROR, e.getMessage()).build();
            }
        }
        return Response.status(500).header(Headers.WRONG_PARAMETER, registro).build();
    }









}
