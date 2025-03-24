//package sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest;
//
//import jakarta.annotation.Resource;
//import jakarta.inject.Inject;
//import jakarta.transaction.UserTransaction;
//import jakarta.validation.constraints.Max;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.*;
//import sv.edu.ues.ingenieria.tpi135.pupassv.control.ProductoBean;
//import sv.edu.ues.ingenieria.tpi135.pupassv.entity.Producto;
//import sv.edu.ues.ingenieria.tpi135.pupassv.entity.ProductoDetalle;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//@Path("tipoproducto/{idTipoProducto}/producto")
//public class ProductoResource implements Serializable {
//
//   @Inject
//    ProductoBean pBean;
//    ProductoDetalle pdBean;
//    @Resource
//    UserTransaction utx;
//
//    @Produces({MediaType.APPLICATION_JSON})
//    public Response findRange(int first,int max) {
//        try {
//            if (first >= 0 && max >= 0 && max <= 50) {
//
//                List<Producto> lista = pBean.findRange(first, max);
//                long total = pBean.count();
//                Response.ResponseBuilder responseHttp = Response.ok(lista).
//                        header(Headers.TOTAL_RECORD, total).
//                        type(MediaType.APPLICATION_JSON);
//                return responseHttp.build();
//            } else {
//                return Response.status(400).header("wrong parameter, first:", first + ",max: " + max).header("wrong parameter : max", "s").build();
//            }
//        } catch (Exception e) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, e.getMessage());
//            return Response.status(500).entity(e.getMessage()).build();
//        }
//    }
//
//
//
//}
